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
 * the layer; otherwise the row stays open, "restored-member: vanilla path unmixed".
 *
 * Merged: public, concrete, non-synthetic methods (no constructors) of a class carrying @Mixin, other than
 * @Shadow / @Overwrite / @Unique / injector handlers / accessors and invokers.
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
                for (MethodNode m : cn.methods) {
                    if (merged(m)) {
                        for (String t : targets) {
                            out.byTarget.computeIfAbsent(t, k -> new ArrayList<>()).add(m);
                        }
                    }
                }
            }
        }
        return out;
    }

    private static boolean merged(MethodNode m) {
        if (m.name.startsWith("<") || (m.access & Opcodes.ACC_PUBLIC) == 0
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

    private static AnnotationNode annotation(List<AnnotationNode> l, String desc) {
        for (AnnotationNode a : l == null ? List.<AnnotationNode>of() : l) {
            if (a.desc.equals(desc)) {
                return a;
            }
        }
        return null;
    }
}
