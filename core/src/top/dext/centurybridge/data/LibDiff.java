package top.dext.centurybridge.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
 * <p>Scope. With an artifacts table (core/tools/lib_artifacts.py: the version JSON's
 * libraries = client, the server bundler's libraries.list = server) only the artifacts V's
 * game ships are measured, and each row carries their sides. Loader-provided jars in the
 * runtime dirs (fabric-loader, sponge-mixin, the loader's asm...) are never measured, but
 * they stay on the class path of both sides: a Fabric runtime always has them, so a class
 * the game dropped that the loader still brings (asm after 1.21.11) still links. Artifacts
 * are matched by maven coordinates (group/artifact); when a dir holds several versions of
 * one, the release's own version is used, unless a newer one sits beside it: that is the
 * loader's copy (Fabric's ASM), the one on the runtime class path. Natives jars are skipped.
 *
 * <p>Resolution. Every reference is resolved on both sides exactly as the JVM does
 * (JVMS 5.4.3.2 fields: declared, then superinterfaces, then superclasses; 5.4.3.3 class
 * methods: superclass chain, then superinterface non-private non-static methods; 5.4.3.4
 * interface methods: declared, Object's public methods, superinterfaces), with JDK
 * supertypes read from the running JDK. A reference "through C" is a member key found
 * anywhere in C's library hierarchy that resolves, on V, to a public/protected,
 * non-synthetic member a library class declares (constructors: C's own only). So a
 * subclass's own private field that hides an inherited public one makes the reference
 * resolve to the private field on both sides (the JVM's IllegalAccessError, javac's
 * compile error) -- not a reachable reference at all, never a finding. Identical jars,
 * and V against V, give 0 rows.
 *
 * <pre>
 *   CLASS     the class is gone (or the whole artifact is), or no longer loads because a
 *             supertype it had on V is gone (one row; its inherited members are not listed)
 *   KIND      class &lt;-&gt; interface (every call site's opcode is wrong: IncompatibleClassChangeError)
 *   METHOD    no member of that name and descriptor resolves any more
 *   FIELD     same, fields
 *   STATIC    static-ness flipped (IncompatibleClassChangeError)
 *   ACCESS    still there, no longer reachable (public/protected -&gt; narrower)
 *   FINAL     an overridable method or extendable class made final
 *
 *   lib-diff &lt;oldLibrariesDir&gt; &lt;newLibrariesDir&gt; &lt;out.tsv&gt; [&lt;artifacts.tsv&gt; &lt;V&gt; &lt;W&gt;]
 * </pre>
 */
public final class LibDiff {

    /** one side: every class on the class path (library jars + JDK on demand), and where each came from */
    static final class Side {
        final Map<String, ClassNode> classes = new HashMap<>();
        final Map<String, String> home = new HashMap<>();
        final Map<String, ClassNode> jdk = new HashMap<>();
        final Set<String> missing = new HashSet<>();

        ClassNode get(String name) {
            if (name == null) return null;
            ClassNode c = classes.get(name);
            if (c != null) return c;
            if (jdk.containsKey(name)) return jdk.get(name);
            ClassNode j = readJdk(name);
            jdk.put(name, j);
            return j;
        }

        boolean isLib(ClassNode c) {
            return c != null && classes.get(c.name) == c;
        }
    }

    record Hit(ClassNode owner, int access) {}

    public static void run(Path oldDir, Path newDir, Path out) throws IOException {
        run(oldDir, newDir, out, null, null, null);
    }

    public static void run(Path oldDir, Path newDir, Path out, Path artifacts, String v, String w) throws IOException {
        Map<String, Map<String, String[]>> table = artifacts == null ? null : readArtifacts(artifacts);
        Map<String, String[]> vSet = table == null ? null : table.get(v);
        Map<String, String[]> wSet = table == null ? null : table.get(w);
        if (table != null && (vSet == null || wSet == null))
            throw new IllegalArgumentException("artifacts table has no rows for " + (vSet == null ? v : w));
        Map<String, Map<String, Map<String, ClassNode>>> a = scan(oldDir);
        Map<String, Map<String, Map<String, ClassNode>>> b = scan(newDir);
        Side oldS = side(a, vSet);
        Side newS = side(b, wSet);

        List<String[]> rows = new ArrayList<>();
        Map<String, Map<String, Integer>> summary = new TreeMap<>();
        for (String coord : a.keySet()) {
            if (vSet != null && !vSet.containsKey(coord)) continue;
            String sides = vSet == null ? "" : vSet.get(coord)[1];
            boolean gone = wSet != null ? !wSet.containsKey(coord) : !b.containsKey(coord);
            Map<String, Integer> s = summary.computeIfAbsent(coord, k -> new TreeMap<>());
            List<String> names = new ArrayList<>();
            oldS.home.forEach((n, h) -> {
                if (h.equals(coord)) names.add(n);
            });
            names.sort(null);
            for (String name : names) {
                ClassNode oc = oldS.classes.get(name);
                if ((oc.access & Opcodes.ACC_PUBLIC) == 0) continue;
                ClassNode nc = newS.get(name);
                if (nc == null) {
                    add(rows, s, coord, sides, "CLASS", name, gone ? "artifact gone" : "");
                    continue;
                }
                // a supertype the old side had and the new one lacks: the class no longer loads
                // (NoClassDefFoundError) -- one finding, not one per member inherited through it
                List<String> lost = new ArrayList<>();
                for (String t : supertypes(newS, nc)) if (newS.get(t) == null && oldS.get(t) != null) lost.add(t);
                if (!lost.isEmpty()) {
                    add(rows, s, coord, sides, "CLASS", name, "no longer loads: supertype gone " + String.join(",", lost));
                    continue;
                }
                if (((oc.access ^ nc.access) & Opcodes.ACC_INTERFACE) != 0)
                    add(rows, s, coord, sides, "KIND", name,
                        (nc.access & Opcodes.ACC_INTERFACE) != 0 ? "class -> interface" : "interface -> class");
                if ((nc.access & Opcodes.ACC_PUBLIC) == 0)
                    add(rows, s, coord, sides, "ACCESS", name, "class no longer public");
                boolean extendable = (oc.access & (Opcodes.ACC_FINAL | Opcodes.ACC_INTERFACE)) == 0;
                if (extendable && (nc.access & Opcodes.ACC_FINAL) != 0)
                    add(rows, s, coord, sides, "FINAL", name, "class made final");
                for (Map.Entry<String, Integer> m : reachable(oldS, oc).entrySet()) {
                    String key = m.getKey();
                    int was = m.getValue();
                    boolean method = key.contains("(");
                    newS.missing.clear();
                    Hit now = resolve(newS, nc, key);
                    String sym = name + "." + key;
                    if (now == null) {
                        String note = newS.missing.isEmpty() ? "" : "hierarchy incomplete: " + String.join(",", newS.missing);
                        add(rows, s, coord, sides, method ? "METHOD" : "FIELD", sym, note);
                        continue;
                    }
                    int n = now.access();
                    if (((was ^ n) & Opcodes.ACC_STATIC) != 0)
                        add(rows, s, coord, sides, "STATIC", sym, (n & Opcodes.ACC_STATIC) != 0 ? "now static" : "no longer static");
                    boolean narrowed = (was & Opcodes.ACC_PUBLIC) != 0 ? (n & Opcodes.ACC_PUBLIC) == 0
                        : (n & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0;
                    if (narrowed) add(rows, s, coord, sides, "ACCESS", sym, "narrowed");
                    if (method && extendable && (was & (Opcodes.ACC_FINAL | Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) == 0
                            && !key.startsWith("<") && (n & Opcodes.ACC_FINAL) != 0)
                        add(rows, s, coord, sides, "FINAL", sym, "method made final");
                }
            }
        }
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out))) {
            pw.println("artifact\tsides\tkind\tsymbol\tnote");
            for (String[] r : rows) pw.println(String.join("\t", r));
        }
        summary.forEach((k, m) -> {
            if (!m.isEmpty()) System.out.println("  " + k + ": " + m);
        });
        System.out.println("lib-diff: " + a.size() + " old artifacts, " + b.size() + " new; " + rows.size() + " findings -> " + out);
    }

    private static void add(List<String[]> rows, Map<String, Integer> s, String coord, String sides, String kind, String sym, String note) {
        rows.add(new String[] {coord, sides, kind, sym, note});
        s.merge(kind, 1, Integer::sum);
    }

    /**
     * every member key a reference through cls can name and that resolves (JVM-style, on this
     * side) to a public/protected, non-synthetic member declared by a library class:
     * key (name+desc for methods, name:desc for fields) -> the resolved member's access
     */
    private static Map<String, Integer> reachable(Side side, ClassNode cls) {
        Set<String> keys = new LinkedHashSet<>();
        Deque<ClassNode> todo = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        todo.add(cls);
        while (!todo.isEmpty()) {
            ClassNode c = todo.poll();
            if (!seen.add(c.name) || !side.isLib(c)) continue;
            for (MethodNode m : c.methods) {
                if (m.name.equals("<clinit>") || (m.name.equals("<init>") && c != cls)) continue;
                keys.add(m.name + m.desc);
            }
            for (FieldNode f : c.fields) keys.add(f.name + ":" + f.desc);
            ClassNode sup = side.get(c.superName);
            if (sup != null) todo.add(sup);
            for (String i : c.interfaces) {
                ClassNode ic = side.get(i);
                if (ic != null) todo.add(ic);
            }
        }
        Map<String, Integer> out = new TreeMap<>();
        for (String key : keys) {
            Hit h = resolve(side, cls, key);
            if (h == null || !side.isLib(h.owner())) continue;
            if ((h.access() & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0 || (h.access() & Opcodes.ACC_SYNTHETIC) != 0) continue;
            out.put(key, h.access());
        }
        return out;
    }

    /** JVMS 5.4.3.2 / 5.4.3.3 / 5.4.3.4 by the kind of cls */
    static Hit resolve(Side side, ClassNode cls, String key) {
        int p = key.indexOf('(');
        if (p < 0) {
            int c = key.indexOf(':');
            return resolveField(side, cls, key.substring(0, c), key.substring(c + 1), new HashSet<>());
        }
        String name = key.substring(0, p), desc = key.substring(p);
        if (name.equals("<init>")) {
            MethodNode m = declared(cls, name, desc);
            return m == null ? null : new Hit(cls, m.access);
        }
        return (cls.access & Opcodes.ACC_INTERFACE) != 0 ? resolveInterfaceMethod(side, cls, name, desc)
            : resolveClassMethod(side, cls, name, desc);
    }

    private static Hit resolveField(Side side, ClassNode c, String name, String desc, Set<String> seen) {
        if (c == null || !seen.add(c.name)) return null;
        for (FieldNode f : c.fields) if (f.name.equals(name) && f.desc.equals(desc)) return new Hit(c, f.access);
        for (String i : c.interfaces) {
            Hit h = resolveField(side, need(side, i), name, desc, seen);
            if (h != null) return h;
        }
        return c.superName == null ? null : resolveField(side, need(side, c.superName), name, desc, seen);
    }

    private static Hit resolveClassMethod(Side side, ClassNode cls, String name, String desc) {
        for (ClassNode c = cls; c != null; c = c.superName == null ? null : need(side, c.superName)) {
            MethodNode m = declared(c, name, desc);
            if (m != null) return new Hit(c, m.access);
        }
        return superinterfaceMethod(side, cls, name, desc);
    }

    private static Hit resolveInterfaceMethod(Side side, ClassNode cls, String name, String desc) {
        MethodNode m = declared(cls, name, desc);
        if (m != null) return new Hit(cls, m.access);
        ClassNode obj = side.get("java/lang/Object");
        if (obj != null) {
            MethodNode om = declared(obj, name, desc);
            if (om != null && (om.access & Opcodes.ACC_PUBLIC) != 0 && (om.access & Opcodes.ACC_STATIC) == 0)
                return new Hit(obj, om.access);
        }
        return superinterfaceMethod(side, cls, name, desc);
    }

    /**
     * the superinterface step: among non-private non-static methods of every superinterface,
     * a maximally-specific non-abstract one if there is exactly one, else any (JVMS 5.4.3.3 step 3)
     */
    private static Hit superinterfaceMethod(Side side, ClassNode cls, String name, String desc) {
        Set<String> all = new LinkedHashSet<>();
        Deque<String> todo = new ArrayDeque<>();
        for (ClassNode c = cls; c != null; c = c.superName == null ? null : need(side, c.superName)) todo.addAll(c.interfaces);
        while (!todo.isEmpty()) {
            String i = todo.poll();
            if (!all.add(i)) continue;
            ClassNode ic = need(side, i);
            if (ic != null) todo.addAll(ic.interfaces);
        }
        List<Hit> found = new ArrayList<>();
        for (String i : all) {
            ClassNode ic = side.get(i);
            if (ic == null) continue;
            MethodNode m = declared(ic, name, desc);
            if (m != null && (m.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) == 0) found.add(new Hit(ic, m.access));
        }
        if (found.isEmpty()) return null;
        List<Hit> specific = new ArrayList<>();
        for (Hit h : found) {
            boolean shadowed = false;
            for (Hit o : found) if (o != h && subInterface(side, o.owner(), h.owner().name, new HashSet<>())) shadowed = true;
            if (!shadowed && (h.access() & Opcodes.ACC_ABSTRACT) == 0) specific.add(h);
        }
        return specific.size() == 1 ? specific.get(0) : found.get(0);
    }

    private static boolean subInterface(Side side, ClassNode c, String target, Set<String> seen) {
        if (c == null || !seen.add(c.name)) return false;
        for (String i : c.interfaces) if (i.equals(target) || subInterface(side, side.get(i), target, seen)) return true;
        return false;
    }

    /** every supertype name of c (resolvable or not), transitively */
    private static Set<String> supertypes(Side side, ClassNode c) {
        Set<String> out = new LinkedHashSet<>();
        Deque<ClassNode> todo = new ArrayDeque<>();
        todo.add(c);
        while (!todo.isEmpty()) {
            ClassNode x = todo.poll();
            List<String> sup = new ArrayList<>(x.interfaces);
            if (x.superName != null) sup.add(x.superName);
            for (String t : sup) {
                if (!out.add(t)) continue;
                ClassNode tc = side.get(t);
                if (tc != null) todo.add(tc);
            }
        }
        return out;
    }

    private static MethodNode declared(ClassNode c, String name, String desc) {
        for (MethodNode m : c.methods) if (m.name.equals(name) && m.desc.equals(desc)) return m;
        return null;
    }

    private static ClassNode need(Side side, String name) {
        ClassNode c = side.get(name);
        if (c == null) side.missing.add(name);
        return c;
    }

    private static ClassNode readJdk(String name) {
        try (InputStream in = ClassLoader.getSystemResourceAsStream(name + ".class")) {
            if (in == null) return null;
            ClassNode cn = new ClassNode();
            new ClassReader(in.readAllBytes()).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return cn;
        } catch (IOException | RuntimeException ex) {
            return null;
        }
    }

    /**
     * the class path of one side: the release's own version of every artifact it ships, and
     * every other artifact in the dir (the loader's) at its highest version present
     */
    private static Side side(Map<String, Map<String, Map<String, ClassNode>>> dir, Map<String, String[]> set) {
        Side s = new Side();
        if (set != null) {
            for (Map.Entry<String, String[]> e : new TreeMap<>(set).entrySet()) {
                Map<String, Map<String, ClassNode>> versions = dir.get(e.getKey());
                if (versions == null) System.out.println("  warn: " + e.getKey() + " " + e.getValue()[0] + " not in the libraries dir (not measured)");
                else if (!versions.containsKey(e.getValue()[0]))
                    System.out.println("  warn: " + e.getKey() + " " + e.getValue()[0] + " not in the libraries dir, using " + versions.keySet());
            }
        }
        List<String> coords = new ArrayList<>(dir.keySet());
        // the game's artifacts first: a class both the game and a loader jar carry is the game's
        coords.sort((x, y) -> Boolean.compare(set == null || !set.containsKey(x), set == null || !set.containsKey(y)));
        for (String coord : coords) {
            Map<String, Map<String, ClassNode>> versions = dir.get(coord);
            String want = set != null && set.containsKey(coord) ? set.get(coord)[0] : null;
            String highest = versions.keySet().stream().max(LibDiff::compareVersions).orElseThrow();
            // a newer copy of a game artifact beside the game's own is the loader's (Fabric brings its ASM, which
            // the game listed too from 1.21.2 on), and the loader's is the one on the runtime class path
            String pick = want != null && versions.containsKey(want) && compareVersions(highest, want) <= 0 ? want : highest;
            versions.get(pick).forEach((n, c) -> {
                if (s.classes.putIfAbsent(n, c) == null) s.home.put(n, coord);
            });
        }
        return s;
    }

    static int compareVersions(String x, String y) {
        String[] a = x.split("[.\\-+]"), b = y.split("[.\\-+]");
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            boolean na = a[i].matches("\\d+"), nb = b[i].matches("\\d+");
            int c = na && nb ? Long.compare(Long.parseLong(a[i]), Long.parseLong(b[i])) : a[i].compareTo(b[i]);
            if (c != 0) return c;
        }
        return Integer.compare(a.length, b.length);
    }

    /** release -> group/artifact -> {version, sides} */
    private static Map<String, Map<String, String[]>> readArtifacts(Path f) throws IOException {
        Map<String, Map<String, String[]>> out = new HashMap<>();
        for (String line : Files.readAllLines(f)) {
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("release\t")) continue;
            String[] t = line.split("\t");
            out.computeIfAbsent(t[0], k -> new HashMap<>()).put(t[1], new String[] {t[2], t[3]});
        }
        return out;
    }

    /** group/artifact -> version -> its classes; natives and Minecraft itself skipped */
    private static Map<String, Map<String, Map<String, ClassNode>>> scan(Path dir) throws IOException {
        Map<String, Map<String, Map<String, ClassNode>>> out = new TreeMap<>();
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
            String version = rel.getName(n - 2).toString();
            if (coord.startsWith("net/minecraft/")) continue;
            Map<String, ClassNode> classes = out.computeIfAbsent(coord, k -> new TreeMap<>())
                .computeIfAbsent(version, k -> new HashMap<>());
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
                    classes.putIfAbsent(cn.name, cn);
                }
            }
        }
        return out;
    }

    private LibDiff() {
    }
}
