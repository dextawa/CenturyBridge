package top.dext.centurybridge.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * The mixin side of the full diff as one synthetic mod, the way {@link ApiProbe} is the API side:
 * every point of release V a mixin can name, named once, chosen by nothing but V's own code.
 *
 * For every class of the game (net/minecraft, com/mojang/blaze3d; not interfaces, not anonymous or
 * synthetic classes -- their names are renumbered by every release) and every method of any access
 * that has code (not synthetic: lambda bodies and bridges are renumbered too):
 *
 *   cbmprobe/i/X_n   class mixin on X: @Inject at HEAD (taking the method's arguments; constructors
 *                    take none, Mixin injects there at RETURN only) and at RETURN; one @Inject per
 *                    distinct INVOKE / FIELD / NEW / constant anchor of the body, unordinaled, and
 *                    where it stands more than once at its first and its last occurrence (ordinal 0
 *                    and count-1; one in between survives exactly when both do and the count holds,
 *                    which the unordinaled one checks); and on every
 *                    eighth INVOKE anchor (a stable hash of method and anchor, not a corpus) a
 *                    @Redirect, a MixinExtras @WrapOperation, @ModifyExpressionValue (a value-returning
 *                    call) and @ModifyArg (index 0 of a call taking arguments)
 *   cbmprobe/s/X_n   class mixin on X: a @Shadow of every method (constructors excepted) and field,
 *                    each used once by a method of the mixin (a call, a read, a write of a non-final
 *                    field), as mods use them
 *   cbmprobe/a/X_n   accessor interface on X: @Accessor getter of every field (setter of every
 *                    non-final one), @Invoker of every method, constructor invokers of a concrete class
 *
 * Nothing in it is ever executed; Main mixin-judge judges it statically against a release's real
 * bodies, raw (the damage table) or converted by the engine (what the step's bridging carries).
 *
 *   mixin-probe <V code jar> <out.jar>
 * also writes <out.jar>.index.tsv: probeClass, member, usage, owner, target, anchor, ordinal, vcount
 * (one row per probe member: its mixin, its own name+desc, what it names in V).
 */
public final class MixinProbe {
    static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    static final String FINAL = "Lorg/spongepowered/asm/mixin/Final;";
    static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";
    static final String AT = "Lorg/spongepowered/asm/mixin/injection/At;";
    static final String REDIRECT = "Lorg/spongepowered/asm/mixin/injection/Redirect;";
    static final String MODIFY_ARG = "Lorg/spongepowered/asm/mixin/injection/ModifyArg;";
    static final String WRAP_OPERATION = "Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;";
    static final String MODIFY_EXPRESSION = "Lcom/llamalad7/mixinextras/injector/ModifyExpressionValue;";
    static final String OPERATION = "Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;";
    static final String CI = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";
    static final String CIR = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;";
    /** members per probe mixin class: keeps every class file's constant pool well inside its limit */
    private static final int CHUNK = 1500;

    /**
     * --only: just these members (keys usage, owner, target, anchor, ordinal -- a step's damage rows),
     * each in a mixin of its own. Mixin applies or rejects a mixin whole (a shadow it cannot find takes
     * the class down, and triage strips it for that), so a converted probe judged member by member
     * needs every member alone: the grouped probe is for raw judging only, which needs no triage.
     */
    private java.util.Set<String> only;

    private final List<String[]> index = new ArrayList<>();
    private final Map<String, byte[]> out = new LinkedHashMap<>();
    private final List<String> configNames = new ArrayList<>();

    public static void run(String[] args) throws IOException {
        MixinProbe p = new MixinProbe();
        Path code = Path.of(args[1]);
        Path jar = Path.of(args[2]);
        for (int i = 3; i < args.length; i++) {
            if (args[i].equals("--only")) {
                p.only = new java.util.HashSet<>();
                List<String> lines = Files.readAllLines(Path.of(args[++i]), StandardCharsets.UTF_8);
                for (int k = 1; k < lines.size(); k++) {
                    String[] r = lines.get(k).split("\t", -1);
                    p.only.add(String.join("\t", r[0], r[1], r[2], r[3], r[4]));
                }
            }
        }
        int targets = 0;
        try (ZipFile z = new ZipFile(code.toFile())) {
            for (ZipEntry e : java.util.Collections.list(z.entries())) {
                String n = e.getName();
                if (!n.endsWith(".class") || !(n.startsWith("net/minecraft/") || n.startsWith("com/mojang/blaze3d/"))) {
                    continue;
                }
                ClassNode cn = new ClassNode();
                try (InputStream in = z.getInputStream(e)) {
                    new ClassReader(in).accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                }
                if (probed(cn)) {
                    p.probe(cn);
                    targets++;
                }
            }
        }
        p.write(jar);
        System.out.println("mixin-probe: " + targets + " target classes, " + p.out.size() + " probe mixins, "
            + p.index.size() + " probe members -> " + jar);
    }

    /** a class a mod's mixin can name stably: not an interface, not anonymous / local, not synthetic */
    static boolean probed(ClassNode cn) {
        if ((cn.access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_MODULE)) != 0) {
            return false;
        }
        if (cn.name.endsWith("package-info")) {
            return false;
        }
        return !anonymous(cn.name);
    }

    /** Outer$1, Outer$1Local, Outer$1$Inner */
    static boolean anonymous(String name) {
        for (int i = name.indexOf('$'); i >= 0; i = name.indexOf('$', i + 1)) {
            if (i + 1 < name.length() && Character.isDigit(name.charAt(i + 1))) {
                return true;
            }
        }
        return false;
    }

    /** a method a mod's mixin can name stably: not synthetic (lambda bodies, bridges, access$) */
    static boolean probed(MethodNode m) {
        return (m.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) == 0 && !m.name.startsWith("lambda$");
    }

    // ------------------------------------------------------------------ anchors

    /** one anchor of a V body: the @At it is named by, and how often it stands there */
    record Anchor(String kind, String atValue, String target, List<String> args, AbstractInsnNode first) {
        String key() {
            return kind + " " + (target != null ? target : String.join(",", args));
        }
    }

    /**
     * Every distinct INVOKE / FIELD / NEW / constant anchor of a body with its occurrence count, in
     * first-occurrence order -- counted the way Mixin's injection points (and MixinRepair, which
     * mirrors them) match: a call or field access by owner, name and descriptor, a NEW by class, an
     * int constant by ICONST / BIPUSH / SIPUSH / LDC alike.
     */
    static Map<String, Object[]> anchors(MethodNode m) {
        Map<String, Object[]> out = new LinkedHashMap<>();
        for (AbstractInsnNode in : m.instructions) {
            Anchor a = anchor(in);
            if (a == null) {
                continue;
            }
            Object[] c = out.get(a.key());
            if (c == null) {
                out.put(a.key(), new Object[] {a, 1});
            } else {
                c[1] = (Integer) c[1] + 1;
            }
        }
        return out;
    }

    static Anchor anchor(AbstractInsnNode in) {
        if (in instanceof MethodInsnNode mi) {
            if (mi.owner.startsWith("[")) {
                return null; // array clone(): no class to name
            }
            return new Anchor("INVOKE", "INVOKE", "L" + mi.owner + ";" + mi.name + mi.desc, null, in);
        }
        if (in instanceof FieldInsnNode fi) {
            return new Anchor("FIELD", "FIELD", "L" + fi.owner + ";" + fi.name + ":" + fi.desc, null, in);
        }
        if (in instanceof TypeInsnNode ti && ti.getOpcode() == Opcodes.NEW) {
            return new Anchor("NEW", "NEW", ti.desc, null, in);
        }
        String c = constant(in);
        return c == null ? null : new Anchor("CONSTANT", "CONSTANT", null, List.of(c), in);
    }

    /** the @At(CONSTANT) args naming the instruction's constant, or null */
    static String constant(AbstractInsnNode in) {
        int op = in.getOpcode();
        if (op >= Opcodes.ICONST_M1 && op <= Opcodes.ICONST_5) {
            return "intValue=" + (op - Opcodes.ICONST_0);
        }
        if (op == Opcodes.LCONST_0 || op == Opcodes.LCONST_1) {
            return "longValue=" + (op - Opcodes.LCONST_0);
        }
        if (op >= Opcodes.FCONST_0 && op <= Opcodes.FCONST_2) {
            return "floatValue=" + (float) (op - Opcodes.FCONST_0);
        }
        if (op == Opcodes.DCONST_0 || op == Opcodes.DCONST_1) {
            return "doubleValue=" + (double) (op - Opcodes.DCONST_0);
        }
        if (in instanceof IntInsnNode ii && (op == Opcodes.BIPUSH || op == Opcodes.SIPUSH)) {
            return "intValue=" + ii.operand;
        }
        if (in instanceof LdcInsnNode l) {
            Object v = l.cst;
            if (v instanceof Integer) {
                return "intValue=" + v;
            }
            if (v instanceof Long) {
                return "longValue=" + v;
            }
            if (v instanceof Float) {
                return "floatValue=" + v;
            }
            if (v instanceof Double) {
                return "doubleValue=" + v;
            }
            // a string Mixin's args parser reads back unchanged (it trims, and splits at the first '=')
            if (v instanceof String s && s.equals(s.trim()) && s.indexOf('\n') < 0 && s.indexOf('\r') < 0 && s.length() < 2000) {
                return "stringValue=" + s;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ one target class

    private final class Chunked {
        final String kind;
        final String target;
        final boolean itf;
        ClassNode cur;
        int seq;
        int members;

        Chunked(String kind, String target, boolean itf) {
            this.kind = kind;
            this.target = target;
            this.itf = itf;
        }

        ClassNode cls() {
            if (cur == null || members >= (only != null ? 1 : CHUNK)) {
                flush();
                cur = new ClassNode();
                cur.version = Opcodes.V17;
                cur.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | (itf ? Opcodes.ACC_INTERFACE : 0);
                cur.name = "cbmprobe/" + kind + "/" + mangle(target) + "_" + (seq++);
                cur.superName = "java/lang/Object";
                AnnotationNode mx = new AnnotationNode(MIXIN);
                mx.values = new ArrayList<>(List.of("value", new ArrayList<>(List.of(Type.getObjectType(target)))));
                cur.invisibleAnnotations = new ArrayList<>(List.of(mx));
                members = 0;
            }
            members++;
            return cur;
        }

        void flush() {
            if (cur != null) {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cur.accept(cw);
                out.put(cur.name, cw.toByteArray());
                configNames.add(cur.name.substring("cbmprobe/".length()).replace('/', '.'));
                cur = null;
            }
        }
    }

    private void probe(ClassNode x) {
        Chunked inj = new Chunked("i", x.name, false);
        Chunked sh = new Chunked("s", x.name, false);
        Chunked acc = new Chunked("a", x.name, true);
        boolean concrete = (x.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_ENUM)) == 0;
        int n = 0;
        for (FieldNode f : x.fields) {
            if ((f.access & Opcodes.ACC_SYNTHETIC) != 0) {
                continue;
            }
            boolean st = (f.access & Opcodes.ACC_STATIC) != 0;
            boolean fin = (f.access & Opcodes.ACC_FINAL) != 0;
            String fk = f.name + ":" + f.desc;
            boolean wantGet = want("shadow-field-get", x.name, fk, "", -1);
            boolean wantPut = !fin && want("shadow-field-put", x.name, fk, "", -1);
            for (int pass = 0; pass < 2; pass++) {
            boolean doGet = pass == 0 ? wantGet : false;
            boolean doPut = pass == 0 ? (only == null && wantPut) : (only != null && wantPut);
            if (!doGet && !doPut) {
                continue;
            }
            // @Shadow field, read once (and written once when not final)
            ClassNode s = sh.cls();
            FieldNode sf = new FieldNode(f.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC),
                f.name, f.desc, null, null);
            sf.visibleAnnotations = new ArrayList<>(List.of(new AnnotationNode(SHADOW)));
            if (fin) {
                sf.visibleAnnotations.add(new AnnotationNode(FINAL));
            }
            s.fields.add(sf);
            if (doGet) {
            MethodNode get = method(s, Opcodes.ACC_PRIVATE | (st ? Opcodes.ACC_STATIC : 0), "g" + (n++), "()V");
            if (!st) {
                get.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            }
            get.instructions.add(new FieldInsnNode(st ? Opcodes.GETSTATIC : Opcodes.GETFIELD, s.name, f.name, f.desc));
            get.instructions.add(new InsnNode(Type.getType(f.desc).getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
            get.instructions.add(new InsnNode(Opcodes.RETURN));
            row(s, get, "shadow-field-get", x.name, f.name + ":" + f.desc, "", -1, 0);
            }
            if (doPut) {
                MethodNode put = method(s, Opcodes.ACC_PRIVATE | (st ? Opcodes.ACC_STATIC : 0), "p" + (n++), "()V");
                if (!st) {
                    put.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                }
                put.instructions.add(zero(Type.getType(f.desc)));
                put.instructions.add(new FieldInsnNode(st ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, s.name, f.name, f.desc));
                put.instructions.add(new InsnNode(Opcodes.RETURN));
                row(s, put, "shadow-field-put", x.name, f.name + ":" + f.desc, "", -1, 0);
            }
            }
            // @Accessor getter (and setter)
            if (want("accessor-get", x.name, fk, "", -1)) {
                ClassNode a = acc.cls();
                MethodNode ag = accessorMethod(a, st, "ag" + (n++), "()" + f.desc, ACCESSOR, f.name);
                row(a, ag, "accessor-get", x.name, fk, "", -1, 0);
            }
            if (!fin && want("accessor-set", x.name, fk, "", -1)) {
                ClassNode a = acc.cls();
                MethodNode as = accessorMethod(a, st, "as" + (n++), "(" + f.desc + ")V", ACCESSOR, f.name);
                row(a, as, "accessor-set", x.name, fk, "", -1, 0);
            }
        }
        for (MethodNode m : x.methods) {
            if (!probed(m)) {
                continue;
            }
            boolean st = (m.access & Opcodes.ACC_STATIC) != 0;
            boolean ctor = m.name.equals("<init>");
            boolean clinit = m.name.equals("<clinit>");
            if (!ctor && !clinit) {
                if (want("shadow-method", x.name, m.name + m.desc, "", -1)) {
                    shadowMethod(sh, x, m, n++);
                }
                if (want("invoker", x.name, m.name + m.desc, "", -1)) {
                    ClassNode a = acc.cls();
                    MethodNode iv = accessorMethod(a, st, "iv" + (n++), m.desc, INVOKER, m.name);
                    row(a, iv, "invoker", x.name, m.name + m.desc, "", -1, 0);
                }
            } else if (ctor && concrete && want("invoker-new", x.name, m.name + m.desc, "", -1)) {
                ClassNode a = acc.cls();
                MethodNode iv = accessorMethod(a, true, "in" + (n++), m.desc.substring(0, m.desc.length() - 1) + "L" + x.name + ";",
                    INVOKER, "<init>");
                row(a, iv, "invoker-new", x.name, m.name + m.desc, "", -1, 0);
            }
            if (m.instructions == null || m.instructions.size() == 0) {
                continue;
            }
            String sel = m.name + m.desc;
            Type ret = Type.getReturnType(m.desc);
            String cb = ret.getSort() == Type.VOID ? CI : CIR;
            if (!ctor) {
                // HEAD, taking the target's arguments as mods' handlers usually do
                String d = "(" + m.desc.substring(1, m.desc.indexOf(')')) + cb + ")V";
                inject(inj, x, m, st, n++, d, sel, at("HEAD", null, null, -1), "inject-head", "HEAD", -1, 1);
            }
            inject(inj, x, m, st, n++, "(" + cb + ")V", sel, at("RETURN", null, null, -1), "inject-return", "RETURN", -1, 1);
            for (Object[] e : anchors(m).values()) {
                Anchor an = (Anchor) e[0];
                int count = (Integer) e[1];
                String use = "inject-" + an.kind().toLowerCase(java.util.Locale.ROOT);
                inject(inj, x, m, st, n++, "(" + cb + ")V", sel, at(an.atValue(), an.target(), an.args(), -1), use, an.key(), -1, count);
                // the first occurrence (what mods name most) and the last (the first to go when the count
                // drops); an ordinal between them survives exactly when both do and the count holds
                for (int k : count > 1 ? new int[] {0, count - 1} : new int[0]) {
                    inject(inj, x, m, st, n++, "(" + cb + ")V", sel, at(an.atValue(), an.target(), an.args(), k), use, an.key(), k, count);
                }
                if (an.kind().equals("INVOKE") && sampled(x.name, sel, an.key())) {
                    n = operandInjectors(inj, x, m, st, n, sel, an, count);
                }
            }
        }
        inj.flush();
        sh.flush();
        acc.flush();
    }

    /** whether the probe holds this member (all of them, or only the --only table's) */
    private boolean want(String usage, String owner, String target, String anchor, int ordinal) {
        return only == null || only.contains(String.join("\t", usage, owner, target, tsv(anchor), String.valueOf(ordinal)));
    }

    /** every eighth INVOKE anchor, by a stable hash of where it stands and what it names */
    static boolean sampled(String owner, String sel, String key) {
        int h = (owner + "." + sel + " " + key).hashCode();
        return (h & 7) == 0;
    }

    /** @Redirect, @WrapOperation, @ModifyExpressionValue, @ModifyArg on one call anchor */
    private int operandInjectors(Chunked inj, ClassNode x, MethodNode m, boolean st, int n, String sel, Anchor an, int count) {
        MethodInsnNode call = (MethodInsnNode) an.first();
        if (call.getOpcode() == Opcodes.INVOKESPECIAL) {
            return n; // constructor and super calls: Mixin does not redirect them
        }
        boolean callStatic = call.getOpcode() == Opcodes.INVOKESTATIC;
        Type[] cargs = Type.getArgumentTypes(call.desc);
        Type cret = Type.getReturnType(call.desc);
        StringBuilder operands = new StringBuilder();
        if (!callStatic) {
            operands.append("L").append(call.owner).append(";");
        }
        for (Type t : cargs) {
            operands.append(t.getDescriptor());
        }
        String r = cret.getDescriptor();
        handler(inj, x, st, n++, "(" + operands + ")" + r, REDIRECT, sel, at("INVOKE", an.target(), null, -1), false, null,
            "redirect-invoke", m, an.key(), count);
        handler(inj, x, st, n++, "(" + operands + OPERATION + ")" + r, WRAP_OPERATION, sel, at("INVOKE", an.target(), null, -1), true,
            null, "wrap-invoke", m, an.key(), count);
        if (cret.getSort() != Type.VOID) {
            handler(inj, x, st, n++, "(" + r + ")" + r, MODIFY_EXPRESSION, sel, at("INVOKE", an.target(), null, -1), true, null,
                "modexpr-invoke", m, an.key(), count);
        }
        if (cargs.length > 0) {
            String a0 = cargs[0].getDescriptor();
            handler(inj, x, st, n++, "(" + a0 + ")" + a0, MODIFY_ARG, sel, at("INVOKE", an.target(), null, -1), false, 0,
                "modarg-invoke", m, an.key(), count);
        }
        return n;
    }

    private void shadowMethod(Chunked sh, ClassNode x, MethodNode m, int n) {
        ClassNode s = sh.cls();
        boolean st = (m.access & Opcodes.ACC_STATIC) != 0;
        boolean priv = (m.access & Opcodes.ACC_PRIVATE) != 0;
        int access = m.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC);
        boolean abs = !st && !priv;
        MethodNode shadow = new MethodNode(access | (abs ? Opcodes.ACC_ABSTRACT : 0), m.name, m.desc, null, null);
        shadow.visibleAnnotations = new ArrayList<>(List.of(new AnnotationNode(SHADOW)));
        if (!abs) {
            throwBody(shadow.instructions);
        }
        s.methods.add(shadow);
        MethodNode use = method(s, Opcodes.ACC_PRIVATE | (st ? Opcodes.ACC_STATIC : 0), "c" + n, "()V");
        if (!st) {
            use.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        }
        for (Type t : Type.getArgumentTypes(m.desc)) {
            use.instructions.add(zero(t));
        }
        int op = st ? Opcodes.INVOKESTATIC : priv ? Opcodes.INVOKESPECIAL : Opcodes.INVOKEVIRTUAL;
        use.instructions.add(new MethodInsnNode(op, s.name, m.name, m.desc, false));
        Type ret = Type.getReturnType(m.desc);
        if (ret.getSort() != Type.VOID) {
            use.instructions.add(new InsnNode(ret.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
        }
        use.instructions.add(new InsnNode(Opcodes.RETURN));
        row(s, use, "shadow-method", x.name, m.name + m.desc, "", -1, 0);
    }

    private void inject(Chunked inj, ClassNode x, MethodNode m, boolean st, int n, String desc, String sel, AnnotationNode at,
                        String usage, String anchor, int ordinal, int count) {
        handler(inj, x, st, n, desc, INJECT, sel, at, true, null, usage, m, anchor, count, ordinal);
    }

    private void handler(Chunked inj, ClassNode x, boolean st, int n, String desc, String ann, String sel, AnnotationNode at,
                         boolean atArray, Integer index, String usage, MethodNode target, String anchor, int count) {
        handler(inj, x, st, n, desc, ann, sel, at, atArray, index, usage, target, anchor, count, -1);
    }

    private void handler(Chunked inj, ClassNode x, boolean st, int n, String desc, String ann, String sel, AnnotationNode at,
                         boolean atArray, Integer index, String usage, MethodNode target, String anchor, int count, int ordinal) {
        if (!want(usage, x.name, target.name + target.desc, anchor, ordinal)) {
            return;
        }
        ClassNode c = inj.cls();
        MethodNode h = method(c, Opcodes.ACC_PRIVATE | (st ? Opcodes.ACC_STATIC : 0), "h" + n, desc);
        Type r = Type.getReturnType(desc);
        Type[] a = Type.getArgumentTypes(desc);
        if (r.getSort() == Type.VOID) {
            h.instructions.add(new InsnNode(Opcodes.RETURN));
        } else if (a.length > 0 && a[0].equals(r) && !ann.equals(REDIRECT) && !ann.equals(WRAP_OPERATION)) {
            h.instructions.add(new VarInsnNode(r.getOpcode(Opcodes.ILOAD), st ? 0 : 1));
            h.instructions.add(new InsnNode(r.getOpcode(Opcodes.IRETURN)));
        } else {
            h.instructions.add(zero(r));
            h.instructions.add(new InsnNode(r.getOpcode(Opcodes.IRETURN)));
        }
        AnnotationNode an = new AnnotationNode(ann);
        an.values = new ArrayList<>();
        an.values.add("method");
        an.values.add(new ArrayList<>(List.of(sel)));
        an.values.add("at");
        an.values.add(atArray ? new ArrayList<>(List.of(at)) : at);
        if (index != null) {
            an.values.add("index");
            an.values.add(index);
        }
        h.visibleAnnotations = new ArrayList<>(List.of(an));
        row(c, h, usage, x.name, target.name + target.desc, anchor, ordinal, count);
    }

    static AnnotationNode at(String value, String target, List<String> args, int ordinal) {
        AnnotationNode at = new AnnotationNode(AT);
        at.values = new ArrayList<>();
        at.values.add("value");
        at.values.add(value);
        if (target != null) {
            at.values.add("target");
            at.values.add(target);
        }
        if (args != null) {
            at.values.add("args");
            at.values.add(new ArrayList<>(args));
        }
        if (ordinal >= 0) {
            at.values.add("ordinal");
            at.values.add(ordinal);
        }
        return at;
    }

    private MethodNode accessorMethod(ClassNode a, boolean st, String name, String desc, String ann, String value) {
        MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC | (st ? Opcodes.ACC_STATIC : Opcodes.ACC_ABSTRACT), name, desc, null, null);
        AnnotationNode an = new AnnotationNode(ann);
        an.values = new ArrayList<>(List.of("value", value));
        m.visibleAnnotations = new ArrayList<>(List.of(an));
        if (st) {
            throwBody(m.instructions);
        }
        a.methods.add(m);
        return m;
    }

    private static MethodNode method(ClassNode c, int access, String name, String desc) {
        MethodNode m = new MethodNode(access, name, desc, null, null);
        c.methods.add(m);
        return m;
    }

    private static void throwBody(InsnList in) {
        in.add(new TypeInsnNode(Opcodes.NEW, "java/lang/AbstractMethodError"));
        in.add(new InsnNode(Opcodes.DUP));
        in.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/AbstractMethodError", "<init>", "()V", false));
        in.add(new InsnNode(Opcodes.ATHROW));
    }

    private static AbstractInsnNode zero(Type t) {
        return switch (t.getSort()) {
            case Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT -> new InsnNode(Opcodes.ICONST_0);
            case Type.FLOAT -> new InsnNode(Opcodes.FCONST_0);
            case Type.LONG -> new InsnNode(Opcodes.LCONST_0);
            case Type.DOUBLE -> new InsnNode(Opcodes.DCONST_0);
            default -> new InsnNode(Opcodes.ACONST_NULL);
        };
    }

    private void row(ClassNode c, MethodNode m, String usage, String owner, String target, String anchor, int ordinal, int count) {
        index.add(new String[] {c.name, m.name + m.desc, usage, owner, target, tsv(anchor), String.valueOf(ordinal), String.valueOf(count)});
    }

    /** a string constant's tabs and backslashes escaped, so an anchor stays one TSV field */
    static String tsv(String s) {
        return s.indexOf('\t') < 0 && s.indexOf('\\') < 0 ? s : s.replace("\\", "\\\\").replace("\t", "\\t");
    }

    private static String mangle(String internal) {
        return internal.replace('/', '_').replace('$', '_');
    }

    // ------------------------------------------------------------------ jar

    private void write(Path jar) throws IOException {
        Files.createDirectories(jar.toAbsolutePath().getParent());
        StringBuilder cfg = new StringBuilder("{\"required\":true,\"package\":\"cbmprobe\",\"compatibilityLevel\":\"JAVA_17\","
            + "\"minVersion\":\"0.8\",\"injectors\":{\"defaultRequire\":1},\"mixins\":[");
        for (int i = 0; i < configNames.size(); i++) {
            cfg.append(i == 0 ? "" : ",").append('"').append(configNames.get(i)).append('"');
        }
        cfg.append("]}");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(jar))) {
            zip.putNextEntry(new ZipEntry("fabric.mod.json"));
            zip.write(("{\"schemaVersion\":1,\"id\":\"cb_mixinprobe\",\"version\":\"1\",\"name\":\"CenturyBridge mixin probe\","
                + "\"environment\":\"*\",\"mixins\":[\"cbmprobe.mixins.json\"]}").getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("cbmprobe.mixins.json"));
            zip.write(cfg.toString().getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            for (Map.Entry<String, byte[]> e : out.entrySet()) {
                zip.putNextEntry(new ZipEntry(e.getKey() + ".class"));
                zip.write(e.getValue());
                zip.closeEntry();
            }
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(Path.of(jar + ".index.tsv"), StandardCharsets.UTF_8))) {
            w.println("probeClass\tmember\tusage\towner\ttarget\tanchor\tordinal\tvcount");
            for (String[] r : index) {
                w.println(String.join("\t", r));
            }
        }
    }

    private MixinProbe() {
    }
}
