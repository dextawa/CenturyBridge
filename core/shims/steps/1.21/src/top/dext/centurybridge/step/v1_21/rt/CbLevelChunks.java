package top.dext.centurybridge.step.v1_21.rt;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;

import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_2794;
import net.minecraft.class_2806;
import net.minecraft.class_3218;
import net.minecraft.class_3227;
import net.minecraft.class_3230;
import net.minecraft.class_3485;
import net.minecraft.class_3898;
import net.minecraft.class_9259;
import net.minecraft.class_9312;
import net.minecraft.class_9761;
import top.dext.centurybridge.step.v1_21.mixin.CbLevelChunkMapAccessor;
import top.dext.centurybridge.step.v1_21.mixin.CbLevelGenHolderAccessor;

/**
 * 1.20.6's chunk holder / chunk map / chunk status API (net/minecraft/server/level, world/level/chunk/status) on
 * 1.21's rebuilt chunk system. 1.21 split ChunkHolder into GenerationChunkHolder (the per-status futures, the
 * status queries, generation scheduling) and ChunkHolder (ticket levels, broadcasting), moved generation into
 * ChunkStep / ChunkPyramid and replaced chunk range futures with generation tasks. Wired from s6fin-level.json.
 */
public final class CbLevelChunks {

    private CbLevelChunks() {
    }

    // ------------------------------------------------------------------------------------------------ ChunkHolder

    /** 1.20.6 ChunkHolder.UNLOADED_CHUNK: 1.21's GenerationChunkHolder.UNLOADED_CHUNK, the same error result */
    public static class_9259<class_2791> field_16426() {
        return class_9761.field_51866;
    }

    /** 1.20.6 ChunkHolder.UNLOADED_CHUNK_FUTURE: 1.21's GenerationChunkHolder.UNLOADED_CHUNK_FUTURE */
    public static CompletableFuture<class_9259<class_2791>> field_16430() {
        return class_9761.field_51867;
    }

    /** 1.20.6 ChunkHolder.NOT_DONE_YET: 1.21's GenerationChunkHolder.NOT_DONE_YET (the instance vanilla compares against) */
    public static class_9259<class_2791> field_36388() {
        return CbLevelGenHolderAccessor.cb$notDoneYet();
    }

    private static AtomicReferenceArray<CompletableFuture<class_9259<class_2791>>> futures(class_9761 holder) {
        return ((CbLevelGenHolderAccessor) holder).cb$futures();
    }

    /** the 1.20.6 ChunkHolder method CbLevelChunkHolderMixin gives back that 1.21's own getters stand on */
    public interface LegacyHolder {
        CompletableFuture<class_9259<class_2791>> method_16146(class_2806 status);
    }

    /**
     * 1.20.6 holder.getFutureIfPresentUnchecked(status): the holder's future for that status, or the unloaded
     * future when none was made. 1.21 keeps the same array, by status index, in GenerationChunkHolder.
     */
    public static CompletableFuture<class_9259<class_2791>> futureIfPresentUnchecked(class_9761 holder, class_2806 status) {
        CompletableFuture<class_9259<class_2791>> future = futures(holder).get(status.method_16559());
        return future == null ? class_9761.field_51867 : future;
    }

    /**
     * The new shape a 1.20.6 override of getFutureIfPresentUnchecked / getFutureIfPresent answers in 1.21
     * (getChunkIfPresentUnchecked / getChunkIfPresent): the chunk that future holds now, which is what 1.21's
     * own implementations answer from the same future (getNow(NOT_DONE_YET).orElse(null)).
     */
    public static class_2791 chunkNow(CompletableFuture<class_9259<class_2791>> future) {
        return future == null ? null : future.getNow(CbLevelGenHolderAccessor.cb$notDoneYet()).method_57130(null);
    }

    // ------------------------------------------------------------------------------------------------ TicketType

    /**
     * 1.20.6 TicketType.LIGHT ("light", ordered by chunk position), gone in 1.21 whose light engine needs no
     * ticket. The same ticket type, made the way 1.20.6 made it: a mod's light tickets hold chunks at the
     * level it gives them, as they did (CbLevelChunkMapMixin.releaseLightTicket removes them).
     */
    public static final class_3230<class_1923> LIGHT = class_3230.method_14291("light", Comparator.comparingLong(class_1923::method_8324));

    public static class_3230<class_1923> field_19270() {
        return LIGHT;
    }

    // ------------------------------------------------------------------------------------------------ WorldGenContext

    /**
     * new WorldGenContext(level, generator, structureManager, lightEngine) of 1.20.6: 1.21's also carries the
     * main thread mailbox generation steps post to, the one the level's chunk map builds its own context with.
     */
    public static class_9312 newWorldGenContext(class_3218 level, class_2794 generator, class_3485 structures, class_3227 light) {
        class_3898 map = level.method_14178().field_17254;
        return new class_9312(level, generator, structures, light, ((CbLevelChunkMapAccessor) map).cb$mainThreadMailbox());
    }

    // ------------------------------------------------------------------------------------------------ ChunkGenerator

    /**
     * The executor a 1.20.6 fillFromNoise / createBiomes override is handed when 1.21 calls the new shape, which
     * has none: 1.20.6 passed the generation task's executor for the continuations (the noise generator's
     * whenCompleteAsync releasing its section locks); 1.21 runs those continuations where the future completes,
     * and so does this one.
     */
    public static final Executor DIRECT = Runnable::run;

    /** a 1.20.6 generator class's own fillFromNoise(executor, ...) / createBiomes(executor, ...) */
    public interface LegacyGenerator {
        CompletableFuture<class_2791> method_12088(Executor executor, net.minecraft.class_6748 blender, net.minecraft.class_7138 random,
                                                   net.minecraft.class_5138 structures, class_2791 chunk);
    }

    public interface LegacyBiomes {
        CompletableFuture<class_2791> method_38275(Executor executor, net.minecraft.class_7138 random, net.minecraft.class_6748 blender,
                                                   net.minecraft.class_5138 structures, class_2791 chunk);
    }

    /** the super accessors a 1.20.6 generator subclass gets for its super calls */
    public interface SuperGenerator {
        CompletableFuture<class_2791> cb$super$method_12088(net.minecraft.class_6748 blender, net.minecraft.class_7138 random,
                                                            net.minecraft.class_5138 structures, class_2791 chunk);

        CompletableFuture<class_2791> cb$super$method_38275(net.minecraft.class_7138 random, net.minecraft.class_6748 blender,
                                                            net.minecraft.class_5138 structures, class_2791 chunk);
    }

    /** generator.fillFromNoise(executor, ...) of 1.20.6: a 1.20.6 class's own method with the caller's executor, else 1.21's */
    public static CompletableFuture<class_2791> method_12088(class_2794 generator, Executor executor, net.minecraft.class_6748 blender,
                                                           net.minecraft.class_7138 random, net.minecraft.class_5138 structures, class_2791 chunk) {
        if (generator instanceof LegacyGenerator legacy) {
            return legacy.method_12088(executor, blender, random, structures, chunk);
        }
        return generator.method_12088(blender, random, structures, chunk);
    }

    /** the same, the call made on a NoiseBasedChunkGenerator */
    public static CompletableFuture<class_2791> method_12088(net.minecraft.class_3754 generator, Executor executor, net.minecraft.class_6748 blender,
                                                           net.minecraft.class_7138 random, net.minecraft.class_5138 structures, class_2791 chunk) {
        return method_12088((class_2794) generator, executor, blender, random, structures, chunk);
    }

    /** 1.21's fillFromNoise(...) of a 1.20.6 generator class: its own fillFromNoise(executor, ...) */
    public static CompletableFuture<class_2791> fillFromNoise(LegacyGenerator self, net.minecraft.class_6748 blender, net.minecraft.class_7138 random,
                                                            net.minecraft.class_5138 structures, class_2791 chunk) {
        return self.method_12088(DIRECT, blender, random, structures, chunk);
    }

    /** 1.21's createBiomes(...) of a 1.20.6 generator class: its own createBiomes(executor, ...) */
    public static CompletableFuture<class_2791> createBiomes(LegacyBiomes self, net.minecraft.class_7138 random, net.minecraft.class_6748 blender,
                                                           net.minecraft.class_5138 structures, class_2791 chunk) {
        return self.method_38275(DIRECT, random, blender, structures, chunk);
    }

    /** super.fillFromNoise(executor, ...) of a 1.20.6 subclass: super's 1.21 fillFromNoise (the executor is 1.21's to drop) */
    public static CompletableFuture<class_2791> superFillFromNoise(SuperGenerator self, Executor executor, net.minecraft.class_6748 blender,
                                                                 net.minecraft.class_7138 random, net.minecraft.class_5138 structures, class_2791 chunk) {
        return self.cb$super$method_12088(blender, random, structures, chunk);
    }

    /** super.createBiomes(executor, ...) of a 1.20.6 subclass: super's 1.21 createBiomes */
    public static CompletableFuture<class_2791> superCreateBiomes(SuperGenerator self, Executor executor, net.minecraft.class_7138 random,
                                                                net.minecraft.class_6748 blender, net.minecraft.class_5138 structures, class_2791 chunk) {
        return self.cb$super$method_38275(random, blender, structures, chunk);
    }
}
