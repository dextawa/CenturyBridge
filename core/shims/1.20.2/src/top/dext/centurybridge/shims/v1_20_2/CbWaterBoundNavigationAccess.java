package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** WaterBoundPathNavigation's allowBreaching: part of its 1.20.1 canUpdatePath (CbNavigation). */
@Mixin(net.minecraft.class_1412.class)
public interface CbWaterBoundNavigationAccess {

    @Accessor("field_6689")
    boolean cb$allowBreaching();
}
