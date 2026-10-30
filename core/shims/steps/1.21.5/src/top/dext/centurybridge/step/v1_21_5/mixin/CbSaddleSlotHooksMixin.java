package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1304;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;
import top.dext.centurybridge.step.v1_21_5.rt.CbSaddles;

/**
 * canUseSlot(SADDLE) (method_56991) of Pig, Strider and AbstractHorse is where 1.21.5 asks what 1.21.4 asked the
 * Saddleable's isSaddleable() (the saddle's Equippable through isEquippableInSlot, the dispenser through
 * canEquipWithDispenser; javap -c 1.21.5 class_10192 / class_10205 / class_1309). For a converted subclass declaring
 * 1.21.4's isSaddleable() below the restoring vanilla class, the SADDLE answer is that override's when the method
 * returns; other slots and other classes keep 1.21.5's answer.
 */
@Mixin({net.minecraft.class_1452.class, net.minecraft.class_4985.class, net.minecraft.class_1496.class})
public abstract class CbSaddleSlotHooksMixin {

    @Inject(method = "method_56991(Lnet/minecraft/class_1304;)Z", at = @At("RETURN"), cancellable = true)
    private void cb$v1_21_5$canUseSaddle(class_1304 slot, CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if (CbSaddles.saddleSlot(slot) && self instanceof Saddleable s && CbSaddles.overrides(self, "method_6765")) {
            cir.setReturnValue(s.method_6765());
        }
    }
}
