package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Generic drift of the step's rendering area that changes the elements a value holds (not only how a renderer is
 * typed, which family 9 of gen_s7render.py witnesses):
 * - MultiPartBakedModel(selectors): see CbRenderBakeSelectors (client).
 * - BlockModelGenerators.MULTIFACE_GENERATOR: 1.21.1 keyed each face's variant function by the face's boolean block
 *   state property (BlockStateProperties.NORTH ... DOWN, in that order), 1.21.2 by the Direction. The same list with
 *   each direction's property (PipeBlock.PROPERTY_BY_DIRECTION, the same property objects), in 1.21.2's order.
 */
public final class CbRenderGeneric {

    private static List<Pair<net.minecraft.class_2746, Function<net.minecraft.class_2960, net.minecraft.class_4935>>> multiface;

    private CbRenderGeneric() {
    }

    /** BlockModelGenerators.MULTIFACE_GENERATOR as 1.21.1 typed it */
    public static synchronized List<Pair<net.minecraft.class_2746, Function<net.minecraft.class_2960, net.minecraft.class_4935>>> field_28548() {
        if (multiface == null) {
            List<Pair<net.minecraft.class_2746, Function<net.minecraft.class_2960, net.minecraft.class_4935>>> l = new ArrayList<>();
            for (Pair<net.minecraft.class_2350, Function<net.minecraft.class_2960, net.minecraft.class_4935>> p : net.minecraft.class_4910.field_28548) {
                l.add(Pair.of(net.minecraft.class_2429.field_11329.get(p.getFirst()), p.getSecond()));
            }
            multiface = List.copyOf(l);
        }
        return multiface;
    }
}
