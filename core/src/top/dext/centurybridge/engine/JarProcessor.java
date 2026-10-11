package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Runs one jar through a chain of segments: mixin triage (dead mixins are
 * stripped from their configs, load-bearing ones with a warning), reference
 * verification with boundary attribution, fabric.mod.json patching.
 * Verdicts follow the tombstone philosophy: missing symbols degrade lazily.
 */
public final class JarProcessor {

    /**
     * Emit the new-shape method vanilla now calls, forwarding to the mod's old
     * override:
     * <pre>
     *   new(a1..an):
     *     if (guard.cbEnter(this)) { try { this.old(wrap(a1)..an); } finally { guard.cbExit(this); } return; }
     *     super.new(a1..an);          // re-entered through a super-call chain: fall through to vanilla
     * </pre>
     * Arguments whose type differs between the shapes go through the hook's
     * wrap static (new type -> old type); everything else passes through.
     * Descriptors are emitted in the analysis namespace and pass through the
     * same ClassRemapper as the rest of the class, so a facade-renamed old
     * parameter type lands on the facade.
     */
    /** verifier frame type for one parameter (locals are never widened here) */
    private static Object frameType(org.objectweb.asm.Type t) {
        switch (t.getSort()) {
            case org.objectweb.asm.Type.BOOLEAN, org.objectweb.asm.Type.CHAR, org.objectweb.asm.Type.BYTE,
                 org.objectweb.asm.Type.SHORT, org.objectweb.asm.Type.INT -> {
                return Opcodes.INTEGER;
            }
            case org.objectweb.asm.Type.FLOAT -> {
                return Opcodes.FLOAT;
            }
            case org.objectweb.asm.Type.LONG -> {
                return Opcodes.LONG;
            }
            case org.objectweb.asm.Type.DOUBLE -> {
                return Opcodes.DOUBLE;
            }
            default -> {
                return t.getInternalName();
            }
        }
    }

    private static void synthesizeOverride(ClassVisitor cv, Chain.OverrideHook h, String cls, String superName) {
        org.objectweb.asm.Type[] na = org.objectweb.asm.Type.getArgumentTypes(h.newDesc);
        org.objectweb.asm.Type[] oa = org.objectweb.asm.Type.getArgumentTypes(h.oldDesc);
        org.objectweb.asm.Type nr = org.objectweb.asm.Type.getReturnType(h.newDesc);
        int[] map = h.args;
        if (h.bridgeName != null) {
            map = null; // the bridge takes every new argument as is
        } else if (map == null) {
            if (na.length != oa.length) {
                return; // arity change without an explicit argument map
            }
            map = new int[oa.length];
            for (int i = 0; i < map.length; i++) {
                map[i] = i;
            }
        } else if (map.length != oa.length) {
            return; // malformed hook
        }
        // local slot of each new parameter
        int[] slotOf = new int[na.length];
        for (int i = 0, s = 1; i < na.length; i++) {
            slotOf[i] = s;
            s += na[i].getSize();
        }
        // the engine writes with COMPUTE_MAXS only (frame computation would
        // need the game's class hierarchy), so every branch target and handler
        // in a synthesized method carries an explicit expanded frame
        Object[] locals = new Object[na.length + 1];
        locals[0] = cls;
        for (int i = 0; i < na.length; i++) {
            locals[i + 1] = frameType(na[i]);
        }
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, h.newName, h.newDesc, null, null);
        mv.visitCode();
        org.objectweb.asm.Label fallthrough = new org.objectweb.asm.Label();
        org.objectweb.asm.Label tryStart = new org.objectweb.asm.Label();
        org.objectweb.asm.Label tryEnd = new org.objectweb.asm.Label();
        org.objectweb.asm.Label handler = new org.objectweb.asm.Label();
        boolean guarded = h.guard != null;
        if (guarded) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.guard, "cbEnter", "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(Opcodes.IFEQ, fallthrough);
            mv.visitTryCatchBlock(tryStart, tryEnd, handler, null);
            mv.visitLabel(tryStart);
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        if (h.bridgeName != null) {
            for (int i = 0; i < na.length; i++) {
                mv.visitVarInsn(na[i].getOpcode(Opcodes.ILOAD), slotOf[i]);
            }
        } else {
            for (int i = 0; i < oa.length; i++) {
                int src = map[i];
                mv.visitVarInsn(na[src].getOpcode(Opcodes.ILOAD), slotOf[src]);
                if (!na[src].equals(oa[i]) && h.wrapName != null) {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.wrapOwner, h.wrapName, h.wrapDesc, false);
                }
            }
        }
        int slot = 1;
        for (org.objectweb.asm.Type t : na) {
            slot += t.getSize();
        }
        if (h.bridgeName != null) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.bridgeOwner, h.bridgeName, h.bridgeDesc, false);
        } else {
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, cls, h.oldName, h.oldDesc, false);
        }
        if (h.unwrapName != null) {
            // the old override answers in the old return type; convert it to
            // what vanilla expects from the new shape (facade -> real type)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.unwrapOwner, h.unwrapName, h.unwrapDesc, false);
        }
        if (guarded) {
            mv.visitLabel(tryEnd);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.guard, "cbExit", "(Ljava/lang/Object;)V", false);
            mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
            mv.visitLabel(handler);
            mv.visitFrame(Opcodes.F_NEW, locals.length, locals, 1, new Object[] {"java/lang/Throwable"});
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.guard, "cbExit", "(Ljava/lang/Object;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
            mv.visitLabel(fallthrough);
            mv.visitFrame(Opcodes.F_NEW, locals.length, locals, 0, new Object[0]);
            if (superName == null || superName.equals("java/lang/Object")) {
                // nothing above us declares the new shape: a re-entry here is a no-op
                if (nr.getSort() != org.objectweb.asm.Type.VOID) {
                    mv.visitInsn(nr.getSort() == org.objectweb.asm.Type.OBJECT || nr.getSort() == org.objectweb.asm.Type.ARRAY
                        ? Opcodes.ACONST_NULL : Opcodes.ICONST_0);
                }
                mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                slot = 1;
                for (org.objectweb.asm.Type t : na) {
                    mv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), slot);
                    slot += t.getSize();
                }
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, h.newName, h.newDesc, false);
                mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
            }
        } else {
            mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
        }
        mv.visitMaxs(slot + 3, slot + 1);
        mv.visitEnd();
    }

    /**
     * Constructor pre-pass, three mechanisms by call shape:
     * <ul>
     * <li>NEW/DUP/args/INVOKESPECIAL owner.<init>(desc) -> args/INVOKESTATIC
     *     factory(desc)Lowner; for every constructor in ctorRedirects. NEWs are
     *     matched to their <init> LIFO per type, which is exactly how nesting
     *     works on the operand stack (an inner new completes before the outer
     *     <init>). A branch inside the argument expression (a frame that records
     *     the two uninitialized refs) keeps NEW/DUP and pops them at the call.</li>
     * <li>superCtorReshapes: any INVOKESPECIAL <init> -- super()/this() with no
     *     NEW to drop, or a NEW site no factory claims -- has its arguments
     *     spilled to fresh locals and re-pushed per the ops (drop / conv / push),
     *     then takes the new descriptor. See {@link Chain#superCtorReshapes}.</li>
     * <li>superCtorAdapts: the one-tail-argument special case of the above.</li>
     * </ul>
     * Every rewrite is credited in the full audit here, by the pass that did
     * it: the analysis pass runs on the rewritten bytes and only ever sees the
     * residue (a dead constructor still called), which it reports as damage.
     */
    private static byte[] rewriteConstructors(byte[] bytes, Chain chain, Report rpt, String tag) {
        String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        boolean any = false;
        for (String o : chain.ctorRedirectOwners) {
            if (raw.contains(o)) {
                any = true;
                break;
            }
        }
        if (!any) {
            return bytes;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        int rewrites = 0;
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if (m.instructions == null) {
                continue;
            }
            java.util.Map<String, java.util.ArrayDeque<org.objectweb.asm.tree.TypeInsnNode>> pending = new HashMap<>();
            org.objectweb.asm.tree.AbstractInsnNode next;
            for (org.objectweb.asm.tree.AbstractInsnNode in = m.instructions.getFirst(); in != null; in = next) {
                next = in.getNext(); // captured first: set()/remove() below detach the current node
                if (in.getOpcode() == Opcodes.NEW) {
                    org.objectweb.asm.tree.TypeInsnNode t = (org.objectweb.asm.tree.TypeInsnNode) in;
                    if (chain.ctorRedirectOwners.contains(t.desc)) {
                        pending.computeIfAbsent(t.desc, k -> new java.util.ArrayDeque<>()).push(t);
                    }
                } else if (in.getOpcode() == Opcodes.INVOKESPECIAL) {
                    org.objectweb.asm.tree.MethodInsnNode mi = (org.objectweb.asm.tree.MethodInsnNode) in;
                    if (!mi.name.equals("<init>") || !chain.ctorRedirectOwners.contains(mi.owner)) {
                        continue;
                    }
                    java.util.ArrayDeque<org.objectweb.asm.tree.TypeInsnNode> stack = pending.get(mi.owner);
                    org.objectweb.asm.tree.TypeInsnNode newInsn = stack == null || stack.isEmpty() ? null : stack.pop();
                    String key = mi.owner + "." + mi.name + mi.desc;
                    String target = chain.ctorRedirects.get(key);
                    Chain.CtorReshape reshape = chain.superCtorReshapes.get(key);
                    String[] adapt = chain.superCtorAdapts.get(key);
                    if (newInsn == null || target == null) {
                        // no NEW site (a super(...) / this(...) delegation), or a NEW
                        // site no factory claims: a factory cannot replace the call,
                        // but the arguments can be reshaped / adapted in place
                        if (reshape != null) {
                            reshapeArgs(m, mi, reshape);
                            rewrites++;
                            rpt.notes.add(tag + (newInsn == null ? "super-ctor-reshaped: " : "ctor-reshaped: ")
                                + key + " -> " + reshape.newDesc + " in " + cn.name);
                            top.dext.centurybridge.data.FullAudit.record(key,
                                newInsn == null ? "super-ctor-reshape" : "ctor-reshape");
                        } else if (adapt != null) {
                            m.instructions.insertBefore(mi, new org.objectweb.asm.tree.MethodInsnNode(
                                Opcodes.INVOKESTATIC, redirCls(adapt[1]),
                                redirName(adapt[1], "cbConv"), adapt[2], false));
                            mi.desc = adapt[0];
                            rewrites++;
                            rpt.notes.add(tag + (newInsn == null ? "super-ctor-adapted: " : "ctor-adapted: ")
                                + key + " in " + cn.name);
                            top.dext.centurybridge.data.FullAudit.record(key,
                                newInsn == null ? "super-ctor-adapt" : "ctor-adapt");
                        } else if (newInsn == null && target != null) {
                            // left in place: the analysis pass sees the dead constructor
                            // still called from a super()/this() site and accounts it
                            // (ledger / datagen / SUPER-CTOR-ISSUE)
                            rpt.notes.add(tag + "!! super-ctor call not adaptable (no superCtorReshapes/superCtorAdapts entry): "
                                + key + " in " + cn.name);
                        }
                        continue;
                    }
                    org.objectweb.asm.tree.AbstractInsnNode dup = newInsn.getNext();
                    if (dup == null || dup.getOpcode() != Opcodes.DUP) {
                        // unusual shape (javac always emits NEW DUP): the factory cannot
                        // take this site; the analysis pass reports the residue
                        rpt.notes.add(tag + "!! ctor-redirect skipped (NEW without DUP): " + key + " in " + cn.name);
                        continue;
                    }
                    String factoryDesc = mi.desc.substring(0, mi.desc.lastIndexOf(')') + 1) + "L" + mi.owner + ";";
                    // a branch inside the argument expression means a frame that
                    // records the two uninitialized slots: dropping NEW/DUP would
                    // invalidate it. Keep them, and at the call: spill the
                    // arguments, pop the two uninitialized refs, reload, call the
                    // factory -- every existing frame stays exact.
                    boolean framed = false;
                    for (org.objectweb.asm.tree.AbstractInsnNode p = dup.getNext(); p != null && p != mi; p = p.getNext()) {
                        if (p.getType() == org.objectweb.asm.tree.AbstractInsnNode.FRAME
                                || p.getType() == org.objectweb.asm.tree.AbstractInsnNode.JUMP_INSN
                                || p.getType() == org.objectweb.asm.tree.AbstractInsnNode.TABLESWITCH_INSN
                                || p.getType() == org.objectweb.asm.tree.AbstractInsnNode.LOOKUPSWITCH_INSN) {
                            framed = true;
                            break;
                        }
                    }
                    if (framed) {
                        org.objectweb.asm.Type[] args = org.objectweb.asm.Type.getArgumentTypes(mi.desc);
                        int[] slots = spillArgs(m, mi, args);
                        org.objectweb.asm.tree.InsnList seq = new org.objectweb.asm.tree.InsnList();
                        seq.add(new org.objectweb.asm.tree.InsnNode(Opcodes.POP2)); // the two uninitialized refs
                        for (int i = 0; i < args.length; i++) {
                            seq.add(new org.objectweb.asm.tree.VarInsnNode(args[i].getOpcode(Opcodes.ILOAD), slots[i]));
                        }
                        m.instructions.insertBefore(mi, seq);
                        rpt.notes.add(tag + "ctor-redirected through spill (branch in argument expression): " + key
                            + " in " + cn.name);
                    } else {
                        m.instructions.remove(dup);
                        m.instructions.remove(newInsn);
                    }
                    m.instructions.set(mi, new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC,
                        redirCls(target), redirName(target, "cbNew"), factoryDesc, false));
                    rewrites++;
                    top.dext.centurybridge.data.FullAudit.record(key, "ctor-redirect");
                }
            }
            // Constructor method references (owner::new) compile to an
            // invokedynamic whose bootstrap carries a REF_newInvokeSpecial
            // MethodHandle to owner.<init>, or (rarely) to an LDC of such a
            // handle. The NEW/INVOKESPECIAL rewrite above never sees these, so a
            // dead constructor reached only through ::new (e.g. resourcefullib's
            // Identifier::new byte-codec) slips past as a NoSuchMethodError at
            // the lambda's first use. Rewrite the handle to an INVOKESTATIC
            // pointing at the same factory the direct-call path uses.
            for (org.objectweb.asm.tree.AbstractInsnNode in = m.instructions.getFirst(); in != null; in = in.getNext()) {
                if (in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode idin) {
                    if (idin.bsmArgs != null) {
                        for (int i = 0; i < idin.bsmArgs.length; i++) {
                            if (idin.bsmArgs[i] instanceof org.objectweb.asm.Handle h) {
                                org.objectweb.asm.Handle nh = rewriteCtorHandle(h, chain);
                                if (nh != null) {
                                    idin.bsmArgs[i] = nh;
                                    rewrites++;
                                    top.dext.centurybridge.data.FullAudit.record(
                                        h.getOwner() + ".<init>" + h.getDesc(), "ctor-redirect-methodref");
                                }
                            }
                        }
                    }
                } else if (in instanceof org.objectweb.asm.tree.LdcInsnNode ldc
                        && ldc.cst instanceof org.objectweb.asm.Handle h) {
                    org.objectweb.asm.Handle nh = rewriteCtorHandle(h, chain);
                    if (nh != null) {
                        ldc.cst = nh;
                        rewrites++;
                        top.dext.centurybridge.data.FullAudit.record(
                            h.getOwner() + ".<init>" + h.getDesc(), "ctor-redirect-methodref");
                    }
                }
            }
        }
        if (rewrites == 0) {
            return bytes;
        }
        rpt.notes.add(tag + "ctor-redirected: " + rewrites + " new-site(s) in " + cn.name);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /**
     * If {@code h} is a {@code owner::new} constructor method handle
     * (REF_newInvokeSpecial) whose owner has a ctorRedirect factory, return an
     * equivalent REF_invokeStatic handle to that factory (same arg types, with
     * the owner appended as the return type -- exactly the factory signature the
     * direct-call rewrite produces). Returns null if nothing to do, so callers
     * can detect a hit.
     */
    private static org.objectweb.asm.Handle rewriteCtorHandle(org.objectweb.asm.Handle h, Chain chain) {
        if (h.getTag() != org.objectweb.asm.Opcodes.H_NEWINVOKESPECIAL
                || !h.getName().equals("<init>")
                || !chain.ctorRedirectOwners.contains(h.getOwner())) {
            return null;
        }
        String key = h.getOwner() + ".<init>" + h.getDesc();
        String target = chain.ctorRedirects.get(key);
        if (target == null) {
            return null; // owner is redirected but not this arity: leave for the audit
        }
        String factoryDesc = h.getDesc().substring(0, h.getDesc().lastIndexOf(')') + 1)
            + "L" + h.getOwner() + ";";
        return new org.objectweb.asm.Handle(org.objectweb.asm.Opcodes.H_INVOKESTATIC,
            redirCls(target), redirName(target, "cbNew"), factoryDesc, false);
    }

    /**
     * Store the arguments sitting on the operand stack before {@code at} into
     * fresh locals above the method's max_locals (top of stack first, so the
     * slots end up in argument order). Returns the slot of each argument.
     * COMPUTE_MAXS re-derives max_locals from the code afterwards; the node's
     * value is bumped anyway so a second spill in the same method stays fresh.
     */
    private static int[] spillArgs(org.objectweb.asm.tree.MethodNode m,
                                   org.objectweb.asm.tree.AbstractInsnNode at,
                                   org.objectweb.asm.Type[] args) {
        int[] slots = new int[args.length];
        int s = m.maxLocals;
        for (int i = 0; i < args.length; i++) {
            slots[i] = s;
            s += args[i].getSize();
        }
        org.objectweb.asm.tree.InsnList seq = new org.objectweb.asm.tree.InsnList();
        for (int i = args.length - 1; i >= 0; i--) {
            seq.add(new org.objectweb.asm.tree.VarInsnNode(args[i].getOpcode(Opcodes.ISTORE), slots[i]));
        }
        m.instructions.insertBefore(at, seq);
        m.maxLocals = s;
        return slots;
    }

    /**
     * In-place constructor reshape at an INVOKESPECIAL <init>: spill the old
     * arguments, re-push them per the ops (keep / drop / conv / push), swap the
     * descriptor. Valid at super()/this() and at NEW sites alike -- whatever
     * sits below the arguments (uninitializedThis, or NEW DUP's two refs) is
     * untouched, so existing frames stay exact.
     */
    private static void reshapeArgs(org.objectweb.asm.tree.MethodNode m,
                                    org.objectweb.asm.tree.MethodInsnNode mi,
                                    Chain.CtorReshape r) {
        org.objectweb.asm.Type[] old = org.objectweb.asm.Type.getArgumentTypes(mi.desc);
        int[] slots = spillArgs(m, mi, old);
        org.objectweb.asm.tree.InsnList seq = new org.objectweb.asm.tree.InsnList();
        for (Chain.ReshapeOp op : r.ops) {
            if (op.drop) {
                continue;
            }
            for (int idx : op.from) {
                seq.add(new org.objectweb.asm.tree.VarInsnNode(old[idx].getOpcode(Opcodes.ILOAD), slots[idx]));
            }
            if (op.owner != null) {
                seq.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, op.owner, op.name, op.desc, false));
            }
        }
        m.instructions.insertBefore(mi, seq);
        mi.desc = r.newDesc;
    }

    /** Redirect values are a class name, optionally "class#member" past the
     *  namespace wall where call-site names are no longer globally unique. */
    private static String redirCls(String v) {
        int i = v.indexOf('#');
        return i < 0 ? v : v.substring(0, i);
    }

    private static String redirName(String v, String callSiteName) {
        int i = v.indexOf('#');
        return i < 0 ? callSiteName : v.substring(i + 1);
    }


    private static final Gson GSON = new Gson();

    public static final class Report {
        public String file;
        public String modId;
        public String verdict;
        public List<String> issues = new ArrayList<>();        // server-runtime relevant
        public List<String> clientIssues = new ArrayList<>();  // client-side residuals, tracked separately
        /** mod classes the JVM refuses to load on the target: extends a class that
         *  is gone / final / an interface there, implements a gone or de-interfaced
         *  type. No call-site bridge reaches these; the verdict is broken_hierarchy. */
        public List<String> brokenHierarchy = new ArrayList<>();
        public int datagenSkipped;                             // dev-time-only refs, never execute in play
        public int ledgered;                                   // examined damage classified in the quirk ledger
        public List<String> strippedMixins = new ArrayList<>();
        /** injectors set to require=0: anchor gone in the target body, unrepairable -- known loss */
        public List<String> disabledInjectors = new ArrayList<>();
        /** mod overrides of a vanilla method whose shape is gone from every target
         *  ancestor and no override hook reconnects: vanilla never calls them again,
         *  silently -- no crash, the behaviour is just gone */
        public List<String> orphanedOverrides = new ArrayList<>();
        /** data and assets the resource translation could not carry to the target
         *  format: the loader skips or misreads them -- content quietly missing */
        public List<String> resourceLosses = new ArrayList<>();
        public List<String> notes = new ArrayList<>();
        public Path outputJar;
    }

    /** Walk superclasses through the mod's own classes, then the vanilla hierarchy. */
    private static boolean isUnder(String superName, String target, Map<String, String> jarSuper, Chain chain) {
        String cur = superName;
        int guard = 0;
        while (cur != null && guard++ < 64) {
            if (cur.equals(target)) {
                return true;
            }
            String next = jarSuper.get(cur);
            if (next == null) {
                next = chain.superOf.get(cur);
            }
            cur = next;
        }
        return false;
    }

    /**
     * Header index of one jar layer: class -> superclass, and class -> the
     * members it declares ("name(desc)" for methods, "name:desc" for fields).
     * The declarations are what tells an inherited vanilla member apart from
     * the mod's own: javac qualifies a call by the static type it was made
     * through, so {@code this.method_25420(ctx)} inside a Screen subclass is
     * emitted against the MOD class, and a segment that killed the vanilla
     * method was invisible behind that owner.
     */
    static final class LayerIndex {
        final Map<String, String> superOf = new HashMap<>();
        final Map<String, List<String>> interfacesOf = new HashMap<>();
        final Map<String, Set<String>> decls = new HashMap<>();
    }

    /** the vanilla types first reached from a mod class through the jar's own hierarchy */
    private static List<String> vanillaAncestors(String superName, String[] ifaces, LayerIndex layer) {
        List<String> out = new ArrayList<>();
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
        if (superName != null) {
            todo.add(superName);
        }
        if (ifaces != null) {
            todo.addAll(List.of(ifaces));
        }
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String t = todo.poll();
            if (!seen.add(t)) {
                continue;
            }
            if (t.startsWith("net/minecraft/") || t.startsWith("com/mojang/blaze3d/")) {
                out.add(t);
                continue;
            }
            if (!layer.superOf.containsKey(t)) {
                continue; // a library or another mod: not this jar's to judge
            }
            String sup = layer.superOf.get(t);
            if (sup != null) {
                todo.add(sup);
            }
            todo.addAll(layer.interfacesOf.getOrDefault(t, List.of()));
        }
        return out;
    }

    private static LayerIndex indexLayer(Map<String, byte[]> entries) {
        LayerIndex idx = new LayerIndex();
        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            if (!e.getKey().endsWith(".class")) {
                continue;
            }
            try {
                ClassReader cr = new ClassReader(e.getValue());
                Set<String> members = new HashSet<>();
                cr.accept(new ClassVisitor(Opcodes.ASM9) {
                    @Override
                    public MethodVisitor visitMethod(int acc, String n, String d, String s, String[] x) {
                        members.add(n + d);
                        return null;
                    }

                    @Override
                    public org.objectweb.asm.FieldVisitor visitField(int acc, String n, String d, String s, Object v) {
                        members.add(n + ":" + d);
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                idx.superOf.put(cr.getClassName(), cr.getSuperName());
                idx.interfacesOf.put(cr.getClassName(), List.of(cr.getInterfaces()));
                idx.decls.put(cr.getClassName(), members);
            } catch (RuntimeException ignored) {
                // not a class file after all
            }
        }
        return idx;
    }

    /**
     * For a member reference whose owner is a class of this jar layer: the
     * first VANILLA class up the superclass chain when no mod class on the
     * way declares the member (an inherited vanilla member, to be resolved --
     * cover / redirect / ledger / tombstone -- as if referenced on that
     * class); null when the mod declares it itself or the chain leaves into a
     * library. Interfaces are not walked: a default method inherited from a
     * vanilla interface through a mod-owned owner stays outside the audit.
     */
    private static String inheritedVanillaOwner(String owner, String member, LayerIndex layer) {
        String cur = owner;
        for (int hops = 0; cur != null && hops < 64; hops++) {
            if (cur.startsWith("net/minecraft/")) {
                return cur;
            }
            Set<String> d = layer.decls.get(cur);
            if (d == null) {
                return null; // not a class of this layer: a library, outside the stub diffs
            }
            if (d.contains(member)) {
                return null; // the mod's own member
            }
            cur = layer.superOf.get(cur);
        }
        return null;
    }

    public static Report process(Path in, Path outDir, Chain chain) throws IOException {
        Report rpt = new Report();
        rpt.file = in.getFileName().toString();

        Map<String, byte[]> entries = readAll(Files.readAllBytes(in));
        Set<String> issues = new TreeSet<>();
        // tombstone class names must be unique PER JAR, not just per layer:
        // every converted mod ships its own centurybridge/gen/Tombstones*, and
        // on the flat runtime classpath the first one loaded shadows the rest
        // -- another mod's calls then hit stubs with the wrong descriptors
        String jarTag = in.getFileName().toString()
            .replaceAll("\\.jar$", "").replaceAll("[^A-Za-z0-9]", "_");
        processEntries(entries, chain, rpt, issues, "", jarTag);

        // ---- metadata patch ----
        byte[] fmj = entries.get("fabric.mod.json");
        if (fmj != null) {
            entries.put("fabric.mod.json", patchFmj(fmj, chain, rpt));
        }

        // ---- emit ----
        String base = rpt.file.endsWith(".jar") ? rpt.file.substring(0, rpt.file.length() - 4) : rpt.file;
        Path out = outDir.resolve(base + "+" + chain.to + ".jar");
        rpt.outputJar = out;
        Files.createDirectories(outDir);
        try (ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(out))) {
            for (Map.Entry<String, byte[]> e : entries.entrySet()) {
                zout.putNextEntry(new ZipEntry(e.getKey()));
                zout.write(e.getValue());
                zout.closeEntry();
            }
        }

        rpt.issues = new ArrayList<>(issues);
        boolean degraded = !rpt.strippedMixins.isEmpty() || !rpt.orphanedOverrides.isEmpty()
            || !rpt.resourceLosses.isEmpty();
        rpt.verdict = degraded ? "ok_degraded" : issues.isEmpty() ? "ok_direct" : "ok_partial";
        if (!issues.isEmpty() && degraded) {
            rpt.verdict = "ok_degraded_partial";
        }
        if (!rpt.brokenHierarchy.isEmpty()) {
            // a class that cannot load is not "ok" in any degree: the mod either
            // dies at its entrypoint or the first time the class is touched
            rpt.verdict = "broken_hierarchy";
        }
        rpt.notes.add("output: " + out.getFileName());
        return rpt;
    }

    // ---------------------------------------------------------------- pieces

    /**
     * Triage + strip + verify one jar's entry map, recursing into JIJ-nested
     * jars (bundled libraries carry their own mixins -- a nested mixin whose
     * target died crashes the whole game just as hard as a top-level one).
     */
    private static void processEntries(Map<String, byte[]> entries, Chain chain,
                                       Report rpt, Set<String> issues, String tag,
                                       String jarTag) throws IOException {
        LayerIndex layer = indexLayer(entries);
        Map<String, String> jarSuper = layer.superOf;
        stripSignatures(entries, rpt, tag);
        // anchors are checked against real target bodies BEFORE triage: a repair
        // (desc-drop / descend) rewrites the selector triage then reads
        MixinRepair.Outcome repaired = chain.mixinRepair != null ? chain.mixinRepair.repair(entries) : null;
        if (repaired != null) {
            for (String r : repaired.repairs()) rpt.notes.add(tag + "mixin repaired: " + r);
            for (String d : repaired.disabled()) rpt.disabledInjectors.add(tag + d);
        }
        MixinTriage.Result triage = MixinTriage.run(entries, chain);
        Set<String> deadMixins = new HashSet<>();
        if (repaired != null) {
            for (String cls : repaired.fatal()) {
                deadMixins.add(cls);
                rpt.strippedMixins.add(tag + shortName(cls) + " -- " + repaired.fatalWhy().get(cls));
            }
        }
        for (MixinTriage.MixinInfo mx : triage.mixins()) {
            if (mx.vanilla() && mx.applyFatal(repaired != null)) {
                deadMixins.add(mx.className());
                String lb = mx.loadBearing() ? " [load-bearing! may be unstable]" : "";
                rpt.strippedMixins.add(tag + shortName(mx.className()) + lb + " -- " + String.join("; ", mx.broken()));
            } else if (mx.vanilla() && mx.worst().level() != Chain.Level.OK) {
                // triage decided to KEEP this mixin (no explicit-desc spec to fail at
                // apply); an informational note, not an unaccounted residual
                rpt.notes.add(tag + "kept L2 mixin (target sig changed@" + mx.worst().boundary()
                    + "): " + shortName(mx.className()));
            }
        }
        // a mixin extending a stripped mixin cannot apply either ("super class
        // ... was not found in the hierarchy of target"): strip the subtree too
        boolean grew = true;
        while (grew) {
            grew = false;
            for (String cls : triage.configOfClass().keySet()) {
                if (deadMixins.contains(cls)) continue;
                for (String sup = jarSuper.get(cls); sup != null; sup = jarSuper.get(sup)) {
                    if (deadMixins.contains(sup)) {
                        deadMixins.add(cls);
                        rpt.strippedMixins.add(tag + shortName(cls) + " -- extends stripped mixin " + shortName(sup));
                        grew = true;
                        break;
                    }
                }
            }
        }
        stripConfigs(entries, triage, deadMixins);
        markConfigsOptional(entries, triage);

        // tombstone stub class must be unique per jar layer (all layers share the runtime classpath)
        String tombClass = "centurybridge/gen/Tombstones_" + jarTag
            + (tag.isEmpty() ? "" : "$" + tag.chars().filter(c -> c == '(').count());
        Tombstones reg = new Tombstones(tombClass, chain.to);
        // the access widener the loader reads is the file fabric.mod.json names, whatever
        // its extension (".classtweaker" too); nothing else in the jar is one
        String awPath = accessWidenerPath(entries.get("fabric.mod.json"));

        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            String name = e.getKey();
            if (name.endsWith(".class")) {
                String cls = name.substring(0, name.length() - 6);
                if (deadMixins.contains(cls)) {
                    continue; // stripped: never applied, never executes
                }
                // mixin classes go through the same rewrite as everything else:
                // their method bodies are merged into the target and execute
                // there, so a broken call site inside one crashes just as hard.
                // Skipping them left hook/redirect damage in mixin bodies
                // shipping silently.
                // fabric convention: the mod's own datagen providers live under
                // .../datagen/ or .../data/ and never execute in a live game
                boolean datagenCaller = cls.contains("datagen") || cls.contains("/data/");
                Set<String> sink = datagenCaller ? new TreeSet<>() : issues;
                List<String> brokenSink = datagenCaller ? new ArrayList<>() : rpt.brokenHierarchy;
                byte[] rewritten = verifyAndRewrite(e.getValue(), layer, chain, sink, brokenSink, tag, reg, rpt);
                if (datagenCaller) {
                    rpt.datagenSkipped += sink.size();
                }
                if (rewritten != null) {
                    e.setValue(rewritten);
                }
            } else if (name.startsWith("META-INF/jars/") && name.endsWith(".jar")) {
                Map<String, byte[]> nested = readAll(e.getValue());
                // sibling nested jars share the flat runtime classpath too, so
                // the nested jar's own name joins the uniqueness tag
                String nestedTag = jarTag + "_" + name.substring("META-INF/jars/".length())
                    .replaceAll("\\.jar$", "").replaceAll("[^A-Za-z0-9]", "_");
                processEntries(nested, chain, rpt, issues, tag + "(bundled) ", nestedTag);
                // a bundled mod is a mod to the loader: its own minecraft range
                // still says the source version, and dependency resolution
                // refuses the whole pack before a class loads (293 nested mods
                // across 26 corpus jars at 1.21.4). Throwaway report: the outer
                // jar keeps its mod id.
                byte[] nfmj = nested.get("fabric.mod.json");
                if (nfmj != null) {
                    try {
                        nested.put("fabric.mod.json", patchFmj(nfmj, chain, new Report()));
                    } catch (RuntimeException ex) {
                        rpt.notes.add("bundled " + name + ": fabric.mod.json unparseable, left as is");
                    }
                }
                e.setValue(writeJar(nested));
            } else if (!chain.wallClassFwd.isEmpty() && name.equals(awPath)) {
                // A mod's own access widener is a resource, not a class, so the
                // bytecode rewrite never touches it. Past the namespace wall the
                // runtime rejects an intermediary-named AW ("Namespace
                // (intermediary) does not match current runtime namespace"):
                // translate owners/names/descriptors to the mojang namespace the
                // converted bytecode now uses, and relabel the header to named.
                byte[] rew = rewriteAccessWidener(e.getValue(), chain, tag, rpt);
                if (rew != null) {
                    e.setValue(rew);
                }
            } else if (name.endsWith(".json") && name.contains("/lang/")
                    && name.startsWith("assets/") && e.getValue().length == 0) {
                // A mod shipping a zero-byte lang json loaded fine on older MC
                // (the parser tolerated empty), but 26.2's Language.loadFromJson
                // NPEs on null entries and aborts world load. Materialize an
                // empty object so the resource parses to zero translations --
                // the faithful equivalent of "this lang file adds nothing".
                e.setValue("{}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                rpt.notes.add(tag + "empty lang json " + name + " -> {} (26.2 parser rejects empty)");
            }
        }
        if (!reg.stubs.isEmpty()) {
            entries.put(tombClass + ".class", reg.synthesize());
        }
        // the data pack and resource pack are read by the target's loaders, in the
        // target's formats: carry them over the same boundaries the bytecode crossed
        top.dext.centurybridge.engine.res.ResourceTranslator.Outcome res =
            top.dext.centurybridge.engine.res.ResourceTranslator.apply(entries, chain.from, chain.to, chain.resourceTypes);
        if (res.changedAnything()) {
            rpt.notes.add(tag + "resources: " + res.moved + " moved, " + res.rewritten + " rewritten, "
                + res.generated + " generated for " + chain.to);
        }
        for (String n : res.notes) {
            rpt.notes.add(tag + "resources: " + n);
        }
        res.losses.forEach((what, n) -> rpt.resourceLosses.add(tag + what + (n > 1 ? " (x" + n + ")" : "")));
    }

    /** fabric.mod.json's "accessWidener" entry path, or null */
    static String accessWidenerPath(byte[] fmj) {
        if (fmj == null) {
            return null;
        }
        try {
            var r = new com.google.gson.stream.JsonReader(new java.io.StringReader(
                new String(fmj, java.nio.charset.StandardCharsets.UTF_8)));
            r.setLenient(true);
            var o = com.google.gson.JsonParser.parseReader(r).getAsJsonObject();
            if (!o.has("accessWidener") || !o.get("accessWidener").isJsonPrimitive()) {
                return null;
            }
            String p = o.get("accessWidener").getAsString();
            return p.startsWith("/") ? p.substring(1) : p;
        } catch (RuntimeException ex) {
            return null;
        }
    }

    /**
     * Rewrite a mod's access widener / class tweaker across the namespace wall:
     * intermediary owners/names/descriptors -> mojang, header namespace ->
     * official. Lines are {@code <access> class <owner>}, {@code <access> field
     * <owner> <name> <desc>}, {@code <access> method <owner> <name> <desc>},
     * {@code inject-interface <owner> <interface>}, {@code extend-enum <owner>
     * <constant>} (any of them "transitive-"), plus the {@code accessWidener} /
     * {@code classTweaker} header and {@code #} comments. An owner/name with no
     * mapping is left as is (survivors keep their name; only wall-renamed tokens
     * move). Returns null to leave the file unchanged when nothing needed rewriting.
     */
    private static byte[] rewriteAccessWidener(byte[] bytes, Chain chain, String tag, Report rpt) {
        String text = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        String nl = text.contains("\r\n") ? "\r\n" : "\n";
        String[] lines = text.split("\r\n|\n", -1);
        java.util.regex.Pattern descCls = java.util.regex.Pattern.compile("L([^;]+);");
        boolean changed = false;
        int moved = 0;
        for (int i = 0; i < lines.length; i++) {
            String ln = lines[i];
            String trimmed = ln.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            // split off a trailing comment so we rewrite only the directive
            String comment = "";
            int hash = ln.indexOf('#');
            String body = ln;
            if (hash >= 0) {
                comment = ln.substring(hash);
                body = ln.substring(0, hash);
            }
            String[] tok = body.split("\\s+");
            if (tok.length >= 3 && (tok[0].equals("accessWidener") || tok[0].equals("classTweaker"))) {
                // header: accessWidener v1 intermediary -> official. Past the
                // wall the 26.x runtime names ARE the mojang/official ones the
                // converted bytecode uses; fabric calls that namespace
                // "official", so the AW must declare official (not named) to
                // match the runtime it loads against.
                if (tok[2].equals("intermediary")) {
                    tok[2] = "official";
                    lines[i] = String.join("\t", tok) + comment;
                    changed = true;
                }
                continue;
            }
            // directive lines start with the access verb; the kind is tok[1]
            if (tok.length < 3) {
                continue;
            }
            String verb = tok[0].startsWith("transitive-") ? tok[0].substring("transitive-".length()) : tok[0];
            if (verb.equals("inject-interface") || verb.equals("extend-enum")) {
                // class tweaker: <verb> <class> <interface|constant>; an enum constant is the
                // mod's own new name, an interface may carry a generic signature
                String moj = chain.wallClassFwd.get(tok[1]);
                if (moj != null) { tok[1] = moj; changed = true; moved++; }
                if (verb.equals("inject-interface")) {
                    String itf = tok[2];
                    int lt = itf.indexOf('<');
                    String raw = lt < 0 ? itf : itf.substring(0, lt);
                    String rest = lt < 0 ? "" : descCls.matcher(itf.substring(lt)).replaceAll(mr -> "L"
                        + java.util.regex.Matcher.quoteReplacement(
                            chain.wallClassFwd.getOrDefault(mr.group(1), mr.group(1))) + ";");
                    String nitf = chain.wallClassFwd.getOrDefault(raw, raw) + rest;
                    if (!nitf.equals(itf)) { tok[2] = nitf; changed = true; }
                }
                lines[i] = String.join("\t", tok) + comment;
                continue;
            }
            String kind = tok[1];
            if (kind.equals("class") && tok.length >= 3) {
                String moj = chain.wallClassFwd.get(tok[2]);
                if (moj != null) { tok[2] = moj; changed = true; moved++; }
            } else if ((kind.equals("field") || kind.equals("method")) && tok.length >= 5) {
                String owner = tok[2];
                String member = tok[3];
                String desc = tok[4];
                String key = owner + "." + member;
                String mojName = kind.equals("field")
                    ? chain.wallFieldFwd.get(key) : chain.wallMethodFwd.get(key);
                String mojOwner = chain.wallClassFwd.get(owner);
                if (mojOwner != null) { tok[2] = mojOwner; changed = true; moved++; }
                if (mojName != null) { tok[3] = mojName; changed = true; }
                String nd = descCls.matcher(desc).replaceAll(mr -> "L"
                    + java.util.regex.Matcher.quoteReplacement(
                        chain.wallClassFwd.getOrDefault(mr.group(1), mr.group(1))) + ";");
                if (!nd.equals(desc)) { tok[4] = nd; changed = true; }
            } else {
                continue;
            }
            lines[i] = String.join("\t", tok) + comment;
        }
        if (!changed) {
            return null;
        }
        rpt.notes.add(tag + "access widener rewritten across wall (" + moved + " owners/classes -> mojang, header -> official)");
        return String.join(nl, lines).getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /** per-jar-layer registry of lazy-fail stubs, synthesized as one class */
    private static final class Tombstones {
        record Stub(String name, String staticDesc, String message) {}
        final String clsName;
        final String targetVer;
        final Map<String, Stub> stubs = new LinkedHashMap<>();

        Tombstones(String clsName, String targetVer) {
            this.clsName = clsName;
            this.targetVer = targetVer;
        }

        Stub get(String owner, String name, String desc, boolean isStatic, String boundary) {
            return stubs.computeIfAbsent(owner + "." + name + desc + isStatic, k -> {
                String staticDesc = isStatic ? desc : "(L" + owner + ";" + desc.substring(1);
                String msg = "CenturyBridge tombstone: " + owner.replace('/', '.') + "." + name
                    + " no longer exists in " + targetVer + " (died @" + boundary + ")";
                return new Stub("t" + stubs.size(), staticDesc, msg);
            });
        }

        byte[] synthesize() {
            org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
            cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, clsName, null, "java/lang/Object", null);
            for (Stub s : stubs.values()) {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, s.name(), s.staticDesc(), null, null);
                mv.visitCode();
                mv.visitTypeInsn(Opcodes.NEW, "java/lang/UnsupportedOperationException");
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn(s.message());
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/UnsupportedOperationException",
                    "<init>", "(Ljava/lang/String;)V", false);
                mv.visitInsn(Opcodes.ATHROW);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            cw.visitEnd();
            return cw.toByteArray();
        }
    }

    /**
     * A signed jar carries per-entry digests; rewriting any entry makes the
     * JVM's JarVerifier throw SecurityException on READ of that entry, which
     * surfaces as maddeningly indirect breakage (Mixin reporting its config
     * "could not be read" while the JSON inside is perfectly valid). Every
     * converted jar is modified by definition, so the signature has to go:
     * drop the signature files and keep only the manifest's main section.
     */
    private static void stripSignatures(Map<String, byte[]> entries, Report rpt, String tag) {
        boolean signed = entries.keySet().removeIf(n -> {
            String u = n.toUpperCase(java.util.Locale.ROOT);
            return u.startsWith("META-INF/") && (u.endsWith(".SF")
                || u.endsWith(".RSA") || u.endsWith(".DSA") || u.endsWith(".EC"));
        });
        if (!signed) {
            return;
        }
        byte[] mf = entries.get("META-INF/MANIFEST.MF");
        if (mf != null) {
            String text = new String(mf, StandardCharsets.UTF_8);
            int cut = text.indexOf("\r\n\r\n");
            int len = 4;
            if (cut < 0) {
                cut = text.indexOf("\n\n");
                len = 2;
            }
            if (cut >= 0) {
                entries.put("META-INF/MANIFEST.MF",
                    text.substring(0, cut + len).getBytes(StandardCharsets.UTF_8));
            }
        }
        rpt.notes.add(tag + "unsigned: entry digests dropped (conversion invalidates them)");
    }

    private static byte[] writeJar(Map<String, byte[]> entries) throws IOException {
        var buf = new java.io.ByteArrayOutputStream();
        try (ZipOutputStream zout = new ZipOutputStream(buf)) {
            for (Map.Entry<String, byte[]> e : entries.entrySet()) {
                zout.putNextEntry(new ZipEntry(e.getKey()));
                zout.write(e.getValue());
                zout.closeEntry();
            }
        }
        return buf.toByteArray();
    }

    private static Map<String, byte[]> readAll(byte[] jar) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        try (ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.isDirectory()) {
                    entries.put(e.getName(), zin.readAllBytes());
                }
            }
        }
        return entries;
    }

    /**
     * Runtime safety net under the static repair: a converted mod's mixin config
     * becomes "required": false, Mixin's own switch for "log this config's errors
     * and carry on". What static analysis could not foresee then skips one mixin
     * instead of killing the game -- and CenturyBridge's error handler writes
     * every such failure to logs/centurybridge-mixin.log, so it stays KNOWN.
     */
    /**
     * Past the namespace wall the converted jar only gets its mojang names in the final
     * rename, so the anchor check {@link MixinRepair} runs pre-wall against an
     * intermediary code jar cannot see 26.x bodies. This runs it again on the renamed
     * jar against the target's real (unobfuscated) code: per injector, a drifted anchor
     * is repaired or the injector dropped and reported, a mixin whose target cannot
     * take it at all is stripped and reported -- instead of every body injector being
     * set to require=0 blind, which also hid the ones that stayed broken.
     * Nested (jar-in-jar) mods are repaired the same way. Rewrites {@code jar} in place.
     */
    public static void repairPastWall(Path jar, MixinRepair repair, Report rpt) throws IOException {
        Map<String, byte[]> entries = readAll(Files.readAllBytes(jar));
        if (repairLayer(entries, repair, rpt, "")) {
            Files.write(jar, writeJar(entries));
        }
        if (!rpt.strippedMixins.isEmpty() && rpt.verdict.equals("ok_direct")) {
            rpt.verdict = "ok_degraded";
        } else if (!rpt.strippedMixins.isEmpty() && rpt.verdict.equals("ok_partial")) {
            rpt.verdict = "ok_degraded_partial";
        }
    }

    private static boolean repairLayer(Map<String, byte[]> entries, MixinRepair repair, Report rpt, String tag)
            throws IOException {
        boolean changed = false;
        Map<String, byte[]> before = new HashMap<>(entries);
        MixinRepair.Outcome out = repair.repair(entries);
        for (String r : out.repairs()) {
            rpt.notes.add(tag + "mixin repaired past the wall: " + r);
        }
        for (String d : out.disabled()) {
            rpt.disabledInjectors.add(tag + d + " (past the wall)");
        }
        if (!out.fatal().isEmpty()) {
            for (String cls : out.fatal()) {
                rpt.strippedMixins.add(tag + shortName(cls) + " -- past the wall: " + out.fatalWhy().get(cls));
            }
            for (Map.Entry<String, byte[]> e : new ArrayList<>(entries.entrySet())) {
                if (!e.getKey().endsWith(".json") || e.getKey().contains("/")) {
                    continue;
                }
                String text = new String(e.getValue(), StandardCharsets.UTF_8);
                if (!text.contains("\"package\"")) {
                    continue;
                }
                JsonObject cfg;
                try {
                    cfg = GSON.fromJson(text, JsonObject.class);
                } catch (RuntimeException ex) {
                    continue;
                }
                if (!cfg.has("package")) {
                    continue;
                }
                String pkg = cfg.get("package").getAsString().replace('.', '/');
                boolean edited = false;
                for (String key : new String[] {"mixins", "client", "server"}) {
                    if (!cfg.has(key)) {
                        continue;
                    }
                    JsonArray kept = new JsonArray();
                    for (JsonElement m : cfg.getAsJsonArray(key)) {
                        String cls = (pkg.isEmpty() ? "" : pkg + "/") + m.getAsString().replace('.', '/');
                        if (out.fatal().contains(cls)) {
                            edited = true;
                        } else {
                            kept.add(m);
                        }
                    }
                    cfg.add(key, kept);
                }
                if (edited) {
                    entries.put(e.getKey(), GSON.toJson(cfg).getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            if (!java.util.Arrays.equals(e.getValue(), before.get(e.getKey()))) {
                changed = true;
            }
        }
        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            String name = e.getKey();
            if (name.startsWith("META-INF/jars/") && name.endsWith(".jar")) {
                Map<String, byte[]> nested = readAll(e.getValue());
                if (repairLayer(nested, repair, rpt, tag + "(bundled) ")) {
                    e.setValue(writeJar(nested));
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static void markConfigsOptional(Map<String, byte[]> entries, MixinTriage.Result triage) {
        for (String cfgName : new HashSet<>(triage.configOfClass().values())) {
            byte[] data = entries.get(cfgName);
            if (data == null) {
                continue;
            }
            try {
                JsonObject cfg = GSON.fromJson(new String(data, StandardCharsets.UTF_8), JsonObject.class);
                cfg.addProperty("required", false);
                entries.put(cfgName, GSON.toJson(cfg).getBytes(StandardCharsets.UTF_8));
            } catch (RuntimeException ex) {
                // unparseable config: left as is (triage could not read it either)
            }
        }
    }

    private static void stripConfigs(Map<String, byte[]> entries, MixinTriage.Result triage, Set<String> dead) {
        if (dead.isEmpty()) {
            return;
        }
        Set<String> configs = new HashSet<>(triage.configOfClass().values());
        for (String cfgName : configs) {
            byte[] data = entries.get(cfgName);
            if (data == null) {
                continue;
            }
            JsonObject cfg = GSON.fromJson(new String(data, StandardCharsets.UTF_8), JsonObject.class);
            String pkg = cfg.has("package") ? cfg.get("package").getAsString().replace('.', '/') : "";
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key)) {
                    continue;
                }
                JsonArray kept = new JsonArray();
                for (JsonElement e : cfg.getAsJsonArray(key)) {
                    String cls = (pkg.isEmpty() ? "" : pkg + "/") + e.getAsString().replace('.', '/');
                    if (!dead.contains(cls)) {
                        kept.add(e);
                    }
                }
                cfg.add(key, kept);
            }
            entries.put(cfgName, GSON.toJson(cfg).getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Analysis pass collects issues and marks call sites that can be repaired
     * (static redirects into the runtime bridge; dead-method tombstones). When
     * repairs exist, a second ASM pass rewrites them -- redirected calls stop
     * being issues, tombstoned calls fail lazily with a descriptive message.
     * Returns the rewritten class bytes, or null when unchanged.
     */
    private static byte[] verifyAndRewrite(byte[] bytes, LayerIndex layer, Chain chain, Set<String> issues,
                                           List<String> brokenSink, String tag, Tombstones reg, Report rpt) {
        Map<String, String> jarSuper = layer.superOf;
        boolean ctorChanged = false;
        if (!chain.ctorRedirects.isEmpty() || !chain.superCtorAdapts.isEmpty() || !chain.superCtorReshapes.isEmpty()) {
            byte[] b2 = rewriteConstructors(bytes, chain, rpt, tag);
            ctorChanged = b2 != bytes;
            bytes = b2;
        }
        Map<String, String> redirects = new HashMap<>();     // static: owner.name+desc -> runtime class
        Map<String, String> instRedirects = new HashMap<>(); // instance: receiver becomes arg 0
        Map<String, String> fldRedirects = new HashMap<>();  // GETSTATIC: owner.name:desc -> runtime class
        Map<String, String> fldGetters = new HashMap<>();    // GETSTATIC -> INVOKESTATIC zero-arg getter on runtime class
        Map<String, String> instFldGetters = new HashMap<>(); // GETFIELD -> INVOKESTATIC getter(receiver) on runtime class
        Map<String, String> instOfSites = new HashMap<>();   // INSTANCEOF type -> INVOKESTATIC predicate(Object)Z
        Map<String, String> fldRenames = new HashMap<>();    // instance fields renamed in place
        Map<String, String> mthRenames = new HashMap<>();    // methods renamed in place (relocated to super)
        Map<String, String> tombstone = new HashMap<>();     // owner.name+desc -> boundary
        Set<String> clientTombstones = new HashSet<>();      // subset of tombstone keys on client-only owners
        Set<String> silentTombstones = new HashSet<>();      // ledgered damage: rewritten but not reported
        // call-site key -> the class to name in an emitted receiver-typed descriptor
        // "(Lowner;...)": the class the wiring keys the member on (instance redirects
        // climb the hierarchy), or the vanilla class an inherited member is reached
        // through when the site names a mod class. The bridge static is declared
        // on that class; the actual receiver is assignable to it.
        Map<String, String> emitOwner = new HashMap<>();

        // dead-class facade renames touch descriptors/signatures everywhere, so the
        // trigger is a raw byte scan rather than per-ref analysis
        boolean needClassRename = false;
        // super/implements-only rename: set when this class's superName or an
        // interface is a superRenames key. Only the hierarchy and super-invokes
        // are rewritten to the facade; every other reference to the dead type
        // is left on the real type (avoids the type-split a global classRename
        // causes when the same class also reads a vanilla constant of that type).
        final boolean[] needSuperRename = {false};
        // Orphaned override: 1.20.5 split Block.use -- the old 6-arg
        // method_9534 stops being called by anyone, silently. Detected here,
        // reconnected in the rewrite pass by synthesizing the new-signature
        // hook that forwards to the mod's old method.
        final boolean[] hasOldUse = {false};
        final boolean[] hasNewUse = {false};
        final Set<String> declaredOldHooks = new HashSet<>();
        final Set<String> declaredNewHooks = new HashSet<>();
        // generic orphaned overrides (overrideHooks): old shape declared here
        final java.util.List<Chain.OverrideHook> declaredOldOverrides = new ArrayList<>();
        final Set<Chain.OverrideHook> declaredNewOverrides = new HashSet<>();
        final int[] classAccess = {0};
        final String[] superOf = {null};
        // call sites hitting a lookup-hook's old descriptor; a class whose ONLY
        // damage is such a call must still go through the rewrite pass -- the
        // fix used to ride along on other damage and silently skip these
        final Set<String> hookCallSites = new HashSet<>();
        final Map<String, String> superRedirectSites = new HashMap<>(); // call-site key -> runtime target
        // a call into an owner whose kind flipped (class <-> interface) is damage
        // even when every member it names survives: the rewrite pass must run so
        // the kind normalizer sees it
        boolean needIfaceFix = false;
        if (!chain.kindFlips().isEmpty()) {
            String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
            for (String flipped : chain.kindFlips()) {
                if (raw.contains(flipped)) {
                    needIfaceFix = true;
                    break;
                }
            }
        }
        if (!chain.classRenames.isEmpty()) {
            String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
            for (String dead : chain.classRenames.keySet()) {
                if (raw.contains(dead)) {
                    needClassRename = true;
                    rpt.notes.add(tag + "facade-renamed: " + dead + " -> " + chain.classRenames.get(dead));
                    break;
                }
            }
        }
        final List<Chain.AdapterHook> adapterApply = new ArrayList<>();
        final Set<String> declaredShapes = new HashSet<>();
        ClassVisitor analysis = new ClassVisitor(Opcodes.ASM9) {
            String cls;
            String[] itfs;
            boolean broken;
            List<String> vanillaRoots;
            /** orphan candidates: {name, desc, overridden owner, side}; settled once adapter hooks are known */
            final List<String[]> orphanCandidates = new ArrayList<>();

            @Override
            public void visitEnd() {
                if ((classAccess[0] & Opcodes.ACC_INTERFACE) == 0 && !broken && !chain.adapterHooks.isEmpty()) {
                    if (vanillaRoots == null) {
                        vanillaRoots = vanillaAncestors(superOf[0], itfs, layer);
                    }
                    for (Chain.AdapterHook h : chain.adapterHooks) {
                        if (declaredShapes.containsAll(h.requires) && chain.sourceSubtype(vanillaRoots, h.under)) {
                            adapterApply.add(h);
                            for (String r : h.requires) {
                                top.dext.centurybridge.data.FullAudit.record(cls + "." + r, "adapter-hooked");
                            }
                            rpt.notes.add(tag + "adapter-hooked: " + cls + " implements " + h.adapter);
                        }
                    }
                }
                for (String[] o : orphanCandidates) {
                    boolean adapted = false;
                    for (Chain.AdapterHook h : adapterApply) {
                        adapted |= h.requires.contains(o[0] + o[1]);
                    }
                    if (adapted) {
                        continue;
                    }
                    top.dext.centurybridge.data.FullAudit.record(cls + "." + o[0] + o[1],
                        "ORPHAN[" + o[3] + "] overrode " + o[2] + "." + o[0] + o[1]);
                    rpt.orphanedOverrides.add(tag + cls + "." + o[0] + o[1] + " overrode " + o[2]
                        + (o[3].equals("client") ? " [client]" : ""));
                }
                super.visitEnd();
            }

            /**
             * An override vanilla no longer calls: the shape it bound to at the
             * source is gone from every target ancestor and no override hook
             * reconnects it. Nothing crashes -- the mod's behaviour is just gone
             * (1.20.2 RecipeSerializer.read(id, json), Recipe.getId, ...). Only
             * intermediary-named methods can be vanilla overrides. A covariant
             * override binds through its synthetic bridge (the erased shape), so
             * bridges are judged too.
             */
            private void checkOrphan(int acc, String mName, String mDesc) {
                if ((acc & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) != 0 || broken
                        || mName.startsWith("<") || !Chain.intermediaryMethod(mName)) {
                    return;
                }
                if (vanillaRoots == null) {
                    vanillaRoots = vanillaAncestors(superOf[0], itfs, layer);
                }
                for (String r : vanillaRoots) {
                    if (chain.classRenames.containsKey(r) || chain.superRenames.containsKey(r)) {
                        return; // a facade keeps the old API and forwards it
                    }
                }
                String overridden = chain.sourceDeclarer(mName, mDesc, vanillaRoots);
                if (overridden == null || chain.targetDeclares(mName, mDesc, vanillaRoots)) {
                    return;
                }
                String side = chain.sideOf(overridden);
                if (side.equals("datagen")) {
                    // dev-time data generation never runs in play; accounted, not damage
                    top.dext.centurybridge.data.FullAudit.record(cls + "." + mName + mDesc, "datagen-skip");
                    return;
                }
                orphanCandidates.add(new String[] {mName, mDesc, overridden, side});
            }

            @Override
            public void visit(int ver, int acc, String name, String sig, String superName, String[] ifaces) {
                cls = name;
                itfs = ifaces;
                int brokenBefore = brokenSink.size();
                classAccess[0] = acc;
                superOf[0] = superName;
                checkClass(superName);
                if (!chain.superRenames.isEmpty()) {
                    if (superName != null && chain.superRenames.containsKey(superName)) {
                        needSuperRename[0] = true;
                    }
                    if (ifaces != null) {
                        for (String i : ifaces) {
                            if (chain.superRenames.containsKey(i)) {
                                needSuperRename[0] = true;
                            }
                        }
                    }
                }
                // the hierarchy itself must load: a superclass that is gone, final
                // (26.2 Enchantment is a final record) or an interface on the target,
                // or an implemented type that is gone / no longer an interface, is
                // refused by the JVM before any bridge can run. Ledgered or not, the
                // mod's verdict must say so.
                if (superName != null && superName.startsWith("net/minecraft/")) {
                    String why = chain.hierarchyBreak(superName, false);
                    if (why != null && !chain.sideOf(superName).equals("datagen")
                            && !chain.superRenames.containsKey(superName)
                            && !name.contains("gametest") && !name.contains("/test/")) {
                        brokenSink.add(tag + name + " extends " + superName + " (" + why + ")"
                            + (chain.sideOf(superName).equals("client") ? " [client]" : ""));
                    }
                }
                // a dead interface in the implements clause kills the class at
                // load exactly like a dead superclass does (26.2: FlyingAnimal)
                if (ifaces != null) {
                    for (String i : ifaces) {
                        checkClass(i);
                        if (i.startsWith("net/minecraft/")) {
                            String why = chain.hierarchyBreak(i, true);
                            if (why != null && !chain.sideOf(i).equals("datagen")
                                    && !chain.superRenames.containsKey(i)
                                    && !name.contains("gametest") && !name.contains("/test/")) {
                                brokenSink.add(tag + name + " implements " + i + " (" + why + ")"
                                    + (chain.sideOf(i).equals("client") ? " [client]" : ""));
                            }
                        }
                    }
                }
                broken = brokenSink.size() > brokenBefore;
            }

            @Override
            public MethodVisitor visitMethod(int acc, String mName, String mDesc, String sig, String[] ex) {
                declaredShapes.add(mName + mDesc);
                if (chain.blockUseSplit && mName.equals("method_9534") && mDesc.equals(
                        "(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;"
                        + "Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;Lnet/minecraft/class_3965;)"
                        + "Lnet/minecraft/class_1269;")) {
                    hasOldUse[0] = true;
                }
                if (chain.blockUseSplit && mName.equals("method_55766")) {
                    hasNewUse[0] = true;
                }
                String[] hk = chain.lookupHooks.get(mName);
                if (hk != null) {
                    if (mDesc.equals(hk[0])) {
                        declaredOldHooks.add(mName);
                    } else if (mDesc.equals(hk[1])) {
                        declaredNewHooks.add(mName);
                    }
                }
                boolean hooked = false;
                if ((acc & (Opcodes.ACC_STATIC | Opcodes.ACC_ABSTRACT)) == 0) {
                    for (Chain.OverrideHook oh : chain.overrideHooksForOld(mName, mDesc)) {
                        if (oh.onlyUnder != null && !isUnder(superOf[0], oh.onlyUnder, jarSuper, chain)) {
                            continue;
                        }
                        if (oh.notUnder != null && isUnder(superOf[0], oh.notUnder, jarSuper, chain)) {
                            continue;
                        }
                        declaredOldOverrides.add(oh);
                        hooked = true;
                    }
                }
                // lookup hooks and the block-use split reconnect their old shapes themselves
                boolean reconnected = (hk != null && mDesc.equals(hk[0]))
                    || (chain.blockUseSplit && mName.equals("method_9534"));
                if (!hooked && !reconnected) {
                    checkOrphan(acc, mName, mDesc);
                }
                Chain.OverrideHook nh = chain.overrideHookForNew(mName, mDesc);
                if (nh != null) {
                    declaredNewOverrides.add(nh);
                }
                return new MethodVisitor(Opcodes.ASM9) {
                    /** NEW sites awaiting their <init>, LIFO per type: an <init> no pending
                     *  NEW pairs with is a super()/this() delegation. Every type is tracked --
                     *  tracking only factory-redirected owners filed every NEW site of any
                     *  other class as a super-constructor call (90 Box(BlockPos, BlockPos)
                     *  NEW sites read as SUPER-CTOR-ISSUE at 1.20.4) */
                    final Map<String, java.util.ArrayDeque<Boolean>> pendingNew = new HashMap<>();

                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        checkClass(owner);
                        boolean newSite = false;
                        if (name.equals("<init>")) {
                            java.util.ArrayDeque<Boolean> st = pendingNew.get(owner);
                            newSite = st != null && !st.isEmpty();
                            if (newSite) {
                                st.pop();
                            }
                        }
                        // lookup-hook call sites match on ANY owner: a mod calls
                        // through whatever static type it holds -- its own
                        // BlockEntity subclass as often as the vanilla owner.
                        // Rewriting to the new shape holds either way: the
                        // subclass declares the old shape (and gets a synthesized
                        // new-shape forward) or inherits the vanilla new shape.
                        String[] lhk = chain.lookupHooks.get(name);
                        if (lhk != null && desc.equals(lhk[0])) {
                            hookCallSites.add(owner + "." + name + desc);
                            top.dext.centurybridge.data.FullAudit.record(
                                owner + "." + name + desc, "lookup-hooked");
                            return;
                        }
                        // the call-site key is what the rewrite pass sees; the chain is
                        // consulted on the VANILLA owner, which differs when the site
                        // names a mod class an inherited vanilla member is reached through
                        final String site = owner + "." + name + desc;
                        if (!Chain.minecraftOwned(owner)) {
                            String inh = name.equals("<init>") ? null
                                : inheritedVanillaOwner(owner, name + desc, layer);
                            if (inh == null) {
                                // library owners are outside the stub diffs, but a
                                // redirect wired explicitly (DFU's Codec.dispatch
                                // contract change hides behind generic erasure)
                                // still applies
                                String lrt = chain.instanceRedirects.get(site);
                                if (lrt != null && op != Opcodes.INVOKESTATIC && !name.equals("<init>")) {
                                    instRedirects.put(site, lrt);
                                    top.dext.centurybridge.data.FullAudit.record(site, "instance-redirect");
                                }
                                String lsrt = chain.staticRedirects.get(site);
                                if (lsrt != null && op == Opcodes.INVOKESTATIC) {
                                    redirects.put(site, lsrt);
                                    top.dext.centurybridge.data.FullAudit.record(site, "static-redirect");
                                }
                                return; // mod-owned members (incl. overrides of dead interface methods) still exist
                            }
                            // TestScreen.method_25420(ctx) is Screen.method_25420(ctx): the
                            // mod class declares no such member, so the segment's verdict on
                            // the vanilla one -- and its cover / redirect / ledger -- applies
                            // here exactly as if the site named the vanilla class
                            owner = inh;
                            emitOwner.put(site, inh);
                        }
                        if (chain.classRenames.containsKey(owner)) {
                            // a class merged into a surviving VANILLA class (BakedModel ->
                            // BlockStateModel) keeps its old members only where a redirect is
                            // wired explicitly on the old owner; the Statics overload takes the
                            // renamed type because the emitted descriptor goes through the same
                            // ClassRemapper. Everything else lives on the facade / renamed class.
                            String rkey = owner + "." + name + desc;
                            String rirt = chain.instanceRedirects.get(rkey);
                            if (rirt != null && op != Opcodes.INVOKESTATIC && !name.equals("<init>")) {
                                instRedirects.put(site, rirt);
                                top.dext.centurybridge.data.FullAudit.record(rkey, "instance-redirect");
                                return;
                            }
                            String rsrt = chain.staticRedirects.get(rkey);
                            if (rsrt != null && op == Opcodes.INVOKESTATIC) {
                                redirects.put(site, rsrt);
                                top.dext.centurybridge.data.FullAudit.record(rkey, "static-redirect");
                                return;
                            }
                            String gap = chain.facadeGaps.get(rkey);
                            if (gap != null) {
                                // the facade does not provide this shape: a dead member like any other
                                rpt.ledgered++;
                                top.dext.centurybridge.data.FullAudit.record(rkey, "LEDGERED[" + chain.sideOf(owner)
                                    + "] facade gap: " + gap);
                                if (!name.equals("<init>")) {
                                    tombstone.put(site, "facade " + chain.classRenames.get(owner));
                                    silentTombstones.add(site);
                                }
                            }
                            return; // owner facade-renamed; the member lives on the facade
                        }
                        String key = owner + "." + name + desc;
                        if (name.equals("<init>")) {
                            // The constructor pre-pass already rewrote every site it
                            // could (factory at NEW, reshape/adapt anywhere) and
                            // credited them in the audit; this pass runs on the
                            // rewritten bytes, so a dead shape still called here is the
                            // residue. Damage whichever way it is called -- the label
                            // keeps super()/this() apart from NEW sites because only
                            // the former can never take a factory.
                            boolean dead;
                            if (!chain.targetCtors.isEmpty()) {
                                dead = chain.targetCtors.containsKey(owner) && !chain.targetCtors.get(owner).contains(desc);
                            } else {
                                // no target stub: wiring is the only knowledge -- a wired
                                // site the pre-pass could not rewrite is dead by definition
                                dead = chain.ctorRedirects.containsKey(key) || chain.superCtorAdapts.containsKey(key)
                                    || chain.superCtorReshapes.containsKey(key);
                            }
                            if (dead && chain.resolveClass(owner).level() == Chain.Level.OK) {
                                String cside = chain.sideOf(owner);
                                String what = newSite ? "ctor" : "super-ctor";
                                if (cside.equals("datagen")) {
                                    rpt.datagenSkipped++;
                                    top.dext.centurybridge.data.FullAudit.record(key, "datagen-skip");
                                    return;
                                }
                                if (chain.ledgerReason(owner, "<init>", desc) != null) {
                                    rpt.ledgered++;
                                    top.dext.centurybridge.data.FullAudit.record(key,
                                        "LEDGERED[" + cside + "] " + what + ": " + chain.ledgerReason(owner, "<init>", desc));
                                    return;
                                }
                                top.dext.centurybridge.data.FullAudit.record(key,
                                    (newSite ? "CTOR-ISSUE[" : "SUPER-CTOR-ISSUE[") + cside + "]");
                                // a super()/this() into a dead constructor means the subclass
                                // cannot be instantiated at all (NoSuchMethodError inside its
                                // own <init>): counted exactly like a NEW site
                                String cline = tag + "L3 " + (newSite ? "constructor: " : "super-constructor: ")
                                    + shortName(owner) + ".<init>" + desc;
                                if (cside.equals("client")) {
                                    rpt.clientIssues.add(tag + "[client] " + cline);
                                } else {
                                    issues.add(cline);
                                }
                                return;
                            }
                            return; // live constructor (a dead owner is reported by checkClass)
                        }
                        if (op == Opcodes.INVOKESPECIAL && chain.superRedirects.containsKey(key)) {
                            superRedirectSites.put(site, chain.superRedirects.get(key));
                            top.dext.centurybridge.data.FullAudit.record(key, "super-redirect");
                            return;
                        }
                        // a shim on any ancestor satisfies this call site: the mod
                        // holds a subclass reference, but the member is declared
                        // (and bridged) further up
                        if (chain.coveredAnywhere(owner, name + desc)) {
                            top.dext.centurybridge.data.FullAudit.record(key, "covered");
                            return; // restored at runtime by a shim mixin
                        }
                        String rt = chain.redirectAnywhere(chain.staticRedirects, owner, name + desc);
                        if (rt != null && op == Opcodes.INVOKESTATIC) {
                            redirects.put(site, rt);
                            top.dext.centurybridge.data.FullAudit.record(key, "static-redirect");
                            return;
                        }
                        String irtHome = chain.instanceRedirectHome(chain.instanceRedirects, owner, name + desc);
                        if (irtHome != null && (op == Opcodes.INVOKEVIRTUAL || op == Opcodes.INVOKEINTERFACE || op == Opcodes.INVOKESPECIAL) && !name.equals("<init>")) {
                            instRedirects.put(site, chain.instanceRedirects.get(irtHome + "." + name + desc));
                            // the bridge static takes the receiver typed as the class the
                            // wiring keys the member on; a call through a subclass (vanilla
                            // or the mod's own) passes an assignable receiver. Naming the
                            // call-site class instead demanded one overload per subclass
                            // ever called through -- and NoSuchMethodError where it was missing.
                            emitOwner.put(site, irtHome);
                            top.dext.centurybridge.data.FullAudit.record(key, "instance-redirect");
                            return;
                        }
                        String mrn = chain.methodRenames.get(key);
                        if (mrn != null) {
                            mthRenames.put(site, mrn);
                            top.dext.centurybridge.data.FullAudit.record(key, "method-rename");
                            return;
                        }
                        Chain.Issue r = chain.resolveMember('m', name);
                        if (r.level() == Chain.Level.OK && !name.startsWith("<") && !Chain.intermediaryClass(owner)
                                && Boolean.FALSE.equals(chain.resolvesOnTarget(owner, name + desc))) {
                            // classes Mojang never obfuscated (blaze3d, ...) keep real member names,
                            // which the death tables -- keyed by intermediary name -- cannot see go:
                            // the target stub is the judge. (On an intermediary-named class a
                            // real-named member is a JDK override or another mod's injection.)
                            r = new Chain.Issue(Chain.Level.L3, "target-decl");
                        }
                        if (r.level() == Chain.Level.OK) {
                            return;
                        }
                        String side = chain.sideOf(owner);
                        if (side.equals("datagen")) {
                            rpt.datagenSkipped++; // dev-time bytecode, never executes in play
                            top.dext.centurybridge.data.FullAudit.record(key, "datagen-skip");
                            return;
                        }
                        if (chain.ledgerReason(owner, name, desc) != null) {
                            rpt.ledgered++;
                            top.dext.centurybridge.data.FullAudit.record(key,
                                "LEDGERED[" + side + "]@" + r.boundary() + ": " + chain.ledgerReason(owner, name, desc));
                            if (r.level() == Chain.Level.L3 && !name.equals("<init>")
                                    && chain.resolveClass(owner).level() == Chain.Level.OK) {
                                tombstone.put(site, r.boundary()); // still fail lazily with a message
                                silentTombstones.add(site);        // but never as an issue line
                            }
                            return;
                        }
                        if (r.level() == Chain.Level.L3 && !name.equals("<init>")
                                && chain.resolveClass(owner).level() == Chain.Level.OK) {
                            tombstone.put(site, r.boundary());
                            top.dext.centurybridge.data.FullAudit.record(key, "TOMBSTONE[" + side + "]@" + r.boundary());
                            if (side.equals("client")) {
                                clientTombstones.add(site);
                            }
                        } else {
                            top.dext.centurybridge.data.FullAudit.record(key,
                                "ISSUE[" + side + "] " + r.level() + "@" + r.boundary());
                            String line = tag + r.level() + " method @" + r.boundary() + ": " + shortName(owner) + "." + name;
                            if (side.equals("client")) {
                                rpt.clientIssues.add(tag + "[client] " + line);
                            } else {
                                issues.add(line);
                            }
                        }
                    }

                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        checkClass(owner);
                        // call-site key vs vanilla owner: see visitMethodInsn
                        final String fsite = owner + "." + name + ":" + desc;
                        if (!owner.startsWith("net/minecraft/")) {
                            String inh = inheritedVanillaOwner(owner, name + ":" + desc, layer);
                            if (inh == null) {
                                // fabric-API fields move too (ClientTickEvents world->level):
                                // rename/redirect them like classRenames does for any owner.
                                // Everything else off the vanilla surface is not our damage.
                                String frn = chain.fieldRenames.get(fsite);
                                if (frn != null) {
                                    fldRenames.put(fsite, frn);
                                    top.dext.centurybridge.data.FullAudit.record(fsite, "field-rename");
                                    return;
                                }
                                String ffrt = chain.fieldRedirects.get(fsite);
                                if (ffrt != null && op == Opcodes.GETSTATIC) {
                                    fldRedirects.put(fsite, ffrt);
                                    top.dext.centurybridge.data.FullAudit.record(fsite, "field-redirect");
                                    return;
                                }
                                String ffgt = chain.fieldGetters.get(fsite);
                                if (ffgt != null && op == Opcodes.GETSTATIC) {
                                    fldGetters.put(fsite, ffgt);
                                    top.dext.centurybridge.data.FullAudit.record(fsite, "field-getter");
                                    return;
                                }
                                return;
                            }
                            // this.field_22789 inside a Screen subclass: an inherited
                            // vanilla field referenced through the mod's own class
                            owner = inh;
                            emitOwner.put(fsite, inh);
                        }
                        if (chain.classRenames.containsKey(owner)) {
                            String gap = chain.facadeGaps.get(owner + "." + name + ":" + desc);
                            if (gap != null) {
                                rpt.ledgered++;
                                top.dext.centurybridge.data.FullAudit.record(owner + "." + name + ":" + desc,
                                    "LEDGERED[" + chain.sideOf(owner) + "] facade gap: " + gap);
                            }
                            return;
                        }
                        String fkey = owner + "." + name + ":" + desc;
                        if (chain.coveredAnywhere(owner, name + ":" + desc)) {
                            // a shim mixin puts the field back on the target, so
                            // the GETFIELD resolves again at runtime -- fields were
                            // being reported as damage even when covered
                            top.dext.centurybridge.data.FullAudit.record(fkey, "covered");
                            return;
                        }
                        String frt = chain.fieldRedirects.get(fkey);
                        if (frt != null && op == Opcodes.GETSTATIC) {
                            fldRedirects.put(fsite, frt);
                            top.dext.centurybridge.data.FullAudit.record(fkey, "field-redirect");
                            return;
                        }
                        String fgt = chain.fieldGetters.get(fkey);
                        if (fgt != null && op == Opcodes.GETSTATIC) {
                            fldGetters.put(fsite, fgt);
                            top.dext.centurybridge.data.FullAudit.record(fkey, "field-getter");
                            return;
                        }
                        String ifgt = chain.instanceFieldGetters.get(fkey);
                        if (ifgt != null && op == Opcodes.GETFIELD) {
                            instFldGetters.put(fsite, ifgt);
                            top.dext.centurybridge.data.FullAudit.record(fkey, "instance-field-getter");
                            return;
                        }
                        String rename = chain.fieldRenames.get(fkey);
                        if (rename != null) {
                            fldRenames.put(fsite, rename);
                            top.dext.centurybridge.data.FullAudit.record(fkey, "field-rename");
                            return;
                        }
                        Chain.Issue r = chain.resolveMember('f', name);
                        if (r.level() == Chain.Level.OK) {
                            return;
                        }
                        String side = chain.sideOf(owner);
                        if (side.equals("datagen")) {
                            rpt.datagenSkipped++;
                            top.dext.centurybridge.data.FullAudit.record(fkey, "datagen-skip");
                            return;
                        }
                        if (chain.ledgerReason(owner, name, desc) != null) {
                            rpt.ledgered++;
                            top.dext.centurybridge.data.FullAudit.record(fkey,
                                "LEDGERED[" + side + "]@" + r.boundary() + ": " + chain.ledgerReason(owner, name, desc));
                            return;
                        }
                        // the field's TYPE was merged into a surviving VANILLA class via
                        // classRename (e.g. DirectionProperty -> EnumProperty): the ClassRemapper
                        // rewrites this GETSTATIC's type descriptor to the renamed class, which is
                        // exactly what the surviving vanilla field now holds, so the read resolves
                        // at runtime even though the segment marks the old type as changed. Only
                        // vanilla targets qualify -- a facade rename does NOT match the real field's
                        // type, so those stay ISSUE.
                        if (op == Opcodes.GETSTATIC) {
                            org.objectweb.asm.Type ft = org.objectweb.asm.Type.getType(desc);
                            String fe = ft.getSort() == org.objectweb.asm.Type.OBJECT ? ft.getInternalName()
                                      : ft.getSort() == org.objectweb.asm.Type.ARRAY
                                        && ft.getElementType().getSort() == org.objectweb.asm.Type.OBJECT
                                        ? ft.getElementType().getInternalName() : null;
                            String ren = fe == null ? null : chain.classRenames.get(fe);
                            if (ren != null && ren.startsWith("net/minecraft/")) {
                                top.dext.centurybridge.data.FullAudit.record(fkey, "class-renamed-field");
                                return;
                            }
                        }
                        top.dext.centurybridge.data.FullAudit.record(fkey,
                            "ISSUE[" + side + "] " + r.level() + "@" + r.boundary());
                        String line = tag + r.level() + " field @" + r.boundary() + ": " + shortName(owner) + "." + name;
                        if (side.equals("client")) {
                            rpt.clientIssues.add(tag + "[client] " + line);
                        } else {
                            issues.add(line);
                        }
                    }

                    @Override
                    public void visitTypeInsn(int op, String type) {
                        if (op == Opcodes.INSTANCEOF) {
                            String pred = chain.instanceOfRedirects.get(type);
                            if (pred != null) {
                                instOfSites.put(type, pred);
                                top.dext.centurybridge.data.FullAudit.record("instanceof " + type, "instanceof-redirect");
                                return; // answered by the predicate; the type itself is not loaded here
                            }
                        }
                        if (op == Opcodes.NEW) {
                            pendingNew.computeIfAbsent(type, k -> new java.util.ArrayDeque<>()).push(Boolean.TRUE);
                        }
                        checkClass(type);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name, String desc,
                            org.objectweb.asm.Handle bsm, Object... bsmArgs) {
                        // method references to dead/redirected methods hide inside
                        // indy bootstrap arguments -- treat each handle like a call site
                        for (Object arg : bsmArgs) {
                            if (!(arg instanceof org.objectweb.asm.Handle h)) {
                                continue;
                            }
                            // handle owner vs vanilla owner: a method reference through the
                            // mod's own class (this::method_25420) resolves like a call
                            final String site = h.getOwner() + "." + h.getName() + h.getDesc();
                            String hOwner = h.getOwner();
                            if (!hOwner.startsWith("net/minecraft/")) {
                                String inh = h.getName().equals("<init>") ? null
                                    : inheritedVanillaOwner(hOwner, h.getName() + h.getDesc(), layer);
                                if (inh == null) {
                                    // library-owner method references carry the same
                                    // explicitly-wired redirects as direct calls
                                    // (Codec::dispatch as a lambda breaks identically)
                                    boolean hStatic = h.getTag() == Opcodes.H_INVOKESTATIC;
                                    String lrt = chain.instanceRedirects.get(site);
                                    if (lrt != null && !hStatic && !h.getName().equals("<init>")) {
                                        instRedirects.put(site, lrt);
                                        top.dext.centurybridge.data.FullAudit.record(site, "instance-redirect");
                                    }
                                    String lsrt = chain.staticRedirects.get(site);
                                    if (lsrt != null && hStatic) {
                                        redirects.put(site, lsrt);
                                        top.dext.centurybridge.data.FullAudit.record(site, "static-redirect");
                                    }
                                    continue;
                                }
                                hOwner = inh;
                                emitOwner.put(site, inh);
                            }
                            if (!chain.classRenames.containsKey(hOwner)) {
                                String key = hOwner + "." + h.getName() + h.getDesc();
                                if (chain.shimCovers.contains(key)) {
                                    top.dext.centurybridge.data.FullAudit.record(key, "covered");
                                    continue;
                                }
                                String rt = chain.staticRedirects.get(key);
                                // same resolution as a direct call: class, superclasses, superinterfaces
                                String irtHome = h.getTag() == Opcodes.H_INVOKESTATIC ? null
                                    : chain.instanceRedirectHome(chain.instanceRedirects, hOwner, h.getName() + h.getDesc());
                                String irt = irtHome == null ? null
                                    : chain.instanceRedirects.get(irtHome + "." + h.getName() + h.getDesc());
                                if (rt != null || irt != null) {
                                    redirects.putAll(rt != null ? Map.of(site, rt) : Map.of());
                                    if (irt != null) {
                                        instRedirects.put(site, irt);
                                        emitOwner.put(site, irtHome);
                                    }
                                    top.dext.centurybridge.data.FullAudit.record(key,
                                        rt != null ? "static-redirect" : "instance-redirect");
                                    continue;
                                }
                                Chain.Issue r = chain.resolveMember('m', h.getName());
                                if (r.level() == Chain.Level.L3 && !h.getName().equals("<init>")
                                        && chain.resolveClass(hOwner).level() == Chain.Level.OK) {
                                    tombstone.put(site, r.boundary());
                                    if (chain.ledgerReason(hOwner, h.getName(), h.getDesc()) != null) {
                                        rpt.ledgered++;
                                        silentTombstones.add(site);
                                        top.dext.centurybridge.data.FullAudit.record(key,
                                            "LEDGERED[" + chain.sideOf(hOwner) + "]@" + r.boundary()
                                            + ": " + chain.ledgerReason(hOwner, h.getName(), h.getDesc()));
                                    } else {
                                        top.dext.centurybridge.data.FullAudit.record(key,
                                            "TOMBSTONE[" + chain.sideOf(hOwner) + "]@" + r.boundary());
                                    }
                                } else if (r.level() != Chain.Level.OK) {
                                    top.dext.centurybridge.data.FullAudit.record(key,
                                        "ISSUE[" + chain.sideOf(hOwner) + "] " + r.level() + "@" + r.boundary());
                                }
                            }
                        }
                    }
                };
            }

            private void checkClass(String internal) {
                if (internal == null) {
                    return;
                }
                String t = internal;
                while (t.startsWith("[")) {
                    t = t.substring(1);
                }
                if (t.startsWith("L") && t.endsWith(";")) {
                    t = t.substring(1, t.length() - 1);
                }
                if (chain.classRenames.containsKey(t)) {
                    return; // facade-renamed, repaired by the rewrite pass
                }
                Chain.Issue r = chain.resolveClass(t);
                if (r.level() != Chain.Level.OK) {
                    String side = chain.sideOf(t);
                    if (chain.ledgerReason(t, null) != null) {
                        rpt.ledgered++;
                        top.dext.centurybridge.data.FullAudit.record(t,
                            "LEDGERED-CLASS[" + side + "]@" + r.boundary() + ": " + chain.ledgerReason(t, null));
                    } else if (side.equals("datagen")) {
                        rpt.datagenSkipped++;
                        top.dext.centurybridge.data.FullAudit.record(t, "datagen-skip");
                    } else if (side.equals("client")) {
                        top.dext.centurybridge.data.FullAudit.record(t,
                            "CLASS-ISSUE[client] " + r.level() + "@" + r.boundary());
                        rpt.clientIssues.add(tag + "[client] " + r.level() + " class @" + r.boundary() + ": " + t);
                    } else {
                        top.dext.centurybridge.data.FullAudit.record(t,
                            "CLASS-ISSUE[" + side + "] " + r.level() + "@" + r.boundary());
                        issues.add(tag + r.level() + " class @" + r.boundary() + ": " + t);
                    }
                }
            }
        };
        new ClassReader(bytes).accept(analysis, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        if (redirects.isEmpty() && instRedirects.isEmpty() && fldRedirects.isEmpty()
                && fldGetters.isEmpty() && instFldGetters.isEmpty() && instOfSites.isEmpty()
                && fldRenames.isEmpty() && mthRenames.isEmpty() && tombstone.isEmpty()
                && !needClassRename && !needIfaceFix && hookCallSites.isEmpty() && superRedirectSites.isEmpty()
                && !needSuperRename[0] && adapterApply.isEmpty()
                && !(hasOldUse[0] && !hasNewUse[0])
                && declaredOldHooks.stream().allMatch(declaredNewHooks::contains)
                && declaredOldOverrides.stream().allMatch(declaredNewOverrides::contains)) {
            return ctorChanged ? bytes : null; // "unchanged" must not discard the constructor pre-pass
        }
        final java.util.List<Chain.OverrideHook> reconnectOverrides = new ArrayList<>();
        if ((classAccess[0] & Opcodes.ACC_INTERFACE) == 0) {
            for (Chain.OverrideHook h : declaredOldOverrides) {
                if (!declaredNewOverrides.contains(h) && !reconnectOverrides.contains(h)) {
                    // two old overrides folding into ONE new shape (Entity.interact +
                    // interactAt -> interact(player, hand, pos)): a second synthesized
                    // method with the same name+desc is a ClassFormatError, so the
                    // first hook (config order) wins and the other is noted
                    boolean dup = false;
                    for (Chain.OverrideHook x : reconnectOverrides) {
                        if (x.newName.equals(h.newName) && x.newDesc.equals(h.newDesc)) {
                            dup = true;
                            break;
                        }
                    }
                    if (dup) {
                        rpt.notes.add(tag + "override-hook skipped (new shape already synthesized from another old override): "
                            + h.oldName + h.oldDesc);
                        top.dext.centurybridge.data.FullAudit.record(h.oldName + h.oldDesc, "override-hook-shadowed");
                        continue;
                    }
                    reconnectOverrides.add(h);
                    rpt.notes.add(tag + "override-hooked: " + h.oldName + h.oldDesc + " -> " + h.newName + h.newDesc);
                    top.dext.centurybridge.data.FullAudit.record(h.oldName + h.oldDesc, "override-hooked");
                }
            }
        }
        final boolean reconnectUse = hasOldUse[0] && !hasNewUse[0];
        final Set<String> reconnectHooks = new HashSet<>(declaredOldHooks);
        reconnectHooks.removeAll(declaredNewHooks);
        for (String k : redirects.keySet()) {
            rpt.notes.add(tag + "static-redirected: " + k + " -> " + redirects.get(k));
        }
        for (String k : instRedirects.keySet()) {
            rpt.notes.add(tag + "instance-redirected: " + k + " -> " + instRedirects.get(k));
        }
        for (String k : fldRedirects.keySet()) {
            rpt.notes.add(tag + "field-redirected: " + k + " -> " + fldRedirects.get(k));
        }
        for (String k : fldGetters.keySet()) {
            rpt.notes.add(tag + "field-gettered: " + k + " -> " + fldGetters.get(k));
        }
        for (String k : instFldGetters.keySet()) {
            rpt.notes.add(tag + "instance-field-gettered: " + k + " -> " + instFldGetters.get(k));
        }
        for (String k : instOfSites.keySet()) {
            rpt.notes.add(tag + "instanceof-redirected: " + k + " -> " + instOfSites.get(k));
        }
        for (String k : hookCallSites) {
            rpt.notes.add(tag + "lookup-hooked: " + k);
        }
        for (Map.Entry<String, String> t : tombstone.entrySet()) {
            if (silentTombstones.contains(t.getKey())) {
                continue; // ledgered: rewritten to a descriptive lazy-fail, accounted elsewhere
            }
            String line = tag + "tombstoned (lazy-fail) @" + t.getValue() + ": " + t.getKey();
            if (clientTombstones.contains(t.getKey())) {
                rpt.clientIssues.add(tag + "[client] " + line);
            } else {
                issues.add(line);
            }
        }

        ClassReader cr = new ClassReader(bytes);
        final String[] clsName = {cr.getClassName()};
        // COMPUTE_MAXS: the lookup-hook call-site fix pushes an extra value, so
        // the original class's declared max_stack can no longer be trusted --
        // ironchests only survived on incidental headroom from adjacent code
        org.objectweb.asm.ClassWriter cw =
            new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        // last stage before the writer, after every rewrite and rename: a call is
        // judged by the owner it finally names
        final Set<String> kindFixed = new java.util.TreeSet<>();
        ClassVisitor kinds = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int acc, String mName, String mDesc, String sig, String[] ex) {
                return new KindNormalizer(super.visitMethod(acc, mName, mDesc, sig, ex), chain, kindFixed);
            }
        };
        ClassVisitor sink = needClassRename
            ? new org.objectweb.asm.commons.ClassRemapper(kinds,
                new org.objectweb.asm.commons.SimpleRemapper(chain.classRenames))
            : kinds;
        // super/implements-only rename layer: rewrites the hierarchy declaration
        // and super-invocations to the facade, leaving every other reference to
        // the dead type on the real type. Sits outside the classRename remapper
        // so the two never fight over the same owner.
        if (needSuperRename[0]) {
            final ClassVisitor downstream = sink;
            sink = new ClassVisitor(Opcodes.ASM9, downstream) {
                @Override
                public void visit(int ver, int acc, String name, String sig, String superName, String[] ifaces) {
                    String sn = superName != null ? chain.superRenames.getOrDefault(superName, superName) : superName;
                    String[] ifs = ifaces;
                    if (ifaces != null) {
                        ifs = new String[ifaces.length];
                        for (int i = 0; i < ifaces.length; i++) {
                            ifs[i] = chain.superRenames.getOrDefault(ifaces[i], ifaces[i]);
                        }
                    }
                    // the generic signature still names the old super/interfaces;
                    // drop it rather than emit one that disagrees with the facade
                    // (the JVM does not need it, and a stale signature is a verify risk)
                    super.visit(ver, acc, name, null, sn, ifs);
                }

                @Override
                public MethodVisitor visitMethod(int a, String mn, String md, String ms, String[] ex) {
                    MethodVisitor mv = super.visitMethod(a, mn, md, ms, ex);
                    if (mv == null) {
                        return null;
                    }
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitMethodInsn(int op, String owner, String n, String d, boolean itf) {
                            // super-invocations (INVOKESPECIAL) to the renamed type
                            // must follow the hierarchy to the facade; a facade
                            // mirroring the method surface answers them. Other
                            // opcodes keep the real owner (no type-split).
                            if (op == Opcodes.INVOKESPECIAL && chain.superRenames.containsKey(owner)) {
                                super.visitMethodInsn(op, chain.superRenames.get(owner), n, d, itf);
                            } else {
                                super.visitMethodInsn(op, owner, n, d, itf);
                            }
                        }
                    };
                }
            };
        }
        cr.accept(new ClassVisitor(Opcodes.ASM9, sink) {
            @Override
            public void visit(int ver, int acc, String name, String sig, String superName, String[] ifaces) {
                boolean bridged = reconnectOverrides.stream().anyMatch(h -> h.adapter != null);
                if (!adapterApply.isEmpty() || bridged) {
                    java.util.LinkedHashSet<String> all = new java.util.LinkedHashSet<>();
                    if (ifaces != null) {
                        all.addAll(List.of(ifaces));
                    }
                    StringBuilder extra = new StringBuilder();
                    for (Chain.AdapterHook h : adapterApply) {
                        if (all.add(h.adapter)) {
                            extra.append('L').append(h.adapter).append(';');
                        }
                    }
                    // a bridged override hook reaches the old override through its adapter
                    for (Chain.OverrideHook h : reconnectOverrides) {
                        if (h.adapter != null && all.add(h.adapter)) {
                            extra.append('L').append(h.adapter).append(';');
                        }
                    }
                    ifaces = all.toArray(new String[0]);
                    // a generic class signature lists the interfaces too: append, never drop
                    if (sig != null) {
                        sig = sig + extra;
                    }
                }
                super.visit(ver, acc, name, sig, superName, ifaces);
            }

            @Override
            public void visitEnd() {
                for (Chain.AdapterHook h : adapterApply) {
                    for (String[] sy : h.synth) {
                        if (declaredShapes.contains(sy[0] + sy[1])) {
                            continue; // the mod already speaks the new shape
                        }
                        // new shape vanilla calls -> static bridge(this-as-adapter, args...)
                        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, sy[0], sy[1], null, null);
                        mv.visitCode();
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        int slot = 1;
                        for (org.objectweb.asm.Type t : org.objectweb.asm.Type.getArgumentTypes(sy[1])) {
                            mv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), slot);
                            slot += t.getSize();
                        }
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, sy[2], sy[3], sy[4], false);
                        org.objectweb.asm.Type ret = org.objectweb.asm.Type.getReturnType(sy[1]);
                        org.objectweb.asm.Type got = org.objectweb.asm.Type.getReturnType(sy[4]);
                        if (ret.getSort() >= org.objectweb.asm.Type.ARRAY && !ret.equals(got)) {
                            mv.visitTypeInsn(Opcodes.CHECKCAST, ret.getInternalName());
                        }
                        mv.visitInsn(ret.getOpcode(Opcodes.IRETURN));
                        mv.visitMaxs(0, 0);
                        mv.visitEnd();
                        rpt.notes.add(tag + "adapter-synthesized: " + clsName[0] + "." + sy[0] + sy[1]);
                    }
                }
                for (Chain.OverrideHook h : reconnectOverrides) {
                    synthesizeOverride(this, h, clsName[0], superOf[0]);
                }
                for (String hook : reconnectHooks) {
                    String[] hk = chain.lookupHooks.get(hook);
                    // new shape forwards to the mod's old override, dropping the lookup
                    MethodVisitor hv = super.visitMethod(Opcodes.ACC_PROTECTED, hook, hk[1], null, null);
                    hv.visitCode();
                    hv.visitVarInsn(Opcodes.ALOAD, 0);
                    boolean takesNbt = hk[0].startsWith("(Lnet/minecraft/class_2487;");
                    if (takesNbt) {
                        hv.visitVarInsn(Opcodes.ALOAD, 1);
                    }
                    hv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clsName[0], hook, hk[0], false);
                    hv.visitInsn(hk[0].endsWith(")V") ? Opcodes.RETURN : Opcodes.ARETURN);
                    hv.visitMaxs(3, 3);
                    hv.visitEnd();
                }
                if (reconnectUse) {
                    // protected class_1269 method_55766(state, world, pos, player, hit)
                    //   -> this.method_9534(state, world, pos, player, MAIN_HAND, hit)
                    // The old override keeps working because the new hook vanilla
                    // actually calls now forwards into it.
                    MethodVisitor mv = super.visitMethod(Opcodes.ACC_PROTECTED, "method_55766",
                        "(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;"
                        + "Lnet/minecraft/class_1657;Lnet/minecraft/class_3965;)Lnet/minecraft/class_1269;",
                        null, null);
                    mv.visitCode();
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitVarInsn(Opcodes.ALOAD, 2);
                    mv.visitVarInsn(Opcodes.ALOAD, 3);
                    mv.visitVarInsn(Opcodes.ALOAD, 4);
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/class_1268",
                        "field_5808", "Lnet/minecraft/class_1268;");
                    mv.visitVarInsn(Opcodes.ALOAD, 5);
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clsName[0], "method_9534",
                        "(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;"
                        + "Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;Lnet/minecraft/class_3965;)"
                        + "Lnet/minecraft/class_1269;", false);
                    mv.visitInsn(Opcodes.ARETURN);
                    mv.visitMaxs(7, 6);
                    mv.visitEnd();
                }
                super.visitEnd();
            }

            @Override
            public MethodVisitor visitMethod(int acc, String mName, String mDesc, String sig, String[] ex) {
                for (Chain.AdapterHook h : adapterApply) {
                    if (h.requires.contains(mName + mDesc)) {
                        // the bridge reaches it through a (public) interface method: a
                        // protected implementation is an IllegalAccessError at that call
                        acc = (acc & ~(Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) | Opcodes.ACC_PUBLIC;
                    }
                }
                for (Chain.OverrideHook h : reconnectOverrides) {
                    if (h.adapter != null && h.oldName.equals(mName) && h.oldDesc.equals(mDesc)) {
                        acc = (acc & ~(Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) | Opcodes.ACC_PUBLIC;
                    }
                }
                MethodVisitor mv = super.visitMethod(acc, mName, mDesc, sig, ex);
                return new MethodVisitor(Opcodes.ASM9, mv) {
                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        String[] hk = chain.lookupHooks.get(name);
                        if (hk != null && desc.equals(hk[0])) {
                            // append the builtin lookup and call the new shape;
                            // the world's manager would be more precise, but the
                            // builtin wrapper resolves everything vanilla
                            // serialization needs here. Any owner qualifies:
                            // mod-owned static types either declare the old
                            // shape (and get a forward synthesized) or inherit
                            // the vanilla new shape.
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, chain.lookupRt,
                                "cbLookup", chain.lookupDesc, false);
                            super.visitMethodInsn(op, owner, name, hk[1], itf);
                            return;
                        }
                        String key = owner + "." + name + desc;
                        // receiver type named in an emitted static descriptor: the vanilla
                        // class when the site reaches an inherited member through a mod class
                        String recv = emitOwner.getOrDefault(key, owner);
                        String srt = superRedirectSites.get(key);
                        if (op == Opcodes.INVOKESPECIAL && !name.equals("<init>") && srt != null) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(srt), redirName(srt, name),
                                "(L" + recv + ";" + desc.substring(1), false);
                            return;
                        }
                        String rt = redirects.get(key);
                        if (rt != null && op == Opcodes.INVOKESTATIC) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(rt),
                                redirName(rt, name), desc, false);
                            return;
                        }
                        String irt = instRedirects.get(key);
                        if (irt != null && (op == Opcodes.INVOKEVIRTUAL || op == Opcodes.INVOKEINTERFACE || op == Opcodes.INVOKESPECIAL) && !name.equals("<init>")) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(irt),
                                redirName(irt, name),
                                "(L" + recv + ";" + desc.substring(1), false);
                            return;
                        }
                        String mrn = mthRenames.get(key);
                        if (mrn != null) {
                            super.visitMethodInsn(op, owner, mrn, desc, itf);
                            return;
                        }
                        String boundary = tombstone.get(key);
                        if (boundary != null) {
                            Tombstones.Stub s = reg.get(recv, name, desc, op == Opcodes.INVOKESTATIC, boundary);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, reg.clsName, s.name(), s.staticDesc(), false);
                            return;
                        }
                        super.visitMethodInsn(op, owner, name, desc, itf);
                    }

                    @Override
                    public void visitTypeInsn(int op, String type) {
                        if (op == Opcodes.INSTANCEOF && instOfSites.containsKey(type)) {
                            String pv = instOfSites.get(type);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(pv),
                                redirName(pv, "cbInstanceOf"), "(Ljava/lang/Object;)Z", false);
                            return;
                        }
                        super.visitTypeInsn(op, type);
                    }

                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        String fkey = owner + "." + name + ":" + desc;
                        if (op == Opcodes.GETFIELD && instFldGetters.containsKey(fkey)) {
                            String fv = instFldGetters.get(fkey);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(fv),
                                redirName(fv, name), "(L" + emitOwner.getOrDefault(fkey, owner) + ";)" + desc, false);
                            return;
                        }
                        if (op == Opcodes.GETSTATIC && fldGetters.containsKey(fkey)) {
                            String fv = fldGetters.get(fkey);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(fv),
                                redirName(fv, name), "()" + desc, false);
                            return;
                        }
                        if (op == Opcodes.GETSTATIC && fldRedirects.containsKey(fkey)) {
                            String fv = fldRedirects.get(fkey);
                            super.visitFieldInsn(Opcodes.GETSTATIC, redirCls(fv),
                                redirName(fv, name), desc);
                            return;
                        }
                        String renamed = fldRenames.get(fkey);
                        if (renamed != null) {
                            super.visitFieldInsn(op, owner, renamed, desc);
                            return;
                        }
                        super.visitFieldInsn(op, owner, name, desc);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name, String desc,
                            org.objectweb.asm.Handle bsm, Object... bsmArgs) {
                        Object[] rewritten = bsmArgs.clone();
                        for (int i = 0; i < rewritten.length; i++) {
                            if (!(rewritten[i] instanceof org.objectweb.asm.Handle h)) {
                                continue;
                            }
                            String key = h.getOwner() + "." + h.getName() + h.getDesc();
                            String recv = emitOwner.getOrDefault(key, h.getOwner());
                            String rt = redirects.get(key);
                            if (rt != null && h.getTag() == Opcodes.H_INVOKESTATIC) {
                                rewritten[i] = new org.objectweb.asm.Handle(
                                    Opcodes.H_INVOKESTATIC, redirCls(rt),
                                    redirName(rt, h.getName()), h.getDesc(), false);
                                continue;
                            }
                            String irt = instRedirects.get(key);
                            if (irt != null && (h.getTag() == Opcodes.H_INVOKEVIRTUAL
                                    || h.getTag() == Opcodes.H_INVOKEINTERFACE)) {
                                rewritten[i] = new org.objectweb.asm.Handle(Opcodes.H_INVOKESTATIC, redirCls(irt),
                                    redirName(irt, h.getName()), "(L" + recv + ";" + h.getDesc().substring(1), false);
                                continue;
                            }
                            String boundary = tombstone.get(key);
                            if (boundary != null) {
                                boolean isStatic = h.getTag() == Opcodes.H_INVOKESTATIC;
                                Tombstones.Stub s = reg.get(recv, h.getName(), h.getDesc(), isStatic, boundary);
                                rewritten[i] = new org.objectweb.asm.Handle(
                                    Opcodes.H_INVOKESTATIC, reg.clsName, s.name(), s.staticDesc(), false);
                            }
                        }
                        super.visitInvokeDynamicInsn(name, desc, bsm, rewritten);
                    }
                };
            }
        }, 0);
        for (String o : kindFixed) {
            rpt.notes.add(tag + "kind-fixed: calls into " + o + " follow its target kind ("
                + (Boolean.TRUE.equals(chain.interfaceOnTarget(o)) ? "interface" : "class") + ") in " + clsName[0]);
        }
        return cw.toByteArray();
    }

    /**
     * Gives every emitted call into an owner of known target kind the opcode and
     * interface flag that kind demands (see {@link Chain#interfaceOnTarget}). A
     * method reference carries its own flag inside the Handle, which the plain
     * call-site fix never sees -- DataResult::success kept crashing until handles
     * were normalized too.
     */
    static final class KindNormalizer extends MethodVisitor {
        private final Chain chain;
        private final Set<String> fixed;

        KindNormalizer(MethodVisitor mv, Chain chain, Set<String> fixed) {
            super(Opcodes.ASM9, mv);
            this.chain = chain;
            this.fixed = fixed;
        }

        @Override
        public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
            Boolean iface = chain.interfaceOnTarget(owner);
            if (iface != null && iface != itf) {
                if (op == Opcodes.INVOKEVIRTUAL && iface) {
                    op = Opcodes.INVOKEINTERFACE;
                } else if (op == Opcodes.INVOKEINTERFACE && !iface) {
                    op = Opcodes.INVOKEVIRTUAL;
                }
                itf = iface;
                fixed.add(owner);
            }
            super.visitMethodInsn(op, owner, name, desc, itf);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, org.objectweb.asm.Handle bsm, Object... args) {
            Object[] out = args.clone();
            for (int i = 0; i < out.length; i++) {
                if (out[i] instanceof org.objectweb.asm.Handle h) {
                    out[i] = fix(h);
                }
            }
            super.visitInvokeDynamicInsn(name, desc, fix(bsm), out);
        }

        @Override
        public void visitLdcInsn(Object value) {
            super.visitLdcInsn(value instanceof org.objectweb.asm.Handle h ? fix(h) : value);
        }

        private org.objectweb.asm.Handle fix(org.objectweb.asm.Handle h) {
            if (h.getTag() < Opcodes.H_INVOKEVIRTUAL) {
                return h; // field handles carry no interface flag
            }
            Boolean iface = chain.interfaceOnTarget(h.getOwner());
            if (iface == null || iface == h.isInterface()) {
                return h;
            }
            int tag = h.getTag();
            if (tag == Opcodes.H_INVOKEVIRTUAL && iface) {
                tag = Opcodes.H_INVOKEINTERFACE;
            } else if (tag == Opcodes.H_INVOKEINTERFACE && !iface) {
                tag = Opcodes.H_INVOKEVIRTUAL;
            }
            fixed.add(h.getOwner());
            return new org.objectweb.asm.Handle(tag, h.getOwner(), h.getName(), h.getDesc(), iface);
        }
    }

    private static byte[] patchFmj(byte[] data, Chain chain, Report rpt) {
        JsonObject fmj = GSON.fromJson(new String(data, StandardCharsets.UTF_8), JsonObject.class);
        if (fmj.has("id")) {
            rpt.modId = fmj.get("id").getAsString();
        }
        if (fmj.has("depends")) {
            JsonObject dep = fmj.getAsJsonObject("depends");
            if (dep.has("minecraft")) {
                dep.add("minecraft", new JsonPrimitive(chain.to));
                rpt.notes.add("minecraft dep -> " + chain.to);
            }
            java.util.List<Map<String, String>> renameSets = new ArrayList<>();
            for (Segment s : chain.segments) {
                renameSets.add(s.depRenames);
            }
            renameSets.add(chain.depRenames);
            for (Map<String, String> set : renameSets) {
                for (Map.Entry<String, String> e : set.entrySet()) {
                    if (dep.has(e.getKey())) {
                        JsonElement v = dep.remove(e.getKey());
                        // several old ids may collapse onto one provider: keep the wider range
                        if (!dep.has(e.getValue())) {
                            dep.add(e.getValue(), v);
                        }
                        rpt.notes.add("dep " + e.getKey() + " -> " + e.getValue());
                    }
                }
            }
        }
        return GSON.toJson(fmj).getBytes(StandardCharsets.UTF_8);
    }

    private static String shortName(String internal) {
        int i = internal.lastIndexOf('/');
        return i < 0 ? internal : internal.substring(i + 1);
    }
}
