package cb.swall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.11's LivingEntity.entityAttackRange() (class_1309.method_76693), as 1.21.11-era mods mixed it: an entity
 * tagged "cbswallv" answers the range (1, 2, 3, 4, 0.25, 1). On 26.1 getAttackRangeWith(active item) takes it
 * (cb-swalln's Driver checks).
 */
@Mixin(net.minecraft.class_1309.class)
public abstract class EntityAttackRangeMixin {
    @Inject(method = "method_76693()Lnet/minecraft/class_12392;", at = @At("RETURN"), cancellable = true, remap = false)
    private void cbswall$range(CallbackInfoReturnable<net.minecraft.class_12392> cir) {
        if (((net.minecraft.class_1297) (Object) this).method_5752().contains("cbswallv")) {
            cir.setReturnValue(new net.minecraft.class_12392(1.0F, 2.0F, 3.0F, 4.0F, 0.25F, 1.0F));
        }
    }
}
