package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;

/**
 * The injector into 1.20.5's own Player.attack that goes with {@link CbRestPlayerMixin} (its restored 1.20.4 members
 * stay there, at {@code CbVanillaReads.PRIORITY}): the weapon's damage bonus evaluated with the attacked entity known
 * (CbRestMobType.cbDamageBonus).
 */
@Mixin(net.minecraft.class_1657.class)
public abstract class CbRestPlayerMixinHooks {

    @Redirect(method = "method_7324", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1890;method_8218(Lnet/minecraft/class_1799;Lnet/minecraft/class_1299;)F"))
    private float cbRest$damageBonus(net.minecraft.class_1799 stack, net.minecraft.class_1299<?> type, net.minecraft.class_1297 target) {
        return CbRestMobType.cbDamageBonus(stack, type, target);
    }
}
