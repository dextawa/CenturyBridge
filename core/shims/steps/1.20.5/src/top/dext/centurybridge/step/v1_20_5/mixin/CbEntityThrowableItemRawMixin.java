package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** ThrowableItemProjectile: 1.20.4's protected getItemRaw() -- the synched item stack itself -- which 1.20.5 dropped */
@Mixin(net.minecraft.class_3857.class)
public abstract class CbEntityThrowableItemRawMixin {

    @Shadow
    @Final
    private static net.minecraft.class_2940<net.minecraft.class_1799> field_17082;

    /** was getItemRaw() */
    protected net.minecraft.class_1799 method_16943() {
        return ((net.minecraft.class_1297) (Object) this).method_5841().method_12789(field_17082);
    }
}
