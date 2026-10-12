package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Set;

/**
 * An item's default components as its 1.20.4 class answered them, for a mod item overriding 1.20.4's
 * isEdible / getFoodProperties / isFireResistant (mixin/CbItemMixin puts it in place of the components
 * the properties built): 1.20.5 reads an item's food and fire resistance off a stack's components, which
 * fall back to these, where 1.20.4 asked the item -- so the food is the class's isEdible() and
 * getFoodProperties() as they answer now, the fire resistance its isFireResistant(). Every other
 * component is the built one.
 */
public final class CbItemComponents implements net.minecraft.class_9323 {

    private final net.minecraft.class_1792 item;
    private final net.minecraft.class_9323 built;

    public CbItemComponents(net.minecraft.class_1792 item, net.minecraft.class_9323 built) {
        this.item = item;
        this.built = built;
    }

    /** the components the item's properties built */
    public net.minecraft.class_9323 built() {
        return built;
    }

    private net.minecraft.class_4174 food() {
        return CbItemProps.isEdible(item) ? CbItemProps.getFoodProperties(item) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T method_57829(net.minecraft.class_9331<? extends T> type) {
        if (type == net.minecraft.class_9334.field_50075) {
            return (T) food();
        }
        if (type == net.minecraft.class_9334.field_50076) {
            return CbItemProps.isFireResistant(item) ? (T) net.minecraft.class_3902.field_17274 : null;
        }
        return built.method_57829(type);
    }

    @Override
    public Set<net.minecraft.class_9331<?>> method_57831() {
        Set<net.minecraft.class_9331<?>> keys = new it.unimi.dsi.fastutil.objects.ReferenceArraySet<>(built.method_57831());
        if (food() != null) {
            keys.add(net.minecraft.class_9334.field_50075);
        } else {
            keys.remove(net.minecraft.class_9334.field_50075);
        }
        if (CbItemProps.isFireResistant(item)) {
            keys.add(net.minecraft.class_9334.field_50076);
        } else {
            keys.remove(net.minecraft.class_9334.field_50076);
        }
        return keys;
    }

    @Override
    public String toString() {
        return "CbItemComponents[" + item + ": " + built + "]";
    }
}
