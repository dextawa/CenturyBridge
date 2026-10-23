package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Set;
import net.minecraft.class_1792;
import net.minecraft.class_9285;
import net.minecraft.class_9323;
import net.minecraft.class_9331;
import net.minecraft.class_9334;

/**
 * A converted 1.21.1 item's default components as its class's 1.21.1 overrides answer them (see
 * CbItemMembers): 1.21.2 reads ENCHANTABLE and ATTRIBUTE_MODIFIERS where 1.21.1 asked the item's
 * getEnchantmentValue() and getDefaultAttributeModifiers(). Every other component is the item's built one.
 * Installed by CbItemMembersMixin only for a class that overrides one of those members, at the end of its
 * construction; the overrides are asked on each read, as 1.21.1 asked them.
 */
public final class CbItemMemberComponents implements class_9323 {

    private final class_1792 item;
    private final class_9323 built;

    public CbItemMemberComponents(class_1792 item, class_9323 built) {
        this.item = item;
        this.built = built;
    }

    /** whether a class overrides a 1.21.1 member these components answer */
    public static boolean needed(class_1792 item) {
        return item instanceof CbItemMembers.LegacyEnchantmentValue || item instanceof CbItemMembers.LegacyDefaultModifiers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T method_57829(class_9331<? extends T> type) {
        if (type == class_9334.field_53695 && item instanceof CbItemMembers.LegacyEnchantmentValue) {
            return (T) CbItemMembers.enchantable(item, built.method_57829(class_9334.field_53695));
        }
        if (type == class_9334.field_49636 && item instanceof CbItemMembers.LegacyDefaultModifiers l) {
            class_9285 m = l.method_7844();
            return (T) (m == null ? class_9285.field_49326 : m);
        }
        return built.method_57829(type);
    }

    @Override
    public Set<class_9331<?>> method_57831() {
        Set<class_9331<?>> keys = new it.unimi.dsi.fastutil.objects.ReferenceArraySet<>(built.method_57831());
        if (item instanceof CbItemMembers.LegacyEnchantmentValue) {
            if (method_57829(class_9334.field_53695) != null) {
                keys.add(class_9334.field_53695);
            } else {
                keys.remove(class_9334.field_53695);
            }
        }
        if (item instanceof CbItemMembers.LegacyDefaultModifiers) {
            keys.add(class_9334.field_49636);
        }
        return keys;
    }

    /**
     * replace the components an item was built with (CbPendingIds' registration backfill), keeping the view of a
     * class whose overrides answer components -- rebuilding from the view would freeze its answers
     */
    public static void setBuilt(class_1792 item, class_9323 built) {
        ((CbPendingIdsItems.ItemFields) item).cb$setComponents(
            item.method_57347() instanceof CbItemMemberComponents ? new CbItemMemberComponents(item, built) : built);
    }

    /** the components the item was built with */
    public class_9323 built() {
        return built;
    }
}
