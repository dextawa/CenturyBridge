package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * GenerationChunkHolder's per-status futures and its NOT_DONE_YET marker: 1.20.6's ChunkHolder kept the same
 * array (one future per chunk status, by status index) and handed it out through getFutureIfPresentUnchecked;
 * 1.21 moved it up into GenerationChunkHolder and keeps it private (CbLevelChunks).
 */
@Mixin(net.minecraft.class_9761.class)
public interface CbLevelGenHolderAccessor {

    @Accessor("field_51872")
    java.util.concurrent.atomic.AtomicReferenceArray<java.util.concurrent.CompletableFuture<net.minecraft.class_9259<net.minecraft.class_2791>>> cb$futures();

    @Accessor("field_51869")
    static net.minecraft.class_9259<net.minecraft.class_2791> cb$notDoneYet() {
        throw new AssertionError();
    }
}
