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

    // ---------------------------------------------------------------- packets 1.20.5 turned into records

    /** ClientboundAwardStatsPacket.getStats() answered the packet's Object2IntMap as a Map; an override's map back as one */
    public static it.unimi.dsi.fastutil.objects.Object2IntMap<?> cbStatsMap(Map<?, ?> stats) {
        if (stats == null || stats instanceof it.unimi.dsi.fastutil.objects.Object2IntMap<?>) {
            return (it.unimi.dsi.fastutil.objects.Object2IntMap<?>) stats;
        }
        it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<Object> out = new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>();
        stats.forEach((k, v) -> out.put(k, ((Number) v).intValue()));
        return out;
    }

    /** ClientboundMapItemDataPacket: 1.20.4's int map id is 1.20.5's MapId of that id */
    public static int cbMapIdInt(class_9209 id) {
        return id.comp_2315();
    }

    public static class_9209 cbMapId(int id) {
        return new class_9209(id);
    }

    /** ServerboundSetCreativeModeSlotPacket's slot: an int in 1.20.4's packet, a short in 1.20.5's (1.20.4 sent it as a short too) */
    public static int cbIntOfShort(short slot) {
        return slot;
    }

    public static short cbShortOfInt(int slot) {
        return (short) slot;
    }

    /** ClientboundUpdateAttributesPacket.AttributeSnapshot's attribute: a Holder in 1.20.5 (BuiltInRegistries.ATTRIBUTE.wrapAsHolder, as CbHolders) */
    public static class_6880<net.minecraft.class_1320> cbAttribute(net.minecraft.class_1320 attribute) {
        return attribute == null ? null : net.minecraft.class_7923.field_41190.method_47983(attribute);
    }

    public static net.minecraft.class_1320 cbAttributeValue(class_6880<net.minecraft.class_1320> holder) {
        return holder == null ? null : holder.comp_349();
    }

    /**
     * new ClientboundGameProfilePacket(profile): 1.20.5 adds strictErrorHandling, which its own server
     * always sends as true (ServerLoginPacketListenerImpl.finishLoginAndWaitForClient).
     */
    public static boolean cbStrictErrorHandling() {
        return true;
    }

    /** the enforcesSecureChat a ClientboundServerDataPacket was made with by 1.20.4 code, which 1.20.5's packet does not carry */
    private static final Map<Object, Boolean> SECURE_CHAT = new com.google.common.collect.MapMaker().weakKeys().makeMap();

    /** new ClientboundServerDataPacket(motd, icon, enforcesSecureChat), after the 1.20.5 constructor: the flag stays with the packet */
    public static void cbSecureChat(net.minecraft.class_7495 packet, boolean enforcesSecureChat) {
        SECURE_CHAT.put(packet, enforcesSecureChat);
    }

    /**
     * ClientboundServerDataPacket.enforcesSecureChat(): the flag 1.20.4 code made the packet with; for
     * a packet 1.20.5 made or received, the session's -- what 1.20.4 put into it: a server sent
     * enforceSecureProfile(), and a client received it. 1.20.5 sends that flag in the login packet,
     * which the client keeps for the connection.
     */
    public static boolean cbSecureChatOf(net.minecraft.class_7495 packet) {
        Boolean made = SECURE_CHAT.get(packet);
        if (made != null) {
            return made;
        }
        if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            Boolean connected = CbNetClientState.cbServerEnforcesSecureChat();
            if (connected != null) {
                return connected;
            }
        } else if (net.fabricmc.loader.api.FabricLoader.getInstance().getGameInstance() instanceof net.minecraft.server.MinecraftServer server) {
            return server.method_43500();
        }
        return false;
    }

    private CbNetPackets() {
    }
}
