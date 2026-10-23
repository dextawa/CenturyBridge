package top.dext.centurybridge.step.v1_21.facade;

import java.util.concurrent.CompletableFuture;

/**
 * 1.20.6's ToFullChunk (net/minecraft/class_9311): what turns a chunk at the spawn status into a full one, handed
 * to the FULL status's generation and loading tasks. 1.21 removed it (its full-chunk step reaches the chunk map
 * itself); a 1.20.6 mod's implementations and calls are renamed here (classRenames, s6fin-level.json) and its
 * ChunkStatus.generate / load calls hand it on as 1.20.6 did (CbLevelChunkGen).
 */
@FunctionalInterface
public interface class_9311 {
    CompletableFuture<net.minecraft.class_2791> apply(net.minecraft.class_2791 chunk);
}
