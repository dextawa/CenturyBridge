package top.dext.centurybridge.step.v1_21.rt;

/**
 * Fabric API's item API (fabric-item-api-v1) of 1.20.6 on 1.21's.
 *
 * <ul>
 * <li>EnchantingContext: 1.20.6's four contexts named the vanilla check Fabric API made ask
 *     canBeEnchantedWith -- RANDOM_ENCHANTMENT the enchanting table's selection, ANVIL, ENCHANT_COMMAND,
 *     LOOT_RANDOM_ENCHANTMENT the enchant-randomly loot function. 1.21's two name what 1.21's checks ask:
 *     PRIMARY (the enchanting table, Enchantment.isPrimaryItem) and ACCEPTABLE (the anvil, the command,
 *     the loot function: Enchantment.canEnchant). Each 1.20.6 context is the one its check asks with now;
 *     1.21 cannot tell the anvil, the command and the loot function apart, so neither can a 1.20.6 mod
 *     comparing contexts.</li>
 * <li>canBeEnchantedWith and EnchantmentEvents.ALLOW_ENCHANTING take the enchantment's holder where
 *     1.20.6 took the object (CbEnchantments' converters).</li>
 * <li>EquipmentSlotProvider.getPreferredEquipmentSlot(stack) gained the entity: 1.20.6's answer read the
 *     stack alone.</li>
 * <li>FabricItem.getAttributeModifiers(stack) is gone: Fabric API 1.20.6 itself never called it (its
 *     default answered the item's default modifiers), so it reached only mods' own calls; those keep it
 *     -- a mod item's own override, else the default.</li>
 * </ul>
 */
public final class CbItemFapi {

    private CbItemFapi() {
    }

    // ---- EnchantingContext ----

    /** EnchantingContext.RANDOM_ENCHANTMENT (fieldGetter): the enchanting table's check, PRIMARY */
    public static net.fabricmc.fabric.api.item.v1.EnchantingContext RANDOM_ENCHANTMENT() {
        return net.fabricmc.fabric.api.item.v1.EnchantingContext.PRIMARY;
    }

    /** EnchantingContext.ANVIL (fieldGetter): ACCEPTABLE */
    public static net.fabricmc.fabric.api.item.v1.EnchantingContext ANVIL() {
        return net.fabricmc.fabric.api.item.v1.EnchantingContext.ACCEPTABLE;
    }

    /** EnchantingContext.ENCHANT_COMMAND (fieldGetter): ACCEPTABLE */
    public static net.fabricmc.fabric.api.item.v1.EnchantingContext ENCHANT_COMMAND() {
        return net.fabricmc.fabric.api.item.v1.EnchantingContext.ACCEPTABLE;
    }

    /** EnchantingContext.LOOT_RANDOM_ENCHANTMENT (fieldGetter): ACCEPTABLE */
    public static net.fabricmc.fabric.api.item.v1.EnchantingContext LOOT_RANDOM_ENCHANTMENT() {
        return net.fabricmc.fabric.api.item.v1.EnchantingContext.ACCEPTABLE;
    }

    // ---- EnchantmentEvents.AllowEnchanting ----

    /** a 1.20.6 AllowEnchanting lambda (or class) of a mod: allowEnchanting(enchantment, stack, context) */
    @FunctionalInterface
    public interface LegacyAllowEnchanting {
        net.fabricmc.fabric.api.util.TriState allowEnchanting(top.dext.centurybridge.step.v1_21.facade.class_1887 enchantment,
                net.minecraft.class_1799 stack, net.fabricmc.fabric.api.item.v1.EnchantingContext context);
    }

    /** lambdaAdapters: a 1.20.6 AllowEnchanting lambda as 1.21's, handed the holder's 1.20.6 object */
    public static net.fabricmc.fabric.api.item.v1.EnchantmentEvents.AllowEnchanting cbAllowEnchanting(LegacyAllowEnchanting legacy) {
        return (holder, stack, context) -> legacy.allowEnchanting(CbEnchantments.cbEnchantment(holder), stack, context);
    }

    // ---- EquipmentSlotProvider ----

    /** a 1.20.6 EquipmentSlotProvider lambda of a mod: getPreferredEquipmentSlot(stack) */
    @FunctionalInterface
    public interface LegacyEquipmentSlotProvider {
        net.minecraft.class_1304 getPreferredEquipmentSlot(net.minecraft.class_1799 stack);
    }

    /** lambdaAdapters: a 1.20.6 EquipmentSlotProvider lambda as 1.21's, the entity not passed on */
    public static net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider cbEquipmentSlotProvider(LegacyEquipmentSlotProvider legacy) {
        return (entity, stack) -> legacy.getPreferredEquipmentSlot(stack);
    }

    /** the entity a 1.20.6 getPreferredEquipmentSlot(stack) call passes: none (callReshapes) */
    public static net.minecraft.class_1309 cbNoEntity() {
        return null;
    }

    // ---- FabricItem.getAttributeModifiers ----

    /** the 1.20.6 FabricItem.getAttributeModifiers(stack) a mod item declares (adapterHooks) */
    public interface LegacyAttributeModifiers {
        net.minecraft.class_9285 getAttributeModifiers(net.minecraft.class_1799 stack);
    }

    /** item.getAttributeModifiers(stack) called by a 1.20.6 mod (instanceRedirect): the item's own, else the default */
    public static net.minecraft.class_9285 getAttributeModifiers(net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack) {
        if (item instanceof LegacyAttributeModifiers legacy) {
            return legacy.getAttributeModifiers(stack);
        }
        return ((net.minecraft.class_1792) item).method_7844();
    }

    /** the same call through Item (the interface Fabric API injects into it) */
    public static net.minecraft.class_9285 getAttributeModifiers(net.minecraft.class_1792 item, net.minecraft.class_1799 stack) {
        return getAttributeModifiers((net.fabricmc.fabric.api.item.v1.FabricItem) item, stack);
    }

    /** FabricItem.super.getAttributeModifiers(stack) of a mod item (superRedirects): 1.20.6's default, the item's default modifiers */
    public static net.minecraft.class_9285 superGetAttributeModifiers(net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack) {
        return ((net.minecraft.class_1792) item).method_7844();
    }
}
