package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.class_10129;
import net.minecraft.class_10215;
import net.minecraft.class_3902;
import net.minecraft.class_8103;
import net.minecraft.class_9331;
import net.minecraft.class_9334;

/**
 * Data component values whose type 1.21.2 changed under a component type a 1.21.1 mod still names, converted where
 * mods hand values in and take them out (ItemStack / DataComponentHolder / DataComponentMap / patch and map builders /
 * Item.Properties):
 * <ul>
 * <li>RECIPES (the knowledge book's): 1.21.1's recipe ids, 1.21.2's recipe keys;</li>
 * <li>OMINOUS_BOTTLE_AMPLIFIER: 1.21.1's Integer, 1.21.2's OminousBottleAmplifier(value);</li>
 * <li>FIRE_RESISTANT (1.21.1, a Unit marker): 1.21.2's DAMAGE_RESISTANT for the is_fire damage tag -- what 1.21.2's
 *     Item.Properties.fireResistant() sets. A 1.21.1 read of it sees the marker for that resistance and nothing for a
 *     resistance to other damage, which 1.21.1 could not express.</li>
 * </ul>
 * A value going in is converted by its component type ({@link #toW}); one coming out by what it is ({@link #toV}),
 * since only these components hold such values: a recipe key list, an OminousBottleAmplifier, a DamageResistant.
 * Each converter passes anything else through unchanged, and each undoes the other.
 */
public final class CbItemComponentValues {
    private CbItemComponentValues() {
    }

    /** DataComponents.FIRE_RESISTANT (fieldGetters): 1.21.2's DAMAGE_RESISTANT, whose values convert at the sites */
    public static class_9331<?> field_50076() {
        return class_9334.field_54273;
    }

    /** a 1.21.1 value handed in under a component type, as 1.21.2's */
    public static Object toW(class_9331<?> type, Object value) {
        if (value == null) {
            return null;
        }
        if (type == class_9334.field_49613 && value instanceof List<?> list) {
            return CbItemGeneric.recipeKeys(list);
        }
        if (type == class_9334.field_50238 && value instanceof Integer amplifier) {
            return new class_10129(amplifier);
        }
        if (type == class_9334.field_54273 && value == class_3902.field_17274) {
            return new class_10215(class_8103.field_42246);
        }
        return value;
    }

    /** a 1.21.2 component value taken out, as 1.21.1's */
    public static Object toV(Object value) {
        if (value instanceof class_10129 amplifier) {
            return amplifier.comp_3090();
        }
        if (value instanceof class_10215 resistant) {
            return resistant.comp_3210() == class_8103.field_42246 ? class_3902.field_17274 : null;
        }
        if (value instanceof List<?> list) {
            return CbItemGeneric.recipeIds(list);
        }
        return value;
    }

    /** a patch's Optional value taken out */
    public static Optional<?> toV(Optional<?> value) {
        return value == null ? null : value.map(CbItemComponentValues::toV);
    }

    /** update(type, default, function): the 1.21.1 function sees and answers 1.21.1 values */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static UnaryOperator<?> operator(class_9331<?> type, UnaryOperator<?> function) {
        UnaryOperator f = function;
        return value -> toW(type, f.apply(toV(value)));
    }

    /** update(type, default, argument, function) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BiFunction<?, ?, ?> biFunction(class_9331<?> type, BiFunction<?, ?, ?> function) {
        BiFunction f = function;
        return (value, argument) -> toW(type, f.apply(toV(value), argument));
    }
}
