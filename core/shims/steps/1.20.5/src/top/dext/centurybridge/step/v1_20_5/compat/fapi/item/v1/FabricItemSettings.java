package top.dext.centurybridge.step.v1_20_5.compat.fapi.item.v1;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemProps;

/**
 * Fabric API 0.97.3's (1.20.4) FabricItemSettings, removed in 1.20.5 (Item.Settings itself carries Fabric's
 * equipmentSlot / customDamage there, through an injected interface). A 1.20.4 mod's every reference to the
 * class names this one (classRenames): {@code new FabricItemSettings()}, subclasses, and the builder calls,
 * which 1.20.4 declared under their yarn names with this class as the return type. As in 1.20.4 each
 * covariant method sets the vanilla property and answers this, and vanilla's own methods (intermediary
 * names) are overridden to call the covariant ones -- what javac's bridges did -- so a mod subclass overriding
 * one is reached through either. maxDamageIfAbsent is 1.20.4's defaultDurability, which 1.20.5 dropped
 * (s4item's CbItemProps: its 1.20.4 body, and a subclass's override reached through DefaultDurability).
 */
public class FabricItemSettings extends net.minecraft.class_1792.class_1793 implements CbItemProps.DefaultDurability {

    public FabricItemSettings() {
    }

    /** 1.20.5's Item.Settings is a FabricItem.Settings (Fabric API's injected interface) */
    private net.fabricmc.fabric.api.item.v1.FabricItem.Settings fabric() {
        return (net.fabricmc.fabric.api.item.v1.FabricItem.Settings) (Object) this;
    }

    public FabricItemSettings equipmentSlot(EquipmentSlotProvider equipmentSlotProvider) {
        fabric().equipmentSlot(equipmentSlotProvider);
        return this;
    }

    public FabricItemSettings customDamage(CustomDamageHandler handler) {
        fabric().customDamage(CustomDamageHandler.adapt(handler));
        return this;
    }

    public FabricItemSettings food(net.minecraft.class_4174 foodComponent) {
        super.method_19265(foodComponent);
        return this;
    }

    public FabricItemSettings maxCount(int maxCount) {
        super.method_7889(maxCount);
        return this;
    }

    public FabricItemSettings maxDamageIfAbsent(int maxDamage) {
        CbItemProps.superDefaultDurability(this, maxDamage);
        return this;
    }

    public FabricItemSettings maxDamage(int maxDamage) {
        super.method_7895(maxDamage);
        return this;
    }

    public FabricItemSettings recipeRemainder(net.minecraft.class_1792 recipeRemainder) {
        super.method_7896(recipeRemainder);
        return this;
    }

    public FabricItemSettings rarity(net.minecraft.class_1814 rarity) {
        super.method_7894(rarity);
        return this;
    }

    public FabricItemSettings fireproof() {
        super.method_24359();
        return this;
    }

    public FabricItemSettings requires(net.minecraft.class_7696... features) {
        super.method_45434(features);
        return this;
    }

    // ---------------------------------------------------------------- vanilla's names reach the covariant methods

    @Override
    public net.minecraft.class_1792.class_1793 method_19265(net.minecraft.class_4174 foodComponent) {
        return food(foodComponent);
    }

    @Override
    public net.minecraft.class_1792.class_1793 method_7889(int maxCount) {
        return maxCount(maxCount);
    }

    /** defaultDurability(int), 1.20.4's (CbItemProps.DefaultDurability: Item.Settings' calls of it reach here) */
    @Override
    public net.minecraft.class_1792.class_1793 method_7898(int durability) {
        return maxDamageIfAbsent(durability);
    }

    @Override
    public net.minecraft.class_1792.class_1793 method_7895(int maxDamage) {
        return maxDamage(maxDamage);
    }

    @Override
    public net.minecraft.class_1792.class_1793 method_7896(net.minecraft.class_1792 recipeRemainder) {
        return recipeRemainder(recipeRemainder);
    }

    @Override
    public net.minecraft.class_1792.class_1793 method_7894(net.minecraft.class_1814 rarity) {
        return rarity(rarity);
    }

    @Override
    public net.minecraft.class_1792.class_1793 method_24359() {
        return fireproof();
    }

    @Override
    public net.minecraft.class_1792.class_1793 method_45434(net.minecraft.class_7696... features) {
        return requires(features);
    }
}
