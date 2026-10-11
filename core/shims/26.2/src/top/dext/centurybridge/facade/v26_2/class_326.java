package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.ItemStack;

/**
 * ItemColorProvider (class_326 / ItemColor) as the corpus implements it. Item
 * tints stopped being a runtime callback in 1.21.4: they are declared in the
 * item model definition JSON (tints: [...]). CbColorProviderRegistry.ITEM keeps
 * what mods register, and the centurybridge:legacy_color tint the converter
 * writes into their item model definitions asks it (CbLegacyItemModels).
 */
@FunctionalInterface
public interface class_326 {
    /** was ItemColorProvider.getColor(stack, tintIndex) */
    int getColor(ItemStack stack, int tintIndex);
}
