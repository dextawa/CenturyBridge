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
