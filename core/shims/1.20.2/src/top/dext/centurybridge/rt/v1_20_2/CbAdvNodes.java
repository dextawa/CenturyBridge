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

    /** getParent(): the parent of the node the advancement is placed at (null for a root or an unplaced one) */
    public static net.minecraft.class_161 method_687(net.minecraft.class_161 self) {
        net.minecraft.class_8781 node = NODES.get(self);
        return node == null ? null : valueOf(node.method_53651());
    }

    /** getRoot(): the root of its tree (itself when unplaced) */
    public static net.minecraft.class_161 method_48024(net.minecraft.class_161 self) {
        return method_48023(self);
    }

    /** getRoot(advancement) (static), as 1.20.1 walked it: parents until there is none */
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

    /** getChildren(): the children of its node, in the order 1.20.2 added them */
    public static Iterable<net.minecraft.class_161> method_681(net.minecraft.class_161 self) {
        net.minecraft.class_8781 node = NODES.get(self);
        List<net.minecraft.class_161> out = new ArrayList<>();
        if (node != null) {
            for (net.minecraft.class_8781 child : node.method_53653()) {
                out.add(child.method_53647());
            }
        }
        return out;
    }

    /** addChild(child): 1.20.1 linked the child under this advancement; 1.20.2 links the nodes */
    public static void method_690(net.minecraft.class_161 self, net.minecraft.class_161 child) {
        nodeOf(self).method_53650(nodeOf(child));
    }

    /** sendsTelemetryEvent() */
    public static boolean method_51697(net.minecraft.class_161 self) {
        return self.comp_1917();
    }

    /** getRewards() */
    public static net.minecraft.class_170 method_691(net.minecraft.class_161 self) {
        return self.comp_1914();
    }

    /** getCriteria(): name -> criterion */
    public static Map<String, net.minecraft.class_175<?>> method_682(net.minecraft.class_161 self) {
        return self.comp_1915();
    }

    /** getRequirements(): the requirement groups */
    public static String[][] method_680(net.minecraft.class_161 self) {
        return self.comp_1916().comp_1922();
    }

    /** getMaxCriteraRequired(): the number of requirement groups */
    public static int method_683(net.minecraft.class_161 self) {
        return self.comp_1916().method_53664();
    }

    /**
     * getChatComponent(): 1.20.1 built it from the display (title in the frame's colour, hover with the
     * description) or, without a display, the id as literal text; 1.20.2's record keeps the first, its
     * holder's name answers the second (Advancement.name(holder))
     */
    public static net.minecraft.class_2561 method_684(net.minecraft.class_161 self) {
        return self.comp_1918().orElseGet(() -> net.minecraft.class_161.method_53622(holderOf(self)));
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
