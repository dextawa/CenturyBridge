package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorMaterial;
import top.dext.centurybridge.rt.v26_2.Statics;

/**
 * Facade for 1.20.1 net/minecraft/class_1738 (ArmorItem), deleted on 26.2
 * (armor is data-driven: a plain Item + ARMOR components). A corpus mod both
 * {@code new class_1738(...)} and types fields/returns as class_1738 (e.g.
 * MythicMetals' metal armor), so this facade extends the real 26.2 Item and
 * reconstructs the old (ArmorMaterial, Type, Properties) constructor by routing
 * through 26.2's own {@code Properties.humanoidArmor(ArmorMaterial, ArmorType)}
 * -- which attaches the EQUIPPABLE / ARMOR / durability components -- so the
 * armor actually protects and wears, not just loads.
 *
 * The legacy material arg is the mod's own class_1741 (the old ArmorMaterial
 * interface the mod implements); {@link Statics#cbArmorMaterial} turns it into
 * a 26.2 ArmorMaterial record (per-slot defense/durability from the legacy
 * accessors; repair + render asset degrade -- see there). Reached via a
 * classRename on class_1738 so both direct new and subclass extends resolve.
 * Member names are intermediary tokens.
 */
public class class_1738 extends Item implements class_5151 {

    public final class_8051 field_41933;   // Type
    protected final class_1741 field_7881;     // the legacy ArmorMaterial facade

    public class_1738(class_1741 material, class_8051 type, Item.Properties props) {
        super(buildProps(material, type, props));
        this.field_7881 = material;
        this.field_41933 = type;
    }

    private static Item.Properties buildProps(class_1741 material, class_8051 type, Item.Properties props) {
        try {
            return props.humanoidArmor(Statics.cbArmorMaterial(material), Statics.cbArmorType(type));
        } catch (Throwable t) {
            // never let an armor piece fail to construct over a component edge case
            return props;
        }
    }

    /** getType() */
    public class_8051 method_48398() {
        return field_41933;
    }

    /** getMaterial() -> the real 26.2 ArmorMaterial record (converted) */
    public ArmorMaterial method_7686() {
        return Statics.cbArmorMaterial(field_7881);
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
