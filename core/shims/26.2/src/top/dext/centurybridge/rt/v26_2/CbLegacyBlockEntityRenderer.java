package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * A 1.20.1 block entity renderer's render(blockEntity, tickDelta, matrices, buffers, light,
 * overlay), as the namespace wall names it (the token became "submit"). The converter adds
 * this to a mod's renderer so CbBlockEntityRender can call it.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyBlockEntityRenderer {

    void submit(BlockEntity blockEntity, float tickDelta, PoseStack matrices, class_4597 buffers, int light, int overlay);
}
