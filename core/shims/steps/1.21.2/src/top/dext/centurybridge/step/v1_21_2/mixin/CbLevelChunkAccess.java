package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelChunks;

/** ChunkAccess.blendingData, for 1.21.1's setBlendingData (CbLevelChunks) */
@Mixin(net.minecraft.class_2791.class)
public interface CbLevelChunkAccess extends CbLevelChunks.Fields {

    @Override
    @Accessor("field_35468")
    void cb$blendingData(net.minecraft.class_6749 data);
}
