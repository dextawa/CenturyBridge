package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorMaterial;

/**
 * Facade for 1.20.1 net/minecraft/class_1738 (ArmorItem), deleted on 26.2
 * (armor is a data component, no ArmorItem subclass). Reached via superRenames:
 * a mod armor subclass has its extends + super-calls pointed here; every other
 * class_1738 reference stays real. Extends the real 26.2 Item so the subclass
 * is still a genuine Item and registers; implements the Equipable facade as the
 * original did. The old (ArmorMaterial, Type, Properties) constructor forwards
 * to Item(Properties), dropping the armor params (data-driven on 26.2).
 *
 * ArmorMaterial (class_1741) survives on 26.2 as a real class, so the ctor and
 * getMaterial use its real type directly. Type (class_8051) is a CB facade enum.
 * Member names are intermediary tokens.
 */
public class class_1738 extends Item implements class_5151 {

    protected final class_8051 field_41933;   // Type
    protected final ArmorMaterial field_7881; // ArmorMaterial (real 26.2 type)

    public class_1738(ArmorMaterial material, class_8051 type, Item.Properties props) {
        super(props);
        this.field_7881 = material;
        this.field_41933 = type;
    }

    /** getType() */
    public class_8051 method_48398() {
        return field_41933;
    }

    /** getMaterial() */
    public ArmorMaterial method_7686() {
        return field_7881;
    }

    /** getEquipmentSlot() -- from Equipable (class_5151) */
    @Override
    public EquipmentSlot method_7685() {
        return field_41933 == null ? EquipmentSlot.HEAD : field_41933.method_48399();
    }

    /** getEnchantmentValue() */
    public int method_7837() {
        return 9;
    }

    /** getDefense() */
    public int method_7687() {
        return 0;
    }

    /** getToughness() */
    public float method_26353() {
        return 0.0F;
    }

    /** isValidRepairItem(stack, repair) */
    public boolean method_7878(ItemStack stack, ItemStack repair) {
        return false;
    }

    /** use(level, player, hand) -> pass */
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

    /** getDefaultAttributeModifiers(slot) -> empty */
    public com.google.common.collect.Multimap<
            net.minecraft.world.entity.ai.attributes.Attribute,
            net.minecraft.world.entity.ai.attributes.AttributeModifier> method_7844(
            EquipmentSlot slot) {
        return com.google.common.collect.ImmutableMultimap.of();
    }
}
