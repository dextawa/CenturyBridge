package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1534;
import net.minecraft.class_1535;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPaintings;

/**
 * Painting.setVariant(holder): a holder of 1.20.6's painting variant registry (CbEntityPaintings' stand-in) becomes
 * the world's holder of the same name, the only kind 1.21 can sync and save. The constructor that takes a variant
 * sets it through here too.
 */
@Mixin(class_1534.class)
public abstract class CbEntityPaintingMixin {

    @ModifyVariable(method = "method_43402(Lnet/minecraft/class_6880;)V", at = @At("HEAD"), argsOnly = true)
    private class_6880<class_1535> cb$worldHolder(class_6880<class_1535> variant) {
        return CbEntityPaintings.worldHolder(variant);
    }
}
