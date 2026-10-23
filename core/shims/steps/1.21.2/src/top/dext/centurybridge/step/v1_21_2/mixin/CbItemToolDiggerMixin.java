package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1766;
import net.minecraft.class_9886;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemTiers;

/**
 * 1.21.2's DiggerItem keeps the material it was built with (1.21.2 drops it after putting it into the components):
 * 1.21.1's TieredItem.getTier() reads it (CbItemTiers).
 */
@Mixin(class_1766.class)
public abstract class CbItemToolDiggerMixin implements CbItemTiers.ToolFields {

    @Unique
    private class_9886 cb$material;

    @Inject(method = "<init>(Lnet/minecraft/class_9886;Lnet/minecraft/class_6862;FFLnet/minecraft/class_1792$class_1793;)V", at = @At("RETURN"))
    private void cb$keep(net.minecraft.class_9886 material, net.minecraft.class_6862<net.minecraft.class_2248> mineable, float attackDamage, float attackSpeed, net.minecraft.class_1792.class_1793 props, CallbackInfo ci) {
        this.cb$material = material;
    }

    @Override
    public class_9886 cb$toolMaterial() {
        return cb$material;
    }
}
