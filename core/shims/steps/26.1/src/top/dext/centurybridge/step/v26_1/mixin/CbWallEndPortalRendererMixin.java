package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.11's AbstractEndPortalRenderer.submit and the hooks it read: getOffsetUp() (0.75: the top face's height),
 * getOffsetDown() (0.375: the bottom face's) and renderType() (the end portal's). 1.21.11's renderer drew a cube
 * whose four sides span the whole block and whose top and bottom faces sit at those heights, showing the faces the
 * block entity wants shown, with renderType(); a subclass changed the slab or the render type by overriding them.
 * 26.1 moved submit into each vanilla renderer (TheEndPortalRenderer scales a unit cube with a transformation,
 * TheEndGatewayRenderer draws the unit cube; AbstractEndPortalRenderer.submitCube does the faces) and left the
 * abstract class without one -- a 1.21.11 subclass relying on the inherited submit had none. This gives 1.21.11's
 * submit back on the abstract class, reading the hooks, so a 1.21.11 subclass draws as it did; the vanilla
 * renderers keep their own submit.
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractEndPortalRenderer.class)
public abstract class CbWallEndPortalRendererMixin {

    protected float getOffsetUp() {
        return 0.75F;
    }

    protected float getOffsetDown() {
        return 0.375F;
    }

    protected RenderType renderType() {
        return RenderTypes.endPortal();
    }

    public void submit(EndPortalRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        Set<Direction> faces = state.facesToShow;
        float down = getOffsetDown();
        float up = getOffsetUp();
        collector.submitCustomGeometry(poseStack, renderType(), (pose, consumer) -> cb$cube(faces, pose.pose(), consumer, down, up));
    }

    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submit((EndPortalRenderState) state, poseStack, collector, camera);
    }

    private static void cb$cube(Set<Direction> faces, Matrix4f pose, VertexConsumer consumer, float down, float up) {
        cb$face(faces, pose, consumer, 0, 1, 0, 1, 1, 1, 1, 1, Direction.SOUTH);
        cb$face(faces, pose, consumer, 0, 1, 1, 0, 0, 0, 0, 0, Direction.NORTH);
        cb$face(faces, pose, consumer, 1, 1, 1, 0, 0, 1, 1, 0, Direction.EAST);
        cb$face(faces, pose, consumer, 0, 0, 0, 1, 0, 1, 1, 0, Direction.WEST);
        cb$face(faces, pose, consumer, 0, 1, down, down, 0, 0, 1, 1, Direction.DOWN);
        cb$face(faces, pose, consumer, 0, 1, up, up, 1, 1, 0, 0, Direction.UP);
    }

    private static void cb$face(Set<Direction> faces, Matrix4f pose, VertexConsumer consumer, float x0, float x1, float y0,
                                float y1, float z0, float z1, float z2, float z3, Direction direction) {
        if (faces.contains(direction)) {
            consumer.addVertex(pose, x0, y0, z0);
            consumer.addVertex(pose, x1, y0, z1);
            consumer.addVertex(pose, x1, y1, z2);
            consumer.addVertex(pose, x0, y1, z3);
        }
    }
}
