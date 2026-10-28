package top.dext.centurybridge.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * K3: a mixin triage would strip whole for members W no longer declares, taken apart member by member instead.
 *
 * Mixin rejects a mixin whole when one of its @Shadow / @Accessor / @Invoker members does not resolve on the target,
 * and triage strips it before that: every working injector and interface merge of the mixin goes with the one gone
 * member, and an accessor mod code calls kills every caller. Here each such member is judged alone, by triage's own
 * rules (MixinTriage.reanalyze on a copy holding only that member: the target's declarations, the step layer's and
 * the jar's own restored members, priority order). The gone ones are taken out:
 *   a class mixin's @Shadow    the methods of the mixin that name it get a body throwing what is missing (they would
 *                              link to nothing); an injector whose handler reaches one of them is disabled and
 *                              reported (it would throw every time W runs the injection point)
 *   an accessor / invoker      removed from the interface; every call of it in the jar goes to a generated static that
 *                              throws what is missing (a loud refusal at the call, not a dead interface merge)
 * The peeled copy is judged again whole: kept only when that leaves it applying; otherwise the mixin is stripped as
 * before (it has other damage), unchanged.
 */
final class MixinPeel {
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";

    /** what peeling one mixin did: the report note and the injectors it disabled */
    record Result(String note, List<String> disabled) {}

    private final Map<String, byte[]> entries;
    private final Chain chain;
    private final boolean injectorsHandled;
    private final String stubName;
    private final Map<String, String> cfgs = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> refmaps = new HashMap<>();
    private final LayerMembers own;
    /** "owner.name desc" of a removed accessor / invoker -> {stub method name, stub desc, message} */
    private final Map<String, String[]> refused = new LinkedHashMap<>();

    MixinPeel(Map<String, byte[]> entries, Chain chain, boolean injectorsHandled, String stubName) {
        this.entries = entries;
        this.chain = chain;
        this.injectorsHandled = injectorsHandled;
        this.stubName = stubName;
        MixinTriage.configs(entries, cfgs, refmaps);
        this.own = MixinTriage.ownMembers(entries);
    }

    private boolean fatal(ClassNode cn) {
        return MixinTriage.reanalyze(write(cn), cn.name, cfgs, refmaps, chain, own).applyFatal(injectorsHandled);
    }

    /** the peeled mixin's report, or null when nothing of it is a gone member alone (strip it as before) */
    Result peel(String cls) {
        byte[] b = entries.get(cls + ".class");
        if (b == null) return null;
        ClassNode cn = read(b);
        List<FieldNode> goneF = new ArrayList<>();
        List<MethodNode> goneM = new ArrayList<>();
        for (FieldNode f : cn.fields) {
            if (has(f.visibleAnnotations, SHADOW) || has(f.invisibleAnnotations, SHADOW)) {
                ClassNode one = shell(cn);
                one.fields.add(f);
                if (fatal(one)) goneF.add(f);
            }
        }
        for (MethodNode m : cn.methods) {
            if (memberAnnotated(m)) {
                ClassNode one = shell(cn);
                one.methods.add(m);
                if (fatal(one)) goneM.add(m);
            }
        }
        if (goneF.isEmpty() && goneM.isEmpty()) return null;
        boolean itf = (cn.access & Opcodes.ACC_INTERFACE) != 0;
        String target = MixinRepair.mixinTargets(cn, null).stream().findFirst().orElse("?");
        String w = chain.to;
        Set<String> goneKeys = new LinkedHashSet<>();
        for (FieldNode f : goneF) goneKeys.add(f.name + ":" + f.desc);
        for (MethodNode m : goneM) goneKeys.add(m.name + m.desc);
        cn.fields.removeAll(goneF);
        cn.methods.removeAll(goneM);
        List<String> disabled = new ArrayList<>();
        Map<String, String[]> refusedHere = new LinkedHashMap<>();
        int thrown = 0;
        String what = String.join(", ", goneKeys);
        String sn = cn.name.substring(cn.name.lastIndexOf('/') + 1);
        if (!itf) {
            // the methods naming a gone member: a body that throws what is missing
            Set<MethodNode> direct = new LinkedHashSet<>();
            for (MethodNode m : cn.methods) {
                if (m.instructions == null) continue;
                for (AbstractInsnNode in : m.instructions) {
                    if (in instanceof FieldInsnNode fi && fi.owner.equals(cn.name) && goneKeys.contains(fi.name + ":" + fi.desc)
                            || in instanceof MethodInsnNode mi && mi.owner.equals(cn.name) && goneKeys.contains(mi.name + mi.desc)) {
                        direct.add(m);
                        break;
                    }
                }
            }
            // the methods that run one of them (calls, transitively): an injector among them is disabled
            Set<String> reach = new HashSet<>();
            for (MethodNode m : direct) reach.add(m.name + m.desc);
            boolean grew = true;
            while (grew) {
                grew = false;
                for (MethodNode m : cn.methods) {
                    if (m.instructions == null || reach.contains(m.name + m.desc)) continue;
                    for (AbstractInsnNode in : m.instructions) {
                        if (in instanceof MethodInsnNode mi && mi.owner.equals(cn.name) && reach.contains(mi.name + mi.desc)) {
                            reach.add(m.name + m.desc);
                            grew = true;
                            break;
                        }
                    }
                }
            }
            for (MethodNode m : cn.methods) {
                if (!reach.contains(m.name + m.desc)) continue;
                AnnotationNode inj = injector(m);
                if (inj != null) {
                    disabled.add("@" + inj.desc.substring(inj.desc.lastIndexOf('/') + 1, inj.desc.length() - 1) + " " + sn + "."
                        + m.name + " -> " + target.substring(target.lastIndexOf('/') + 1) + ": its handler needs " + what
                        + ", which " + target.substring(target.lastIndexOf('/') + 1) + " no longer declares in " + w
                        + " (the mixin kept, member by member)");
                    disarm(m);
                }
                if (direct.contains(m)) {
                    throwBody(m, "CenturyBridge: " + cn.name.replace('/', '.') + "." + m.name + " needs " + what + ", which "
                        + target.replace('/', '.') + " no longer declares in " + w + " (no bridge)");
                    thrown++;
                }
            }
        } else {
            for (MethodNode m : goneM) {
                boolean st = (m.access & Opcodes.ACC_STATIC) != 0;
                String desc = st ? m.desc : "(L" + cn.name + ";" + m.desc.substring(1);
                refusedHere.put(cn.name + "." + m.name + " " + m.desc, new String[] {"r" + (refused.size() + refusedHere.size()), desc,
                    "CenturyBridge: " + cn.name.replace('/', '.') + "." + m.name + " (accessor / invoker) needs a member "
                        + target.replace('/', '.') + " no longer declares in " + w + " (no bridge)"});
            }
        }
        if (fatal(cn)) return null; // other damage: stripped whole, as before
        entries.put(cls + ".class", write(cn));
        refused.putAll(refusedHere);
        String note = "mixin peeled: " + sn + " -- " + what + " not declared on " + target.substring(target.lastIndexOf('/') + 1)
            + " in " + w + ": removed, the rest of the mixin kept (" + disabled.size() + " injector(s) disabled, "
            + (itf ? refusedHere.size() + " accessor / invoker call(s) refused at the call" : thrown + " method(s) throw what is missing") + ")";
        return new Result(note, disabled);
    }

    /** every call of a removed accessor / invoker in the jar goes to the refusing static; the static's class added */
    List<String> finish() {
        List<String> notes = new ArrayList<>();
        if (refused.isEmpty()) return notes;
        int sites = 0;
        for (Map.Entry<String, byte[]> e : new ArrayList<>(entries.entrySet())) {
            if (!e.getKey().endsWith(".class")) continue;
            ClassNode cn = read(e.getValue());
            boolean changed = false;
            for (MethodNode m : cn.methods) {
                if (m.instructions == null) continue;
                for (AbstractInsnNode in : m.instructions.toArray()) {
                    if (!(in instanceof MethodInsnNode mi)) continue;
                    String[] r = refused.get(mi.owner + "." + mi.name + " " + mi.desc);
                    if (r == null) continue;
                    m.instructions.set(mi, new MethodInsnNode(Opcodes.INVOKESTATIC, stubName, r[0], r[1], false));
                    changed = true;
                    sites++;
                }
            }
            if (changed) entries.put(e.getKey(), write(cn));
        }
        ClassNode st = new ClassNode();
        st.version = Opcodes.V17;
        st.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC;
        st.name = stubName;
        st.superName = "java/lang/Object";
        for (String[] r : refused.values()) {
            MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, r[0], r[1], null, null);
            throwBody(m, r[2]);
            st.methods.add(m);
        }
        entries.put(stubName + ".class", write(st));
        notes.add("mixin peeled: " + refused.size() + " removed accessor / invoker method(s), " + sites + " call site(s) refused through "
            + stubName.substring(stubName.lastIndexOf('/') + 1));
        return notes;
    }

    // ------------------------------------------------------------------ helpers

    private static boolean memberAnnotated(MethodNode m) {
        for (String d : new String[] {SHADOW, ACCESSOR, INVOKER}) {
            if (has(m.visibleAnnotations, d) || has(m.invisibleAnnotations, d)) return true;
        }
        return false;
    }

    /** the class with no fields or methods (annotations, interfaces, super kept): one member is added to judge it alone */
    private static ClassNode shell(ClassNode cn) {
        ClassNode c = new ClassNode();
        c.version = cn.version;
        c.access = cn.access;
        c.name = cn.name;
        c.superName = cn.superName;
        c.interfaces = cn.interfaces;
        c.visibleAnnotations = cn.visibleAnnotations;
        c.invisibleAnnotations = cn.invisibleAnnotations;
        return c;
    }

    private static boolean has(List<AnnotationNode> l, String desc) {
        return l != null && l.stream().anyMatch(a -> a.desc.equals(desc));
    }

    private static boolean injectorDesc(String d) {
        return d.startsWith("Lorg/spongepowered/asm/mixin/injection/") && !d.endsWith("/At;") && !d.endsWith("/Slice;")
            && !d.endsWith("/Desc;") && !d.endsWith("/Group;") && !d.contains("/callback/")
            || d.startsWith("Lcom/llamalad7/mixinextras/injector/");
    }

    private static AnnotationNode injector(MethodNode m) {
        for (List<AnnotationNode> l : java.util.Arrays.asList(m.visibleAnnotations, m.invisibleAnnotations)) {
            if (l == null) continue;
            for (AnnotationNode a : l) if (injectorDesc(a.desc)) return a;
        }
        return null;
    }

    /** the handler a plain method: its injector annotations, MixinExtras expressions and sugar parameter marks go */
    private static void disarm(MethodNode m) {
        for (List<AnnotationNode> l : java.util.Arrays.asList(m.visibleAnnotations, m.invisibleAnnotations)) {
            if (l != null) l.removeIf(a -> injectorDesc(a.desc) || a.desc.startsWith("Lcom/llamalad7/mixinextras/expression/"));
        }
        for (List<AnnotationNode>[] pa : java.util.Arrays.<List<AnnotationNode>[]>asList(m.visibleParameterAnnotations, m.invisibleParameterAnnotations)) {
            if (pa == null) continue;
            for (List<AnnotationNode> l : pa) {
                if (l != null) l.removeIf(a -> a.desc.startsWith("Lcom/llamalad7/mixinextras/sugar/"));
            }
        }
    }

    private static void throwBody(MethodNode m, String msg) {
        m.access &= ~(Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE);
        m.instructions.clear();
        m.tryCatchBlocks = new ArrayList<>();
        m.localVariables = null;
        m.visibleLocalVariableAnnotations = null;
        m.invisibleLocalVariableAnnotations = null;
        m.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/IllegalStateException"));
        m.instructions.add(new InsnNode(Opcodes.DUP));
        m.instructions.add(new LdcInsnNode(msg));
        m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V", false));
        m.instructions.add(new InsnNode(Opcodes.ATHROW));
        m.maxStack = 3;
        m.maxLocals = (Type.getArgumentsAndReturnSizes(m.desc) >> 2) - ((m.access & Opcodes.ACC_STATIC) != 0 ? 1 : 0);
    }

    private static ClassNode read(byte[] b) {
        ClassNode cn = new ClassNode();
        new ClassReader(b).accept(cn, 0);
        return cn;
    }

    private static byte[] write(ClassNode cn) {
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
