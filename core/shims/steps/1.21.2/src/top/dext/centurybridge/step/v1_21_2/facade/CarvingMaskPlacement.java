package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_5444;
import net.minecraft.class_5819;
import net.minecraft.class_6797;
import net.minecraft.class_6798;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelCarving;

/**
 * 1.21.1's CarvingMaskPlacement (net/minecraft/class_5851; classRenames), gone in 1.21.2: a placement at every position
 * of the step's carving mask of the chunk. 1.21.1's body; the type is 1.21.1's "minecraft:carving_mask", registered by
 * this layer (CbLevelCarving), so 1.21.1 placed features naming it load as they did.
 */
public class CarvingMaskPlacement extends class_6797 {
    public static final MapCodec<CarvingMaskPlacement> field_28934 = GenerationStepCarving.field_24770.fieldOf("step")
        .xmap(CarvingMaskPlacement::new, p -> p.field_28935);

    public final GenerationStepCarving field_28935;

    public CarvingMaskPlacement(GenerationStepCarving step) {
        this.field_28935 = step;
    }

    /** forStep(step) */
    public static CarvingMaskPlacement method_39619(GenerationStepCarving step) {
        return new CarvingMaskPlacement(step);
    }

    @Override
    public Stream<class_2338> method_14452(class_5444 context, class_5819 random, class_2338 pos) {
        class_1923 chunk = new class_1923(pos);
        return CbLevelCarving.method_30459(context, chunk, this.field_28935).method_38866(chunk);
    }

    @Override
    public class_6798<?> method_39615() {
        return CbLevelCarving.field_35751();
    }
}
