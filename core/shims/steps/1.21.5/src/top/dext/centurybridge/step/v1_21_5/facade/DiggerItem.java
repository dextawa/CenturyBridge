package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_6862;
import net.minecraft.class_9886;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemTools;

/**
 * 1.21.4's DiggerItem (net/minecraft/class_1766), gone in 1.21.5 (Axe/Shovel/HoeItem extend Item, pickaxes are Items
 * built from Item.Properties.pickaxe). Superclass of a converted 1.21.4 DiggerItem subclass (superRenames); every
 * other reference names Item (classRenames), and a type test asks CbItemToolTypes.
 * <p>
 * 1.21.4's bodies: the constructor applies ToolMaterial's tool properties (1.21.4's: no WEAPON, CbItemTools);
 * hurtEnemy answers true (the restored Item.hurtEnemy answers so for this class); postHurtEnemy wears the item by 2.
 */
public class DiggerItem extends class_1792 {

    protected DiggerItem(class_9886 material, class_6862<class_2248> mineable, float attackDamage, float attackSpeed,
            class_1792.class_1793 properties) {
        super(CbItemTools.toolProps(material, properties, mineable, attackDamage, attackSpeed));
    }

    /** postHurtEnemy(stack, target, attacker): 1.21.4's DiggerItem wears the stack by 2 */
    @Override
    public void method_59978(class_1799 stack, class_1309 target, class_1309 attacker) {
        CbItemTools.wear(stack, 2, attacker);
    }
}
