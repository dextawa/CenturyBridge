package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelStorage;

/** ChunkMap's WorldGenContext, whose unsaved listener a 1.21.1 WorldGenContext takes (CbLevelStorage) */
@Mixin(net.minecraft.class_3898.class)
public interface CbLevelChunkMapAccess extends CbLevelStorage.ChunkMapState {

    @Override
    @Accessor("field_51861")
    net.minecraft.class_9312 cb$worldGenContext();
}
