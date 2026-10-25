package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_1792;
import net.minecraft.class_3481;
import net.minecraft.class_9886;

/**
 * 1.21.4's PickaxeItem (net/minecraft/class_1810), gone in 1.21.5: a DiggerItem for BlockTags.MINEABLE_WITH_PICKAXE
 * (1.21.4's constructor, its only member). Superclass of a converted subclass (superRenames) and what a 1.21.4
 * {@code new PickaxeItem(..)} builds (ctorRedirects); other references name Item (classRenames).
 */
public class PickaxeItem extends DiggerItem {

    public PickaxeItem(class_9886 material, float attackDamage, float attackSpeed, class_1792.class_1793 properties) {
        super(material, class_3481.field_33715, attackDamage, attackSpeed, properties);
    }
}
