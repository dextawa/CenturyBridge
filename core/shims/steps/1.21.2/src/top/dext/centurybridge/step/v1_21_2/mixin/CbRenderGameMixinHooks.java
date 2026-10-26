package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.2's own GameRenderer methods that goes with {@link CbRenderGameMixin} (its restored 1.21.1
 * members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them):
 * renderItemInHand and renderLevel set the perspective projection through 1.21.1's resetProjectionMatrix again.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_757.class)
public abstract class CbRenderGameMixinHooks {

    @Redirect(method = {"method_3172", "method_3188"}, require = 2, at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lorg/joml/Matrix4f;Lnet/minecraft/class_10366;)V"))
    private void cb$v1_21_2$resetProjectionMatrix(org.joml.Matrix4f matrix, net.minecraft.class_10366 type) {
        if (type == net.minecraft.class_10366.field_54953) {
            ((CbPrioritySplitClient.GameProjection) (Object) this).cb$v1_21_2$resetProjectionMatrix(matrix);
        } else {
            com.mojang.blaze3d.systems.RenderSystem.setProjectionMatrix(matrix, type);
        }
    }
}
