package top.dext.centurybridge.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Restored members: the V-named methods a step layer's mixins merge onto game classes (a member W dropped,
 * given back on the W class for mods' calls -- a "cover"). At runtime they are methods of the game class like
 * any other, so a mod's injector selecting V's member lands on the restored one: MixinBridge keeps such a
 * selector, MixinRepair and MixinJudge find the method there.
 *
 * Whether that brings back what the mod's mixin did is another question: on V the mixin changed what vanilla
 * read, and a restored member W's own code never calls changes nothing vanilla does. The layer says where
 * vanilla's W read path goes through the restored member with an annotation on it,
 * {@code @CbVanillaReads("<LayerMixin> <how>")} (any annotation type named CbVanillaReads under
 * top/dext/centurybridge; class retention), naming the layer mixin that routes W's read through it. MixinJudge
 * counts an injector on a restored member as bridged only when the marker is there and names a mixin class of
 * the layer; otherwise the row stays open, "restored-member: vanilla path unmixed". A marker whose route carries
 * only part of the member (vanilla takes its answer, not its body) lists what it carries in {@code carries}
 * ("Inject@HEAD", ...); any other injector on it stays open, "restored-member: vanilla path partial".
 *
 * Merged: concrete, non-synthetic methods of any access (as V declared the member) (no constructors) of a class carrying @Mixin, other than
 * @Shadow / @Overwrite / @Unique / injector handlers / accessors and invokers; and its non-synthetic fields other than
 * @Shadow / @Unique (a restored field: declared on the target at runtime, for mods' @Shadow / @Accessor on it).
 */
public final class LayerMembers {
    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final Set<String> NOT_MERGED = Set.of(
        "Lorg/spongepowered/asm/mixin/Shadow;", "Lorg/spongepowered/asm/mixin/Overwrite;",
        "Lorg/spongepowered/asm/mixin/Unique;", "Lorg/spongepowered/asm/mixin/gen/Accessor;",
        "Lorg/spongepowered/asm/mixin/gen/Invoker;");
    private static final String INJECTION = "Lorg/spongepowered/asm/mixin/injection/";
    private static final String EXTRAS = "Lcom/llamalad7/mixinextras/injector/";

    /** restored members by game class */
    private final Map<String, List<MethodNode>> byTarget = new HashMap<>();
    /** the @Mixin priority of the layer mixin that merges each restored member (Mixin's default 1000) */
    private final Map<MethodNode, Integer> priority = new java.util.IdentityHashMap<>();
    /**
     * restored fields by game class: a layer mixin's own fields (not @Shadow / @Unique) Mixin merges onto its
     * targets under the names the mixin declares -- a field W dropped, given back on the W class (1.21.11's
     * Item.components on 26.1). Each with the priority of the mixin merging it
     */
    private final Map<String, List<FieldNode>> fieldsByTarget = new HashMap<>();
    private final Map<FieldNode, Integer> fieldPriority = new java.util.IdentityHashMap<>();
    /** the layer's mixin classes */
    private final Set<String> mixins = new HashSet<>();

    private LayerMembers() {
    }

    /** an empty set (no layer) */
    public static LayerMembers none() {
        return new LayerMembers();
    }

    public static LayerMembers read(Path layerJar) throws IOException {
        LayerMembers out = new LayerMembers();
        if (layerJar == null) {
            return out;
        }
        try (ZipFile z = new ZipFile(layerJar.toFile())) {
            for (ZipEntry e : java.util.Collections.list(z.entries())) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(z.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES);
                AnnotationNode mx = annotation(cn.invisibleAnnotations, MIXIN);
                if (mx == null) {
                    mx = annotation(cn.visibleAnnotations, MIXIN);
                }
                if (mx == null) {
                    continue;
                }
                out.mixins.add(cn.name);
                if ((cn.access & Opcodes.ACC_INTERFACE) != 0) {
                    out.readInvokers(cn, mx);
                    continue;
                }
                List<String> targets = new ArrayList<>();
                for (Object o : MixinRepair.list(MixinRepair.values(mx).get("value"))) {
                    if (o instanceof Type t) {
                        targets.add(t.getInternalName());
                    }
                }
                for (Object o : MixinRepair.list(MixinRepair.values(mx).get("targets"))) {
                    if (o instanceof String t) {
                        targets.add(t.replace('.', '/'));
                    }
                }
                int prio = MixinRepair.values(mx).get("priority") instanceof Integer p ? p : DEFAULT_PRIORITY;
                for (MethodNode m : cn.methods) {
                    if (merged(m)) {
                        for (String t : targets) {
                            MethodNode a = applied(m, cn.name, t);
                            out.byTarget.computeIfAbsent(t, k -> new ArrayList<>()).add(a);
                            out.priority.put(a, prio);
                        }
                    }
                }
                for (FieldNode f : cn.fields) {
                    if (mergedField(f)) {
                        for (String t : targets) {
                            FieldNode a = new FieldNode(f.access, f.name,
                                new org.objectweb.asm.commons.SimpleRemapper(cn.name, t).mapDesc(f.desc), f.signature, null);
                            out.fieldsByTarget.computeIfAbsent(t, k -> new ArrayList<>()).add(a);
                            out.fieldPriority.put(a, prio);
                        }
                    }
                }
            }
        }
        return out;
    }

    /**
     * the layer's @Invoker methods by the game member they invoke: "owner.name desc" -> {interface, method, desc}. A
     * restored body cannot call another class's restored static directly (restored statics are private), so it calls
     * the layer's invoker of it (smx1 CASE 11: CbAdvBlockPredicateAccess.cb$fromJson for class_4550.method_22453)
     */
    private final Map<String, String[]> invokers = new HashMap<>();

    private void readInvokers(ClassNode cn, AnnotationNode mx) {
        List<String> targets = new ArrayList<>();
        for (Object o : MixinRepair.list(MixinRepair.values(mx).get("value"))) {
            if (o instanceof Type t) {
                targets.add(t.getInternalName());
            }
        }
        for (Object o : MixinRepair.list(MixinRepair.values(mx).get("targets"))) {
            if (o instanceof String t) {
                targets.add(t.replace('.', '/'));
            }
        }
        if (targets.size() != 1) {
            return;
        }
        for (MethodNode m : cn.methods) {
            AnnotationNode inv = annotation(m.invisibleAnnotations, "Lorg/spongepowered/asm/mixin/gen/Invoker;");
            if (inv == null) {
                inv = annotation(m.visibleAnnotations, "Lorg/spongepowered/asm/mixin/gen/Invoker;");
            }
            if (inv == null || !(MixinRepair.values(inv).get("value") instanceof String v) || v.isEmpty()) {
                continue; // a name inflected from the method's own: not read here
            }
            MixinRepair.Sel s = MixinRepair.Sel.parse(v);
            if (s.name() == null || s.name().startsWith("<") || s.desc() != null && !s.desc().equals(m.desc)) {
                continue;
            }
            // a static invoker takes the member's arguments; an instance one is called on the target, its desc the member's
            invokers.put(targets.get(0) + "." + s.name() + m.desc, new String[] {cn.name, m.name, m.desc});
        }
    }

    /** the layer's invoker of a game member ({interface, method, desc}), or null */
    public String[] invokerOf(String owner, String name, String desc) {
        return invokers.get(owner + "." + name + desc);
    }

    /** a field Mixin merges onto the targets: not @Shadow / @Unique (a @Unique one is renamed apart), not synthetic */
    private static boolean mergedField(FieldNode f) {
        if ((f.access & Opcodes.ACC_SYNTHETIC) != 0) {
            return false;
        }
        for (List<AnnotationNode> l : java.util.Arrays.asList(f.visibleAnnotations, f.invisibleAnnotations)) {
            for (AnnotationNode a : l == null ? List.<AnnotationNode>of() : l) {
                if (NOT_MERGED.contains(a.desc)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * The members an earlier step's layer restores, added to this step's: in a climb that layer is in the runtime
     * at W too (climbed onto W like a mod), so its merged fields and methods are declared on W's classes at runtime
     * as much as this layer's -- a field 1.21.6's layer gives back on AbstractTexture is there for a climbed 1.21.4
     * mixin's @Shadow at every later step, not only at 1.21.6. This step's own members come first in every lookup.
     */
    public void inherit(LayerMembers earlier) {
        earlier.byTarget.forEach((t, ms) -> byTarget.computeIfAbsent(t, k -> new ArrayList<>()).addAll(ms));
        priority.putAll(earlier.priority);
        earlier.fieldsByTarget.forEach((t, fs) -> fieldsByTarget.computeIfAbsent(t, k -> new ArrayList<>()).addAll(fs));
        fieldPriority.putAll(earlier.fieldPriority);
        mixins.addAll(earlier.mixins);
    }

    /**
     * a field a layer mixin merges onto a game class (desc null: any descriptor), or null. The caller's mixin
     * resolves a @Shadow / @Accessor on it only when the layer's mixin was merged first: Mixin applies a target's
     * mixins in priority order, and a shadow of a field not yet in the class fails at apply -- so the merging
     * mixin's priority must be below the caller's ({@link #fieldPriority})
     */
    public FieldNode field(String target, String name, String desc) {
        for (FieldNode f : fieldsByTarget.getOrDefault(target, List.of())) {
            if (f.name.equals(name) && (desc == null || f.desc.equals(desc))) {
                return f;
            }
        }
        return null;
    }

    public int fieldPriority(FieldNode f) {
        return fieldPriority.getOrDefault(f, DEFAULT_PRIORITY);
    }

    /**
     * the member as Mixin merges it onto one target: Mixin rewrites the mixin class's own name to the target's at
     * apply (this.x(..), @Shadow field reads, the type in descriptors), so a restored body compiled against the
     * mixin class runs as calls on the game class -- V's anchors. Matching reads that view, not the layer jar's
     */
    static MethodNode applied(MethodNode m, String mixin, String target) {
        MethodNode out = new MethodNode(m.access, m.name, m.desc, m.signature,
            m.exceptions == null ? null : m.exceptions.toArray(new String[0]));
        m.accept(new org.objectweb.asm.commons.MethodRemapper(out,
            new org.objectweb.asm.commons.SimpleRemapper(mixin, target)));
        out.desc = new org.objectweb.asm.commons.SimpleRemapper(mixin, target).mapMethodDesc(m.desc);
        return out;
    }

    private static boolean merged(MethodNode m) {
        // any access: a restored V member keeps V's access (protected / package-private; making it public would break
        // V subclasses overriding it with V's access), and a private one is merged too (Mixin refuses non-private
        // statics: 1.21.11's private static AbstractFurnaceBlockEntity.burn). A selector only meets one under V's name
        if (m.name.startsWith("<")
                || (m.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) != 0) {
            return false;
        }
        for (AnnotationNode a : MixinRepair.annotations(m)) {
            if (NOT_MERGED.contains(a.desc) || a.desc.startsWith(INJECTION) || a.desc.startsWith(EXTRAS)) {
                return false;
            }
        }
        return true;
    }

    /** Mixin's default mixin priority */
    public static final int DEFAULT_PRIORITY = 1000;

    /**
     * whether Mixin lets an injector of a mixin at {@code mixinPriority} apply to a restored member: Mixin injects
     * into a method another mixin merged only when the merging mixin's priority is below the injector's
     * (InjectionPoint.checkPriority: mergedPriority < mixinPriority), except HEAD / RETURN / TAIL, which always may
     * (MethodHead, BeforeReturn, BeforeFinalReturn override it); a constant-only injector has no such point
     */
    public boolean injectable(MethodNode m, int mixinPriority, Map<String, Object> v) {
        if (priority.getOrDefault(m, DEFAULT_PRIORITY) < mixinPriority) {
            return true;
        }
        if (v.get("constant") != null) {
            return false;
        }
        Object at = v.get("at");
        List<Object> ats = at instanceof List<?> l ? new ArrayList<>(l) : at == null ? List.of() : List.of(at);
        if (ats.isEmpty()) {
            return false;
        }
        for (Object o : ats) {
            Object val = o instanceof Map<?, ?> am ? am.get("value") : null;
            if (!"HEAD".equals(val) && !"RETURN".equals(val) && !"TAIL".equals(val)) {
                return false;
            }
        }
        return true;
    }

    public int priority(MethodNode m) {
        return priority.getOrDefault(m, DEFAULT_PRIORITY);
    }

    /** the restored members of a game class a selector names */
    public List<MethodNode> match(String target, MixinRepair.Sel s) {
        List<MethodNode> out = new ArrayList<>();
        for (MethodNode m : byTarget.getOrDefault(target, List.of())) {
            if (m.name.equals(s.name()) && (s.desc() == null || m.desc.equals(s.desc()))) {
                out.add(m);
            }
        }
        return out;
    }

    /** the restored methods of a game class with one name, any descriptor */
    public List<MethodNode> byName(String target, String name) {
        List<MethodNode> out = new ArrayList<>();
        for (MethodNode m : byTarget.getOrDefault(target, List.of())) {
            if (m.name.equals(name)) {
                out.add(m);
            }
        }
        return out;
    }

    /** a restored member of a game class, or null */
    public MethodNode member(String target, String name, String desc) {
        for (MethodNode m : byTarget.getOrDefault(target, List.of())) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return m;
            }
        }
        return null;
    }

    public boolean isMember(MethodNode m) {
        for (List<MethodNode> l : byTarget.values()) {
            if (l.contains(m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * where vanilla's W read path goes through a restored member: its CbVanillaReads marker, when the marker
     * names (first word) a mixin class of the layer by simple or internal name; null otherwise
     */
    public String vanillaReads(MethodNode m) {
        for (AnnotationNode a : MixinRepair.annotations(m)) {
            if (a.desc.startsWith("Ltop/dext/centurybridge/") && a.desc.endsWith("/CbVanillaReads;")) {
                Object v = MixinRepair.values(a).get("value");
                if (!(v instanceof String s) || s.isBlank()) {
                    return null;
                }
                String first = s.trim().split("[\\s:]+", 2)[0];
                for (String mx : mixins) {
                    if (mx.equals(first) || mx.endsWith("/" + first)) {
                        return s;
                    }
                }
                return null;
            }
        }
        return null;
    }

    /**
     * what of a restored member vanilla's W read path carries, when the route carries only part of it: the
     * CbVanillaReads marker's {@code carries} entries, each "<injector annotation simple name>@<@At value>"
     * ("Inject@HEAD", "ModifyReturnValue@RETURN"). null = the marker carries the whole member (no carries
     * element): vanilla runs the restored body itself, so every injector that applies to it changes vanilla.
     */
    public List<String> carries(MethodNode m) {
        for (AnnotationNode a : MixinRepair.annotations(m)) {
            if (a.desc.startsWith("Ltop/dext/centurybridge/") && a.desc.endsWith("/CbVanillaReads;")) {
                Object c = MixinRepair.values(a).get("carries");
                return c == null ? null : MixinRepair.strings(c);
            }
        }
        return null;
    }

    /**
     * whether an injector (its annotation) is one the marker's carries entries name: every @At it has is listed
     * under its annotation's simple name, and it has at least one (a constant-only or slice-bounded injector acts
     * inside the restored body, which a partial route does not run)
     */
    public static boolean carried(List<String> carries, AnnotationNode an, Map<String, Object> v) {
        if (v.get("constant") != null || v.get("slice") != null) {
            return false;
        }
        String kind = an.desc.substring(an.desc.lastIndexOf('/') + 1, an.desc.length() - 1);
        Object at = v.get("at");
        List<Object> ats = at instanceof List<?> l ? new ArrayList<>(l) : at == null ? List.of() : List.of(at);
        if (ats.isEmpty()) {
            return false;
        }
        for (Object o : ats) {
            if (!(o instanceof Map<?, ?> am) || !carries.contains(kind + "@" + am.get("value"))) {
                return false;
            }
        }
        return true;
    }

    private static AnnotationNode annotation(List<AnnotationNode> l, String desc) {
        for (AnnotationNode a : l == null ? List.<AnnotationNode>of() : l) {
            if (a.desc.equals(desc)) {
                return a;
            }
        }
        return null;
    }
}
