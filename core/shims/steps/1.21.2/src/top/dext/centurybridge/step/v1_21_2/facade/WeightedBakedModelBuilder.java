package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's WeightedBakedModel.Builder (net/minecraft/class_1097$class_1098, classRenames), gone in 1.21.2: collects
 * weighted models; build() answers null for none, the model itself for one, else a WeightedBakedModel. 1.21.1's bodies;
 * 1.21.2's WeightedBakedModel takes a SimpleWeightedRandomList of the same weighted entries.
 */
@Environment(EnvType.CLIENT)
public class WeightedBakedModelBuilder {

    /** list */
    public final List<net.minecraft.class_6008.class_6010<net.minecraft.class_1087>> field_5436 = new ArrayList<>();

    public WeightedBakedModelBuilder() {
    }

    /** add(model, weight) */
    public WeightedBakedModelBuilder method_4752(net.minecraft.class_1087 model, int weight) {
        this.field_5436.add(net.minecraft.class_6008.method_34980(model, weight));
        return this;
    }

    /** build() */
    public net.minecraft.class_1087 method_4751() {
        if (this.field_5436.isEmpty()) {
            return null;
        }
        if (this.field_5436.size() == 1) {
            return this.field_5436.get(0).comp_2542();
        }
        net.minecraft.class_6005.class_6006<net.minecraft.class_1087> b = net.minecraft.class_6005.method_34971();
        for (net.minecraft.class_6008.class_6010<net.minecraft.class_1087> e : this.field_5436) {
            b.method_34975(e.comp_2542(), e.comp_2543().method_34976());
        }
        return new net.minecraft.class_1097(b.method_34974());
    }
}
