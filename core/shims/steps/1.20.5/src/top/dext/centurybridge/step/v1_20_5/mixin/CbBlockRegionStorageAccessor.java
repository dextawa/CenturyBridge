package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockChunks;

/** SimpleRegionStorage's private data fixer, for CbBlockChunks */
@Mixin(net.minecraft.class_9172.class)
public interface CbBlockRegionStorageAccessor extends CbBlockChunks.RegionFixer {

    @Override
    @Accessor("field_48761")
    com.mojang.datafixers.DataFixer cb$fixerUpper();
}
