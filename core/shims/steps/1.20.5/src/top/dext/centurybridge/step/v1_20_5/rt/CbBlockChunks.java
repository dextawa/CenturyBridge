package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Either;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * 1.20.4's chunk storage and chunk status API on 1.20.5.
 *
 * <p>Region storage: 1.20.5 describes every region storage with a RegionStorageInfo (level id,
 * dimension, type) that 1.20.4's constructors did not take. 1.20.5 reads it in two places only: the
 * IOWorker names its mailbox "IOWorker-" + type, as 1.20.4's named it "IOWorker-" + its name argument;
 * and the JFR chunk-region read / write events, which 1.20.4 did not have, record all three (the
 * event dereferences the dimension). The type is therefore 1.20.4's IOWorker name -- "chunk" for a
 * ChunkStorage, the folder's name for a SectionStorage, "entities" for an EntityStorage, the name
 * argument for an IOWorker -- and, for a RegionFile opened on its own (1.20.4 named it nothing), the
 * name of its region folder. The level id and dimension are the level's where the 1.20.4 constructor
 * names one (EntityStorage); a constructor that names none labels its events with no level id and
 * the dimension {@link #NO_LEVEL}.
 *
 * <p>1.20.4's SectionStorage and EntityStorage built their IOWorker, data fixer and fix type
 * themselves; 1.20.5 takes them as a SimpleRegionStorage, which upgrades a tag exactly as 1.20.4's
 * storages did (the tag's DataVersion, else the default each passes, through the fix type).
 *
 * <p>ChunkStorage.write: 1.20.4's answered nothing, 1.20.5's answers the store's future. A 1.20.4
 * storage overriding write gives its caller nothing to wait on -- the worker finishes the store on its
 * own and flushes it on close -- so a caller of the new shape gets a completed future once the
 * override has returned.
 *
 * <p>ChunkStatus.generate / load: 1.20.4 passed the level, generator, structure manager and light
 * engine one by one where 1.20.5 passes a WorldGenContext of the same four (load named no generator:
 * the level's, which is the one its chunk map's context holds); 1.20.4's full-chunk function and its
 * futures carried {@code Either<ChunkAccess, ChunkLoadingFailure>} where 1.20.5's carry the chunk and
 * fail exceptionally. A failure the 1.20.4 function answers travels through 1.20.5's future as
 * {@link Failed} and comes back out as that same failure.
 */
public final class CbBlockChunks {

    /** the dimension a storage is labelled with when its 1.20.4 constructor named no level */
    public static final net.minecraft.class_5321<net.minecraft.class_1937> NO_LEVEL =
        net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41223, new net.minecraft.class_2960("centurybridge", "no_level"));

    /** MinecraftServer's storage access (CbBlockServerStorageAccessor) */
    public interface ServerStorage {
        net.minecraft.class_32.class_5143 cb$storageSource();
    }

    /** EntityStorage's region storage (CbBlockEntityStorageAccessor) */
    public interface EntityRegions {
        net.minecraft.class_9172 cb$simpleRegionStorage();
    }

    /** SimpleRegionStorage's data fixer (CbBlockRegionStorageAccessor) */
    public interface RegionFixer {
        com.mojang.datafixers.DataFixer cb$fixerUpper();
    }

    /** 1.20.4's ChunkStorage.write(pos, tag) a mod storage class overrides */
    public interface LegacyWrite {
        void method_17910(net.minecraft.class_1923 pos, net.minecraft.class_2487 tag);
    }

    private static net.minecraft.class_9240 unnamed(String type) {
        return new net.minecraft.class_9240("", NO_LEVEL, type);
    }

    /** ChunkStorage(path, fixer, sync): 1.20.4's worker was "chunk" */
    public static net.minecraft.class_9240 chunkStorageInfo() {
        return unnamed("chunk");
    }

    /** IOWorker(path, sync, name), a mod's super(..): the name 1.20.4 gave the worker */
    public static net.minecraft.class_9240 ioWorkerInfo(String name) {
        return unnamed(name);
    }

    /** RegionFile(path, externalDir, ..) opened on its own: labelled with its region folder's name */
    public static net.minecraft.class_9240 regionFileInfo(Path externalDir) {
        Path folder = externalDir == null ? null : externalDir.getFileName();
        return unnamed(folder == null ? "" : folder.toString());
    }

    /** SectionStorage(path, .., fixer, type, sync, ..): the region storage 1.20.4's constructor built, worker named after the folder */
    public static net.minecraft.class_9172 sectionStorage(Path path, com.mojang.datafixers.DataFixer fixer, net.minecraft.class_4284 type, boolean sync) {
        return new net.minecraft.class_9172(unnamed(path.getFileName().toString()), path, fixer, sync, type);
    }

    /** EntityStorage(level, path, fixer, sync, ..): the level's "entities" region storage, as 1.20.4's constructor built it */
    public static net.minecraft.class_9172 entityStorage(net.minecraft.class_3218 level, Path path, com.mojang.datafixers.DataFixer fixer, boolean sync) {
        String levelId = ((ServerStorage) level.method_8503()).cb$storageSource().method_27005();
        return new net.minecraft.class_9172(new net.minecraft.class_9240(levelId, level.method_27983(), "entities"), path, fixer, sync,
            net.minecraft.class_4284.field_26990);
    }

    /** EntityStorage.fixerUpper (protected in 1.20.4): the fixer its region storage upgrades with */
    public static com.mojang.datafixers.DataFixer field_27231(net.minecraft.class_5565 self) {
        return ((RegionFixer) ((EntityRegions) self).cb$simpleRegionStorage()).cb$fixerUpper();
    }

    /** write(pos, tag) as 1.20.5 asks it of a storage overriding 1.20.4's (see the class comment) */
    public static CompletableFuture<Void> write(LegacyWrite self, net.minecraft.class_1923 pos, net.minecraft.class_2487 tag) {
        self.method_17910(pos, tag);
        return CompletableFuture.completedFuture(null);
    }

    /** a failure a 1.20.4 full-chunk function answered, carried through 1.20.5's future */
    static final class Failed extends RuntimeException {
        final Object failure;

        Failed(Object failure) {
            super("chunk loading failure " + failure, null, false, false);
            this.failure = failure;
        }
    }

    /** 1.20.4's full-chunk function as 1.20.5's ToFullChunk */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static net.minecraft.class_9311 toFullChunk(Function fullChunk) {
        return chunk -> ((CompletableFuture<Object>) fullChunk.apply(chunk)).thenApply(result -> {
            Either either = (Either) CbRestChunks.cbEither(result);
            return (net.minecraft.class_2791) either.map(c -> c, failure -> {
                throw new Failed(failure);
            });
        });
    }

    /** 1.20.5's chunk future as 1.20.4's: the chunk on the left, a carried failure on the right */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static CompletableFuture either(CompletableFuture<net.minecraft.class_2791> future) {
        return future.handle((chunk, thrown) -> {
            if (thrown == null) {
                return Either.left(chunk);
            }
            Throwable cause = thrown instanceof CompletionException && thrown.getCause() != null ? thrown.getCause() : thrown;
            if (cause instanceof Failed f) {
                return Either.right(f.failure);
            }
            throw thrown instanceof CompletionException ce ? ce : new CompletionException(thrown);
        });
    }

    /** ChunkStatus.generate(executor, level, generator, structures, light, fullChunk, neighbors) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture method_12154(net.minecraft.class_2806 self, Executor executor, net.minecraft.class_3218 level,
            net.minecraft.class_2794 generator, net.minecraft.class_3485 structures, net.minecraft.class_3227 light, Function fullChunk,
            List neighbors) {
        return either(self.method_12154(new net.minecraft.class_9312(level, generator, structures, light), executor, toFullChunk(fullChunk),
            neighbors));
    }

    /** ChunkStatus.load(level, structures, light, fullChunk, chunk) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture method_20612(net.minecraft.class_2806 self, net.minecraft.class_3218 level, net.minecraft.class_3485 structures,
            net.minecraft.class_3227 light, Function fullChunk, net.minecraft.class_2791 chunk) {
        net.minecraft.class_9312 context = new net.minecraft.class_9312(level, level.method_14178().method_12129(), structures, light);
        return either(self.method_20612(context, toFullChunk(fullChunk), chunk));
    }

    private CbBlockChunks() {
    }
}
