package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 1.21.1 GameRenderer members 1.21.2 dropped or reshaped:
 * - resetProjectionMatrix(matrix): RenderSystem.setProjectionMatrix(matrix, DISTANCE_TO_ORIGIN) -- 1.21.2's
 *   perspective projection type, whose sorting that is. 1.21.1's renderItemInHand and renderLevel set the projection
 *   through it (an overridable call); 1.21.2 calls RenderSystem directly there with the perspective type, so those two
 *   calls are routed through it again (a mod GameRenderer's override sees them as in 1.21.1).
 * - getProjectionMatrix(double fov): 1.21.2's getProjectionMatrix(float) with the same field of view (1.21.1's
 *   callers passed a double getFov, 1.21.2's getFov is a float).
 * - getMapRenderer(): 1.21.2 keeps the one map renderer on Minecraft.
 * - processBlurEffect(partialTick): 1.21.2's processBlurEffect(); its blur chain has no time uniform to take the tick.
 * And the resource pool 1.21.2 renders the level with, for CbRenderFrames.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_757.class)
public abstract class CbRenderGameMixin implements top.dext.centurybridge.step.v1_21_2.rt.CbRenderFrames.ResourcePool {

    @Shadow
    @Final
    private net.minecraft.class_9920 field_53066;

    @Override
    public net.minecraft.class_9922 cb$resourcePool() {
        return this.field_53066;
    }

    public void method_22709(org.joml.Matrix4f matrix) {
        com.mojang.blaze3d.systems.RenderSystem.setProjectionMatrix(matrix, net.minecraft.class_10366.field_54953);
    }

    @Redirect(method = {"method_3172", "method_3188"}, require = 2, at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lorg/joml/Matrix4f;Lnet/minecraft/class_10366;)V"))
    private void cb$resetProjectionMatrix(org.joml.Matrix4f matrix, net.minecraft.class_10366 type) {
        if (type == net.minecraft.class_10366.field_54953) {
            this.method_22709(matrix);
        } else {
            com.mojang.blaze3d.systems.RenderSystem.setProjectionMatrix(matrix, type);
        }
    }

    public org.joml.Matrix4f method_22973(double fov) {
        return ((net.minecraft.class_757) (Object) this).method_22973((float) fov);
    }

    public net.minecraft.class_330 method_3194() {
        return net.minecraft.class_310.method_1551().method_61965();
    }

    public void method_57796(float partialTick) {
        ((net.minecraft.class_757) (Object) this).method_57796();
    }
}
