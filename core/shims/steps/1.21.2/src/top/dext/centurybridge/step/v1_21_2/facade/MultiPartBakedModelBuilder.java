package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's MultiPartBakedModel.Builder (net/minecraft/class_1095$class_1096, classRenames), gone in 1.21.2: collects
 * (state predicate, model) selectors and builds a MultiPartBakedModel of them, in order. 1.21.1's bodies; 1.21.2's
 * MultiPartBakedModel takes the same pairs as its Selector records.
 */
@Environment(EnvType.CLIENT)
public class MultiPartBakedModelBuilder {

    /** selectors */
    public final List<org.apache.commons.lang3.tuple.Pair<Predicate<net.minecraft.class_2680>, net.minecraft.class_1087>> field_5432 = new ArrayList<>();

    public MultiPartBakedModelBuilder() {
    }

    /** add(predicate, model) */
    public void method_4749(Predicate<net.minecraft.class_2680> predicate, net.minecraft.class_1087 model) {
        this.field_5432.add(org.apache.commons.lang3.tuple.Pair.of(predicate, model));
    }

    /** build() */
    public net.minecraft.class_1087 method_4750() {
        List<net.minecraft.class_1095.class_10204> selectors = new ArrayList<>();
        for (org.apache.commons.lang3.tuple.Pair<Predicate<net.minecraft.class_2680>, net.minecraft.class_1087> p : this.field_5432) {
            selectors.add(new net.minecraft.class_1095.class_10204(p.getLeft(), p.getRight()));
        }
        return new net.minecraft.class_1095(selectors);
    }
}
