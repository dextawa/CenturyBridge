package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1921;
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
import top.dext.centurybridge.step.v1_21_9.facade.WorldRenderContext;

/**
 * The per-renderer frame context behind the restored WorldRenderContext / BlockOutlineContext (Fabric API 0.136's
 * WorldRenderContextImpl): prepared when renderLevel starts, the frustum set when terrain setup took it, the pose stack
 * when the main pass made it, the outline values before each block outline.
 */
@Environment(EnvType.CLIENT)
public final class CbWorldRenderContextImpl implements WorldRenderContext, WorldRenderContext.BlockOutlineContext {
    private class_761 worldRenderer;
    private class_9779 tickCounter;
    private boolean blockOutlines;
    private class_4184 camera;
    private class_757 gameRenderer;
    private Matrix4f positionMatrix;
    private Matrix4f projectionMatrix;
    private class_4597 consumers;
    private boolean advancedTranslucency;
    private class_638 world;
    private class_4604 frustum;
    private class_4587 matrixStack;
    private boolean translucentBlockOutline;
    private class_1297 entity;
    private double cameraX;
    private double cameraY;
    private double cameraZ;
    private class_2338 blockPos;
    private class_2680 blockState;

    /** BEFORE_BLOCK_OUTLINE's answer for the outline being drawn */
    boolean renderBlockOutline = true;
    /** between renderLevel's start and its return */
    boolean rendering;

    void prepare(class_761 worldRenderer, class_9779 tickCounter, boolean blockOutlines, class_4184 camera, class_757 gameRenderer,
            Matrix4f positionMatrix, Matrix4f projectionMatrix, class_4597 consumers, boolean advancedTranslucency, class_638 world) {
        this.worldRenderer = worldRenderer;
        this.tickCounter = tickCounter;
        this.blockOutlines = blockOutlines;
        this.camera = camera;
        this.gameRenderer = gameRenderer;
        this.positionMatrix = positionMatrix;
        this.projectionMatrix = projectionMatrix;
        this.consumers = consumers;
        this.advancedTranslucency = advancedTranslucency;
        this.world = world;
        this.frustum = null;
        this.matrixStack = null;
    }

    void setFrustum(class_4604 frustum) {
        this.frustum = frustum;
    }

    void setMatrixStack(class_4587 matrixStack) {
        this.matrixStack = matrixStack;
    }

    void setTranslucentBlockOutline(boolean translucentBlockOutline) {
        this.translucentBlockOutline = translucentBlockOutline;
    }

    void prepareBlockOutline(class_1297 entity, double cameraX, double cameraY, double cameraZ, class_2338 blockPos, class_2680 blockState) {
        this.entity = entity;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    @Override
    public class_761 worldRenderer() {
        return worldRenderer;
    }

    @Override
    public class_9779 tickCounter() {
        return tickCounter;
    }

    @Override
    public boolean blockOutlines() {
        return blockOutlines;
    }

    @Override
    public class_4184 camera() {
        return camera;
    }

    @Override
    public class_757 gameRenderer() {
        return gameRenderer;
    }

    @Override
    public Matrix4f positionMatrix() {
        return positionMatrix;
    }

    @Override
    public Matrix4f projectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public class_638 world() {
        return world;
    }

    @Override
    public boolean advancedTranslucency() {
        return advancedTranslucency;
    }

    @Override
    public class_4597 consumers() {
        return consumers;
    }

    @Override
    public class_4604 frustum() {
        return frustum;
    }

    @Override
    public class_4587 matrixStack() {
        return matrixStack;
    }

    @Override
    public boolean translucentBlockOutline() {
        return translucentBlockOutline;
    }

    @Override
    public class_1297 entity() {
        return entity;
    }

    @Override
    public double cameraX() {
        return cameraX;
    }

    @Override
    public double cameraY() {
        return cameraY;
    }

    @Override
    public double cameraZ() {
        return cameraZ;
    }

    @Override
    public class_2338 blockPos() {
        return blockPos;
    }

    @Override
    public class_2680 blockState() {
        return blockState;
    }

    /** the lines buffer of the frame's consumers, as 0.136 (1.21.9's MultiBufferSource.getBuffer is method_73477) */
    @Override
    public class_4588 vertexConsumer() {
        return consumers.method_73477(class_1921.method_23594());
    }
}
