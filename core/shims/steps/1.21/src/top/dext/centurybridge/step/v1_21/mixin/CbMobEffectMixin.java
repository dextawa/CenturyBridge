package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbMobEffectTemplates;

/**
 * MobEffect (class_1291): its two asks of an AttributeTemplate for a modifier go through the restored 1.20.6
 * create(name, amplifier) (CbMobEffectTemplateMixin) with the name 1.20.6 passed, the effect's description id:
 * addAttributeModifiers (method_5555, applying the effect) and createModifiers (method_55650, whose per-template step
 * method_55651 is static in 1.21 and no longer knows the effect; createModifiers records it while it runs). With
 * nothing mixed into create the modifiers are 1.21's.
 */
@Mixin(net.minecraft.class_1291.class)
public abstract class CbMobEffectMixin {

    @Redirect(method = "method_5555(Lnet/minecraft/class_5131;I)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1291$class_8634;method_52522(I)Lnet/minecraft/class_1322;"))
    private net.minecraft.class_1322 cb$create(net.minecraft.class_1291.class_8634 template, int amplifier) {
        return CbMobEffectTemplates.create(template, (net.minecraft.class_1291) (Object) this, amplifier);
    }

    @Inject(method = "method_55650(ILjava/util/function/BiConsumer;)V", at = @At("HEAD"))
    private void cb$beginModifiers(int amplifier, java.util.function.BiConsumer<?, ?> consumer, CallbackInfo ci) {
        CbMobEffectTemplates.begin((net.minecraft.class_1291) (Object) this);
    }

    @Inject(method = "method_55650(ILjava/util/function/BiConsumer;)V", at = @At("RETURN"))
    private void cb$endModifiers(int amplifier, java.util.function.BiConsumer<?, ?> consumer, CallbackInfo ci) {
        CbMobEffectTemplates.end();
    }

    @Redirect(method = "method_55651(Ljava/util/function/BiConsumer;ILnet/minecraft/class_6880;Lnet/minecraft/class_1291$class_8634;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1291$class_8634;method_52522(I)Lnet/minecraft/class_1322;"))
    private static net.minecraft.class_1322 cb$createStatic(net.minecraft.class_1291.class_8634 template, int amplifier) {
        return CbMobEffectTemplates.create(template, CbMobEffectTemplates.creating(), amplifier);
    }
}
