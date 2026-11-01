package top.dext.centurybridge.engine;

import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

// Run (beta2, engine compiled into core/build/classes): javac -cp "core/build/classes:$LIBS" -d /tmp/so core/tools/SitesOneProbe.java && java -cp "/tmp/so:core/build/classes:$LIBS" top.dext.centurybridge.engine.SitesOneProbe data/jars/client-1.21.5-intermediary-code.jar data/jars/client-1.21.6-intermediary-code.jar
/**
 * MixinLocals.operandRolesDiffer and the one-site keep of MixinRepair.splitSites (smixin14), plus end to end through
 * MixinRepair.repair (smixin16): a sites-one keep never bypasses the captured-args check (static V foo(X,int) -> instance W
 * foo(int) is refused), while the 17 allmix targets' shape (static V -> instance W, same descriptor) is kept.
 */
public class SitesOneProbe {
    static int fails;
    static final String B = "net/minecraft/class_287", BD = "()Lnet/minecraft/class_9801;";
    static ClassNode load(String jar, String cls) throws Exception {
        try (ZipFile z = new ZipFile(jar)) {
            ClassNode cn = new ClassNode();
            new ClassReader(z.getInputStream(z.getEntry(cls + ".class")).readAllBytes()).accept(cn, 0);
            return cn;
        }
    }
    static MethodNode m(ClassNode c, String name) { for (MethodNode m : c.methods) if (m.name.equals(name)) return m; return null; }
    static List<AbstractInsnNode> sites(MethodNode m) {
        List<AbstractInsnNode> o = new ArrayList<>();
        for (AbstractInsnNode in : m.instructions) if (in instanceof MethodInsnNode mi && mi.owner.equals(B) && mi.name.equals("method_60794")) o.add(in);
        return o;
    }
    /** a body: receiver from (0) Map.Entry.getValue checkcast, (1) a field of this; then an unrelated tail call */
    static MethodNode synth(int recv, String tailOwner) {
        MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC, "t", "(Ljava/util/Map$Entry;)V", null, null);
        InsnList l = m.instructions;
        if (recv == 0) { l.add(new VarInsnNode(Opcodes.ALOAD, 1)); l.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true)); l.add(new TypeInsnNode(Opcodes.CHECKCAST, B)); }
        else { l.add(new VarInsnNode(Opcodes.ALOAD, 0)); l.add(new FieldInsnNode(Opcodes.GETFIELD, "x/Owner", "builder", "L" + B + ";")); }
        l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, B, "method_60794", BD, false));
        l.add(new InsnNode(Opcodes.POP));
        l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, tailOwner, "tail", "()V", false));
        l.add(new InsnNode(Opcodes.RETURN));
        m.maxLocals = 2; m.maxStack = 2;
        return m;
    }
    static void check(String what, String got, boolean wantSame) {
        boolean ok = (got == null) == wantSame; fails += ok ? 0 : 1;
        System.out.println((ok ? "PASS " : "FAIL ") + what + " -> " + (got == null ? "same roles" : got));
    }
    public static void main(String[] a) throws Exception {
        MixinRepair w = new MixinRepair(Path.of(a[1]));
        ClassNode vc = load(a[0], "net/minecraft/class_9810"), wc = load(a[1], "net/minecraft/class_9810");
        MethodNode vm = m(vc, "method_60904"), wm = m(wc, "method_60904");
        List<AbstractInsnNode> vo = sites(vm), wo = sites(wm);
        System.out.println("real: V sites " + vo.size() + ", W sites " + wo.size() + "; SiteMatch.match -> " + Arrays.toString(SiteMatch.match(vm, vo, wm, wo)));
        check("positive: 1.21.5 -> 1.21.6 SectionCompiler.compile build()", MixinLocals.operandRolesDiffer(w, vc.name, vm, vo.get(0), wc.name, wm, wo.get(0)), true);
        MethodNode sv = synth(0, "a/A"), sw = synth(0, "b/B"), sn = synth(1, "a/A");
        check("positive: same receiver role, different following call", MixinLocals.operandRolesDiffer(w, "x/Owner", sv, sites(sv).get(0), "x/Owner", sw, sites(sw).get(0)), true);
        check("negative: one occurrence, receiver a field of this instead of Entry.getValue", MixinLocals.operandRolesDiffer(w, "x/Owner", sv, sites(sv).get(0), "x/Owner", sn, sites(sn).get(0)), false);
        endToEnd();
        System.out.println(fails == 0 ? "ALL PASS" : fails + " FAIL");
    }

    // ---------------------------------------------------------------- smixin16: end to end through MixinRepair.repair
    static final String T = "net/minecraft/class_t", X = "net/minecraft/class_x";
    static final String CI = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";

    /** the target class: foo with one call to X.baz()V; before/after differ per side so the site is not matched by context */
    static byte[] target(boolean st, String desc, boolean wSide) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, T, null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | (st ? Opcodes.ACC_STATIC : 0), "foo", desc, null, null);
        mv.visitCode();
        if (wSide) { mv.visitInsn(Opcodes.ICONST_0); mv.visitInsn(Opcodes.POP); } else { mv.visitInsn(Opcodes.ICONST_5); mv.visitInsn(Opcodes.POP); }
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, X, "baz", "()V", false);
        if (wSide) { mv.visitInsn(Opcodes.ICONST_1); mv.visitInsn(Opcodes.POP); }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }
    static byte[] xClass() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, X, null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "baz", "()V", null, null);
        mv.visitCode(); mv.visitInsn(Opcodes.RETURN); mv.visitMaxs(0, 0); mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }
    static Path jar(String tag, byte[] t) throws Exception {
        Path p = Files.createTempFile("sitesone-" + tag, ".jar");
        try (ZipOutputStream z = new ZipOutputStream(Files.newOutputStream(p))) {
            z.putNextEntry(new ZipEntry(T + ".class")); z.write(t); z.closeEntry();
            z.putNextEntry(new ZipEntry(X + ".class")); z.write(xClass()); z.closeEntry();
        }
        return p;
    }
    /** the mod's mixin, written against V: a static @Inject on foo capturing V's arguments (X, int) */
    static byte[] mixin() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT, "m/M", null, "java/lang/Object", null);
        AnnotationVisitor av = cw.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;", false);
        AnnotationVisitor arr = av.visitArray("value"); arr.visit(null, Type.getObjectType(T)); arr.visitEnd(); av.visitEnd();
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "h", "(L" + X + ";I" + CI + ")V", null, null);
        AnnotationVisitor ia = mv.visitAnnotation("Lorg/spongepowered/asm/mixin/injection/Inject;", true);
        AnnotationVisitor ma = ia.visitArray("method"); ma.visit(null, "foo"); ma.visitEnd();
        AnnotationVisitor ata = ia.visitArray("at");
        AnnotationVisitor at = ata.visitAnnotation(null, "Lorg/spongepowered/asm/mixin/injection/At;");
        at.visit("value", "INVOKE"); at.visit("target", "L" + X + ";baz()V"); at.visitEnd();
        ata.visitEnd(); ia.visitEnd();
        mv.visitCode(); mv.visitInsn(Opcodes.RETURN); mv.visitMaxs(0, 0); mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }
    static MixinRepair.Outcome run(String tag, boolean wStatic, String wDesc) throws Exception {
        String vDesc = "(L" + X + ";I)V";
        MixinRepair w = new MixinRepair(jar(tag + "-w", target(wStatic, wDesc, true)));
        w.source = new MixinRepair(jar(tag + "-v", target(true, vDesc, false)));
        Map<String, byte[]> e = new HashMap<>();
        e.put("fabric.mod.json", "{\"schemaVersion\":1,\"id\":\"m\",\"mixins\":[\"m.mixins.json\"]}".getBytes());
        e.put("m.mixins.json", "{\"package\":\"m\",\"mixins\":[\"M\"],\"injectors\":{\"defaultRequire\":1}}".getBytes());
        e.put("m/M.class", mixin());
        MixinRepair.Outcome o = w.repair(e);
        System.out.println("  [" + tag + "] repairs " + o.repairs());
        System.out.println("  [" + tag + "] disabled " + o.disabled());
        System.out.println("  [" + tag + "] fatal " + o.fatal());
        return o;
    }
    static void endToEnd() throws Exception {
        String vDesc = "(L" + X + ";I)V";
        // control: W keeps V's static method with V's descriptor, only the surroundings of the one call differ -> sites-one keeps it
        MixinRepair.Outcome a = run("static-same", true, vDesc);
        boolean aOk = a.disabled().isEmpty() && a.fatal().isEmpty() && a.repairs().stream().anyMatch(r -> r.contains("sites-one"));
        fails += aOk ? 0 : 1;
        System.out.println((aOk ? "PASS " : "FAIL ") + "control: static V -> static W, same descriptor: sites-one keeps the injector");
        // the 17 allmix targets' shape: V static, W instance, same descriptor (the receiver is new): kept, no refusal
        MixinRepair.Outcome b = run("instance-same", false, vDesc);
        boolean bOk = b.disabled().isEmpty() && b.fatal().isEmpty();
        fails += bOk ? 0 : 1;
        System.out.println((bOk ? "PASS " : "FAIL ") + "positive: static V -> instance W, same descriptor (the 17 targets' shape): kept");
        // negative (coordinator): V static foo(X, int), W instance foo(int) -- X became this. The handler captures (X, int);
        // sites-one may judge the one call alike, but the injector must not stay with V's captured arguments
        MixinRepair.Outcome c = run("instance-dropped-first", false, "(I)V");
        boolean refused = !c.disabled().isEmpty() && c.disabled().stream().anyMatch(d -> d.contains("M.h") || d.contains(".h "));
        boolean reshaped = c.repairs().stream().anyMatch(r -> r.contains("captured-args"));
        boolean cOk = (refused || reshaped) && c.fatal().isEmpty();
        fails += cOk ? 0 : 1;
        System.out.println((cOk ? "PASS " : "FAIL ") + "negative: static V foo(X,int) -> instance W foo(int): injector "
            + (refused ? "refused" : reshaped ? "args-reshaped" : "KEPT with V's captured arguments"));
    }
}
