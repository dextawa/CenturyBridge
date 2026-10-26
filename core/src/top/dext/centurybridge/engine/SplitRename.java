package top.dext.centurybridge.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Split-aware rename (smx4 (m)): V's interface X, which W turned into a final record (1.21.4 MetadataSectionType,
 * ParticleRenderType), keeps two spellings in a converted mod. The mod's own world -- its implements, fields,
 * method descriptors, locals, casts -- moves to CenturyBridge's facade F (V's interface, given back by the layer);
 * every reference to a game member keeps W's spelling X, and values cross at that boundary through the wiring's
 * converters (toW (F)X before an X goes into the game, toV (X)F after one comes out). A mod method overriding a
 * game method keeps the game descriptor: its X arguments become F on entry and an F answer X at each return, so it
 * still overrides; calls of it are game-spelled too. Wiring "splitRenames": {X: {"facade": F, "toW":
 * "OWNER#name(LF;)LX;", "toV": "OWNER#name(LX;)LF;", "basis"}}; the converters must undo each other (a record made
 * from a mod object maps back to that object). Not carried, the class left as it is and reported: mixin classes,
 * and a class whose invokedynamic / method handle / class literal names X.
 */
final class SplitRename {
    record Split(String x, String facade, String[] toW, String[] toV) {}

    /** whether a member reference keeps W's spelling: it resolves to a game member or to a mod override of one */
    interface Resolver {
        boolean gameSpelled(String owner, String name, String desc, boolean field);

        boolean overridesGame(ClassNode c, MethodNode m);
    }

    private SplitRename() {
    }

    /** the class rewritten, or null when it names no split class or is not carried (then a note says why) */
    static byte[] apply(byte[] bytes, Map<String, Split> splits, Resolver res, List<String> notes) {
        Set<String> named = new java.util.LinkedHashSet<>();
        String text = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        for (String x : splits.keySet()) {
            if (text.contains(x)) {
                named.add(x);
            }
        }
        if (named.isEmpty()) {
            return null;
        }
        ClassNode cn = new ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        for (Split sp : splits.values()) {
            if (cn.name.equals(sp.facade()) || cn.name.equals(sp.toW()[0]) || cn.name.equals(sp.toV()[0])) {
                return null; // the facade and the converters speak both spellings on purpose
            }
        }
        if (isMixin(cn)) {
            notes.add("split rename: " + cn.name + " is a mixin naming " + named + ": not carried");
            return null;
        }
        String blocker = blocker(cn, named);
        if (blocker != null) {
            notes.add("split rename: " + cn.name + " not carried (" + blocker + ")");
            return null;
        }
        Set<String> keep = new java.util.HashSet<>();
        int conv = 0;
        for (MethodNode m : cn.methods) {
            if (mentions(m.desc, named) && res.overridesGame(cn, m)) {
                keep.add(m.name + m.desc);
                conv += entryExit(m, splits, named);
            }
        }
        for (MethodNode m : cn.methods) {
            if (m.instructions != null) {
                conv += boundaries(m, splits, named, res);
            }
        }
        Set<String> convs = converters(splits);
        Map<String, String> map = new java.util.HashMap<>();
        for (String x : named) {
            map.put(x, splits.get(x).facade());
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassRemapper(cw, new SimpleRemapper(map)) {
            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                if (keep.contains(name + desc)) {
                    // the game's descriptor, so it still overrides; the body speaks F (entryExit converted)
                    MethodVisitor mv = cv.visitMethod(acc, name, desc, sig, ex);
                    return mv == null ? null : createMethodRemapper(mv);
                }
                return super.visitMethod(acc, name, desc, sig, ex);
            }

            @Override
            protected MethodVisitor createMethodRemapper(MethodVisitor mv) {
                return new MethodRemapper(api, mv, remapper) {
                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        if (convs.contains(owner + "." + name + desc) || res.gameSpelled(owner, name, desc, false)) {
                            this.mv.visitMethodInsn(op, owner, name, desc, itf); // the game's member, W's spelling
                            return;
                        }
                        super.visitMethodInsn(op, owner, name, desc, itf);
                    }

                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        if (res.gameSpelled(owner, name, desc, true)) {
                            this.mv.visitFieldInsn(op, owner, name, desc);
                            return;
                        }
                        super.visitFieldInsn(op, owner, name, desc);
                    }
                };
            }
        };
        cn.accept(cv);
        notes.add("split rename: " + cn.name + ": " + named + " -> the facade in its own declarations, " + conv
            + " value(s) converted where they cross a game member" + (keep.isEmpty() ? "" : ", " + keep.size()
            + " override(s) keep the game descriptor"));
        return cw.toByteArray();
    }

    private static boolean isMixin(ClassNode cn) {
        for (List<AnnotationNode> l : java.util.Arrays.asList(cn.visibleAnnotations, cn.invisibleAnnotations)) {
            for (AnnotationNode a : l == null ? List.<AnnotationNode>of() : l) {
                if (a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    return true;
                }
            }
        }
        return false;
    }

    /** a use the boundary rule does not cover: a handle, an invokedynamic or a class literal naming a split class */
    private static String blocker(ClassNode cn, Set<String> named) {
        for (MethodNode m : cn.methods) {
            if (m.instructions == null) {
                continue;
            }
            for (AbstractInsnNode in : m.instructions) {
                if (in instanceof InvokeDynamicInsnNode id) {
                    if (mentions(id.desc, named)) {
                        return "invokedynamic " + id.name + id.desc + " in " + m.name;
                    }
                    for (Object a : id.bsmArgs) {
                        if (a instanceof Handle h && (mentions(h.getDesc(), named) || named.contains(h.getOwner()))
                                || a instanceof Type t && mentions(t.getDescriptor(), named)) {
                            return "method handle " + a + " in " + m.name;
                        }
                    }
                } else if (in instanceof LdcInsnNode l && (l.cst instanceof Type t && mentions(t.getDescriptor(), named)
                        || l.cst instanceof Handle)) {
                    return "constant " + l.cst + " in " + m.name;
                }
            }
        }
        return null;
    }

    static boolean mentions(String desc, Set<String> named) {
        for (String x : named) {
            if (desc.contains("L" + x + ";")) {
                return true;
            }
        }
        return false;
    }

    private static boolean split(Type t, Set<String> named) {
        return t.getSort() == Type.OBJECT && named.contains(t.getInternalName());
    }

    private static MethodInsnNode call(String[] ref) {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, ref[0], ref[1], ref[2], false);
    }

    /** an override keeping the game descriptor: X arguments to F on entry, an F answer to X at each return */
    private static int entryExit(MethodNode m, Map<String, Split> splits, Set<String> named) {
        if (m.instructions == null || m.instructions.size() == 0) {
            return 0;
        }
        int n = 0;
        InsnList pro = new InsnList();
        int slot = (m.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        for (Type t : Type.getArgumentTypes(m.desc)) {
            if (split(t, named)) {
                pro.add(new VarInsnNode(Opcodes.ALOAD, slot));
                pro.add(call(splits.get(t.getInternalName()).toV()));
                pro.add(new VarInsnNode(Opcodes.ASTORE, slot));
                n++;
            }
            slot += t.getSize();
        }
        m.instructions.insert(pro);
        Type r = Type.getReturnType(m.desc);
        if (split(r, named)) {
            for (AbstractInsnNode in : m.instructions.toArray()) {
                if (in.getOpcode() == Opcodes.ARETURN) {
                    m.instructions.insertBefore(in, call(splits.get(r.getInternalName()).toW()));
                    n++;
                }
            }
        }
        return n;
    }

    /** every game-spelled member reference naming a split class: F -> X going in, X -> F coming out */
    private static int boundaries(MethodNode m, Map<String, Split> splits, Set<String> named, Resolver res) {
        int n = 0;
        Set<String> convs = converters(splits);
        for (AbstractInsnNode in : m.instructions.toArray()) {
            if (in instanceof MethodInsnNode c && convs.contains(c.owner + "." + c.name + c.desc)) {
                continue; // a converter entryExit put in
            }
            if (in instanceof MethodInsnNode mi && (mentions(mi.desc, named) || named.contains(mi.owner))
                    && res.gameSpelled(mi.owner, mi.name, mi.desc, false)) {
                boolean st = mi.getOpcode() == Opcodes.INVOKESTATIC;
                List<Type> ins = new ArrayList<>();
                if (!st && !mi.name.equals("<init>")) {
                    ins.add(Type.getObjectType(mi.owner));
                } else if (!st) {
                    ins.add(Type.getObjectType("java/lang/Object")); // the uninitialized receiver: never split
                }
                ins.addAll(List.of(Type.getArgumentTypes(mi.desc)));
                if (ins.stream().anyMatch(t -> split(t, named))) {
                    if (mi.name.equals("<init>")) {
                        continue; // a game constructor taking X: not carried here (the uninitialized receiver cannot be stored)
                    }
                    // the operands into fresh locals, the split ones converted on the way back
                    int[] slots = new int[ins.size()];
                    int s = m.maxLocals;
                    for (int i = 0; i < ins.size(); i++) {
                        slots[i] = s;
                        s += ins.get(i).getSize();
                    }
                    InsnList pre = new InsnList();
                    for (int i = ins.size() - 1; i >= 0; i--) {
                        pre.add(new VarInsnNode(ins.get(i).getOpcode(Opcodes.ISTORE), slots[i]));
                    }
                    for (int i = 0; i < ins.size(); i++) {
                        pre.add(new VarInsnNode(ins.get(i).getOpcode(Opcodes.ILOAD), slots[i]));
                        if (split(ins.get(i), named)) {
                            pre.add(call(splits.get(ins.get(i).getInternalName()).toW()));
                            n++;
                        }
                    }
                    m.instructions.insertBefore(mi, pre);
                    m.maxLocals = s;
                }
                Type r = Type.getReturnType(mi.desc);
                if (split(r, named)) {
                    m.instructions.insert(mi, call(splits.get(r.getInternalName()).toV()));
                    n++;
                }
            } else if (in instanceof FieldInsnNode fi && res.gameSpelled(fi.owner, fi.name, fi.desc, true)) {
                if (named.contains(fi.owner) && fi.getOpcode() == Opcodes.GETFIELD) {
                    m.instructions.insertBefore(fi, call(splits.get(fi.owner).toW()));
                    n++;
                }
                Type t = Type.getType(fi.desc);
                if (!split(t, named)) {
                    continue;
                }
                String[] conv = fi.getOpcode() == Opcodes.GETSTATIC || fi.getOpcode() == Opcodes.GETFIELD
                    ? splits.get(t.getInternalName()).toV() : splits.get(t.getInternalName()).toW();
                if (fi.getOpcode() == Opcodes.GETSTATIC || fi.getOpcode() == Opcodes.GETFIELD) {
                    m.instructions.insert(fi, call(conv));
                } else {
                    m.instructions.insertBefore(fi, call(conv));
                }
                n++;
            }
        }
        return n;
    }

    /** the converters' keys "owner.name(desc)": calls of them are spelled as the wiring writes them */
    private static Set<String> converters(Map<String, Split> splits) {
        Set<String> convs = new java.util.HashSet<>();
        for (Split sp : splits.values()) {
            convs.add(sp.toW()[0] + "." + sp.toW()[1] + sp.toW()[2]);
            convs.add(sp.toV()[0] + "." + sp.toV()[1] + sp.toV()[2]);
        }
        return convs;
    }

    /** the jar class's own field of that name and desc (a jar field is the mod's, F-spelled) */
    static boolean declaresField(ClassNode c, String name, String desc) {
        for (FieldNode f : c.fields) {
            if (f.name.equals(name) && f.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }
}
