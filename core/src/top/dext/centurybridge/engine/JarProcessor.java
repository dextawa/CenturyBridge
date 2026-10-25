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
     * A narrowing hook ({@link Chain.OverrideHook#narrow}) instead tests each
     * such argument against the old type first and, on a value of another
     * type, takes the fall-through path (super.new(..) or the zero value) as
     * if the mod had not overridden anything.
     * Descriptors are emitted in the analysis namespace and pass through the
     * same ClassRemapper as the rest of the class, so a facade-renamed old
     * parameter type lands on the facade.
     */
    /** does W's code (in the analysis namespace) declare the class; false when no code is at hand */
    static boolean wDeclares(Chain chain, String cls) {
        MixinRepair w = MixinBridge.wcode(chain);
        return w != null && w.declares(cls);
    }

    /** verifier frame type for one parameter (locals are never widened here) */
    static Object frameType(org.objectweb.asm.Type t) {
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

    /**
     * The adapter (Chain.lambdaAdapters) for a LambdaMetafactory invokedynamic that builds a functional
     * interface whose single abstract method changed shape, keyed by the interface it produces, the
     * SAM name and the erased SAM type it implements; null for any other invokedynamic.
     */
    private static Chain.LambdaAdapter lambdaAdapterFor(Chain chain, String name, String desc,
                                                        org.objectweb.asm.Handle bsm, Object[] args) {
        if (chain.lambdaAdapters.isEmpty() || !bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")
                || args.length < 1 || !(args[0] instanceof org.objectweb.asm.Type sam)) {
            return null;
        }
        org.objectweb.asm.Type ret = org.objectweb.asm.Type.getReturnType(desc);
        if (ret.getSort() != org.objectweb.asm.Type.OBJECT) {
            return null;
        }
        return chain.lambdaAdapters.get(ret.getInternalName() + "." + name + sam.getDescriptor());
    }

    /**
     * A call (or method reference) naming a class of this jar whose old method a rename-mode
     * {@link Chain#overrideRenames} entry renames (superCalls "rename", no adapter: the member lives on
     * under the new name): the site's class is under the entry's {@code onlyUnder}, so the method it
     * reaches is either the jar's own declaration, renamed with it, or the library's / vanilla's, which
     * the release renamed -- the new name, same owner and descriptor. Left on the old name, a mod's
     * {@code this.onWorldLoad(..)} would resolve to nothing once its own declaration took the new name.
     * Null for any other site (a static, a constructor, an owner outside the jar, adapter-mode entries).
     */
    private static String ownRenamed(String owner, String name, String desc, int op, LayerIndex layer, Chain chain) {
        if (chain.overrideRenames.isEmpty() || op == Opcodes.INVOKESTATIC || name.startsWith("<")
                || !layer.superOf.containsKey(owner)) {
            return null;
        }
        for (Chain.OverrideRename r : chain.overrideRenames) {
            if (r.superCalls && r.adapter == null && r.oldName.equals(name) && r.oldDesc.equals(desc)
                    && (owner.equals(r.onlyUnder) || isUnder(layer.superOf.get(owner),
                        layer.interfacesOf.getOrDefault(owner, List.of()).toArray(new String[0]), r.onlyUnder, layer, chain))) {
                return r.newName;
            }
        }
        return null;
    }

    /**
     * The new name of a LambdaMetafactory invokedynamic's SAM when {@link Chain#methodRenames} renames
     * the abstract method of the interface it produces (found as for a call on that interface); null
     * for any other invokedynamic. The lambda is then spun under the new name: same interface, erased
     * and instantiated types, implementation and captured values -- and the bridges an altMetafactory
     * lists, which share the name.
     */
    private static String lambdaRename(Chain chain, String name, String desc, org.objectweb.asm.Handle bsm, Object[] args) {
        if (chain.methodRenames.isEmpty() || !bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")
                || args.length < 1 || !(args[0] instanceof org.objectweb.asm.Type sam)) {
            return null;
        }
        org.objectweb.asm.Type ret = org.objectweb.asm.Type.getReturnType(desc);
        if (ret.getSort() != org.objectweb.asm.Type.OBJECT) {
            return null;
        }
        String home = chain.methodRenameHome(ret.getInternalName(), name + sam.getDescriptor(), false);
        return home == null ? null : chain.methodRenames.get(home + "." + name + sam.getDescriptor());
    }

    private static void synthesizeOverride(ClassVisitor cv, Chain.OverrideHook h, String cls, String superName) {
        synthesizeOverride(cv, h, cls, superName, null);
    }

    /**
     * The new shape in a mod class; {@code superIface} names the class's direct vanilla superinterface
     * whose default method a guarded or narrowing fall-through reaches (INVOKESPECIAL on the interface)
     * when the superclass chain does not have the new shape (see {@link #defaultHome}); null for super's.
     */
    private static void synthesizeOverride(ClassVisitor cv, Chain.OverrideHook h, String cls, String superName,
                                           String superIface) {
        if (h.frames != null) {
            synthesizeFramedOverride(cv, h, cls, superName, superIface);
            return;
        }
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
        // narrowing forward: an argument the release widened reaches the old override only when
        // vanilla's value is of the old type; any other value goes to super's new shape
        boolean narrowed = false;
        if (h.narrow && h.bridgeName == null) {
            for (int i = 0; i < oa.length; i++) {
                int src = map[i];
                if (!na[src].equals(oa[i]) && oa[i].getSort() >= org.objectweb.asm.Type.ARRAY) {
                    mv.visitVarInsn(Opcodes.ALOAD, slotOf[src]);
                    mv.visitTypeInsn(Opcodes.INSTANCEOF, oa[i].getInternalName());
                    mv.visitJumpInsn(Opcodes.IFEQ, fallthrough);
                    narrowed = true;
                }
            }
        }
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
                String[] w = na[src].equals(oa[i]) ? null : h.wrapFor(na[src], oa[i]);
                if (w == null && !na[src].equals(oa[i]) && h.wraps.size() == 1) {
                    w = h.wraps.get(0); // a single wrap is applied as written (its parameter may be a supertype)
                }
                if (w != null) {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, w[0], w[1], w[2], false);
                } else if (!na[src].equals(oa[i]) && h.narrow && oa[i].getSort() >= org.objectweb.asm.Type.ARRAY) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, oa[i].getInternalName());
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
        // a new shape that returns nothing where the forwarded call answers a value (1.20.2
        // RealmsServerList.updateServersList: the 1.20.1 override returned the list) drops it
        org.objectweb.asm.Type produced = org.objectweb.asm.Type.getReturnType(h.bridgeName != null ? h.bridgeDesc
            : h.unwrapName != null ? h.unwrapDesc : h.oldDesc);
        if (nr.getSort() == org.objectweb.asm.Type.VOID && produced.getSort() != org.objectweb.asm.Type.VOID) {
            mv.visitInsn(produced.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP);
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
            emitFallthrough(mv, h, superName, superIface, na, nr);
        } else if (narrowed) {
            mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
            mv.visitLabel(fallthrough);
            mv.visitFrame(Opcodes.F_NEW, locals.length, locals, 0, new Object[0]);
            emitFallthrough(mv, h, superName, superIface, na, nr);
        } else {
            mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
        }
        mv.visitMaxs(slot + 3, slot + 1);
        mv.visitEnd();
    }

    /**
     * What the synthesized new shape does when it does not forward (guard re-entry, or a narrowed
     * argument of another type): super's new shape, or -- nothing above declares it -- the return
     * type's zero value.
     */
    private static void emitFallthrough(MethodVisitor mv, Chain.OverrideHook h, String superName, String superIface,
                                        org.objectweb.asm.Type[] na, org.objectweb.asm.Type nr) {
        if (superIface != null) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            int slot = 1;
            for (org.objectweb.asm.Type t : na) {
                mv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), slot);
                slot += t.getSize();
            }
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superIface, h.newName, h.newDesc, true);
            mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
            return;
        }
        if (superName == null || superName.equals("java/lang/Object")) {
            switch (nr.getSort()) {
                case org.objectweb.asm.Type.VOID -> { }
                case org.objectweb.asm.Type.OBJECT, org.objectweb.asm.Type.ARRAY -> mv.visitInsn(Opcodes.ACONST_NULL);
                case org.objectweb.asm.Type.LONG -> mv.visitInsn(Opcodes.LCONST_0);
                case org.objectweb.asm.Type.FLOAT -> mv.visitInsn(Opcodes.FCONST_0);
                case org.objectweb.asm.Type.DOUBLE -> mv.visitInsn(Opcodes.DCONST_0);
                default -> mv.visitInsn(Opcodes.ICONST_0);
            }
            mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
            return;
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        int slot = 1;
        for (org.objectweb.asm.Type t : na) {
            mv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), slot);
            slot += t.getSize();
        }
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, h.newName, h.newDesc, false);
        mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
    }

    /**
     * The new shape for a hook that carries its arguments ({@link Chain.OverrideHook#frames}):
     * <pre>
     *   new(a0..an):
     *     frames.cbPush(this, new Object[] {a0..an});
     *     try { return this.old(a[args[0]]..a[args[k]]); } finally { frames.cbPop(); }
     * </pre>
     * The old method's own super call, rewritten to the new shape, reads back the arguments the
     * old shape has no place for ({@link #rewriteHookSuperCalls}). With {@link Chain.OverrideHook#narrow}
     * an argument the release widened is tested first: a value of the old type is cast and forwarded,
     * any other goes to super's new shape as if the mod had not overridden anything ({@link #emitFallthrough}).
     */
    private static void synthesizeFramedOverride(ClassVisitor cv, Chain.OverrideHook h, String cls,
                                                 String superName, String superIface) {
        org.objectweb.asm.Type[] na = org.objectweb.asm.Type.getArgumentTypes(h.newDesc);
        org.objectweb.asm.Type[] oa = org.objectweb.asm.Type.getArgumentTypes(h.oldDesc);
        org.objectweb.asm.Type nr = org.objectweb.asm.Type.getReturnType(h.newDesc);
        org.objectweb.asm.Type or = org.objectweb.asm.Type.getReturnType(h.oldDesc);
        int[] map = h.args;
        if (map == null) {
            map = new int[oa.length];
            for (int i = 0; i < map.length; i++) {
                map[i] = i;
            }
        }
        int[] slotOf = new int[na.length];
        int slot = 1;
        for (int i = 0; i < na.length; i++) {
            slotOf[i] = slot;
            slot += na[i].getSize();
        }
        Object[] locals = new Object[na.length + 1];
        locals[0] = cls;
        for (int i = 0; i < na.length; i++) {
            locals[i + 1] = frameType(na[i]);
        }
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, h.newName, h.newDesc, null, null);
        mv.visitCode();
        org.objectweb.asm.Label tryStart = new org.objectweb.asm.Label();
        org.objectweb.asm.Label tryEnd = new org.objectweb.asm.Label();
        org.objectweb.asm.Label handler = new org.objectweb.asm.Label();
        org.objectweb.asm.Label fallthrough = new org.objectweb.asm.Label();
        mv.visitTryCatchBlock(tryStart, tryEnd, handler, null);
        boolean narrowed = false;
        if (h.narrow) {
            for (int i = 0; i < oa.length; i++) {
                if (!na[map[i]].equals(oa[i]) && oa[i].getSort() >= org.objectweb.asm.Type.ARRAY) {
                    mv.visitVarInsn(Opcodes.ALOAD, slotOf[map[i]]);
                    mv.visitTypeInsn(Opcodes.INSTANCEOF, oa[i].getInternalName());
                    mv.visitJumpInsn(Opcodes.IFEQ, fallthrough);
                    narrowed = true;
                }
            }
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(na.length);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < na.length; i++) {
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(i);
            mv.visitVarInsn(na[i].getOpcode(Opcodes.ILOAD), slotOf[i]);
            box(mv, na[i]);
            mv.visitInsn(Opcodes.AASTORE);
        }
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.frames, "cbPush", "(Ljava/lang/Object;[Ljava/lang/Object;)V", false);
        mv.visitLabel(tryStart);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        for (int i = 0; i < oa.length; i++) {
            mv.visitVarInsn(na[map[i]].getOpcode(Opcodes.ILOAD), slotOf[map[i]]);
            if (h.narrow && !na[map[i]].equals(oa[i]) && oa[i].getSort() >= org.objectweb.asm.Type.ARRAY) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, oa[i].getInternalName());
            }
        }
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, cls, h.oldName, h.oldDesc, false);
        if (nr.getSort() == org.objectweb.asm.Type.VOID && or.getSort() != org.objectweb.asm.Type.VOID) {
            mv.visitInsn(or.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP);
        }
        mv.visitLabel(tryEnd);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.frames, "cbPop", "()V", false);
        mv.visitInsn(nr.getOpcode(Opcodes.IRETURN));
        mv.visitLabel(handler);
        mv.visitFrame(Opcodes.F_NEW, locals.length, locals, 1, new Object[] {"java/lang/Throwable"});
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, h.frames, "cbPop", "()V", false);
        mv.visitInsn(Opcodes.ATHROW);
        if (narrowed) {
            mv.visitLabel(fallthrough);
            mv.visitFrame(Opcodes.F_NEW, locals.length, locals, 0, new Object[0]);
            emitFallthrough(mv, h, superName, superIface, na, nr);
        }
        mv.visitMaxs(slot + 6, slot);
        mv.visitEnd();
    }

    private static void box(MethodVisitor mv, org.objectweb.asm.Type t) {
        String w = switch (t.getSort()) {
            case org.objectweb.asm.Type.BOOLEAN -> "java/lang/Boolean";
            case org.objectweb.asm.Type.BYTE -> "java/lang/Byte";
            case org.objectweb.asm.Type.CHAR -> "java/lang/Character";
            case org.objectweb.asm.Type.SHORT -> "java/lang/Short";
            case org.objectweb.asm.Type.INT -> "java/lang/Integer";
            case org.objectweb.asm.Type.LONG -> "java/lang/Long";
            case org.objectweb.asm.Type.FLOAT -> "java/lang/Float";
            case org.objectweb.asm.Type.DOUBLE -> "java/lang/Double";
            default -> null;
        };
        if (w != null) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, w, "valueOf", "(" + t.getDescriptor() + ")L" + w + ";", false);
        }
    }

    /** the frames accessor answering a carried argument of this type: {name, return descriptor} */
    private static String[] frameArg(org.objectweb.asm.Type t) {
        return switch (t.getSort()) {
            case org.objectweb.asm.Type.BOOLEAN -> new String[] {"cbArgZ", "Z"};
            case org.objectweb.asm.Type.BYTE -> new String[] {"cbArgB", "B"};
            case org.objectweb.asm.Type.CHAR -> new String[] {"cbArgC", "C"};
            case org.objectweb.asm.Type.SHORT -> new String[] {"cbArgS", "S"};
            case org.objectweb.asm.Type.INT -> new String[] {"cbArgI", "I"};
            case org.objectweb.asm.Type.LONG -> new String[] {"cbArgJ", "J"};
            case org.objectweb.asm.Type.FLOAT -> new String[] {"cbArgF", "F"};
            case org.objectweb.asm.Type.DOUBLE -> new String[] {"cbArgD", "D"};
            default -> new String[] {"cbArgL", "Ljava/lang/Object;"};
        };
    }

    /**
     * A mod's super.old(..) inside a class under a hook with {@code superCalls}: the old shape is
     * gone from vanilla, the new one takes more (or other) arguments. The call becomes
     * INVOKESPECIAL super.new(..): each new argument is the old argument the hook maps it from
     * (cast down when the new shape declares a subtype -- the value vanilla handed the mod's
     * method), and an argument the old shape has no place for is read back from the frame the
     * synthesized new shape pushed for this object -- what vanilla passed. Outside such a call
     * (the mod calling its super's old shape from another method) there is no frame and the
     * argument is its type's zero value.
     */
    /** the value a superDefault pushes (Chain.OverrideHook#superDefault) */
    private static org.objectweb.asm.tree.InsnList superDefaultValue(String d) {
        org.objectweb.asm.tree.InsnList out = new org.objectweb.asm.tree.InsnList();
        if (d.equals("void")) {
            return out;
        }
        if (d.equals("null")) {
            out.add(new org.objectweb.asm.tree.InsnNode(Opcodes.ACONST_NULL));
        } else if (d.startsWith("new ")) {
            String owner = d.substring(4);
            out.add(new org.objectweb.asm.tree.TypeInsnNode(Opcodes.NEW, owner));
            out.add(new org.objectweb.asm.tree.InsnNode(Opcodes.DUP));
            out.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESPECIAL, owner, "<init>", "()V", false));
        } else if (d.startsWith("i:")) {
            out.add(new org.objectweb.asm.tree.LdcInsnNode(Integer.valueOf(d.substring(2))));
        } else if (d.startsWith("l:")) {
            out.add(new org.objectweb.asm.tree.LdcInsnNode(Long.valueOf(d.substring(2))));
        } else if (d.startsWith("f:")) {
            out.add(new org.objectweb.asm.tree.LdcInsnNode(Float.valueOf(d.substring(2))));
        } else if (d.startsWith("d:")) {
            out.add(new org.objectweb.asm.tree.LdcInsnNode(Double.valueOf(d.substring(2))));
        } else {
            throw new IllegalArgumentException("superDefault " + d);
        }
        return out;
    }

    private static byte[] rewriteHookSuperCalls(byte[] bytes, Chain chain, LayerIndex layer, Report rpt, String tag) {
        java.util.Map<String, java.util.List<Chain.OverrideHook>> byOld = superCallHooks(chain);
        if (byOld.isEmpty()) {
            return bytes;
        }
        String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        boolean any = false;
        for (java.util.List<Chain.OverrideHook> hs : byOld.values()) {
            if (raw.contains(hs.get(0).oldName)) {
                any = true;
                break;
            }
        }
        if (!any) {
            return bytes;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        String[] ifaces = cn.interfaces.toArray(new String[0]);
        int rewrites = 0;
        // hooks whose super bridge this class now calls: it gets their accessors (Chain.OverrideHook#superAccess),
        // each calling super where the rewritten super call did -- {owner, "itf"} of the first one: the
        // superclass, or the interface of an Iface.super.m() call to a default method
        java.util.LinkedHashMap<Chain.OverrideHook, String[]> needAccess = new java.util.LinkedHashMap<>();
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if (m.instructions == null || (m.access & Opcodes.ACC_STATIC) != 0) {
                continue;
            }
            org.objectweb.asm.tree.AbstractInsnNode next;
            for (org.objectweb.asm.tree.AbstractInsnNode in = m.instructions.getFirst(); in != null; in = next) {
                next = in.getNext();
                if (in.getOpcode() != Opcodes.INVOKESPECIAL) {
                    continue;
                }
                org.objectweb.asm.tree.MethodInsnNode mi = (org.objectweb.asm.tree.MethodInsnNode) in;
                Chain.OverrideHook h = superCallHook(byOld.get(mi.name + mi.desc), chain, layer, cn, ifaces, mi);
                if (h == null) {
                    continue;
                }
                if (h.superBridgeName != null) {
                    // the old contract through a bridge: this + the old arguments are on the stack as
                    // they are, the static takes the receiver as the hook's super-access interface
                    needAccess.putIfAbsent(h, new String[] {mi.owner, mi.itf ? "itf" : ""});
                    mi.setOpcode(Opcodes.INVOKESTATIC);
                    mi.owner = h.superBridgeOwner;
                    mi.name = h.superBridgeName;
                    mi.desc = h.superBridgeDesc;
                    mi.itf = false;
                    rewrites++;
                    top.dext.centurybridge.data.FullAudit.record(h.oldName + h.oldDesc, "hook-super-bridge");
                    continue;
                }
                if (h.superDefault != null) {
                    // W's target is abstract: V's default body in place of the call (Chain.OverrideHook#superDefault)
                    org.objectweb.asm.tree.InsnList seq = new org.objectweb.asm.tree.InsnList();
                    org.objectweb.asm.Type[] dropped = org.objectweb.asm.Type.getArgumentTypes(mi.desc);
                    for (int i = dropped.length - 1; i >= 0; i--) {
                        seq.add(new org.objectweb.asm.tree.InsnNode(dropped[i].getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
                    }
                    seq.add(new org.objectweb.asm.tree.InsnNode(Opcodes.POP));
                    seq.add(superDefaultValue(h.superDefault));
                    m.instructions.insert(mi, seq);
                    m.instructions.remove(mi);
                    rewrites++;
                    top.dext.centurybridge.data.FullAudit.record(h.oldName + h.oldDesc, "hook-super-default");
                    continue;
                }
                org.objectweb.asm.Type[] oa = org.objectweb.asm.Type.getArgumentTypes(h.oldDesc);
                org.objectweb.asm.Type[] na = org.objectweb.asm.Type.getArgumentTypes(h.newDesc);
                int[] from = new int[na.length];
                java.util.Arrays.fill(from, -1);
                for (int i = 0; i < oa.length; i++) {
                    from[h.args == null ? i : h.args[i]] = i;
                }
                int[] slots = spillArgs(m, mi, oa);
                org.objectweb.asm.tree.InsnList seq = new org.objectweb.asm.tree.InsnList();
                for (int j = 0; j < na.length; j++) {
                    if (from[j] >= 0) {
                        int i = from[j];
                        seq.add(new org.objectweb.asm.tree.VarInsnNode(oa[i].getOpcode(Opcodes.ILOAD), slots[i]));
                        if (!oa[i].equals(na[j]) && na[j].getSort() >= org.objectweb.asm.Type.ARRAY) {
                            seq.add(new org.objectweb.asm.tree.TypeInsnNode(Opcodes.CHECKCAST, na[j].getInternalName()));
                        }
                    } else {
                        String[] acc = frameArg(na[j]);
                        String[] dflt = h.frameDefaults.get(j);
                        seq.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0));
                        seq.add(new org.objectweb.asm.tree.LdcInsnNode(j));
                        if (dflt == null) {
                            seq.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, h.frames, acc[0],
                                "(Ljava/lang/Object;I)" + acc[1], false));
                        } else {
                            // no frame for this object (the old override entered directly): what V used,
                            // computed from nothing, the receiver, or the receiver and the old arguments
                            int dn = org.objectweb.asm.Type.getArgumentTypes(dflt[2]).length;
                            if (dn >= 1) {
                                seq.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0));
                            }
                            if (dn > 1) {
                                for (int i = 0; i < oa.length; i++) {
                                    seq.add(new org.objectweb.asm.tree.VarInsnNode(oa[i].getOpcode(Opcodes.ILOAD), slots[i]));
                                }
                            }
                            seq.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, dflt[0], dflt[1], dflt[2], false));
                            seq.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, h.frames,
                                "cbArgOr" + acc[0].substring("cbArg".length()), "(Ljava/lang/Object;I" + acc[1] + ")" + acc[1], false));
                        }
                        if (na[j].getSort() >= org.objectweb.asm.Type.ARRAY && !na[j].getInternalName().equals("java/lang/Object")) {
                            seq.add(new org.objectweb.asm.tree.TypeInsnNode(Opcodes.CHECKCAST, na[j].getInternalName()));
                        }
                    }
                }
                m.instructions.insertBefore(mi, seq);
                mi.name = h.newName;
                mi.desc = h.newDesc;
                org.objectweb.asm.Type or = org.objectweb.asm.Type.getReturnType(h.oldDesc);
                org.objectweb.asm.Type nr = org.objectweb.asm.Type.getReturnType(h.newDesc);
                if (!or.equals(nr)) {
                    if (or.getSort() == org.objectweb.asm.Type.VOID) {
                        m.instructions.insert(mi, new org.objectweb.asm.tree.InsnNode(nr.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
                    } else if (or.getSort() >= org.objectweb.asm.Type.ARRAY) {
                        m.instructions.insert(mi, new org.objectweb.asm.tree.TypeInsnNode(Opcodes.CHECKCAST, or.getInternalName()));
                    }
                }
                rewrites++;
                top.dext.centurybridge.data.FullAudit.record(mi.owner + "." + h.oldName + h.oldDesc, "hook-super-call");
            }
        }
        if (rewrites == 0) {
            return bytes;
        }
        for (java.util.Map.Entry<Chain.OverrideHook, String[]> na : needAccess.entrySet()) {
            Chain.OverrideHook h = na.getKey();
            String superOwner = na.getValue()[0];
            boolean superItf = !na.getValue()[1].isEmpty();
            // the super bridge reaches super's implementations through accessors this class carries
            if (!cn.interfaces.contains(h.superAccess)) {
                cn.interfaces.add(h.superAccess);
                if (cn.signature != null) {
                    cn.signature = cn.signature + "L" + h.superAccess + ";"; // a generic signature lists them too
                }
            }
            for (String shape : h.superAccessors) {
                int p = shape.indexOf('(');
                String name = shape.substring(0, p);
                String desc = shape.substring(p);
                String accName = Chain.SUPER_ACCESSOR_PREFIX + name;
                boolean have = cn.methods.stream().anyMatch(x -> x.name.equals(accName) && x.desc.equals(desc));
                if (have) {
                    continue;
                }
                org.objectweb.asm.tree.MethodNode acc = new org.objectweb.asm.tree.MethodNode(
                    Opcodes.ACC_PUBLIC, accName, desc, null, null);
                acc.instructions.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0));
                int slot = 1;
                for (org.objectweb.asm.Type t : org.objectweb.asm.Type.getArgumentTypes(desc)) {
                    acc.instructions.add(new org.objectweb.asm.tree.VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
                    slot += t.getSize();
                }
                acc.instructions.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESPECIAL, superOwner,
                    name, desc, superItf));
                acc.instructions.add(new org.objectweb.asm.tree.InsnNode(
                    org.objectweb.asm.Type.getReturnType(desc).getOpcode(Opcodes.IRETURN)));
                cn.methods.add(acc);
            }
            rpt.notes.add(tag + "super-accessors (" + h.superAccess + "): " + cn.name);
        }
        rpt.notes.add(tag + "hook-super-calls: " + rewrites + " in " + cn.name);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
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
                            org.objectweb.asm.Type[] oldArgs = org.objectweb.asm.Type.getArgumentTypes(mi.desc);
                            int[] slots = reshapeArgs(m, mi, reshape);
                            if (reshape.postName != null) {
                                // hand the constructed object and the old arguments the new
                                // constructor has no place for to the post static: the NEW site's
                                // object is on the stack after the call, a super(...) has `this`
                                boolean superCall = newInsn == null && m.name.equals("<init>") && mi.owner.equals(cn.superName);
                                boolean newDup = newInsn != null && newInsn.getNext() != null
                                    && newInsn.getNext().getOpcode() == Opcodes.DUP;
                                if (!superCall && !newDup) {
                                    // the call is reshaped but the value the post static carries is lost: an issue, not a note
                                    rpt.issues.add(tag + "ctor reshape post skipped (neither a NEW DUP site nor this class's super(...)): "
                                        + key + " in " + cn.name + "." + m.name);
                                } else {
                                    org.objectweb.asm.tree.InsnList post = new org.objectweb.asm.tree.InsnList();
                                    post.add(superCall ? new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0)
                                        : new org.objectweb.asm.tree.InsnNode(Opcodes.DUP));
                                    for (int idx : reshape.postArgs) {
                                        post.add(new org.objectweb.asm.tree.VarInsnNode(oldArgs[idx].getOpcode(Opcodes.ILOAD), slots[idx]));
                                    }
                                    post.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC,
                                        reshape.postOwner, reshape.postName, reshape.postDesc, false));
                                    m.instructions.insert(mi, post);
                                }
                            }
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
     * Method references to reshaped members ({@link Chain#callReshapes}). A LambdaMetafactory
     * invokedynamic whose implementation handle names such a member -- {@code manager::byKey},
     * {@code RecipeManager::getRecipes}, {@code ChatComponent::getRecentChat} -- calls the member with
     * the old descriptor and hands its answer to the functional interface untouched, past every
     * conversion a call site gets: a reshape that changed the descriptor fails to link at the
     * lambda's first use (BootstrapMethodError), a same-shape one (generic drift) hands the mod the
     * new type arguments' values (the ClassCastException comes where the mod reads them). The handle
     * is pointed at a private static forwarder of the same class that makes that very call -- the
     * receiver first for an instance member, then the arguments -- and returns its answer; the call
     * pass that runs next ({@link #rewriteCalls}) reshapes and converts that call like any other,
     * with the same resolution (an inherited member named through a mod class included). The lambda
     * keeps its interface, its erased and instantiated types and its captured values; the forwarder
     * is straight-line code, so no frames. A serializable lambda (altMetafactory, FLAG_SERIALIZABLE)
     * is left alone and reported: its $deserializeLambda$ matches the implementation handle by name.
     */
    private static byte[] rewriteReshapedMethodRefs(byte[] bytes, Chain chain, LayerIndex layer, Report rpt, String tag) {
        String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        if (!raw.contains("java/lang/invoke/LambdaMetafactory")) {
            return bytes;
        }
        boolean any = false;
        for (String n : chain.callReshapeNames) {
            if (raw.contains(n)) {
                any = true;
                break;
            }
        }
        if (!any) {
            return bytes;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        boolean itf = (cn.access & Opcodes.ACC_INTERFACE) != 0;
        Set<String> names = new HashSet<>();
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            names.add(m.name);
        }
        Map<String, org.objectweb.asm.tree.MethodNode> forwarders = new LinkedHashMap<>();
        int rewrites = 0;
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if (m.instructions == null) {
                continue;
            }
            for (org.objectweb.asm.tree.AbstractInsnNode in = m.instructions.getFirst(); in != null; in = in.getNext()) {
                if (!(in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode idin)
                        || !idin.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")
                        || idin.bsmArgs.length < 3 || !(idin.bsmArgs[1] instanceof org.objectweb.asm.Handle h)) {
                    continue;
                }
                int kind = h.getTag();
                if ((kind != Opcodes.H_INVOKEVIRTUAL && kind != Opcodes.H_INVOKEINTERFACE && kind != Opcodes.H_INVOKESTATIC)
                        || h.getName().startsWith("<")) {
                    continue;
                }
                String nameDesc = h.getName() + h.getDesc();
                String home = chain.instanceRedirectHome(chain.callReshapeIndex, h.getOwner(), nameDesc);
                if (home == null && layer.decls.containsKey(h.getOwner())) {
                    home = inheritedReshapeHome(h.getOwner(), nameDesc, layer, chain);
                } else if (home == null && !Chain.minecraftOwned(h.getOwner()) && !h.getOwner().startsWith("java/")) {
                    home = libraryHome(h.getOwner(), nameDesc, h.getTag() == Opcodes.H_INVOKESTATIC, layer, chain,
                        chain.callReshapeIndex::containsKey);
                }
                if (home == null) {
                    continue;
                }
                String key = home + "." + nameDesc;
                if (idin.bsm.getName().equals("altMetafactory") && idin.bsmArgs.length > 3
                        && idin.bsmArgs[3] instanceof Integer flags && (flags & 1) != 0) {
                    rpt.issues.add(tag + "method reference to a reshaped member left as it was (serializable lambda): "
                        + key + " in " + cn.name);
                    top.dext.centurybridge.data.FullAudit.record(key, "METHODREF-NOT-RESHAPED[" + chain.sideOf(home) + "] serializable");
                    continue;
                }
                String fk = kind + " " + h.getOwner() + "." + nameDesc;
                org.objectweb.asm.tree.MethodNode f = forwarders.get(fk);
                if (f == null) {
                    String name = "cb$ref$" + h.getName();
                    for (int i = 1; names.contains(name); i++) {
                        name = "cb$ref$" + h.getName() + "$" + i;
                    }
                    names.add(name);
                    f = methodRefForwarder(h, name);
                    forwarders.put(fk, f);
                }
                idin.bsmArgs[1] = new org.objectweb.asm.Handle(Opcodes.H_INVOKESTATIC, cn.name, f.name, f.desc, itf);
                rewrites++;
                top.dext.centurybridge.data.FullAudit.record(key, "call-reshape-methodref");
            }
        }
        if (rewrites == 0) {
            return bytes;
        }
        cn.methods.addAll(forwarders.values());
        rpt.notes.add(tag + "method references to reshaped members forwarded: " + rewrites + " in " + cn.name);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /** the forwarder {@link #rewriteReshapedMethodRefs} points a method reference at: the very call, receiver first */
    private static org.objectweb.asm.tree.MethodNode methodRefForwarder(org.objectweb.asm.Handle h, String name) {
        boolean statik = h.getTag() == Opcodes.H_INVOKESTATIC;
        org.objectweb.asm.Type[] params = org.objectweb.asm.Type.getArgumentTypes(h.getDesc());
        org.objectweb.asm.Type ret = org.objectweb.asm.Type.getReturnType(h.getDesc());
        StringBuilder d = new StringBuilder("(");
        if (!statik) {
            d.append('L').append(h.getOwner()).append(';');
        }
        for (org.objectweb.asm.Type p : params) {
            d.append(p.getDescriptor());
        }
        d.append(')').append(ret.getDescriptor());
        org.objectweb.asm.tree.MethodNode f = new org.objectweb.asm.tree.MethodNode(
            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, name, d.toString(), null, null);
        int slot = 0;
        if (!statik) {
            f.instructions.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0));
            slot = 1;
        }
        for (org.objectweb.asm.Type p : params) {
            f.instructions.add(new org.objectweb.asm.tree.VarInsnNode(p.getOpcode(Opcodes.ILOAD), slot));
            slot += p.getSize();
        }
        int op = statik ? Opcodes.INVOKESTATIC : h.getTag() == Opcodes.H_INVOKEINTERFACE ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
        f.instructions.add(new org.objectweb.asm.tree.MethodInsnNode(op, h.getOwner(), h.getName(), h.getDesc(), h.isInterface()));
        f.instructions.add(new org.objectweb.asm.tree.InsnNode(ret.getOpcode(Opcodes.IRETURN)));
        f.maxLocals = slot;
        f.maxStack = Math.max(slot, ret.getSize());
        return f;
    }

    /**
     * Method calls whose descriptor changed at this step ({@link Chain#callReshapes}): every call
     * site whose JVM-resolved home is a reshaped key -- whatever its opcode, a mod's own
     * super.m(..) included, so the call keeps its dispatch -- has its arguments re-pushed per the
     * ops and takes the new descriptor; the return value is dropped, converted or supplied right
     * after. Straight-line code at the call, so existing frames stay exact.
     *
     * One exception: a mod's super.old(..) in a class under an override hook that carries its
     * arguments ("frames" + "superCalls": "rewrite") belongs to that hook. Its rewrite hands the
     * super call what vanilla passed to the synthesized new shape; a reshape's ops could only
     * supply a fixed value there, so such a call is left for {@link #rewriteHookSuperCalls}. So is
     * one under a hook with a super bridge ("superBridge"), which makes the super call itself.
     * A call may name a class of the mod's own jar while the member is a vanilla one it inherits:
     * javac's qualifying type for an unqualified call to an inherited member is the subclass
     * itself (a mod's RecipeProvider calling offerSmelting(..), a loot function builder calling
     * this.getConditions()). The JVM resolves such a call on the first vanilla class up the mod's
     * own hierarchy when no mod class on the way declares the member, so the key is looked up from
     * there ({@link #inheritedReshapeHome}: the superclass chain first, then the vanilla interfaces
     * the mod's types implement -- a default method such as RecipeBuilder.save(consumer) reached
     * through a mod builder); a mod class that does declare the old shape keeps the call (its own
     * method, reconnected to the new shape by an override hook if there is one).
     */
    private static byte[] rewriteCalls(byte[] bytes, Chain chain, LayerIndex layer, Report rpt, String tag) {
        String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        boolean any = false;
        for (String n : chain.callReshapeNames) {
            if (raw.contains(n)) {
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
            org.objectweb.asm.tree.AbstractInsnNode next;
            for (org.objectweb.asm.tree.AbstractInsnNode in = m.instructions.getFirst(); in != null; in = next) {
                next = in.getNext();
                if (!(in instanceof org.objectweb.asm.tree.MethodInsnNode mi) || mi.name.startsWith("<")) {
                    continue;
                }
                String nameDesc = mi.name + mi.desc;
                // a key's own owner (vanilla, com/mojang/blaze3d, a library such as DFU) directly; a
                // class of the mod's jar through the vanilla class or interface it inherits the member
                // from (javac qualifies such a call by the class it is made in, JLS 13.1). The call keeps
                // its owner: the JVM resolves the new descriptor up the hierarchy as it did the old one --
                // except a static call (below)
                String home = chain.instanceRedirectHome(chain.callReshapeIndex, mi.owner, nameDesc);
                boolean inherited = false;
                if (home == null && layer.decls.containsKey(mi.owner)) {
                    home = inheritedReshapeHome(mi.owner, nameDesc, layer, chain);
                    inherited = home != null;
                } else if (home == null && !Chain.minecraftOwned(mi.owner) && !mi.owner.startsWith("java/")) {
                    // a library type (ItemVariant) whose library supertype (TransferVariant) the reshape is keyed on
                    home = libraryHome(mi.owner, nameDesc, mi.getOpcode() == Opcodes.INVOKESTATIC, layer, chain,
                        chain.callReshapeIndex::containsKey);
                }
                if (home == null) {
                    continue;
                }
                if (mi.getOpcode() == Opcodes.INVOKESPECIAL && framedSuperCall(chain, layer, cn, mi)) {
                    continue; // the carrying hook's super-call rewrite takes it
                }
                String key = home + "." + nameDesc;
                Chain.CallReshape r = chain.callReshapes.get(key);
                reshapeCall(m, mi, r);
                if (inherited && mi.getOpcode() == Opcodes.INVOKESTATIC) {
                    // a static call through a class of the jar resolved, in the source, to the class
                    // declaring it -- the home. Named through the mod class it no longer resolves when
                    // the mod class's ancestry changed at this step: a superRenames facade in place of
                    // the vanilla class (static members are not inherited past it), or a home that is an
                    // interface on the target (1.20.3's CommandFunction; an interface's static methods are
                    // never inherited, JVMS 5.4.3.3). The call names the home, as the JVM resolved it
                    mi.owner = home;
                    Boolean itf = chain.interfaceOnTarget(home);
                    if (itf != null) {
                        mi.itf = itf;
                    }
                }
                rewrites++;
                top.dext.centurybridge.data.FullAudit.record(key, "call-reshape");
            }
        }
        if (rewrites == 0) {
            return bytes;
        }
        rpt.notes.add(tag + "call-reshaped: " + rewrites + " call site(s) in " + cn.name);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /**
     * One call site through a {@link Chain.CallReshape}: the arguments on the stack are re-pushed per
     * the ops, the call takes the new descriptor, and the return value is dropped, converted or
     * supplied right after it. Straight-line code at the call, so existing frames stay exact. Shared
     * by {@link #rewriteCalls} and {@link MixinBridge} (a mixin's call through a renamed invoker).
     */
    static void reshapeCall(org.objectweb.asm.tree.MethodNode m, org.objectweb.asm.tree.MethodInsnNode mi,
                            Chain.CallReshape r) {
        reshapeArgs(m, mi, r.args);
        mi.desc = r.newDesc;
        if (r.retKind != null) {
            org.objectweb.asm.Type newRet = org.objectweb.asm.Type.getReturnType(r.newDesc);
            org.objectweb.asm.tree.AbstractInsnNode after = r.retKind.equals("pop")
                ? new org.objectweb.asm.tree.InsnNode(newRet.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP)
                : new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, r.retOwner, r.retName, r.retDesc, false);
            m.instructions.insert(mi, after);
        }
    }

    /**
     * Whether a super call is one {@link #rewriteHookSuperCalls} rewrites with a carrying hook: the
     * same choice it makes ({@link #superCallHook}), for a hook with frames or a super bridge.
     * A super bridge reaches super's implementation of the new shape through the class's accessors;
     * reshaped here instead, a super call whose new shape is a bridge onto a typed method of another
     * name (1.21's AbstractCookingRecipe.matches(RecipeInput) onto matches(SingleRecipeInput)) would
     * come back into the class's own override of the typed method.
     */
    private static boolean framedSuperCall(Chain chain, LayerIndex layer, org.objectweb.asm.tree.ClassNode cn,
                                           org.objectweb.asm.tree.MethodInsnNode mi) {
        Chain.OverrideHook h = superCallHook(superCallHooks(chain).get(mi.name + mi.desc), chain, layer, cn,
            cn.interfaces.toArray(new String[0]), mi);
        return h != null && (h.frames != null || h.superBridgeName != null);
    }

    /** the superCalls and superBridge hooks by old shape, each list in wiring order */
    private static java.util.Map<String, java.util.List<Chain.OverrideHook>> superCallHooks(Chain chain) {
        java.util.Map<String, java.util.List<Chain.OverrideHook>> byOld = new HashMap<>();
        for (Chain.OverrideHook h : chain.overrideHooks) {
            if (h.superCalls || h.superBridgeName != null) {
                byOld.computeIfAbsent(h.oldName + h.oldDesc, k -> new ArrayList<>()).add(h);
            }
        }
        return byOld;
    }

    /**
     * The hook that takes a mod's super.old(..): of the superCalls / superBridge hooks for the call's old
     * shape, the first in wiring order whose hierarchy filter the class meets -- one old shape can have a
     * hook per vanilla class that declared it (1.21's typed matches of AbstractCookingRecipe,
     * StonecutterRecipe and the two smithing recipes, each for the classes under its own) -- and only for
     * a call into a vanilla supertype: the super call's owner is the direct superclass, and a mod class in
     * between inherits the vanilla method (this layer's hierarchy says from where) unless it declares it
     * itself. Null when none takes it.
     */
    private static Chain.OverrideHook superCallHook(java.util.List<Chain.OverrideHook> candidates, Chain chain, LayerIndex layer,
                                                    org.objectweb.asm.tree.ClassNode cn, String[] ifaces,
                                                    org.objectweb.asm.tree.MethodInsnNode mi) {
        if (candidates == null || mi.owner.equals(cn.name) || !mi.owner.startsWith("net/minecraft/")
                && inheritedVanillaOwner(mi.owner, mi.name + mi.desc, layer) == null) {
            return null;
        }
        for (Chain.OverrideHook h : candidates) {
            if ((h.onlyUnder == null || isUnder(cn.superName, ifaces, h.onlyUnder, layer, chain))
                    && (h.notUnder == null || !isUnder(cn.superName, ifaces, h.notUnder, layer, chain))) {
                return h;
            }
        }
        return null;
    }

    /**
     * Same-shape override bridges ({@link Chain#convertHooks}): a method of the mod's own class that
     * overrides a member whose type arguments changed at this step -- name and erased descriptor as
     * before -- is renamed, and a bridge with the member's name and descriptor takes its place,
     * turning what vanilla passes into what the old body was written for and its answer back:
     * <pre>
     *   m(a0..an):  return ret(this.m$cbW(arg0(a0) .. argn(an)));
     * </pre>
     * (a converter that takes the mod object is called as conv(this, value)).
     * The bridge calls its own class's renamed body non-virtually (invokespecial): each overriding
     * class gets its own bridge, and a subclass's super.m(..) -- which lands on the parent's bridge --
     * must reach the parent's body, not dispatch back into the subclass's (a StackOverflowError for
     * every 1.20.1 override that calls super above another converted override).
     * An abstract redeclaration is renamed too and gets the same bridge, which dispatches to the
     * subclass's renamed body; a javac bridge with the member's descriptor (a covariant override's)
     * is what gets renamed, and still forwards to the mod's own method. Calls are converted on the
     * other side ({@link #rewriteCalls}: a same-shape reshape climbs past the mod's declaration), so
     * every value reaching the renamed body is V's. Straight-line code: no frames to write.
     */
    private static byte[] rewriteConvertHooks(byte[] bytes, Chain chain, LayerIndex layer, Report rpt, String tag) {
        String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        boolean any = false;
        for (List<Chain.ConvertHook> hs : chain.convertHooks.values()) {
            if (raw.contains(hs.get(0).name)) {
                any = true;
                break;
            }
        }
        if (!any) {
            return bytes;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        boolean itf = (cn.access & Opcodes.ACC_INTERFACE) != 0;
        String[] ifaces = cn.interfaces.toArray(new String[0]);
        Set<String> declared = new HashSet<>();
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            declared.add(m.name + m.desc);
        }
        List<org.objectweb.asm.tree.MethodNode> bridges = new ArrayList<>();
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if ((m.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) != 0 || m.name.startsWith("<")) {
                continue;
            }
            List<Chain.ConvertHook> hs = chain.convertHooks.get(m.name + m.desc);
            if (hs == null) {
                continue;
            }
            Chain.ConvertHook h = null;
            for (Chain.ConvertHook x : hs) {
                if (isUnder(cn.superName, ifaces, x.under, layer, chain)) {
                    h = x;
                    break;
                }
            }
            if (h == null || declared.contains(h.renamed + h.desc)) {
                continue; // not an override of the hooked member, or already bridged at this step
            }
            m.name = h.renamed;
            bridges.add(convertBridge(cn.name, itf, m, h));
            rpt.notes.add(tag + "convert-hooked: " + cn.name + "." + h.name + h.desc + " (old body now " + h.renamed + ")");
            top.dext.centurybridge.data.FullAudit.record(h.under + "." + h.name + h.desc, "convert-hooked");
        }
        if (bridges.isEmpty()) {
            return bytes;
        }
        cn.methods.addAll(bridges);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /**
     * The lambda side of {@link Chain#convertHooks}: a LambdaMetafactory invokedynamic that builds a
     * hooked functional interface (RecipeManager.CachedCheck, RecipeShownListener) from a lambda or a
     * method reference. Vanilla calls the interface method with W's values and reads W's from its
     * answer; the lambda was written for V's, and no class of the mod declares the method for
     * {@link #rewriteConvertHooks} to bridge. The implementation handle is pointed at a private static
     * forwarder of the same class that takes what LambdaMetafactory hands the implementation (the
     * captured values, then the interface method's arguments), runs the hook's converters on the
     * arguments, calls the implementation the way LambdaMetafactory would, and converts its answer:
     * <pre>
     *   f(c0..ck, a0..an):  return (R) ret(impl(c0..ck, (P0) arg0(a0) .. (Pn) argn(an)));
     * </pre>
     * Casts stand where LambdaMetafactory casts (reference adaptations) and where a converter's
     * erased answer meets the implementation's own parameter type. The lambda keeps its interface,
     * erased and instantiated types and captured values. Calls of the method made by the mod itself
     * still convert on the call side ({@link #rewriteCalls}); the converters are adaptive and undo
     * each other, so the forwarder sees V's values either way. Left as they were, and reported: a
     * serializable lambda (its $deserializeLambda$ matches the implementation handle by name), a
     * constructor reference, a hook whose converter takes the mod object (the receiver here is the
     * lambda itself), and a primitive adaptation (LambdaMetafactory would box or unbox there). Runs
     * before the method-reference and call passes, so a method reference to a reshaped member as the
     * implementation is reshaped inside the forwarder like any call.
     */
    private static byte[] rewriteConvertHookLambdas(byte[] bytes, Chain chain, LayerIndex layer, Report rpt, String tag) {
        String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        if (!raw.contains("java/lang/invoke/LambdaMetafactory")) {
            return bytes;
        }
        boolean any = false;
        for (List<Chain.ConvertHook> hs : chain.convertHooks.values()) {
            if (raw.contains(hs.get(0).name)) {
                any = true;
                break;
            }
        }
        if (!any) {
            return bytes;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        boolean itf = (cn.access & Opcodes.ACC_INTERFACE) != 0;
        Set<String> names = new HashSet<>();
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            names.add(m.name);
        }
        Map<String, org.objectweb.asm.tree.MethodNode> forwarders = new LinkedHashMap<>();
        int rewrites = 0;
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if (m.instructions == null) {
                continue;
            }
            for (org.objectweb.asm.tree.AbstractInsnNode in = m.instructions.getFirst(); in != null; in = in.getNext()) {
                if (!(in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode idin)
                        || !idin.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory") || idin.bsmArgs.length < 3
                        || !(idin.bsmArgs[0] instanceof org.objectweb.asm.Type sam)
                        || !(idin.bsmArgs[1] instanceof org.objectweb.asm.Handle impl)
                        || !(idin.bsmArgs[2] instanceof org.objectweb.asm.Type inst)) {
                    continue;
                }
                org.objectweb.asm.Type iface = org.objectweb.asm.Type.getReturnType(idin.desc);
                List<Chain.ConvertHook> hs = iface.getSort() == org.objectweb.asm.Type.OBJECT
                    ? chain.convertHooks.get(idin.name + sam.getDescriptor()) : null;
                if (hs == null) {
                    continue;
                }
                Chain.ConvertHook h = null;
                for (Chain.ConvertHook x : hs) {
                    if (isUnder(null, new String[] {iface.getInternalName()}, x.under, layer, chain)) {
                        h = x;
                        break;
                    }
                }
                if (h == null) {
                    continue;
                }
                String key = h.under + "." + h.name + h.desc;
                String why = hookLambdaBlocker(idin, impl, inst, h);
                if (why != null) {
                    rpt.issues.add(tag + "lambda of a convert-hooked interface left as it was (" + why + "): " + key + " in " + cn.name);
                    top.dext.centurybridge.data.FullAudit.record(key, "LAMBDA-NOT-CONVERTED[" + chain.sideOf(h.under) + "] " + why);
                    continue;
                }
                String fk = key + " " + impl + " " + idin.desc + " " + inst.getDescriptor();
                org.objectweb.asm.tree.MethodNode f = forwarders.get(fk);
                if (f == null) {
                    String name = "cb$lambda$" + h.name;
                    for (int i = 1; names.contains(name); i++) {
                        name = "cb$lambda$" + h.name + "$" + i;
                    }
                    names.add(name);
                    f = hookLambdaForwarder(name, idin.desc, inst, impl, h);
                    forwarders.put(fk, f);
                }
                idin.bsmArgs[1] = new org.objectweb.asm.Handle(Opcodes.H_INVOKESTATIC, cn.name, f.name, f.desc, itf);
                rewrites++;
                top.dext.centurybridge.data.FullAudit.record(key, "convert-hooked-lambda");
            }
        }
        if (rewrites == 0) {
            return bytes;
        }
        cn.methods.addAll(forwarders.values());
        rpt.notes.add(tag + "convert-hooked lambdas: " + rewrites + " in " + cn.name);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /**
     * {@link Chain#checkCastRedirects} for the cast a lambda makes without a CHECKCAST of its own: a lambda or
     * method reference whose instantiated method type takes a redirected type (a 1.21.11 mod's
     * {@code registry.forEach((LootItemFunctionType t) -> ..)}, {@code stream.map(LootItemFunctionType::codec)})
     * has LambdaMetafactory cast the erased argument to it. Such a lambda is pointed at a forwarder taking the
     * erased type there, whose explicit CHECKCAST the call pass then redirects like any other, and which calls
     * the implementation. Serializable lambdas and constructor references are reported and left as they were.
     */
    private static byte[] rewriteCastLambdas(byte[] bytes, Chain chain, Report rpt, String tag) {
        String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        if (!raw.contains("java/lang/invoke/LambdaMetafactory")) {
            return bytes;
        }
        boolean any = false;
        for (String t : chain.checkCastRedirects.keySet()) {
            if (raw.contains("L" + t + ";")) {
                any = true;
                break;
            }
        }
        if (!any) {
            return bytes;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        boolean itf = (cn.access & Opcodes.ACC_INTERFACE) != 0;
        Set<String> names = new HashSet<>();
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            names.add(m.name);
        }
        Map<String, org.objectweb.asm.tree.MethodNode> forwarders = new LinkedHashMap<>();
        int rewrites = 0;
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if (m.instructions == null) {
                continue;
            }
            for (org.objectweb.asm.tree.AbstractInsnNode in = m.instructions.getFirst(); in != null; in = in.getNext()) {
                if (!(in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode idin)
                        || !idin.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory") || idin.bsmArgs.length < 3
                        || !(idin.bsmArgs[0] instanceof org.objectweb.asm.Type sam)
                        || !(idin.bsmArgs[1] instanceof org.objectweb.asm.Handle impl)
                        || !(idin.bsmArgs[2] instanceof org.objectweb.asm.Type inst)) {
                    continue;
                }
                org.objectweb.asm.Type[] instArgs = inst.getArgumentTypes();
                org.objectweb.asm.Type[] samArgs = sam.getArgumentTypes();
                boolean[] redirected = new boolean[instArgs.length];
                String type = null;
                for (int i = 0; i < instArgs.length; i++) {
                    if (instArgs[i].getSort() == org.objectweb.asm.Type.OBJECT
                            && chain.checkCastRedirects.containsKey(instArgs[i].getInternalName())
                            && i < samArgs.length && !samArgs[i].equals(instArgs[i])) {
                        redirected[i] = true;
                        type = instArgs[i].getInternalName();
                    }
                }
                if (type == null) {
                    continue;
                }
                String why = castLambdaBlocker(idin, impl, inst);
                if (why != null) {
                    rpt.issues.add(tag + "lambda taking a cast-redirected type left as it was (" + why + "): " + type + " in " + cn.name);
                    top.dext.centurybridge.data.FullAudit.record("checkcast " + type, "LAMBDA-CAST-NOT-REDIRECTED " + why);
                    continue;
                }
                org.objectweb.asm.Type[] newArgs = instArgs.clone();
                for (int i = 0; i < instArgs.length; i++) {
                    if (redirected[i]) {
                        newArgs[i] = samArgs[i];
                    }
                }
                org.objectweb.asm.Type newInst = org.objectweb.asm.Type.getMethodType(inst.getReturnType(), newArgs);
                String fk = impl + " " + idin.desc + " " + inst.getDescriptor();
                org.objectweb.asm.tree.MethodNode f = forwarders.get(fk);
                if (f == null) {
                    String name = "cb$cast$" + impl.getName().replace('<', '_').replace('>', '_');
                    for (int i = 1; names.contains(name); i++) {
                        name = "cb$cast$" + impl.getName() + "$" + i;
                    }
                    names.add(name);
                    f = castLambdaForwarder(name, idin.desc, inst, newInst, impl);
                    forwarders.put(fk, f);
                }
                idin.bsmArgs[1] = new org.objectweb.asm.Handle(Opcodes.H_INVOKESTATIC, cn.name, f.name, f.desc, itf);
                idin.bsmArgs[2] = newInst;
                rewrites++;
                top.dext.centurybridge.data.FullAudit.record("checkcast " + type, "checkcast-redirect-lambda");
            }
        }
        if (rewrites == 0) {
            return bytes;
        }
        cn.methods.addAll(forwarders.values());
        rpt.notes.add(tag + "lambdas taking cast-redirected types forwarded: " + rewrites + " in " + cn.name);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /** why {@link #rewriteCastLambdas} cannot forward this lambda, or null */
    private static String castLambdaBlocker(org.objectweb.asm.tree.InvokeDynamicInsnNode idin, org.objectweb.asm.Handle impl,
                                            org.objectweb.asm.Type inst) {
        if (idin.bsm.getName().equals("altMetafactory") && idin.bsmArgs.length > 3
                && idin.bsmArgs[3] instanceof Integer flags && (flags & 1) != 0) {
            return "serializable";
        }
        int kind = impl.getTag();
        if (kind != Opcodes.H_INVOKESTATIC && kind != Opcodes.H_INVOKEVIRTUAL && kind != Opcodes.H_INVOKEINTERFACE
                && kind != Opcodes.H_INVOKESPECIAL) {
            return "constructor reference";
        }
        org.objectweb.asm.Type[] cap = org.objectweb.asm.Type.getArgumentTypes(idin.desc);
        org.objectweb.asm.Type[] samArgs = inst.getArgumentTypes();
        org.objectweb.asm.Type[] implTypes = implInvocationTypes(impl);
        if (cap.length + samArgs.length != implTypes.length) {
            return "arity";
        }
        for (int j = 0; j < implTypes.length; j++) {
            org.objectweb.asm.Type from = j < cap.length ? cap[j] : samArgs[j - cap.length];
            if (!from.equals(implTypes[j]) && (isPrimitive(from) || isPrimitive(implTypes[j]))) {
                return "primitive adaptation";
            }
        }
        org.objectweb.asm.Type ir = org.objectweb.asm.Type.getReturnType(impl.getDesc());
        org.objectweb.asm.Type sr = inst.getReturnType();
        if (sr.getSort() != org.objectweb.asm.Type.VOID && !sr.equals(ir) && (isPrimitive(sr) || isPrimitive(ir))) {
            return "primitive adaptation";
        }
        return null;
    }

    /**
     * the forwarder {@link #rewriteCastLambdas} points a lambda at: the captured values and the arguments as
     * LambdaMetafactory now passes them, each cast to what the implementation takes (a redirected type by a
     * CHECKCAST the call pass redirects), then the implementation's call
     */
    private static org.objectweb.asm.tree.MethodNode castLambdaForwarder(String name, String indyDesc, org.objectweb.asm.Type inst,
                                                                         org.objectweb.asm.Type newInst, org.objectweb.asm.Handle impl) {
        org.objectweb.asm.Type[] cap = org.objectweb.asm.Type.getArgumentTypes(indyDesc);
        org.objectweb.asm.Type[] args = newInst.getArgumentTypes();
        org.objectweb.asm.Type[] wanted = inst.getArgumentTypes();
        org.objectweb.asm.Type samRet = inst.getReturnType();
        org.objectweb.asm.Type[] all = new org.objectweb.asm.Type[cap.length + args.length];
        System.arraycopy(cap, 0, all, 0, cap.length);
        System.arraycopy(args, 0, all, cap.length, args.length);
        org.objectweb.asm.tree.MethodNode f = new org.objectweb.asm.tree.MethodNode(
            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, name,
            org.objectweb.asm.Type.getMethodDescriptor(samRet, all), null, null);
        org.objectweb.asm.tree.InsnList in = f.instructions;
        org.objectweb.asm.Type[] implTypes = implInvocationTypes(impl);
        int slot = 0;
        for (int j = 0; j < all.length; j++) {
            in.add(new org.objectweb.asm.tree.VarInsnNode(all[j].getOpcode(Opcodes.ILOAD), slot));
            slot += all[j].getSize();
            org.objectweb.asm.Type have = all[j];
            if (j >= cap.length && !all[j].equals(wanted[j - cap.length])) {
                castTo(in, have, wanted[j - cap.length]); // the redirected cast LambdaMetafactory made
                have = wanted[j - cap.length];
            }
            castTo(in, have, implTypes[j]);
        }
        int op = switch (impl.getTag()) {
            case Opcodes.H_INVOKESTATIC -> Opcodes.INVOKESTATIC;
            case Opcodes.H_INVOKEINTERFACE -> Opcodes.INVOKEINTERFACE;
            case Opcodes.H_INVOKESPECIAL -> Opcodes.INVOKESPECIAL;
            default -> Opcodes.INVOKEVIRTUAL;
        };
        in.add(new org.objectweb.asm.tree.MethodInsnNode(op, impl.getOwner(), impl.getName(), impl.getDesc(), impl.isInterface()));
        org.objectweb.asm.Type implRet = org.objectweb.asm.Type.getReturnType(impl.getDesc());
        if (samRet.getSort() == org.objectweb.asm.Type.VOID) {
            if (implRet.getSort() != org.objectweb.asm.Type.VOID) {
                in.add(new org.objectweb.asm.tree.InsnNode(implRet.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
            }
        } else {
            castTo(in, implRet, samRet);
        }
        in.add(new org.objectweb.asm.tree.InsnNode(samRet.getOpcode(Opcodes.IRETURN)));
        f.maxLocals = slot;
        f.maxStack = slot + 2;
        return f;
    }

    /** the parameter types LambdaMetafactory passes the implementation, the receiver first for an instance method */
    private static org.objectweb.asm.Type[] implInvocationTypes(org.objectweb.asm.Handle impl) {
        org.objectweb.asm.Type[] p = org.objectweb.asm.Type.getArgumentTypes(impl.getDesc());
        if (impl.getTag() == Opcodes.H_INVOKESTATIC) {
            return p;
        }
        org.objectweb.asm.Type[] out = new org.objectweb.asm.Type[p.length + 1];
        out[0] = org.objectweb.asm.Type.getObjectType(impl.getOwner());
        System.arraycopy(p, 0, out, 1, p.length);
        return out;
    }

    /** why {@link #rewriteConvertHookLambdas} cannot forward this lambda, or null */
    private static String hookLambdaBlocker(org.objectweb.asm.tree.InvokeDynamicInsnNode idin, org.objectweb.asm.Handle impl,
                                            org.objectweb.asm.Type inst, Chain.ConvertHook h) {
        if (idin.bsm.getName().equals("altMetafactory") && idin.bsmArgs.length > 3
                && idin.bsmArgs[3] instanceof Integer flags && (flags & 1) != 0) {
            return "serializable";
        }
        int kind = impl.getTag();
        if (kind != Opcodes.H_INVOKESTATIC && kind != Opcodes.H_INVOKEVIRTUAL && kind != Opcodes.H_INVOKEINTERFACE
                && kind != Opcodes.H_INVOKESPECIAL) {
            return "constructor reference";
        }
        for (String[] a : h.args) {
            if (a != null && Chain.takesReceiver(a)) {
                return "converter takes the mod object";
            }
        }
        if (h.ret != null && Chain.takesReceiver(h.ret)) {
            return "converter takes the mod object";
        }
        org.objectweb.asm.Type[] cap = org.objectweb.asm.Type.getArgumentTypes(idin.desc);
        org.objectweb.asm.Type[] samArgs = inst.getArgumentTypes();
        org.objectweb.asm.Type[] implTypes = implInvocationTypes(impl);
        if (cap.length + samArgs.length != implTypes.length) {
            return "arity";
        }
        for (int j = 0; j < implTypes.length; j++) {
            org.objectweb.asm.Type from = j < cap.length ? cap[j] : samArgs[j - cap.length];
            if (!from.equals(implTypes[j]) && (isPrimitive(from) || isPrimitive(implTypes[j]))) {
                return "primitive adaptation";
            }
        }
        org.objectweb.asm.Type ir = org.objectweb.asm.Type.getReturnType(impl.getDesc());
        org.objectweb.asm.Type sr = inst.getReturnType();
        if (sr.getSort() != org.objectweb.asm.Type.VOID && !sr.equals(ir) && (isPrimitive(sr) || isPrimitive(ir))) {
            return "primitive adaptation";
        }
        return null;
    }

    private static boolean isPrimitive(org.objectweb.asm.Type t) {
        return t.getSort() != org.objectweb.asm.Type.OBJECT && t.getSort() != org.objectweb.asm.Type.ARRAY;
    }

    /** a cast where a reference value of type {@code have} goes where {@code want} is taken (none to Object or to itself) */
    private static void castTo(org.objectweb.asm.tree.InsnList in, org.objectweb.asm.Type have, org.objectweb.asm.Type want) {
        if (!isPrimitive(want) && !want.equals(have) && !want.getDescriptor().equals("Ljava/lang/Object;")) {
            in.add(new org.objectweb.asm.tree.TypeInsnNode(Opcodes.CHECKCAST, want.getSort() == org.objectweb.asm.Type.ARRAY
                ? want.getDescriptor() : want.getInternalName()));
        }
    }

    /** the forwarder {@link #rewriteConvertHookLambdas} points a hooked lambda at */
    private static org.objectweb.asm.tree.MethodNode hookLambdaForwarder(String name, String indyDesc, org.objectweb.asm.Type inst,
                                                                         org.objectweb.asm.Handle impl, Chain.ConvertHook h) {
        org.objectweb.asm.Type[] cap = org.objectweb.asm.Type.getArgumentTypes(indyDesc);
        org.objectweb.asm.Type[] samArgs = inst.getArgumentTypes();
        org.objectweb.asm.Type samRet = inst.getReturnType();
        org.objectweb.asm.Type[] all = new org.objectweb.asm.Type[cap.length + samArgs.length];
        System.arraycopy(cap, 0, all, 0, cap.length);
        System.arraycopy(samArgs, 0, all, cap.length, samArgs.length);
        org.objectweb.asm.tree.MethodNode f = new org.objectweb.asm.tree.MethodNode(
            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, name,
            org.objectweb.asm.Type.getMethodDescriptor(samRet, all), null, null);
        org.objectweb.asm.tree.InsnList in = f.instructions;
        org.objectweb.asm.Type[] implTypes = implInvocationTypes(impl);
        org.objectweb.asm.Type[] hookParams = org.objectweb.asm.Type.getArgumentTypes(h.desc);
        int slot = 0;
        for (int j = 0; j < all.length; j++) {
            in.add(new org.objectweb.asm.tree.VarInsnNode(all[j].getOpcode(Opcodes.ILOAD), slot));
            slot += all[j].getSize();
            int i = j - cap.length;
            org.objectweb.asm.Type have = all[j];
            if (i >= 0 && h.args[i] != null) {
                in.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, h.args[i][0], h.args[i][1], h.args[i][2], false));
                have = hookParams[i];
            }
            castTo(in, have, implTypes[j]);
        }
        int op = switch (impl.getTag()) {
            case Opcodes.H_INVOKESTATIC -> Opcodes.INVOKESTATIC;
            case Opcodes.H_INVOKEINTERFACE -> Opcodes.INVOKEINTERFACE;
            case Opcodes.H_INVOKESPECIAL -> Opcodes.INVOKESPECIAL;
            default -> Opcodes.INVOKEVIRTUAL;
        };
        in.add(new org.objectweb.asm.tree.MethodInsnNode(op, impl.getOwner(), impl.getName(), impl.getDesc(), impl.isInterface()));
        org.objectweb.asm.Type implRet = org.objectweb.asm.Type.getReturnType(impl.getDesc());
        if (samRet.getSort() == org.objectweb.asm.Type.VOID) {
            if (implRet.getSort() != org.objectweb.asm.Type.VOID) {
                in.add(new org.objectweb.asm.tree.InsnNode(implRet.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
            }
        } else if (h.ret != null) {
            org.objectweb.asm.Type r = org.objectweb.asm.Type.getReturnType(h.desc);
            castTo(in, implRet, r);
            in.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, h.ret[0], h.ret[1], h.ret[2], false));
            castTo(in, r, samRet);
        } else {
            castTo(in, implRet, samRet);
        }
        in.add(new org.objectweb.asm.tree.InsnNode(samRet.getOpcode(Opcodes.IRETURN)));
        f.maxLocals = slot;
        f.maxStack = slot + 2;
        return f;
    }

    /** the bridge {@link #rewriteConvertHooks} puts where the mod's method was */
    private static org.objectweb.asm.tree.MethodNode convertBridge(String cls, boolean itf,
                                                                   org.objectweb.asm.tree.MethodNode body,
                                                                   Chain.ConvertHook h) {
        int acc = body.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_FINAL);
        String[] ex = body.exceptions == null ? null : body.exceptions.toArray(new String[0]);
        org.objectweb.asm.tree.MethodNode b = new org.objectweb.asm.tree.MethodNode(acc, h.name, h.desc, body.signature, ex);
        org.objectweb.asm.Type[] params = org.objectweb.asm.Type.getArgumentTypes(h.desc);
        org.objectweb.asm.tree.InsnList in = b.instructions;
        in.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0));
        int slot = 1;
        for (int i = 0; i < params.length; i++) {
            if (h.args[i] != null && Chain.takesReceiver(h.args[i])) {
                in.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0)); // the converter's mod object
            }
            in.add(new org.objectweb.asm.tree.VarInsnNode(params[i].getOpcode(Opcodes.ILOAD), slot));
            slot += params[i].getSize();
            if (h.args[i] != null) {
                in.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, h.args[i][0], h.args[i][1], h.args[i][2], false));
            }
        }
        // a body of this class's own: invokespecial, so a subclass's super.m(..) arriving here reaches
        // this class's body; an abstract one: dispatched to whichever subclass implements it
        boolean dispatch = (body.access & Opcodes.ACC_ABSTRACT) != 0;
        in.add(new org.objectweb.asm.tree.MethodInsnNode(
            dispatch ? (itf ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL) : Opcodes.INVOKESPECIAL,
            cls, h.renamed, h.desc, itf));
        if (h.ret != null) {
            if (Chain.takesReceiver(h.ret)) {
                // ret(this, value): converted values are references, one slot each
                in.add(new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, 0));
                in.add(new org.objectweb.asm.tree.InsnNode(Opcodes.SWAP));
            }
            in.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, h.ret[0], h.ret[1], h.ret[2], false));
        }
        in.add(new org.objectweb.asm.tree.InsnNode(org.objectweb.asm.Type.getReturnType(h.desc).getOpcode(Opcodes.IRETURN)));
        b.maxLocals = slot;
        b.maxStack = slot + 2;
        return b;
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
    static int[] reshapeArgs(org.objectweb.asm.tree.MethodNode m,
                             org.objectweb.asm.tree.MethodInsnNode mi,
                             Chain.CtorReshape r) {
        org.objectweb.asm.Type[] old = org.objectweb.asm.Type.getArgumentTypes(mi.desc);
        int[] slots = spillArgs(m, mi, old);
        org.objectweb.asm.tree.InsnList seq = new org.objectweb.asm.tree.InsnList();
        for (Chain.ReshapeOp op : r.ops) {
            if (op.drop) {
                if (op.owner != null) {
                    // "drop OWNER#name(T)V": the dropped value goes to the sink, nothing is pushed
                    int idx = op.from[0];
                    seq.add(new org.objectweb.asm.tree.VarInsnNode(old[idx].getOpcode(Opcodes.ILOAD), slots[idx]));
                    seq.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, op.owner, op.name, op.desc, false));
                }
                continue;
            }
            if (op.param != null) {
                // the enclosing method's own first parameter of that type, else the supplier
                int slot = (m.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
                int found = -1;
                for (org.objectweb.asm.Type t : org.objectweb.asm.Type.getArgumentTypes(m.desc)) {
                    if (t.equals(op.param)) {
                        found = slot;
                        break;
                    }
                    slot += t.getSize();
                }
                seq.add(found >= 0 ? new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, found)
                    : new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, op.owner, op.name, op.desc, false));
                continue;
            }
            for (int idx : op.from) {
                seq.add(new org.objectweb.asm.tree.VarInsnNode(old[idx].getOpcode(Opcodes.ILOAD), slots[idx]));
            }
            if (op.owner != null) {
                seq.add(new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKESTATIC, op.owner, op.name, op.desc, false));
                if (op.cast != null) {
                    // the converter answered a supertype it could name; the new shape takes this type
                    seq.add(new org.objectweb.asm.tree.TypeInsnNode(Opcodes.CHECKCAST, op.cast.getInternalName()));
                }
            }
        }
        m.instructions.insertBefore(mi, seq);
        mi.desc = r.newDesc;
        return slots;
    }

    /** Redirect values are a class name, optionally "class#member" past the
     *  namespace wall where call-site names are no longer globally unique. */
    static String redirCls(String v) {
        int i = v.indexOf('#');
        return i < 0 ? v : v.substring(0, i);
    }

    static String redirName(String v, String callSiteName) {
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
    /**
     * Whether a class with this superclass and these interfaces is a subtype of {@code target},
     * through the jar's own classes and the source release's: superclasses and interfaces both
     * (a hook on FluidDrainable must reach a mod block that implements it directly).
     */
    private static boolean isUnder(String superName, String[] ifaces, String target, LayerIndex layer, Chain chain) {
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
        if (superName != null) {
            todo.add(superName);
        }
        if (ifaces != null) {
            todo.addAll(List.of(ifaces));
        }
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String cur = todo.poll();
            if (!seen.add(cur)) {
                continue;
            }
            if (cur.equals(target)) {
                return true;
            }
            // the jar's own classes, then Minecraft's (source stub, with Fabric API's injected interfaces), then
            // a library type's (the source release's Fabric API: a mod class under FabricBlockLootTableProvider
            // is under the LootTableSubProvider it implements)
            String sup = layer.superOf.containsKey(cur) ? layer.superOf.get(cur)
                : chain.superOf.containsKey(cur) ? chain.superOf.get(cur) : chain.libSuperOf.get(cur);
            if (sup != null) {
                todo.add(sup);
            }
            todo.addAll(layer.superOf.containsKey(cur) ? layer.interfacesOf.getOrDefault(cur, List.of())
                : chain.interfacesOf.containsKey(cur) ? chain.interfacesOf.get(cur)
                : chain.libInterfacesOf.getOrDefault(cur, List.of()));
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

    /**
     * The new name an override rename ({@link Chain#overrideRenames}) gives the declaration a call naming a jar
     * class reaches: the first class of the jar up from {@code owner} declaring name+desc, when a rename's
     * hierarchy filter takes it. Null when no jar class on the way declares the shape (the call reaches vanilla's)
     * or no rename applies to the declaring class.
     */
    private static String jarOverrideRename(String owner, String name, String desc, LayerIndex layer, Chain chain) {
        if (chain.overrideRenames.isEmpty()) {
            return null;
        }
        for (String cur = owner; cur != null && !Chain.minecraftOwned(cur) && layer.superOf.containsKey(cur);
                cur = layer.superOf.get(cur)) {
            if (!layer.decls.getOrDefault(cur, Set.of()).contains(name + desc)) {
                continue;
            }
            List<String> ifs = layer.interfacesOf.getOrDefault(cur, List.of());
            for (Chain.OverrideRename r : chain.overrideRenames) {
                if (r.oldName.equals(name) && r.oldDesc.equals(desc)
                        && isUnder(layer.superOf.get(cur), ifs.toArray(new String[0]), r.onlyUnder, layer, chain)) {
                    return r.newName;
                }
            }
            return null;
        }
        return null;
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

    /** the first of {@code types} the class's code casts to, tests with instanceof or calls a method of; null if none */
    private static String usesAny(byte[] bytes, Set<String> types) {
        String[] hit = {null};
        try {
            new ClassReader(bytes).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(int acc, String n, String d, String s, String[] x) {
                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public void visitTypeInsn(int op, String type) {
                            if (hit[0] == null && (op == Opcodes.CHECKCAST || op == Opcodes.INSTANCEOF) && types.contains(type)) {
                                hit[0] = type;
                            }
                        }

                        @Override
                        public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                            if (hit[0] == null && types.contains(owner)) {
                                hit[0] = owner;
                            }
                        }
                    };
                }
            }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (RuntimeException e) {
            return null; // not a class file after all
        }
        return hit[0];
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
     * library.
     *
     * A method that class's source hierarchy does not have may be a default
     * method of a vanilla interface the jar's classes implement, which is
     * where the JVM finds it (1.20.2's ChangeOverTimeBlock.onRandomTick
     * reached as this.onRandomTick in a mod's copper block): with the chain
     * given, that interface answers instead, so the wiring keyed on it --
     * rename, redirect, reshape -- applies to the call as well. So it does when
     * the superclass chain leaves the jar into the JDK (a mod class extending
     * Object, Record or Enum that implements a vanilla interface: this.detect(..)
     * in a mod's PlayerDetector), as a JDK class declares no vanilla member; a
     * chain leaving into a library keeps null, as the library's own members may
     * answer. Fields are resolved through the superclass chain only.
     */
    private static String inheritedVanillaOwner(String owner, String member, LayerIndex layer, Chain chain) {
        String inh = inheritedVanillaOwner(owner, member, layer);
        int paren = member.indexOf('(');
        if (chain == null || paren < 0 || inh == null && !leavesIntoJdk(owner, member, layer)) {
            return inh;
        }
        String name = member.substring(0, paren);
        String desc = member.substring(paren);
        if (inh != null && chain.sourceDeclarer(name, desc, List.of(inh)) != null) {
            return inh;
        }
        // the vanilla interfaces the jar's classes on the way implement (through the jar's own interfaces too)
        List<String> vanillaIfaces = new ArrayList<>();
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
        for (String cur = owner; cur != null && !Chain.minecraftOwned(cur) && layer.superOf.containsKey(cur);
                cur = layer.superOf.get(cur)) {
            todo.addAll(layer.interfacesOf.getOrDefault(cur, List.of()));
        }
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String i = todo.poll();
            if (!seen.add(i)) {
                continue;
            }
            if (Chain.minecraftOwned(i)) {
                vanillaIfaces.add(i);
            } else if (layer.superOf.containsKey(i)) {
                todo.addAll(layer.interfacesOf.getOrDefault(i, List.of()));
            }
        }
        String declarer = vanillaIfaces.isEmpty() ? null : chain.sourceDeclarer(name, desc, vanillaIfaces);
        return declarer != null ? declarer : inh;
    }

    /**
     * The direct vanilla superinterface of a class whose default method answers name+desc on the target,
     * when the class's vanilla superclass chain has no such method (1.20.3's ChangeOverTimeBlock.getNextState
     * in a mod's copper block that extends Block): the one INVOKESPECIAL can reach a default through. Null
     * when the superclass has it, the superclass is one of the jar's own, or no direct interface answers.
     */
    private static String defaultHome(Chain chain, String superName, String[] ifaces, String name, String desc) {
        if (superName == null || !superName.startsWith("net/minecraft/") || ifaces == null
                || chain.targetDeclares(name, desc, List.of(superName))) {
            return null;
        }
        for (String i : ifaces) {
            if (Chain.minecraftOwned(i) && chain.targetDeclares(name, desc, List.of(i))) {
                return i;
            }
        }
        return null;
    }

    private static String clsName0(byte[] bytes) {
        return new ClassReader(bytes).getClassName();
    }

    /**
     * Whether the superclass chain from a class of this jar layer reaches a JDK class with no class of
     * the layer on the way declaring the member (the walk of {@link #inheritedVanillaOwner(String, String,
     * LayerIndex)} ending outside the layer in java/).
     */
    private static boolean leavesIntoJdk(String owner, String member, LayerIndex layer) {
        String cur = owner;
        for (int hops = 0; cur != null && hops < 64; hops++) {
            if (Chain.minecraftOwned(cur)) {
                return false;
            }
            Set<String> d = layer.decls.get(cur);
            if (d == null) {
                return cur.startsWith("java/");
            }
            if (d.contains(member)) {
                return false;
            }
            cur = layer.superOf.get(cur);
        }
        return false;
    }

    /**
     * The library type whose wiring serves a call, found as the JVM resolves it, for a call naming a library
     * type (Fabric API's ItemVariant.getNbt(), declared by its TransferVariant) or a class of this jar layer
     * that inherits the member from one (a mod's PacketSender calling this.sendPacket(channel, buf), a mod's
     * provider subclass calling an inherited FabricLootTableProvider member). First the superclass chain: the
     * layer's own classes to the first class outside the layer, then that library class and its library
     * superclasses ({@link Chain#libSuperOf}, the source release's Fabric API); then, for an instance member,
     * the library interfaces of those classes and of the layer's, and their superinterfaces (a default
     * method). The first type the wiring keys the member on answers. Null when a class or interface of the
     * layer declares the member (the mod's own), the chain reaches Minecraft (vanilla wiring answers there),
     * or no library type on the way is keyed ({@link #inheritedVanillaOwner} leaves library classes alone
     * because their own members may answer: a key on the member says it changed there).
     */
    private static String libraryHome(String owner, String member, boolean isStatic, LayerIndex layer, Chain chain,
                                      java.util.function.Predicate<String> keyed) {
        List<String> layerClasses = new ArrayList<>();
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
        String cur = owner;
        for (int hops = 0; cur != null && hops < 64; hops++) {
            if (Chain.minecraftOwned(cur)) {
                return null;
            }
            Set<String> d = layer.decls.get(cur);
            if (d == null) {
                // the first class outside the layer: a library class and its library superclasses
                String c = cur;
                boolean declared = false;
                for (; c != null && !c.startsWith("java/") && !Chain.minecraftOwned(c) && hops < 64;
                        c = chain.libSuperOf.get(c), hops++) {
                    if (keyed.test(c + "." + member)) {
                        return c;
                    }
                    declared |= chain.libDecls.getOrDefault(c, Set.of()).contains(member);
                    todo.addAll(chain.libInterfacesOf.getOrDefault(c, List.of()));
                }
                // a static member reached through library classes above a Minecraft class, none of them declaring
                // it (a mod's Fabric API FabricRecipeProvider subclass calling RecipeProvider's static has(item)
                // through its own name): resolved by declaration, it is the Minecraft class's, served by the wiring
                // keyed there. Not an instance member: the library class may stand on another hierarchy on the
                // target (a superRename), where its own members answer the virtual call.
                if (isStatic && c != null && Chain.minecraftOwned(c) && !declared) {
                    for (String v : chain.selfAndSupers(c)) {
                        if (keyed.test(v + "." + member)) {
                            return v;
                        }
                    }
                }
                break;
            }
            if (d.contains(member)) {
                return null;
            }
            layerClasses.add(cur);
            cur = layer.superOf.get(cur);
        }
        if (isStatic) {
            return null; // a static interface method is not inherited
        }
        for (String c : layerClasses) {
            todo.addAll(layer.interfacesOf.getOrDefault(c, List.of()));
        }
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String t = todo.poll();
            if (!seen.add(t) || Chain.minecraftOwned(t) || t.startsWith("java/")) {
                continue;
            }
            Set<String> d = layer.decls.get(t);
            if (d == null) {
                if (keyed.test(t + "." + member)) {
                    return t;
                }
                todo.addAll(chain.libInterfacesOf.getOrDefault(t, List.of()));
                continue;
            }
            if (d.contains(member)) {
                return null; // a default method of the mod's own interface
            }
            todo.addAll(layer.interfacesOf.getOrDefault(t, List.of()));
        }
        return null;
    }

    /*
     * "Vanilla" here is every class the game owns (Chain.minecraftOwned): net/minecraft/, and the unobfuscated
     * com/mojang/blaze3d/ and com/mojang/realmsclient/ classes. Walking only to net/minecraft/ left a call through
     * a mod subclass of, say, RenderSystem (RenderSystem.initGameThread called as MyRenderSystem.initGameThread)
     * filed as a library member, so the vanilla class's redirects never applied to it.
     */
    private static String inheritedVanillaOwner(String owner, String member, LayerIndex layer) {
        String cur = owner;
        for (int hops = 0; cur != null && hops < 64; hops++) {
            if (Chain.minecraftOwned(cur)) {
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


    /**
     * For a call naming a class of this jar layer: the reshape key's home when the member is a
     * vanilla one the class inherits -- no class or interface of the layer on the way declares it --
     * found in JVM resolution order: the superclass chain to the first vanilla class (the walk of
     * {@link #inheritedVanillaOwner}), then, when that has no key or the chain ends in the JDK, the
     * vanilla interfaces the layer's types implement (a default method, RecipeBuilder.save(consumer)
     * called on a mod builder). A chain that leaves into a library class (Fabric API's
     * FabricRecipeProvider, FakePlayer) takes a reshape keyed on that library class itself: its
     * members are not this layer's to judge, but the key says the member changed there, and the
     * JVM resolves the call through the mod's subclass up to it exactly as for a vanilla class.
     * Null when the mod declares the member, the chain leaves into a library class no reshape is
     * keyed on, or no key applies.
     */
    private static String inheritedReshapeHome(String owner, String member, LayerIndex layer, Chain chain) {
        // the superclass chain first, as the JVM resolves: the layer's own classes, then the first vanilla one
        List<String> layerClasses = new ArrayList<>();
        String cur = owner;
        for (int hops = 0; cur != null && hops < 64 && !Chain.minecraftOwned(cur); hops++) {
            Set<String> d = layer.decls.get(cur);
            if (d == null) {
                if (cur.startsWith("java/")) {
                    cur = null; // the JDK ends the chain (it declares no game member): on to the interfaces
                    break;
                }
                // a library or another jar: a reshape keyed on that library type, found through its own
                // hierarchy (Chain.libSuperOf), applies
                return libraryHome(cur, member, false, layer, chain, chain.callReshapeIndex::containsKey);
            }
            if (d.contains(member) && !chain.sameShapeCalls.contains(member)) {
                return null; // the mod's own member (a same-shape one sits behind a convertHooks bridge)
            }
            layerClasses.add(cur);
            cur = layer.superOf.get(cur);
        }
        if (cur != null && Chain.minecraftOwned(cur)) {
            String home = chain.instanceRedirectHome(chain.callReshapeIndex, cur, member);
            if (home != null) {
                return home;
            }
        }
        // then the interfaces the layer's classes implement (a default method), the layer's own first
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
        for (String c : layerClasses) {
            todo.addAll(layer.interfacesOf.getOrDefault(c, List.of()));
        }
        Set<String> seen = new HashSet<>();
        List<String> vanilla = new ArrayList<>();
        while (!todo.isEmpty()) {
            String t = todo.poll();
            if (!seen.add(t)) {
                continue;
            }
            if (Chain.minecraftOwned(t)) {
                vanilla.add(t);
                continue;
            }
            Set<String> d = layer.decls.get(t);
            if (d == null) {
                // a library interface (Fabric API's PacketSender implemented by a mod's sender): a reshape
                // keyed on it serves the inherited default, as on a library superclass above
                if (!t.startsWith("java/") && chain.callReshapeIndex.containsKey(t + "." + member)) {
                    return t;
                }
                todo.addAll(chain.libInterfacesOf.getOrDefault(t, List.of())); // and its library superinterfaces
                continue;
            }
            if (d.contains(member) && !chain.sameShapeCalls.contains(member)) {
                return null; // a default method of the mod's own interface
            }
            todo.addAll(layer.interfacesOf.getOrDefault(t, List.of()));
        }
        for (String v : vanilla) {
            String home = chain.instanceRedirectHome(chain.callReshapeIndex, v, member);
            if (home != null) {
                return home;
            }
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
     *
     * Two things are settled first. A CenturyBridge layer's classes that retire at this step
     * ({@link Chain#retiredClasses}) leave the jar and its mixin configs. And classRenames, which move
     * references to a class the jar does not define (a dead vanilla class onto its facade, a compat API
     * onto the native one that came back), leave the jar's own classes alone: renaming a class's own
     * declaration leaves an entry whose class carries another name -- a jar that cannot load (a bundled
     * Fabric API module declaring a class step 1 renames, a bridge layer's own compat classes).
     */
    private static void processEntries(Map<String, byte[]> entries, Chain chain,
                                       Report rpt, Set<String> issues, String tag,
                                       String jarTag) throws IOException {
        retire(entries, chain, rpt, tag);
        Map<String, String> own = new HashMap<>();
        for (String n : entries.keySet()) {
            if (n.endsWith(".class")) {
                String cls = n.substring(0, n.length() - 6);
                String to = chain.classRenames.remove(cls);
                if (to != null) {
                    own.put(cls, to);
                    rpt.notes.add(tag + "own class keeps its name: " + cls + " (its classRenames entry, -> " + to
                        + ", moves other jars' references)");
                }
            }
        }
        try {
            processLayer(entries, chain, rpt, issues, tag, jarTag, own);
        } finally {
            chain.classRenames.putAll(own);
        }
    }

    /**
     * {@link Chain#retiredClasses}: the jar's classes an entry names leave it, and the mixin configs its
     * fabric.mod.json names stop listing them -- before anything else reads the jar, so the triage and the
     * rewrite see the layer as it is from this step on.
     */
    private static void retire(Map<String, byte[]> entries, Chain chain, Report rpt, String tag) {
        if (chain.retiredClasses.isEmpty()) {
            return;
        }
        Set<String> gone = new TreeSet<>();
        for (String n : new ArrayList<>(entries.keySet())) {
            if (n.endsWith(".class")) {
                String cls = n.substring(0, n.length() - 6);
                String why = chain.retiredReason(cls);
                if (why != null) {
                    entries.remove(n);
                    gone.add(cls);
                    rpt.notes.add(tag + "retired at " + chain.to + ": " + cls + " -- " + why);
                }
            }
        }
        byte[] fmjBytes = entries.get("fabric.mod.json");
        if (gone.isEmpty() || fmjBytes == null) {
            return;
        }
        JsonObject fmj = GSON.fromJson(new String(fmjBytes, StandardCharsets.UTF_8), JsonObject.class);
        for (JsonElement ref : fmj.has("mixins") ? fmj.getAsJsonArray("mixins") : new JsonArray()) {
            String cfgName = ref.isJsonObject() ? ref.getAsJsonObject().get("config").getAsString() : ref.getAsString();
            byte[] data = entries.get(cfgName);
            if (data == null) {
                continue;
            }
            JsonObject cfg = GSON.fromJson(new String(data, StandardCharsets.UTF_8), JsonObject.class);
            String pkg = cfg.has("package") ? cfg.get("package").getAsString().replace('.', '/') : "";
            boolean edited = false;
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key)) {
                    continue;
                }
                JsonArray kept = new JsonArray();
                for (JsonElement m : cfg.getAsJsonArray(key)) {
                    if (gone.contains((pkg.isEmpty() ? "" : pkg + "/") + m.getAsString().replace('.', '/'))) {
                        edited = true;
                    } else {
                        kept.add(m);
                    }
                }
                cfg.add(key, kept);
            }
            if (edited) {
                entries.put(cfgName, GSON.toJson(cfg).getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static void processLayer(Map<String, byte[]> entries, Chain chain,
                                     Report rpt, Set<String> issues, String tag,
                                     String jarTag, Map<String, String> ownRenames) throws IOException {
        LayerIndex layer = indexLayer(entries);
        Map<String, String> jarSuper = layer.superOf;
        stripSignatures(entries, rpt, tag);
        // the step's wiring knows what became of most members a mixin names: carry the mixins'
        // invokers, accessors, shadows, injector selectors and @At targets across it first, so
        // repair and triage judge the retargeted mixin (MixinBridge; every step but the wall --
        // across it the refmaps take their names later, see repairPastWall)
        MixinBridge.Result bridged = null;
        // a mixin of a class the step renames into a class W adds in its place goes there first (Chain.mixinFollow)
        for (String moved : MixinClassRenames.followTargets(entries, chain.mixinFollow(), chain.classRenames)) {
            rpt.notes.add(tag + "mixin follows its target's class rename: " + shortName(moved));
        }
        if (MixinBridge.applies(chain)) {
            bridged = MixinBridge.run(entries, chain,
                "centurybridge/gen/MixinBridges_" + jarTag + "_" + chain.to.replace('.', '_')
                    + (tag.isEmpty() ? "" : "$" + tag.chars().filter(c -> c == '(').count()));
        }
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
            if (mx.vanilla() && mx.applyFatal(repaired != null || chain.injectorsJudgedPastWall)) {
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
        // a stripped mixin's interfaces are no longer merged onto its target: code of the jar that casts to one
        // or calls through one throws ClassCastException wherever it runs (1.21.2 boot: cb-1.20.2's
        // CbRecipeJsonCookingHooks casting the cooking serializer to CbRecipeJson$CookingTime in its constructor
        // once CbRecipeJsonCooking is stripped at 1.20.5). Mixin applies a mixin whole, so such a mixin is
        // stripped with the reason, like one extending a stripped mixin; any other class using it is reported
        boolean orphans = true;
        while (orphans) {
            orphans = false;
            Set<String> orphaned = new HashSet<>();
            for (String dead : deadMixins) {
                for (String i : layer.interfacesOf.getOrDefault(dead, List.of())) {
                    if (layer.superOf.containsKey(i)) orphaned.add(i); // the jar's own interface
                }
            }
            for (String live : triage.configOfClass().keySet()) {
                if (!deadMixins.contains(live)) orphaned.removeAll(layer.interfacesOf.getOrDefault(live, List.of()));
            }
            for (Map.Entry<String, byte[]> en : new java.util.TreeMap<>(entries).entrySet()) {
                String n = en.getKey();
                if (orphaned.isEmpty() || !n.endsWith(".class")) continue;
                String cls = n.substring(0, n.length() - 6);
                if (deadMixins.contains(cls) || orphaned.contains(cls)) continue;
                String used = usesAny(en.getValue(), orphaned);
                if (used == null) continue;
                if (triage.configOfClass().containsKey(cls)) {
                    deadMixins.add(cls);
                    rpt.strippedMixins.add(tag + shortName(cls) + " -- uses " + shortName(used)
                        + ", which only a stripped mixin merged onto the target (ClassCastException where it runs)");
                    orphans = true;
                } else {
                    String note = tag + "uses an interface only a stripped mixin merged (ClassCastException where it runs): "
                        + shortName(cls) + " -> " + shortName(used);
                    if (!rpt.notes.contains(note)) rpt.notes.add(note);
                }
            }
        }
        stripConfigs(entries, triage, deadMixins);
        markConfigsOptional(entries, triage);
        if (bridged != null) {
            // a member MixinBridge carried counts once its mixin survived triage and repair kept its injector
            for (String l : bridged.report(deadMixins, repaired != null ? repaired.disabled() : List.of())) {
                rpt.notes.add(tag + l);
            }
        }

        // tombstone stub class must be unique per jar layer (all layers share the runtime classpath)
        // and per climb step: each step converts the jar again, and a later step's Tombstones class
        // of the same name would replace the earlier step's, whose call sites still point into it
        String tombClass = "centurybridge/gen/Tombstones_" + jarTag + "_" + chain.to.replace('.', '_')
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
                if (verbatimLibrary(nested.get("fabric.mod.json"))) {
                    continue; // a library a layer gives back whole (nest_libraries.py): carried byte for byte
                }
                // sibling nested jars share the flat runtime classpath too, so
                // the nested jar's own name joins the uniqueness tag
                String nestedTag = jarTag + "_" + name.substring("META-INF/jars/".length())
                    .replaceAll("\\.jar$", "").replaceAll("[^A-Za-z0-9]", "_");
                // a bundled jar is another jar: the renames kept off this jar's own classes apply to it
                chain.classRenames.putAll(ownRenames);
                try {
                    processEntries(nested, chain, rpt, issues, tag + "(bundled) ", nestedTag);
                } finally {
                    ownRenames.keySet().forEach(chain.classRenames::remove);
                }
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
            } else if (name.endsWith(".json") && name.contains("refmap") && !chain.classRenames.isEmpty()) {
                // the mixins' refmap names classes in strings the class rewrite never reads: the
                // step's class renames carried into its values (MixinClassRenames)
                Map<String, String> vanillaRenames = MixinClassRenames.forVanilla(chain.classRenames, c -> wDeclares(chain, c));
                byte[] rew = MixinClassRenames.refmap(e.getValue(), chain.classRenames, mixinCls -> {
                    byte[] mb = entries.get(mixinCls + ".class");
                    if (mb == null) {
                        return chain.classRenames;
                    }
                    org.objectweb.asm.tree.ClassNode mcn = new org.objectweb.asm.tree.ClassNode();
                    new org.objectweb.asm.ClassReader(mb).accept(mcn, org.objectweb.asm.ClassReader.SKIP_CODE);
                    List<String> ts = MixinRepair.mixinTargets(mcn, null);
                    return ts.isEmpty() || MixinClassRenames.facadeMixin(ts, chain.classRenames) ? chain.classRenames : vanillaRenames;
                });
                if (rew != null) {
                    e.setValue(rew);
                    rpt.notes.add(tag + "refmap " + name + ": class renames carried into its values");
                }
            }
        }
        if (!reg.stubs.isEmpty()) {
            entries.put(tombClass + ".class", reg.synthesize());
        }
        // the data pack and resource pack are read by the target's loaders, in the
        // target's formats: carry them over the same boundaries the bytecode crossed
        top.dext.centurybridge.engine.res.ResourceTranslator.Outcome res =
            top.dext.centurybridge.engine.res.ResourceTranslator.apply(entries, chain.from, chain.to, chain.resourceTypes,
                chain.tagMap, chain.tagMirror);
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

    /**
     * A nested jar marked "centurybridge:verbatim" in its fabric.mod.json custom values: a whole library a step layer gives
     * back (core/tools/nest_libraries.py: the release's own artifact, checked against its version-JSON sha1). Converting,
     * remapping or repairing it would change a library that has no Minecraft reference to change: it climbs byte for byte.
     */
    public static boolean verbatimLibrary(byte[] fmj) {
        if (fmj == null) {
            return false;
        }
        try {
            var root = com.google.gson.JsonParser.parseString(new String(fmj, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            var custom = root.getAsJsonObject("custom");
            return custom != null && custom.has("centurybridge:verbatim") && custom.get("centurybridge:verbatim").getAsBoolean();
        } catch (RuntimeException ex) {
            return false;
        }
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
                if (verbatimLibrary(nested.get("fabric.mod.json"))) {
                    continue;
                }
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
        if (!chain.convertHooks.isEmpty()) {
            // hooked lambdas first: their forwarders call the implementation, a call site for the passes below
            byte[] b7 = rewriteConvertHookLambdas(bytes, chain, layer, rpt, tag);
            ctorChanged |= b7 != bytes;
            bytes = b7;
        }
        if (!chain.checkCastRedirects.isEmpty()) {
            // the cast LambdaMetafactory makes on a lambda's argument is a checkcast site too
            byte[] b8 = rewriteCastLambdas(bytes, chain, rpt, tag);
            ctorChanged |= b8 != bytes;
            bytes = b8;
        }
        if (!chain.callReshapes.isEmpty()) {
            // method references first: their forwarders are call sites for the call pass
            byte[] b6 = rewriteReshapedMethodRefs(bytes, chain, layer, rpt, tag);
            ctorChanged |= b6 != bytes;
            bytes = b6;
            byte[] b3 = rewriteCalls(bytes, chain, layer, rpt, tag);
            ctorChanged |= b3 != bytes;
            bytes = b3;
        }
        if (!chain.convertHooks.isEmpty()) {
            // after the call pass: the bridges' own calls of the renamed bodies are not call sites to convert
            byte[] b5 = rewriteConvertHooks(bytes, chain, layer, rpt, tag);
            ctorChanged |= b5 != bytes;
            bytes = b5;
        }
        if (!chain.overrideHooks.isEmpty()) {
            byte[] b4 = rewriteHookSuperCalls(bytes, chain, layer, rpt, tag);
            ctorChanged |= b4 != bytes;
            bytes = b4;
        }
        Map<String, String> redirects = new HashMap<>();     // static: owner.name+desc -> runtime class
        Map<String, String> instRedirects = new HashMap<>(); // instance: receiver becomes arg 0
        Map<String, String> ifaceCallRedirects = new HashMap<>(); // INVOKEINTERFACE only: receiver becomes arg 0
        Map<String, String> fldRedirects = new HashMap<>();  // GETSTATIC: owner.name:desc -> runtime class
        Map<String, String> fldGetters = new HashMap<>();    // GETSTATIC -> INVOKESTATIC zero-arg getter on runtime class
        Map<String, String> fldSetters = new HashMap<>();    // PUTSTATIC -> INVOKESTATIC setter(value)V on runtime class
        Map<String, String> instFldGetters = new HashMap<>(); // GETFIELD -> INVOKESTATIC getter(receiver) on runtime class
        Map<String, String> instFldSetters = new HashMap<>(); // PUTFIELD -> INVOKESTATIC setter(receiver, value) on runtime class
        Map<String, String> instOfSites = new HashMap<>();   // INSTANCEOF type -> INVOKESTATIC predicate(Object)Z
        Map<String, String> castSites = new HashMap<>();     // CHECKCAST type -> INVOKESTATIC check(Object)Object first
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
        // a library type's member is keyed for the call / handle rewrite (libraryHome)
        final java.util.function.Predicate<String> libraryKeyed = k -> chain.instanceRedirects.containsKey(k)
            || chain.staticRedirects.containsKey(k) || chain.interfaceCallRedirects.containsKey(k)
            || chain.methodRenames.containsKey(k) || chain.superRedirects.containsKey(k);

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
        // overrides of a method the target made final / renamed, declared here (Chain.overrideRenames)
        final java.util.List<Chain.OverrideRename> declaredRenames = new ArrayList<>();
        final Set<Chain.OverrideHook> declaredNewOverrides = new HashSet<>();
        final int[] classAccess = {0};
        final String[] superOf = {null};
        final String[][] ifacesOf = {null};
        // call sites hitting a lookup-hook's old descriptor; a class whose ONLY
        // damage is such a call must still go through the rewrite pass -- the
        // fix used to ride along on other damage and silently skip these
        final Set<String> hookCallSites = new HashSet<>();
        // lambdas built for a functional interface whose SAM changed shape (Chain.lambdaAdapters)
        final Set<String> lambdaSites = new HashSet<>();
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
                        // under a Minecraft type or a library (Fabric API) one: isUnder walks the jar, the
                        // source stub with its injected interfaces, and the source Fabric API's hierarchy
                        if (declaredShapes.containsAll(h.requires) && isUnder(superOf[0], itfs, h.under, layer, chain)) {
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
                ifacesOf[0] = ifaces;
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
                        if (oh.onlyUnder != null && !isUnder(superOf[0], itfs, oh.onlyUnder, layer, chain)) {
                            continue;
                        }
                        if (oh.notUnder != null && isUnder(superOf[0], itfs, oh.notUnder, layer, chain)) {
                            continue;
                        }
                        declaredOldOverrides.add(oh);
                        hooked = true;
                    }
                }
                if ((acc & Opcodes.ACC_STATIC) == 0) {
                    for (Chain.OverrideRename r : chain.overrideRenames) {
                        if (r.oldName.equals(mName) && r.oldDesc.equals(mDesc)
                                && isUnder(superOf[0], itfs, r.onlyUnder, layer, chain)) {
                            declaredRenames.add(r);
                            hooked = true;
                            top.dext.centurybridge.data.FullAudit.record(mName + mDesc, "override-renamed");
                        }
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
                        // a super call (super(..) included) into the type this class extends through
                        // superRenames goes to the facade -- the superRename layer retargets it, and the
                        // facade serves the member itself. Wired here as damage it would be redirected
                        // to a static (a virtual call again: back into the class's own override) or
                        // reported dead, though the facade answers it
                        if (op == Opcodes.INVOKESPECIAL && needSuperRename[0] && chain.superRenames.containsKey(owner)) {
                            top.dext.centurybridge.data.FullAudit.record(site, "super-renamed");
                            return;
                        }
                        String own = ownRenamed(owner, name, desc, op, layer, chain);
                        if (own != null) {
                            mthRenames.put(site, own);
                            top.dext.centurybridge.data.FullAudit.record(site, "override-renamed-call");
                            return;
                        }
                        if (!Chain.minecraftOwned(owner)) {
                            // a call naming a class of this jar whose own declaration of the shape an override
                            // rename moves (1.21.2's final AbstractBoat.getDropItem: a mod Boat's override is
                            // renamed) follows the rename: the old name would resolve past the renamed body to
                            // vanilla's -- inaccessible, or not the method the mod meant
                            if (op != Opcodes.INVOKESTATIC && op != Opcodes.INVOKESPECIAL && !name.equals("<init>")) {
                                String renamed = jarOverrideRename(owner, name, desc, layer, chain);
                                if (renamed != null) {
                                    mthRenames.put(site, renamed);
                                    top.dext.centurybridge.data.FullAudit.record(site, "override-rename-call");
                                    return;
                                }
                            }
                            String inh = name.equals("<init>") ? null
                                : inheritedVanillaOwner(owner, name + desc, layer, chain);
                            if (inh == null) {
                                // library owners are outside the stub diffs, but a
                                // redirect wired explicitly (DFU's Codec.dispatch
                                // contract change hides behind generic erasure)
                                // still applies -- also when the site names a class of
                                // this jar that inherits the member from that library
                                // type (libraryHome: a mod's PacketSender calling
                                // this.sendPacket(channel, buf))
                                String lib = name.equals("<init>") ? null
                                    : libraryHome(owner, name + desc, op == Opcodes.INVOKESTATIC, layer, chain, libraryKeyed);
                                final String lsite = lib != null ? lib + "." + name + desc : site;
                                // a super call into a library type's default (a mod's PacketSender implementation
                                // calling PacketSender.super.sendPacket(..)) takes the super redirect: the default
                                // body, not a dispatch back into the override making the call
                                String lsr = op == Opcodes.INVOKESPECIAL && !name.equals("<init>") ? chain.superRedirects.get(lsite) : null;
                                if (lsr != null) {
                                    superRedirectSites.put(site, lsr);
                                    if (lib != null) {
                                        emitOwner.put(site, lib);
                                    }
                                    top.dext.centurybridge.data.FullAudit.record(lsite, "super-redirect");
                                    return;
                                }
                                // an instance redirect found through an inherited library member serves
                                // virtual and interface calls only, not a super call: the redirect's own
                                // dispatch would come back into the caller's override
                                String lrt = lib != null && op == Opcodes.INVOKESPECIAL ? null : chain.instanceRedirects.get(lsite);
                                if (lrt != null && op != Opcodes.INVOKESTATIC && !name.equals("<init>")) {
                                    instRedirects.put(site, lrt);
                                    if (lib != null) {
                                        emitOwner.put(site, lib);
                                    }
                                    top.dext.centurybridge.data.FullAudit.record(lsite, "instance-redirect");
                                }
                                String lsrt = chain.staticRedirects.get(lsite);
                                if (lsrt != null && op == Opcodes.INVOKESTATIC) {
                                    redirects.put(site, lsrt);
                                    top.dext.centurybridge.data.FullAudit.record(lsite, "static-redirect");
                                }
                                // interface calls only: a super call to the interface's default stays (Chain.interfaceCallRedirects)
                                String licr = chain.interfaceCallRedirects.get(lsite);
                                if (licr != null && op == Opcodes.INVOKEINTERFACE) {
                                    ifaceCallRedirects.put(site, licr);
                                    if (lib != null) {
                                        emitOwner.put(site, lib);
                                    }
                                    top.dext.centurybridge.data.FullAudit.record(lsite, "interface-call-redirect");
                                }
                                // a library member that only changed its name (Fabric API's
                                // FabricItem.allowNbtUpdateAnimation -> allowComponentsUpdateAnimation):
                                // the site keeps its owner, the JVM resolves the new name the same way
                                String lmr = lrt == null && lsrt == null && licr == null ? chain.methodRenames.get(lsite) : null;
                                if (lmr != null) {
                                    mthRenames.put(site, lmr);
                                    top.dext.centurybridge.data.FullAudit.record(lsite, "method-rename");
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
                        if (chain.classRenames.containsKey(owner) && !chain.classRenames.get(owner).startsWith("top/dext/")
                                && !name.equals("<init>")) {
                            // a class renamed to a surviving vanilla class (1.21.2: Boat -> AbstractBoat) only
                            // stands in for the members it declared itself: a member it inherited in the source
                            // (Entity.kill reached through Boat) is judged on its source declarer, as any vanilla
                            // owner's call would be. The site still names the renamed class, a subtype of it
                            String home = chain.sourceDeclarer(name, desc, java.util.List.of(owner));
                            if (home != null && !home.equals(owner)) {
                                owner = home;
                            }
                        }
                        if (chain.classRenames.containsKey(owner)) {
                            // a class merged into a surviving VANILLA class (BakedModel ->
                            // BlockStateModel) keeps its old members only where a redirect is
                            // wired explicitly on the old owner; the Statics overload takes the
                            // renamed type because the emitted descriptor goes through the same
                            // ClassRemapper. Everything else lives on the facade / renamed class.
                            String rkey = owner + "." + name + desc;
                            // a super call into a dead class renamed to a CenturyBridge facade resolves on the
                            // facade, which serves the class's members itself (the build gate holds it to the
                            // whole surface), as a super call into a superRenames facade does: an instance
                            // redirect wired for the type's other callers would turn it into a virtual call
                            // again -- back into the class's own override
                            if (op == Opcodes.INVOKESPECIAL && !name.equals("<init>")
                                    && chain.classRenames.get(owner).startsWith("top/dext/") && !chain.facadeGaps.containsKey(rkey)) {
                                top.dext.centurybridge.data.FullAudit.record(rkey, "super-facade");
                                return;
                            }
                            // a class merged into a surviving vanilla superclass whose members moved there
                            // under new names (1.21.2: Boat into AbstractBoat): a super call takes the super
                            // redirect wired on the old owner -- an instance redirect would dispatch back into
                            // the caller's own override -- and a member that only changed its name keeps its
                            // site, renamed; the ClassRemapper then names the new class
                            if (op == Opcodes.INVOKESPECIAL && !name.equals("<init>") && chain.superRedirects.containsKey(rkey)) {
                                superRedirectSites.put(site, chain.superRedirects.get(rkey));
                                top.dext.centurybridge.data.FullAudit.record(rkey, "super-redirect");
                                return;
                            }
                            String rmr = name.equals("<init>") ? null : chain.methodRenames.get(rkey);
                            if (rmr != null) {
                                mthRenames.put(site, rmr);
                                top.dext.centurybridge.data.FullAudit.record(rkey, "method-rename");
                                return;
                            }
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
                        if (op == Opcodes.INVOKESPECIAL) {
                            // a super call resolves where the source version declared the member
                            // (Chain.superRedirectHome): a vanilla subclass in between that does not
                            // redeclare it takes the declaring class's super redirect, and the
                            // emitted static names that class as the receiver type
                            String srHome = chain.superRedirectHome(owner, name + desc);
                            if (srHome != null) {
                                superRedirectSites.put(site, chain.superRedirects.get(srHome + "." + name + desc));
                                if (!srHome.equals(owner)) {
                                    emitOwner.put(site, srHome);
                                }
                                top.dext.centurybridge.data.FullAudit.record(srHome + "." + name + desc, "super-redirect");
                                return;
                            }
                        }
                        // a shim on any ancestor satisfies this call site: the mod
                        // holds a subclass reference, but the member is declared
                        // (and bridged) further up -- unless an instance redirect keys
                        // it on a nearer class (Chain#instanceRedirectNearerThanCover)
                        String covHome = chain.coverHome(owner, name + desc);
                        if (covHome != null && !chain.instanceRedirectNearerThanCover(owner, name + desc, covHome)) {
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
                        // an interface call through a vanilla interface, found as an instance redirect is (the
                        // supertype the key names may be a Fabric API interface injected into it); only
                        // INVOKEINTERFACE, the super call keeps reaching the default (Chain.interfaceCallRedirects)
                        String icrHome = op == Opcodes.INVOKEINTERFACE
                            ? chain.instanceRedirectHome(chain.interfaceCallRedirects, owner, name + desc) : null;
                        if (icrHome != null) {
                            ifaceCallRedirects.put(site, chain.interfaceCallRedirects.get(icrHome + "." + name + desc));
                            emitOwner.put(site, icrHome);
                            top.dext.centurybridge.data.FullAudit.record(key, "interface-call-redirect");
                            return;
                        }
                        // found where the JVM resolves the site: a Fabric API member injected into a vanilla
                        // class (Item.getRecipeRemainder is FabricItem's) through the class's interfaces
                        String mrnHome = chain.methodRenameHome(owner, name + desc, op == Opcodes.INVOKESTATIC);
                        if (mrnHome != null) {
                            mthRenames.put(site, chain.methodRenames.get(mrnHome + "." + name + desc));
                            top.dext.centurybridge.data.FullAudit.record(mrnHome + "." + name + desc, "method-rename");
                            return;
                        }
                        if (chain.callReshapeTargets.contains(name + desc)) {
                            return; // the call pre-pass (rewriteCalls) already gave it the step's new shape
                        }
                        if (op == Opcodes.INVOKESPECIAL && chain.superAccessorTargets.contains(name + desc)) {
                            return; // a super-bridge accessor (rewriteHookSuperCalls) calling super's new shape
                        }
                        Chain.Issue r = chain.resolveMember('m', name);
                        // judged as the converted jar will spell the site: the step's classRenames move the
                        // owner and the descriptor's classes (26.2: RecipeBuilder.unlockedBy(String,
                        // advancements/Criterion) is unlockedBy(String, advancements/triggers/Criterion))
                        if (r.level() == Chain.Level.OK && !name.startsWith("<") && !Chain.intermediaryClass(owner)
                                && Boolean.FALSE.equals(chain.resolvesOnTarget(chain.classRenames.getOrDefault(owner, owner),
                                    name + chain.renamedDesc(desc)))) {
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
                                // found where the JVM resolves the field: a mod provider reading a renamed
                                // protected field of its Fabric API superclass names itself as the owner
                                // (libraryHome; a field of an interface is inherited, so its interfaces too)
                                String frnHome = chain.fieldRenames.containsKey(fsite) ? owner
                                    : libraryHome(owner, name + ":" + desc, false, layer, chain, chain.fieldRenames::containsKey);
                                String frn = frnHome == null ? null : chain.fieldRenames.get(frnHome + "." + name + ":" + desc);
                                if (frn != null) {
                                    fldRenames.put(fsite, frn);
                                    top.dext.centurybridge.data.FullAudit.record(frnHome + "." + name + ":" + desc, "field-rename");
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
                                String ffst = chain.staticFieldSetters.get(fsite);
                                if (ffst != null && op == Opcodes.PUTSTATIC) {
                                    fldSetters.put(fsite, ffst);
                                    top.dext.centurybridge.data.FullAudit.record(fsite, "field-setter");
                                    return;
                                }
                                // a library instance field that is gone and lives on as other state (1.21.11's
                                // protected GameRuleBuilder.fabricCategory, read and written by a mod's builder
                                // subclass under its own name): found as the JVM resolves it (libraryHome), the
                                // static takes the receiver as the keyed library class
                                if (op == Opcodes.GETFIELD || op == Opcodes.PUTFIELD) {
                                    java.util.Map<String, String> fmap = op == Opcodes.GETFIELD
                                        ? chain.instanceFieldGetters : chain.instanceFieldSetters;
                                    String ifHome = fmap.containsKey(fsite) ? owner
                                        : libraryHome(owner, name + ":" + desc, false, layer, chain, fmap::containsKey);
                                    if (ifHome != null) {
                                        String hk = ifHome + "." + name + ":" + desc;
                                        (op == Opcodes.GETFIELD ? instFldGetters : instFldSetters).put(fsite, fmap.get(hk));
                                        emitOwner.put(fsite, ifHome);
                                        top.dext.centurybridge.data.FullAudit.record(hk,
                                            op == Opcodes.GETFIELD ? "instance-field-getter" : "instance-field-setter");
                                        return;
                                    }
                                }
                                return;
                            }
                            // this.field_22789 inside a Screen subclass: an inherited
                            // vanilla field referenced through the mod's own class
                            owner = inh;
                            emitOwner.put(fsite, inh);
                        }
                        if (chain.classRenames.containsKey(owner)) {
                            // as for methods: a class merged into a surviving vanilla class keeps an old
                            // static field the new class lacks only where a redirect or getter is wired
                            // on the old owner (1.20.5 moved SuspiciousEffectHolder.EffectEntry into
                            // SuspiciousStewEffects.Entry without its LIST_CODEC)
                            String rkey = owner + "." + name + ":" + desc;
                            String rfrt = chain.fieldRedirects.get(rkey);
                            if (rfrt != null && op == Opcodes.GETSTATIC) {
                                fldRedirects.put(fsite, rfrt);
                                top.dext.centurybridge.data.FullAudit.record(rkey, "field-redirect");
                                return;
                            }
                            String rfgt = chain.fieldGetters.get(rkey);
                            if (rfgt != null && op == Opcodes.GETSTATIC) {
                                fldGetters.put(fsite, rfgt);
                                top.dext.centurybridge.data.FullAudit.record(rkey, "field-getter");
                                return;
                            }
                            String rfst = chain.staticFieldSetters.get(rkey);
                            if (rfst != null && op == Opcodes.PUTSTATIC) {
                                fldSetters.put(fsite, rfst);
                                top.dext.centurybridge.data.FullAudit.record(rkey, "field-setter");
                                return;
                            }
                            // and an old instance field the new class keeps as other state (26.2 folded
                            // DripstoneClusterConfiguration's public fields into SpeleothemClusterConfiguration's
                            // record components): the getter / setter wired on the old owner, which takes the
                            // receiver as the renamed class
                            if (op == Opcodes.GETFIELD || op == Opcodes.PUTFIELD) {
                                String rift = (op == Opcodes.GETFIELD ? chain.instanceFieldGetters : chain.instanceFieldSetters).get(rkey);
                                if (rift != null) {
                                    (op == Opcodes.GETFIELD ? instFldGetters : instFldSetters).put(fsite, rift);
                                    emitOwner.put(fsite, owner);
                                    top.dext.centurybridge.data.FullAudit.record(rkey,
                                        op == Opcodes.GETFIELD ? "instance-field-getter" : "instance-field-setter");
                                    return;
                                }
                            }
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
                        String fst = chain.staticFieldSetters.get(fkey);
                        if (fst != null && op == Opcodes.PUTSTATIC) {
                            fldSetters.put(fsite, fst);
                            top.dext.centurybridge.data.FullAudit.record(fkey, "field-setter");
                            return;
                        }
                        // an instance field is keyed on the class that declares it, but javac names
                        // the receiver's static type (JLS 13.1): this.width in an ObjectSelectionList
                        // subclass is ObjectSelectionList.width, declared by AbstractSelectionList. The
                        // getter / setter is found up the hierarchy and takes the receiver as that class
                        String ifstHome = op == Opcodes.PUTFIELD
                            ? chain.redirectHome(chain.instanceFieldSetters, owner, name + ":" + desc) : null;
                        if (ifstHome != null) {
                            String hk = ifstHome + "." + name + ":" + desc;
                            instFldSetters.put(fsite, chain.instanceFieldSetters.get(hk));
                            emitOwner.put(fsite, ifstHome);
                            top.dext.centurybridge.data.FullAudit.record(hk, "instance-field-setter");
                            return;
                        }
                        String ifgtHome = op == Opcodes.GETFIELD
                            ? chain.redirectHome(chain.instanceFieldGetters, owner, name + ":" + desc) : null;
                        if (ifgtHome != null) {
                            String hk = ifgtHome + "." + name + ":" + desc;
                            instFldGetters.put(fsite, chain.instanceFieldGetters.get(hk));
                            emitOwner.put(fsite, ifgtHome);
                            top.dext.centurybridge.data.FullAudit.record(hk, "instance-field-getter");
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
                        if (op == Opcodes.CHECKCAST && chain.checkCastRedirects.containsKey(type)) {
                            castSites.put(type, chain.checkCastRedirects.get(type));
                            top.dext.centurybridge.data.FullAudit.record("checkcast " + type, "checkcast-redirect");
                        }
                        if (op == Opcodes.NEW) {
                            pendingNew.computeIfAbsent(type, k -> new java.util.ArrayDeque<>()).push(Boolean.TRUE);
                        }
                        checkClass(type);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name, String desc,
                            org.objectweb.asm.Handle bsm, Object... bsmArgs) {
                        Chain.LambdaAdapter la = lambdaAdapterFor(chain, name, desc, bsm, bsmArgs);
                        if (la != null) {
                            String lkey = la.iface + "." + la.sam + la.samDesc;
                            if (bsm.getName().equals("metafactory")) {
                                lambdaSites.add(lkey);
                                top.dext.centurybridge.data.FullAudit.record(lkey, "lambda-adapted");
                            } else {
                                // altMetafactory: a serializable lambda names its interface in
                                // $deserializeLambda$, markers and bridges add types -- not ours to swap
                                top.dext.centurybridge.data.FullAudit.record(lkey,
                                    "LAMBDA-NOT-ADAPTED[" + chain.sideOf(la.iface) + "] " + bsm.getName());
                                issues.add(tag + "lambda not adapted (" + bsm.getName()
                                    + ": serializable / marker / bridge lambda keeps its interface): " + lkey);
                            }
                        } else if (lambdaRename(chain, name, desc, bsm, bsmArgs) != null) {
                            String lkey = org.objectweb.asm.Type.getReturnType(desc).getInternalName() + "." + name
                                + ((org.objectweb.asm.Type) bsmArgs[0]).getDescriptor();
                            lambdaSites.add(lkey);
                            top.dext.centurybridge.data.FullAudit.record(lkey, "lambda-renamed");
                            if (bsm.getName().equals("altMetafactory") && bsmArgs.length > 3 && bsmArgs[3] instanceof Integer fl
                                    && (fl & 1) != 0) {
                                // LambdaMetafactory.FLAG_SERIALIZABLE: $deserializeLambda$ compares the old name as a string
                                rpt.notes.add(tag + "serializable lambda renamed, its deserialization still names the old SAM: " + lkey);
                            }
                        }
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
                                    : inheritedVanillaOwner(hOwner, h.getName() + h.getDesc(), layer, chain);
                                if (inh == null) {
                                    // library-owner method references carry the same
                                    // explicitly-wired redirects as direct calls
                                    // (Codec::dispatch as a lambda breaks identically),
                                    // through this jar's classes too (libraryHome)
                                    boolean hStatic = h.getTag() == Opcodes.H_INVOKESTATIC;
                                    String lib = h.getName().equals("<init>") ? null
                                        : libraryHome(hOwner, h.getName() + h.getDesc(), hStatic, layer, chain, libraryKeyed);
                                    final String lsite = lib != null ? lib + "." + h.getName() + h.getDesc() : site;
                                    // as for a call: an inherited library member's instance redirect serves
                                    // virtual and interface handles, not a special (super) one
                                    String lrt = lib != null && h.getTag() == Opcodes.H_INVOKESPECIAL ? null
                                        : chain.instanceRedirects.get(lsite);
                                    if (lrt != null && !hStatic && !h.getName().equals("<init>")) {
                                        instRedirects.put(site, lrt);
                                        if (lib != null) {
                                            emitOwner.put(site, lib);
                                        }
                                        top.dext.centurybridge.data.FullAudit.record(lsite, "instance-redirect");
                                    }
                                    String lsrt = chain.staticRedirects.get(lsite);
                                    if (lsrt != null && hStatic) {
                                        redirects.put(site, lsrt);
                                        top.dext.centurybridge.data.FullAudit.record(lsite, "static-redirect");
                                    }
                                    String licr = chain.interfaceCallRedirects.get(lsite);
                                    if (licr != null && h.getTag() == Opcodes.H_INVOKEINTERFACE) {
                                        ifaceCallRedirects.put(site, licr);
                                        if (lib != null) {
                                            emitOwner.put(site, lib);
                                        }
                                        top.dext.centurybridge.data.FullAudit.record(lsite, "interface-call-redirect");
                                    }
                                    String lmr = lrt == null && lsrt == null && licr == null ? chain.methodRenames.get(lsite) : null;
                                    if (lmr != null) {
                                        mthRenames.put(site, lmr);
                                        top.dext.centurybridge.data.FullAudit.record(lsite, "method-rename");
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
                                // after the redirects, as for a call: a method reference to a renamed member reached through a vanilla class (an
                                // injected Fabric API member: Item::getRecipeRemainder), resolved as a call is
                                String hmrnHome = h.getName().startsWith("<") ? null
                                    : chain.methodRenameHome(hOwner, h.getName() + h.getDesc(), h.getTag() == Opcodes.H_INVOKESTATIC);
                                if (hmrnHome != null) {
                                    mthRenames.put(site, chain.methodRenames.get(hmrnHome + "." + h.getName() + h.getDesc()));
                                    top.dext.centurybridge.data.FullAudit.record(hmrnHome + "." + h.getName() + h.getDesc(), "method-rename");
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

        // abstract fills: implementations the target newly demands of a concrete class (Chain.abstractFills)
        final List<Chain.AbstractFill> fillApply = new ArrayList<>();
        if ((classAccess[0] & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT)) == 0 && !chain.abstractFills.isEmpty()) {
            List<String> fillRoots = null;
            for (Chain.AbstractFill f : chain.abstractFills) {
                String shape = f.name + f.desc;
                if (declaredShapes.contains(shape) || !isUnder(superOf[0], ifacesOf[0], f.under, layer, chain)
                        || inheritedVanillaOwner(superOf[0], shape, layer) == null && layer.superOf.containsKey(superOf[0])) {
                    continue; // declared here, not under the type, or a jar superclass declares it
                }
                if (fillApply.stream().anyMatch(x -> (x.name + x.desc).equals(shape))) {
                    continue; // two entries give the class the same method (it is under both types): the first fills it
                }
                boolean synthesized = declaredOldOverrides.stream().anyMatch(h -> (h.newName + h.newDesc).equals(shape))
                    || adapterApply.stream().anyMatch(h -> h.synth.stream().anyMatch(sy -> (sy[0] + sy[1]).equals(shape)));
                if (synthesized) {
                    continue; // a hook gives the class the method
                }
                if (fillRoots == null) {
                    fillRoots = vanillaAncestors(superOf[0], ifacesOf[0], layer);
                }
                if (chain.concreteOnTarget(f.name, f.desc, fillRoots)) {
                    continue; // inherits a concrete one
                }
                fillApply.add(f);
                rpt.notes.add(tag + "abstract-filled: " + clsName0(bytes) + "." + shape);
                top.dext.centurybridge.data.FullAudit.record(f.under + "." + shape, "abstract-fill");
            }
        }
        // a redirect static takes its receiver as the type the wiring keys the member on (emitOwner); that type
        // may be one the class never named -- a Fabric API interface reached through the vanilla type it is
        // injected into (TagAppender.remove is 1.21.11's FabricProvidedTagBuilder's) -- and a renamed one: the
        // class-rename pass must then run on this class too, or the emitted descriptor keeps the source name
        if (!needClassRename) {
            for (String o : emitOwner.values()) {
                if (chain.classRenames.containsKey(o)) {
                    needClassRename = true;
                    rpt.notes.add(tag + "facade-renamed (redirect receiver): " + o + " -> " + chain.classRenames.get(o));
                    break;
                }
            }
        }
        if (redirects.isEmpty() && instRedirects.isEmpty() && ifaceCallRedirects.isEmpty() && fldRedirects.isEmpty()
                && fldGetters.isEmpty() && fldSetters.isEmpty() && instFldGetters.isEmpty() && instFldSetters.isEmpty() && instOfSites.isEmpty() && castSites.isEmpty()
                && fldRenames.isEmpty() && mthRenames.isEmpty() && tombstone.isEmpty()
                && !needClassRename && !needIfaceFix && hookCallSites.isEmpty() && superRedirectSites.isEmpty()
                && !needSuperRename[0] && adapterApply.isEmpty() && lambdaSites.isEmpty() && declaredRenames.isEmpty()
                && fillApply.isEmpty()
                && !(hasOldUse[0] && !hasNewUse[0])
                && declaredOldHooks.stream().allMatch(declaredNewHooks::contains)
                && declaredOldOverrides.stream().allMatch(declaredNewOverrides::contains)) {
            return ctorChanged ? bytes : null; // "unchanged" must not discard the constructor pre-pass
        }
        final java.util.List<Chain.OverrideHook> reconnectOverrides = new ArrayList<>();
        if ((classAccess[0] & Opcodes.ACC_INTERFACE) == 0) {
            // two old overrides folding into ONE new shape (Entity.interact + interactAt ->
            // interact(player, hand, pos)): a second synthesized method with the same name+desc is a
            // ClassFormatError, so one hook reconnects the shape and the other is noted. The hook that
            // states it shadows the other wins (Chain.OverrideHook#shadows); else the first in wiring
            // order -- not the order the class happens to declare its methods in
            java.util.List<Chain.OverrideHook> ordered = new ArrayList<>(declaredOldOverrides);
            ordered.sort(java.util.Comparator.comparingInt(chain.overrideHooks::indexOf));
            java.util.Map<String, Chain.OverrideHook> byShape = new java.util.LinkedHashMap<>();
            java.util.List<Chain.OverrideHook> shadowed = new ArrayList<>();
            for (Chain.OverrideHook h : ordered) {
                if (declaredNewOverrides.contains(h)) {
                    continue;
                }
                String shape = h.newName + h.newDesc;
                Chain.OverrideHook x = byShape.get(shape);
                if (x == null) {
                    byShape.put(shape, h);
                } else if (x != h) {
                    if (h.shadows.contains(x.oldName + x.oldDesc) && !x.shadows.contains(h.oldName + h.oldDesc)) {
                        byShape.put(shape, h);
                        shadowed.add(x);
                    } else {
                        shadowed.add(h);
                    }
                }
            }
            for (Chain.OverrideHook h : shadowed) {
                rpt.notes.add(tag + "override-hook skipped (new shape already synthesized from another old override): "
                    + h.oldName + h.oldDesc);
                top.dext.centurybridge.data.FullAudit.record(h.oldName + h.oldDesc, "override-hook-shadowed");
            }
            java.util.List<String> hookRoots = null;
            for (Chain.OverrideHook h : byShape.values()) {
                if (hookRoots == null) {
                    hookRoots = vanillaAncestors(superOf[0], ifacesOf[0], layer);
                }
                if (chain.finalOnTarget(h.newName, h.newDesc, hookRoots)) {
                    // the target made the new shape final under this class (1.21.2's ItemEntity.hurtServer): a
                    // synthesized override would keep the class from loading; a mixin on the final method
                    // reconnects the old override there (through the hook's adapter, when the wiring has one)
                    rpt.notes.add(tag + "override-hook not synthesized (the new shape is final on the target): "
                        + clsName0(bytes) + "." + h.newName + h.newDesc);
                    top.dext.centurybridge.data.FullAudit.record(h.oldName + h.oldDesc, "override-hook-final");
                    continue;
                }
                reconnectOverrides.add(h);
                rpt.notes.add(tag + "override-hooked: " + h.oldName + h.oldDesc + " -> " + h.newName + h.newDesc);
                top.dext.centurybridge.data.FullAudit.record(h.oldName + h.oldDesc, "override-hooked");
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
        for (String k : ifaceCallRedirects.keySet()) {
            rpt.notes.add(tag + "interface-call-redirected: " + k + " -> " + ifaceCallRedirects.get(k));
        }
        for (String k : fldRedirects.keySet()) {
            rpt.notes.add(tag + "field-redirected: " + k + " -> " + fldRedirects.get(k));
        }
        for (String k : fldGetters.keySet()) {
            rpt.notes.add(tag + "field-gettered: " + k + " -> " + fldGetters.get(k));
        }
        for (String k : fldSetters.keySet()) {
            rpt.notes.add(tag + "field-settered: " + k + " -> " + fldSetters.get(k));
        }
        for (String k : instFldGetters.keySet()) {
            rpt.notes.add(tag + "instance-field-gettered: " + k + " -> " + instFldGetters.get(k));
        }
        for (String k : instFldSetters.keySet()) {
            rpt.notes.add(tag + "instance-field-settered: " + k + " -> " + instFldSetters.get(k));
        }
        for (String k : instOfSites.keySet()) {
            rpt.notes.add(tag + "instanceof-redirected: " + k + " -> " + instOfSites.get(k));
        }
        for (String k : castSites.keySet()) {
            rpt.notes.add(tag + "checkcast-redirected: " + k + " -> " + castSites.get(k));
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
            ? MixinClassRenames.annotations(new org.objectweb.asm.commons.ClassRemapper(kinds,
                new org.objectweb.asm.commons.SimpleRemapper(chain.classRenames)), chain.classRenames, c -> wDeclares(chain, c))
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
                boolean bridged = reconnectOverrides.stream().anyMatch(h -> h.adapter != null)
                    || declaredRenames.stream().anyMatch(r -> r.adapter != null);
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
                    // a renamed override is reached through its adapter by the mixin on the final method
                    for (Chain.OverrideRename r : declaredRenames) {
                        if (r.adapter != null && all.add(r.adapter)) {
                            extra.append('L').append(r.adapter).append(';');
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
                // one new shape reconnected by two hooks (a covariant override declares the
                // old shape twice, once per vanilla supertype that had it -- KilledTrigger's
                // createInstance and SimpleCriterionTrigger's both): the first hook in wiring
                // order synthesizes it, a second copy would be a duplicate method
                Set<String> synthesized = new HashSet<>();
                for (Chain.AdapterHook h : adapterApply) {
                    for (String[] sy : h.synth) {
                        if (declaredShapes.contains(sy[0] + sy[1])) {
                            continue; // the mod already speaks the new shape
                        }
                        if (!synthesized.add(sy[0] + sy[1])) {
                            rpt.notes.add(tag + "adapter-synth skipped (already synthesized by another hook): "
                                + clsName[0] + "." + sy[0] + sy[1] + " via " + h.adapter);
                            continue;
                        }
                        if (h.guard != null) {
                            // guarded: the bridge runs on first entry; calling the method again
                            // from the bridge reaches super's shape (a vanilla implementation)
                            Chain.OverrideHook g = new Chain.OverrideHook();
                            g.oldName = g.newName = sy[0];
                            g.oldDesc = g.newDesc = sy[1];
                            g.bridgeOwner = sy[2];
                            g.bridgeName = sy[3];
                            g.bridgeDesc = sy[4];
                            g.guard = h.guard;
                            synthesizeOverride(this, g, clsName[0], superOf[0]);
                            rpt.notes.add(tag + "adapter-synthesized (guarded): " + clsName[0] + "." + sy[0] + sy[1]);
                            continue;
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
                    synthesizeOverride(this, h, clsName[0], superOf[0],
                        defaultHome(chain, superOf[0], ifacesOf[0], h.newName, h.newDesc));
                }
                for (Chain.AbstractFill f : fillApply) {
                    // public R m(args) { return call(this, args...); }
                    MethodVisitor fv = super.visitMethod(Opcodes.ACC_PUBLIC, f.name, f.desc, null, null);
                    fv.visitCode();
                    fv.visitVarInsn(Opcodes.ALOAD, 0);
                    int fslot = 1;
                    for (org.objectweb.asm.Type t : org.objectweb.asm.Type.getArgumentTypes(f.desc)) {
                        fv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), fslot);
                        fslot += t.getSize();
                    }
                    fv.visitMethodInsn(Opcodes.INVOKESTATIC, f.callOwner, f.callName, f.callDesc, false);
                    fv.visitInsn(org.objectweb.asm.Type.getReturnType(f.desc).getOpcode(Opcodes.IRETURN));
                    fv.visitMaxs(0, 0);
                    fv.visitEnd();
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
                if ((acc & Opcodes.ACC_STATIC) == 0) {
                    for (Chain.OverrideRename r : declaredRenames) {
                        if (r.oldName.equals(mName) && r.oldDesc.equals(mDesc)) {
                            rpt.notes.add(tag + "override-renamed: " + clsName[0] + "." + mName + mDesc + " -> " + r.newName);
                            mName = r.newName;
                            if (r.adapter != null) {
                                // the adapter's (public) interface method is what the mixin calls
                                acc = (acc & ~(Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) | Opcodes.ACC_PUBLIC;
                            }
                            break;
                        }
                    }
                }
                MethodVisitor mv = super.visitMethod(acc, mName, mDesc, sig, ex);
                // javac's bridge for a covariant override (SystemToast subclass: Object getToken() ->
                // SystemToastIds getToken()) forwards the erased shape to the old override. When an
                // override hook gives the class the new shape (same arguments, another return type), the
                // erased shape must reach the new one as vanilla's own bridge does, or vanilla calling
                // through the erased shape gets the old type's value
                Chain.OverrideHook bridgeHook = null;
                if ((acc & Opcodes.ACC_BRIDGE) != 0 && mDesc.endsWith(")Ljava/lang/Object;")) {
                    String args = mDesc.substring(0, mDesc.indexOf(')') + 1);
                    for (Chain.OverrideHook h : reconnectOverrides) {
                        if (h.oldName.equals(mName) && h.newName.equals(mName) && !mDesc.equals(h.oldDesc)
                                && !mDesc.equals(h.newDesc) && h.oldDesc.startsWith(args) && h.newDesc.startsWith(args)
                                && h.newDesc.charAt(args.length()) == 'L') {
                            bridgeHook = h;
                            rpt.notes.add(tag + "bridge-retargeted: " + clsName[0] + "." + mName + mDesc + " -> " + h.newDesc);
                            break;
                        }
                    }
                }
                final Chain.OverrideHook covariantBridge = bridgeHook;
                return new MethodVisitor(Opcodes.ASM9, mv) {
                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        if (covariantBridge != null && op == Opcodes.INVOKEVIRTUAL && owner.equals(clsName[0])
                                && name.equals(covariantBridge.oldName) && desc.equals(covariantBridge.oldDesc)) {
                            desc = covariantBridge.newDesc;
                        }
                        if (op == Opcodes.INVOKESPECIAL && !owner.equals(clsName[0])) {
                            for (Chain.OverrideRename r : declaredRenames) {
                                if (r.superCalls && r.oldName.equals(name) && r.oldDesc.equals(desc)) {
                                    // super.old(..): the method lives on under the new name -- in vanilla, and in a
                                    // mod superclass too, whose own declaration (under the same onlyUnder) was
                                    // renamed with this one. Left on the old name, a mod superclass's call would
                                    // resolve past its renamed body to whatever still answers the old name, which
                                    // calls the new name virtually: back into this class's override, forever.
                                    name = r.newName;
                                    break;
                                }
                            }
                        }
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
                        String icrt = ifaceCallRedirects.get(key);
                        if (icrt != null && op == Opcodes.INVOKEINTERFACE) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(icrt),
                                redirName(icrt, name),
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
                        if (op == Opcodes.CHECKCAST && castSites.containsKey(type)) {
                            // the source release's cast first (throws as it would have), then the renamed type's
                            String cv = castSites.get(type);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(cv),
                                redirName(cv, "cbCheckCast"), "(Ljava/lang/Object;)Ljava/lang/Object;", false);
                        }
                        super.visitTypeInsn(op, type);
                    }

                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        String fkey = owner + "." + name + ":" + desc;
                        if (op == Opcodes.PUTFIELD && instFldSetters.containsKey(fkey)) {
                            String fv = instFldSetters.get(fkey);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(fv), redirName(fv, name),
                                "(L" + emitOwner.getOrDefault(fkey, owner) + ";" + desc + ")V", false);
                            return;
                        }
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
                        if (op == Opcodes.PUTSTATIC && fldSetters.containsKey(fkey)) {
                            String fv = fldSetters.get(fkey);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, redirCls(fv),
                                redirName(fv, name), "(" + desc + ")V", false);
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
                            String icrt = ifaceCallRedirects.get(key);
                            if (icrt != null && h.getTag() == Opcodes.H_INVOKEINTERFACE) {
                                rewritten[i] = new org.objectweb.asm.Handle(Opcodes.H_INVOKESTATIC, redirCls(icrt),
                                    redirName(icrt, h.getName()), "(L" + recv + ";" + h.getDesc().substring(1), false);
                                continue;
                            }
                            String hmrn = mthRenames.get(key);
                            if (hmrn != null) {
                                // a method reference to a renamed member: same owner and kind, the new name
                                rewritten[i] = new org.objectweb.asm.Handle(h.getTag(), h.getOwner(), hmrn, h.getDesc(), h.isInterface());
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
                        Chain.LambdaAdapter la = lambdaAdapterFor(chain, name, desc, bsm, bsmArgs);
                        if (la != null && bsm.getName().equals("metafactory")) {
                            // the lambda is spun as the CB interface carrying the old SAM -- every
                            // bootstrap argument still describes it exactly -- and the adapter turns
                            // it into the vanilla interface the code expects on the stack
                            org.objectweb.asm.Type factory = org.objectweb.asm.Type.getMethodType(desc);
                            String legacyDesc = org.objectweb.asm.Type.getMethodDescriptor(
                                org.objectweb.asm.Type.getObjectType(la.legacy), factory.getArgumentTypes());
                            super.visitInvokeDynamicInsn(name, legacyDesc, bsm, rewritten);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, la.adaptOwner, la.adaptName, la.adaptDesc, false);
                            return;
                        }
                        // a lambda for a SAM the release renamed (Chain.methodRenames) is spun under the new name
                        String lrn = la == null ? lambdaRename(chain, name, desc, bsm, bsmArgs) : null;
                        super.visitInvokeDynamicInsn(lrn != null ? lrn : name, desc, bsm, rewritten);
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
                        // "newId@range": the new provider's own versions are not the old module's (a
                        // Fabric API module CenturyBridge gives back after the release removed it), so
                        // the range the mod asked of the old id would not fit -- the wiring names one
                        String target = e.getValue();
                        int at = target.indexOf('@');
                        if (at > 0) {
                            v = new JsonPrimitive(target.substring(at + 1));
                            target = target.substring(0, at);
                        }
                        // several old ids may collapse onto one provider: keep the wider range
                        if (!dep.has(target)) {
                            dep.add(target, v);
                        }
                        rpt.notes.add("dep " + e.getKey() + " -> " + target);
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
