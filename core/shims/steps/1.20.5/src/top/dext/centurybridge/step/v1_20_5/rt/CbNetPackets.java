package top.dext.centurybridge.step.v1_20_5.rt;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_1291;
import net.minecraft.class_6880;
import net.minecraft.class_9209;

/**
 * Value conversions for packets and client-side network state between 1.20.4 and 1.20.5 (area
 * s4net). Where only the type arguments changed (the erased descriptor stayed), the engine converts
 * in place; every converter here is adaptive -- a value already of the wanted kind passes through --
 * and each pair undoes the other, because erasure cannot tell who handed a value over.
 */
public final class CbNetPackets {

    // ---------------------------------------------------------------- beacon

    /**
     * ServerboundSetBeaconPacket's effects: 1.20.4 Optional&lt;MobEffect&gt;, 1.20.5
     * Optional&lt;Holder&lt;MobEffect&gt;&gt; (CbHolders.mobEffect: the registered reference).
     */
    public static Optional<?> cbEffectHolder(Optional<?> effect) {
        return effect == null ? null : effect.map(v -> v instanceof class_1291 e ? CbHolders.mobEffect(e) : v);
    }

    /** ... and back, for 1.20.4 code reading the packet's effects */
    public static Optional<?> cbEffectValue(Optional<?> effect) {
        return effect == null ? null : effect.map(v -> v instanceof class_6880<?> h ? CbHolders.value(h) : v);
    }

    // ---------------------------------------------------------------- map data on the client

    /**
     * ClientLevel's map data: 1.20.4 keyed it by the map's name, "map_" + id (MapItem.makeKey), 1.20.5
     * by MapId -- whose key() is that same name. getAllMapData() answered an immutable copy in both, so
     * this answers one too, in the same order.
     */
    public static Map<?, ?> cbMapsByName(Map<?, ?> maps) {
        if (maps == null) {
            return null;
        }
        ImmutableMap.Builder<Object, Object> out = ImmutableMap.builder();
        maps.forEach((k, v) -> out.put(k instanceof class_9209 id ? id.method_56814() : k, v));
        return out.build();
    }

    /** ... and back: a 1.20.4 name "map_" + id is the MapId of that id; a name of any other form names no map 1.20.5 can hold */
    public static Map<?, ?> cbMapsById(Map<?, ?> maps) {
        if (maps == null) {
            return null;
        }
        Map<Object, Object> out = new LinkedHashMap<>();
        maps.forEach((k, v) -> out.put(k instanceof String name ? cbMapId(name) : k, v));
        return out;
    }

    static class_9209 cbMapId(String name) {
        if (name.startsWith("map_")) {
            try {
                return new class_9209(Integer.parseInt(name.substring(4)));
            } catch (NumberFormatException ignored) {
                // not a map name
            }
        }
        throw new IllegalArgumentException("CenturyBridge: '" + name + "' is not a map name (1.20.4's MapItem.makeKey"
            + " made every one as map_<id>); 1.20.5 keys a client's map data by MapId");
    }

    private CbNetPackets() {
    }
}
