package top.dext.centurybridge.engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * The first release past the namespace wall (26.1), its real method bodies spelled in the analysis
 * namespace the 1.21.11 -> 26.1 step converts in: what MixinBridge counts injector anchors in and retargets
 * selectors to at the wall, where the mod's mixins and the step's wiring are intermediary-named and W's own
 * code jar is not.
 *
 * Inverse of the wall table (intermediary -> mojang, data/mappings/inter-to-mojang-1.21.11-26.1.tsv). The
 * table names members without descriptors, and mojang names overload, so a member is taken back by its
 * owner, mojang name and descriptor together: every member 1.21.11 declares, renamed forward by the same
 * table, says which intermediary id stands for that (owner, name, descriptor) in 26.1. A member reached
 * through a subclass, or declared on a class 1.21.11 did not declare it on, takes the intermediary id when
 * exactly one 1.21.11 member renames to that name and descriptor. Classes and members born after the wall
 * keep their mojang names -- no 1.21.11 mod can name them, as Chain.loadTargetCtors says of constructors.
 */
public final class WallInverse {
    private WallInverse() {
    }

    /** writes W's code jar renamed into the analysis namespace to a temporary jar and returns it */
    public static Path intermediaryView(Path wCode, Path vCode, Path table) throws IOException {
        String cacheKey = vCode.toAbsolutePath().normalize() + "|" + table.toAbsolutePath().normalize();
        Inverse inv = INVERSES.get(cacheKey);
        if (inv == null) {
            inv = inverse(vCode, table);
            INVERSES.put(cacheKey, inv);
        }
        return view(wCode, inv.classBack, inv.ownerBack, inv.globalBack, inv.nameBack);
    }

    /**
     * the classes a mixin class (a step layer's) names in @Mixin (value and targets), first in its lookup order: Mixin
     * merges its members onto them, so a member it declares under a target's 26.1 name (a @Shadow, a restored field such
     * as Item.components) is that target's member and takes the target's id in the view, as a call naming the target does
     */
    private static java.util.List<String> mixinTargets(ClassReader cr) {
        java.util.List<String> out = new java.util.ArrayList<>();
        cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                if (!desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    return null;
                }
                return new org.objectweb.asm.AnnotationVisitor(org.objectweb.asm.Opcodes.ASM9) {
                    @Override
                    public org.objectweb.asm.AnnotationVisitor visitArray(String name) {
                        return new org.objectweb.asm.AnnotationVisitor(org.objectweb.asm.Opcodes.ASM9) {
                            @Override
                            public void visit(String n, Object value) {
                                if (value instanceof org.objectweb.asm.Type t) {
                                    out.add(t.getInternalName());
                                } else if (value instanceof String str && "targets".equals(name)) {
                                    out.add(str.replace('.', '/'));
                                }
                            }
                        };
                    }
                };
            }
        }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return out;
    }

    /** the wall table taken back (by 1.21.11's declarations), per (vCode, table): one conversion views several jars */
    private record Inverse(Map<String, String> classBack, Map<String, String> ownerBack,
                           Map<String, Set<String>> globalBack, Map<String, Set<String>> nameBack) {
    }

    private static final Map<String, Inverse> INVERSES = new HashMap<>();

    private static Inverse inverse(Path vCode, Path table) throws IOException {
        top.dext.centurybridge.data.JarRemapper.Table t = top.dext.centurybridge.data.JarRemapper.loadTable(table);
        Remapper fwd = t.remapper();
        Map<String, String> classBack = new HashMap<>();
        // an older token's row onto a class V declares by that name (the table's --extra rows: class_293 ->
        // com/mojang/blaze3d/vertex/VertexFormat, which 1.21.11 already spells that way) is not read backwards
        Set<String> vClasses = new HashSet<>();
        try (ZipFile z = new ZipFile(vCode.toFile())) {
            for (ZipEntry e : Collections.list(z.entries())) {
                if (e.getName().endsWith(".class")) {
                    vClasses.add(e.getName().substring(0, e.getName().length() - 6));
                }
            }
        }
        t.classes.forEach((inter, moj) -> {
            if (!(vClasses.contains(moj) && !vClasses.contains(inter))) {
                classBack.put(moj, inter);
            }
        });
        Map<String, String> ownerBack = new HashMap<>();
        Map<String, Set<String>> globalBack = new HashMap<>();
        try (ZipFile z = new ZipFile(vCode.toFile())) {
            for (ZipEntry e : Collections.list(z.entries())) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                ClassNode cn = new ClassNode();
                try (InputStream in = z.getInputStream(e)) {
                    new ClassReader(in).accept(cn, ClassReader.SKIP_CODE);
                }
                String mo = fwd.map(cn.name);
                for (MethodNode m : cn.methods) {
                    if (!m.name.startsWith("method_") && !m.name.startsWith("comp_")) {
                        continue;
                    }
                    String key = fwd.mapMethodName(cn.name, m.name, m.desc) + fwd.mapMethodDesc(m.desc);
                    ownerBack.put(mo + "." + key, m.name);
                    globalBack.computeIfAbsent(key, k -> new HashSet<>()).add(m.name);
                }
                for (FieldNode f : cn.fields) {
                    if (!f.name.startsWith("field_") && !f.name.startsWith("comp_")) {
                        continue;
                    }
                    String key = fwd.mapFieldName(cn.name, f.name, f.desc) + ":" + fwd.mapDesc(f.desc);
                    ownerBack.put(mo + "." + key, f.name);
                    globalBack.computeIfAbsent(key, k -> new HashSet<>()).add(f.name);
                }
            }
        }
        // "mojOwner.mojName" -> the intermediary ids the table renames to it on that owner: the wiring names
        // a member whose descriptor changed at the wall by the id of the same-named member of its owner
        Map<String, Set<String>> nameBack = new HashMap<>();
        t.byOwner.forEach((k, moj) -> {
            int dot = k.lastIndexOf('.');
            String o = t.classes.getOrDefault(k.substring(0, dot), k.substring(0, dot));
            nameBack.computeIfAbsent(o + "." + moj, x -> new HashSet<>()).add(k.substring(dot + 1));
        });
        return new Inverse(classBack, ownerBack, globalBack, nameBack);
    }

    private static Path view(Path wCode, Map<String, String> classBack, Map<String, String> ownerBack,
                             Map<String, Set<String>> globalBack, Map<String, Set<String>> nameBack) throws IOException {
        // W's hierarchy (mojang names): a call through a subclass finds the declaring owner's id
        Map<String, java.util.List<String>> supers = new HashMap<>();
        try (ZipFile z = new ZipFile(wCode.toFile())) {
            for (ZipEntry e : Collections.list(z.entries())) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                try (InputStream in = z.getInputStream(e)) {
                    ClassReader cr = new ClassReader(in);
                    java.util.List<String> up = new java.util.ArrayList<>(mixinTargets(cr));
                    if (cr.getSuperName() != null) {
                        up.add(cr.getSuperName());
                    }
                    up.addAll(java.util.Arrays.asList(cr.getInterfaces()));
                    supers.put(cr.getClassName(), up);
                }
            }
        }
        Remapper back = new Remapper() {
            @Override
            public String map(String internalName) {
                String hit = classBack.get(internalName);
                if (hit != null) {
                    return hit;
                }
                int d = internalName.length();
                while ((d = internalName.lastIndexOf('$', d - 1)) > 0) {
                    String outer = classBack.get(internalName.substring(0, d));
                    if (outer != null) {
                        return outer + internalName.substring(d);
                    }
                }
                return internalName;
            }

            private String member(String owner, String key, String name) {
                // the owner and its supertypes, nearest first: same name and descriptor, then same name
                java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(java.util.List.of(owner));
                Set<String> seen = new HashSet<>();
                while (!todo.isEmpty()) {
                    String o = todo.poll();
                    if (!seen.add(o)) {
                        continue;
                    }
                    String hit = ownerBack.get(o + "." + key);
                    if (hit != null) {
                        return hit;
                    }
                    Set<String> byName = nameBack.get(o + "." + name);
                    if (byName != null && byName.size() == 1) {
                        return byName.iterator().next();
                    }
                    todo.addAll(supers.getOrDefault(o, java.util.List.of()));
                }
                Set<String> all = globalBack.get(key);
                return all != null && all.size() == 1 ? all.iterator().next() : name;
            }

            @Override
            public String mapMethodName(String owner, String name, String desc) {
                return name.startsWith("<") ? name : member(owner, name + desc, name);
            }

            @Override
            public String mapFieldName(String owner, String name, String desc) {
                return member(owner, name + ":" + desc, name);
            }

            @Override
            public String mapRecordComponentName(String owner, String name, String desc) {
                return member(owner, name + ":" + desc, name);
            }
        };
        Path out = Files.createTempFile("cb-wall-view-", ".jar");
        out.toFile().deleteOnExit();
        int n = 0;
        try (ZipFile z = new ZipFile(wCode.toFile());
             ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(out))) {
            for (ZipEntry e : Collections.list(z.entries())) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                ClassReader cr;
                try (InputStream in = z.getInputStream(e)) {
                    cr = new ClassReader(in);
                }
                ClassWriter cw = new ClassWriter(0);
                cr.accept(new ClassRemapper(cw, back), 0);
                zout.putNextEntry(new ZipEntry(back.map(cr.getClassName()) + ".class"));
                zout.write(cw.toByteArray());
                zout.closeEntry();
                n++;
            }
        }
        System.out.println("wall view: " + n + " classes of " + wCode.getFileName() + " in the analysis namespace -> " + out);
        return out;
    }
}
