package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;
import top.dext.centurybridge.step.v26_2.rt.CbS26BlockOutlineHook;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * LevelRenderer.renderBlockOutline(BufferSource, PoseStack, boolean, LevelRenderState), which 26.2 replaced by
 * submitBlockOutline(PoseStack, SubmitNodeCollector, LevelRenderState), restored as the hook member 26.1.2 mixins on it
 * (the 26.1 layer's restored WorldRenderEvents BEFORE_BLOCK_OUTLINE / BLOCK_OUTLINE, a 26.1.2 mod's HEAD / camera-read
 * injector) reach (javap of both):
 * <ul>
 *   <li>26.1.2's control flow up to its drawing is kept as it was: nothing without an outline, nothing when the
 *       outline's translucency is not the call's, then the read of the camera position (the anchor 26.1.2 mixins use
 *       to stop vanilla's outline);</li>
 *   <li>its drawing is 26.2's own: the same shapes, colours and line width (secondary outline -16777216 / 7.0 when high
 *       contrast, then lines in -11010079 or ARGB.black(102) at the window's line width), which 26.2 submits in
 *       submitBlockOutline once this member answers that nothing stopped it.</li>
 * </ul>
 * mixin/CbS26BlockOutlineRouteMixin calls it from submitBlockOutline, only when a converted mod mixed it or the restored
 * renderHitOutline (CbMixedMembers), and only after 26.2's own and Fabric API's injectors at that method's head and
 * camera read ran.
 * <p>LevelRenderer.renderHitOutline(poseStack, consumer, camX, camY, camZ, outline, color, lineWidth), which 26.2 replaced
 * by submitHitOutline(poseStack, collector, renderType, outline, color, lineWidth, translucent), is restored too and
 * called as 26.1.2 called it (javap): with high contrast first into the secondary-outline buffer (-16777216, 7.0), then
 * into the lines buffer (-11010079 with high contrast, else ARGB.black(102), at the window's line width), each buffer
 * taken from the source after the camera position was read. 26.2 submits its outline in the same calls and order
 * (submitBlockOutline @99 secondary when high contrast, @151 lines). The restored body records only that the call reached
 * 26.1.2's drawing; mixin/CbS26HitOutlineRouteMixin cancels 26.2's matching submit of a call a mixin stopped (a 26.1.2
 * HEAD injector that draws its own outline into the consumer and cancels: resourcefullib, magna, autoslabs, otyacraft,
 * valkyrien) or that was never made (a @WrapWithCondition on the call: porting_lib, railways). What a mod draws into the
 * consumer goes into the frame's main buffer source (rt/CbS26RenderBuffers), whose batches mixin/CbS26LevelFlushMixin
 * ends in the main pass after the solid and the translucent features, where 26.1.2's main pass drew the outline.
 * 26.1.2's endLastBatch at the end of renderBlockOutline is not made here: this route runs while 26.2 collects submits,
 * outside any render pass, and the flush ends the batch inside the pass. An injector inside renderHitOutline's drawing
 * (its ShapeRenderer.renderShape calls) does not follow: 26.2 draws through the submit.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = LevelRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26BlockOutlineRestoreMixin implements CbS26BlockOutlineHook {

    @Shadow
    @Final
    private GameRenderer gameRenderer;

    /** whether the restored body reached 26.1.2's drawing this call */
    @Unique
    private boolean cbS26$outlineReached;

    /** the restored renderHitOutline calls of this route, in order: how many were made, which reached the drawing (bits) */
    @Unique
    private int cbS26$hitCalls;
    @Unique
    private int cbS26$hitDrawn;
    /** which of them 26.2's next submitHitOutline stands for; -1 when no route ran for this outline */
    @Unique
    private int cbS26$hitSubmitted = -1;

    /** 26.1.2 LevelRenderer.renderBlockOutline(consumers, poseStack, translucent, state), up to its drawing */
    @CbVanillaReads(value = "CbS26BlockOutlineRouteMixin: submitBlockOutline, after 26.2's and Fabric API's head and camera-read injectors, calls it with the frame's main buffer source, the submit's pose stack, the outline's translucency and the frame's state when a converted mod mixed it; its answer (cancelled or not) stops or keeps 26.2's submit, and its renderHitOutline calls (CbS26HitOutlineRouteMixin) stop or keep each of 26.2's submitHitOutline calls",
        carries = {"Inject@HEAD", "Inject@FIELD", "Inject@INVOKE", "WrapWithCondition@INVOKE"})
    private void renderBlockOutline(MultiBufferSource.BufferSource consumers, PoseStack poseStack, boolean translucent, LevelRenderState state) {
        BlockOutlineRenderState outline = state.blockOutlineRenderState;
        if (outline == null) {
            return;
        }
        if (outline.isTranslucent() != translucent) {
            return;
        }
        // 26.1.2's camera read before drawing: the anchor of 26.1.2 mixins that stop vanilla's outline
        Vec3 cameraPos = state.cameraRenderState.pos;
        this.cbS26$outlineReached = true;
        if (outline.highContrast()) {
            VertexConsumer secondary = consumers.getBuffer(RenderTypes.secondaryBlockOutline());
            this.cbS26$hitCalls++;
            this.renderHitOutline(poseStack, secondary, cameraPos.x, cameraPos.y, cameraPos.z, outline, -16777216, 7.0F);
        }
        VertexConsumer lines = consumers.getBuffer(RenderTypes.lines());
        int color = outline.highContrast() ? -11010079 : ARGB.black(102);
        this.cbS26$hitCalls++;
        this.renderHitOutline(poseStack, lines, cameraPos.x, cameraPos.y, cameraPos.z, outline, color,
            this.gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth);
    }

    /** 26.1.2 LevelRenderer.renderHitOutline(poseStack, consumer, camX, camY, camZ, outline, color, lineWidth), up to its drawing */
    @CbVanillaReads(value = "CbS26HitOutlineRouteMixin: the restored renderBlockOutline calls it as 26.1.2 did; 26.2's submitHitOutline of the same call, after its head injectors, is cancelled when a mixin stopped this one before its drawing",
        carries = {"Inject@HEAD"})
    private void renderHitOutline(PoseStack poseStack, VertexConsumer consumer, double camX, double camY, double camZ,
                                  BlockOutlineRenderState outline, int color, float lineWidth) {
        this.cbS26$hitDrawn |= 1 << (this.cbS26$hitCalls - 1);
    }

    @Override
    public boolean cbS26$restoredBlockOutline(MultiBufferSource.BufferSource consumers, PoseStack poseStack, boolean translucent, LevelRenderState state) {
        this.cbS26$outlineReached = false;
        this.cbS26$hitCalls = 0;
        this.cbS26$hitDrawn = 0;
        this.renderBlockOutline(consumers, poseStack, translucent, state);
        this.cbS26$hitSubmitted = 0;
        return this.cbS26$outlineReached;
    }

    @Override
    public void cbS26$noOutlineRoute() {
        this.cbS26$hitSubmitted = -1;
    }

    @Override
    public boolean cbS26$nextHitDrawn() {
        if (this.cbS26$hitSubmitted < 0) {
            return true;
        }
        int call = this.cbS26$hitSubmitted++;
        return (this.cbS26$hitDrawn & (1 << call)) != 0;
    }
}
