package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * A 1.20.1 renderer's render override at the LivingEntityRenderer level (render(LivingEntity,
 * ...); past the namespace wall the token is named submit). A renderer compiled against
 * LivingEntityRenderer overrides this erasure; the converter adds this interface so
 * CbEntityRender can call it.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyLivingRenderer {

    void submit(net.minecraft.world.entity.LivingEntity entity, float yaw, float tickDelta, PoseStack matrices,
                class_4597 buffers, int light);
}
