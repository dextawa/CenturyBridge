package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * VertexMultiConsumer.Double's two consumers: 1.20.6's forwarded endVertex and defaultColor to both (CbRenderWrappers).
 */
@Mixin(targets = "net.minecraft.class_4720$class_4589")
public interface CbRenderDoubleAccess {

    @Accessor("field_21685")
    net.minecraft.class_4588 cb$first();

    @Accessor("field_21686")
    net.minecraft.class_4588 cb$second();
}
