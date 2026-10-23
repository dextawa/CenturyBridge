package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

/**
 * Remaps an official (obfuscated) Minecraft jar into the intermediary
 * namespace, producing a compile-classpath stub jar (method bodies stripped)
 * so intermediary-era shim mixins can be compiled with plain javac -- no loom.
 */
public final class JarRemapper {

    /**
     * An intermediary member id in a refmap: method_, field_ and comp_ (a record component's accessor; the
     * class files' pass already maps all three, the refmap pass took only the first two, leaving every mod
     * mixin that names a record accessor -- injector selector, @At target, @Invoker -- pointing at a name
     * 26.x does not have). Preceded by "L<owner>;" it is looked up under that owner first, as the class
     * files' pass does, so an id the table holds ambiguously still maps where the refmap says whose it is.
     */
    private static final java.util.regex.Pattern MEMBER_TOKEN =
        java.util.regex.Pattern.compile("(?:L([\\w/$]+);)?((?:method|field|comp)_\\d+)");
    /**
     * An intermediary class name: net/minecraft/class_N with its nested classes, and an intermediary-named
     * nested class of a class whose own name is not intermediary (com/mojang/blaze3d/opengl/GlStateManager$class_1017
     * -> GlStateManager$BlendState; net/minecraft/util/profiling/jfr/event/...$class_N). The second form is
     * anchored at the two package roots that have such classes, so it never starts at a descriptor's "L"
     * and never takes a match from the first. A match the table does not hold maps to itself.
     */
    private static final java.util.regex.Pattern CLASS_TOKEN = java.util.regex.Pattern.compile(
        "net/minecraft/class_\\d+(?:\\$[\\w$]+)?|(?:com/mojang|net/minecraft)/(?:\\w+/)*\\w+\\$class_\\d+(?:\\$[\\w$]+)?");

    /**
     * Remaps a jar from the intermediary namespace into Mojang names using a
     * composed C/M/F table (compose_mojang.py). This is the conversion stage
     * for year-versioned targets (26.1+): those jars ship unobfuscated, so an
     * intermediary-named mod must be wholly renamed before any of its
     * references resolve. Intermediary member ids are globally unique, so
     * member maps are keyed by bare name; anything not matching
     * method_/field_ passes through untouched. Code is preserved (this runs
     * on real mod jars, not classpath stubs); non-class entries are copied
     * verbatim -- refmap JSONs still carry intermediary names and need their
     * own pass later.
     */
    /**
     * The composed inter->mojang table, and the one Remapper every consumer
     * of it must share: the whole-jar conversion pass, and the build gate that
     * predicts what a converted call site will look like (WiringCheck) -- a
     * gate that translated names its own way would pass wiring the converter
     * then emits differently.
     */
    public static final class Table {
        public final java.util.Map<String, String> classes = new java.util.HashMap<>();
        /** "interOwner.name" -> mojang member name */
        public final java.util.Map<String, String> byOwner = new java.util.HashMap<>();
        /** bare intermediary member name -> mojang name, ambiguous ids removed */
        public final java.util.Map<String, String> members = new java.util.HashMap<>();
        /** how many bare ids were ambiguous (owner-qualified lookup only) */
        public int ambiguous;

        public Remapper remapper() {
            return new Remapper() {
                @Override
                public String map(String internalName) {
                    String hit = classes.get(internalName);
                    if (hit != null) {
                        return hit;
                    }
                    // nested classes absent from the table (anonymous/synthetic)
                    // follow their outermost mapped enclosing class
                    int d = internalName.length();
                    while ((d = internalName.lastIndexOf('$', d - 1)) > 0) {
                        String outer = classes.get(internalName.substring(0, d));
                        if (outer != null) {
                            return outer + internalName.substring(d);
                        }
                    }
                    return internalName;
                }

                private String member(String owner, String name) {
                    // our runtime classes (Statics, facades, tombstones) keep their
                    // token-named members: the converter wired call sites to them
                    // by exactly that name, and nothing in the game maps them
                    if (owner.startsWith("top/dext/centurybridge/") || owner.startsWith("centurybridge/")) {
                        return name;
                    }
                    String hit = byOwner.get(owner + "." + name);
                    return hit != null ? hit : members.getOrDefault(name, name);
                }

                @Override
                public String mapMethodName(String owner, String name, String desc) {
                    // comp_NNNN: a record component accessor (intermediary names them
                    // apart from method_; the table lists them as methods)
                    return name.startsWith("method_") || name.startsWith("comp_") ? member(owner, name) : name;
                }

                @Override
                public String mapFieldName(String owner, String name, String desc) {
                    // a record component's backing field carries the accessor's token
                    // and takes the accessor's mojang name
                    return name.startsWith("field_") || name.startsWith("comp_") ? member(owner, name) : name;
                }
            };
        }
    }

    public static Table loadTable(Path tsv) throws IOException {
        Table t = new Table();
        // owner-qualified ("interOwner.name" -> moj) wins; the bare-name map
        // covers call sites whose owner is a subclass of the declaring class,
        // and drops the handful of ids the client/server merge left ambiguous
        // (same field_/method_ id, two unrelated mojang members)
        java.util.Set<String> ambiguous = new java.util.HashSet<>();
        for (String ln : Files.readAllLines(tsv)) {
            String[] p = ln.split("\t");
            if (p.length < 3) {
                continue;
            }
            if (p[0].equals("C")) {
                t.classes.put(p[1], p[2]);
            } else if (p[0].equals("M") || p[0].equals("F")) {
                String name = p[1].substring(p[1].lastIndexOf('.') + 1);
                String moj = p[2].substring(p[2].lastIndexOf('.') + 1);
                t.byOwner.put(p[1], moj);
                String prev = t.members.putIfAbsent(name, moj);
                if (prev != null && !prev.equals(moj)) {
                    ambiguous.add(name);
                }
            }
        }
        t.members.keySet().removeAll(ambiguous);
        t.ambiguous = ambiguous.size();
        return t;
    }

    public static void remapWithTable(Path tsv, Path inJar, Path outJar) throws IOException {
        Table table = loadTable(tsv);
        int nClasses;
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(inJar));
             ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
            nClasses = remapEntries(zin, zout, table);
        }
        System.out.println("renamed " + nClasses + " classes (table: " + table.classes.size()
            + " classes, " + table.byOwner.size() + " members, " + table.ambiguous
            + " ambiguous ids owner-only) -> " + outJar);
    }

    /**
     * One jar layer. Bundled libraries under META-INF/jars/ are jars of their
     * own (the loader extracts and loads them like top-level mods) and get the
     * same rename recursively -- the converter rewrites them, BytecodeVerify
     * checks them, and until now the wall pass skipped them wholesale: every
     * jar-in-jar library crossed into 26.x still spelling intermediary names.
     */
    private static int remapEntries(ZipInputStream zin, ZipOutputStream zout, Table table) throws IOException {
        java.util.Map<String, String> byOwner = table.byOwner;
        java.util.Map<String, String> members = table.members;
        Remapper remapper = table.remapper();
        int nClasses = 0;
        java.util.Set<String> written = new java.util.HashSet<>();
        {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory()) {
                    continue;
                }
                // shaded fat jars sometimes carry literal duplicate entries;
                // first wins, same as every classloader would resolve them
                if (!written.add(e.getName())) {
                    continue;
                }
                byte[] data = zin.readAllBytes();
                if (e.getName().startsWith("META-INF/jars/") && e.getName().endsWith(".jar")) {
                    java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
                    try (ZipInputStream nin = new ZipInputStream(new java.io.ByteArrayInputStream(data));
                         ZipOutputStream nout = new ZipOutputStream(bos)) {
                        nClasses += remapEntries(nin, nout, table);
                    }
                    zout.putNextEntry(new ZipEntry(e.getName()));
                    zout.write(bos.toByteArray());
                    zout.closeEntry();
                    continue;
                }
                if (!e.getName().endsWith(".class")) {
                    if (e.getName().endsWith(".accesswidener")) {
                        // the loader validates the AW namespace against the
                        // runtime before a single class loads: an intermediary
                        // header on a 26.x server is fatal at boot
                        String text = new String(data, java.nio.charset.StandardCharsets.UTF_8);
                        StringBuilder sb = new StringBuilder();
                        for (String ln : text.split("\n", -1)) {
                            String t = ln.trim();
                            if (t.startsWith("accessWidener")) {
                                ln = ln.replace("intermediary", "official");
                            } else if (!t.isEmpty() && !t.startsWith("#")) {
                                String[] w = ln.split("\\s+");
                                // accessible|extendable|mutable  kind  owner  [name  desc]
                                if (w.length >= 4 && (w[1].equals("method") || w[1].equals("field"))) {
                                    String hit = byOwner.get(w[2] + "." + w[3]);
                                    if (hit == null) {
                                        hit = members.getOrDefault(w[3], w[3]);
                                    }
                                    w[3] = hit;
                                }
                                if (w.length >= 3) {
                                    w[2] = remapper.map(w[2]);
                                }
                                if (w.length >= 5) {
                                    w[4] = CLASS_TOKEN.matcher(w[4]).replaceAll(
                                        mr -> java.util.regex.Matcher.quoteReplacement(
                                            remapper.map(mr.group())));
                                }
                                ln = String.join(" ", w);
                            }
                            sb.append(ln).append("\n");
                        }
                        text = sb.toString();
                        if (text.endsWith("\n")) {
                            text = text.substring(0, text.length() - 1);
                        }
                        data = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    }
                    if (e.getName().endsWith(".json") && e.getName().contains("refmap")) {
                        // refmaps carry intermediary names as JSON strings
                        // ("Lnet/minecraft/class_310;method_1507()V"); rewrite
                        // them textually or every mixin target goes stale
                        String text = new String(data, java.nio.charset.StandardCharsets.UTF_8);
                        text = MEMBER_TOKEN.matcher(text).replaceAll(mr -> {
                            String owner = mr.group(1);
                            String id = mr.group(2);
                            String moj = owner != null ? byOwner.get(owner + "." + id) : null;
                            if (moj == null) {
                                moj = members.getOrDefault(id, id);
                            }
                            return java.util.regex.Matcher.quoteReplacement((owner != null ? "L" + owner + ";" : "") + moj);
                        });
                        text = CLASS_TOKEN.matcher(text).replaceAll(
                            mr -> java.util.regex.Matcher.quoteReplacement(
                                remapper.map(mr.group())));
                        data = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    }
                    zout.putNextEntry(new ZipEntry(e.getName()));
                    zout.write(data);
                    zout.closeEntry();
                    continue;
                }
                ClassReader cr = new ClassReader(data);
                ClassWriter cw = new ClassWriter(0);
                cr.accept(new ClassRemapper(cw, remapper), 0);
                String outName = remapper.map(cr.getClassName()) + ".class";
                if (!outName.equals(e.getName()) && !written.add(outName)) {
                    continue;
                }
                zout.putNextEntry(new ZipEntry(outName));
                zout.write(cw.toByteArray());
                zout.closeEntry();
                nClasses++;
            }
        }
        return nClasses;
    }

    public static void remapToIntermediary(Path tiny, Path inJar, Path outJar) throws IOException {
        TinyMappings.Obf obf = TinyMappings.loadObf(tiny);

        // The tiny file names a method once, on its declaring class. An
        // override in a subclass carries the same obf name and must get the
        // same intermediary name -- tiny-remapper propagates this through the
        // hierarchy at runtime, and a stub that does not leaves Screen.render
        // obfuscated below Drawable.render: javac then refuses subclasses,
        // mixin-check misses targets, the self-recursion gate goes blind.
        java.util.Map<String, byte[]> entries = new java.util.LinkedHashMap<>();
        java.util.Map<String, String> superOf = new java.util.HashMap<>();
        java.util.Map<String, String[]> ifacesOf = new java.util.HashMap<>();
        // owner.name+desc of every private method: a private method is not inherited, so a subclass
        // method of the same obf name and descriptor is never "its" override (ChestBoat's
        // clearItemStacks once took Boat's private controlBoat's name: the loader names it after
        // ContainerEntity, which declares it)
        java.util.Set<String> privates = new java.util.HashSet<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(inJar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory() || !e.getName().endsWith(".class")) {
                    continue; // classpath stub: classes only
                }
                byte[] data = zin.readAllBytes();
                entries.put(e.getName(), data);
                ClassReader cr = new ClassReader(data);
                superOf.put(cr.getClassName(), cr.getSuperName());
                ifacesOf.put(cr.getClassName(), cr.getInterfaces());
                String cls = cr.getClassName();
                cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                    @Override
                    public org.objectweb.asm.MethodVisitor visitMethod(int acc, String n, String d, String s, String[] x) {
                        if ((acc & org.objectweb.asm.Opcodes.ACC_PRIVATE) != 0) {
                            privates.add(cls + "." + n + d);
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }

        Remapper remapper = new Remapper() {
            @Override
            public String map(String internalName) {
                return obf.classes.getOrDefault(internalName, internalName);
            }

            @Override
            public String mapMethodName(String owner, String name, String desc) {
                String hit = obf.methods.get(owner + "." + name + desc);
                if (hit != null) {
                    return hit;
                }
                // climb as the JVM resolves: the superclass chain first, then the interfaces; a private
                // method of an ancestor is not inherited and owns nothing below it
                java.util.List<String> chain = new java.util.ArrayList<>();
                for (String cur = superOf.get(owner); cur != null; cur = superOf.get(cur)) {
                    chain.add(cur);
                }
                for (String cur : chain) {
                    String h = obf.methods.get(cur + "." + name + desc);
                    if (h != null && !privates.contains(cur + "." + name + desc)) {
                        return h;
                    }
                }
                java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
                java.util.Set<String> seen = new java.util.HashSet<>();
                todo.add(owner);
                todo.addAll(chain);
                while (!todo.isEmpty()) {
                    String cur = todo.poll();
                    if (!seen.add(cur)) {
                        continue;
                    }
                    String[] ifs = ifacesOf.get(cur);
                    if (ifs == null) {
                        continue;
                    }
                    for (String i : ifs) {
                        String h = obf.methods.get(i + "." + name + desc);
                        if (h != null && !privates.contains(i + "." + name + desc)) {
                            return h;
                        }
                        todo.add(i);
                    }
                }
                return name;
            }

            @Override
            public String mapFieldName(String owner, String name, String desc) {
                // a reference through a subclass (putfield Horse.at) names the
                // declaring superclass's field: climb like methods do
                for (String cur = owner; cur != null; cur = superOf.get(cur)) {
                    String h = obf.fields.get(cur + "." + name + ":" + desc);
                    if (h != null) {
                        return h;
                    }
                    String[] ifs = ifacesOf.get(cur);
                    if (ifs != null) {
                        for (String i : ifs) {
                            String hi = obf.fields.get(i + "." + name + ":" + desc);
                            if (hi != null) {
                                return hi;
                            }
                        }
                    }
                }
                return name;
            }
        };

        int classes = 0;
        try (ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
            for (byte[] data : entries.values()) {
                ClassReader cr = new ClassReader(data);
                ClassWriter cw = new ClassWriter(0);
                // CB_REMAP_KEEP_CODE=1: keep bodies -- an analysis jar for injector
                // anchor checks (MixinRepair) where no loader-remapped runtime exists
                cr.accept(new ClassRemapper(cw, remapper), System.getenv("CB_REMAP_KEEP_CODE") != null
                    ? ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
                    : ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                String newName = remapper.map(cr.getClassName());
                zout.putNextEntry(new ZipEntry(newName + ".class"));
                zout.write(cw.toByteArray());
                zout.closeEntry();
                classes++;
            }
        }
        System.out.println("remapped " + classes + " classes -> " + outJar);
    }
}
