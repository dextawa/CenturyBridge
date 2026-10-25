package top.dext.centurybridge.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * What a mixin that followed a split class ({@link Chain#mixinSplits}: 26.2 Gui -> Hud) needs besides its new
 * target, once MixinClassRenames.followTargets moved it:
 * <ul>
 * <li>its own code's "(V class)(Object) this" -- {@code ALOAD 0 [CHECKCAST Object] CHECKCAST V} -- is the W
 * instance of V's class name reached from the new target by the split's "self" hops ({@code this.minecraft.gui});
 * the merged code runs inside the target, so the hops' private fields of the target are its own;</li>
 * <li>a cast (or instanceof) anywhere in the jar to an interface that now merges onto the new target (an accessor
 * or duck interface of a moved mixin) first passes the value through an adapter: an instance of V's class name
 * takes the "back" hops ({@code gui.hud}), anything else passes unchanged (instanceof-tested, so a value already
 * of the new class is left alone).</li>
 * </ul>
 * Any other use of V's class inside a moved mixin (a cast of another value, a V-class-typed handler argument) is
 * reported, not changed.
 */
final class MixinSplitAdapt {
    private MixinSplitAdapt() {
    }

    static List<String> run(Map<String, byte[]> entries, Chain chain, List<String> moved, String helper) {
        List<String> notes = new ArrayList<>();
        if (chain.mixinSplits.isEmpty() || moved.isEmpty()) {
            return notes;
        }
        Map<String, String> back = new HashMap<>();
        chain.mixinFollow().forEach((from, to) -> {
            if (chain.mixinSplits.containsKey(from) && chain.mixinSplits.get(from).to().equals(to)) {
                back.put(to, from);
            }
        });
        if (back.isEmpty()) {
            return notes;
        }
        // interface -> V class whose instances take the "back" hops
        Map<String, String> adapt = new HashMap<>();
        for (String cls : moved) {
            byte[] b = entries.get(cls + ".class");
            if (b == null) {
                continue;
            }
            ClassNode cn = new ClassNode();
            new ClassReader(b).accept(cn, 0);
            List<String> ts = MixinRepair.mixinTargets(cn, null);
            if (ts.size() != 1 || !back.containsKey(ts.get(0).replace('.', '/'))) {
                continue;
            }
            String to = ts.get(0).replace('.', '/');
            String from = back.get(to);
            Chain.MixinSplit sp = chain.mixinSplits.get(from);
            if ((cn.access & Opcodes.ACC_INTERFACE) != 0) {
                adapt.put(cn.name, from);
                continue;
            }
            for (String i : cn.interfaces) {
                if (entries.containsKey(i + ".class")) {
                    adapt.put(i, from); // the jar's duck interface the mixin merges onto the target
                }
            }
            int selfs = 0;
            int other = 0;
            for (MethodNode m : cn.methods) {
                for (AbstractInsnNode in : m.instructions.toArray()) {
                    if (!(in instanceof TypeInsnNode t) || t.getOpcode() != Opcodes.CHECKCAST || !t.desc.equals(from)) {
                        continue;
                    }
                    AbstractInsnNode p = prev(in);
                    if (p instanceof TypeInsnNode po && po.getOpcode() == Opcodes.CHECKCAST && po.desc.equals("java/lang/Object")) {
                        p = prev(p);
                    }
                    if (!(p instanceof VarInsnNode v && v.getOpcode() == Opcodes.ALOAD && v.var == 0)
                            || (m.access & Opcodes.ACC_STATIC) != 0 || sp.self().isEmpty()) {
                        other++;
                        continue;
                    }
                    InsnList hops = new InsnList();
                    hops.add(new TypeInsnNode(Opcodes.CHECKCAST, to));
                    for (String[] h : sp.self()) {
                        hops.add(new FieldInsnNode(Opcodes.GETFIELD, h[0], h[1], h[2]));
                    }
                    m.instructions.insertBefore(in, hops);
                    m.instructions.remove(in);
                    selfs++;
                }
            }
            if (selfs > 0) {
                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);
                entries.put(cls + ".class", cw.toByteArray());
                notes.add("mixin split: " + shortName(cls) + " -> " + shortName(to) + ": " + selfs
                    + " use(s) of this as " + shortName(from) + " reach it by " + hopNames(sp.self()));
            }
            if (other > 0) {
                notes.add("mixin split: " + shortName(cls) + " -> " + shortName(to) + ": " + other + " cast(s) to "
                    + shortName(from) + " of another value than this, left as written (ClassCastException where it runs)");
            }
        }
        if (adapt.isEmpty()) {
            return notes;
        }
        // the adapters, one per V class, and every cast / instanceof of the jar to an adapted interface
        Map<String, String> adapterOf = new HashMap<>();
        Set<String> vs = new LinkedHashSet<>(adapt.values());
        int n = 0;
        for (String v : vs) {
            adapterOf.put(v, "to" + n++);
        }
        int casts = 0;
        for (Map.Entry<String, byte[]> e : new ArrayList<>(entries.entrySet())) {
            if (!e.getKey().endsWith(".class")) {
                continue;
            }
            ClassNode cn = new ClassNode();
            try {
                new ClassReader(e.getValue()).accept(cn, 0);
            } catch (RuntimeException ex) {
                continue;
            }
            int here = 0;
            for (MethodNode m : cn.methods) {
                if (m.instructions == null) {
                    continue;
                }
                for (AbstractInsnNode in : m.instructions.toArray()) {
                    if (in instanceof TypeInsnNode t && (t.getOpcode() == Opcodes.CHECKCAST || t.getOpcode() == Opcodes.INSTANCEOF)
                            && adapt.containsKey(t.desc)) {
                        m.instructions.insertBefore(in, new MethodInsnNode(Opcodes.INVOKESTATIC, helper,
                            adapterOf.get(adapt.get(t.desc)), "(Ljava/lang/Object;)Ljava/lang/Object;", false));
                        here++;
                    }
                }
            }
            if (here > 0) {
                ClassWriter cw = new ClassWriter(0); // the adapter takes and leaves one value: stack and frames unchanged
                cn.accept(cw);
                entries.put(e.getKey(), cw.toByteArray());
                casts += here;
            }
        }
        if (casts > 0) {
            entries.put(helper + ".class", helperClass(helper, adapterOf, chain));
            notes.add("mixin split: " + casts + " cast(s) / instanceof to " + adapt.size() + " interface(s) now merged onto the split's class"
                + " pass through " + shortName(helper) + " (" + String.join(", ", adapt.keySet().stream().map(MixinSplitAdapt::shortName).toList()) + ")");
        }
        return notes;
    }

    private static byte[] helperClass(String name, Map<String, String> adapterOf, Chain chain) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
            @Override
            protected String getCommonSuperClass(String a, String b) {
                return "java/lang/Object";
            }
        };
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC, name, null, "java/lang/Object", null);
        for (Map.Entry<String, String> e : adapterOf.entrySet()) {
            Chain.MixinSplit sp = chain.mixinSplits.get(e.getKey());
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, e.getValue(),
                "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label keep = new Label();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitTypeInsn(Opcodes.INSTANCEOF, e.getKey());
            mv.visitJumpInsn(Opcodes.IFEQ, keep);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitTypeInsn(Opcodes.CHECKCAST, e.getKey());
            for (String[] h : sp.back()) {
                mv.visitFieldInsn(Opcodes.GETFIELD, h[0], h[1], h[2]);
            }
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitLabel(keep);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    private static AbstractInsnNode prev(AbstractInsnNode in) {
        AbstractInsnNode p = in.getPrevious();
        while (p != null && p.getOpcode() < 0) {
            p = p.getPrevious();
        }
        return p;
    }

    private static String hopNames(List<String[]> hops) {
        StringBuilder b = new StringBuilder("this");
        for (String[] h : hops) {
            b.append('.').append(h[1]);
        }
        return b.toString();
    }

    private static String shortName(String n) {
        return n.substring(n.lastIndexOf('/') + 1);
    }
}
