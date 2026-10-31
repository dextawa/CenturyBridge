package top.dext.centurybridge.engine;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

/**
 * smixin K1: a kept injector whose handler captures the target's locals -- an @Inject with locals =
 * CAPTURE_FAILHARD / CAPTURE_FAILEXCEPTION (the handler parameters after the CallbackInfo), or a MixinExtras @Local
 * parameter (implicit by type, ordinal or index) -- fails at apply when the step moved or retyped those locals
 * ("LVT ... incompatible", "Found 0 candidate variables", "Unable to find matching local"), and nothing judged them:
 * the converter carried such injectors live and silent (Kilt S1, S2, S3, S5, S6).
 * <p>
 * The locals are modelled as Mixin sees them in the game jar, which carries no local variable table: per slot, the
 * type a verifier's analysis gives at the injection point (Locals.getLocalsAt / generateLocalVariableTable). A capture is
 * judged only where the model says it fits V's target and not W's -- where it does not fit V either, the model is not
 * trusted and nothing is said. Then:
 * <ul><li>an index or ordinal @Local whose type is unique among V's locals at every point and unique among W's: the
 * discriminator is dropped (implicit by type picks the same variable on W);</li>
 * <li>anything else: the injector is disabled with the reason (the caller's downgrade, require 0, reported).</li></ul>
 */
final class MixinLocals {
    private MixinLocals() {
    }

    static final String LOCAL = "Lcom/llamalad7/mixinextras/sugar/Local;";
    private static final String CI = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";
    private static final String CIR = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;";
    private static final Map<String, Type> REFS = Map.of(
        "com/llamalad7/mixinextras/sugar/ref/LocalBooleanRef", Type.BOOLEAN_TYPE,
        "com/llamalad7/mixinextras/sugar/ref/LocalByteRef", Type.BYTE_TYPE,
        "com/llamalad7/mixinextras/sugar/ref/LocalCharRef", Type.CHAR_TYPE,
        "com/llamalad7/mixinextras/sugar/ref/LocalShortRef", Type.SHORT_TYPE,
        "com/llamalad7/mixinextras/sugar/ref/LocalIntRef", Type.INT_TYPE,
        "com/llamalad7/mixinextras/sugar/ref/LocalLongRef", Type.LONG_TYPE,
        "com/llamalad7/mixinextras/sugar/ref/LocalFloatRef", Type.FLOAT_TYPE,
        "com/llamalad7/mixinextras/sugar/ref/LocalDoubleRef", Type.DOUBLE_TYPE);

    /** why null: fits or not judged; fix non-null: a remap to apply (note says what) */
    record Verdict(String why, Runnable fix, String note) {
    }

    static Verdict judge(MixinRepair w, MixinRepair v, AnnotationNode an, MethodNode h, List<MethodNode> wTargets,
            ClassNode wOwner, ClassNode vOwner, MethodNode vTarget, JsonObject refmap) {
        if (v == null || vTarget == null || vOwner == null || an.desc.endsWith("/ModifyVariable;")) {
            return null;
        }
        Map<String, Object> av = MixinRepair.values(an);
        List<String> notes = new ArrayList<>();
        List<Runnable> fixes = new ArrayList<>();
        // CAPTURE_FAILHARD / FAILEXCEPTION: the handler's parameters after its CallbackInfo, sugar excluded
        if (an.desc.endsWith("/Inject;") && av.get("locals") instanceof String[] e && e.length == 2
                && (e[1].equals("CAPTURE_FAILHARD") || e[1].equals("CAPTURE_FAILEXCEPTION"))) {
            List<Type> want = capturedLocals(h);
            if (want != null && !want.isEmpty()) {
                Boolean vf = captureFits(v, vOwner, vTarget, av, refmap, want);
                if (vf != null && vf) {
                    List<Type> wWant = want.stream().map(t -> renamed(w, t)).toList();
                    for (MethodNode m : wTargets) {
                        Boolean wf = captureFits(w, wOwner, m, av, refmap, wWant);
                        if (wf != null && !wf) {
                            return new Verdict(e[1] + " captures " + want + " after the arguments; " + m.name + m.desc
                                + " has other locals there", null, null);
                        }
                    }
                }
            }
        }
        // MixinExtras @Local parameters
        Type[] hp = Type.getArgumentTypes(h.desc);
        for (int k = 0; k < hp.length; k++) {
            AnnotationNode loc = paramAnnotation(h, k, LOCAL);
            if (loc == null) {
                continue;
            }
            Type t = localType(hp[k]);
            if (t == null) {
                continue; // a generic LocalRef<T>: the type is in the signature, not judged
            }
            Map<String, Object> lv = MixinRepair.values(loc);
            int ordinal = lv.get("ordinal") instanceof Integer o ? o : -1;
            int index = lv.get("index") instanceof Integer x ? x : -1;
            boolean argsOnly = Boolean.TRUE.equals(lv.get("argsOnly"));
            if (lv.get("name") != null && !MixinRepair.list(lv.get("name")).isEmpty()) {
                continue; // by name: no table in the game jar to judge it against
            }
            int[] vc = pick(v, vOwner, vTarget, av, refmap, t, ordinal, index, argsOnly);
            if (vc == null || vc[0] != 1) {
                continue; // not modelled, or does not fit V by the model: not trusted
            }
            boolean wFits = true;
            boolean wUnique = true;
            Type tw = renamed(w, t);
            for (MethodNode m : wTargets) {
                int[] wc = pick(w, wOwner, m, av, refmap, tw, ordinal, index, argsOnly);
                if (wc == null) {
                    wFits = true;
                    wUnique = false;
                    break;
                }
                wFits &= wc[0] == 1;
                wUnique &= wc[1] == 1;
            }
            if (wFits) {
                continue;
            }
            int[] vUnique = pick(v, vOwner, vTarget, av, refmap, t, -1, -1, argsOnly);
            if ((ordinal >= 0 || index >= 0) && wUnique && vUnique != null && vUnique[1] == 1) {
                notes.add("@Local " + t.getDescriptor() + (index >= 0 ? " index " + index : " ordinal " + ordinal)
                    + " -> implicit (the one " + t.getDescriptor() + " local on V and on W)");
                fixes.add(() -> {
                    removeValue(loc, "index");
                    removeValue(loc, "ordinal");
                });
                continue;
            }
            return new Verdict("@Local " + t.getDescriptor() + (index >= 0 ? " index " + index : ordinal >= 0 ? " ordinal " + ordinal : " (implicit)")
                + " fits V's " + vTarget.name + vTarget.desc + " but not W's (the step moved or retyped the local)", null, null);
        }
        if (fixes.isEmpty()) {
            return null;
        }
        return new Verdict(null, () -> fixes.forEach(Runnable::run), String.join("; ", notes));
    }

    /**
     * a V type as the converted jar spells it on W: the step's classRenames rewrite the handler's descriptor, so
     * the local W must hold is the renamed type (1.20.5: ChunkStatus$ChunkType -> ChunkType)
     */
    private static Type renamed(MixinRepair w, Type t) {
        return w.classRenames.isEmpty() ? t : Type.getType(MixinClassRenames.rename(t.getDescriptor(), w.classRenames));
    }

    /** the handler's captured local types: its parameters after the CallbackInfo, sugar parameters left out */
    private static List<Type> capturedLocals(MethodNode h) {
        Type[] hp = Type.getArgumentTypes(h.desc);
        int ci = -1;
        for (int k = 0; k < hp.length; k++) {
            if (hp[k].getDescriptor().equals(CI) || hp[k].getDescriptor().equals(CIR)) {
                ci = k;
                break;
            }
        }
        if (ci < 0) {
            return null;
        }
        List<Type> out = new ArrayList<>();
        for (int k = ci + 1; k < hp.length; k++) {
            if (sugared(h, k)) {
                break;
            }
            out.add(hp[k]);
        }
        return out;
    }

    private static boolean sugared(MethodNode h, int k) {
        for (List<AnnotationNode>[] pa : java.util.Arrays.<List<AnnotationNode>[]>asList(h.visibleParameterAnnotations, h.invisibleParameterAnnotations)) {
            if (pa != null && k < pa.length && pa[k] != null
                    && pa[k].stream().anyMatch(a -> a.desc.startsWith("Lcom/llamalad7/mixinextras/sugar/"))) {
                return true;
            }
        }
        return false;
    }

    private static AnnotationNode paramAnnotation(MethodNode h, int k, String desc) {
        for (List<AnnotationNode>[] pa : java.util.Arrays.<List<AnnotationNode>[]>asList(h.visibleParameterAnnotations, h.invisibleParameterAnnotations)) {
            if (pa != null && k < pa.length && pa[k] != null) {
                for (AnnotationNode a : pa[k]) {
                    if (a.desc.equals(desc)) {
                        return a;
                    }
                }
            }
        }
        return null;
    }

    private static Type localType(Type p) {
        if (p.getSort() == Type.OBJECT && p.getInternalName().startsWith("com/llamalad7/mixinextras/sugar/ref/")) {
            return REFS.get(p.getInternalName());
        }
        return p;
    }

    private static void removeValue(AnnotationNode a, String key) {
        if (a.values == null) {
            return;
        }
        for (int i = 0; i + 1 < a.values.size(); i += 2) {
            if (key.equals(a.values.get(i))) {
                a.values.remove(i + 1);
                a.values.remove(i);
                return;
            }
        }
    }

    /** whether the captured types are the locals after the arguments at every point (null: not modelled) */
    private static Boolean captureFits(MixinRepair code, ClassNode owner, MethodNode m, Map<String, Object> av, JsonObject refmap,
            List<Type> want) {
        List<Integer> pts = points(m, av, refmap);
        Frame<BasicValue>[] fs = frames(code, owner, m);
        if (pts == null || pts.isEmpty() || fs == null) {
            return null;
        }
        int first = (m.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        for (Type t : Type.getArgumentTypes(m.desc)) {
            first += t.getSize();
        }
        for (int p : pts) {
            Frame<BasicValue> f = p < fs.length ? fs[p] : null;
            if (f == null) {
                continue;
            }
            List<Type> have = new ArrayList<>();
            for (int s = first; s < f.getLocals(); s++) {
                Type t = type(f.getLocal(s));
                if (t != null) {
                    have.add(t);
                }
            }
            if (have.size() < want.size()) {
                return false;
            }
            for (int i = 0; i < want.size(); i++) {
                if (!compatible(want.get(i), have.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * the @Local discriminator at every point: {1 if it finds its local at all points else 0, 1 if the type has exactly
     * one candidate at all points else 0} (LocalVariableDiscriminator: candidates from the first argument slot; ordinal
     * counts locals of the type from slot 0; index names a slot; implicit wants exactly one); null: not modelled
     */
    private static int[] pick(MixinRepair code, ClassNode owner, MethodNode m, Map<String, Object> av, JsonObject refmap, Type t,
            int ordinal, int index, boolean argsOnly) {
        List<Integer> pts = points(m, av, refmap);
        Frame<BasicValue>[] fs = frames(code, owner, m);
        if (pts == null || pts.isEmpty() || fs == null) {
            return null;
        }
        int base = (m.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        int argEnd = base;
        for (Type a : Type.getArgumentTypes(m.desc)) {
            argEnd += a.getSize();
        }
        boolean found = true;
        boolean unique = true;
        for (int p : pts) {
            Frame<BasicValue> f = p < fs.length ? fs[p] : null;
            if (f == null) {
                continue;
            }
            int ord = -1;
            int count = 0;
            boolean hit = false;
            int end = argsOnly ? Math.min(argEnd, f.getLocals()) : f.getLocals();
            for (int s = 0; s < end; s++) {
                Type lt = type(f.getLocal(s));
                if (lt == null || !compatible(t, lt)) {
                    continue;
                }
                ord++;
                if (s < base) {
                    continue;
                }
                count++;
                if (ordinal >= 0 ? ordinal == ord : index >= 0 && index == s) {
                    hit = true;
                }
            }
            unique &= count == 1;
            found &= ordinal >= 0 || index >= 0 ? hit : count == 1;
        }
        return new int[] {found ? 1 : 0, unique ? 1 : 0};
    }

    /** the frame index of each point the injector's @At finds (HEAD, RETURN, TAIL, INVOKE*, FIELD, NEW), null: not modelled */
    private static List<Integer> points(MethodNode m, Map<String, Object> av, JsonObject refmap) {
        if (m.instructions == null || m.instructions.size() == 0) {
            return null;
        }
        List<Object> ats = MixinRepair.list(av.get("at"));
        if (ats.size() != 1 || !(ats.get(0) instanceof Map<?, ?> am) || av.get("slice") != null) {
            return null;
        }
        String value = String.valueOf(am.get("value"));
        boolean after = am.get("shift") instanceof String[] sh && sh.length == 2 && sh[1].equals("AFTER")
            || value.equals("INVOKE_ASSIGN");
        if (am.get("shift") instanceof String[] sh && sh.length == 2 && !sh[1].equals("AFTER") && !sh[1].equals("BEFORE")) {
            return null;
        }
        AbstractInsnNode[] insns = m.instructions.toArray();
        List<Integer> out = new ArrayList<>();
        switch (value) {
            case "HEAD" -> {
                for (int i = 0; i < insns.length; i++) {
                    if (insns[i].getOpcode() >= 0) {
                        out.add(i);
                        break;
                    }
                }
            }
            case "RETURN", "TAIL" -> {
                for (int i = 0; i < insns.length; i++) {
                    int op = insns[i].getOpcode();
                    if (op >= Opcodes.IRETURN && op <= Opcodes.RETURN) {
                        out.add(i);
                    }
                }
                if (value.equals("TAIL") && !out.isEmpty()) {
                    out = List.of(out.get(out.size() - 1));
                }
            }
            case "INVOKE", "INVOKE_ASSIGN", "FIELD", "NEW" -> {
                MixinRepair.Anchor a = MixinRepair.parseAt(am, refmap);
                if (a == null || a == MixinRepair.POSITIONAL) {
                    return null;
                }
                int seen = -1;
                for (int i = 0; i < insns.length; i++) {
                    if (MixinRepair.matchesAnchor(insns[i], a) && (a.ordinal() < 0 || ++seen == a.ordinal())) {
                        out.add(after ? i + 1 : i);
                    }
                }
            }
            default -> {
                return null;
            }
        }
        return out;
    }

    /**
     * smx6 E8g, a @Local carried by a guarded descend (MixinRepair.stashed) from V's site in vm to W's site in the callee
     * into: per handler parameter the ordinal its @Local takes in into (-1 for a parameter that is not a @Local), or
     * null with why. A @Local is carried only by role: the local it picks at V's site (ordinal or implicit, as
     * LocalVariableDiscriminator counts) holds one definition there, a store fed by one producer instruction (a
     * method call or field read); in into exactly one local of the same type at W's site holds one definition, a store
     * fed by the same instruction (opcode, owner, name, descriptor, in W's spelling). Anything else -- an argument, an
     * index / name / argsOnly discriminator, a merged definition, no or several same-role locals -- is refused.
     */
    static int[] roleOrdinals(MixinRepair v, ClassNode vc, MethodNode vm, AbstractInsnNode vSite, MixinRepair w, ClassNode wc,
            MethodNode into, AbstractInsnNode wSite, MethodNode h, int ci, AnnotationNode an, List<String> why) {
        return roleOrdinals(v, vc, vm, vSite, w, wc, into, wSite, h, ci, an, why, false);
    }

    /**
     * As above; {@code inherited} (smixin12, Kilt E8i checkIsTNTBlock): producers compare by the class that declares
     * the member they call or read, each side resolved in its own jar (1.21 javac names ServerLevel as the owner of
     * Level.getBlockState, which ServerLevel inherits: the same call), and a @Local V picked implicitly that W's site
     * also holds exactly one local of its type for answers -2 (leave it implicit).
     */
    static int[] roleOrdinals(MixinRepair v, ClassNode vc, MethodNode vm, AbstractInsnNode vSite, MixinRepair w, ClassNode wc,
            MethodNode into, AbstractInsnNode wSite, MethodNode h, int ci, AnnotationNode an, List<String> why, boolean inherited) {
        Type[] hp = Type.getArgumentTypes(h.desc);
        int[] out = new int[hp.length];
        java.util.Arrays.fill(out, -1);
        Map<String, Object> av = MixinRepair.values(an);
        List<Object> ats = MixinRepair.list(av.get("at"));
        int shift = 0;
        if (ats.size() == 1 && ats.get(0) instanceof Map<?, ?> am && am.get("shift") instanceof String[] sh && sh.length == 2) {
            if (sh[1].equals("AFTER")) {
                shift = 1;
            } else if (!sh[1].equals("BEFORE")) {
                why.add("@Local role: the injector's @At shift " + sh[1] + " is not modelled");
                return null;
            }
        }
        int vAt = vm.instructions.indexOf(vSite) + shift;
        int wAt = into.instructions.indexOf(wSite) + shift;
        Frame<BasicValue>[] vt = frames(v, vc, vm);
        Frame<BasicValue>[] wt = frames(w, wc, into);
        Frame<SourceValue>[] vs = sources(vc.name, vm);
        Frame<SourceValue>[] ws = sources(wc.name, into);
        if (vt == null || wt == null || vs == null || ws == null || vt[vAt] == null || wt[wAt] == null || vs[vAt] == null || ws[wAt] == null) {
            why.add("@Local role: V's or W's frames at the site cannot be computed");
            return null;
        }
        for (int k = ci + 1; k < hp.length; k++) {
            AnnotationNode loc = paramAnnotation(h, k, LOCAL);
            if (loc == null) {
                why.add("@Local role: parameter " + k + " is not a @Local");
                return null;
            }
            Map<String, Object> lv = MixinRepair.values(loc);
            if (lv.get("index") != null || Boolean.TRUE.equals(lv.get("argsOnly"))
                    || lv.get("name") != null && !MixinRepair.list(lv.get("name")).isEmpty()) {
                why.add("@Local role: parameter " + k + " picks by index, name or argsOnly, not carried");
                return null;
            }
            int ordinal = lv.get("ordinal") instanceof Integer o ? o : -1;
            Type t = hp[k];
            Type tw = renamed(w, t);
            int vSlot = slotOf(vt[vAt], t, ordinal);
            if (vSlot < 0) {
                why.add("@Local role: " + t.getDescriptor() + (ordinal >= 0 ? " ordinal " + ordinal : " (implicit)")
                    + " picks no single local at V's site");
                return null;
            }
            String vRole = producer(vm, vs, vs[vAt].getLocal(vSlot), w, inherited ? v : null);
            if (vRole == null) {
                why.add("@Local role: V's local (slot " + vSlot + ") has no single store fed by one call or field read");
                return null;
            }
            int hit = -1;
            int hitOrd = -1;
            int ord = -1;
            Frame<BasicValue> f = wt[wAt];
            for (int s = 0; s < f.getLocals(); s++) {
                Type lt = type(f.getLocal(s));
                if (lt == null || !compatible(tw, lt)) {
                    continue;
                }
                ord++;
                if (vRole.equals(producer(into, ws, ws[wAt].getLocal(s), null, inherited ? w : null))) {
                    if (hit >= 0) {
                        why.add("@Local role: two locals in " + into.name + into.desc + " are fed by " + vRole);
                        return null;
                    }
                    hit = s;
                    hitOrd = ord;
                }
            }
            if (hit < 0) {
                why.add("@Local role: no " + tw.getDescriptor() + " local at W's site in " + into.name + into.desc + " is fed by " + vRole);
                return null;
            }
            out[k] = inherited && ordinal < 0 && ord == 0 ? -2 : hitOrd;
        }
        return out;
    }

    /** the class declaring owner's member name+desc (a method, or a field when desc has no parenthesis): owner, its superclasses, then interfaces */
    static String declaring(MixinRepair code, String owner, String name, String desc) {
        boolean field = !desc.startsWith("(");
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(List.of(owner));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            ClassNode cn = code.target(c);
            if (cn == null) {
                continue;
            }
            if (field ? cn.fields.stream().anyMatch(x -> x.name.equals(name) && x.desc.equals(desc))
                    : cn.methods.stream().anyMatch(x -> x.name.equals(name) && x.desc.equals(desc))) {
                return c;
            }
            if (cn.superName != null) {
                todo.addFirst(cn.superName);
            }
            todo.addAll(cn.interfaces);
        }
        return null;
    }

    /** the slot a @Local of type t picks in frame f (ordinal counted from slot 0; implicit: the one local of t), or -1 */
    private static int slotOf(Frame<BasicValue> f, Type t, int ordinal) {
        int ord = -1;
        int one = -1;
        int count = 0;
        for (int s = 0; s < f.getLocals(); s++) {
            Type lt = type(f.getLocal(s));
            if (lt == null || !compatible(t, lt)) {
                continue;
            }
            ord++;
            if (ordinal >= 0 && ord == ordinal) {
                return s;
            }
            count++;
            one = s;
        }
        return ordinal < 0 && count == 1 ? one : -1;
    }

    /**
     * the instruction feeding the one store a local holds, as "opcode owner.name desc" (in W's spelling when w is
     * given: the step's classRenames applied), or null: an argument, several stores, or a store of anything but one
     * method call or field read
     */
    private static String producer(MethodNode m, Frame<SourceValue>[] fs, SourceValue local, MixinRepair w, MixinRepair resolveIn) {
        if (local == null || local.insns.size() != 1) {
            return null;
        }
        AbstractInsnNode store = local.insns.iterator().next();
        int op = store.getOpcode();
        if (op < Opcodes.ISTORE || op > Opcodes.ASTORE) {
            return null;
        }
        Frame<SourceValue> sf = fs[m.instructions.indexOf(store)];
        if (sf == null || sf.getStackSize() == 0) {
            return null;
        }
        SourceValue top = sf.getStack(sf.getStackSize() - 1);
        if (top.insns.size() != 1) {
            return null;
        }
        AbstractInsnNode p = top.insns.iterator().next();
        String key;
        if (p instanceof org.objectweb.asm.tree.MethodInsnNode mi) {
            String o = resolveIn == null ? mi.owner : declaring(resolveIn, mi.owner, mi.name, mi.desc);
            if (o == null) {
                return null;
            }
            key = mi.getOpcode() + " " + o + "." + mi.name + " " + mi.desc;
        } else if (p instanceof org.objectweb.asm.tree.FieldInsnNode fi) {
            String o = resolveIn == null ? fi.owner : declaring(resolveIn, fi.owner, fi.name, fi.desc);
            if (o == null) {
                return null;
            }
            key = fi.getOpcode() + " " + o + "." + fi.name + " " + fi.desc;
        } else {
            return null;
        }
        return w == null || w.classRenames.isEmpty() ? key : MixinClassRenames.rename(key, w.classRenames);
    }

    private static final Map<MethodNode, Frame<SourceValue>[]> SOURCES = java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());

    private static Frame<SourceValue>[] sources(String owner, MethodNode m) {
        if (SOURCES.containsKey(m)) {
            return SOURCES.get(m);
        }
        Frame<SourceValue>[] out;
        try {
            out = new Analyzer<>(new SourceInterpreter()).analyze(owner, m);
        } catch (AnalyzerException | RuntimeException ex) {
            out = null;
        }
        SOURCES.put(m, out);
        return out;
    }

    private static Type type(BasicValue v) {
        return v == null || v == BasicValue.UNINITIALIZED_VALUE || v.getType() == null ? null : v.getType();
    }

    /** the handler's declared type against the verifier's: equal, or the verifier's merged reference type taken as fitting */
    private static boolean compatible(Type want, Type have) {
        if (want.equals(have)) {
            return true;
        }
        int ws = want.getSort();
        int hs = have.getSort();
        // the verifier keeps boolean / byte / char / short as int
        if (hs == Type.INT && (ws == Type.BOOLEAN || ws == Type.BYTE || ws == Type.CHAR || ws == Type.SHORT)) {
            return true;
        }
        return false;
    }

    private static final Map<MethodNode, Frame<BasicValue>[]> FRAMES = java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());

    private static Frame<BasicValue>[] frames(MixinRepair code, ClassNode owner, MethodNode m) {
        if (FRAMES.containsKey(m)) {
            return FRAMES.get(m);
        }
        Frame<BasicValue>[] out;
        try {
            List<Type> itfs = new ArrayList<>();
            for (String s : owner.interfaces) {
                itfs.add(Type.getObjectType(s));
            }
            Map<String, ClassNode> seen = new HashMap<>();
            SimpleVerifier sv = new SimpleVerifier(Opcodes.ASM9, Type.getObjectType(owner.name),
                    owner.superName == null ? null : Type.getObjectType(owner.superName), itfs,
                    (owner.access & Opcodes.ACC_INTERFACE) != 0) {
                private ClassNode cls(String n) {
                    return seen.computeIfAbsent(n, x -> code.target(x));
                }

                @Override
                protected boolean isInterface(Type t) {
                    ClassNode c = cls(t.getInternalName());
                    return c != null && (c.access & Opcodes.ACC_INTERFACE) != 0;
                }

                @Override
                protected Type getSuperClass(Type t) {
                    ClassNode c = cls(t.getInternalName());
                    return c == null ? (t.getInternalName().equals("java/lang/Object") ? null : Type.getObjectType("java/lang/Object"))
                        : c.superName == null ? null : Type.getObjectType(c.superName);
                }

                @Override
                protected Class<?> getClass(Type t) {
                    throw new IllegalStateException("not loaded: " + t);
                }

                @Override
                protected boolean isAssignableFrom(Type to, Type from) {
                    if (to.equals(from) || to.getInternalName().equals("java/lang/Object") || isInterface(to)) {
                        return true;
                    }
                    if (to.getSort() == Type.ARRAY || from.getSort() == Type.ARRAY) {
                        return to.getSort() == Type.ARRAY && from.getSort() == Type.ARRAY;
                    }
                    Set<String> walked = new HashSet<>();
                    for (String c = from.getInternalName(); c != null && walked.add(c); ) {
                        if (c.equals(to.getInternalName())) {
                            return true;
                        }
                        ClassNode n = cls(c);
                        if (n == null) {
                            return true; // unknown (a library class): taken as compatible
                        }
                        c = n.superName;
                    }
                    return false;
                }
            };
            out = new Analyzer<>(sv).analyze(owner.name, m);
        } catch (AnalyzerException | RuntimeException ex) {
            out = null;
        }
        FRAMES.put(m, out);
        return out;
    }
}
