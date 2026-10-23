package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 1.20.1's Advancement as a node of the advancement tree, on 1.20.2.
 *
 * 1.20.1's Advancement carried its id, its parent advancement and its children; 1.20.2 split it into the
 * Advancement record (the data), the AdvancementHolder (id + record) and, once a tree is built, the
 * AdvancementNode (holder + parent and children nodes). A 1.20.1 mod's Advancement is the record. Every
 * 1.20.2 node records its record as it is built (CbAdvNodeTrack), so the record a 1.20.1 mod holds finds
 * the node of the tree it was placed in -- 1.20.1's own parent/children links -- and the holder that
 * names it (CbAdvancements: every holder records its record). Both maps hold the record weakly.
 *
 * Where 1.20.2 hands 1.20.1 code a node or a holder (a tree listener's argument, a tab's root), the
 * code gets the record ({@link #valueOf(net.minecraft.class_8781)}); where 1.20.1 code hands 1.20.2 an
 * advancement, it is turned back ({@link #nodeOf}, {@link #holderOf}).
 */
public final class CbAdvNodes {

    private static final ConcurrentMap<Object, net.minecraft.class_8781> NODES = new MapMaker().weakKeys().makeMap();

    /** called as an AdvancementNode is constructed: the record of its holder is placed at that node */
    public static void track(net.minecraft.class_8781 node) {
        net.minecraft.class_8779 h = node.method_53649();
        if (h != null && h.comp_1920() != null) {
            NODES.put(h.comp_1920(), node);
        }
    }

    // ------------------------------------------------------------------ the conversions

    /** the record of a node handed to 1.20.1 code (null stays null) */
    public static net.minecraft.class_161 valueOf(net.minecraft.class_8781 node) {
        return node == null ? null : node.method_53647();
    }

    /** the record of a holder handed to 1.20.1 code */
    public static net.minecraft.class_161 valueOf(net.minecraft.class_8779 holder) {
        return holder == null ? null : holder.comp_1920();
    }

    /**
     * the tree node of a 1.20.1 advancement: the node it was last placed at (a loaded advancement is
     * placed in the server's and, sent to a client, in the client's tree); one never placed (built by the
     * mod and not loaded) is a node of its own -- its holder, no parent, no children, as a 1.20.1
     * advancement built without a parent had
     */
    public static net.minecraft.class_8781 nodeOf(net.minecraft.class_161 advancement) {
        if (advancement == null) {
            return null;
        }
        net.minecraft.class_8781 node = NODES.get(advancement);
        return node != null ? node : new net.minecraft.class_8781(holderOf(advancement), null);
    }

    /** the holder of a 1.20.1 advancement (CbAdvancements: every holder records its record) */
    public static net.minecraft.class_8779 holderOf(net.minecraft.class_161 advancement) {
        if (advancement == null) {
            return null;
        }
        net.minecraft.class_8781 node = NODES.get(advancement);
        return node != null ? node.method_53649() : CbAdvancements.holderOf(advancement);
    }

    // ------------------------------------------------------------------ Advancement's 1.20.1 members

    /*
     * Each is a call (instanceRedirects): a 1.20.1 subclass's own override answers first (its class has the
     * adapter interface: adapterHooks), as 1.20.1's virtual call did; the super_ variant is that subclass's
     * own super call (superRedirects) and 1.20.1's body on the 1.20.2 record. A subclass overriding one of
     * the record-backed getters (rewards, criteria, requirements, telemetry, chat name) also answers 1.20.2's
     * accessor (synthesized: vanilla reads what the subclass answers, as 1.20.1's vanilla called the getter);
     * its super call reads the record's stored component.
     */

    /** the node's own child set, read past a 1.20.1 subclass's getChildren override (CbAdvNodeChildren) */
    public interface NodeChildren {
        java.util.Set<net.minecraft.class_8781> cb$children();
    }

    private static top.dext.centurybridge.shims.v1_20_2.CbAdvRecordFields fields(net.minecraft.class_161 self) {
        return (top.dext.centurybridge.shims.v1_20_2.CbAdvRecordFields) (Object) self;
    }

    /** getParent() */
    public static net.minecraft.class_161 method_687(net.minecraft.class_161 self) {
        return (Object) self instanceof CbAdvLegacy.AdvParent own ? own.method_687() : super_method_687(self);
    }

    /** 1.20.1's getParent(): the parent of the node the advancement is placed at (null for a root or an unplaced one) */
    public static net.minecraft.class_161 super_method_687(net.minecraft.class_161 self) {
        net.minecraft.class_8781 node = NODES.get(self);
        return node == null ? null : valueOf(node.method_53651());
    }

    /** getRoot() */
    public static net.minecraft.class_161 method_48024(net.minecraft.class_161 self) {
        return (Object) self instanceof CbAdvLegacy.AdvRoot own ? own.method_48024() : method_48023(self);
    }

    /** 1.20.1's getRoot(): getRoot(this) */
    public static net.minecraft.class_161 super_method_48024(net.minecraft.class_161 self) {
        return method_48023(self);
    }

    /** getRoot(advancement) (static), as 1.20.1 walked it: getParent() until there is none */
    public static net.minecraft.class_161 method_48023(net.minecraft.class_161 advancement) {
        net.minecraft.class_161 a = advancement;
        while (true) {
            net.minecraft.class_161 parent = method_687(a);
            if (parent == null) {
                return a;
            }
            a = parent;
        }
    }

    /** getChildren() */
    public static Iterable<net.minecraft.class_161> method_681(net.minecraft.class_161 self) {
        return (Object) self instanceof CbAdvLegacy.AdvChildren own ? own.method_681() : super_method_681(self);
    }

    /** 1.20.1's getChildren(): the children of its node as the tree placed them */
    public static Iterable<net.minecraft.class_161> super_method_681(net.minecraft.class_161 self) {
        net.minecraft.class_8781 node = NODES.get(self);
        List<net.minecraft.class_161> out = new ArrayList<>();
        if (node != null) {
            for (net.minecraft.class_8781 child : ((NodeChildren) (Object) node).cb$children()) {
                out.add(child.method_53647());
            }
        }
        return out;
    }

    /** addChild(child) */
    public static void method_690(net.minecraft.class_161 self, net.minecraft.class_161 child) {
        if ((Object) self instanceof CbAdvLegacy.AdvAddChild own) {
            own.method_690(child);
        } else {
            super_method_690(self, child);
        }
    }

    /** 1.20.1's addChild(child): the child linked under this advancement's node */
    public static void super_method_690(net.minecraft.class_161 self, net.minecraft.class_161 child) {
        nodeOf(self).method_53650(nodeOf(child));
    }

    /** sendsTelemetryEvent() (a subclass's override answers through the synthesized accessor) */
    public static boolean method_51697(net.minecraft.class_161 self) {
        return self.comp_1917();
    }

    /** super.sendsTelemetryEvent() */
    public static boolean super_method_51697(net.minecraft.class_161 self) {
        return fields(self).cb$telemetry();
    }

    /** getRewards() */
    public static net.minecraft.class_170 method_691(net.minecraft.class_161 self) {
        return self.comp_1914();
    }

    /** super.getRewards() */
    public static net.minecraft.class_170 super_method_691(net.minecraft.class_161 self) {
        return fields(self).cb$rewards();
    }

    /** getCriteria(): name -> criterion */
    public static Map<String, net.minecraft.class_175<?>> method_682(net.minecraft.class_161 self) {
        return self.comp_1915();
    }

    /** super.getCriteria() */
    public static Map<String, net.minecraft.class_175<?>> super_method_682(net.minecraft.class_161 self) {
        return fields(self).cb$criteria();
    }

    /** getRequirements(): the requirement groups */
    public static String[][] method_680(net.minecraft.class_161 self) {
        return self.comp_1916().comp_1922();
    }

    /** super.getRequirements() */
    public static String[][] super_method_680(net.minecraft.class_161 self) {
        return fields(self).cb$requirements().comp_1922();
    }

    /** getMaxCriteraRequired() */
    public static int method_683(net.minecraft.class_161 self) {
        return (Object) self instanceof CbAdvLegacy.AdvMaxCriteria own ? own.method_683() : super_method_683(self);
    }

    /** 1.20.1's getMaxCriteraRequired(): requirements.length */
    public static int super_method_683(net.minecraft.class_161 self) {
        return method_680(self).length;
    }

    /** getChatComponent() (a subclass's override answers through the synthesized name accessor) */
    public static net.minecraft.class_2561 method_684(net.minecraft.class_161 self) {
        return (Object) self instanceof CbAdvLegacy.AdvChat own ? own.method_684() : super_method_684(self);
    }

    /**
     * 1.20.1's getChatComponent(): built from the display (title in the frame's colour, hover with the
     * description) or, without a display, the id as literal text; 1.20.2's record keeps the first, the
     * holder's name answers the second (Advancement.name(holder))
     */
    public static net.minecraft.class_2561 super_method_684(net.minecraft.class_161 self) {
        return fields(self).cb$name().orElseGet(() -> net.minecraft.class_2561.method_43470(holderOf(self).comp_1919().toString()));
    }

    // ---- the record accessors a 1.20.1 subclass's override answers (adapterHooks synthesize them)

    public static boolean legacyTelemetry(CbAdvLegacy.AdvTelemetry own) {
        return own.method_51697();
    }

    public static net.minecraft.class_170 legacyRewards(CbAdvLegacy.AdvRewards own) {
        return own.method_691();
    }

    public static Map<String, net.minecraft.class_175<?>> legacyCriteria(CbAdvLegacy.AdvCriteria own) {
        return own.method_682();
    }

    public static net.minecraft.class_8782 legacyRequirements(CbAdvLegacy.AdvRequirements own) {
        return new net.minecraft.class_8782(own.method_680());
    }

    public static java.util.Optional<net.minecraft.class_2561> legacyName(CbAdvLegacy.AdvChat own) {
        return java.util.Optional.ofNullable(own.method_684());
    }

    /**
     * AdvancementVisibilityEvaluator.evaluateVisibility(advancement, isDone, output): 1.20.1's walked the
     * advancement's tree testing Predicate&lt;Advancement&gt; and reporting each to output.accept(advancement,
     * visible); 1.20.2's walks the node's tree with Predicate&lt;AdvancementNode&gt; and Output.accept(node, visible).
     * The 1.20.1 predicate and output see the nodes' advancements.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_48031(net.minecraft.class_161 advancement, java.util.function.Predicate isDone,
            net.minecraft.class_8005.class_8006 output) {
        java.util.function.Predicate<net.minecraft.class_161> done = isDone;
        net.minecraft.class_8005.method_48031(nodeOf(advancement), n -> done.test(n.method_53647()), output);
    }

    /** a 1.20.1 visibility output lambda (accept(advancement, visible)) as 1.20.2's (accept(node, visible)) */
    public static net.minecraft.class_8005.class_8006 visibilityOutput(CbAdvLegacy.VisibilityOutput legacy) {
        return (node, visible) -> legacy.accept(node.method_53647(), visible);
    }

    /** a collection of 1.20.1 advancements (a 1.20.1 override's answer) as their nodes */
    public static java.util.Collection<net.minecraft.class_8781> nodes(java.util.Collection<net.minecraft.class_161> advancements) {
        List<net.minecraft.class_8781> out = new ArrayList<>(advancements.size());
        for (net.minecraft.class_161 a : advancements) {
            out.add(nodeOf(a));
        }
        return out;
    }

    private CbAdvNodes() {
    }
}
