package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.function.Supplier;
import net.minecraft.class_1802;
import net.minecraft.class_1856;
import net.minecraft.class_1935;
import net.minecraft.class_2248;
import net.minecraft.class_3481;
import net.minecraft.class_3489;
import net.minecraft.class_6862;
import net.minecraft.class_7923;

/**
 * 1.21.1's Tiers (net/minecraft/class_1834, classRenames): the vanilla tiers, transcribed from its bytecode
 * (incorrect blocks, uses, speed, attack damage bonus, enchantment value, repair ingredient). The repair
 * ingredient is read once, when first asked (1.21.1 memoized the supplier). The intermediary field names are
 * kept as aliases, which is what 1.21.1 bytecode reads.
 */
public enum Tiers implements Tier {
    WOOD(class_3481.field_49930, 59, 2.0F, 0.0F, 15, () -> tag(class_3489.field_15537)),
    STONE(class_3481.field_49928, 131, 4.0F, 1.0F, 5, () -> tag(class_3489.field_23802)),
    IRON(class_3481.field_49927, 250, 6.0F, 2.0F, 14, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8620})),
    DIAMOND(class_3481.field_49926, 1561, 8.0F, 3.0F, 10, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8477})),
    GOLD(class_3481.field_49929, 32, 12.0F, 0.0F, 22, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8695})),
    NETHERITE(class_3481.field_49925, 2031, 9.0F, 4.0F, 15, () -> class_1856.method_8091(new class_1935[] {class_1802.field_22020}));

    public static final Tiers field_8922 = WOOD;
    public static final Tiers field_8927 = STONE;
    public static final Tiers field_8923 = IRON;
    public static final Tiers field_8930 = DIAMOND;
    public static final Tiers field_8929 = GOLD;
    public static final Tiers field_22033 = NETHERITE;

    /** incorrectBlocksForDrops (public: an access widener never reaches a CenturyBridge class) */
    public final class_6862<class_2248> field_50006;
    /** uses */
    public final int field_8924;
    /** speed */
    public final float field_8932;
    /** damage */
    public final float field_8931;
    /** enchantmentValue */
    public final int field_8933;
    /** repairIngredient */
    public final Supplier<class_1856> field_8928;

    Tiers(class_6862<class_2248> incorrectBlocksForDrops, int uses, float speed, float damage, int enchantmentValue,
          Supplier<class_1856> repairIngredient) {
        this.field_50006 = incorrectBlocksForDrops;
        this.field_8924 = uses;
        this.field_8932 = speed;
        this.field_8931 = damage;
        this.field_8933 = enchantmentValue;
        this.field_8928 = com.google.common.base.Suppliers.memoize(repairIngredient::get);
    }

    /** Ingredient.of(tag) */
    private static class_1856 tag(class_6862<net.minecraft.class_1792> tag) {
        return class_1856.method_8106(class_7923.method_62715(class_7923.field_41178).method_46735(tag));
    }

    @Override
    public int method_8025() {
        return field_8924;
    }

    @Override
    public float method_8027() {
        return field_8932;
    }

    @Override
    public float method_8028() {
        return field_8931;
    }

    @Override
    public class_6862<class_2248> method_58419() {
        return field_50006;
    }

    @Override
    public int method_8026() {
        return field_8933;
    }

    @Override
    public class_1856 method_8023() {
        return field_8928.get();
    }
}
