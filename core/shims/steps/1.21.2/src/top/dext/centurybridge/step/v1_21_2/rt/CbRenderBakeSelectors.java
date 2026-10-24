package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * MultiPartBakedModel(selectors): 1.21.1 took a list of (state predicate, model) commons-lang Pairs, 1.21.2 a list of
 * its Selector records of the same two values. The list a 1.21.1 caller builds is converted element by element (an
 * element already a Selector passes: erasure cannot tell who built the list).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBakeSelectors {

    private CbRenderBakeSelectors() {
    }

    @SuppressWarnings("unchecked")
    public static List<net.minecraft.class_1095.class_10204> selectors(List<?> selectors) {
        List<net.minecraft.class_1095.class_10204> out = new ArrayList<>(selectors.size());
        for (Object o : selectors) {
            if (o instanceof org.apache.commons.lang3.tuple.Pair<?, ?> p) {
                out.add(new net.minecraft.class_1095.class_10204((Predicate<net.minecraft.class_2680>) p.getLeft(), (net.minecraft.class_1087) p.getRight()));
            } else {
                out.add((net.minecraft.class_1095.class_10204) o);
            }
        }
        return out;
    }

}
