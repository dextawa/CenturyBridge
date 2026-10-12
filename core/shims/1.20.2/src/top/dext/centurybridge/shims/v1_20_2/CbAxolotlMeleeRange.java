package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Axolotl's own 1.20.1 getMeleeAttackRangeSqr (1.5 plus twice the target's width), over Mob's
 * (CbMobMeleeRange): a call on an axolotl and a converted subclass's super call answer it.
 */
@Mixin(net.minecraft.class_5762.class)
public abstract class CbAxolotlMeleeRange {

    public double method_33191(class_1309 target) {
        return 1.5 + (double) target.method_17681() * 2.0;
    }
}
