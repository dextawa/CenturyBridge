package top.dext.centurybridge.step.v1_21_5.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_10192;
import net.minecraft.class_10205;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_2342;
import net.minecraft.class_3419;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;
import top.dext.centurybridge.step.v1_21_5.rt.CbSaddles;

/**
 * EquipmentDispenseItemBehavior.dispenseEquipment(source, stack) (class_10205.method_64106), the path a dispensed
 * 1.21.5 saddle takes where 1.21.4's dispenser saddle behaviour (class_2357$10) called the Saddleable (javap -c both):
 * - on Pig / Strider / AbstractHorse whose converted subclass overrides 1.21.4's equipSaddle, the SADDLE equip calls
 *   that override with the same stack and BLOCKS (1.21.4's source); every other equip runs 1.21.5's call;
 * - when 1.21.5 finds no entity to equip, a SADDLE equippable goes to the first mod's own Saddleable in front of the
 *   dispenser that 1.21.4's behaviour would have saddled (CbSaddles.dispenseSaddle).
 */
@Mixin(class_10205.class)
public abstract class CbSaddleDispenseHooksMixin {

    @WrapOperation(method = "method_64106(Lnet/minecraft/class_2342;Lnet/minecraft/class_1799;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1309;method_5673(Lnet/minecraft/class_1304;Lnet/minecraft/class_1799;)V"))
    private static void cb$v1_21_5$dispenseSaddle(class_1309 target, class_1304 slot, class_1799 one, Operation<Void> original) {
        if (CbSaddles.saddleSlot(slot) && CbSaddles.vanillaSaddleable(target) && CbSaddles.overridesEquip(target)) {
            ((Saddleable) target).method_6576(one, class_3419.field_15245);
            return;
        }
        original.call(target, slot, one);
    }

    @Inject(method = "method_64106(Lnet/minecraft/class_2342;Lnet/minecraft/class_1799;)Z", at = @At("RETURN"), cancellable = true)
    private static void cb$v1_21_5$modSaddleable(class_2342 source, class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        class_10192 eq = stack.method_58694(class_9334.field_54196);
        if (!cir.getReturnValue() && eq != null && CbSaddles.saddleSlot(eq.comp_3174()) && CbSaddles.dispenseSaddle(source, stack)) {
            cir.setReturnValue(true);
        }
    }
}
