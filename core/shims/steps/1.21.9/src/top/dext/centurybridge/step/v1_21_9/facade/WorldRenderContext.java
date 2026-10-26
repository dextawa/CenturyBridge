package top.dext.centurybridge.step.v1_21_9.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_757;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import org.joml.Matrix4f;
import top.dext.centurybridge.step.v1_21_9.rt.CbWorldRender;

/**
 * Fabric API 0.136's (1.21.8) WorldRenderContext and its BlockOutlineContext, deleted by Fabric API for 1.21.9: the
 * same accessors, answered by the layer's context (rt/CbWorldRenderContextImpl) with what 1.21.9's renderLevel was
 * handed or built at the matching point of the frame.
 */
@Environment(EnvType.CLIENT)
public interface WorldRenderContext {

    /** the context of the frame the renderer is drawing; IllegalStateException outside a frame, as 0.136 */
    static WorldRenderContext getInstance(class_761 worldRenderer) {
        return CbWorldRender.cbContext(worldRenderer);
    }

    class_761 worldRenderer();

    class_9779 tickCounter();

    boolean blockOutlines();

    class_4184 camera();

    class_757 gameRenderer();

    Matrix4f positionMatrix();

    Matrix4f projectionMatrix();

    class_638 world();

    boolean advancedTranslucency();

    class_4597 consumers();

    class_4604 frustum();

    class_4587 matrixStack();

    boolean translucentBlockOutline();

    @Environment(EnvType.CLIENT)
    interface BlockOutlineContext {
        class_1297 entity();

        double cameraX();

        double cameraY();

        double cameraZ();

        class_2338 blockPos();

        class_2680 blockState();

        class_4588 vertexConsumer();
    }
}
