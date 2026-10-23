package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ItemFeatureRenderer's private item helpers (1.21.11's ItemRenderer.renderItem, facade/ItemRenderer) */
@Environment(EnvType.CLIENT)
@Mixin(ItemFeatureRenderer.class)
public interface CbWallItemFeatureRendererAccess {

    @Invoker("computeFoilDecalPose")
    static PoseStack.Pose cb$computeFoilDecalPose(ItemDisplayContext context, PoseStack.Pose pose) {
        throw new AssertionError();
    }

    @Invoker("getFoilBuffer")
    static VertexConsumer cb$getFoilBuffer(MultiBufferSource buffers, RenderType renderType, PoseStack.Pose decalPose) {
        throw new AssertionError();
    }

    @Invoker("getLayerColorSafe")
    static int cb$getLayerColorSafe(int[] tintLayers, BakedQuad.MaterialInfo material) {
        throw new AssertionError();
    }
}
