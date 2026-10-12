package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1922;
import net.minecraft.class_2248;
import net.minecraft.class_2302;
import net.minecraft.class_2338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** CropBlock.getGrowthSpeed (protected static), for the 1.20.2 stem growth (CbWorldStemMixin) */
@Mixin(class_2302.class)
public interface CbWorldCropBlockAccess {

    @Invoker("method_9830")
    static float cb$getGrowthSpeed(class_2248 block, class_1922 level, class_2338 pos) {
        throw new AssertionError("mixin invoker");
    }
}
