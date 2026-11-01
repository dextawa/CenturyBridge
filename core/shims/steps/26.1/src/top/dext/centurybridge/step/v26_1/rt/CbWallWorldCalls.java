package top.dext.centurybridge.step.v26_1.rt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

/**
 * 1.21.11 world-side calls whose shape 26.1 changed while the work stayed, 1.21.11's semantics on 26.1's:
 * <ul>
 * <li>Block.getDrops(state, level, pos, blockEntity, entity, tool): 26.1 takes the tool as an ItemInstance,
 *     which ItemStack is; the body is the same;</li>
 * <li>BubbleColumnBlock.updateColumn(level, pos, state, below): 26.1 names the column block first; 1.21.11's
 *     column was always the bubble column;</li>
 * <li>AttributeType.ofInterpolated(codec, modifiers, lerp, partialTickLerp): 26.1's five-argument factory with
 *     the float view 26.1's own three-argument factory leaves out (null);</li>
 * <li>RegistryDataLoader.load(..): 1.21.11 loaded synchronously and answered the frozen registries; 26.1's
 *     load answers a future fed by an executor. Run on the calling thread, which is where 1.21.11 did the
 *     work, and joined; a load failure surfaces as the exception 1.21.11 threw;</li>
 * <li>ClientboundSetTimePacket: 1.21.11's (gameTime, dayTime, tickDayTime); 26.1 carries the world clocks.
 *     1.21.11's day time is 26.1's overworld clock (CbWallLevelData) and tickDayTime the advance_time rule,
 *     which 26.1 sends as the clock's rate (0 when time stands);</li>
 * <li>DamageResistant.types(): 1.21.11 held a damage type tag, 26.1 a holder set (a tag, or since 26.1 a
 *     list);</li>
 * <li>LevelStorageAccess.saveDataTag(registries, worldData[, player]): 26.1 names the single player in
 *     level.dat by UUID and keeps the player's data in the player data directory.</li>
 * </ul>
 */
public final class CbWallWorldCalls {

    private static final boolean CLIENT =
        net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT;

    private CbWallWorldCalls() {
    }

    // ---- blocks ----

    /** 1.21.11 Block.getDrops(BlockState, ServerLevel, BlockPos, BlockEntity, Entity, ItemStack) */
    public static List<ItemStack> getDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity,
                                           Entity entity, ItemStack tool) {
        return Block.getDrops(state, level, pos, blockEntity, entity, tool);
    }

    /** 1.21.11 BubbleColumnBlock.updateColumn(LevelAccessor, BlockPos, BlockState, BlockState) */
    public static void updateColumn(LevelAccessor level, BlockPos pos, BlockState state, BlockState below) {
        BubbleColumnBlock.updateColumn(Blocks.BUBBLE_COLUMN, level, pos, state, below);
    }

    // ---- environment attributes ----

    /** 1.21.11 AttributeType.ofInterpolated(Codec, Map, LerpFunction, LerpFunction) */
    public static <V> AttributeType<V> ofInterpolated(Codec<V> codec,
                                                      Map<AttributeModifier.OperationId, AttributeModifier<V, ?>> modifiers,
                                                      LerpFunction<V> lerp, LerpFunction<V> partialTickLerp) {
        return AttributeType.ofInterpolated(codec, modifiers, lerp, partialTickLerp, null);
    }

    // ---- registry loading ----

    private static final Executor CALLING_THREAD = Runnable::run;

    private static <T> T join(CompletableFuture<T> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            if (cause instanceof Error err) {
                throw err;
            }
            throw e;
        }
    }

    /** 1.21.11 RegistryDataLoader.load(ResourceManager, List, List) */
    public static RegistryAccess.Frozen load(ResourceManager resources, List<HolderLookup.RegistryLookup<?>> lookups,
                                             List<RegistryDataLoader.RegistryData<?>> registries) {
        return join(RegistryDataLoader.load(resources, lookups, registries, CALLING_THREAD));
    }

    /** 1.21.11 RegistryDataLoader.load(Map, ResourceProvider, List, List) */
    public static RegistryAccess.Frozen load(
            Map<ResourceKey<? extends net.minecraft.core.Registry<?>>, RegistryDataLoader.NetworkedRegistryData> data,
            ResourceProvider resources, List<HolderLookup.RegistryLookup<?>> lookups,
            List<RegistryDataLoader.RegistryData<?>> registries) {
        return join(RegistryDataLoader.load(data, resources, lookups, registries, CALLING_THREAD));
    }

    // ---- ClientboundSetTimePacket ----

    private static Holder<WorldClock> overworldClock() {
        return CbWallRegistries.access().get(WorldClocks.OVERWORLD).orElseThrow(
            () -> new IllegalStateException("no overworld clock registered: 1.21.11's day time has nowhere to go"));
    }

    /** 1.21.11 new ClientboundSetTimePacket(gameTime, dayTime, tickDayTime) */
    public static ClientboundSetTimePacket setTimePacket(long gameTime, long dayTime, boolean tickDayTime) {
        return new ClientboundSetTimePacket(gameTime,
            Map.of(overworldClock(), new ClockNetworkState(dayTime, 0.0F, tickDayTime ? 1.0F : 0.0F)));
    }

    private static ClockNetworkState overworldState(ClientboundSetTimePacket packet) {
        for (Map.Entry<Holder<WorldClock>, ClockNetworkState> e : packet.clockUpdates().entrySet()) {
            if (e.getKey().is(WorldClocks.OVERWORLD)) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * 1.21.11 ClientboundSetTimePacket.dayTime(): the overworld clock's ticks. A 26.1 packet updating only other
     * clocks says nothing of it; the day time then is the overworld clock's as it stands (it did not change)
     */
    public static long dayTime(ClientboundSetTimePacket packet) {
        ClockNetworkState state = overworldState(packet);
        if (state != null) {
            return state.totalTicks();
        }
        MinecraftServer server = CbWallLevelData.SERVER;
        if (server != null) {
            return server.overworld().getOverworldClockTime();
        }
        if (CLIENT) {
            return CbWallLevelDataClient.overworldClockTime();
        }
        throw new IllegalStateException("1.21.11 day time read with no server running");
    }

    /**
     * 1.21.11 ClientboundSetTimePacket.tickDayTime(): whether the overworld clock runs (26.1 sends rate 0 when
     * the advance_time rule or a pause stops it). Without an overworld entry: the server's rule; a client
     * holds no game rules, and the packet then tells it nothing, so false (the time this packet sets stands)
     */
    public static boolean tickDayTime(ClientboundSetTimePacket packet) {
        ClockNetworkState state = overworldState(packet);
        if (state != null) {
            return state.rate() != 0.0F;
        }
        MinecraftServer server = CbWallLevelData.SERVER;
        return server != null && server.getGlobalGameRules().get(GameRules.ADVANCE_TIME);
    }

    // ---- DamageResistant ----

    /**
     * 1.21.11 new DamageResistant(TagKey): 26.1 holds the tag's holder set -- the registries' named set when they are
     * loaded, else a set that looks the tag up when used (CbWallTagSet), as 1.21.11 did; it names the tag either way
     * (types() below, the codec)
     */
    public static DamageResistant damageResistant(TagKey<DamageType> types) {
        return new DamageResistant(CbWallTagSet.of(types));
    }

    /**
     * 1.21.11 DamageResistant.types(): the tag. 26.1 also reads a list of damage types, which 1.21.11 had no
     * value for
     */
    public static TagKey<DamageType> types(DamageResistant resistant) {
        HolderSet<DamageType> types = resistant.types();
        return types.unwrapKey().orElseThrow(() -> new IllegalStateException(
            resistant + ": damage resistance to a list of damage types, which a 1.21.11 tag cannot name"));
    }

    // ---- level storage ----

    /** 1.21.11 LevelStorageAccess.saveDataTag(RegistryAccess, WorldData) (with its generation settings and rules: CbWallWorldLoad) */
    public static void saveDataTag(LevelStorageSource.LevelStorageAccess access, RegistryAccess registries, WorldData data) {
        access.saveDataTag(data);
        CbWallWorldLoad.saveSplitData(access, registries, data);
    }

    /**
     * 1.21.11 LevelStorageAccess.saveDataTag(RegistryAccess, WorldData, CompoundTag player): 1.21.11 wrote the
     * single player's data into level.dat; 26.1 names that player there by UUID and reads its data from the
     * player data directory, where it is written as 26.1's PlayerDataStorage writes it
     */
    public static void saveDataTag(LevelStorageSource.LevelStorageAccess access, RegistryAccess registries, WorldData data,
                                   CompoundTag player) {
        UUID id = player == null ? null : player.read("UUID", UUIDUtil.CODEC).orElse(null);
        access.saveDataTag(data, id);
        CbWallWorldLoad.saveSplitData(access, registries, data);
        if (id == null) {
            return;
        }
        Path dir = access.getLevelPath(LevelResource.PLAYER_DATA_DIR);
        try {
            Files.createDirectories(dir);
            Path temp = Files.createTempFile(dir, id + "-", ".dat");
            NbtIo.writeCompressed(player, temp);
            Util.safeReplaceFile(dir.resolve(id + ".dat"), temp, dir.resolve(id + ".dat_old"));
        } catch (IOException e) {
            throw new UncheckedIOException("saving the single player's data for " + id, e);
        }
    }
}
