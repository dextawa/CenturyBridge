package top.dext.centurybridge.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * The whole API surface of the source release as one synthetic mod: the diff-driven kill
 * list. A segment diff records which symbols died or changed; what breaks a mod is how it
 * touches them -- a call, a field read or write, a NEW, a subclass and its super(...), an
 * override, a super call, an interface implementation, a lambda. The probe touches every
 * public and protected member of every API class once in every way a mod can, with nothing
 * chosen by what some corpus happens to use. Converted like any mod and link-checked on a
 * target, each finding is one entry of the full diff that the bridges do not cover, and a
 * clean probe site is one that does.
 *
 *   cbprobe/u/X   one static method per use of a public member of X (calls with null or zero
 *                 arguments, field gets and non-final puts, NEW per constructor), and one
 *                 lambda per functional interface
 *   cbprobe/s/X   for an extendable class X: a subclass with one constructor per accessible
 *                 super constructor, an override of every overridable method X declares (a
 *                 super call inside when X's is concrete), implementations of the abstract
 *                 methods it inherits, and in-subclass uses of X's protected members
 *   cbprobe/i/X   for an interface X: an implementation of all its abstract methods and an
 *                 override (with super call) of each default method X declares
 *   cbprobe/c/X   for an extendable class or interface X: an abstract class under X that overrides
 *                 nothing, calling X's public and protected methods through itself (this.m(..) in a
 *                 mod subclass that does not override m names the mod class)
 *
 * The probe is concrete only where 1.20.1 makes that possible (every inherited abstract
 * method resolvable and implementable from another package); otherwise it is declared
 * abstract and overrides are still probed. Nothing in it is ever executed: link-check
 * resolves it statically.
 *
 *   api-probe <sourceStub.jar> <out.jar> [<libraries jar or dir>...]
 * also writes <out.jar>.index.tsv: probeClass, probeMember, usage, owner, name, desc.
 */
public final class ApiProbe {

    private final Map<String, ClassNode> api = new LinkedHashMap<>();
    private final Map<String, ClassNode> libs = new HashMap<>();
    private final Map<String, Integer> innerAccess = new HashMap<>();
    private final List<String[]> index = new ArrayList<>();
    private int sites;

    public static void run(String[] args) throws IOException {
        ApiProbe p = new ApiProbe();
        p.loadApi(Path.of(args[1]));
        for (int i = 3; i < args.length; i++) {
            p.loadLibs(Path.of(args[i]));
        }
        p.write(Path.of(args[2]));
    }

    // ------------------------------------------------------------------ loading

    private void loadApi(Path jar) throws IOException {
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (ZipEntry e : java.util.Collections.list(z.entries())) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                try (InputStream in = z.getInputStream(e)) {
                    ClassNode cn = new ClassNode();
                    new ClassReader(in).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    api.put(cn.name, cn);
                    for (InnerClassNode ic : cn.innerClasses) {
                        innerAccess.putIfAbsent(ic.name, ic.access);
                    }
                }
            }
        }
    }

    private void loadLibs(Path p) throws IOException {
        if (Files.isDirectory(p)) {
            try (Stream<Path> s = Files.walk(p)) {
                for (Path j : s.filter(f -> f.toString().endsWith(".jar")).toList()) {
                    loadLibJar(j);
                }
            }
        } else {
            loadLibJar(p);
        }
    }

    private void loadLibJar(Path jar) throws IOException {
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (ZipEntry e : java.util.Collections.list(z.entries())) {
                String n = e.getName();
                if (!n.endsWith(".class") || n.startsWith("META-INF/")) {
                    continue;
                }
                try (InputStream in = z.getInputStream(e)) {
                    ClassNode cn = new ClassNode();
                    new ClassReader(in).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    libs.putIfAbsent(cn.name, cn);
                }
            }
        } catch (IOException ex) {
            // a natives-only or broken jar contributes nothing to the hierarchy
        }
    }

    /** a class of the API, a library, or the JDK; null when unknown */
    private ClassNode node(String name) {
        ClassNode cn = api.get(name);
        if (cn == null) {
            cn = libs.get(name);
        }
        if (cn == null && (name.startsWith("java/") || name.startsWith("javax/") || name.startsWith("jdk/"))) {
            try (InputStream in = ClassLoader.getSystemResourceAsStream(name + ".class")) {
                if (in != null) {
                    cn = new ClassNode();
                    new ClassReader(in).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    libs.put(name, cn);
                }
            } catch (IOException ignored) {
                // unknown
            }
        }
        return cn;
    }

    // ------------------------------------------------------------------ what a mod can reach

    /** the class's own access as source code sees it (InnerClasses carries protected/private for nested classes) */
    private int classAccess(ClassNode cn) {
        Integer inner = innerAccess.get(cn.name);
        return inner != null ? inner : cn.access;
    }

    /** a mod in another package can name the class: public, and every enclosing class public too */
    private boolean visible(ClassNode cn) {
        if ((classAccess(cn) & Opcodes.ACC_PUBLIC) == 0 || (cn.access & Opcodes.ACC_SYNTHETIC) != 0) {
            return false;
        }
        int dollar = cn.name.lastIndexOf('$');
        if (dollar > 0) {
            String tail = cn.name.substring(dollar + 1);
            if (!tail.isEmpty() && Character.isDigit(tail.charAt(0))) {
                return false; // anonymous / local
            }
            ClassNode outer = api.get(cn.name.substring(0, dollar));
            return outer == null || visible(outer);
        }
        return true;
    }

    private static boolean is(int access, int flag) {
        return (access & flag) != 0;
    }

    private static boolean memberReachable(int access) {
        return is(access, Opcodes.ACC_PUBLIC) || is(access, Opcodes.ACC_PROTECTED);
    }

    private static boolean skipMethod(MethodNode m) {
        return m.name.equals("<clinit>") || is(m.access, Opcodes.ACC_SYNTHETIC) || is(m.access, Opcodes.ACC_BRIDGE);
    }

    private boolean extendable(ClassNode cn) {
        return !is(cn.access, Opcodes.ACC_FINAL) && !is(cn.access, Opcodes.ACC_INTERFACE)
            && !is(cn.access, Opcodes.ACC_ENUM) && !is(cn.access, Opcodes.ACC_ANNOTATION)
            && !"java/lang/Record".equals(cn.superName)
            && (cn.permittedSubclasses == null || cn.permittedSubclasses.isEmpty())
            && cn.methods.stream().anyMatch(m -> m.name.equals("<init>") && memberReachable(m.access));
    }

    // ------------------------------------------------------------------ generation

    private void write(Path out) throws IOException {
        Files.createDirectories(out.toAbsolutePath().getParent());
        int classes = 0;
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(out))) {
            zip.putNextEntry(new ZipEntry("fabric.mod.json"));
            zip.write(("{\"schemaVersion\":1,\"id\":\"cb_apiprobe\",\"version\":\"1\",\"name\":\"CenturyBridge API probe\","
                + "\"environment\":\"*\",\"depends\":{\"minecraft\":\"1.20.1\"}}").getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            for (ClassNode cn : api.values()) {
                if (!visible(cn)) {
                    continue;
                }
                byte[] use = useClass(cn);
                if (use != null) {
                    put(zip, "cbprobe/u/" + mangle(cn.name), use);
                    classes++;
                }
                byte[] caller = null;
                if (is(cn.access, Opcodes.ACC_INTERFACE)) {
                    if (!is(cn.access, Opcodes.ACC_ANNOTATION) && (cn.permittedSubclasses == null || cn.permittedSubclasses.isEmpty())) {
                        put(zip, "cbprobe/i/" + mangle(cn.name), implClass(cn));
                        classes++;
                        caller = callerClass(cn, true);
                    }
                } else if (extendable(cn)) {
                    put(zip, "cbprobe/s/" + mangle(cn.name), subClass(cn));
                    classes++;
                    caller = callerClass(cn, false);
                }
                if (caller != null) {
                    put(zip, "cbprobe/c/" + mangle(cn.name), caller);
                    classes++;
                }
            }
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(Path.of(out + ".index.tsv"), StandardCharsets.UTF_8))) {
            w.println("probeClass\tprobeMember\tusage\towner\tname\tdesc");
            for (String[] r : index) {
                w.println(String.join("\t", r));
            }
        }
        System.out.println("api-probe: " + classes + " probe classes, " + sites + " probe sites over "
            + api.values().stream().filter(this::visible).count() + " visible API classes -> " + out);
    }

    private static void put(ZipOutputStream zip, String name, byte[] bytes) throws IOException {
        zip.putNextEntry(new ZipEntry(name + ".class"));
        zip.write(bytes);
        zip.closeEntry();
    }

    private static String mangle(String internal) {
        return internal.replace('/', '_').replace('$', '_');
    }

    /** the site id the next record() takes, as a constant the conversion keeps: link-check findings name the
     *  converted method, whose name and descriptor the conversion may have changed; the marker maps it back */
    private void marker(MethodVisitor mv) {
        mv.visitLdcInsn("cbprobe#" + index.size());
        mv.visitInsn(Opcodes.POP);
    }

    private void record(String probeClass, String probeMember, String usage, String owner, String name, String desc) {
        index.add(new String[] {probeClass, probeMember, usage, owner, name, desc});
        sites++;
    }

    /** cbprobe/u/X: every public member of X used from outside, plus a lambda if X is functional */
    private byte[] useClass(ClassNode cn) {
        String self = "cbprobe/u/" + mangle(cn.name);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, self, null, "java/lang/Object", null);
        boolean itf = is(cn.access, Opcodes.ACC_INTERFACE);
        boolean instantiable = !itf && !is(cn.access, Opcodes.ACC_ABSTRACT);
        int n = 0;
        for (MethodNode m : cn.methods) {
            if (skipMethod(m) || !is(m.access, Opcodes.ACC_PUBLIC) || (m.name.equals("<init>") && !instantiable)) {
                continue; // an abstract class's constructors are reached through super(...) only (cbprobe/s)
            }
            String probe = "u" + (n++);
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, probe, "()V", null, null);
            mv.visitCode();
            marker(mv);
            Type[] params = Type.getArgumentTypes(m.desc);
            String usage;
            if (m.name.equals("<init>")) {
                mv.visitTypeInsn(Opcodes.NEW, cn.name);
                mv.visitInsn(Opcodes.DUP);
                pushDefaults(mv, params);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, cn.name, "<init>", m.desc, false);
                mv.visitInsn(Opcodes.POP);
                usage = "new";
            } else if (is(m.access, Opcodes.ACC_STATIC)) {
                pushDefaults(mv, params);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, cn.name, m.name, m.desc, itf);
                popResult(mv, Type.getReturnType(m.desc));
                usage = "call-static";
            } else {
                mv.visitInsn(Opcodes.ACONST_NULL);
                pushDefaults(mv, params);
                mv.visitMethodInsn(itf ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL, cn.name, m.name, m.desc, itf);
                popResult(mv, Type.getReturnType(m.desc));
                usage = "call";
            }
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, probe + "()V", usage, cn.name, m.name, m.desc);
        }
        for (FieldNode f : cn.fields) {
            if (!is(f.access, Opcodes.ACC_PUBLIC) || is(f.access, Opcodes.ACC_SYNTHETIC)) {
                continue;
            }
            boolean st = is(f.access, Opcodes.ACC_STATIC);
            String probe = "u" + (n++);
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, probe, "()V", null, null);
            mv.visitCode();
            marker(mv);
            if (!st) {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }
            mv.visitFieldInsn(st ? Opcodes.GETSTATIC : Opcodes.GETFIELD, cn.name, f.name, f.desc);
            popResult(mv, Type.getType(f.desc));
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, probe + "()V", st ? "get-static" : "get", cn.name, f.name, f.desc);
            if (!is(f.access, Opcodes.ACC_FINAL)) {
                probe = "u" + (n++);
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, probe, "()V", null, null);
                mv.visitCode();
                marker(mv);
                if (!st) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                }
                pushDefault(mv, Type.getType(f.desc));
                mv.visitFieldInsn(st ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, cn.name, f.name, f.desc);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
                record(self, probe + "()V", st ? "put-static" : "put", cn.name, f.name, f.desc);
            }
        }
        MethodNode sam = itf ? singleAbstract(cn) : null;
        if (sam != null) {
            // the lambda's body: a static method of this class with the SAM's erased shape
            String implName = "lambda$" + n;
            MethodVisitor impl = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, implName, sam.desc, null, null);
            impl.visitCode();
            returnDefault(impl, Type.getReturnType(sam.desc));
            impl.visitMaxs(0, 0);
            impl.visitEnd();
            String probe = "u" + (n++);
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, probe, "()V", null, null);
            mv.visitCode();
            marker(mv);
            Handle meta = new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;"
                    + "Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false);
            mv.visitInvokeDynamicInsn(sam.name, "()L" + cn.name + ";", meta,
                Type.getMethodType(sam.desc), new Handle(Opcodes.H_INVOKESTATIC, self, implName, sam.desc, false), Type.getMethodType(sam.desc));
            mv.visitInsn(Opcodes.POP);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, probe + "()V", "lambda", cn.name, sam.name, sam.desc);
        }
        if (n == 0) {
            return null;
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    /** cbprobe/s/X extends X */
    private byte[] subClass(ClassNode cn) {
        String self = "cbprobe/s/" + mangle(cn.name);
        Map<String, MethodNode> toImplement = new LinkedHashMap<>();
        boolean concrete = collectAbstract(cn, toImplement);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | (concrete ? 0 : Opcodes.ACC_ABSTRACT), self, null, cn.name, null);
        record(self, "", "extend", cn.name, "", "");
        Set<String> declared = new LinkedHashSet<>();
        for (MethodNode m : cn.methods) {
            if (skipMethod(m) || !memberReachable(m.access)) {
                continue;
            }
            Type[] params = Type.getArgumentTypes(m.desc);
            if (m.name.equals("<init>")) {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", m.desc, null, null);
                mv.visitCode();
                marker(mv);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                loadArgs(mv, params);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, cn.name, "<init>", m.desc, false);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
                record(self, "<init>" + m.desc, "super-ctor", cn.name, "<init>", m.desc);
                continue;
            }
            if (is(m.access, Opcodes.ACC_STATIC) || is(m.access, Opcodes.ACC_FINAL)) {
                continue;
            }
            declared.add(m.name + m.desc);
            boolean abs = is(m.access, Opcodes.ACC_ABSTRACT);
            MethodVisitor mv = cw.visitMethod(m.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED), m.name, m.desc, null, null);
            mv.visitCode();
            marker(mv);
            if (abs) {
                returnDefault(mv, Type.getReturnType(m.desc));
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                loadArgs(mv, params);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, cn.name, m.name, m.desc, false);
                returnValue(mv, Type.getReturnType(m.desc));
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, m.name + m.desc, abs ? "implement" : "override+super", cn.name, m.name, m.desc);
        }
        for (Map.Entry<String, MethodNode> e : toImplement.entrySet()) {
            MethodNode m = e.getValue();
            if (declared.contains(m.name + m.desc) || !memberReachable(m.access)) {
                continue; // package-private abstract: no other package overrides it (the probe is abstract then)
            }
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, m.name, m.desc, null, null);
            mv.visitCode();
            marker(mv);
            returnDefault(mv, Type.getReturnType(m.desc));
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, m.name + m.desc, "implement-inherited", e.getKey().substring(0, e.getKey().indexOf(' ')), m.name, m.desc);
        }
        // protected members, used the only way a mod can: from inside a subclass. A protected static
        // here, qualified by X (X.m(..)); protected instance methods from cbprobe/c/X, which overrides
        // nothing -- here this.m(..) would reach this class's own override. Fields on itself: this
        // class declares none, so the reference resolves to X's
        int n = 0;
        for (MethodNode m : cn.methods) {
            if (skipMethod(m) || !is(m.access, Opcodes.ACC_PROTECTED) || !is(m.access, Opcodes.ACC_STATIC)) {
                continue;
            }
            String probe = "p" + (n++);
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, probe, "()V", null, null);
            mv.visitCode();
            marker(mv);
            pushDefaults(mv, Type.getArgumentTypes(m.desc));
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, cn.name, m.name, m.desc, false);
            popResult(mv, Type.getReturnType(m.desc));
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, probe + "()V", "call-static-protected", cn.name, m.name, m.desc);
        }
        for (FieldNode f : cn.fields) {
            if (!is(f.access, Opcodes.ACC_PROTECTED) || is(f.access, Opcodes.ACC_SYNTHETIC)) {
                continue;
            }
            boolean st = is(f.access, Opcodes.ACC_STATIC);
            String probe = "p" + (n++);
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, probe, "()V", null, null);
            mv.visitCode();
            marker(mv);
            if (!st) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
            }
            mv.visitFieldInsn(st ? Opcodes.GETSTATIC : Opcodes.GETFIELD, st ? cn.name : self, f.name, f.desc);
            popResult(mv, Type.getType(f.desc));
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, probe + "()V", st ? "get-static-protected" : "get-protected", cn.name, f.name, f.desc);
            if (!is(f.access, Opcodes.ACC_FINAL)) {
                probe = "p" + (n++);
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, probe, "()V", null, null);
                mv.visitCode();
                marker(mv);
                if (!st) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                }
                pushDefault(mv, Type.getType(f.desc));
                mv.visitFieldInsn(st ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, st ? cn.name : self, f.name, f.desc);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
                record(self, probe + "()V", st ? "put-static-protected" : "put-protected", cn.name, f.name, f.desc);
            }
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * cbprobe/c/X: an abstract class under X that overrides nothing, calling X's methods through itself
     * the way a mod subclass that does not override them does -- javac names the mod class in an
     * unqualified call (JLS 13.1), and the JVM resolves it up to X. cbprobe/s/X overrides every
     * overridable method, so its own this.m(..) reaches its own override and could never see a method X
     * changed: a mod dispenser's dispenseFrom calling this.getDispenseMethod(stack), which 1.20.5
     * reshaped, was measured nowhere. For an interface X the class implements it, and calls X's
     * instance methods (default or abstract) through itself.
     *
     * Usages: call-protected for a protected instance method, call-inherited for a public one,
     * call-static-inherited for a static one (the call / call-static / call-static-protected rows of
     * the same members name X itself).
     */
    private byte[] callerClass(ClassNode cn, boolean itf) {
        String self = "cbprobe/c/" + mangle(cn.name);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_ABSTRACT, self, null,
            itf ? "java/lang/Object" : cn.name, itf ? new String[] {cn.name} : null);
        int n = 0;
        for (MethodNode m : cn.methods) {
            if (skipMethod(m) || m.name.startsWith("<") || !memberReachable(m.access)
                    || !is(m.access, Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) {
                continue;
            }
            boolean st = is(m.access, Opcodes.ACC_STATIC);
            if (itf && (st || !is(m.access, Opcodes.ACC_PUBLIC))) {
                continue; // an interface's static is not inherited; its methods are public
            }
            String probe = "c" + (n++);
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, probe, "()V", null, null);
            mv.visitCode();
            marker(mv);
            if (!st) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
            }
            pushDefaults(mv, Type.getArgumentTypes(m.desc));
            mv.visitMethodInsn(st ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL, self, m.name, m.desc, false);
            popResult(mv, Type.getReturnType(m.desc));
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            boolean prot = is(m.access, Opcodes.ACC_PROTECTED);
            record(self, probe + "()V", st ? "call-static-inherited" : prot ? "call-protected" : "call-inherited", cn.name, m.name, m.desc);
        }
        if (n == 0) {
            return null;
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    /** cbprobe/i/X implements X */
    private byte[] implClass(ClassNode cn) {
        String self = "cbprobe/i/" + mangle(cn.name);
        Map<String, MethodNode> toImplement = new LinkedHashMap<>();
        boolean concrete = collectAbstract(cn, toImplement);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | (concrete ? 0 : Opcodes.ACC_ABSTRACT), self, null,
            "java/lang/Object", new String[] {cn.name});
        MethodVisitor ctor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(Opcodes.ALOAD, 0);
        ctor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        ctor.visitInsn(Opcodes.RETURN);
        ctor.visitMaxs(0, 0);
        ctor.visitEnd();
        record(self, "", "implement-interface", cn.name, "", "");
        Set<String> done = new LinkedHashSet<>();
        for (MethodNode m : cn.methods) {
            if (skipMethod(m) || is(m.access, Opcodes.ACC_STATIC) || !is(m.access, Opcodes.ACC_PUBLIC) || m.name.equals("<init>")) {
                continue;
            }
            done.add(m.name + m.desc);
            boolean abs = is(m.access, Opcodes.ACC_ABSTRACT);
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, m.name, m.desc, null, null);
            mv.visitCode();
            marker(mv);
            if (abs) {
                returnDefault(mv, Type.getReturnType(m.desc));
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                loadArgs(mv, Type.getArgumentTypes(m.desc));
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, cn.name, m.name, m.desc, true);
                returnValue(mv, Type.getReturnType(m.desc));
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, m.name + m.desc, abs ? "implement" : "override+super", cn.name, m.name, m.desc);
        }
        for (Map.Entry<String, MethodNode> e : toImplement.entrySet()) {
            MethodNode m = e.getValue();
            if (done.contains(m.name + m.desc) || !memberReachable(m.access)) {
                continue;
            }
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, m.name, m.desc, null, null);
            mv.visitCode();
            marker(mv);
            returnDefault(mv, Type.getReturnType(m.desc));
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            record(self, m.name + m.desc, "implement-inherited", e.getKey().substring(0, e.getKey().indexOf(' ')), m.name, m.desc);
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * The abstract methods a concrete subclass/implementation of {@code cn} must supply, as
     * "declaringOwner name+desc" -> method, over the whole 1.20.1 hierarchy (superclasses,
     * all superinterfaces, library and JDK types). Returns false when the probe cannot be
     * concrete: a supertype is unknown, or an abstract method is package-private (only its
     * own package could implement it) -- 1.20.1 mods could not have made it concrete either.
     */
    private boolean collectAbstract(ClassNode cn, Map<String, MethodNode> out) {
        Map<String, MethodNode> concrete = new HashMap<>(); // name+desc implemented somewhere up the class chain
        Map<String, String[]> abstracts = new LinkedHashMap<>();
        boolean ok = true;
        List<ClassNode> chain = new ArrayList<>();
        for (String c = cn.name; c != null; ) {
            ClassNode n = node(c);
            if (n == null) {
                ok = false;
                break;
            }
            chain.add(n);
            c = n.superName;
        }
        for (ClassNode n : chain) {
            for (MethodNode m : n.methods) {
                if (m.name.startsWith("<") || is(m.access, Opcodes.ACC_STATIC)) {
                    continue;
                }
                String k = m.name + m.desc;
                if (is(m.access, Opcodes.ACC_ABSTRACT)) {
                    if (!concrete.containsKey(k) && !abstracts.containsKey(k)) {
                        abstracts.put(k, new String[] {n.name});
                        out.put(n.name + " " + k, m);
                        if (!memberReachable(m.access)) {
                            ok = false;
                        }
                    }
                } else {
                    concrete.putIfAbsent(k, m);
                }
            }
        }
        // interface methods: abstract unless a class in the chain implements them, or a default exists
        Set<String> seen = new LinkedHashSet<>();
        List<String> work = new ArrayList<>();
        for (ClassNode n : chain) {
            work.addAll(n.interfaces);
        }
        if (is(cn.access, Opcodes.ACC_INTERFACE)) {
            work.add(cn.name);
        }
        Map<String, MethodNode> defaults = new HashMap<>();
        Map<String, Object[]> itfAbstract = new LinkedHashMap<>();
        while (!work.isEmpty()) {
            String i = work.remove(work.size() - 1);
            if (!seen.add(i)) {
                continue;
            }
            ClassNode n = node(i);
            if (n == null) {
                ok = false;
                continue;
            }
            work.addAll(n.interfaces);
            for (MethodNode m : n.methods) {
                if (m.name.startsWith("<") || is(m.access, Opcodes.ACC_STATIC) || is(m.access, Opcodes.ACC_PRIVATE)) {
                    continue;
                }
                String k = m.name + m.desc;
                if (is(m.access, Opcodes.ACC_ABSTRACT)) {
                    itfAbstract.putIfAbsent(k, new Object[] {n.name, m});
                } else {
                    defaults.putIfAbsent(k, m);
                }
            }
        }
        for (Map.Entry<String, Object[]> e : itfAbstract.entrySet()) {
            String k = e.getKey();
            if (concrete.containsKey(k) || defaults.containsKey(k) || isObjectMethod(k)) {
                continue;
            }
            out.putIfAbsent(e.getValue()[0] + " " + k, (MethodNode) e.getValue()[1]);
        }
        // an abstract method a class in the chain declares but a superclass implemented is still abstract here;
        // a concrete one lower in the chain shadows an abstract one higher up (handled by order)
        return ok;
    }

    private static boolean isObjectMethod(String nameDesc) {
        return nameDesc.equals("equals(Ljava/lang/Object;)Z") || nameDesc.equals("hashCode()I")
            || nameDesc.equals("toString()Ljava/lang/String;");
    }

    /** the one abstract method of a functional interface (Object's public methods excluded), or null */
    private MethodNode singleAbstract(ClassNode itf) {
        Map<String, MethodNode> abs = new LinkedHashMap<>();
        if (!collectAbstract(itf, abs)) {
            return null;
        }
        MethodNode only = null;
        for (MethodNode m : abs.values()) {
            if (isObjectMethod(m.name + m.desc)) {
                continue;
            }
            if (only != null && !(only.name + only.desc).equals(m.name + m.desc)) {
                return null;
            }
            only = m;
        }
        return only;
    }

    // ------------------------------------------------------------------ bytecode helpers

    private static void pushDefaults(MethodVisitor mv, Type[] types) {
        for (Type t : types) {
            pushDefault(mv, t);
        }
    }

    private static void pushDefault(MethodVisitor mv, Type t) {
        switch (t.getSort()) {
            case Type.BOOLEAN, Type.BYTE, Type.CHAR, Type.SHORT, Type.INT -> mv.visitInsn(Opcodes.ICONST_0);
            case Type.LONG -> mv.visitInsn(Opcodes.LCONST_0);
            case Type.FLOAT -> mv.visitInsn(Opcodes.FCONST_0);
            case Type.DOUBLE -> mv.visitInsn(Opcodes.DCONST_0);
            default -> mv.visitInsn(Opcodes.ACONST_NULL);
        }
    }

    private static void popResult(MethodVisitor mv, Type t) {
        if (t.getSort() == Type.VOID) {
            return;
        }
        mv.visitInsn(t.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP);
    }

    private static void loadArgs(MethodVisitor mv, Type[] params) {
        int slot = 1;
        for (Type t : params) {
            mv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), slot);
            slot += t.getSize();
        }
    }

    private static void returnValue(MethodVisitor mv, Type t) {
        mv.visitInsn(t.getSort() == Type.VOID ? Opcodes.RETURN : t.getOpcode(Opcodes.IRETURN));
    }

    private static void returnDefault(MethodVisitor mv, Type t) {
        if (t.getSort() == Type.VOID) {
            mv.visitInsn(Opcodes.RETURN);
            return;
        }
        pushDefault(mv, t);
        mv.visitInsn(t.getOpcode(Opcodes.IRETURN));
    }

    private ApiProbe() {
    }

    @SuppressWarnings("unused")
    private static void copy(InputStream in, OutputStream out) throws IOException {
        in.transferTo(out);
    }
}
