package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * AbstractHorse: 1.20.4's getCustomJump() (the jump strength attribute; 1.20.5 reads the attribute in
 * LivingEntity.getJumpPower) back, and the rider's jump computed from it again as 1.20.4's executeRidersJump
 * did -- getCustomJump() times the jump scale and the block's jump factor, plus the jump boost -- so a 1.20.4
 * subclass that overrides it still jumps its way (CbEntityHorseMixinHooks, default priority). Unoverridden it is
 * exactly 1.20.5's getJumpPower(scale).
 */
@Mixin(value = net.minecraft.class_1496.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityHorseMixin extends net.minecraft.class_1309 implements CbPrioritySplit.HorseJump {

    protected CbEntityHorseMixin(net.minecraft.class_1299<? extends net.minecraft.class_1309> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /** was getCustomJump() */
    public double method_6771() {
        return this.method_45325(net.minecraft.class_5134.field_23728);
    }

    /** 1.20.4's getCustomJump called virtually, for executeRidersJump (CbEntityHorseMixinHooks) */
    @Override
    public double cb$v1_20_5$customJump() {
        return this.method_6771();
    }
}
