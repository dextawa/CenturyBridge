package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * AbstractHorse: 1.20.4's getCustomJump() (the jump strength attribute; 1.20.5 reads the attribute in
 * LivingEntity.getJumpPower) back, and the rider's jump computed from it again as 1.20.4's executeRidersJump
 * did -- getCustomJump() times the jump scale and the block's jump factor, plus the jump boost -- so a 1.20.4
 * subclass that overrides it still jumps its way. Unoverridden it is exactly 1.20.5's getJumpPower(scale).
 */
@Mixin(net.minecraft.class_1496.class)
public abstract class CbEntityHorseMixin extends net.minecraft.class_1309 {

    protected CbEntityHorseMixin(net.minecraft.class_1299<? extends net.minecraft.class_1309> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /** was getCustomJump() */
    public double method_6771() {
        return this.method_45325(net.minecraft.class_5134.field_23728);
    }

    /** executeRidersJump: getJumpPower(scale) as 1.20.4 computed the rider's jump */
    @Redirect(method = "method_45343", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1496;method_56994(F)F"))
    private float cbS4$riderJump(net.minecraft.class_1496 self, float scale) {
        return (float) this.method_6771() * scale * this.method_23313() + this.method_37416();
    }
}
