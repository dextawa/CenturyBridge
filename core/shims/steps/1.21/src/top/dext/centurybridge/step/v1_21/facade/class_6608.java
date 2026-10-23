package top.dext.centurybridge.step.v1_21.facade;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import net.minecraft.class_156;
import net.minecraft.class_1923;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_2821;
import net.minecraft.class_2902;
import net.minecraft.class_3215;
import net.minecraft.class_3218;
import net.minecraft.class_3846;
import net.minecraft.class_9312;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_21.rt.CbLevelChunkGen;
import top.dext.centurybridge.step.v1_21.rt.CbLevelChunks;

/**
 * 1.20.6's ResetChunksCommand (net/minecraft/class_6608): /resetchunks [range [skipOldChunks]] (permission 2), the
 * developer command that clears the chunks around the source and regenerates them from biomes to light
 * initialisation with a reloaded generator. 1.21 removed it; a 1.20.6 mod's references are renamed here
 * (classRenames, s6fin-level.json). 1.20.6's body, its chunk generation through the step's chunk-list bridge
 * (CbLevelChunkGen) and its generator reload through the given-back ChunkMap.debugReloadGenerator.
 */
public class class_6608 {
    public static final Logger field_34882 = LogUtils.getLogger();

    public class_6608() {
    }

    /** register(dispatcher) */
    public static void method_38623(CommandDispatcher<class_2168> dispatcher) {
        dispatcher.register(class_2170.method_9247("resetchunks")
            .requires(source -> source.method_9259(2))
            .executes(context -> method_38626(context.getSource(), 0, true))
            .then(class_2170.method_9244("range", IntegerArgumentType.integer(0, 5))
                .executes(context -> method_38626(context.getSource(), IntegerArgumentType.getInteger(context, "range"), true))
                .then(class_2170.method_9244("skipOldChunks", BoolArgumentType.bool())
                    .executes(context -> method_38626(context.getSource(), IntegerArgumentType.getInteger(context, "range"),
                        BoolArgumentType.getBool(context, "skipOldChunks"))))));
    }

    private static boolean skipped(class_2818 chunk, boolean skipOldChunks) {
        return chunk == null || skipOldChunks && chunk.method_39297();
    }

    /** resetChunks(source, range, skipOldChunks) */
    public static int method_38626(class_2168 source, int range, boolean skipOldChunks) {
        class_3218 level = source.method_9225();
        class_3215 cache = level.method_14178();
        ((CbLevelChunks.LegacyChunkMap) (Object) cache.field_17254).method_37904();
        class_1923 center = new class_1923(class_2338.method_49638(source.method_9222()));
        int minZ = center.field_9180 - range;
        int maxZ = center.field_9180 + range;
        int minX = center.field_9181 - range;
        int maxX = center.field_9181 + range;
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                class_1923 pos = new class_1923(x, z);
                class_2818 chunk = cache.method_12126(x, z, false);
                if (skipped(chunk, skipOldChunks)) {
                    continue;
                }
                for (class_2338 block : class_2338.method_10094(pos.method_8326(), level.method_31607(), pos.method_8328(), pos.method_8327(),
                    level.method_31600() - 1, pos.method_8329())) {
                    level.method_8652(block, class_2246.field_10124.method_9564(), 16);
                }
            }
        }
        class_3846<Runnable> mailbox = class_3846.method_16902(class_156.method_18349(), "worldgen-resetchunks");
        long start = System.currentTimeMillis();
        int count = (range * 2 + 1) * (range * 2 + 1);
        for (class_2806 status : ImmutableList.of(class_2806.field_12794, class_2806.field_12804, class_2806.field_12796, class_2806.field_12801,
            class_2806.field_12795, class_2806.field_44633)) {
            long statusStart = System.currentTimeMillis();
            CompletableFuture<Unit> future = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, mailbox::method_16901);
            class_9312 context = CbLevelChunks.newWorldGenContext(level, cache.method_12129(), level.method_14183(), cache.method_17293());
            for (int z = center.field_9180 - range; z <= center.field_9180 + range; z++) {
                for (int x = center.field_9181 - range; x <= center.field_9181 + range; x++) {
                    class_1923 pos = new class_1923(x, z);
                    class_2818 chunk = cache.method_12126(x, z, false);
                    if (skipped(chunk, skipOldChunks)) {
                        continue;
                    }
                    List<class_2791> neighbours = Lists.newArrayList();
                    int r = Math.max(1, CbLevelChunks.method_12152(status));
                    for (int nz = pos.field_9180 - r; nz <= pos.field_9180 + r; nz++) {
                        for (int nx = pos.field_9181 - r; nx <= pos.field_9181 + r; nx++) {
                            class_2791 neighbour = cache.method_12121(nx, nz, status.method_16560(), true);
                            class_2791 wrapped;
                            if (neighbour instanceof class_2821 imposter) {
                                wrapped = new class_2821(imposter.method_12240(), true);
                            } else if (neighbour instanceof class_2818 levelChunk) {
                                wrapped = new class_2821(levelChunk, true);
                            } else {
                                wrapped = neighbour;
                            }
                            neighbours.add(wrapped);
                        }
                    }
                    future = future.thenComposeAsync(unit -> CbLevelChunkGen.method_12154(status, context, mailbox::method_16901,
                            c -> {
                                throw new UnsupportedOperationException("Not creating full chunks here");
                            }, neighbours)
                        .thenApply(c -> {
                            if (status == class_2806.field_12804) {
                                class_2902.method_16684(c, class_2806.field_51903);
                            }
                            return Unit.INSTANCE;
                        }), mailbox::method_16901);
                }
            }
            source.method_9211().method_18857(future::isDone);
            field_34882.debug(status + " took " + (System.currentTimeMillis() - statusStart) + " ms");
        }
        long blocksStart = System.currentTimeMillis();
        for (int z = center.field_9180 - range; z <= center.field_9180 + range; z++) {
            for (int x = center.field_9181 - range; x <= center.field_9181 + range; x++) {
                class_1923 pos = new class_1923(x, z);
                class_2818 chunk = cache.method_12126(x, z, false);
                if (skipped(chunk, skipOldChunks)) {
                    continue;
                }
                for (class_2338 block : class_2338.method_10094(pos.method_8326(), level.method_31607(), pos.method_8328(), pos.method_8327(),
                    level.method_31600() - 1, pos.method_8329())) {
                    cache.method_14128(block);
                }
            }
        }
        field_34882.debug("blockChanged took " + (System.currentTimeMillis() - blocksStart) + " ms");
        long took = System.currentTimeMillis() - start;
        source.method_9226(() -> method_52037(count, took), true);
        return 1;
    }

    /** the feedback: "%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk" */
    public static class_2561 method_52037(int count, long took) {
        return class_2561.method_43470(String.format(java.util.Locale.ROOT, "%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk",
            count, took, count, (float) took / (float) count));
    }
}
