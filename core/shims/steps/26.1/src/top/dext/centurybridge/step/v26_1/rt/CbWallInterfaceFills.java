package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameRuleValuesPacket;
import net.minecraft.network.protocol.game.ClientboundLowDiskSpaceWarningPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSetGameRulePacket;
import net.minecraft.network.protocol.game.ServerboundSpectateEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;

/**
 * Methods 26.1 demands of a type 1.21.11 mods implement or extend, where 1.21.11 had none (abstractFills), and
 * 26.1 shapes of methods a 1.21.11 implementation has in its own shape (overrideHooks with a bridge):
 * <ul>
 * <li>Holder: 1.21.11 holders carried no data components -- none, and nothing left to bind;</li>
 * <li>Registry.componentLookup(): the lookup 26.1 builds over a registry's elements, over the mod registry's;</li>
 * <li>WritableRegistry.bindTags(map): 1.21.11 bound one tag at a time (bindTag), each entry so;</li>
 * <li>game packet listeners: the packets 26.1 added (game rule values, low disk space, set game rule, spectate)
 *     did not exist for a 1.21.11 listener, which handled none of them; 26.1 split 1.21.11's interact packet
 *     (whose ATTACK action is 26.1's attack packet), so a 1.21.11 listener's handleInteract takes both;</li>
 * <li>Avatar.getProfile(): a 1.21.11 avatar that is no player had no profile -- 26.1's empty one;</li>
 * <li>Level.clockManager(): 1.21.11's day time of any level was the overworld's (CbWallLevelData) -- the
 *     running server's clocks, on the client the client level's;</li>
 * <li>WorldData: 1.21.11 wrote the single player's data into level.dat (createTag(registries, player)) and
 *     read it back from there (getLoadedPlayerTag); 26.1 names the player by UUID.</li>
 * </ul>
 */
public final class CbWallInterfaceFills {

    private static final boolean CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    private CbWallInterfaceFills() {
    }

    // ---- Holder / Registry ----

    public static boolean areComponentsBound(Holder<?> holder) {
        return true;
    }

    public static DataComponentMap components(Holder<?> holder) {
        return DataComponentMap.EMPTY;
    }

    private static final Map<Registry<?>, DataComponentLookup<?>> LOOKUPS = new WeakHashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DataComponentLookup componentLookup(Registry registry) {
        synchronized (LOOKUPS) {
            return LOOKUPS.computeIfAbsent(registry,
                r -> new DataComponentLookup<>(() -> (java.util.Iterator) ((Registry) r).listElements().iterator()));
        }
    }

    /** 1.21.11 WritableRegistry.bindTag(tag, holders), the mod registry's own */
    @SuppressWarnings("rawtypes")
    public interface LegacyBindTag {
        void bindTag(TagKey tag, List holders);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void bindTags(LegacyBindTag registry, Map tags) {
        ((Map<TagKey, List>) tags).forEach(registry::bindTag);
    }

    /** 1.21.11 WritableRegistry.bindTag(tag, holders) called on a 26.1 registry: 26.1's bindTags of that one tag (the same body) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void bindTag(net.minecraft.core.WritableRegistry registry, TagKey tag, List holders) {
        registry.bindTags(Map.of(tag, holders));
    }

    // ---- packet listeners ----

    public static void handleLowDiskSpaceWarning(ClientGamePacketListener listener, ClientboundLowDiskSpaceWarningPacket packet) {
    }

    public static void handleGameRuleValues(ClientGamePacketListener listener, ClientboundGameRuleValuesPacket packet) {
    }

    public static void handleSetGameRule(ServerGamePacketListener listener, ServerboundSetGameRulePacket packet) {
    }

    public static void handleSpectateEntity(ServerGamePacketListener listener, ServerboundSpectateEntityPacket packet) {
    }

    /** 1.21.11 ServerGamePacketListener.handleInteract(ServerboundInteractPacket), the packet held as a Packet (CbWallInteractPacket) */
    public interface LegacyInteract {
        void handleInteract(Packet<?> packet);
    }

    public static void handleInteract(LegacyInteract listener, ServerboundInteractPacket packet) {
        listener.handleInteract(packet);
    }

    public static void handleAttack(LegacyInteract listener, ServerboundAttackPacket packet) {
        listener.handleInteract(packet);
    }

    // ---- Avatar / Level ----

    public static ResolvableProfile getProfile(Avatar avatar) {
        return ResolvableProfile.Static.EMPTY;
    }

    /** the clocks in play: the running server's, else the client level's */
    private static final ClockManager LIVE = clock -> {
        MinecraftServer server = CbWallLevelData.SERVER;
        if (server != null) {
            return server.clockManager().getTotalTicks(clock);
        }
        if (CLIENT) {
            ClockManager client = Client.clocks();
            if (client != null) {
                return client.getTotalTicks(clock);
            }
        }
        throw new IllegalStateException("1.21.11 level time read with no server and no client level");
    };

    public static ClockManager clockManager(Level level) {
        return LIVE;
    }

    @Environment(EnvType.CLIENT)
    private static final class Client {
        static ClockManager clocks() {
            return Minecraft.getInstance().level != null ? Minecraft.getInstance().level.clockManager() : null;
        }
    }

    // ---- WorldData ----

    /** 1.21.11 WorldData.createTag(registries, player), the mod's own */
    public interface LegacyCreateTag {
        CompoundTag createTag(RegistryAccess registries, CompoundTag player);
    }

    /** 26.1 createTag(singlePlayer): 1.21.11's tag (no player data: 26.1 keeps it apart) naming the player as 26.1 does */
    public static CompoundTag createTag(LegacyCreateTag data, UUID singlePlayer) {
        CompoundTag tag = data.createTag(CbWallRegistries.access(), null);
        if (singlePlayer != null) {
            tag.storeNullable("singleplayer_uuid", UUIDUtil.CODEC, singlePlayer);
        }
        return tag;
    }

    /** 1.21.11 WorldData.getLoadedPlayerTag(), the mod's own */
    public interface LegacyPlayerTag {
        CompoundTag getLoadedPlayerTag();
    }

    /** 26.1 getSinglePlayerUUID(): the UUID of the player 1.21.11's level data loaded */
    public static UUID getSinglePlayerUUID(LegacyPlayerTag data) {
        CompoundTag player = data.getLoadedPlayerTag();
        return player == null ? null : player.read("UUID", UUIDUtil.CODEC).orElse(null);
    }
}
