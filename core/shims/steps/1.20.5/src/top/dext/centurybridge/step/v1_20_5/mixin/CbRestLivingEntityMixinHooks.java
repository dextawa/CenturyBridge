package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.facade.class_1310;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;

/**
 * The injector into 1.20.5's own LivingEntity.canBeAffected / isInvertedHealAndHarm that goes with
 * {@link CbRestLivingEntityMixin} (its restored 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}): an
 * entity whose class declares a 1.20.4 MobType decides undead by it, as in 1.20.4.
 */
@Mixin(net.minecraft.class_1309.class)
public abstract class CbRestLivingEntityMixinHooks {

    @Redirect(method = {"method_6049", "method_5999"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1299;method_20210(Lnet/minecraft/class_6862;)Z"))
    private boolean cbRest$undeadTag(net.minecraft.class_1299<?> type, net.minecraft.class_6862<net.minecraft.class_1299<?>> tag) {
        if (tag == net.minecraft.class_3483.field_48290 || tag == net.minecraft.class_3483.field_48291) {
            class_1310 declared = CbRestMobType.declared((net.minecraft.class_1297) (Object) this);
            if (declared != null) {
                return declared == class_1310.field_6289;
            }
        }
        return type.method_20210(tag);
    }
}
