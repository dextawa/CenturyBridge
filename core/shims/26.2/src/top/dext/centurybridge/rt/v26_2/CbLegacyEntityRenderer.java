package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * A 1.20.1 entity renderer's own render(entity, yaw, tickDelta, matrices, buffers, light),
 * as the namespace wall names it (the token became "submit"). The converter adds this to
 * a mod renderer that overrides render, so the bridge can call it (CbEntityRender.submit).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyEntityRenderer {

    void submit(Entity entity, float yaw, float tickDelta, PoseStack matrices, class_4597 buffers, int light);
}
