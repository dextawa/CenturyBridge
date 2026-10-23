package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemColors;

/**
 * 1.21.3 ItemColors (class_325, gone in 1.21.4: vanilla's tints live in the item model definitions). An instance
 * maps items to their {@link ItemColor}; getColor answers -1 (no tint) for an item without one. createDefault
 * registers 1.21.3's vanilla providers, each computing what 1.21.3's did from the same components; spawn eggs,
 * whose colours 1.21.4 moved into their item model definitions, answer the tint 1.21.4's definition gives.
 */
@Environment(EnvType.CLIENT)
public class ItemColors {

    /** DEFAULT */
    public static final int field_32165 = -1;
    /** itemColors */
    public final net.minecraft.class_2361<ItemColor> field_1996 = new net.minecraft.class_2361<>(32);

    public ItemColors() {
    }

    /** createDefault */
    public static ItemColors method_1706(net.minecraft.class_324 blockColors) {
        ItemColors colors = new ItemColors();
        colors.method_1708((stack, tint) -> tint > 0 ? -1 : net.minecraft.class_9282.method_57470(stack, -6265536),
            net.minecraft.class_1802.field_8267, net.minecraft.class_1802.field_8577, net.minecraft.class_1802.field_8570,
            net.minecraft.class_1802.field_8370, net.minecraft.class_1802.field_18138);
        colors.method_1708((stack, tint) -> tint != 1 ? -1 : net.minecraft.class_9282.method_57470(stack, 0),
            net.minecraft.class_1802.field_47831);
        colors.method_1708((stack, tint) -> net.minecraft.class_1933.method_8377(0.5, 1.0),
            net.minecraft.class_2246.field_10214, net.minecraft.class_2246.field_10313);
        colors.method_1708(ItemColors::fireworkStar, net.minecraft.class_1802.field_8450);
        colors.method_1708((stack, tint) -> tint > 0 ? -1 : net.minecraft.class_9848.method_61334(
                stack.method_57825(net.minecraft.class_9334.field_49651, net.minecraft.class_1844.field_49274).method_8064()),
            net.minecraft.class_1802.field_8574, net.minecraft.class_1802.field_8436, net.minecraft.class_1802.field_8150,
            net.minecraft.class_1802.field_8087);
        for (net.minecraft.class_1826 egg : net.minecraft.class_1826.method_8017()) {
            colors.method_1708(CbItemColors::definitionTint, egg);
        }
        colors.method_1708((stack, tint) -> blockColors.method_1697(
                ((net.minecraft.class_1747) stack.method_7909()).method_7711().method_9564(), null, null, tint),
            net.minecraft.class_2246.field_10219, net.minecraft.class_2246.field_10479, net.minecraft.class_2246.field_10112,
            net.minecraft.class_2246.field_10597, net.minecraft.class_2246.field_10503, net.minecraft.class_2246.field_9988,
            net.minecraft.class_2246.field_10539, net.minecraft.class_2246.field_10335, net.minecraft.class_2246.field_10098,
            net.minecraft.class_2246.field_10035, net.minecraft.class_2246.field_10588);
        // FoliageColor.getMangroveColor(): 1.21.4's FOLIAGE_MANGROVE constant, the same value
        colors.method_1708((stack, tint) -> net.minecraft.class_1926.field_55218, net.minecraft.class_2246.field_37551);
        colors.method_1708((stack, tint) -> tint == 0 ? -1 : net.minecraft.class_9848.method_61334(
                stack.method_57825(net.minecraft.class_9334.field_49645, net.minecraft.class_9294.field_49352).comp_2409()),
            net.minecraft.class_1802.field_8204);
        return colors;
    }

    private static int fireworkStar(net.minecraft.class_1799 stack, int tint) {
        if (tint != 1) {
            return -1;
        }
        net.minecraft.class_9283 explosion = stack.method_57824(net.minecraft.class_9334.field_49615);
        it.unimi.dsi.fastutil.ints.IntList list = explosion != null ? explosion.comp_2387() : it.unimi.dsi.fastutil.ints.IntList.of();
        int n = list.size();
        if (n == 0) {
            return -7697782;
        }
        if (n == 1) {
            return net.minecraft.class_9848.method_61334(list.getInt(0));
        }
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < n; i++) {
            int c = list.getInt(i);
            r += net.minecraft.class_9848.method_61327(c);
            g += net.minecraft.class_9848.method_61329(c);
            b += net.minecraft.class_9848.method_61331(c);
        }
        return net.minecraft.class_9848.method_61323(r / n, g / n, b / n);
    }

    /** getColor */
    public int method_1704(net.minecraft.class_1799 stack, int tintIndex) {
        ItemColor color = this.field_1996.method_10200(net.minecraft.class_7923.field_41178.method_10206(stack.method_7909()));
        return color == null ? -1 : color.getColor(stack, tintIndex);
    }

    /** register */
    public void method_1708(ItemColor color, net.minecraft.class_1935... items) {
        for (net.minecraft.class_1935 item : items) {
            this.field_1996.method_10203(color, net.minecraft.class_1792.method_7880(item.method_8389()));
        }
    }
}
