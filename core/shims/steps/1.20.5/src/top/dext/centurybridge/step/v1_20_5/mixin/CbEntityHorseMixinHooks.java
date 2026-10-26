package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;

/**
 * The injector into 1.20.5's own AbstractHorse.executeRidersJump that goes with {@link CbEntityHorseMixin} (its restored
 * 1.20.4 member stays there, at {@code CbVanillaReads.PRIORITY}): the rider's jump computed from getCustomJump() as
 * 1.20.4's executeRidersJump did.
 */
@Mixin(net.minecraft.class_1496.class)
public abstract class CbEntityHorseMixinHooks extends net.minecraft.class_1309 {

    protected CbEntityHorseMixinHooks(net.minecraft.class_1299<? extends net.minecraft.class_1309> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /** executeRidersJump: getJumpPower(scale) as 1.20.4 computed the rider's jump */
    @Redirect(method = "method_45343", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1496;method_56994(F)F"))
    private float cbS4$riderJump(net.minecraft.class_1496 self, float scale) {
        return (float) ((CbPrioritySplit.HorseJump) this).cb$v1_20_5$customJump() * scale * this.method_23313() + this.method_37416();
    }
}
