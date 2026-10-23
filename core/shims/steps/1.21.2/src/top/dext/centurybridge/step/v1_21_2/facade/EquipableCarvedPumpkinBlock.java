package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.serialization.MapCodec;
import net.minecraft.class_1304;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2276;
import net.minecraft.class_4970;

/**
 * 1.21.1's EquipableCarvedPumpkinBlock (net/minecraft/class_8574; classRenames), gone in 1.21.2, where the carved
 * pumpkin is a plain CarvedPumpkinBlock and its item is equipped by its EQUIPPABLE component: a carved pumpkin that
 * is Equipable to the head. 1.21.1's bodies: the codec of the class, the head slot; the rest is CarvedPumpkinBlock's
 * and Equipable's (its block item gets an EQUIPPABLE component answered by these members, CbItemEquipableBlockItemMixin).
 *
 * <p>1.21.1's Blocks.CARVED_PUMPKIN was one; 1.21.2's is a CarvedPumpkinBlock, which this class cannot be the type
 * of (Jack o'Lantern is one too, and was not Equipable). {@code x instanceof EquipableCarvedPumpkinBlock} answers
 * 1.21.1's yes for it (instanceOfRedirects, {@link #isInstance}).
 */
public class EquipableCarvedPumpkinBlock extends class_2276 implements Equipable {
    public static final MapCodec<EquipableCarvedPumpkinBlock> field_46348 = class_2248.method_54094(EquipableCarvedPumpkinBlock::new);

    public EquipableCarvedPumpkinBlock(class_4970.class_2251 properties) {
        super(properties);
    }

    @Override
    public MapCodec<EquipableCarvedPumpkinBlock> method_53969() {
        return field_46348;
    }

    @Override
    public class_1304 method_7685() {
        return class_1304.field_6169;
    }

    /** {@code x instanceof EquipableCarvedPumpkinBlock}: this class, or 1.21.1's instance of it, the carved pumpkin */
    public static boolean isInstance(Object o) {
        return o instanceof EquipableCarvedPumpkinBlock || o == class_2246.field_10147;
    }
}
