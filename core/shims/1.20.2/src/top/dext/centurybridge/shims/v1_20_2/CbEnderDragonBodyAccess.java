package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EnderDragon's body part: 1.20.1's getPassengersRidingOffset was its height (CbRiding). */
@Mixin(net.minecraft.class_1510.class)
public interface CbEnderDragonBodyAccess {

    @Accessor("field_7023")
    net.minecraft.class_1508 cb$body();
}
