package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ShaderInstance's uniforms by name, for 1.20.4's INVERSE_VIEW_ROTATION_MATRIX field (rt/CbClientRender) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5944.class)
public interface CbClientShaderAccessor {

    /** uniformMap */
    @Accessor("field_29492")
    java.util.Map<String, net.minecraft.class_284> cb$uniforms();
}
