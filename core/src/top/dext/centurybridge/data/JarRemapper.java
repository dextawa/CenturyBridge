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
    public static void remapWithTable(Path tsv, Path inJar, Path outJar) throws IOException {
        java.util.Map<String, String> classes = new java.util.HashMap<>();
        // owner-qualified ("interOwner.name" -> moj) wins; the bare-name map
        // covers call sites whose owner is a subclass of the declaring class,
        // and drops the handful of ids the client/server merge left ambiguous
        // (same field_/method_ id, two unrelated mojang members)
        java.util.Map<String, String> byOwner = new java.util.HashMap<>();
        java.util.Map<String, String> members = new java.util.HashMap<>();
        java.util.Set<String> ambiguous = new java.util.HashSet<>();
        for (String ln : Files.readAllLines(tsv)) {
            String[] p = ln.split("\t");
            if (p.length < 3) {
                continue;
            }
            if (p[0].equals("C")) {
                classes.put(p[1], p[2]);
            } else if (p[0].equals("M") || p[0].equals("F")) {
                String name = p[1].substring(p[1].lastIndexOf('.') + 1);
                String moj = p[2].substring(p[2].lastIndexOf('.') + 1);
                byOwner.put(p[1], moj);
                String prev = members.putIfAbsent(name, moj);
                if (prev != null && !prev.equals(moj)) {
                    ambiguous.add(name);
                }
            }
        }
        members.keySet().removeAll(ambiguous);
        Remapper remapper = new Remapper() {
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
        int nClasses = 0;
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(inJar));
             ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory()) {
                    continue;
                }
                byte[] data = zin.readAllBytes();
                if (!e.getName().endsWith(".class")) {
                    if (e.getName().endsWith(".json") && e.getName().contains("refmap")) {
                        // refmaps carry intermediary names as JSON strings
                        // ("Lnet/minecraft/class_310;method_1507()V"); rewrite
                        // them textually or every mixin target goes stale
                        String text = new String(data, java.nio.charset.StandardCharsets.UTF_8);
                        text = MEMBER_TOKEN.matcher(text).replaceAll(
                            mr -> members.getOrDefault(mr.group(), mr.group()));
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
                zout.putNextEntry(new ZipEntry(remapper.map(cr.getClassName()) + ".class"));
                zout.write(cw.toByteArray());
                zout.closeEntry();
                nClasses++;
            }
        }
        System.out.println("renamed " + nClasses + " classes (table: " + classes.size()
            + " classes, " + byOwner.size() + " members, " + ambiguous.size()
            + " ambiguous ids owner-only) -> " + outJar);
    }

    public static void remapToIntermediary(Path tiny, Path inJar, Path outJar) throws IOException {
        TinyMappings.Obf obf = TinyMappings.loadObf(tiny);
        Remapper remapper = new Remapper() {
            @Override
            public String map(String internalName) {
                return obf.classes.getOrDefault(internalName, internalName);
            }

            @Override
            public String mapMethodName(String owner, String name, String desc) {
                return obf.methods.getOrDefault(owner + "." + name + desc, name);
            }

            @Override
            public String mapFieldName(String owner, String name, String desc) {
                return obf.fields.getOrDefault(owner + "." + name + ":" + desc, name);
            }
        };

        int classes = 0;
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(inJar));
             ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory() || !e.getName().endsWith(".class")) {
                    continue; // classpath stub: classes only
                }
                byte[] data = zin.readAllBytes();
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
