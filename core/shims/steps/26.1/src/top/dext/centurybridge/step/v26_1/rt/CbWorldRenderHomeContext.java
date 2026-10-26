package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import top.dext.centurybridge.step.v26_1.facade.WorldRenderContext;

/**
 * The per-renderer frame context behind the restored 0.136 WorldRenderContext / BlockOutlineContext on 26.1 (Fabric
 * API 0.136's WorldRenderContextImpl): prepared when renderLevel starts, the pose stack set when the main pass makes
 * it, the outline values before each block outline.
 */
@Environment(EnvType.CLIENT)
public final class CbWorldRenderHomeContext implements WorldRenderContext, WorldRenderContext.BlockOutlineContext {
    private LevelRenderer worldRenderer;
    private DeltaTracker tickCounter;
    private boolean blockOutlines;
    private Camera camera;
    private GameRenderer gameRenderer;
    private Matrix4f positionMatrix;
    private Matrix4f projectionMatrix;
    private MultiBufferSource consumers;
    private boolean advancedTranslucency;
    private ClientLevel world;
    private Frustum frustum;
    private PoseStack matrixStack;
    private boolean translucentBlockOutline;
    private Entity entity;
    private double cameraX;
    private double cameraY;
    private double cameraZ;
    private BlockPos blockPos;
    private BlockState blockState;

    /** BEFORE_BLOCK_OUTLINE's answer for the outline being drawn */
    boolean renderBlockOutline = true;
    /** between renderLevel's start and its return */
    boolean rendering;

    void prepare(LevelRenderer worldRenderer, DeltaTracker tickCounter, boolean blockOutlines, Camera camera, GameRenderer gameRenderer,
            Matrix4f positionMatrix, Matrix4f projectionMatrix, MultiBufferSource consumers, boolean advancedTranslucency, ClientLevel world,
            Frustum frustum) {
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
        this.frustum = frustum;
        this.matrixStack = null;
    }

    void setMatrixStack(PoseStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    void setTranslucentBlockOutline(boolean translucentBlockOutline) {
        this.translucentBlockOutline = translucentBlockOutline;
    }

    void prepareBlockOutline(Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState) {
        this.entity = entity;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    @Override
    public LevelRenderer worldRenderer() {
        return worldRenderer;
    }

    @Override
    public DeltaTracker tickCounter() {
        return tickCounter;
    }

    @Override
    public boolean blockOutlines() {
        return blockOutlines;
    }

    @Override
    public Camera camera() {
        return camera;
    }

    @Override
    public GameRenderer gameRenderer() {
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
    public ClientLevel world() {
        return world;
    }

    @Override
    public boolean advancedTranslucency() {
        return advancedTranslucency;
    }

    @Override
    public MultiBufferSource consumers() {
        return consumers;
    }

    @Override
    public Frustum frustum() {
        return frustum;
    }

    @Override
    public PoseStack matrixStack() {
        return matrixStack;
    }

    @Override
    public boolean translucentBlockOutline() {
        return translucentBlockOutline;
    }

    @Override
    public Entity entity() {
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
    public BlockPos blockPos() {
        return blockPos;
    }

    @Override
    public BlockState blockState() {
        return blockState;
    }

    /** the lines buffer of the frame's consumers, as 0.136 */
    @Override
    public VertexConsumer vertexConsumer() {
        return consumers.getBuffer(RenderTypes.lines());
    }
}
