package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_2561;
import net.minecraft.class_304;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbKeyCategories;

/**
 * A key category made from a 1.21.8 category string (CbKeyCategories) displays as that string's translation, as the
 * 1.21.8 controls screen showed it (1.21.9's Category.label() would translate "key.category.centurybridge_legacy...",
 * which no language file has). Every other category keeps 1.21.9's label.
 */
@Mixin(class_304.class_11900.class)
public abstract class CbKeyCategoryNameMixin {

    @Inject(method = "method_74697()Lnet/minecraft/class_2561;", at = @At("HEAD"), cancellable = true)
    private void cb$legacyLabel(CallbackInfoReturnable<class_2561> cir) {
        String legacy = CbKeyCategories.legacyName((class_304.class_11900) (Object) this);
        if (legacy != null) {
            cir.setReturnValue(class_2561.method_43471(legacy));
        }
    }
}
