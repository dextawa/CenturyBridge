package top.dext.centurybridge.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * The game's libraries between two releases -- the kill list the segment diffs never see.
 * The segments cover Minecraft's own classes; a 1.20.1 mod also calls authlib, DFU,
 * brigadier, JOML, Guava, fastutil, netty, LWJGL... and those move under it too
 * (authlib 7 made GameProfile a record, DFU 7 made DataResult a sealed interface).
 *
 * Artifacts are matched by maven coordinates (group/artifact; version and classifier
 * ignored, natives skipped). For every public class of the old artifact and every
 * public/protected member a reference through it resolved to (declared or inherited
 * within the library), the same reference is resolved in the new one:
 *
 *   CLASS     the class is gone (or the whole artifact is)
 *   KIND      class <-> interface (every call site's opcode is wrong: IncompatibleClassChangeError)
 *   METHOD    no member of that name and descriptor resolves any more
 *   FIELD     same, fields
 *   STATIC    static-ness flipped (IncompatibleClassChangeError)
 *   ACCESS    still there, no longer reachable (public/protected -> narrower)
 *   FINAL     an overridable method or extendable class made final
 *
 *   lib-diff <oldLibrariesDir> <newLibrariesDir> <out.tsv>
 */
public final class LibDiff {

    record Lib(String coord, Map<String, ClassNode> classes) {}

    public static void run(Path oldDir, Path newDir, Path out) throws IOException {
        Map<String, Lib> a = scan(oldDir);
        Map<String, Lib> b = scan(newDir);
        // a class may move between artifacts (netty 4.2 split netty-codec): look it up everywhere
        Map<String, ClassNode> everyNew = new HashMap<>();
        for (Lib l : b.values()) l.classes().forEach(everyNew::putIfAbsent);
        List<String[]> rows = new ArrayList<>();
        Map<String, Map<String, Integer>> summary = new TreeMap<>();
        for (Lib old : a.values()) {
            Lib now = b.get(old.coord());
            Map<String, Integer> s = summary.computeIfAbsent(old.coord(), k -> new TreeMap<>());
            for (ClassNode oc : old.classes().values()) {
                if ((oc.access & Opcodes.ACC_PUBLIC) == 0) continue;
                ClassNode nc = everyNew.get(oc.name);
                if (nc == null) {
                    rows.add(new String[] {old.coord(), "CLASS", oc.name, now == null ? "artifact gone" : ""});
                    s.merge("CLASS", 1, Integer::sum);
                    continue;
                }
                if (((oc.access ^ nc.access) & Opcodes.ACC_INTERFACE) != 0) {
                    rows.add(new String[] {old.coord(), "KIND", oc.name,
                        (nc.access & Opcodes.ACC_INTERFACE) != 0 ? "class -> interface" : "interface -> class"});
                    s.merge("KIND", 1, Integer::sum);
                }
                if ((nc.access & Opcodes.ACC_PUBLIC) == 0) {
                    rows.add(new String[] {old.coord(), "ACCESS", oc.name, "class no longer public"});
                    s.merge("ACCESS", 1, Integer::sum);
                }
                boolean extendable = (oc.access & (Opcodes.ACC_FINAL | Opcodes.ACC_INTERFACE)) == 0;
                if (extendable && (nc.access & Opcodes.ACC_FINAL) != 0) {
                    rows.add(new String[] {old.coord(), "FINAL", oc.name, "class made final"});
                    s.merge("FINAL", 1, Integer::sum);
                }
                for (Map.Entry<String, Integer> m : reachable(old.classes(), oc).entrySet()) {
                    String key = m.getKey();
                    int was = m.getValue();
                    boolean method = key.contains("(");
                    Integer now2 = resolve(everyNew, oc.name, key, method);
                    String sym = oc.name + "." + key;
                    if (now2 == null) {
                        if (resolvesInJdk(key, method)) continue;
                        rows.add(new String[] {old.coord(), method ? "METHOD" : "FIELD", sym, ""});
                        s.merge(method ? "METHOD" : "FIELD", 1, Integer::sum);
                        continue;
                    }
                    int n = now2;
                    if (((was ^ n) & Opcodes.ACC_STATIC) != 0) {
                        rows.add(new String[] {old.coord(), "STATIC", sym, (n & Opcodes.ACC_STATIC) != 0 ? "now static" : "no longer static"});
                        s.merge("STATIC", 1, Integer::sum);
                    }
                    boolean narrowed = (was & Opcodes.ACC_PUBLIC) != 0 ? (n & Opcodes.ACC_PUBLIC) == 0
                        : (n & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0;
                    if (narrowed) {
                        rows.add(new String[] {old.coord(), "ACCESS", sym, "narrowed"});
                        s.merge("ACCESS", 1, Integer::sum);
                    }
                    if (method && extendable && (was & (Opcodes.ACC_FINAL | Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) == 0
                            && !key.startsWith("<") && (n & Opcodes.ACC_FINAL) != 0) {
                        rows.add(new String[] {old.coord(), "FINAL", sym, "method made final"});
                        s.merge("FINAL", 1, Integer::sum);
                    }
                }
            }
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            w.println("artifact\tkind\tsymbol\tnote");
            for (String[] r : rows) w.println(String.join("\t", r));
        }
        summary.forEach((k, v) -> {
            if (!v.isEmpty()) System.out.println("  " + k + ": " + v);
        });
        System.out.println("lib-diff: " + a.size() + " old artifacts, " + b.size() + " new; " + rows.size() + " findings -> " + out);
    }

    /** a JDK supertype answers it: java.lang.Object's methods and the like are not the library's to lose */
    private static boolean resolvesInJdk(String key, boolean method) {
        return method && Set.of("toString()Ljava/lang/String;", "hashCode()I", "equals(Ljava/lang/Object;)Z",
            "getClass()Ljava/lang/Class;", "clone()Ljava/lang/Object;", "finalize()V", "notify()V", "notifyAll()V",
            "wait()V", "wait(J)V", "wait(JI)V").contains(key);
    }

    /** name+desc (methods) / name:desc (fields) -> access, for what resolved through cls in its library */
    private static Map<String, Integer> reachable(Map<String, ClassNode> lib, ClassNode cls) {
        Map<String, Integer> out = new HashMap<>();
        List<String> ifaces = new ArrayList<>();
        for (ClassNode c = cls; c != null; c = c.superName == null ? null : lib.get(c.superName)) {
            for (MethodNode m : c.methods) {
                if (m.name.equals("<clinit>") || (m.name.equals("<init>") && c != cls)) continue;
                if ((m.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0 || (m.access & Opcodes.ACC_SYNTHETIC) != 0) continue;
                out.putIfAbsent(m.name + m.desc, m.access);
            }
            for (FieldNode f : c.fields) {
                if ((f.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0 || (f.access & Opcodes.ACC_SYNTHETIC) != 0) continue;
                out.putIfAbsent(f.name + ":" + f.desc, f.access);
            }
            ifaces.addAll(c.interfaces);
        }
        Set<String> seen = new HashSet<>();
        while (!ifaces.isEmpty()) {
            ClassNode i = lib.get(ifaces.remove(0));
            if (i == null || !seen.add(i.name)) continue;
            for (MethodNode m : i.methods) {
                if ((m.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC)) != 0 || m.name.startsWith("<")) continue;
                out.putIfAbsent(m.name + m.desc, m.access);
            }
            for (FieldNode f : i.fields) out.putIfAbsent(f.name + ":" + f.desc, f.access);
            ifaces.addAll(i.interfaces);
        }
        return out;
    }

    private static Integer resolve(Map<String, ClassNode> lib, String cls, String key, boolean method) {
        List<String> ifaces = new ArrayList<>();
        for (ClassNode c = lib.get(cls); c != null; c = c.superName == null ? null : lib.get(c.superName)) {
            if (method) {
                for (MethodNode m : c.methods) if ((m.name + m.desc).equals(key)) return m.access;
                if (key.startsWith("<init>")) return null;
            } else {
                for (FieldNode f : c.fields) if ((f.name + ":" + f.desc).equals(key)) return f.access;
            }
            ifaces.addAll(c.interfaces);
        }
        Set<String> seen = new HashSet<>();
        while (!ifaces.isEmpty()) {
            ClassNode i = lib.get(ifaces.remove(0));
            if (i == null || !seen.add(i.name)) continue;
            if (method) {
                for (MethodNode m : i.methods) {
                    if ((m.name + m.desc).equals(key) && (m.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) == 0) return m.access;
                }
            } else {
                for (FieldNode f : i.fields) if ((f.name + ":" + f.desc).equals(key)) return f.access;
            }
            ifaces.addAll(i.interfaces);
        }
        return null;
    }

    /** coordinates (group/artifact) -> its classes; natives and Minecraft itself skipped */
    private static Map<String, Lib> scan(Path dir) throws IOException {
        Map<String, Lib> out = new TreeMap<>();
        List<Path> jars;
        try (Stream<Path> s = Files.walk(dir)) {
            jars = s.filter(p -> p.toString().endsWith(".jar")).sorted().toList();
        }
        for (Path j : jars) {
            Path rel = dir.relativize(j);
            int n = rel.getNameCount();
            if (n < 4 || j.getFileName().toString().contains("-natives-")) continue;
            String group = rel.subpath(0, n - 3).toString().replace('\\', '/');
            String coord = group + "/" + rel.getName(n - 3);
            if (coord.startsWith("net/minecraft/")) continue;
            Lib lib = out.computeIfAbsent(coord, k -> new Lib(k, new HashMap<>()));
            try (ZipFile z = new ZipFile(j.toFile())) {
                for (var en : java.util.Collections.list(z.entries())) {
                    String name = en.getName();
                    if (!name.endsWith(".class") || name.startsWith("META-INF/") || name.endsWith("module-info.class")) continue;
                    ClassNode cn = new ClassNode();
                    try {
                        new ClassReader(z.getInputStream(en).readAllBytes())
                            .accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    } catch (RuntimeException ex) {
                        continue;
                    }
                    lib.classes().putIfAbsent(cn.name, cn);
                }
            }
        }
        return out;
    }

    private LibDiff() {
    }
}
