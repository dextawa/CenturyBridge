package cb.swall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.11's Mob.canAttackType(EntityType) (class_1308.method_5973), as 1.21.11-era mods mixed it: a mob tagged
 * "cbswallv" refuses villagers and accepts ghasts. On 26.1 the layer restores the member and Mob / IronGolem.canAttack
 * and TargetingConditions.test take its answer where it differs (cb-swalln's Driver checks vanilla's answers).
 */
@Mixin(net.minecraft.class_1308.class)
public abstract class MobAttackTypeMixin {
    @Inject(method = "method_5973(Lnet/minecraft/class_1299;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbswall$attackType(net.minecraft.class_1299<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (((net.minecraft.class_1297) (Object) this).method_5752().contains("cbswallv")) {
            if (type == net.minecraft.class_1299.field_6077) {
                cir.setReturnValue(false);
            } else if (type == net.minecraft.class_1299.field_6107) {
                cir.setReturnValue(true);
            }
        }
    }
}
