package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_3414;
import net.minecraft.class_4059;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;

/**
 * The injectors into 1.21.2's own AnimalArmorItem constructors that go with {@link CbItemAnimalArmorMixin} (its restored
 * 1.21.1 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them):
 * the material and body type each constructor was given, kept for them.
 */
@Mixin(class_4059.class)
public abstract class CbItemAnimalArmorMixinHooks {

    @Inject(method = "<init>(Lnet/minecraft/class_1741;Lnet/minecraft/class_4059$class_9076;Lnet/minecraft/class_1792$class_1793;)V",
        at = @At("RETURN"))
    private void cb$keep(class_1741 material, class_4059.class_9076 bodyType, class_1792.class_1793 props, CallbackInfo ci) {
        ((CbPrioritySplit.AnimalArmorKeep) (Object) this).cb$v1_21_2$keepAnimalArmor(material, bodyType);
    }

    @Inject(method = "<init>(Lnet/minecraft/class_1741;Lnet/minecraft/class_4059$class_9076;Lnet/minecraft/class_6880;ZLnet/minecraft/class_1792$class_1793;)V",
        at = @At("RETURN"))
    private void cb$keep(class_1741 material, class_4059.class_9076 bodyType, class_6880<class_3414> equipSound, boolean damageOnHurt,
                         class_1792.class_1793 props, CallbackInfo ci) {
        ((CbPrioritySplit.AnimalArmorKeep) (Object) this).cb$v1_21_2$keepAnimalArmor(material, bodyType);
    }
}
