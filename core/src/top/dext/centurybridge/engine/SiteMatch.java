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
        int[] map = new int[vo.size()];
        Arrays.fill(map, -1);
        boolean[] taken = new boolean[wo.size()];
        List<String> vs = sig(vm), ws = sig(wm);
        List<Integer> vi = sigIndex(vm, vo), wi = sigIndex(wm, wo);
        for (int k = 4; k >= 1; k--) {
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
        int[] aligned = align(vs, ws);
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
            if (map[i] < 0) continue;
            if (map[i] < last) map[i] = -1;
            else last = map[i];
        }
        return map;
    }

    /**
     * The W instruction standing where V's {@code site} stood when the site itself is gone from W: the
     * one W instruction accepted by {@code candidate} whose significant neighbours (k before, k after,
     * the instruction itself left out) are V's site's, for the widest k from 4 down to 2 that finds any,
     * counting only contexts with a neighbour other than the body's edges and returns.
     * Null when no such instruction, or when two or more stand there, or when another V instruction of
     * the site's kind has the same neighbours (V's site is then not singled out either).
     */
    public static AbstractInsnNode counterpart(MethodNode vm, AbstractInsnNode site, MethodNode wm, Predicate<AbstractInsnNode> candidate) {
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
        return null;
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
    static int[] align(List<String> a, List<String> b) {
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
