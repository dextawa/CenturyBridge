package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A 26.1 special renderer drawn in the pose 1.21.11's renderer of the same special model applied itself
 * (CbWallItemModels.legacyPose): 26.1's special renderers draw unposed and leave the pose to their item or block
 * model's transformation, 1.21.11's posed themselves inside submit. Used where a 1.21.11 caller holds the renderer
 * itself (1.21.11's SpecialBlockModelRenderer table, rt/CbWallSpecialBlocks).
 */
@Environment(EnvType.CLIENT)
public final class CbWallPosedSpecialRenderer<T> implements SpecialModelRenderer<T> {
    private final SpecialModelRenderer<T> renderer;
    private final Matrix4fc pose;

    public CbWallPosedSpecialRenderer(SpecialModelRenderer<T> renderer, Transformation pose) {
        this.renderer = renderer;
        this.pose = pose.getMatrix();
    }

    @Override
    public void submit(T argument, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outline) {
        poseStack.pushPose();
        poseStack.mulPose(this.pose);
        this.renderer.submit(argument, poseStack, collector, light, overlay, hasFoil, outline);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        this.renderer.getExtents(v -> consumer.accept(this.pose.transformPosition(v, new Vector3f())));
    }

    @Override
    public T extractArgument(ItemStack stack) {
        return this.renderer.extractArgument(stack);
    }
}
