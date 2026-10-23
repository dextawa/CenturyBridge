package top.dext.centurybridge.step.v1_20_5.facade;

import top.dext.centurybridge.step.v1_20_5.rt.CbFinItemArmor;

/**
 * 1.20.4's DyeableLeatherItem (class_1768; classRenames): an item interface whose stacks carry a custom
 * colour in display.color. 1.20.5 dropped it for the dyed_color component and the minecraft:dyeable item
 * tag; the stack's 1.20.4 NBT view keeps display.color in step with dyed_color (rt/CbItemTags), so these
 * default methods are 1.20.4's bodies over that view, and every item whose class implements this joins the
 * tag as tags load (rt/CbFinItemArmor.dyeableItems), so 1.20.5's dyeing recipe, cauldron washing and armor
 * tint see it. 1.20.5's ArmorItem implements it too (CbFinItemDyeableArmorMixin), so a 1.20.4 cast of a
 * vanilla leather item -- dyeable in 1.20.4 -- holds; instanceof answers 1.20.4's truth
 * (rt/CbFinItemArmor.cbInstanceOf).
 */
public interface DyeableLeatherItem extends CbFinItemArmor.Dyeable {

    /** TAG_COLOR */
    String field_30873 = "color";
    /** TAG_DISPLAY */
    String field_30871 = "display";
    /** DEFAULT_LEATHER_COLOR */
    int field_30872 = 10511680;

    /** hasCustomColor(stack) */
    @Override
    default boolean method_7801(net.minecraft.class_1799 stack) {
        return CbFinItemArmor.hasCustomColor(stack);
    }

    /** getColor(stack) */
    @Override
    default int method_7800(net.minecraft.class_1799 stack) {
        return CbFinItemArmor.getColor(stack);
    }

    /** clearColor(stack) */
    @Override
    default void method_7798(net.minecraft.class_1799 stack) {
        CbFinItemArmor.clearColor(stack);
    }

    /** setColor(stack, color) */
    @Override
    default void method_7799(net.minecraft.class_1799 stack, int color) {
        CbFinItemArmor.setColor(stack, color);
    }

    /** dyeArmor(stack, dyes): 1.20.4's mix, through the item's own color methods */
    static net.minecraft.class_1799 method_19261(net.minecraft.class_1799 stack, java.util.List<net.minecraft.class_1769> dyes) {
        return CbFinItemArmor.dyeArmor(stack, dyes);
    }
}
