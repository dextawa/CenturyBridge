package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import top.dext.centurybridge.step.v26_1.mixin.CbWallServerAccess;

/**
 * 1.21.11 WorldData / PrimaryLevelData members whose state 26.1 moved out of level.dat:
 * <ul>
 * <li>the world generation options (constructor, parse): into the server's WorldGenSettings saved data. The
 *     options a 1.21.11 call built a level data with are kept for it, and worldGenOptions() answers them
 *     (CbWallLevelData.worldGenOptions).</li>
 * <li>custom boss events: into the CustomBossEvents saved data. 1.21.11's world data held them as a tag the
 *     server loaded at start and stored back at every save; a tag a mod set is what the world data holds
 *     until the next save stores the server's own over it, and the one the server starts with when set
 *     before (mixin/CbWallServerMixin).</li>
 * <li>the single player's data (level.dat's "Player"): into the player data storage under the single
 *     player's UUID, which level.dat names.</li>
 * </ul>
 */
public final class CbWallWorldData {

    private CbWallWorldData() {
    }

    // ---- world generation options ----

    private static final Map<WorldData, WorldOptions> OPTIONS = Collections.synchronizedMap(new WeakHashMap<>());

    /** the options a 1.21.11 constructor or parse built this level data with, else null */
    static WorldOptions options(WorldData data) {
        return OPTIONS.get(data);
    }

    /** post of 1.21.11's new PrimaryLevelData(settings, options, specialWorldProperty, lifecycle) */
    public static void constructed(PrimaryLevelData data, WorldOptions options) {
        OPTIONS.put(data, options);
    }

    /** 1.21.11's PrimaryLevelData.parse(tag, settings, specialWorldProperty, options, lifecycle) */
    public static <T> PrimaryLevelData parse(Dynamic<T> tag, LevelSettings settings, PrimaryLevelData.SpecialWorldProperty specialWorldProperty,
        WorldOptions options, Lifecycle lifecycle) {
        PrimaryLevelData data = PrimaryLevelData.parse(tag, settings, specialWorldProperty, lifecycle);
        OPTIONS.put(data, options);
        return data;
    }

    // ---- custom boss events ----

    private static final Map<WorldData, CompoundTag> BOSS_EVENTS = Collections.synchronizedMap(new WeakHashMap<>());

    private static MinecraftServer serverOf(WorldData data) {
        MinecraftServer s = CbWallLevelData.SERVER;
        return s != null && s.getWorldData() == data ? s : null;
    }

    /** 1.21.11 WorldData.getCustomBossEvents() */
    public static CompoundTag getCustomBossEvents(WorldData data) {
        CompoundTag set = BOSS_EVENTS.get(data);
        if (set != null) {
            return set;
        }
        MinecraftServer s = serverOf(data);
        if (s == null) {
            throw new IllegalStateException(data + ": 1.21.11 world data's custom boss events read with no running server of it;"
                + " 26.1 keeps them in the server's CustomBossEvents saved data");
        }
        Tag tag = CustomBossEvents.TYPE.codec().encodeStart(RegistryOps.create(NbtOps.INSTANCE, s.registryAccess()), s.getCustomBossEvents())
            .getOrThrow();
        return tag instanceof CompoundTag compound ? compound : new CompoundTag();
    }

    /** 1.21.11 WorldData.setCustomBossEvents(tag) */
    public static void setCustomBossEvents(WorldData data, CompoundTag tag) {
        BOSS_EVENTS.put(data, tag);
    }

    /** a server saves: 1.21.11 stored the server's events over the world data's tag */
    public static void saved(MinecraftServer s) {
        BOSS_EVENTS.remove(s.getWorldData());
    }

    /** a server was constructed (and loaded its events): a tag set before is what 1.21.11's server loaded */
    public static void serverConstructed(MinecraftServer s) {
        CompoundTag set = BOSS_EVENTS.remove(s.getWorldData());
        if (set != null) {
            CustomBossEvents events = CustomBossEvents.TYPE.codec().parse(RegistryOps.create(NbtOps.INSTANCE, s.registryAccess()), set).getOrThrow();
            s.getDataStorage().set(CustomBossEvents.TYPE, events);
            ((CbWallServerAccess) s).cb$setCustomBossEvents(events);
        }
    }

    // ---- the single player ----

    /**
     * 1.21.11 WorldData.getLoadedPlayerTag(): the single player's data level.dat held (null in a world with
     * none). 26.1's level.dat names the single player by UUID and keeps the data in the player data storage.
     */
    public static CompoundTag getLoadedPlayerTag(WorldData data) {
        UUID player = data.getSinglePlayerUUID();
        if (player == null) {
            return null;
        }
        MinecraftServer s = serverOf(data);
        if (s == null) {
            throw new IllegalStateException(data + ": 1.21.11 world data's single player read with no running server of it;"
                + " 26.1 keeps the data in the server's player data storage");
        }
        return ((CbWallServerAccess) s).cb$playerDataStorage().load(new NameAndId(player, player.toString())).orElse(null);
    }

    /**
     * 1.21.11 WorldData.createTag(registries, playerTag): level.dat's tag with the single player's data in it.
     * 26.1's names the single player by UUID: the given player's, else the data's own; the given player's data
     * is written under 1.21.11's "Player" too.
     */
    public static CompoundTag createTag(WorldData data, RegistryAccess registries, CompoundTag playerTag) {
        UUID player = playerTag != null ? playerTag.read("UUID", UUIDUtil.CODEC).orElse(data.getSinglePlayerUUID()) : data.getSinglePlayerUUID();
        CompoundTag tag = data.createTag(player);
        if (playerTag != null) {
            tag.put("Player", playerTag);
        }
        return tag;
    }
}
