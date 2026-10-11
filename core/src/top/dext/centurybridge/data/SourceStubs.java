package top.dext.centurybridge.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * The intermediary stubs of every release before a target, newest first: where
 * the ORIGINAL of a wired symbol is looked up. The converter keeps the opcode a
 * mod was compiled with unless its wiring says otherwise, so the gates need the
 * original member's kind -- a cover or instance redirect for a static method, or
 * a static redirect for an instance one, never matches a call site.
 *
 * A symbol that died at the target existed in the release before it; one that
 * died earlier is found further back. Classes load lazily, on first lookup.
 */
public final class SourceStubs implements AutoCloseable {

    private final List<ZipFile> jars = new ArrayList<>();
    private final List<Map<String, Optional<ClassNode>>> cache = new ArrayList<>();

    /** The stubs of every release before {@code targetVersion} found in {@code stubDir}. */
    public static SourceStubs before(Path stubDir, String targetVersion) throws IOException {
        List<String> releases = top.dext.centurybridge.engine.Chain.RELEASES;
        int t = releases.indexOf(targetVersion);
        if (t < 0) {
            throw new IllegalArgumentException("unknown target version " + targetVersion);
        }
        SourceStubs s = new SourceStubs();
        for (int i = t - 1; i >= 0; i--) {
            Path stub = stubDir.resolve("client-" + releases.get(i) + "-intermediary.jar");
            if (Files.exists(stub)) {
                s.jars.add(new ZipFile(stub.toFile()));
                s.cache.add(new HashMap<>());
            }
        }
        return s;
    }

    /** No stubs at all: every lookup answers "unknown". */
    public static SourceStubs none() {
        return new SourceStubs();
    }

    public boolean isEmpty() {
        return jars.isEmpty();
    }

    /** the class in every older release that has it, newest first (members only) */
    public List<ClassNode> versionsOf(String name) {
        List<ClassNode> out = new ArrayList<>();
        for (int i = 0; i < jars.size(); i++) {
            ClassNode cn = load(i, name);
            if (cn != null) {
                out.add(cn);
            }
        }
        return out;
    }

    /**
     * For every older release in which {@code name} is an interface: its abstract methods,
     * "name(desc)", across the interface hierarchy -- what a lambda implementing it has to
     * supply. A default in a more specific interface implements a super's abstract one;
     * Object's public methods redeclared abstract (Comparator.equals) do not count.
     */
    public List<Set<String>> abstractShapes(String name) {
        List<Set<String>> out = new ArrayList<>();
        for (int i = 0; i < jars.size(); i++) {
            ClassNode cn = load(i, name);
            if (cn == null || (cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) == 0) {
                continue;
            }
            final int release = i;
            out.add(abstractShapes(name, c -> load(release, c)));
        }
        return out;
    }

    /** the abstract methods of an interface hierarchy, classes looked up through {@code lookup} */
    public static Set<String> abstractShapes(String iface, java.util.function.Function<String, ClassNode> lookup) {
        Map<String, Boolean> decided = new java.util.LinkedHashMap<>();
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(List.of(iface));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            ClassNode cn = lookup.apply(c);
            if (cn == null) {
                continue;
            }
            for (MethodNode m : cn.methods) {
                if ((m.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0 || m.name.startsWith("<")
                        || (m.access & org.objectweb.asm.Opcodes.ACC_PRIVATE) != 0) {
                    continue;
                }
                // breadth-first from the interface itself: the most specific declaration decides
                decided.putIfAbsent(m.name + m.desc, (m.access & org.objectweb.asm.Opcodes.ACC_ABSTRACT) != 0);
            }
            if (cn.interfaces != null) {
                todo.addAll(cn.interfaces);
            }
        }
        Set<String> out = new java.util.LinkedHashSet<>();
        for (var e : decided.entrySet()) {
            if (e.getValue() && !OBJECT_PUBLIC.contains(e.getKey())) {
                out.add(e.getKey());
            }
        }
        return out;
    }

    private static final Set<String> OBJECT_PUBLIC = Set.of(
        "equals(Ljava/lang/Object;)Z", "hashCode()I", "toString()Ljava/lang/String;");

    /** access flags of the original method, resolved up the source hierarchy; null when no stub has it */
    public Integer methodAccess(String owner, String name, String desc) {
        for (int i = 0; i < jars.size(); i++) {
            Integer a = member(i, owner, name, desc, true, new HashSet<>());
            if (a != null) {
                return a;
            }
        }
        return null;
    }

    /** access flags of the original field, resolved up the source hierarchy; null when no stub has it */
    public Integer fieldAccess(String owner, String name, String desc) {
        for (int i = 0; i < jars.size(); i++) {
            Integer a = member(i, owner, name, desc, false, new HashSet<>());
            if (a != null) {
                return a;
            }
        }
        return null;
    }

    private Integer member(int i, String c, String name, String desc, boolean method, Set<String> seen) {
        if (c == null || !seen.add(c)) {
            return null;
        }
        ClassNode cn = load(i, c);
        if (cn == null) {
            return null;
        }
        if (method) {
            for (MethodNode m : cn.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)) {
                    return m.access;
                }
            }
        } else {
            for (FieldNode f : cn.fields) {
                if (f.name.equals(name) && f.desc.equals(desc)) {
                    return f.access;
                }
            }
        }
        Integer a = member(i, cn.superName, name, desc, method, seen);
        for (int k = 0; a == null && cn.interfaces != null && k < cn.interfaces.size(); k++) {
            a = member(i, cn.interfaces.get(k), name, desc, method, seen);
        }
        return a;
    }

    private ClassNode load(int i, String name) {
        return cache.get(i).computeIfAbsent(name, n -> {
            ZipFile z = jars.get(i);
            var e = z.getEntry(n + ".class");
            if (e == null) {
                return Optional.empty();
            }
            ClassNode cn = new ClassNode();
            try (var in = z.getInputStream(e)) {
                new ClassReader(in).accept(cn, ClassReader.SKIP_CODE);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
            return Optional.of(cn);
        }).orElse(null);
    }

    @Override
    public void close() throws IOException {
        for (ZipFile z : jars) {
            z.close();
        }
    }
}
