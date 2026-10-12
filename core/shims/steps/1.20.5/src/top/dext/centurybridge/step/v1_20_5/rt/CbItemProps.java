package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's per-item properties that 1.20.5 moved into the item's default data components or dropped:
 * edible / food properties, fire resistance and what may hurt the item entity, durability, rarity,
 * whether the stack's tag went to clients. A mod class that overrode one of them is marked by an
 * adapter interface (the converter adds it to every class declaring the method); a call answers with
 * that override, otherwise with what 1.20.4's Item answered, read from the item's own components. The
 * game itself reaches the overrides through the item's component view (CbItemComponents: food, fire
 * resistance) and the stack hooks (mixin/CbItemStackMixin: rarity, what hurts it; mixin/CbItemMixin:
 * whether it is enchantable).
 */
public final class CbItemProps {

    /** a mod class declaring 1.20.4's Item.isEdible() */
    public interface Edible {
        boolean method_19263();
    }

    /** a mod class declaring 1.20.4's Item.getFoodProperties() */
    public interface Food {
        net.minecraft.class_4174 method_19264();
    }

    /** a mod class declaring 1.20.4's Item.isFireResistant() */
    public interface FireResistant {
        boolean method_24358();
    }

    /** a mod class declaring 1.20.4's Item.canBeHurtBy(DamageSource) */
    public interface HurtBy {
        boolean method_24357(net.minecraft.class_1282 source);
    }

    /** a mod class declaring 1.20.4's Item.canBeDepleted() */
    public interface CanBeDepleted {
        boolean method_7846();
    }

    /** a mod class declaring 1.20.4's Item.getRarity(ItemStack) */
    public interface Rarity {
        net.minecraft.class_1814 method_7862(net.minecraft.class_1799 stack);
    }

    /** a mod class declaring 1.20.4's Item.shouldOverrideMultiplayerNbt() */
    public interface MultiplayerNbt {
        boolean method_7887();
    }

    /** a mod subclass of Item.Properties declaring 1.20.4's defaultDurability(int) */
    public interface DefaultDurability {
        net.minecraft.class_1792.class_1793 method_7898(int durability);
    }

    /** the components an item was built with (its component view keeps them under the overrides) */
    public static net.minecraft.class_9323 built(net.minecraft.class_1792 item) {
        net.minecraft.class_9323 c = item.method_57347();
        return c instanceof CbItemComponents v ? v.built() : c;
    }

    // ---------------------------------------------------------------- food

    /** item.isEdible(): the class's own override, else 1.20.4's (the item was given food properties) */
    public static boolean isEdible(net.minecraft.class_1792 item) {
        return item instanceof Edible e ? e.method_19263() : superIsEdible(item);
    }

    /** Item.isEdible()'s 1.20.4 body (super.isEdible() too): its properties gave it food */
    public static boolean superIsEdible(net.minecraft.class_1792 item) {
        return built(item).method_57832(net.minecraft.class_9334.field_50075);
    }

    /** item.getFoodProperties(): the class's own override, else the food its properties gave it */
    public static net.minecraft.class_4174 getFoodProperties(net.minecraft.class_1792 item) {
        return item instanceof Food f ? f.method_19264() : superGetFoodProperties(item);
    }

    /** Item.getFoodProperties()'s 1.20.4 body */
    public static net.minecraft.class_4174 superGetFoodProperties(net.minecraft.class_1792 item) {
        return built(item).method_57829(net.minecraft.class_9334.field_50075);
    }

    // ---------------------------------------------------------------- fire, hurt

    /** item.isFireResistant(): the class's own override, else 1.20.4's (its properties made it so) */
    public static boolean isFireResistant(net.minecraft.class_1792 item) {
        return item instanceof FireResistant f ? f.method_24358() : superIsFireResistant(item);
    }

    /** Item.isFireResistant()'s 1.20.4 body */
    public static boolean superIsFireResistant(net.minecraft.class_1792 item) {
        return built(item).method_57832(net.minecraft.class_9334.field_50076);
    }

    /** item.canBeHurtBy(source): the class's own override, else 1.20.4's body */
    public static boolean canBeHurtBy(net.minecraft.class_1792 item, net.minecraft.class_1282 source) {
        return item instanceof HurtBy h ? h.method_24357(source) : superCanBeHurtBy(item, source);
    }

    /** Item.canBeHurtBy(source)'s 1.20.4 body: anything but fire, unless the item is not fire resistant */
    public static boolean superCanBeHurtBy(net.minecraft.class_1792 item, net.minecraft.class_1282 source) {
        return !isFireResistant(item) || !source.method_48789(net.minecraft.class_8103.field_42246);
    }

    // ---------------------------------------------------------------- durability

    /** item.canBeDepleted(): the class's own 1.20.4 override, else 1.20.4's body (a durability above zero) */
    public static boolean canBeDepleted(net.minecraft.class_1792 item) {
        return item instanceof CanBeDepleted c ? c.method_7846() : superCanBeDepleted(item);
    }

    /** super.canBeDepleted() in a mod subclass: 1.20.4's Item body */
    public static boolean superCanBeDepleted(net.minecraft.class_1792 item) {
        return maxDamage(item) > 0;
    }

    /**
     * item.getMaxDamage() (final in 1.20.4, so never overridden): the durability the item's properties
     * gave it -- 1.20.5 keeps it as the item's default max damage component -- and 0 without one.
     */
    public static int maxDamage(net.minecraft.class_1792 item) {
        Integer max = built(item).method_57829(net.minecraft.class_9334.field_50072);
        return max == null ? 0 : max;
    }

    /**
     * Item.isEnchantable(stack) for an item whose class overrides canBeDepleted: 1.20.4's body -- a single
     * item that can be depleted (1.20.5 asks the stack's max damage component instead).
     */
    public static boolean isEnchantable1204(net.minecraft.class_1792 item) {
        return item.method_7882() == 1 && canBeDepleted(item);
    }

    /** properties.defaultDurability(durability): the class's own override, else 1.20.4's body */
    public static net.minecraft.class_1792.class_1793 defaultDurability(net.minecraft.class_1792.class_1793 p, int durability) {
        return p instanceof DefaultDurability d ? d.method_7898(durability) : superDefaultDurability(p, durability);
    }

    /** Item.Properties.defaultDurability's 1.20.4 body: the durability unless the properties already set one */
    public static net.minecraft.class_1792.class_1793 superDefaultDurability(net.minecraft.class_1792.class_1793 p, int durability) {
        Object set = ((top.dext.centurybridge.step.v1_20_5.mixin.CbComponentBuilderAccessor)
            ((top.dext.centurybridge.step.v1_20_5.mixin.CbItemPropertiesAccessor) p).cbS4$components())
            .cbS4$map().get(net.minecraft.class_9334.field_50072);
        return set == null || (Integer) set == 0 ? p.method_7895(durability) : p;
    }

    // ---------------------------------------------------------------- rarity

    /** item.getRarity(stack): the class's own override, else 1.20.4's body */
    public static net.minecraft.class_1814 getRarity(net.minecraft.class_1792 item, net.minecraft.class_1799 stack) {
        return item instanceof Rarity r ? r.method_7862(stack) : superGetRarity(item, stack);
    }

    /**
     * Item.getRarity(stack)'s 1.20.4 body: the item's rarity, one step up for an enchanted stack (common
     * and uncommon to rare, rare to epic).
     */
    public static net.minecraft.class_1814 superGetRarity(net.minecraft.class_1792 item, net.minecraft.class_1799 stack) {
        net.minecraft.class_1814 r = built(item).method_57830(net.minecraft.class_9334.field_50073, net.minecraft.class_1814.field_8906);
        if (!stack.method_7942()) {
            return r;
        }
        if (r == net.minecraft.class_1814.field_8906 || r == net.minecraft.class_1814.field_8907) {
            return net.minecraft.class_1814.field_8903;
        }
        return r == net.minecraft.class_1814.field_8903 ? net.minecraft.class_1814.field_8904 : r;
    }

    /** rarity.color (1.20.5 made the field private): its chat colour */
    public static net.minecraft.class_124 rarityColor(net.minecraft.class_1814 r) {
        return r.method_58413();
    }

    // ---------------------------------------------------------------- network

    /** item.shouldOverrideMultiplayerNbt(): the class's own override, else 1.20.4's (true) */
    public static boolean shouldOverrideMultiplayerNbt(net.minecraft.class_1792 item) {
        return !(item instanceof MultiplayerNbt m) || m.method_7887();
    }

    /** super.shouldOverrideMultiplayerNbt(): 1.20.4's Item answered true */
    public static boolean superShouldOverrideMultiplayerNbt(net.minecraft.class_1792 item) {
        return true;
    }

    /**
     * Whether 1.20.4 sent a stack of this item to clients without its tag (FriendlyByteBuf.writeItem sent
     * it only for an item that can be depleted or whose shouldOverrideMultiplayerNbt said so): only a
     * class overriding shouldOverrideMultiplayerNbt to false can, every other answers true.
     */
    public static boolean sendsBare(net.minecraft.class_1792 item) {
        return item instanceof MultiplayerNbt m && !m.method_7887() && !canBeDepleted(item);
    }

    private CbItemProps() {
    }
}
