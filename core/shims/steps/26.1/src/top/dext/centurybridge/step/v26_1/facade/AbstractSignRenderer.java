package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * 1.21.11's AbstractSignRenderer on 26.1's. 1.21.11's sign renderer posed the sign as it rendered it, asking
 * its subclass: translateSign(pose, -yRotation, state), then the model scaled by getSignModelRenderScale()
 * (submitSign), and each side's text turned (the back by 180 degrees), moved by getTextOffset() and scaled
 * by 1/64 of getSignTextRenderScale(); the model was getSignModel(state, woodType), its texture
 * getSignMaterial(woodType). 26.1's renders from transformations its subclass puts in the render state
 * when it extracts it, and asks getSignModel(renderState) and getSignSprite(woodType).
 * <p>
 * So this class extracts the transformations 1.21.11's methods describe -- the body's translateSign, the
 * model scale staying in submitSign as in 1.21.11 -- and answers 26.1's questions with 1.21.11's methods.
 * 1.21.11's members keep their intermediary names (a mod's subclass overrides and calls them; 26.1's
 * submit, submitSign, createRenderState and extractRenderState go through them), public (access wideners
 * never reach a CenturyBridge class).
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractSignRenderer extends net.minecraft.client.renderer.blockentity.AbstractSignRenderer<SignRenderState> {

    public AbstractSignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    /** getSignModel(state, woodType) */
    public abstract Model.Simple method_65823(BlockState state, WoodType woodType);

    /** getSignMaterial(woodType) */
    public abstract SpriteId method_45792(WoodType woodType);

    /** getSignModelRenderScale() */
    public abstract float method_51272();

    /** getSignTextRenderScale() */
    public abstract float method_51273();

    /** getTextOffset() */
    public abstract Vec3 method_45790();

    /** translateSign(pose, yRotation, state) */
    public abstract void method_65824(PoseStack pose, float yRotation, BlockState state);

    /** 26.1's sign render state with the block state 1.21.11's block entity render states carried (getSignModel asks it) */
    public static class State extends SignRenderState {
        public BlockState cb$blockState;
    }

    // ---- 1.21.11's members 26.1 also has (under its names, which call these) ----

    /** submit(state, pose, collector, camera) */
    public void method_65820(SignRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, pose, collector, camera);
    }

    @Override
    public void submit(SignRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        this.method_65820(state, pose, collector, camera);
    }

    /** submitSign(pose, light, woodType, model, breakProgress, collector): 1.21.11's scaled the model by getSignModelRenderScale() itself */
    public void method_65825(PoseStack pose, int light, WoodType woodType, Model.Simple model, ModelFeatureRenderer.CrumblingOverlay breakProgress,
        SubmitNodeCollector collector) {
        pose.pushPose();
        float scale = this.method_51272();
        pose.scale(scale, -scale, -scale);
        super.submitSign(pose, light, woodType, model, breakProgress, collector);
        pose.popPose();
    }

    @Override
    protected void submitSign(PoseStack pose, int light, WoodType woodType, Model.Simple model, ModelFeatureRenderer.CrumblingOverlay breakProgress,
        SubmitNodeCollector collector) {
        this.method_65825(pose, light, woodType, model, breakProgress, collector);
    }

    /** getDarkColor(text) */
    public static int method_65822(SignText text) {
        return getDarkColor(text);
    }

    /** createRenderState(): 1.21.11's sign render state, carrying the block state */
    public SignRenderState method_74334() {
        return new State();
    }

    @Override
    public SignRenderState createRenderState() {
        return this.method_74334();
    }

    /** extractRenderState(sign, state, partialTicks, cameraPosition, breakProgress): 26.1's, then the transformations 1.21.11's methods describe */
    public void method_74333(SignBlockEntity sign, SignRenderState state, float partialTicks, Vec3 cameraPosition,
        ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(sign, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = sign.getBlockState();
        if (state instanceof State carrying) {
            carrying.cb$blockState = blockState;
        }
        PoseStack pose = new PoseStack();
        this.method_65824(pose, -((SignBlock) blockState.getBlock()).getYRotationDegrees(blockState), blockState);
        Matrix4f body = new Matrix4f(pose.last().pose());
        state.transformations = new SignRenderState.SignTransformations(new Transformation(body), this.text(body, true), this.text(body, false));
    }

    @Override
    public void extractRenderState(SignBlockEntity sign, SignRenderState state, float partialTicks, Vec3 cameraPosition,
        ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        this.method_74333(sign, state, partialTicks, cameraPosition, breakProgress);
    }

    /** 1.21.11's translateSignText(pose, front, getTextOffset()) after the body's pose */
    private Transformation text(Matrix4f body, boolean front) {
        PoseStack pose = new PoseStack();
        pose.last().pose().set(body);
        if (!front) {
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        }
        float scale = 0.015625F * this.method_51273();
        pose.translate(this.method_45790());
        pose.scale(scale, -scale, scale);
        return new Transformation(new Matrix4f(pose.last().pose()));
    }

    // ---- 26.1's questions, answered by 1.21.11's methods ----

    @Override
    protected Model.Simple getSignModel(SignRenderState state) {
        BlockState blockState = state instanceof State carrying ? carrying.cb$blockState : Minecraft.getInstance().level.getBlockState(state.blockPos);
        return this.method_65823(blockState, state.woodType);
    }

    @Override
    protected SpriteId getSignSprite(WoodType woodType) {
        return this.method_45792(woodType);
    }
}
