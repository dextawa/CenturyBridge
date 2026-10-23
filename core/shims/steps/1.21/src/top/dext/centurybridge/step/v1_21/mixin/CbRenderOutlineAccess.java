package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * OutlineBufferSource.EntityOutlineGenerator's delegate: 1.21's writes position, color and UV on to it as they come, so a 1.20.6 endVertex() goes on to it; 1.20.6's ignored defaultColor, as 1.21's keeps its own color (CbRenderWrappers).
 */
@Mixin(targets = "net.minecraft.class_4618$class_4586")
public interface CbRenderOutlineAccess {

    @Accessor("comp_2851")
    net.minecraft.class_4588 cb$delegate();
}
