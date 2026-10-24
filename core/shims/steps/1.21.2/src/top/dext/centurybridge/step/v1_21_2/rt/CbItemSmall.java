package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1657;
import net.minecraft.class_1745;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1806;
import net.minecraft.class_1937;
import net.minecraft.class_22;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2960;
import net.minecraft.class_5250;
import net.minecraft.class_9209;
import net.minecraft.class_9334;

/**
 * Small 1.21.1 item members 1.21.2 dropped, on 1.21.2:
 * <ul>
 * <li>ComplexItem / MapItem.getUpdatePacket(stack, level, player): the packet 1.21.1's ServerPlayer sent each tick
 *     for a complex item in the inventory (CbItemComplexMixin); MapItem's: its map's update for the player;</li>
 * <li>BannerPatternItem.getDisplayName(): translatable(getDescriptionId() + ".desc");</li>
 * <li>BundleItem(properties): 1.21.2's bundle takes the models of its open state; a 1.21.1 bundle has the plain
 *     bundle's.</li>
 * </ul>
 */
public final class CbItemSmall {
    private CbItemSmall() {
    }

    // ---- getUpdatePacket ----

    /** getUpdatePacket(stack, level, player), declared by a 1.21.1 item (a ComplexItem facade subclass, a MapItem subclass) */
    public interface Legacy_getUpdatePacket {
        class_2596<?> method_7757(class_1799 stack, class_1937 level, class_1657 player);
    }

    /** MapItem.getUpdatePacket(stack, level, player), called */
    public static class_2596<?> getUpdatePacket(class_1806 item, class_1799 stack, class_1937 level, class_1657 player) {
        if (item instanceof Legacy_getUpdatePacket legacy) {
            return legacy.method_7757(stack, level, player);
        }
        return superGetUpdatePacket(item, stack, level, player);
    }

    /** 1.21.1 MapItem's: the update of the stack's map for the player, null without one */
    public static class_2596<?> superGetUpdatePacket(class_1806 item, class_1799 stack, class_1937 level, class_1657 player) {
        class_9209 id = stack.method_57824(class_9334.field_49646);
        class_22 data = class_1806.method_7997(id, level);
        return data != null ? data.method_100(id, player) : null;
    }

    /**
     * Whether 1.21.1's ServerPlayer synchronised the stack's item with its own getUpdatePacket: a ComplexItem whose
     * isComplex() is true, or a MapItem subclass declaring getUpdatePacket (MapItem was a ComplexItem)
     */
    public static Legacy_getUpdatePacket complex(class_1792 item) {
        if (item instanceof Legacy_getUpdatePacket legacy
                && (item instanceof class_1806 || CbItemMembers.method_16698(item))) {
            return legacy;
        }
        return null;
    }

    // ---- BannerPatternItem.getDisplayName ----

    /** getDisplayName(), declared by a 1.21.1 BannerPatternItem subclass */
    public interface Legacy_getDisplayName {
        class_5250 method_7703();
    }

    public static class_5250 getDisplayName(class_1745 item) {
        if (item instanceof Legacy_getDisplayName legacy) {
            return legacy.method_7703();
        }
        return superGetDisplayName(item);
    }

    /** 1.21.1: translatable(getDescriptionId() + ".desc") */
    public static class_5250 superGetDisplayName(class_1745 item) {
        return class_2561.method_43471(item.method_7876() + ".desc");
    }

    // ---- BundleItem ----

    /** the plain bundle's open front model (1.21.2's Items.BUNDLE) */
    public static class_2960 bundleOpenFront() {
        return class_2960.method_60656("bundle_open_front");
    }

    /** the plain bundle's open back model */
    public static class_2960 bundleOpenBack() {
        return class_2960.method_60656("bundle_open_back");
    }
}
