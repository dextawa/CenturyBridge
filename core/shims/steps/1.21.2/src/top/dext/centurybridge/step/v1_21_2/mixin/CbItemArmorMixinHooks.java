package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_8051;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;

/**
 * The injector into 1.21.2's own ArmorItem constructor that goes with {@link CbItemArmorMixin} (its restored 1.21.1
 * members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them): the
 * material and type the constructor was given, kept for them.
 */
@Mixin(class_1738.class)
public abstract class CbItemArmorMixinHooks {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$keep(class_1741 material, class_8051 type, class_1792.class_1793 props, CallbackInfo ci) {
        ((CbPrioritySplit.ArmorKeep) (Object) this).cb$v1_21_2$keepArmor(material, type);
    }
}
