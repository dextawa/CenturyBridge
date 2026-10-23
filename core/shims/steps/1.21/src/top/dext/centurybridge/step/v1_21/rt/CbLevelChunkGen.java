package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;

import com.google.common.collect.ImmutableList;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2839;
import net.minecraft.class_8563;
import net.minecraft.class_9259;
import net.minecraft.class_9312;
import net.minecraft.class_9761;
import net.minecraft.class_9762;
import net.minecraft.class_9767;
import net.minecraft.class_9768;
import net.minecraft.class_9770;
import top.dext.centurybridge.step.v1_21.facade.class_9311;
import top.dext.centurybridge.step.v1_21.mixin.CbLevelGenHolderAccessor;

/**
 * 1.20.6's chunk-list generation API -- ChunkStatus.generate / load and the WorldGenRegion over a square list of
 * chunks -- on 1.21's chunk steps, which read their neighbours from a StaticCache2D of generation chunk holders
 * instead. A 1.20.6 list becomes such a cache of {@link Fixed} holders, each holding its chunk at every status the
 * chunk has reached, so 1.21's region and steps see exactly the chunks (and statuses) 1.20.6's saw.
 */
public final class CbLevelChunkGen {

    private CbLevelChunkGen() {
    }

    /**
     * A generation chunk holder standing for one chunk of a 1.20.6 list: its futures are complete with the chunk
     * for every status up to the chunk's own, as a live holder's are once the chunk reached that status; none
     * where the list has no chunk. The ticket and queue levels are those of a holder generating to the chunk's
     * status (ChunkLevel.byStatus); nothing on these paths reads them.
     */
    public static final class Fixed extends class_9761 {
        private final int level;

        Fixed(class_1923 pos, class_2791 chunk) {
            super(pos);
            if (chunk == null) {
                this.level = class_8563.field_44849;
                return;
            }
            class_2806 status = chunk.method_12009();
            this.level = class_8563.method_51829(status);
            AtomicReferenceArray<CompletableFuture<class_9259<class_2791>>> futures = ((CbLevelGenHolderAccessor) (Object) this).cb$futures();
            CompletableFuture<class_9259<class_2791>> done = CompletableFuture.completedFuture(class_9259.method_57124(chunk));
            for (int i = 0; i <= status.method_16559(); i++) {
                futures.set(i, done);
            }
        }

        @Override
        public int method_14005() {
            return this.level;
        }

        @Override
        public int method_17208() {
            return this.level;
        }
    }

    /** the radius of a 1.20.6 square chunk list (side 2r + 1) */
    private static int radiusOf(List<class_2791> chunks) {
        int side = (int) Math.floor(Math.sqrt(chunks.size()));
        if (side * side != chunks.size()) {
            throw new IllegalStateException("Cache size is not a square.");
        }
        return side / 2;
    }

    /**
     * The 1.21 cache of a 1.20.6 list (row by row from its first chunk: index dx + dz * side, as 1.20.6's
     * WorldGenRegion read it), centred on the middle chunk, out to {@code radius} -- positions past the list get
     * an empty holder, which answers no chunk, as 1.20.6's region had none there.
     */
    static class_9762<class_9761> cacheOf(List<class_2791> chunks, int radius) {
        int listRadius = radiusOf(chunks);
        int side = listRadius * 2 + 1;
        class_1923 first = chunks.get(0).method_12004();
        class_1923 center = chunks.get(chunks.size() / 2).method_12004();
        return class_9762.method_60483(center.field_9181, center.field_9180, radius, (x, z) -> {
            int dx = x - first.field_9181;
            int dz = z - first.field_9180;
            class_2791 chunk = dx >= 0 && dz >= 0 && dx < side && dz < side ? chunks.get(dx + dz * side) : null;
            return new Fixed(new class_1923(x, z), chunk);
        });
    }

    /** 1.20.6 status.generate(context, executor, toFull, neighbours): the status's generation over the list */
    public static CompletableFuture<class_2791> method_12154(class_2806 status, class_9312 context, Executor executor, class_9311 toFull,
                                                          List<class_2791> neighbours) {
        class_2791 chunk = neighbours.get(neighbours.size() / 2);
        if (status == class_2806.field_12803) {
            // 1.20.6's FULL generation task was toFull.apply(chunk), its status marked like every other's
            return toFull.apply(chunk).thenApply(c -> {
                if (c instanceof class_2839 proto && !proto.method_12009().method_12165(status)) {
                    proto.method_12308(status);
                }
                return c;
            });
        }
        // the executor ran 1.20.6's continuations (the noise fill's lock release); 1.21's steps have none
        class_9770 step = class_9768.field_51900.method_60518(status);
        int radius = Math.max(radiusOf(neighbours), step.comp_2804().method_60517());
        return step.method_60560(context, cacheOf(neighbours, radius), chunk);
    }

    /** 1.20.6 status.load(context, toFull, chunk): the status's loading task -- toFull for FULL, else 1.21's loading step */
    public static CompletableFuture<class_2791> method_20612(class_2806 status, class_9312 context, class_9311 toFull, class_2791 chunk) {
        if (status == class_2806.field_12803) {
            return toFull.apply(chunk);
        }
        class_9770 step = class_9768.field_51901.method_60518(status);
        return step.method_60560(context, cacheOf(List.of(chunk), step.comp_2804().method_60517()), chunk);
    }

    /**
     * new WorldGenRegion(level, chunks, status, writeRadius) of 1.20.6 (and a subclass's super call): 1.21's
     * (level, cache, step, center) -- the list's cache ({@link #regionCache}), a step of that status writing
     * blocks within 1.20.6's {@code writeRadius} and reading every chunk of the list at whatever status it has
     * ({@link #regionStep}), the list's middle chunk ({@link #regionCenter}).
     */
    public static class_9762<class_9761> regionCache(List<class_2791> chunks) {
        return cacheOf(chunks, radiusOf(chunks));
    }

    /**
     * 1.20.6's region checked only a chunk's own status against the requested one: dependencies of FULL at every
     * distance of the list leave that check to the holders, which hold each chunk up to its status.
     */
    public static class_9770 regionStep(List<class_2791> chunks, class_2806 status, int writeRadius) {
        int radius = radiusOf(chunks);
        ImmutableList.Builder<class_2806> any = ImmutableList.builder();
        for (int i = 0; i <= radius; i++) {
            any.add(class_2806.field_12803);
        }
        class_9770 base = class_9768.field_51900.method_60518(status);
        return new class_9770(status, new class_9767(any.build()), base.comp_2805(), writeRadius, base.comp_2807());
    }

    public static class_2791 regionCenter(List<class_2791> chunks) {
        return chunks.get(chunks.size() / 2);
    }
}
