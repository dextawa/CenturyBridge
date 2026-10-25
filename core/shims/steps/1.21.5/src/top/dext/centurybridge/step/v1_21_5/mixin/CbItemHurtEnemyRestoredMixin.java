package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemWeapon;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * 1.21.4's Item.hurtEnemy(stack, target, attacker) -> boolean, restored on Item (CbItemWeapon): a 1.21.4 class's
 * override overrides it, a 1.21.4 call or super call links to it. Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1792.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemHurtEnemyRestoredMixin implements CbItemWeapon.ItemHurtEnemy {

    /**
     * 1.21.4's hurtEnemy: what 1.21.5's path does and decides -- its hurtEnemy, then whether the stack is a weapon
     * (WEAPON); the SwordItem / DiggerItem facades answer true, as 1.21.4's bodies did.
     */
    @CbVanillaReads("CbItemStackHurtEnemyMixin: ItemStack.hurtEnemy asks it for every item that answers otherwise than "
        + "1.21.5's path (a 1.21.4 override, the facades) and takes its answer for the stat and postHurtEnemy")
    @Override
    public boolean method_7873(class_1799 stack, class_1309 target, class_1309 attacker) {
        class_1792 self = (class_1792) (Object) this;
        self.method_7873(stack, target, attacker);
        return CbItemWeapon.facadeAnswer(self) || stack.method_57826(class_9334.field_55878);
    }
}
