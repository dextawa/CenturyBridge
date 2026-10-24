package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.class_1293;
import net.minecraft.class_9129;
import net.minecraft.class_9135;
import net.minecraft.class_9139;

/**
 * 1.21.1's FoodProperties.PossibleEffect (net/minecraft/class_4174$class_9423, classRenames), gone in 1.21.2 (an
 * ApplyStatusEffectsConsumeEffect of the item's Consumable took its place): an effect a food gives with a
 * probability. 1.21.1's record written out: effect() answers a copy, as 1.21.1's did; the codecs are 1.21.1's
 * (effect, probability in [0, 1] defaulting to 1). The fields are public under the intermediary names: an access
 * widener never reaches a CenturyBridge class.
 */
public final class FoodPossibleEffect {

    public static final Codec<FoodPossibleEffect> field_49996 = RecordCodecBuilder.create(i -> i.group(
        class_1293.field_48821.fieldOf("effect").forGetter(FoodPossibleEffect::comp_2496),
        Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(FoodPossibleEffect::comp_2497)
    ).apply(i, FoodPossibleEffect::new));

    public static final class_9139<class_9129, FoodPossibleEffect> field_49997 = class_9139.method_56435(
        class_1293.field_49207, FoodPossibleEffect::comp_2496, class_9135.field_48552, FoodPossibleEffect::comp_2497,
        FoodPossibleEffect::new);

    /** effect */
    public final class_1293 comp_2496;
    /** probability */
    public final float comp_2497;

    public FoodPossibleEffect(class_1293 effect, float probability) {
        this.comp_2496 = effect;
        this.comp_2497 = probability;
    }

    /** effect(): a copy */
    public class_1293 comp_2496() {
        return new class_1293(comp_2496);
    }

    /** probability() */
    public float comp_2497() {
        return comp_2497;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof FoodPossibleEffect e && Float.compare(comp_2497, e.comp_2497) == 0
            && Objects.equals(comp_2496, e.comp_2496);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(comp_2496) + Float.hashCode(comp_2497);
    }

    @Override
    public String toString() {
        return "PossibleEffect[effect=" + comp_2496 + ", probability=" + comp_2497 + "]";
    }
}
