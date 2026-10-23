package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.serialization.Codec;
import net.minecraft.class_3542;

/**
 * 1.21.1's GenerationStep.Carving (net/minecraft/class_2893$class_2894; classRenames), gone in 1.21.2, which keeps one
 * carving step. 1.21.1's enum as it was: AIR ("air") and LIQUID ("liquid"), its codec and names. The constants keep
 * their source names (name(), valueOf), and the intermediary fields a converted mod reads are the same objects.
 * What a step selects on 1.21.2 is CbLevelCarving's: AIR is 1.21.2's one step, LIQUID 1.21.1's second one (which
 * 1.21.1's own chunk generation never ran either).
 */
public enum GenerationStepCarving implements class_3542 {
    AIR("air"),
    LIQUID("liquid");

    public static final GenerationStepCarving field_13169 = AIR;
    public static final GenerationStepCarving field_13166 = LIQUID;
    public static final Codec<GenerationStepCarving> field_24770 = class_3542.method_28140(GenerationStepCarving::values);

    public final String field_13167;

    GenerationStepCarving(String name) {
        this.field_13167 = name;
    }

    /** getName() */
    public String method_12581() {
        return this.field_13167;
    }

    /** getSerializedName() */
    @Override
    public String method_15434() {
        return this.field_13167;
    }
}
