package top.dext.centurybridge.step.v26_1.facade;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import top.dext.centurybridge.step.v26_1.rt.CbWallCauldrons;

/**
 * 1.21.11's CauldronInteraction.InteractionMap record: a named map from item to interaction, which mods filled
 * (WATER.map().put(item, interaction)) and the cauldron read. 26.1 keeps a Dispatcher per cauldron content:
 * interactions per item tag (asked first) and per item, else CauldronInteraction.DEFAULT. Each InteractionMap
 * stands for one dispatcher (rt/CbWallCauldrons); its map is a live view of what the dispatcher answers per
 * item -- an item put through it is answered before the dispatcher's tags (1.21.11 had no tags: an item's
 * entry was the item's interaction), get answers the dispatcher's interaction (DEFAULT for an item it does not
 * know, 1.21.11's default return value), the entries enumerate every item with an interaction. Components and
 * record equality keep 1.21.11's intermediary names; the fields are public (access wideners never reach this
 * class).
 */
public final class CauldronInteractionMap {

    public final String comp_1981;
    public final Map<Item, CauldronInteraction> comp_1982;

    /** 1.21.11's constructor: a map of the mod's own, standing for a dispatcher of its own (rt/CbWallCauldrons.adopt) */
    public CauldronInteractionMap(String name, Map<Item, CauldronInteraction> map) {
        this(name, map, CbWallCauldrons.adopt(name, map));
    }

    private CauldronInteractionMap(String name, Map<Item, CauldronInteraction> map, CauldronInteraction.Dispatcher dispatcher) {
        this.comp_1981 = name;
        this.comp_1982 = map;
        CbWallCauldrons.bind(this, dispatcher);
    }

    /** the map standing for a 26.1 dispatcher */
    public static CauldronInteractionMap of(String name, CauldronInteraction.Dispatcher dispatcher) {
        return new CauldronInteractionMap(name, new View(dispatcher), dispatcher);
    }

    /** name */
    public String comp_1981() {
        return this.comp_1981;
    }

    /** map */
    public Map<Item, CauldronInteraction> comp_1982() {
        return this.comp_1982;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CauldronInteractionMap m && Objects.equals(this.comp_1981, m.comp_1981) && Objects.equals(this.comp_1982, m.comp_1982);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_1981) + System.identityHashCode(this.comp_1982);
    }

    @Override
    public String toString() {
        return "class_8821[comp_1981=" + this.comp_1981 + ", comp_1982=" + this.comp_1982 + "]";
    }

    /** a dispatcher seen as 1.21.11's item map */
    static final class View extends AbstractMap<Item, CauldronInteraction> {
        private final CauldronInteraction.Dispatcher dispatcher;

        View(CauldronInteraction.Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public CauldronInteraction get(Object key) {
            return key instanceof Item item ? CbWallCauldrons.interaction(this.dispatcher, item) : CauldronInteraction.DEFAULT;
        }

        @Override
        public CauldronInteraction put(Item item, CauldronInteraction interaction) {
            return CbWallCauldrons.put(this.dispatcher, item, interaction);
        }

        @Override
        public CauldronInteraction remove(Object key) {
            return key instanceof Item item ? CbWallCauldrons.remove(this.dispatcher, item) : null;
        }

        @Override
        public boolean containsKey(Object key) {
            return key instanceof Item item && CbWallCauldrons.known(this.dispatcher, item);
        }

        @Override
        public Set<Entry<Item, CauldronInteraction>> entrySet() {
            Map<Item, CauldronInteraction> all = new LinkedHashMap<>();
            CbWallCauldrons.forEachKnown(this.dispatcher, all::put);
            return java.util.Collections.unmodifiableMap(all).entrySet();
        }
    }
}
