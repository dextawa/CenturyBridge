package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * A 1.20.1 renderer's render override at the MobRenderer level (render(net.minecraft.world.entity.Mob, ...); the token stays
 * past the wall). A renderer compiled against MobRenderer overrides this erasure; the converter
 * adds this interface so CbEntityRender can call it.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyMobRenderer {

    void method_4072(net.minecraft.world.entity.Mob entity, float yaw, float tickDelta, PoseStack matrices, class_4597 buffers, int light);
}
