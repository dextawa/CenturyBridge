package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26LegacySign;

/**
 * 26.1.2 AbstractSignRenderer.submitSignWithText drew the body before the text: pushPose, mulPose(body),
 * submitSign(pose, lightCoords, woodType, model, breakProgress, collector) = collector.submitModel(model, Unit,
 * pose, lightCoords, OverlayTexture.NO_OVERLAY, -1, sprite, sprites, 0, breakProgress), popPose (javap). 26.2's
 * submit draws only the text; the body a 26.1.2 sign carries (rt/CbS26LegacySign) is submitted the same way first.
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractSignRenderer.class)
public abstract class CbS26SignSubmitMixin {

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD"))
    private void cbS26$legacyBody(SignRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera,
            CallbackInfo ci) {
        CbS26LegacySign legacy = (CbS26LegacySign) state;
        if (legacy.cbS26$legacyModel() == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(legacy.cbS26$legacyBody());
        collector.submitModel(legacy.cbS26$legacyModel(), Unit.INSTANCE, poseStack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1,
            legacy.cbS26$legacySprite(), legacy.cbS26$legacySprites(), 0, state.breakProgress);
        poseStack.popPose();
    }
}
