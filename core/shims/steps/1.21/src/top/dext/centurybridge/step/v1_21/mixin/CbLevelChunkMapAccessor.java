package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ChunkMap's main thread mailbox, which 1.21's WorldGenContext carries (CbLevelChunks.newWorldGenContext) */
@Mixin(net.minecraft.class_3898.class)
public interface CbLevelChunkMapAccessor {

    @Accessor("field_17226")
    net.minecraft.class_3906<net.minecraft.class_3900.class_3946<Runnable>> cb$mainThreadMailbox();
}
