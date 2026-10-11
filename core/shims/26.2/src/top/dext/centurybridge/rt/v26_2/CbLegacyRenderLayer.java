package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * A 1.20.1 feature renderer's render(matrices, buffers, light, entity, limbAngle,
 * limbDistance, tickDelta, animationProgress, headYaw, headPitch), as the namespace wall
 * names it (the token became "submit"). The converter adds this to a mod's feature layer
 * so CbRenderLayers can call it.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyRenderLayer {

    void submit(PoseStack matrices, class_4597 buffers, int light, Entity entity, float limbAngle, float limbDistance,
                float tickDelta, float animationProgress, float headYaw, float headPitch);
}
