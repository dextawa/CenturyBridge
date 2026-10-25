package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1308;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMeleeRange;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMeleeReference;

/**
 * 1.20.1's melee range back on Mob (s1misc melee bridge), 1.20.1 bodies: getMeleeAttackRangeSqr
 * (twice the mob's width, squared, plus the target's width) and
 * getPerceivedTargetDistanceSquareForMeleeAttack (the nearer of the target's position and its
 * melee reference position). 1.20.2 calls neither; see CbMelee.
 */
@Mixin(value = class_1308.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbMobMeleeRange implements CbLegacyMeleeRange {

    @Override
    public double method_33191(class_1309 target) {
        class_1308 self = (class_1308) (Object) this;
        return (double) (self.method_17681() * 2.0F * self.method_17681() * 2.0F + target.method_17681());
    }

    @Override
    public double method_47922(class_1309 target) {
        class_1308 self = (class_1308) (Object) this;
        return Math.max(self.method_5707(((CbLegacyMeleeReference) target).cb$meleeReferencePosition()),
            self.method_5707(target.method_19538()));
    }
}
