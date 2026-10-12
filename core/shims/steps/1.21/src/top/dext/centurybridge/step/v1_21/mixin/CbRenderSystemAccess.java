package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * RenderSystem's render thread and shader light directions: 1.20.6's initGameThread checked the render thread
 * was set, its _setShaderLights wrote the light directions without a thread check (CbRenderSystem).
 */
@Mixin(com.mojang.blaze3d.systems.RenderSystem.class)
public interface CbRenderSystemAccess {

    @Accessor("renderThread")
    static Thread cb$renderThread() {
        throw new AssertionError();
    }

    @Accessor("shaderLightDirections")
    static org.joml.Vector3f[] cb$shaderLightDirections() {
        throw new AssertionError();
    }
}
