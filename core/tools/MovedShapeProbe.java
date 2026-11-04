package top.dext.centurybridge.engine;

import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.*;

// Run (beta2, engine compiled into core/build/classes): javac -cp "core/build/classes:$LIBS" -d /tmp/ms core/tools/MovedShapeProbe.java && java -cp "/tmp/ms:core/build/classes:$LIBS" top.dext.centurybridge.engine.MovedShapeProbe data/jars/client-1.20.6-intermediary-code.jar data/jars/client-1.21-intermediary-code.jar
/**
 * smixin18: mixinMoves onto a W method of another shape (1.20.6 RebuildTask.compile -> 1.21 SectionCompiler.compile) end to
 * end through MixinRepair.repair on the real jars. Positive: a HEAD @Inject that never reads this moves; doTask's
 * @At INVOKE compile is respelled to the SectionCompiler.compile call. Negative: a handler reading this, one setting the
 * answer, and an operand injector (@Redirect) on doTask's moved call stay disabled.
 */
public class MovedShapeProbe {
    static int fails;
    static final String RT = "net/minecraft/class_846$class_851$class_4578";
    static final String CR = RT + "$class_7435";
    static final String VKEY = RT + ".method_22785(FFFLnet/minecraft/class_750;)L" + CR + ";";
    static final String WTO = "net/minecraft/class_9810.method_60904(Lnet/minecraft/class_4076;Lnet/minecraft/class_853;Lnet/minecraft/class_8251;Lnet/minecraft/class_750;)Lnet/minecraft/class_9810$class_9811;";
    static final String CI = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";
    static final String CIR = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;";

    /** kind: 0 HEAD on compile, plain; 1 HEAD on compile reading this; 2 HEAD on compile setting the answer;
     *  3 @Inject INVOKE compile in doTask; 4 @Redirect INVOKE compile in doTask */
    static byte[] mixin(int kind) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT, "m/M", null, "java/lang/Object", null);
        AnnotationVisitor av = cw.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;", false);
        AnnotationVisitor arr = av.visitArray("value"); arr.visit(null, Type.getObjectType(RT)); arr.visitEnd(); av.visitEnd();
        boolean onDoTask = kind >= 3;
        String desc = kind == 4 ? "(L" + RT + ";FFFLnet/minecraft/class_750;)L" + CR + ";" : onDoTask ? "(" + CIR + ")V" : "(" + CIR + ")V";
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE, "h", desc, null, null);
        AnnotationVisitor ia = mv.visitAnnotation(kind == 4 ? "Lorg/spongepowered/asm/mixin/injection/Redirect;"
            : "Lorg/spongepowered/asm/mixin/injection/Inject;", true);
        AnnotationVisitor ma = ia.visitArray("method");
        ma.visit(null, onDoTask ? "method_22783(Lnet/minecraft/class_750;)Ljava/util/concurrent/CompletableFuture;"
            : "method_22785(FFFLnet/minecraft/class_750;)L" + CR + ";");
        ma.visitEnd();
        AnnotationVisitor at;
        if (kind == 4) {
            at = ia.visitAnnotation("at", "Lorg/spongepowered/asm/mixin/injection/At;");
        } else {
            AnnotationVisitor ata = ia.visitArray("at");
            at = ata.visitAnnotation(null, "Lorg/spongepowered/asm/mixin/injection/At;");
            if (onDoTask) { at.visit("value", "INVOKE"); at.visit("target", "L" + RT + ";method_22785(FFFLnet/minecraft/class_750;)L" + CR + ";"); }
            else at.visit("value", "HEAD");
            at.visitEnd(); ata.visitEnd();
            at = null;
        }
        if (at != null) { at.visit("value", "INVOKE"); at.visit("target", "L" + RT + ";method_22785(FFFLnet/minecraft/class_750;)L" + CR + ";"); at.visitEnd(); }
        ia.visitEnd();
        mv.visitCode();
        if (kind == 1) { mv.visitVarInsn(Opcodes.ALOAD, 0); mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false); mv.visitInsn(Opcodes.POP); }
        if (kind == 2) { mv.visitVarInsn(Opcodes.ALOAD, 1); mv.visitInsn(Opcodes.ACONST_NULL); mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, CIR.substring(1, CIR.length() - 1), "setReturnValue", "(Ljava/lang/Object;)V", false); }
        if (kind == 4) { mv.visitInsn(Opcodes.ACONST_NULL); mv.visitInsn(Opcodes.ARETURN); } else mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0); mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    static MixinRepair.Outcome run(String tag, int kind, Path v, Path w) throws Exception {
        MixinRepair r = new MixinRepair(w);
        r.source = new MixinRepair(v);
        r.mixinMoves = Map.of(VKEY, WTO);
        Map<String, byte[]> e = new HashMap<>();
        e.put("fabric.mod.json", "{\"schemaVersion\":1,\"id\":\"m\",\"mixins\":[\"m.mixins.json\"]}".getBytes());
        e.put("m.mixins.json", "{\"package\":\"m\",\"mixins\":[\"M\"],\"injectors\":{\"defaultRequire\":1}}".getBytes());
        e.put("m/M.class", mixin(kind));
        MixinRepair.Outcome o = r.repair(e);
        System.out.println("  [" + tag + "] repairs " + o.repairs());
        System.out.println("  [" + tag + "] disabled " + o.disabled());
        System.out.println("  [" + tag + "] fatal " + o.fatal());
        return o;
    }

    static void check(String what, boolean ok) {
        fails += ok ? 0 : 1;
        System.out.println((ok ? "PASS " : "FAIL ") + what);
    }

    public static void main(String[] a) throws Exception {
        Path v = Path.of(a[0]), w = Path.of(a[1]);
        MixinRepair.Outcome o0 = run("head", 0, v, w);
        check("positive: HEAD @Inject on compile, never reads this -> moved onto SectionCompiler.compile",
            o0.disabled().isEmpty() && o0.repairs().stream().anyMatch(x -> x.contains("class_9810.method_60904")));
        MixinRepair.Outcome o1 = run("this", 1, v, w);
        check("negative: handler reads this -> disabled",
            o1.repairs().stream().noneMatch(x -> x.startsWith("move ")) && o1.disabled().stream().anyMatch(x -> x.contains("reads this")));
        MixinRepair.Outcome o2 = run("answer", 2, v, w);
        check("negative: handler sets CompileResults answer -> disabled",
            o2.repairs().stream().noneMatch(x -> x.startsWith("move ")) && o2.disabled().stream().anyMatch(x -> x.contains("answer")));
        MixinRepair.Outcome o3 = run("call", 3, v, w);
        check("positive: doTask @Inject INVOKE compile -> SectionCompiler.compile call",
            o3.disabled().isEmpty() && o3.repairs().stream().anyMatch(x -> x.startsWith("anchor-moved-call") && x.contains("method_60904")));
        MixinRepair.Outcome o4 = run("redirect", 4, v, w);
        check("negative: doTask @Redirect of compile (operands changed) -> not respelled",
            o4.repairs().stream().noneMatch(x -> x.startsWith("anchor-moved-call")) && !o4.disabled().isEmpty());
        System.out.println(fails == 0 ? "ALL PASS" : fails + " FAIL");
    }
}
