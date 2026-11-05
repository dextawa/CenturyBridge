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
            ClassNode wOwner, ClassNode vOwner, MethodNode vTarget, JsonObject refmap, ClassNode mx) {
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
            Type tw = renamed(w, t);
            int[] vc = pick(v, vOwner, vTarget, av, refmap, t, ordinal, index, argsOnly);
            if (vc == null) {
                // smixin20: a point the model does not find (MIXINEXTRAS:EXPRESSION, a slice): an implicit @Local is
                // judged over the whole method -- V holds its one t local; W holding no local of t anywhere cannot bind
                boolean explicit = ordinal >= 0 || index >= 0;
                Set<Integer> vs = slotsAnywhere(v, vOwner, vTarget, t, argsOnly);
                if (vs == null || vs.isEmpty() || !explicit && vs.size() != 1) {
                    continue; // not modelled, or V holds no (implicit: not one) local of t: not trusted
                }
                boolean wNone = false;
                boolean unknown = false;
                for (MethodNode m : wTargets) {
                    Set<Integer> ws = slotsAnywhere(w, wOwner, m, tw, argsOnly);
                    if (ws == null) {
                        unknown = true;
                    } else if (ws.isEmpty()) {
                        wNone = true;
                    }
                }
                if (unknown || !wNone) {
                    continue;
                }
                String sub = explicit ? "picks by ordinal or index"
                    : subtypeFix(v, vOwner, vTarget, w, wOwner, wTargets, mx, h, k, t, tw, argsOnly, notes, fixes);
                if (sub == null) {
                    continue;
                }
                return new Verdict("@Local " + t.getDescriptor() + (explicit ? (index >= 0 ? " index " + index : " ordinal " + ordinal)
                    : " (implicit)") + " finds a local in V's " + vTarget.name + vTarget.desc
                    + "; W's holds no local declared " + tw.getDescriptor() + " (" + sub + ")", null, null);
            }
            if (vc[0] != 1) {
                continue; // does not fit V by the model: not trusted
            }
            boolean wFits = true;
            boolean wUnique = true;
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
            String sub = ordinal < 0 && index < 0
                ? subtypeFix(v, vOwner, vTarget, w, wOwner, wTargets, mx, h, k, t, tw, argsOnly, notes, fixes) : "picks by ordinal or index";
            if (sub == null) {
                continue;
            }
            return new Verdict("@Local " + t.getDescriptor() + (index >= 0 ? " index " + index : ordinal >= 0 ? " ordinal " + ordinal : " (implicit)")
                + " fits V's " + vTarget.name + vTarget.desc + " but not W's (the step moved or retyped the local; " + sub + ")", null, null);
        }
        if (fixes.isEmpty()) {
            return null;
        }
        return new Verdict(null, () -> fixes.forEach(Runnable::run), String.join("; ", notes));
    }

    /**
     * smixin20: an implicit @Local of t that binds on V and finds no t local on W is retyped to the one subtype local W
     * holds in the same role (subtypeByRole) when that subtype is then the one local of its type in every W target;
     * null with the fix queued (and a note), else the reason it is refused
     */
    private static String subtypeFix(MixinRepair v, ClassNode vOwner, MethodNode vTarget, MixinRepair w, ClassNode wOwner,
            List<MethodNode> wTargets, ClassNode mx, MethodNode h, int k, Type t, Type tw, boolean argsOnly, List<String> notes,
            List<Runnable> fixes) {
        if (wTargets.size() != 1) {
            return "the selector matches " + wTargets.size() + " W methods";
        }
        int slot = (h.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        Type[] hp = Type.getArgumentTypes(h.desc);
        for (int i = 0; i < k; i++) {
            slot += hp[i].getSize();
        }
        for (AbstractInsnNode n : h.instructions) {
            if (n instanceof org.objectweb.asm.tree.VarInsnNode vi && vi.var == slot && vi.getOpcode() == Opcodes.ASTORE) {
                return "the handler stores into the parameter";
            }
        }
        if (mx != null) {
            for (MethodNode m : mx.methods) {
                for (AbstractInsnNode n : m.instructions) {
                    if (n instanceof org.objectweb.asm.tree.MethodInsnNode mi && mi.name.equals(h.name) && mi.desc.equals(h.desc)
                            && mi.owner.equals(mx.name)) {
                        return "another method of the mixin calls the handler";
                    }
                }
            }
        }
        List<String> why = new ArrayList<>();
        Type u = subtypeByRole(v, vOwner, vTarget, w, wOwner, wTargets.get(0), t, tw, why);
        if (u == null) {
            return "no W local in its role: " + String.join("; ", why);
        }
        if (w.classRenames.containsKey(u.getInternalName())) {
            return u.getDescriptor() + " is a name the step's class rewrite renames";
        }
        Set<Integer> us = slotsAnywhere(w, wOwner, wTargets.get(0), u, argsOnly);
        if (us == null || us.size() != 1) {
            return u.getDescriptor() + " is not one local of W's method";
        }
        notes.add("@Local " + t.getDescriptor() + " -> " + u.getDescriptor() + " (W declares the local of the same role as the subtype; "
            + "the handler takes it)");
        fixes.add(() -> retypeParam(h, k, u));
        return null;
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
        Decl d = decl(code, owner, m);
        for (int p : pts) {
            Frame<BasicValue> f = p < fs.length ? fs[p] : null;
            if (f == null) {
                continue;
            }
            List<Type> have = new ArrayList<>();
            for (int s = first; s < f.getLocals(); s++) {
                Type t = d.at(p, s, f);
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
        Decl d = decl(code, owner, m);
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
                Type lt = s < base ? type(f.getLocal(s)) : d.at(p, s, f);
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

    /**
     * smixin14 (cb-smx6n SectionBuildCounter, 1.21.5 -> 1.21.6): why V's call site vSite and W's wSite (the same
     * member; MixinRepair matched both by one anchor) do not take their receiver and arguments in the same roles, or
     * null when they do. Each operand's role is the instruction producing it (SourceInterpreter): a call or field read
     * (opcode, owner, name, descriptor; V's in W's spelling through the step's classRenames), a checkcast (its type and
     * the role of what it casts), a constant, a NEW, this, an argument by index, or a local by the role of its one
     * store. A merged operand, a local with several stores, or anything else is not judged (a reason).
     */
    static String operandRolesDiffer(MixinRepair w, String vOwner, MethodNode vm, AbstractInsnNode vSite,
            String wOwner, MethodNode wm, AbstractInsnNode wSite) {
        if (!(vSite instanceof org.objectweb.asm.tree.MethodInsnNode vi) || !(wSite instanceof org.objectweb.asm.tree.MethodInsnNode wi)) {
            return "operand roles: the site is not a call";
        }
        int n = Type.getArgumentTypes(vi.desc).length + (vi.getOpcode() == Opcodes.INVOKESTATIC ? 0 : 1);
        int nw = Type.getArgumentTypes(wi.desc).length + (wi.getOpcode() == Opcodes.INVOKESTATIC ? 0 : 1);
        if (n != nw) {
            return "operand roles: V's call takes " + n + " operands, W's " + nw;
        }
        Frame<SourceValue>[] vs = sources(vOwner, vm);
        Frame<SourceValue>[] ws = sources(wOwner, wm);
        Frame<SourceValue> vf = vs == null ? null : vs[vm.instructions.indexOf(vSite)];
        Frame<SourceValue> wf = ws == null ? null : ws[wm.instructions.indexOf(wSite)];
        if (vf == null || wf == null) {
            return "operand roles: V's or W's frames at the site cannot be computed";
        }
        for (int k = 0; k < n; k++) {
            String vr = role(vm, vs, vf.getStack(vf.getStackSize() - n + k), 3);
            String wr = role(wm, ws, wf.getStack(wf.getStackSize() - n + k), 3);
            if (vr == null || wr == null) {
                return "operand roles: operand " + k + " has no single producer on " + (vr == null ? "V" : "W") + "'s side";
            }
            if (!w.classRenames.isEmpty()) {
                vr = MixinClassRenames.rename(vr, w.classRenames);
            }
            if (!vr.equals(wr)) {
                return "operand roles: operand " + k + " is " + vr + " on V, " + wr + " on W";
            }
        }
        return null;
    }

    /** the role of a stack value (see operandRolesDiffer), or null when it has no single producer */
    private static String role(MethodNode m, Frame<SourceValue>[] fs, SourceValue v, int depth) {
        if (v == null || v.insns.size() != 1 || depth < 0) {
            return null;
        }
        AbstractInsnNode p = v.insns.iterator().next();
        int op = p.getOpcode();
        if (p instanceof org.objectweb.asm.tree.MethodInsnNode mi) {
            return op + " " + mi.owner + "." + mi.name + " " + mi.desc;
        }
        if (p instanceof org.objectweb.asm.tree.FieldInsnNode fi) {
            return op + " " + fi.owner + "." + fi.name + " " + fi.desc;
        }
        if (p instanceof org.objectweb.asm.tree.TypeInsnNode ti) {
            if (op == Opcodes.NEW) {
                return "NEW " + ti.desc;
            }
            if (op != Opcodes.CHECKCAST) {
                return null;
            }
            Frame<SourceValue> f = fs[m.instructions.indexOf(p)];
            String in = f == null ? null : role(m, fs, f.getStack(f.getStackSize() - 1), depth - 1);
            return in == null ? null : "CHECKCAST " + ti.desc + " <- " + in;
        }
        Object c = SiteMatch.constantOf(p);
        if (c != null) {
            return "const " + c;
        }
        if (p instanceof org.objectweb.asm.tree.VarInsnNode vi && op >= Opcodes.ILOAD && op <= Opcodes.ALOAD) {
            Frame<SourceValue> f = fs[m.instructions.indexOf(p)];
            if (f == null) {
                return null;
            }
            SourceValue l = f.getLocal(vi.var);
            boolean isStatic = (m.access & Opcodes.ACC_STATIC) != 0;
            int argSize = (Type.getArgumentsAndReturnSizes(m.desc) >> 2) - (isStatic ? 1 : 0);
            if (l.insns.isEmpty() && vi.var < argSize) {
                return !isStatic && vi.var == 0 ? "this" : "arg slot " + vi.var;
            }
            if (l.insns.size() != 1) {
                return null;
            }
            AbstractInsnNode store = l.insns.iterator().next();
            Frame<SourceValue> sf = fs[m.instructions.indexOf(store)];
            if (store.getOpcode() < Opcodes.ISTORE || store.getOpcode() > Opcodes.ASTORE || sf == null || sf.getStackSize() == 0) {
                return null;
            }
            String in = role(m, fs, sf.getStack(sf.getStackSize() - 1), depth - 1);
            return in == null ? null : "local <- " + in;
        }
        return null;
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

    /**
     * smixin20 (Kilt loss 6): the local types as Mixin reads them. Where the method carries a local variable table (a
     * 26.x jar keeps its own) or the game jar carries one for it (GameLvt: the obfuscated jar beside an intermediary code
     * jar), Mixin's LocalVariableDiscriminator types each slot by the table's declared type and matches by Type.equals:
     * that type, or none when no entry covers the point. Only without a table is the verifier's type the model.
     */
    private record Decl(MethodNode m, GameLvt.Table game, int[] ords) {
        boolean tabled() {
            return game != null || m.localVariables != null && !m.localVariables.isEmpty();
        }

        Type at(int p, int slot, Frame<BasicValue> f) {
            if (m.localVariables != null && !m.localVariables.isEmpty()) {
                for (org.objectweb.asm.tree.LocalVariableNode lv : m.localVariables) {
                    if (lv.index == slot && m.instructions.indexOf(lv.start) <= p && p < m.instructions.indexOf(lv.end)) {
                        return Type.getType(lv.desc);
                    }
                }
                return null;
            }
            if (game != null) {
                return game.at(slot, ords[p]);
            }
            return f == null ? null : type(f.getLocal(slot));
        }
    }

    private static Decl decl(MixinRepair code, ClassNode owner, MethodNode m) {
        GameLvt.Table g = null;
        if ((m.localVariables == null || m.localVariables.isEmpty()) && code.lvt() != null) {
            g = code.lvt().of(owner.name, m);
        }
        int[] ords = null;
        if (g != null) {
            AbstractInsnNode[] a = m.instructions.toArray();
            ords = new int[a.length + 1];
            int n = 0;
            for (int i = 0; i < a.length; i++) {
                ords[i] = n;
                if (a[i].getOpcode() >= 0) {
                    n++;
                }
            }
            ords[a.length] = n;
        }
        return new Decl(m, g, ords);
    }

    /**
     * the slots past the receiver (and past the arguments unless argsOnly) whose declared type is t at some instruction
     * of m: null when not modelled (no frames), else the distinct slots
     */
    private static Set<Integer> slotsAnywhere(MixinRepair code, ClassNode owner, MethodNode m, Type t, boolean argsOnly) {
        Frame<BasicValue>[] fs = frames(code, owner, m);
        Decl d = fs == null ? null : decl(code, owner, m);
        if (d == null || !d.tabled()) {
            return null; // the verifier's types are no model of what Mixin reads over a whole method
        }
        int base = (m.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        int argEnd = base;
        for (Type a : Type.getArgumentTypes(m.desc)) {
            argEnd += a.getSize();
        }
        Set<Integer> out = new java.util.TreeSet<>();
        for (int p = 0; p < fs.length; p++) {
            Frame<BasicValue> f = fs[p];
            if (f == null) {
                continue;
            }
            int end = argsOnly ? Math.min(argEnd, f.getLocals()) : f.getLocals();
            for (int s = base; s < end; s++) {
                Type lt = d.at(p, s, f);
                if (lt != null && compatible(t, lt)) {
                    out.add(s);
                }
            }
        }
        return out;
    }

    /**
     * smixin20: a @Local of type t that W's method holds as no local of t can take the one W local whose declared type
     * extends t when it plays the same role as V's one t local: each side has exactly one store into a local of that
     * type in the method, fed by the same call or field read (owners compared by the declaring class, V's spelled
     * through the step's renames). The handler then takes the subtype: everything it does with a t it can do with it.
     * Returns that type, or null (why says why).
     */
    private static Type subtypeByRole(MixinRepair v, ClassNode vOwner, MethodNode vm, MixinRepair w, ClassNode wOwner,
            MethodNode wm, Type t, Type tw, List<String> why) {
        if (t.getSort() != Type.OBJECT) {
            why.add("not a reference type");
            return null;
        }
        String vRole = oneStoreRole(v, vOwner, vm, x -> x.equals(t), w, why);
        if (vRole == null) {
            return null;
        }
        Type[] hit = new Type[1];
        String wRole = oneStoreRole(w, wOwner, wm, x -> {
            if (x.getSort() == Type.OBJECT && !x.equals(tw) && extendsClass(w, x.getInternalName(), tw.getInternalName())) {
                if (hit[0] == null || hit[0].equals(x)) {
                    hit[0] = x;
                    return true;
                }
            }
            return false;
        }, null, why);
        if (wRole == null) {
            return null;
        }
        if (!vRole.equals(wRole)) {
            why.add("V's " + t.getDescriptor() + " local is fed by " + vRole + ", W's " + hit[0].getDescriptor() + " by " + wRole);
            return null;
        }
        return hit[0];
    }

    /** the role of the one store into a local whose declared type passes want, or null (why) */
    private static String oneStoreRole(MixinRepair code, ClassNode owner, MethodNode m, java.util.function.Predicate<Type> want,
            MixinRepair spellIn, List<String> why) {
        Frame<BasicValue>[] fs = frames(code, owner, m);
        Frame<SourceValue>[] ss = sources(owner.name, m);
        if (fs == null || ss == null) {
            why.add("frames of " + m.name + m.desc + " cannot be computed");
            return null;
        }
        Decl d = decl(code, owner, m);
        if (!d.tabled()) {
            why.add(m.name + m.desc + " has no local variable table to read declared types from");
            return null;
        }
        AbstractInsnNode[] a = m.instructions.toArray();
        String role = null;
        for (int i = 0; i + 1 < a.length; i++) {
            if (!(a[i] instanceof org.objectweb.asm.tree.VarInsnNode vi) || vi.getOpcode() != Opcodes.ASTORE) {
                continue;
            }
            Type lt = d.at(i + 1, vi.var, fs[i + 1]);
            if (lt == null || !want.test(lt)) {
                continue;
            }
            if (role != null) {
                why.add("two stores into such a local in " + m.name + m.desc);
                return null;
            }
            Frame<SourceValue> f = ss[i];
            String r = f == null || f.getStackSize() == 0 ? null : producerOf(f.getStack(f.getStackSize() - 1), code, spellIn);
            if (r == null) {
                why.add("the local's store in " + m.name + m.desc + " is not fed by one call or field read");
                return null;
            }
            role = r;
        }
        if (role == null) {
            why.add("no such local in " + m.name + m.desc);
        }
        return role;
    }

    private static String producerOf(SourceValue top, MixinRepair resolveIn, MixinRepair spellIn) {
        if (top.insns.size() != 1) {
            return null;
        }
        AbstractInsnNode p = top.insns.iterator().next();
        String key;
        if (p instanceof org.objectweb.asm.tree.MethodInsnNode mi) {
            String o = declaring(resolveIn, mi.owner, mi.name, mi.desc);
            key = mi.getOpcode() + " " + (o == null ? mi.owner : o) + "." + mi.name + " " + mi.desc;
        } else if (p instanceof org.objectweb.asm.tree.FieldInsnNode fi) {
            String o = declaring(resolveIn, fi.owner, fi.name, fi.desc);
            key = fi.getOpcode() + " " + (o == null ? fi.owner : o) + "." + fi.name + " " + fi.desc;
        } else {
            return null;
        }
        return spellIn == null || spellIn.classRenames.isEmpty() ? key : MixinClassRenames.rename(key, spellIn.classRenames);
    }

    /** whether class sub extends class sup (superclasses only, as W's code jar has them) */
    private static boolean extendsClass(MixinRepair code, String sub, String sup) {
        Set<String> seen = new HashSet<>();
        for (String c = sub; c != null && seen.add(c); ) {
            if (c.equals(sup)) {
                return true;
            }
            ClassNode n = code.target(c);
            c = n == null ? null : n.superName;
        }
        return false;
    }

    /** the handler's parameter k retyped to u (descriptor and its own local table) */
    private static void retypeParam(MethodNode h, int k, Type u) {
        Type[] ps = Type.getArgumentTypes(h.desc);
        String old = ps[k].getDescriptor();
        ps[k] = u;
        h.desc = Type.getMethodDescriptor(Type.getReturnType(h.desc), ps);
        if (h.signature != null) {
            h.signature = null; // the generic signature names the old type; erasure is all Mixin reads
        }
        if (h.localVariables != null) {
            int slot = (h.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
            for (int i = 0; i < k; i++) {
                slot += Type.getArgumentTypes(h.desc)[i].getSize();
            }
            for (org.objectweb.asm.tree.LocalVariableNode lv : h.localVariables) {
                if (lv.index == slot && lv.desc.equals(old)) {
                    lv.desc = u.getDescriptor();
                    lv.signature = null;
                }
            }
        }
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
