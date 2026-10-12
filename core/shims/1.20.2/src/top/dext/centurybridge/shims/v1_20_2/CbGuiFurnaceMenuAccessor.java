package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The furnace menu's synced numbers (fuel, fuel total, cook, cook total), for 1.20.1's integer progress (CbGuiFurnace). */
@Mixin(net.minecraft.class_1720.class)
public interface CbGuiFurnaceMenuAccessor {

    @Accessor("field_17286")
    net.minecraft.class_3913 cb$data();
}
