package cb.swalln.mixin;

import cb.swalln.Driver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 26.1's LivingEntity.getAttackRangeWith: an entity tagged "cbswalln" answers Driver.NATIVE_RANGE. */
@Mixin(LivingEntity.class)
public abstract class AttackRangeMixin {
    @Inject(method = "getAttackRangeWith(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/component/AttackRange;",
        remap = false, at = @At("RETURN"), cancellable = true)
    private void cbswalln$range(ItemStack stack, CallbackInfoReturnable<AttackRange> cir) {
        if (((LivingEntity) (Object) this).entityTags().contains("cbswalln")) {
            cir.setReturnValue(Driver.NATIVE_RANGE);
        }
    }
}
