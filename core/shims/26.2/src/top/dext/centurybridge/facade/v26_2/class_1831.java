package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Facade for 1.20.1 net/minecraft/class_1831 (TieredItem), deleted when tool
 * behaviour moved to data components. Legacy tools/weapons extend it (directly
 * or via DiggerItem/SwordItem). It extends the REAL 26.2 Item, so a mod's
 * subclass is still a genuine Item and registers normally; only the old
 * Tier-carrying constructor is reconstructed, forwarding to Item(Properties)
 * and dropping the Tier (attributes are data-driven on 26.2).
 *
 * Member names are intermediary tokens.
 */
public class class_1831 extends Item {

    public final class_1832 field_8921; // the Tier facade

    public class_1831(class_1832 tier, Item.Properties props) {
        super(props);
        this.field_8921 = tier;
    }

    /** getTier() */
    public class_1832 method_8022() {
        return field_8921;
    }

    /** getEnchantmentValue() */
    public int method_7837() {
        return field_8921 == null ? 0 : field_8921.method_8026();
    }

    /** isValidRepairItem(stack, repair) */
    public boolean method_7878(ItemStack stack, ItemStack repair) {
        return field_8921 != null && field_8921.method_8023() != null
            && field_8921.method_8023().test(repair);
    }

    // ---- legacy Item API surface (1.20.1 shapes) the subclasses call/override.
    // ---- Declared here so a mod tool/weapon subclass reached via superRenames
    // ---- resolves its own method_* calls (and super-calls) to the facade rather
    // ---- than dangling (the real 26.2 Item has only the mojang-named, reshaped
    // ---- equivalents). Bodies are inert defaults: a subclass that overrides one
    // ---- gets its own behaviour; one that calls super gets a safe no-op. 26.2
    // ---- drives the real item behaviour through data components, so these
    // ---- legacy hooks being inert is a ledgered degrade, not a crash.

    /** use(level, player, hand) -> pass (no action) */
    public net.minecraft.world.InteractionResult method_7836(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        return net.minecraft.world.InteractionResult.PASS;
    }

    /** useOn(context) -> pass */
    public net.minecraft.world.InteractionResult method_7884(net.minecraft.world.item.context.UseOnContext ctx) {
        return net.minecraft.world.InteractionResult.PASS;
    }

    /** appendHoverText(stack, level, tooltip, flag) -> no extra tooltip */
    public void method_7851(ItemStack stack, net.minecraft.world.level.Level level,
            java.util.List tooltip, net.minecraft.world.item.TooltipFlag flag) {
    }

    /** inventoryTick(stack, level, entity, slot, selected) -> no-op */
    public void method_7888(ItemStack stack, net.minecraft.world.level.Level level,
            net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
    }

    /** finishUsingItem(stack, level, entity) -> return the stack unchanged */
    public ItemStack method_7861(ItemStack stack, net.minecraft.world.level.Level level,
            net.minecraft.world.entity.LivingEntity entity) {
        return stack;
    }

    /** hurtEnemy / postHurt-style hook (stack, level, livingTarget, int) -> no-op */
    public void method_7840(ItemStack stack, net.minecraft.world.level.Level level,
            net.minecraft.world.entity.LivingEntity target, int amount) {
    }

    /** getDefaultInstance() -> one of this item */
    public ItemStack method_7854() {
        return new ItemStack(this);
    }

    /** isFoil(stack) -> false */
    public boolean method_7886(ItemStack stack) {
        return false;
    }

    /** getBarColor(stack) -> default durability-bar color */
    public int method_31571(ItemStack stack) {
        return 0x00FF00;
    }

    /** onDestroyed(itemEntity) -> no-op */
    public void method_33261(net.minecraft.world.entity.item.ItemEntity entity) {
    }

    /** getDefaultAttributeModifiers(slot) -> empty */
    public com.google.common.collect.Multimap<
            net.minecraft.world.entity.ai.attributes.Attribute,
            net.minecraft.world.entity.ai.attributes.AttributeModifier> method_7844(
            net.minecraft.world.entity.EquipmentSlot slot) {
        return com.google.common.collect.ImmutableMultimap.of();
    }
}
