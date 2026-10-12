package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;

/**
 * 1.20.1's Advancement on 1.20.2.
 *
 * A 1.20.1 Advancement carried its id, its parent and its children; 1.20.2 split it into the
 * Advancement record (the data: parent id, display, rewards, criteria, requirements), the
 * AdvancementHolder (id + record) and, in a loaded tree, the AdvancementNode (parent and children
 * nodes). A 1.20.1 mod's Advancement is the 1.20.2 record -- what every 1.20.2 API still names
 * Advancement -- and the rest is found from it: the holder that holds it (every holder ever made
 * records its record as it is constructed, CbAdvHolderTrack), the tree node built for it.
 * Records are keys by identity, held weakly.
 */
public final class CbAdvancements {

    private static final ConcurrentMap<Object, net.minecraft.class_8779> HOLDERS = new MapMaker().weakKeys().makeMap();

    /** called as an AdvancementHolder is constructed: the record belongs to that holder */
    public static void track(net.minecraft.class_8779 holder) {
        if (holder != null && holder.comp_1920() != null) {
            HOLDERS.put(holder.comp_1920(), holder);
        }
    }

    /**
     * The holder of a 1.20.1 advancement value -- the id and the record 1.20.2 hands around where
     * 1.20.1 handed the advancement. Every holder records its record, so an advancement 1.20.1 code
     * holds (loaded, received, built or deconstructed through a bridge) has one.
     */
    public static net.minecraft.class_8779 holderOf(net.minecraft.class_161 advancement) {
        if (advancement == null) {
            return null;
        }
        net.minecraft.class_8779 holder = HOLDERS.get(advancement);
        if (holder == null) {
            throw new IllegalStateException("CenturyBridge: no AdvancementHolder holds this advancement -- 1.20.2 names"
                + " advancements by holder, and this record was made outside every holder");
        }
        return holder;
    }

    /** the value of a holder handed to 1.20.1 code, which wants the advancement */
    public static net.minecraft.class_161 valueOf(net.minecraft.class_8779 holder) {
        return holder == null ? null : holder.comp_1920();
    }

    private CbAdvancements() {
    }
}
