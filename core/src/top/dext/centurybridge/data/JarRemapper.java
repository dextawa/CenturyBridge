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

    private static final java.util.regex.Pattern MEMBER_TOKEN =
        java.util.regex.Pattern.compile("(?:method|field)_\\d+");
    private static final java.util.regex.Pattern CLASS_TOKEN =
        java.util.regex.Pattern.compile("net/minecraft/class_\\d+(?:\\$[\\w$]+)?");

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
                    return name.startsWith("method_") ? member(owner, name) : name;
                }

                @Override
                public String mapFieldName(String owner, String name, String desc) {
                    return name.startsWith("field_") ? member(owner, name) : name;
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
        java.util.Map<String, String> byOwner = table.byOwner;
        java.util.Map<String, String> members = table.members;
        Remapper remapper = table.remapper();
        int nClasses = 0;
        java.util.Set<String> written = new java.util.HashSet<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(inJar));
             ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
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
                        text = MEMBER_TOKEN.matcher(text).replaceAll(
                            mr -> java.util.regex.Matcher.quoteReplacement(
                                members.getOrDefault(mr.group(), mr.group())));
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
        System.out.println("renamed " + nClasses + " classes (table: " + table.classes.size()
            + " classes, " + byOwner.size() + " members, " + table.ambiguous
            + " ambiguous ids owner-only) -> " + outJar);
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
                // climb: the declaring ancestor owns the mapping
                java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
                java.util.Set<String> seen = new java.util.HashSet<>();
                todo.add(owner);
                while (!todo.isEmpty()) {
                    String cur = todo.poll();
                    if (!seen.add(cur)) {
                        continue;
                    }
                    String h = obf.methods.get(cur + "." + name + desc);
                    if (h != null) {
                        return h;
                    }
                    String sup = superOf.get(cur);
                    if (sup != null) {
                        todo.add(sup);
                    }
                    String[] ifs = ifacesOf.get(cur);
                    if (ifs != null) {
                        for (String i : ifs) {
                            todo.add(i);
                        }
                    }
                }
                return name;
            }

            @Override
            public String mapFieldName(String owner, String name, String desc) {
                return obf.fields.getOrDefault(owner + "." + name + ":" + desc, name);
            }
        };

        int classes = 0;
        try (ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
            for (byte[] data : entries.values()) {
                ClassReader cr = new ClassReader(data);
                ClassWriter cw = new ClassWriter(0);
                cr.accept(new ClassRemapper(cw, remapper),
                    ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
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
