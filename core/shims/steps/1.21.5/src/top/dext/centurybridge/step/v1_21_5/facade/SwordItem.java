package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_9886;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemTools;

/**
 * 1.21.4's SwordItem (net/minecraft/class_1829), gone in 1.21.5 (its swords are Items built from
 * Item.Properties.sword). Superclass of a converted 1.21.4 SwordItem subclass (superRenames) and what a 1.21.4
 * {@code new SwordItem(..)} builds (ctorRedirects); every other reference names Item (classRenames), the type
 * 1.21.5's vanilla swords have, and a type test asks CbItemToolTypes.
 * <p>
 * 1.21.4's bodies: the constructor applies ToolMaterial's sword properties (1.21.4's: no WEAPON, CbItemTools);
 * hurtEnemy answers true (the restored Item.hurtEnemy, CbItemHurtEnemyRestoredMixin, answers so for this class);
 * postHurtEnemy wears the item by 1. canAttackBlock -- 1.21.4's {@code !player.isCreative()} -- is what 1.21.5's
 * Item.canAttackBlock answers from the sword Tool's canDestroyBlocksInCreative = false, so it is not redeclared.
 */
public class SwordItem extends class_1792 {

    public SwordItem(class_9886 material, float attackDamage, float attackSpeed, class_1792.class_1793 properties) {
        super(CbItemTools.swordProps(material, properties, attackDamage, attackSpeed));
    }

    /** postHurtEnemy(stack, target, attacker): 1.21.4's SwordItem wears the stack by 1 */
    @Override
    public void method_59978(class_1799 stack, class_1309 target, class_1309 attacker) {
        CbItemTools.wear(stack, 1, attacker);
    }
}
