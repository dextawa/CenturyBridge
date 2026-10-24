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
        java.util.regex.Pattern.compile("(?:L([\\w/$]+);)?((?:method|field|comp)_\\d+)(\\([^)]*\\)[^\\s\"\\\\,]*)?");
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
        /** bare intermediary method id -> every mojang name the table gives it (more than one: reused ids) */
        public final java.util.Map<String, java.util.Set<String>> candidates = new java.util.HashMap<>();
        /**
         * The target release's methods ("owner" -> name + desc, mojang names) and supertypes, and the input jar's
         * own classes' supertypes (renamed): with them a reused id resolves by descriptor ({@link #remapper}).
         * Null when no target jar is known.
         */
        public java.util.Map<String, java.util.Set<String>> targetMethods;
        public java.util.Map<String, String[]> targetSupers;
        public final java.util.Map<String, String[]> jarSupers = new java.util.HashMap<>();

        /**
         * Tokens only the step's facade classes declare keep their names across the wall. A class the
         * step retires into a CB facade ({@code classRenames} to a top/dext/centurybridge/ class) keeps
         * its members' intermediary names -- the facade declares them so, and a reference naming the
         * facade passes through untouched ({@link #remapper}) -- but a reference through a mod class
         * under it (a call of an inherited facade method, the mod's own override of one) names the mod
         * class, and the bare-name map gave it the vanilla member's mojang name: 1.21.11's
         * ExpirableValue.tick() is method_24913 on the facade, and a mod subclass's this.tick() went out
         * as tick (NoSuchMethodError), its override of it as tick (never called). A token declared by any
         * other class as well (an override family reaching vanilla classes) keeps its mapping.
         * <p>
         * Only a token the facade really declares under that name is kept. A facade written against the
         * target declares the member under the table's name instead when it overrides the target's own
         * (1.21.11 ArmorDyeRecipe.matches(CraftingInput, Level), method_17701, is 26.1 Recipe.matches on the
         * facade): there the token takes the table's rename, through the mod class and through the facade
         * alike ({@link #remapper}), or the mod's override kept the token and nothing called it (ORPHAN).
         * {@code declared}: each facade's member names, its CB superclasses' included ({@link #facadeMembers});
         * null when the facades are not known, and then every facade-only token is kept.
         */
        public Table keepFacadeTokens(java.util.Map<String, String> classRenames, java.util.Map<String, java.util.Set<String>> declared) {
            java.util.Set<String> facadeOwners = facadeOwners(classRenames);
            facadeOrigins.clear();
            for (String c : facadeOwners) {
                facadeOrigins.computeIfAbsent(classRenames.get(c), x -> new java.util.ArrayList<>()).add(c);
            }
            facadeDeclared = declared;
            java.util.Map<String, java.util.Set<String>> owners = new java.util.HashMap<>();
            for (String k : byOwner.keySet()) {
                int dot = k.lastIndexOf('.');
                owners.computeIfAbsent(k.substring(dot + 1), x -> new java.util.HashSet<>()).add(k.substring(0, dot));
            }
            for (var e : owners.entrySet()) {
                if (facadeOwners.containsAll(e.getValue()) && (declared == null || declaredByFacade(e.getValue(), classRenames, e.getKey()))) {
                    members.remove(e.getKey());
                }
            }
            return this;
        }

        private boolean declaredByFacade(java.util.Set<String> owners, java.util.Map<String, String> classRenames, String token) {
            for (String o : owners) {
                java.util.Set<String> d = facadeDeclared.get(classRenames.get(o));
                if (d != null && d.contains(token)) {
                    return true;
                }
            }
            return false;
        }

        /** facade class -> the intermediary classes classRenames retire into it ({@link #keepFacadeTokens}) */
        public final java.util.Map<String, java.util.List<String>> facadeOrigins = new java.util.HashMap<>();
        /** facade class -> the member names it declares or inherits from CB superclasses; null: not known */
        public java.util.Map<String, java.util.Set<String>> facadeDeclared;

        /**
         * A reference naming a facade: the token it names when the facade declares it so (or the facades are
         * not known); otherwise the table's rename of the member of the class retired into it (or of one it
         * inherits), null if none.
         */
        String facadeMember(String facade, String name) {
            java.util.List<String> origins = facadeOrigins.get(facade);
            if (origins == null || facadeDeclared == null) {
                return null;
            }
            java.util.Set<String> d = facadeDeclared.get(facade);
            if (d != null && d.contains(name)) {
                return null;
            }
            for (String o : origins) {
                String hit = byOwner.get(o + "." + name);
                if (hit != null) {
                    return hit;
                }
            }
            // a member the facade inherits from the target (26.1 CustomRecipe.isSpecial under ArmorDyeRecipe)
            return members.get(name);
        }

        /**
         * The member names of the CB classes among {@code classes} (internal name -> node), each with those
         * of its CB superclasses: what {@link #keepFacadeTokens} reads a facade's surface from.
         */
        public static java.util.Map<String, java.util.Set<String>> facadeMembers(java.util.Map<String, org.objectweb.asm.tree.ClassNode> classes) {
            java.util.Map<String, java.util.Set<String>> out = new java.util.HashMap<>();
            for (String n : classes.keySet()) {
                if (!n.startsWith("top/dext/centurybridge/")) {
                    continue;
                }
                java.util.Set<String> names = new java.util.HashSet<>();
                java.util.Set<String> seen = new java.util.HashSet<>();
                for (org.objectweb.asm.tree.ClassNode c = classes.get(n); c != null && seen.add(c.name);
                     c = c.superName != null ? classes.get(c.superName) : null) {
                    c.methods.forEach(m -> names.add(m.name));
                    c.fields.forEach(f -> names.add(f.name));
                }
                out.put(n, names);
            }
            return out;
        }

        /** {@link #facadeMembers} of a built layer jar's CB classes */
        public static java.util.Map<String, java.util.Set<String>> facadeMembers(Path layerJar) throws IOException {
            java.util.Map<String, org.objectweb.asm.tree.ClassNode> classes = new java.util.HashMap<>();
            try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(layerJar))) {
                ZipEntry e;
                while ((e = zin.getNextEntry()) != null) {
                    if (!e.getName().endsWith(".class") || !e.getName().startsWith("top/dext/centurybridge/")) {
                        continue;
                    }
                    org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
                    new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(cn.name, cn);
                }
            }
            return facadeMembers(classes);
        }

        /** the classes a wiring's classRenames retire into CB facades */
        public static java.util.Set<String> facadeOwners(java.util.Map<String, String> classRenames) {
            java.util.Set<String> out = new java.util.HashSet<>();
            classRenames.forEach((k, v) -> {
                if (v.startsWith("top/dext/centurybridge/")) {
                    out.add(k);
                }
            });
            return out;
        }
        /**
         * The target release's game classes and whether each is an interface (null: not known). 26.1 made
         * some 1.21.11 interfaces classes (GpuDevice, CommandEncoder, RenderPass wrap a backend now) and
         * could do the reverse; a call keeps the invoke kind and the interface flag of the type it was
         * compiled against, which the JVM refuses on the other kind (IncompatibleClassChangeError at the
         * first call). With this set the rename gives every call, method handle and invokedynamic argument
         * the kind the target declares the owner as.
         */
        public java.util.Map<String, Boolean> targetKinds;

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
                        // a facade not declaring the token answers to the table's name (keepFacadeTokens)
                        String f = facadeMember(owner, name);
                        return f != null ? f : name;
                    }
                    String hit = byOwner.get(owner + "." + name);
                    return hit != null ? hit : members.getOrDefault(name, name);
                }

                @Override
                public String mapMethodName(String owner, String name, String desc) {
                    // comp_NNNN: a record component accessor (intermediary names them
                    // apart from method_; the table lists them as methods)
                    if (!name.startsWith("method_") && !name.startsWith("comp_")) {
                        return name;
                    }
                    String m = member(owner, name);
                    return targetMethods == null ? m : byDescriptor(owner, name, desc, m);
                }

                /**
                 * An id intermediary gave two members of one hierarchy (1.21.11's RepeatingPlacement.count(random,
                 * pos) and getPositions(context, random, pos) are both method_14452) maps to two names; the
                 * table keys members by owner and name only, so a call through any other owner found no
                 * mapping and an override of the other member took the wrong name. When the name chosen is
                 * not a method the target declares with this (renamed) descriptor in the owner's hierarchy,
                 * the one candidate it does declare is taken.
                 */
                private String byDescriptor(String owner, String name, String desc, String m) {
                    java.util.Set<String> cand = candidates.get(name);
                    if (cand == null || cand.size() < 2 || owner.startsWith("top/dext/centurybridge/")) {
                        return m;
                    }
                    String mo = map(owner);
                    String md = mapMethodDesc(desc);
                    if (declares(mo, m + md, new java.util.HashSet<>())) {
                        return m;
                    }
                    String only = null;
                    for (String c : cand) {
                        if (declares(mo, c + md, new java.util.HashSet<>())) {
                            if (only != null) {
                                return m;
                            }
                            only = c;
                        }
                    }
                    return only != null ? only : m;
                }

                private boolean declares(String owner, String nameDesc, java.util.Set<String> seen) {
                    if (owner == null || !seen.add(owner)) {
                        return false;
                    }
                    java.util.Set<String> ms = targetMethods.get(owner);
                    if (ms != null && ms.contains(nameDesc)) {
                        return true;
                    }
                    String[] sup = targetSupers.containsKey(owner) ? targetSupers.get(owner) : jarSupers.get(owner);
                    if (sup != null) {
                        for (String x : sup) {
                            if (declares(x, nameDesc, seen)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public String mapFieldName(String owner, String name, String desc) {
                    // a record component's backing field carries the accessor's token
                    // and takes the accessor's mojang name
                    return name.startsWith("field_") || name.startsWith("comp_") ? member(owner, name) : name;
                }

                /**
                 * A lambda or method reference names the functional interface's method in its
                 * invokedynamic (LambdaMetafactory: the call site's name is the SAM's, the interface is
                 * the descriptor's return type). ASM's default keeps that name, so a 1.21.11 lambda for a
                 * vanilla interface crossed the wall still implementing method_NNNN -- an
                 * AbstractMethodError at the first call. Other bootstraps (string concatenation, record
                 * object methods, pattern switches) never carry a token name and stay as they are.
                 */
                @Override
                public String mapInvokeDynamicMethodName(String name, String desc) {
                    if (!name.startsWith("method_") && !name.startsWith("comp_")) {
                        return name;
                    }
                    org.objectweb.asm.Type ret = org.objectweb.asm.Type.getReturnType(desc);
                    return ret.getSort() == org.objectweb.asm.Type.OBJECT ? member(ret.getInternalName(), name) : name;
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
                if (p[0].equals("M")) {
                    t.candidates.computeIfAbsent(name, x -> new java.util.TreeSet<>()).add(moj);
                }
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
        remapWithTable(tsv, inJar, outJar, null);
    }

    /** as above; with {@code targetJar}, calls also take the invoke kind the target declares their owner as ({@link Table#targetKinds}) */
    public static void remapWithTable(Path tsv, Path inJar, Path outJar, Path targetJar) throws IOException {
        remapWithTable(loadTable(tsv), inJar, outJar, targetJar);
    }

    /** as above, with a loaded table (the conversion's, facade tokens kept: {@link Table#keepFacadeTokens}) */
    public static void remapWithTable(Table table, Path inJar, Path outJar, Path targetJar) throws IOException {
        if (targetJar != null && Files.exists(targetJar)) {
            table.targetKinds = loadKinds(targetJar);
            loadTarget(table, targetJar);
            loadJarSupers(table, inJar);
        }
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
     * Intermediary names in text renamed (a refmap; a mixin annotation's string): member ids first (under the
     * owner a preceding "L<owner>;" names), then class names.
     */
    static String renameText(String text, Table table) {
        Remapper remapper = table.remapper();
        text = MEMBER_TOKEN.matcher(text).replaceAll(mr -> {
            String owner = mr.group(1);
            String id = mr.group(2);
            String desc = mr.group(3);
            String moj = owner != null ? table.byOwner.get(owner + "." + id) : null;
            if (moj == null) {
                moj = table.members.getOrDefault(id, id);
            }
            if (owner != null && desc != null && table.targetMethods != null && !id.startsWith("field_")) {
                // a reused id resolves by the descriptor written after it, as the class files' pass does
                moj = remapper.mapMethodName(owner, id, desc);
            }
            return java.util.regex.Matcher.quoteReplacement((owner != null ? "L" + owner + ";" : "") + moj + (desc != null ? desc : ""));
        });
        return CLASS_TOKEN.matcher(text).replaceAll(
            mr -> java.util.regex.Matcher.quoteReplacement(remapper.map(mr.group())));
    }

    /**
     * The strings of a class's Mixin / MixinExtras annotations renamed like a refmap (upstream of the
     * ClassRemapper, which renames Type values only): selectors, @At targets, accessor names a mixin spells
     * in intermediary itself -- a mod built without a refmap, or what the engine's MixinBridge wrote into an
     * annotation in the analysis namespace at the wall step. A mod's development names (a refmap's keys)
     * hold no intermediary token and pass unchanged.
     */
    private static org.objectweb.asm.ClassVisitor mixinStrings(org.objectweb.asm.ClassVisitor next, Table table) {
        java.util.function.Function<org.objectweb.asm.AnnotationVisitor, org.objectweb.asm.AnnotationVisitor>[] wrap =
            new java.util.function.Function[1];
        wrap[0] = av -> new org.objectweb.asm.AnnotationVisitor(org.objectweb.asm.Opcodes.ASM9, av) {
            @Override
            public void visit(String name, Object value) {
                super.visit(name, value instanceof String s ? renameText(s, table) : value);
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String name, String desc) {
                org.objectweb.asm.AnnotationVisitor n = super.visitAnnotation(name, desc);
                return n == null ? null : wrap[0].apply(n);
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitArray(String name) {
                org.objectweb.asm.AnnotationVisitor n = super.visitArray(name);
                return n == null ? null : wrap[0].apply(n);
            }
        };
        java.util.function.BiFunction<String, org.objectweb.asm.AnnotationVisitor, org.objectweb.asm.AnnotationVisitor> top =
            (desc, av) -> av != null && (desc.startsWith("Lorg/spongepowered/asm/mixin/")
                || desc.startsWith("Lcom/llamalad7/mixinextras/")) ? wrap[0].apply(av) : av;
        return new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9, next) {
            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return top.apply(desc, super.visitAnnotation(desc, visible));
            }

            @Override
            public org.objectweb.asm.MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                return new org.objectweb.asm.MethodVisitor(org.objectweb.asm.Opcodes.ASM9, super.visitMethod(acc, name, desc, sig, ex)) {
                    @Override
                    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return top.apply(d, super.visitAnnotation(d, visible));
                    }
                };
            }

            @Override
            public org.objectweb.asm.FieldVisitor visitField(int acc, String name, String desc, String sig, Object value) {
                return new org.objectweb.asm.FieldVisitor(org.objectweb.asm.Opcodes.ASM9, super.visitField(acc, name, desc, sig, value)) {
                    @Override
                    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return top.apply(d, super.visitAnnotation(d, visible));
                    }
                };
            }
        };
    }

    /**
     * One jar layer. Bundled libraries under META-INF/jars/ are jars of their
     * own (the loader extracts and loads them like top-level mods) and get the
     * same rename recursively -- the converter rewrites them, BytecodeVerify
     * checks them, and until now the wall pass skipped them wholesale: every
     * jar-in-jar library crossed into 26.x still spelling intermediary names.
     */
    /** a nested jar marked "centurybridge:verbatim" (JarProcessor.verbatimLibrary) */
    private static boolean verbatim(byte[] jar) throws IOException {
        try (ZipInputStream nin = new ZipInputStream(new java.io.ByteArrayInputStream(jar))) {
            ZipEntry n;
            while ((n = nin.getNextEntry()) != null) {
                if (n.getName().equals("fabric.mod.json")) {
                    return top.dext.centurybridge.engine.JarProcessor.verbatimLibrary(nin.readAllBytes());
                }
            }
        }
        return false;
    }

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
                if (e.getName().startsWith("META-INF/jars/") && e.getName().endsWith(".jar") && verbatim(data)) {
                    zout.putNextEntry(new ZipEntry(e.getName())); // a library a layer gives back whole: byte for byte
                    zout.write(data);
                    zout.closeEntry();
                    continue;
                }
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
                        data = renameText(text, table).getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    }
                    zout.putNextEntry(new ZipEntry(e.getName()));
                    zout.write(data);
                    zout.closeEntry();
                    continue;
                }
                ClassReader cr = new ClassReader(data);
                ClassWriter cw = new ClassWriter(0);
                cr.accept(mixinStrings(new ClassRemapper(table.targetKinds == null ? cw : new KindFix(cw, table.targetKinds), remapper), table), 0);
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

    /** the target's game classes' methods and supertypes ({@link Table#targetMethods}) */
    public static void loadTarget(Table table, Path jar) throws IOException {
        java.util.Map<String, java.util.Set<String>> methods = new java.util.HashMap<>();
        java.util.Map<String, String[]> supers = new java.util.HashMap<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                String n = e.getName();
                if (!n.endsWith(".class") || !(n.startsWith("net/minecraft/") || n.startsWith("com/mojang/"))) {
                    continue;
                }
                org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
                new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                java.util.Set<String> ms = new java.util.HashSet<>();
                for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
                    ms.add(m.name + m.desc);
                }
                methods.put(cn.name, ms);
                java.util.List<String> sup = new java.util.ArrayList<>();
                if (cn.superName != null) {
                    sup.add(cn.superName);
                }
                sup.addAll(cn.interfaces);
                supers.put(cn.name, sup.toArray(new String[0]));
            }
        }
        table.targetMethods = methods;
        table.targetSupers = supers;
    }

    /** the input jar's classes' supertypes, renamed as the jar will be ({@link Table#jarSupers}) */
    private static void loadJarSupers(Table table, Path jar) throws IOException {
        Remapper r = table.remapper();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                ClassReader cr = new ClassReader(zin.readAllBytes());
                java.util.List<String> sup = new java.util.ArrayList<>();
                if (cr.getSuperName() != null) {
                    sup.add(r.map(cr.getSuperName()));
                }
                for (String i : cr.getInterfaces()) {
                    sup.add(r.map(i));
                }
                table.jarSupers.put(r.map(cr.getClassName()), sup.toArray(new String[0]));
            }
        }
    }

    /** every game class of a jar and whether it is an interface */
    private static java.util.Map<String, Boolean> loadKinds(Path jar) throws IOException {
        java.util.Map<String, Boolean> kinds = new java.util.HashMap<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                String n = e.getName();
                if (!n.endsWith(".class") || !(n.startsWith("net/minecraft/") || n.startsWith("com/mojang/"))) {
                    continue;
                }
                ClassReader cr = new ClassReader(zin.readAllBytes());
                kinds.put(cr.getClassName(), (cr.getAccess() & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0);
            }
        }
        return kinds;
    }

    /** gives calls on target game classes the invoke kind the target declares the owner as (runs after the rename) */
    private static final class KindFix extends org.objectweb.asm.ClassVisitor {
        private final java.util.Map<String, Boolean> kinds;

        KindFix(org.objectweb.asm.ClassVisitor cv, java.util.Map<String, Boolean> kinds) {
            super(org.objectweb.asm.Opcodes.ASM9, cv);
            this.kinds = kinds;
        }

        private org.objectweb.asm.Handle fix(org.objectweb.asm.Handle h) {
            Boolean itf = this.kinds.get(h.getOwner());
            if (itf == null || itf == h.isInterface()) {
                return h;
            }
            int tag = h.getTag();
            if (tag == org.objectweb.asm.Opcodes.H_INVOKEINTERFACE && !itf) {
                tag = org.objectweb.asm.Opcodes.H_INVOKEVIRTUAL;
            } else if (tag == org.objectweb.asm.Opcodes.H_INVOKEVIRTUAL && itf) {
                tag = org.objectweb.asm.Opcodes.H_INVOKEINTERFACE;
            }
            return new org.objectweb.asm.Handle(tag, h.getOwner(), h.getName(), h.getDesc(), itf);
        }

        private Object fixValue(Object v) {
            return v instanceof org.objectweb.asm.Handle h ? fix(h) : v;
        }

        @Override
        public org.objectweb.asm.MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] exc) {
            org.objectweb.asm.MethodVisitor mv = super.visitMethod(acc, name, desc, sig, exc);
            return mv == null ? null : new org.objectweb.asm.MethodVisitor(org.objectweb.asm.Opcodes.ASM9, mv) {
                @Override
                public void visitMethodInsn(int op, String owner, String n, String d, boolean itf) {
                    Boolean want = KindFix.this.kinds.get(owner);
                    if (want != null && want != itf) {
                        if (op == org.objectweb.asm.Opcodes.INVOKEINTERFACE && !want) {
                            op = org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
                        } else if (op == org.objectweb.asm.Opcodes.INVOKEVIRTUAL && want) {
                            op = org.objectweb.asm.Opcodes.INVOKEINTERFACE;
                        }
                        itf = want;
                    }
                    super.visitMethodInsn(op, owner, n, d, itf);
                }

                @Override
                public void visitInvokeDynamicInsn(String n, String d, org.objectweb.asm.Handle bsm, Object... args) {
                    Object[] fixed = new Object[args.length];
                    for (int i = 0; i < args.length; i++) {
                        fixed[i] = fixValue(args[i]);
                    }
                    super.visitInvokeDynamicInsn(n, d, fix(bsm), fixed);
                }

                @Override
                public void visitLdcInsn(Object value) {
                    super.visitLdcInsn(fixValue(value));
                }
            };
        }
    }
}
