package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockChunks;

/** EntityStorage's private region storage, for CbBlockChunks */
@Mixin(net.minecraft.class_5565.class)
public interface CbBlockEntityStorageAccessor extends CbBlockChunks.EntityRegions {

    @Override
    @Accessor("field_48747")
    net.minecraft.class_9172 cb$simpleRegionStorage();
}
