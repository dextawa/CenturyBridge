package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestEntityData;

/** a SynchedEntityData's items, for 1.20.4's defineSynchedData() on a constructed entity (CbRestEntityData) */
@Mixin(net.minecraft.class_2945.class)
public interface CbRestEntityDataAccessor extends CbRestEntityData.Items {

    @Override
    @Accessor("field_13331")
    net.minecraft.class_2945.class_2946<?>[] cbRest$items();
}
