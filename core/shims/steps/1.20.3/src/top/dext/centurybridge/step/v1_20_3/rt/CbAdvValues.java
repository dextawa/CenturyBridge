package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Optional;

/**
 * Value conversions for the vanilla trigger instances and triggers whose 1.20.2 constructors and
 * methods took values 1.20.3 holds differently: a block or potion that 1.20.2 kept as a nullable
 * object (null meaning "any") is an Optional holder in 1.20.3, a dimension key an Optional, the
 * three slot bounds of InventoryChangeTrigger one Slots record; BrewedPotionTrigger passes the
 * potion as its registry holder.
 */
public final class CbAdvValues {

    /** a 1.20.2 block condition (null: any block) as 1.20.3's optional holder */
    public static Optional<net.minecraft.class_6880<net.minecraft.class_2248>> block(net.minecraft.class_2248 block) {
        return block == null ? Optional.empty() : Optional.of(block.method_40142());
    }

    /** a 1.20.2 potion condition (null: any potion) as 1.20.3's optional holder */
    public static Optional<net.minecraft.class_6880<net.minecraft.class_1842>> potionCondition(net.minecraft.class_1842 potion) {
        return potion == null ? Optional.empty() : Optional.of(potionHolder(potion));
    }

    /**
     * A potion as the holder 1.20.3 passes it in: the registry's reference for a registered potion
     * (one object per potion, as 1.20.2 compared potions by identity), a direct holder otherwise.
     */
    public static net.minecraft.class_6880<net.minecraft.class_1842> potionHolder(net.minecraft.class_1842 potion) {
        return net.minecraft.class_7923.field_41179.method_47983(potion);
    }

    /** the potion a 1.20.3 holder stands for, as 1.20.2 passed it */
    public static net.minecraft.class_1842 potion(net.minecraft.class_6880<net.minecraft.class_1842> potion) {
        return potion.comp_349();
    }

    /** a 1.20.2 dimension condition (null: any) as 1.20.3's Optional */
    public static Optional<net.minecraft.class_5321<net.minecraft.class_1937>> dimension(
            net.minecraft.class_5321<net.minecraft.class_1937> dimension) {
        return Optional.ofNullable(dimension);
    }

    /** InventoryChangeTrigger's 1.20.2 occupied / full / empty slot bounds as 1.20.3's Slots */
    public static net.minecraft.class_2066.class_2068.class_8948 slots(net.minecraft.class_2096.class_2100 occupied,
            net.minecraft.class_2096.class_2100 full, net.minecraft.class_2096.class_2100 empty) {
        return new net.minecraft.class_2066.class_2068.class_8948(occupied, full, empty);
    }

    private CbAdvValues() {
    }
}
