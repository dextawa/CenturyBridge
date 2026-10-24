package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1842;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemPotions;

/**
 * A Potion built by 1.21.1's Potion(effects) constructor has no name; 1.21.1 named it by its registry path (Potion.getName),
 * so its name() answers that path (CbItemPotions). A named potion answers its name, as 1.21.2's does.
 */
@Mixin(class_1842.class)
public abstract class CbItemPotionNameMixin {

    @Inject(method = "method_63990()Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void cb$registryName(CallbackInfoReturnable<String> cir) {
        if (((class_1842) (Object) this).field_8954 == null) {
            cir.setReturnValue(CbItemPotions.registryName((class_1842) (Object) this));
        }
    }
}
