package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_3414;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;
import top.dext.centurybridge.step.v1_21_5.rt.CbSaddles;

/**
 * The sound of a horse's SADDLE equip (AbstractHorse / Camel method_66667) is where 1.21.5 plays what 1.21.4's
 * AbstractHorse.containerChanged played: getSaddleSoundEvent() when a saddle went in (javap -c both). For a converted
 * subclass declaring 1.21.4's getSaddleSoundEvent(), the SADDLE answer is that override's sound when the method returns;
 * other slots and other horses keep 1.21.5's sound.
 */
@Mixin({net.minecraft.class_1496.class, net.minecraft.class_7689.class})
public abstract class CbSaddleSoundHooksMixin {

    @Inject(method = "method_66667(Lnet/minecraft/class_1304;Lnet/minecraft/class_1799;Lnet/minecraft/class_10192;)Lnet/minecraft/class_6880;",
        at = @At("RETURN"), cancellable = true)
    private void cb$v1_21_5$saddleSound(class_1304 slot, class_1799 stack, class_10192 equippable, CallbackInfoReturnable<class_6880<class_3414>> cir) {
        Object self = this;
        if (CbSaddles.saddleSlot(slot) && self instanceof Saddleable s && CbSaddles.overrides(self, "method_45328")) {
            cir.setReturnValue(class_6880.method_40223(s.method_45328()));
        }
    }
}
