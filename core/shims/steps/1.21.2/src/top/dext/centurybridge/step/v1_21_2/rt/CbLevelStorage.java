package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_1923;
import net.minecraft.class_2487;
import net.minecraft.class_2791;
import net.minecraft.class_2839;
import net.minecraft.class_2852;
import net.minecraft.class_2509;
import net.minecraft.class_3218;
import net.minecraft.class_3977;
import net.minecraft.class_4153;
import net.minecraft.class_5455;
import net.minecraft.class_9240;
import net.minecraft.class_9312;
import top.dext.centurybridge.step.v1_21_2.facade.ChunkTaskPriorityQueueSorter;
import top.dext.centurybridge.step.v1_21_2.facade.ProcessorHandle;

/**
 * 1.21.1's chunk storage API on 1.21.2.
 * <ul>
 * <li>SimpleRegionStorage.write(pos, tag): 1.21.2 takes a supplier of the tag (computed on the IO thread); the tag
 *     1.21.1 was handed is what it supplies.</li>
 * <li>SectionStorage's 1.21.1 constructor took a codec of the section type made per section with its setDirty
 *     callback; 1.21.2's takes a codec of a packed form and the conversions between it and the section. The packed
 *     form here is the section's serialized data as it was read (a Dynamic): it decodes only when 1.21.1's codec
 *     parses it (a section that does not parse is logged and absent, as 1.21.1's read left it), and unpacks with
 *     1.21.1's codec made for the section's callback; packing encodes with 1.21.1's codec through the registry
 *     access 1.21.1's storage serialized with.</li>
 * <li>ChunkSerializer (1.21.1's static chunk reader/writer; 1.21.2 reuses its intermediary name for
 *     SerializableChunkData): read(level, poi, info, pos, tag) is 1.21.2's parse(level, registries, tag).read(level,
 *     poi, info, pos); write(level, chunk) is copyOf(level, chunk).write().</li>
 * <li>WorldGenContext: 1.21.1's main-thread mailbox is 1.21.2's main-thread executor (CbRestExecutors, both ways);
 *     the unsaved listener 1.21.2 adds is the level's ChunkMap's own, the one its WorldGenContext carries.</li>
 * </ul>
 */
public final class CbLevelStorage {

    // ---- region storage ----

    /** a 1.21.1 write's tag, as the supplier 1.21.2's write takes */
    public static Supplier<class_2487> supplier(class_2487 tag) {
        return () -> tag;
    }

    public interface LegacyWrite {
        CompletableFuture<Void> method_17910(class_1923 pos, class_2487 tag);
    }

    public interface SuperWrite {
        CompletableFuture<Void> cb$super$method_17910(class_1923 pos, Supplier<class_2487> tag);
    }

    /** vanilla's write(pos, supplier) of a 1.21.1 storage: its override, with the supplied tag */
    public static CompletableFuture<Void> write(LegacyWrite self, class_1923 pos, Supplier<class_2487> tag) {
        return self.method_17910(pos, tag.get());
    }

    public static CompletableFuture<Void> superWrite(SuperWrite self, class_1923 pos, class_2487 tag) {
        return self.cb$super$method_17910(pos, supplier(tag));
    }

    // ---- section storage ----

    private static final Runnable CLEAN = () -> {
    };

    /** 1.21.2's packed codec: the section's data, decoding only when 1.21.1's codec parses it */
    public static <R> Codec<Dynamic<?>> packedCodec(Function<Runnable, Codec<R>> codec) {
        return Codec.PASSTHROUGH.validate(d -> codec.apply(CLEAN).parse(d).map(r -> d));
    }

    /** 1.21.2's packer: a section encoded with 1.21.1's codec, through the storage's registry access */
    public static <R> Function<R, Dynamic<?>> packer(Function<Runnable, Codec<R>> codec, class_5455 registries) {
        return r -> {
            var ops = registries.method_57093(class_2509.field_11560);
            return new Dynamic<>(ops, codec.apply(CLEAN).encodeStart(ops, r).getOrThrow());
        };
    }

    /** 1.21.2's unpacker: the data parsed with 1.21.1's codec made for the section's setDirty callback */
    public static <R> BiFunction<Dynamic<?>, Runnable, R> unpacker(Function<Runnable, Codec<R>> codec) {
        return (d, setDirty) -> codec.apply(setDirty).parse(d).getOrThrow();
    }

    // ---- ChunkSerializer ----

    /** ChunkSerializer.read(level, poi, regionStorageInfo, pos, tag) */
    public static class_2839 method_12395(class_3218 level, class_4153 poi, class_9240 info, class_1923 pos, class_2487 tag) {
        return class_2852.method_61794(level, level.method_30349(), tag).method_12395(level, poi, info, pos);
    }

    /** ChunkSerializer.write(level, chunk) */
    public static class_2487 method_12410(class_3218 level, class_2791 chunk) {
        return class_2852.method_61793(level, chunk).method_12410();
    }

    // ---- WorldGenContext ----

    /** WorldGenContext.mainThreadMailBox() */
    public static ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> comp_2808(class_9312 self) {
        return CbRestExecutors.handle(self.comp_2942());
    }

    /** the main-thread executor for a 1.21.1 mailbox */
    public static Executor executor(ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> mailbox) {
        return CbRestExecutors.executor(mailbox);
    }

    /** the unsaved listener of a level's chunks: its ChunkMap's (CbLevelChunkMapAccess) */
    public static net.minecraft.class_2818.class_10368 unsavedListener(class_3218 level) {
        return ((ChunkMapState) level.method_14178().field_17254).cb$worldGenContext().comp_3323();
    }

    /** ChunkMap's WorldGenContext (CbLevelChunkMapAccess) */
    public interface ChunkMapState {
        class_9312 cb$worldGenContext();
    }

    private CbLevelStorage() {
    }
}
