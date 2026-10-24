package top.dext.centurybridge.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * Which occurrence of an anchor in W's body is the counterpart of which occurrence in V's: the
 * evidence behind an ordinal remap. A mixin pinned to the n-th call of X injects at whatever W's
 * n-th call of X is; when W added or removed a call of X before it, that is another site.
 *
 * Each occurrence is known by its context: the significant instructions around it (calls and field
 * accesses by owner.name, type instructions, constants, invokedynamic names, returns/throws). A V
 * occurrence and a W occurrence are counterparts when their context of width k is the same and no
 * other unmatched occurrence on either side has that context; the widest context (k = 4) is tried
 * first, down to k = 1. What context leaves unmatched is matched by the two bodies' alignment (the
 * longest common subsequence of their significant instructions) where the occurrence and both its
 * neighbours are aligned in a row. A match out of order with the matches before it is dropped. Every
 * occurrence with no unique counterpart stays unmatched (-1): never guessed.
 */
public final class SiteMatch {
    private SiteMatch() {}

    public static List<AbstractInsnNode> occurrences(MethodNode m, Predicate<AbstractInsnNode> p) {
        List<AbstractInsnNode> out = new ArrayList<>();
        if (m.instructions != null) {
            for (AbstractInsnNode in : m.instructions) {
                if (p.test(in)) out.add(in);
            }
        }
        return out;
    }

    /** for each V occurrence, the index of its W counterpart in {@code wo}, or -1 */
    public static int[] match(MethodNode vm, List<AbstractInsnNode> vo, MethodNode wm, List<AbstractInsnNode> wo) {
        return match(vm, vo, wm, wo, 1, true);
    }

    /**
     * As {@link #match(MethodNode, List, MethodNode, List)}, with contexts no narrower than minK and the
     * alignment fallback only when asked: for a W method other than V's own (descend), where order
     * protects nothing and a narrow context can be another statement of the same shape.
     */
    public static int[] match(MethodNode vm, List<AbstractInsnNode> vo, MethodNode wm, List<AbstractInsnNode> wo, int minK, boolean alignment) {
        int[] map = new int[vo.size()];
        Arrays.fill(map, -1);
        boolean[] taken = new boolean[wo.size()];
        // the strongest evidence first: the string constants the occurrence (or what consumes its value) takes
        boolean[] byKey = new boolean[vo.size()];
        matchByKey(vm, vo, wm, wo, map, taken, byKey);
        List<String> vs = sig(vm), ws = sig(wm);
        List<Integer> vi = sigIndex(vm, vo), wi = sigIndex(wm, wo);
        for (int k = 4; k >= minK; k--) {
            Map<String, List<Integer>> wctx = new HashMap<>();
            for (int j = 0; j < wo.size(); j++) {
                if (!taken[j]) wctx.computeIfAbsent(ctx(ws, wi.get(j), k), x -> new ArrayList<>()).add(j);
            }
            Map<String, List<Integer>> vctx = new HashMap<>();
            for (int i = 0; i < vo.size(); i++) {
                if (map[i] < 0) vctx.computeIfAbsent(ctx(vs, vi.get(i), k), x -> new ArrayList<>()).add(i);
            }
            for (Map.Entry<String, List<Integer>> e : vctx.entrySet()) {
                List<Integer> w = wctx.get(e.getKey());
                if (e.getValue().size() == 1 && w != null && w.size() == 1) {
                    map[e.getValue().get(0)] = w.get(0);
                    taken[w.get(0)] = true;
                }
            }
        }
        // what context alone leaves: the two bodies' alignment (longest common subsequence of their
        // significant instructions), where the occurrence and both its neighbours are aligned in a row
        int[] aligned = alignment ? align(vs, ws) : null;
        if (aligned != null) {
            for (int i = 0; i < vo.size(); i++) {
                if (map[i] >= 0) continue;
                int p = vi.get(i);
                int q = aligned[p];
                if (q < 0 || p == 0 || p + 1 >= vs.size() || aligned[p - 1] != q - 1 || aligned[p + 1] != q + 1) continue;
                int j = wi.indexOf(q);
                if (j >= 0 && !taken[j]) {
                    map[i] = j;
                    taken[j] = true;
                }
            }
        }
        int last = -1;
        for (int i = 0; i < map.length; i++) {
            if (map[i] < 0 || byKey[i]) continue; // a match by key holds where W reordered the statements
            if (map[i] < last) map[i] = -1;
            else last = map[i];
        }
        return map;
    }

    // ------------------------------------------------------------------ argument keys

    /**
     * Matches occurrences by their argument key ({@link #keys}): a V occurrence and a W occurrence
     * whose keys are equal, where V and W hold the same number of occurrences with that key (the n-th
     * with a key is the n-th's counterpart -- the n-th addBox of the "left_sleeve" part).
     */
    private static void matchByKey(MethodNode vm, List<AbstractInsnNode> vo, MethodNode wm, List<AbstractInsnNode> wo,
                                   int[] map, boolean[] taken, boolean[] byKey) {
        if (vo.isEmpty() || wo.isEmpty()) return;
        Map<AbstractInsnNode, String> vk = keys(vm), wk = keys(wm);
        Map<String, List<Integer>> vg = new HashMap<>(), wg = new HashMap<>();
        for (int i = 0; i < vo.size(); i++) {
            String k = vk.get(vo.get(i));
            if (k != null) vg.computeIfAbsent(k, x -> new ArrayList<>()).add(i);
        }
        for (int j = 0; j < wo.size(); j++) {
            String k = wk.get(wo.get(j));
            if (k != null) wg.computeIfAbsent(k, x -> new ArrayList<>()).add(j);
        }
        for (Map.Entry<String, List<Integer>> e : vg.entrySet()) {
            List<Integer> w = wg.get(e.getKey());
            if (w == null || w.size() != e.getValue().size()) continue;
            for (int n = 0; n < w.size(); n++) {
                map[e.getValue().get(n)] = w.get(n);
                taken[w.get(n)] = true;
                byKey[e.getValue().get(n)] = true;
            }
        }
    }

    /**
     * Each call / field instruction's argument key, where it has one: the string constants it takes as
     * operands ({@code addOrReplaceChild("left_sleeve", ..)}), or else those of the first instruction
     * down its value's chain of consumers that takes any (a CubeListBuilder's addBox, whose builder the
     * "left_sleeve" call takes), with that instruction's owner.name. A string constant is a statement's
     * own name for what it builds; the key survives W reordering the statements or reshaping their
     * other arguments.
     */
    static Map<AbstractInsnNode, String> keys(MethodNode m) {
        Map<AbstractInsnNode, String> cached = KEYS.get(m);
        if (cached != null) return cached;
        Map<AbstractInsnNode, String> out = new java.util.IdentityHashMap<>();
        Flow f = flowOf(m);
        if (f != null) {
            for (AbstractInsnNode in : m.instructions) {
                if (!(in instanceof MethodInsnNode) && !(in instanceof FieldInsnNode)) continue;
                AbstractInsnNode c = in;
                for (int hop = 0; c != null && hop < 32; hop++) {
                    String s = f.strings.get(c);
                    if (s != null) {
                        out.put(in, (hop == 0 ? "" : sigOf(c) + "<-") + s);
                        break;
                    }
                    c = f.consumer.get(c);
                }
            }
        }
        KEYS.put(m, out);
        return out;
    }

    private static final Map<MethodNode, Map<AbstractInsnNode, String>> KEYS = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /** who takes each instruction's value (when exactly one instruction does), and each call's string operands */
    private record Flow(Map<AbstractInsnNode, AbstractInsnNode> consumer, Map<AbstractInsnNode, String> strings,
                        Map<AbstractInsnNode, AbstractInsnNode[]> producers) {}

    private static final Map<MethodNode, Flow> FLOWS = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private static Flow flowOf(MethodNode m) {
        if (FLOWS.containsKey(m)) return FLOWS.get(m);
        Flow f = flow(m);
        FLOWS.put(m, f);
        return f;
    }

    /**
     * The W instruction standing in V's site's slot: V's site is an operand of one consumer that has an
     * argument key ({@link #keys}), the only V instruction of its owner.name with that key; W has exactly
     * one such consumer, and what feeds the same operand there is a single instruction accepted by
     * {@code candidate} (PartPose.offset(..) re-described as offsetAndRotation(..) in the same
     * addOrReplaceChild("ear", ..) slot). Null otherwise.
     */
    public static AbstractInsnNode slotCounterpart(MethodNode vm, AbstractInsnNode site, MethodNode wm, Predicate<AbstractInsnNode> candidate) {
        return slotCounterpart(vm, site, wm, candidate, false);
    }

    /**
     * As above; with {@code place}, a position only (a positional @Inject): where W feeds the slot a method
     * reference instead (Blocks.register("air", AirBlock::new, ..) for register("air", new AirBlock(..))),
     * V's site now runs inside the keyed consumer, and the consumer's call is the place just before it.
     */
    public static AbstractInsnNode slotCounterpart(MethodNode vm, AbstractInsnNode site, MethodNode wm, Predicate<AbstractInsnNode> candidate,
                                                   boolean place) {
        Flow vf = flowOf(vm), wf = flowOf(wm);
        if (vf == null || wf == null) return null;
        // a constructor call produces nothing itself: the value is its NEW's
        AbstractInsnNode value = site instanceof MethodInsnNode sm && sm.name.equals("<init>") ? newOf(sm) : site;
        AbstractInsnNode c = value == null ? null : vf.consumer.get(value);
        if (c == null) return null;
        AbstractInsnNode[] vp = vf.producers.get(c);
        int slot = -1;
        for (int a = 0; vp != null && a < vp.length; a++) if (vp[a] == value) slot = a;
        String key = keys(vm).get(c);
        if (slot < 0 || key == null) return null;
        String cs = sigOf(c);
        if (count(keys(vm), key, cs) != 1) return null;
        AbstractInsnNode wc = only(keys(wm), key, cs);
        if (wc != null && value == site) {
            AbstractInsnNode[] wp = wf.producers.get(wc);
            if (wp != null && wp.length == vp.length && wp[slot] != null && candidate.test(wp[slot])) return wp[slot];
        }
        if (!place || value == site) return null;
        // a position: V's construction now runs inside the keyed consumer, handed a method reference (the consumer
        // may be another overload: Blocks.register(String, Block) -> register(String, Function, Properties))
        if (wc == null && count(keys(vm), key, null) == 1) wc = only(keys(wm), key, null);
        if (!(wc instanceof MethodInsnNode)) return null;
        AbstractInsnNode[] wp = wf.producers.get(wc);
        for (int a = 0; wp != null && a < wp.length; a++) {
            if (wp[a] instanceof InvokeDynamicInsnNode) return wc;
        }
        return null;
    }

    /** how many instructions have the key (and owner.name sig, unless null) */
    private static int count(Map<AbstractInsnNode, String> keys, String key, String sig) {
        int n = 0;
        for (Map.Entry<AbstractInsnNode, String> e : keys.entrySet()) {
            if (e.getValue().equals(key) && (sig == null || sig.equals(sigOf(e.getKey())))) n++;
        }
        return n;
    }

    /** the one instruction with the key (and sig, unless null), or null */
    private static AbstractInsnNode only(Map<AbstractInsnNode, String> keys, String key, String sig) {
        AbstractInsnNode hit = null;
        for (Map.Entry<AbstractInsnNode, String> e : keys.entrySet()) {
            if (e.getValue().equals(key) && (sig == null || sig.equals(sigOf(e.getKey())))) {
                if (hit != null) return null;
                hit = e.getKey();
            }
        }
        return hit;
    }

    /** the NEW a constructor call initializes: the nearest NEW of its class before it, nested constructions skipped */
    private static AbstractInsnNode newOf(MethodInsnNode init) {
        int depth = 0;
        for (AbstractInsnNode in = init.getPrevious(); in != null; in = in.getPrevious()) {
            if (in instanceof MethodInsnNode mi && mi.name.equals("<init>") && mi.getOpcode() == Opcodes.INVOKESPECIAL) {
                depth++;
            } else if (in instanceof TypeInsnNode ti && ti.getOpcode() == Opcodes.NEW) {
                if (depth == 0) return ti.desc.equals(init.owner) ? ti : null;
                depth--;
            }
        }
        return null;
    }

    private static Flow flow(MethodNode m) {
        if (m.instructions == null || m.instructions.size() == 0) return null;
        org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue>[] frames;
        try {
            frames = new org.objectweb.asm.tree.analysis.Analyzer<>(new org.objectweb.asm.tree.analysis.SourceInterpreter())
                .analyze("cb/Flow", m);
        } catch (org.objectweb.asm.tree.analysis.AnalyzerException | RuntimeException ex) {
            return null;
        }
        Map<AbstractInsnNode, AbstractInsnNode> consumer = new java.util.IdentityHashMap<>();
        Set<AbstractInsnNode> shared = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        Map<AbstractInsnNode, String> strings = new java.util.IdentityHashMap<>();
        Map<AbstractInsnNode, AbstractInsnNode[]> producers = new java.util.IdentityHashMap<>();
        for (int i = 0; i < m.instructions.size(); i++) {
            AbstractInsnNode in = m.instructions.get(i);
            var fr = frames[i];
            if (fr == null) continue;
            int n = operands(in);
            if (n <= 0 || n > fr.getStackSize()) continue;
            StringBuilder s = new StringBuilder();
            AbstractInsnNode[] ps = new AbstractInsnNode[n];
            producers.put(in, ps);
            for (int a = 0; a < n; a++) {
                var v = fr.getStack(fr.getStackSize() - n + a);
                if (v.insns.size() != 1) continue;
                AbstractInsnNode p = v.insns.iterator().next();
                ps[a] = p;
                if (consumer.containsKey(p) && consumer.get(p) != in) shared.add(p);
                consumer.put(p, in);
                if (p instanceof LdcInsnNode ldc && ldc.cst instanceof String str) {
                    s.append('"').append(str).append('"').append('@').append(a).append(';');
                }
            }
            if (s.length() > 0) strings.put(in, s.toString());
        }
        for (AbstractInsnNode p : shared) consumer.remove(p);
        return new Flow(consumer, strings, producers);
    }

    /** stack operands a call or field instruction takes (receiver included); 0 for others */
    private static int operands(AbstractInsnNode in) {
        if (in instanceof MethodInsnNode mi) {
            return Type.getArgumentTypes(mi.desc).length + (mi.getOpcode() == Opcodes.INVOKESTATIC ? 0 : 1);
        }
        if (in instanceof FieldInsnNode) {
            return switch (in.getOpcode()) {
                case Opcodes.PUTFIELD -> 2;
                case Opcodes.GETFIELD, Opcodes.PUTSTATIC -> 1;
                default -> 0;
            };
        }
        return 0;
    }

    /**
     * The W instruction standing where V's {@code site} stood when the site itself is gone from W: the
     * one W instruction accepted by {@code candidate} whose significant neighbours (k before, k after,
     * the instruction itself left out) are V's site's, for the widest k from 4 down to 2 that finds any,
     * counting only contexts with a neighbour other than the body's edges and returns.
     * Null when no such instruction, or when two or more stand there, or when another V instruction of
     * the site's kind has the same neighbours (V's site is then not singled out either). When no width
     * finds one, the instruction filling V's site's operand slot in the same keyed statement
     * ({@link #slotCounterpart}).
     */
    public static AbstractInsnNode counterpart(MethodNode vm, AbstractInsnNode site, MethodNode wm, Predicate<AbstractInsnNode> candidate) {
        return counterpart(vm, site, wm, candidate, false);
    }

    /** as above; {@code place}: a position only is asked for (see {@link #slotCounterpart(MethodNode, AbstractInsnNode, MethodNode, Predicate, boolean)}) */
    public static AbstractInsnNode counterpart(MethodNode vm, AbstractInsnNode site, MethodNode wm, Predicate<AbstractInsnNode> candidate,
                                               boolean place) {
        List<String> vs = sig(vm), ws = sig(wm);
        List<AbstractInsnNode> vsi = sigInsns(vm), wsi = sigInsns(wm);
        int p = vsi.indexOf(site);
        if (p < 0) return null;
        for (int k = 4; k >= 2; k--) {
            String key = around(vs, p, k);
            if (!informative(vs, p, k)) continue; // only the body's edges and returns around it: no evidence
            for (int r = 0; r < vsi.size(); r++) {
                if (r != p && candidate.test(vsi.get(r)) && around(vs, r, k).equals(key)) return null;
            }
            AbstractInsnNode hit = null;
            int hits = 0;
            for (int q = 0; q < wsi.size(); q++) {
                if (candidate.test(wsi.get(q)) && around(ws, q, k).equals(key)) {
                    hit = wsi.get(q);
                    hits++;
                }
            }
            if (hits == 1) return hit;
            if (hits > 1) return null;
        }
        // the neighbours changed around it (its own operands re-described): the slot it fills in a keyed statement
        return slotCounterpart(vm, site, wm, candidate, place);
    }

    /**
     * A W instruction of the same anchor kind as V's site: a call (a constructor call for one), the
     * same field opcode, an allocation, a constant of the same type.
     */
    public static boolean sameKind(AbstractInsnNode site, AbstractInsnNode in) {
        if (site instanceof MethodInsnNode sm) {
            return in instanceof MethodInsnNode mi && mi.name.equals("<init>") == sm.name.equals("<init>");
        }
        if (site instanceof FieldInsnNode) {
            return in instanceof FieldInsnNode && in.getOpcode() == site.getOpcode();
        }
        if (site instanceof TypeInsnNode && site.getOpcode() == Opcodes.NEW) {
            return in instanceof TypeInsnNode && in.getOpcode() == Opcodes.NEW;
        }
        Object c = constantOf(site);
        Object d = constantOf(in);
        return c != null && d != null && !(c instanceof Type) && c.getClass() == d.getClass();
    }

    /** a read of a value: a call returning one (no constructor), or a GETFIELD / GETSTATIC */
    public static boolean valueRead(AbstractInsnNode in) {
        if (in instanceof MethodInsnNode mi) {
            return !mi.name.equals("<init>") && Type.getReturnType(mi.desc).getSort() != Type.VOID;
        }
        return in instanceof FieldInsnNode && (in.getOpcode() == Opcodes.GETFIELD || in.getOpcode() == Opcodes.GETSTATIC);
    }

    /** some neighbour within k is a call, field access, type instruction, constant or invokedynamic */
    private static boolean informative(List<String> s, int at, int k) {
        for (int d = -k; d <= k; d++) {
            int p = at + d;
            if (d != 0 && p >= 0 && p < s.size() && !s.get(p).startsWith("ret")) return true;
        }
        return false;
    }

    /** the context of position at, without the instruction itself */
    private static String around(List<String> s, int at, int k) {
        StringBuilder b = new StringBuilder();
        for (int d = -k; d <= k; d++) {
            if (d == 0) {
                b.append("*|");
                continue;
            }
            int p = at + d;
            b.append(p < 0 || p >= s.size() ? "^" : s.get(p)).append('|');
        }
        return b.toString();
    }

    private static List<AbstractInsnNode> sigInsns(MethodNode m) {
        List<AbstractInsnNode> out = new ArrayList<>();
        if (m.instructions != null) {
            for (AbstractInsnNode in : m.instructions) {
                if (sigOf(in) != null) out.add(in);
            }
        }
        return out;
    }

    private static String ctx(List<String> s, int at, int k) {
        StringBuilder b = new StringBuilder();
        for (int d = -k; d <= k; d++) {
            int p = at + d;
            b.append(p < 0 || p >= s.size() ? "^" : s.get(p)).append('|');
        }
        return b.toString();
    }

    /**
     * For each position of a, the position of b it is aligned with by a longest common subsequence
     * (-1: not aligned), ties broken the same way every time; null when the bodies are too long.
     */
    public static int[] align(List<String> a, List<String> b) {
        int n = a.size(), m = b.size();
        if ((long) n * m > 4_000_000L) return null;
        int[][] t = new int[n + 1][m + 1];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                t[i][j] = a.get(i).equals(b.get(j)) ? t[i + 1][j + 1] + 1 : Math.max(t[i + 1][j], t[i][j + 1]);
            }
        }
        int[] out = new int[n];
        Arrays.fill(out, -1);
        int i = 0, j = 0;
        while (i < n && j < m) {
            if (a.get(i).equals(b.get(j))) {
                out[i++] = j++;
            } else if (t[i + 1][j] >= t[i][j + 1]) {
                i++;
            } else {
                j++;
            }
        }
        return out;
    }

    /** 2 x longest common subsequence / both lengths: 1 for the same sequence (0 when too long to compare) */
    public static double similarity(List<String> a, List<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 1;
        int n = a.size(), m = b.size();
        if ((long) n * m > 4_000_000L) return 0;
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) cur[j] = a.get(i - 1).equals(b.get(j - 1)) ? prev[j - 1] + 1 : Math.max(prev[j], cur[j - 1]);
            int[] t = prev;
            prev = cur;
            cur = t;
        }
        return 2.0 * prev[m] / (n + m);
    }

    /**
     * As {@link #sig}, with the class's own members known by kind, arity and the sort of their type instead
     * of name and descriptor: two
     * releases that name the same private helper differently (past the namespace wall the table lists
     * only what it could match, so a 26.1 helper keeps its own name while 1.21.11's is method_NNNN) still
     * line up. For comparing whole bodies (body follow), not for anchor contexts.
     */
    public static List<String> sigSelf(MethodNode m, String owner) {
        List<String> out = new ArrayList<>();
        if (m.instructions == null) return out;
        for (AbstractInsnNode in : m.instructions) {
            String s;
            if (in instanceof MethodInsnNode mi && mi.owner.equals(owner)) {
                // by arity and the sort of its answer: the classes in a descriptor are renamed / retyped too
                s = "self/" + Type.getArgumentTypes(mi.desc).length + Type.getReturnType(mi.desc).getSort();
            } else if (in instanceof FieldInsnNode fi && fi.owner.equals(owner)) {
                s = fi.getOpcode() + "self:" + Type.getType(fi.desc).getSort();
            } else {
                s = sigOf(in);
            }
            if (s != null) out.add(s);
        }
        return out;
    }

    /** the significant instructions of a body, rendered */
    public static List<String> sig(MethodNode m) {
        List<String> out = new ArrayList<>();
        if (m.instructions == null) return out;
        for (AbstractInsnNode in : m.instructions) {
            String s = sigOf(in);
            if (s != null) out.add(s);
        }
        return out;
    }

    /** each occurrence's position in sig(m) (an occurrence is always significant itself) */
    private static List<Integer> sigIndex(MethodNode m, List<AbstractInsnNode> occ) {
        Set<AbstractInsnNode> want = new HashSet<>(occ);
        List<Integer> out = new ArrayList<>();
        int idx = 0;
        if (m.instructions != null) {
            for (AbstractInsnNode in : m.instructions) {
                if (want.contains(in)) out.add(idx);
                if (sigOf(in) != null) idx++;
            }
        }
        return out;
    }

    static String sigOf(AbstractInsnNode in) {
        if (in instanceof MethodInsnNode mi) return mi.owner + "." + mi.name;
        if (in instanceof FieldInsnNode fi) return fi.getOpcode() + fi.owner + "." + fi.name;
        if (in instanceof TypeInsnNode ti) return ti.getOpcode() + ti.desc;
        if (in instanceof InvokeDynamicInsnNode id) return "indy." + id.name;
        Object c = constantOf(in);
        if (c != null) return "c" + c;
        int op = in.getOpcode();
        if (op >= Opcodes.IRETURN && op <= Opcodes.RETURN || op == Opcodes.ATHROW) return "ret" + op;
        return null;
    }

    /** a constant as Mixin's @Constant / CONSTANT sees it (null for anything else) */
    public static Object constantOf(AbstractInsnNode in) {
        int op = in.getOpcode();
        if (in instanceof LdcInsnNode ldc) return ldc.cst;
        if (op >= Opcodes.ICONST_M1 && op <= Opcodes.ICONST_5) return op - Opcodes.ICONST_0;
        if (in instanceof IntInsnNode ii && (op == Opcodes.BIPUSH || op == Opcodes.SIPUSH)) return ii.operand;
        if (op == Opcodes.LCONST_0 || op == Opcodes.LCONST_1) return (long) (op - Opcodes.LCONST_0);
        if (op >= Opcodes.FCONST_0 && op <= Opcodes.FCONST_2) return (float) (op - Opcodes.FCONST_0);
        if (op == Opcodes.DCONST_0 || op == Opcodes.DCONST_1) return (double) (op - Opcodes.DCONST_0);
        if (in instanceof TypeInsnNode ti && (op == Opcodes.INSTANCEOF || op == Opcodes.CHECKCAST)) return Type.getObjectType(ti.desc);
        return null;
    }
}
