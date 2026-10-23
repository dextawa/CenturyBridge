package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPotions;

/**
 * Potion: 1.20.4 asked a potion its name through the virtual getName(prefix), so a mod's potion class
 * could answer its own; 1.20.5 names a potion through a static of its holder. A 1.20.4 class's own
 * getName answers that static for its potion again (rt/CbPotions), and the name field is readable for
 * 1.20.4's body of the method.
 */
@Mixin(net.minecraft.class_1842.class)
public abstract class CbPotionNameMixin implements CbPotions.NameField {

    /** name */
    @Shadow
    private String field_8954;

    @Override
    public String cbS4$name() {
        return field_8954;
    }

    /** getName(potion, prefix) */
    @Inject(method = "method_8051", at = @At("HEAD"), cancellable = true)
    private static void cbS4$overriddenName(java.util.Optional<net.minecraft.class_6880<net.minecraft.class_1842>> potion, String prefix,
                                  CallbackInfoReturnable<String> cir) {
        String own = CbPotions.overriddenName(potion, prefix);
        if (own != null) {
            cir.setReturnValue(own);
        }
    }
}
