package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * SheetedDecalTextureGenerator's delegate: 1.21's decal writes each element on to it as it comes, so a 1.20.6 endVertex() goes on to it (CbRenderWrappers).
 */
@Mixin(net.minecraft.class_4583.class)
public interface CbRenderDecalAccess {

    @Accessor("field_20866")
    net.minecraft.class_4588 cb$delegate();
}
