package top.dext.centurybridge.step.v26_1_1.rt;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;

/**
 * 26.1's FabricOrderedSubmitNodeCollector.submitBlockModel shape (one RenderType), as a 26.1 class
 * implementing the collector declares it. The method 26.1.1 calls instead is synthesized on such a class
 * (overrideHooks, bridge mode) and reaches the class's own 26.1 method through this interface
 * ({@link CbBlockModelSubmits#submitBlockModel}).
 */
@Environment(EnvType.CLIENT)
public interface CbLegacyBlockModelSubmitter {
    void submitBlockModel(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.rendertype.RenderType renderType,
                          List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts, Mesh mesh, int[] tintLayers,
                          int lightCoords, int overlayCoords, int outlineColor);
}
