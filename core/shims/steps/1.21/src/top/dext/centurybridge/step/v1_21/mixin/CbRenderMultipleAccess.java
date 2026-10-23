package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * VertexMultiConsumer.Multiple's consumers: 1.20.6's forwarded endVertex and defaultColor to each (CbRenderWrappers).
 */
@Mixin(targets = "net.minecraft.class_4720$class_6189")
public interface CbRenderMultipleAccess {

    @Accessor("comp_2847")
    net.minecraft.class_4588[] cb$delegates();
}
