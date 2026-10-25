package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallAttackRange;

/**
 * 1.21.11 LivingEntity.entityAttackRange() (class_1309.method_76693): the ATTACK_RANGE of the active item, or the
 * entity's default. 1.21.11's vanilla read it for the active item (Player.isWithinAttackRange, the kinetic and piercing
 * weapons); 26.1 asks getAttackRangeWith(stack) for the weapon's stack. A range asked for the active item goes
 * through entityAttackRange, so a mod's override of it answers where 1.21.11's did.
 */
@Mixin(LivingEntity.class)
public abstract class CbWallLivingEntityAttackRangeMixin implements CbWallAttackRange {

    @Shadow
    public abstract ItemStack getActiveItem();

    @CbVanillaReads("getAttackRangeWith: the range of the active item is answered here")
    @Override
    public AttackRange entityAttackRange() {
        AttackRange range = getActiveItem().get(DataComponents.ATTACK_RANGE);
        return range != null ? range : AttackRange.defaultFor((LivingEntity) (Object) this);
    }

    @Inject(method = "getAttackRangeWith", at = @At("HEAD"), cancellable = true)
    private void cb$entityAttackRange(ItemStack stack, CallbackInfoReturnable<AttackRange> cir) {
        if (stack == getActiveItem()) {
            cir.setReturnValue(entityAttackRange());
        }
    }
}
