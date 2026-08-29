package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Compat for Fabric API's 1.20.1-era {@code net.fabricmc.fabric.api.item.v1.FabricItemSettings},
 * deleted from Fabric API for 1.21+ (42 corpus mods construct it). It was a fluent subclass of
 * Item.Settings; the two Fabric-specific setters now live on the interface fapi injects into
 * Item.Properties ({@link FabricItem.Properties}), so every method is a faithful delegation.
 * classRename: net/fabricmc/fabric/api/item/v1/FabricItemSettings -> this class.
 */
public class FabricItemSettings extends Item.Properties {
    private boolean durabilitySet;

    public FabricItemSettings() {
        super();
    }

    public FabricItemSettings equipmentSlot(EquipmentSlotProvider provider) {
        ((FabricItem.Properties) (Object) this).equipmentSlot(provider);
        return this;
    }

    public FabricItemSettings customDamage(CustomDamageHandler handler) {
        ((FabricItem.Properties) (Object) this).customDamage(handler);
        return this;
    }

    public FabricItemSettings food(FoodProperties food) {
        super.food(food);
        return this;
    }

    public FabricItemSettings maxCount(int count) {
        super.stacksTo(count);
        return this;
    }

    /** 1.20.1: set the durability only when none was set on this settings object yet. */
    public FabricItemSettings maxDamageIfAbsent(int damage) {
        if (!durabilitySet) {
            maxDamage(damage);
        }
        return this;
    }

    public FabricItemSettings maxDamage(int damage) {
        super.durability(damage);
        durabilitySet = true;
        return this;
    }

    public FabricItemSettings recipeRemainder(Item remainder) {
        super.craftRemainder(remainder);
        return this;
    }

    public FabricItemSettings rarity(Rarity rarity) {
        super.rarity(rarity);
        return this;
    }

    public FabricItemSettings fireproof() {
        super.fireResistant();
        return this;
    }

    public FabricItemSettings requires(FeatureFlag... flags) {
        super.requiredFeatures(flags);
        return this;
    }
}
