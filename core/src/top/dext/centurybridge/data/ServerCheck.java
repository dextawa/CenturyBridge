package top.dext.centurybridge.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * A dedicated server loads every bridge class that is not client-only, and the
 * verifier resolves the classes a method's bytecode needs for its type checks:
 * one blaze3d type reachable from the server-loaded Statics was a
 * NoClassDefFoundError that took the whole 26.2 server down at the first static
 * redirect. The client classpath link-check uses cannot see this.
 *
 * Rule: a plain class of the bridge jar is client-only when it carries
 * {@code @Environment(EnvType.CLIENT)} (Fabric's own marker); every other class
 * may only name classes the dedicated server has -- the server jar, its
 * libraries, the fabric-api modules that do not declare {@code "environment":
 * "client"}, the JDK, and the bridge's own server-side classes. Generic
 * signatures, annotations and debug tables are not loaded and are ignored;
 * descriptors, instructions, handles, stack-map frames and catch types are --
 * except the owner of a static call or static field access, which the JVM
 * resolves only when the instruction first executes (a server-side class may
 * call into a client-only helper from a branch a server never takes; it may not
 * hold, pass, cast to or receive a client-only type).
 *
 * A mixin's side is derived the same way, its @Mixin targets counted as
 * references: "client" exactly when it names something the server lacks. The
 * other direction matters as much -- a server-capable bridge parked in "client"
 * never applies on a dedicated server, so the call site its cover left alone
 * throws NoSuchMethodError there while every client test passes. A deliberate
 * "server" entry stays where it is.
 *
 *   server-check <compiledShimDir> <mixins.json> <serverJar> <serverLibraries> <fabricApiJar> [--fix]
 *
 * --fix rewrites the mixin config: every compiled mixin listed, on its side.
 */
public final class ServerCheck {

    private static final String ENVIRONMENT = "Lnet/fabricmc/api/Environment;";

    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";

    private final Set<String> server = new HashSet<>();
    private final Map<String, ClassNode> bridge = new TreeMap<>();
    private final Set<String> clientOnly = new HashSet<>();
    /** mixin class -> its side as listed ("mixins" / "client" / "server"); absent when unlisted */
    private final Map<String, String> listedSide = new TreeMap<>();
    private final Set<String> mixins = new TreeSet<>();
    private String pkg;

    public static void run(Path classes, Path mixinConfig, Path serverJar, Path serverLibs, Path fabricApi)
            throws IOException {
        run(classes, mixinConfig, serverJar, serverLibs, fabricApi, false);
    }

    public static void run(Path classes, Path mixinConfig, Path serverJar, Path serverLibs, Path fabricApi,
                           boolean fix) throws IOException {
        ServerCheck c = new ServerCheck();
        c.indexJar(Files.readAllBytes(serverJar), false);
        if (Files.isDirectory(serverLibs)) {
            try (Stream<Path> s = Files.walk(serverLibs)) {
                for (Path p : (Iterable<Path>) s.filter(f -> f.toString().endsWith(".jar"))::iterator) {
                    c.indexJar(Files.readAllBytes(p), false);
                }
            }
        }
        c.indexJar(Files.readAllBytes(fabricApi), true);
        c.loadBridge(classes, mixinConfig);
        Map<String, String> sides = c.deriveMixinSides();
        if (fix) {
            c.writeConfig(mixinConfig, sides);
        }
        List<String> problems = new ArrayList<>();
        int agree = 0;
        for (String m : c.mixins) {
            String listed = c.listedSide.get(m);
            String rule = sides.get(m);
            String simple = m.substring(c.pkg.length()).replace('/', '.');
            if (listed == null) {
                problems.add(simple + ": compiled @Mixin not listed in the config (never applied)");
            } else if (listed.equals(rule) || (listed.equals("server") && rule.equals("mixins"))) {
                agree++;
            } else if (rule.equals("client")) {
                problems.add(simple + ": listed \"" + listed + "\" but names client-only "
                    + String.join(", ", c.clientRefs(c.bridge.get(m))));
            } else {
                problems.add(simple + ": listed \"client\" but needs nothing client-only -- on a dedicated "
                    + "server it never applies, and the call sites it serves fail there");
            }
        }
        problems.addAll(c.check());
        System.out.println("server-check: " + agree + " mixins on their side, "
            + c.bridge.keySet().stream().filter(n -> !c.mixins.contains(n) && !c.clientOnly.contains(n)).count()
            + " server-side plain classes, "
            + problems.size() + " problems" + (fix ? " (after --fix)" : ""));
        for (String p : problems) {
            System.out.println("    " + p);
        }
        if (!problems.isEmpty()) {
            System.exit(1);
        }
    }

    /**
     * client exactly when a mixin names something the server lacks; settled as a
     * fixpoint because one mixin may name another (an accessor interface) whose
     * side is derived too
     */
    private Map<String, String> deriveMixinSides() {
        Map<String, String> sides = new TreeMap<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String m : mixins) {
                String side = clientRefs(bridge.get(m)).isEmpty() ? "mixins" : "client";
                if (!side.equals(sides.put(m, side))) {
                    changed = true;
                }
                if (side.equals("client") ? clientOnly.add(m) : clientOnly.remove(m)) {
                    changed = true;
                }
            }
        }
        return sides;
    }

    /** every name the class needs that a dedicated server does not have */
    private Set<String> clientRefs(ClassNode cn) {
        Set<String> all = new TreeSet<>();
        if (cn.superName != null) {
            all.add(cn.superName);
        }
        all.addAll(cn.interfaces);
        all.addAll(mixinTargets(cn));
        for (FieldNode f : cn.fields) {
            types(Type.getType(f.desc), all);
        }
        for (MethodNode m : cn.methods) {
            all.addAll(refs(m));
        }
        Set<String> bad = new TreeSet<>();
        for (String r : all) {
            if (!r.equals(cn.name) && !available(r)) {
                bad.add(r);
            }
        }
        return bad;
    }

    private static Set<String> mixinTargets(ClassNode cn) {
        Set<String> out = new TreeSet<>();
        for (List<AnnotationNode> anns : java.util.Arrays.asList(cn.visibleAnnotations, cn.invisibleAnnotations)) {
            if (anns == null) {
                continue;
            }
            for (AnnotationNode a : anns) {
                if (!a.desc.equals(MIXIN) || a.values == null) {
                    continue;
                }
                for (int i = 0; i + 1 < a.values.size(); i += 2) {
                    if (a.values.get(i + 1) instanceof List<?> l) {
                        for (Object o : l) {
                            if (o instanceof Type t) {
                                out.add(t.getInternalName());
                            } else if (o instanceof String str) {
                                out.add(str.replace('.', '/'));
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    private void writeConfig(Path mixinConfig, Map<String, String> sides) throws IOException {
        var cfg = new com.google.gson.Gson().fromJson(Files.readString(mixinConfig), com.google.gson.JsonObject.class);
        Map<String, Set<String>> lists = new TreeMap<>();
        for (String side : new String[] {"mixins", "client", "server"}) {
            lists.put(side, new TreeSet<>());
        }
        for (String m : mixins) {
            String side = "server".equals(listedSide.get(m)) && sides.get(m).equals("mixins") ? "server" : sides.get(m);
            lists.get(side).add(m.substring(pkg.length()).replace('/', '.'));
            listedSide.put(m, side);
        }
        for (var e : lists.entrySet()) {
            if (e.getValue().isEmpty() && !cfg.has(e.getKey())) {
                continue;
            }
            var arr = new com.google.gson.JsonArray();
            e.getValue().forEach(arr::add);
            cfg.add(e.getKey(), arr);
        }
        String json = new com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(cfg);
        Files.writeString(mixinConfig, json + "\n");
    }

    /** class names of a jar; nested jars (bundler, jar-in-jar) recurse, client-only fabric modules skipped */
    private void indexJar(byte[] jar, boolean modules) throws IOException {
        List<byte[]> nested = new ArrayList<>();
        Set<String> names = new HashSet<>();
        boolean client = false;
        try (ZipInputStream z = new ZipInputStream(new ByteArrayInputStream(jar))) {
            ZipEntry e;
            while ((e = z.getNextEntry()) != null) {
                String n = e.getName();
                if (n.endsWith(".class")) {
                    names.add(n.substring(0, n.length() - 6));
                } else if (n.endsWith(".jar")) {
                    nested.add(z.readAllBytes());
                } else if (modules && n.equals("fabric.mod.json")) {
                    var fmj = new com.google.gson.Gson().fromJson(new String(z.readAllBytes(),
                        java.nio.charset.StandardCharsets.UTF_8), com.google.gson.JsonObject.class);
                    client = fmj.has("environment") && fmj.get("environment").getAsString().equals("client");
                }
            }
        }
        if (!client) {
            server.addAll(names);
        }
        for (byte[] n : nested) {
            indexJar(n, modules);
        }
    }

    private void loadBridge(Path classes, Path mixinConfig) throws IOException {
        var cfg = new com.google.gson.Gson().fromJson(Files.readString(mixinConfig), com.google.gson.JsonObject.class);
        pkg = cfg.get("package").getAsString().replace('.', '/') + "/";
        for (String side : new String[] {"mixins", "client", "server"}) {
            if (cfg.has(side)) {
                for (var el : cfg.getAsJsonArray(side)) {
                    listedSide.put(pkg + el.getAsString().replace('.', '/'), side);
                }
            }
        }
        try (Stream<Path> s = Files.walk(classes)) {
            for (Path p : (Iterable<Path>) s.filter(f -> f.toString().endsWith(".class"))::iterator) {
                ClassNode cn = new ClassNode();
                new ClassReader(Files.readAllBytes(p)).accept(cn, 0);
                bridge.put(cn.name, cn);
                if (!mixinTargets(cn).isEmpty() && cn.name.startsWith(pkg)) {
                    mixins.add(cn.name);
                } else if (environmentClient(cn.visibleAnnotations) || environmentClient(cn.invisibleAnnotations)) {
                    clientOnly.add(cn.name);
                }
            }
        }
        // a nested class of a client-only class is client-only too
        for (String n : bridge.keySet()) {
            int d = n.indexOf('$');
            if (d > 0 && clientOnly.contains(n.substring(0, d))) {
                clientOnly.add(n);
            }
        }
    }

    private static boolean environmentClient(List<AnnotationNode> anns) {
        if (anns == null) {
            return false;
        }
        for (AnnotationNode a : anns) {
            if (a.desc.equals(ENVIRONMENT) && a.values != null) {
                for (Object v : a.values) {
                    if (v instanceof String[] en && en.length == 2 && en[1].equals("CLIENT")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** plain (non-mixin) classes that are not client-only must not name anything client-only */
    private List<String> check() {
        List<String> out = new ArrayList<>();
        for (ClassNode cn : bridge.values()) {
            if (clientOnly.contains(cn.name) || mixins.contains(cn.name)) {
                continue;
            }
            Set<String> classLevel = new TreeSet<>();
            if (cn.superName != null) {
                classLevel.add(cn.superName);
            }
            classLevel.addAll(cn.interfaces);
            report(out, cn.name, classLevel);
            for (FieldNode f : cn.fields) {
                report(out, cn.name + "." + f.name, types(Type.getType(f.desc), new TreeSet<>()));
            }
            for (MethodNode m : cn.methods) {
                report(out, cn.name + "." + m.name + m.desc, refs(m));
            }
        }
        return out;
    }

    private void report(List<String> out, String where, Set<String> refs) {
        Set<String> bad = new TreeSet<>();
        for (String r : refs) {
            if (!available(r)) {
                bad.add(r);
            }
        }
        if (!bad.isEmpty()) {
            out.add(where + " -> " + String.join(", ", bad));
        }
    }

    private boolean available(String name) {
        if (name.startsWith("java/") || name.startsWith("javax/") || name.startsWith("jdk/") || name.startsWith("sun/")) {
            return true;
        }
        if (bridge.containsKey(name)) {
            return !clientOnly.contains(name);
        }
        return server.contains(name);
    }

    private static Set<String> refs(MethodNode m) {
        Set<String> out = new TreeSet<>();
        types(Type.getMethodType(m.desc), out);
        if (m.tryCatchBlocks != null) {
            for (TryCatchBlockNode t : m.tryCatchBlocks) {
                if (t.type != null) {
                    out.add(t.type);
                }
            }
        }
        if (m.instructions == null) {
            return out;
        }
        for (AbstractInsnNode in : m.instructions) {
            if (in instanceof MethodInsnNode mi) {
                // a static call's owner resolves lazily, at first execution: the
                // "call into a client-only helper from a branch a server never takes"
                // pattern is sound. A receiver's type is checked by the verifier.
                if (mi.getOpcode() != org.objectweb.asm.Opcodes.INVOKESTATIC) {
                    owner(mi.owner, out);
                }
                types(Type.getMethodType(mi.desc), out);
            } else if (in instanceof FieldInsnNode fi) {
                if (fi.getOpcode() != org.objectweb.asm.Opcodes.GETSTATIC && fi.getOpcode() != org.objectweb.asm.Opcodes.PUTSTATIC) {
                    owner(fi.owner, out);
                }
                types(Type.getType(fi.desc), out);
            } else if (in instanceof TypeInsnNode ti) {
                owner(ti.desc, out);
            } else if (in instanceof MultiANewArrayInsnNode ma) {
                types(Type.getType(ma.desc), out);
            } else if (in instanceof LdcInsnNode ldc) {
                if (ldc.cst instanceof Type t) {
                    types(t, out);
                } else if (ldc.cst instanceof Handle h) {
                    handle(h, out);
                }
            } else if (in instanceof InvokeDynamicInsnNode indy) {
                types(Type.getMethodType(indy.desc), out);
                handle(indy.bsm, out);
                for (Object a : indy.bsmArgs) {
                    if (a instanceof Handle h) {
                        handle(h, out);
                    } else if (a instanceof Type t) {
                        types(t, out);
                    }
                }
            } else if (in instanceof FrameNode f) {
                frame(f.local, out);
                frame(f.stack, out);
            }
        }
        return out;
    }

    private static void frame(List<Object> slots, Set<String> out) {
        if (slots == null) {
            return;
        }
        for (Object o : slots) {
            if (o instanceof String s) {
                owner(s, out);
            }
        }
    }

    private static void handle(Handle h, Set<String> out) {
        owner(h.getOwner(), out);
        types(h.getTag() <= org.objectweb.asm.Opcodes.H_PUTSTATIC ? Type.getType(h.getDesc())
                                                                 : Type.getMethodType(h.getDesc()), out);
    }

    private static void owner(String internalName, Set<String> out) {
        if (internalName.startsWith("[")) {
            types(Type.getType(internalName), out);
        } else {
            out.add(internalName);
        }
    }

    private static Set<String> types(Type t, Set<String> out) {
        switch (t.getSort()) {
            case Type.METHOD -> {
                for (Type a : t.getArgumentTypes()) {
                    types(a, out);
                }
                types(t.getReturnType(), out);
            }
            case Type.ARRAY -> types(t.getElementType(), out);
            case Type.OBJECT -> out.add(t.getInternalName());
            default -> {
            }
        }
        return out;
    }

    private ServerCheck() {
    }
}
