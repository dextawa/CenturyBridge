package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

/**
 * 26.1.2's net.minecraft.client.renderer.ShapeRenderer, which 26.2 deleted: its one drawing body moved into
 * ShapeOutlineFeatureRenderer.buildGroup (the same per-edge vertices, colour, normal and line width, with the offset
 * folded into the submitted pose), which runs over submits and has no entry taking a consumer. Restored as it was
 * (javap of 26.1.2's class: renderShape walks the shape's edges, each edge two vertices at edge + offset in the pose
 * with the colour, the edge's normalised direction as normal and the line width). classRenames send a 26.1.2 mod's
 * references here.
 */
@Environment(EnvType.CLIENT)
public class ShapeRenderer {
    public static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z,
                                   int color, float lineWidth) {
        PoseStack.Pose pose = poseStack.last();
        shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            Vector3f normal = new Vector3f((float) (x1 - x0), (float) (y1 - y0), (float) (z1 - z0)).normalize();
            consumer.addVertex(pose, (float) (x0 + x), (float) (y0 + y), (float) (z0 + z))
                .setColor(color).setNormal(pose, normal).setLineWidth(lineWidth);
            consumer.addVertex(pose, (float) (x1 + x), (float) (y1 + y), (float) (z1 + z))
                .setColor(color).setNormal(pose, normal).setLineWidth(lineWidth);
        });
    }
}
