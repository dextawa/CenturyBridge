package top.dext.centurybridge.step.v1_21_5.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_10192;
import net.minecraft.class_1269;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_3419;
import net.minecraft.class_5712;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;
import top.dext.centurybridge.step.v1_21_5.rt.CbSaddles;

/**
 * Equippable.equipOnTarget(player, target, stack) (class_10192.method_66700), the path 1.21.5's saddle takes where
 * 1.21.4's SaddleItem.interactLivingEntity called the Saddleable (javap -c both):
 * - on Pig / Strider / AbstractHorse whose converted subclass overrides 1.21.4's equipSaddle, the SADDLE equip (1.21.5:
 *   target.setItemSlot(SADDLE, one of the stack)) calls that override with the same stack and NEUTRAL, then the EQUIP
 *   game event, as 1.21.4's SaddleItem did; every other equip runs 1.21.5's call (a native mod's wrapper of the call
 *   still wraps it);
 * - on a mod's own Saddleable (not one of those three), which 1.21.5's component refuses (PASS), a SADDLE equippable
 *   runs 1.21.4's SaddleItem body when the method returns (CbSaddles.useSaddle).
 */
@Mixin(class_10192.class)
public abstract class CbSaddleEquipHooksMixin {

    @WrapOperation(method = "method_66700(Lnet/minecraft/class_1657;Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1269;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1309;method_5673(Lnet/minecraft/class_1304;Lnet/minecraft/class_1799;)V"))
    private void cb$v1_21_5$equipSaddle(class_1309 target, class_1304 slot, class_1799 one, Operation<Void> original) {
        if (CbSaddles.saddleSlot(slot) && CbSaddles.vanillaSaddleable(target) && CbSaddles.overridesEquip(target)) {
            ((Saddleable) target).method_6576(one, class_3419.field_15254);
            target.method_37908().method_43275(target, class_5712.field_28739, target.method_19538());
            return;
        }
        original.call(target, slot, one);
    }

    @Inject(method = "method_66700(Lnet/minecraft/class_1657;Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1269;",
        at = @At("RETURN"), cancellable = true)
    private void cb$v1_21_5$modSaddleable(class_1657 player, class_1309 target, class_1799 stack, CallbackInfoReturnable<class_1269> cir) {
        if (cir.getReturnValue() == class_1269.field_5811 && CbSaddles.saddleSlot(((class_10192) (Object) this).comp_3174())
                && target instanceof Saddleable && !CbSaddles.vanillaSaddleable(target)) {
            cir.setReturnValue(CbSaddles.useSaddle(stack, player, target));
        }
    }
}
