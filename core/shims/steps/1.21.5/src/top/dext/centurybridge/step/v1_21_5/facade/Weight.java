package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.class_155;
import net.minecraft.class_156;
import org.slf4j.Logger;

/**
 * 1.21.4's net.minecraft.util.random.Weight (net/minecraft/class_6007), gone in 1.21.5: weights are plain ints there
 * (Weighted.weight(), WeightedList.Builder.add(value, int)). Converted references name this class (classRenames); the
 * wiring turns it into W's int where a W member takes one (Weighted's constructor, WeightedRandom's weigher) and back.
 * Every 1.21.4 member is here, public (a facade's members stand in for the class's; access wideners never reach a
 * CenturyBridge class). Bodies from javap -c -p 1.21.4 class_6007: of(1) answers the shared ONE instance; of(n)
 * validates n (validateWeight: IllegalArgumentException("Weight should be >= 0") through Util.pauseInIde; a warning on 0
 * when running in the IDE) and builds a new Weight; CODEC is Codec.INT xmapped through of / asInt; toString / hashCode /
 * equals are the int's.
 */
public class Weight {
    public static final Codec<Weight> field_29927 = Codec.INT.xmap(Weight::method_34977, Weight::method_34976);
    /** ONE */
    public static final Weight field_29928 = new Weight(1);
    /** LOGGER */
    public static final Logger field_29929 = LogUtils.getLogger();

    /** value */
    public final int field_29930;

    public Weight(int weight) {
        this.field_29930 = weight;
    }

    /** of(int) */
    public static Weight method_34977(int weight) {
        if (weight == 1) {
            return field_29928;
        }
        method_34978(weight);
        return new Weight(weight);
    }

    /** asInt() */
    public int method_34976() {
        return this.field_29930;
    }

    /** validateWeight(int) */
    public static void method_34978(int weight) {
        if (weight < 0) {
            throw class_156.method_22320(new IllegalArgumentException("Weight should be >= 0"));
        }
        if (weight == 0 && class_155.field_1125) {
            field_29929.warn("Found 0 weight, make sure this is intentional!");
        }
    }

    @Override
    public String toString() {
        return Integer.toString(this.field_29930);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.field_29930);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Weight w && this.field_29930 == w.field_29930;
    }
}
