package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.class_1792;
import net.minecraft.class_1814;
import net.minecraft.class_4174;
import net.minecraft.class_7696;

/**
 * Compat for Fabric API's 1.20.1-era {@code net.fabricmc.fabric.api.item.v1.FabricItemSettings},
 * deleted from Fabric API for 1.21+ (42 corpus mods construct it). It was a fluent subclass of
 * Item.Settings (class_1792$class_1793); the two Fabric-specific setters now live on the
 * interface fapi's mixin injects into Item.Settings ({@code FabricItem.Settings}), so every
 * method is a faithful delegation. The fluent names are fapi's own (unobfuscated); the vanilla
 * setters they wrap keep their intermediary ids, so a mod calling the inherited
 * {@code method_7889(I)} directly resolves through the superclass.
 * classRename: net/fabricmc/fabric/api/item/v1/FabricItemSettings -> this class.
 */
public class FabricItemSettings extends class_1792.class_1793 {
    private boolean durabilitySet;

    public FabricItemSettings() {
        super();
    }

    public FabricItemSettings equipmentSlot(EquipmentSlotProvider provider) {
        ((FabricItem.Settings) (Object) this).equipmentSlot(provider);
        return this;
    }

    public FabricItemSettings customDamage(CustomDamageHandler handler) {
        ((FabricItem.Settings) (Object) this).customDamage(handler);
        return this;
    }

    /** was food(FoodComponent) -> Item.Settings.food */
    public FabricItemSettings food(class_4174 food) {
        super.method_19265(food);
        return this;
    }

    /** was maxCount(int) -> Item.Settings.maxCount (stacksTo) */
    public FabricItemSettings maxCount(int count) {
        super.method_7889(count);
        return this;
    }

    /** 1.20.1: set the durability only when none was set on this settings object yet. */
    public FabricItemSettings maxDamageIfAbsent(int damage) {
        if (!durabilitySet) {
            maxDamage(damage);
        }
        return this;
    }

    /** was maxDamage(int) -> Item.Settings.maxDamage (durability) */
    public FabricItemSettings maxDamage(int damage) {
        super.method_7895(damage);
        durabilitySet = true;
        return this;
    }

    /** was recipeRemainder(Item) -> Item.Settings.recipeRemainder (craftRemainder) */
    public FabricItemSettings recipeRemainder(class_1792 remainder) {
        super.method_7896(remainder);
        return this;
    }

    /** was rarity(Rarity) */
    public FabricItemSettings rarity(class_1814 rarity) {
        super.method_7894(rarity);
        return this;
    }

    /** was fireproof() -> Item.Settings.fireproof (fireResistant) */
    public FabricItemSettings fireproof() {
        super.method_24359();
        return this;
    }

    /** was requires(FeatureFlag...) -> Item.Settings.requires (requiredFeatures) */
    public FabricItemSettings requires(class_7696... flags) {
        super.method_45434(flags);
        return this;
    }
}
