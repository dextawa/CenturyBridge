package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Objects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallAttackRange;

/**
 * 26.1's LivingEntity.getAttackRangeWith(stack) asked for the active item reads the restored entityAttackRange()
 * (CbWallLivingEntityAttackRangeMixin) after its own body ran: 26.1's answer stands unless entityAttackRange answers
 * otherwise than 1.21.11's body (rt/CbWallAttackRange.unmixed; a 1.21.11 mod mixed it or a 1.21.11 subclass overrides
 * it) -- then that answer. 26.1's body and whatever a 26.1 mod mixed into it run unchanged. Default priority.
 */
@Mixin(LivingEntity.class)
public abstract class CbWallAttackRangeReadsMixin {

    @Inject(method = "getAttackRangeWith(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/component/AttackRange;",
        at = @At("RETURN"), cancellable = true)
    private void cb$entityAttackRange(ItemStack stack, CallbackInfoReturnable<AttackRange> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (stack != self.getActiveItem()) {
            return;
        }
        AttackRange answer = ((CbWallAttackRange) self).entityAttackRange();
        if (answer != null && !Objects.equals(answer, CbWallAttackRange.unmixed(self))) {
            cir.setReturnValue(answer);
        }
    }
}
