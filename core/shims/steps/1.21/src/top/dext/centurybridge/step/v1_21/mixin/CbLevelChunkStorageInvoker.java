package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ChunkStorage.storageInfo(), which 1.21's ChunkSerializer.read takes (CbLevelChunks.storageOf) */
@Mixin(net.minecraft.class_3977.class)
public interface CbLevelChunkStorageInvoker {

    @Invoker("method_60999")
    net.minecraft.class_9240 cb$storageInfo();
}
