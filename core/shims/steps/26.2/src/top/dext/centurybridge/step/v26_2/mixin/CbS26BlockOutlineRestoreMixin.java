package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
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
 *       submitBlockOutline once this member answers that nothing stopped it. This body records only that it got
 *       there.</li>
 * </ul>
 * mixin/CbS26BlockOutlineRouteMixin calls it from submitBlockOutline, only when a converted mod mixed it (CbMixedMembers)
 * and only after 26.2's own and Fabric API's injectors at that method's head and camera read ran. What a 26.1.2 mixin
 * changes about the drawing itself (an injector into renderHitOutline's calls) does not follow: 26.2 draws through the
 * submit.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = LevelRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26BlockOutlineRestoreMixin implements CbS26BlockOutlineHook {

    /** whether the restored body reached 26.1.2's drawing this call */
    @Unique
    private boolean cbS26$outlineReached;

    /** 26.1.2 LevelRenderer.renderBlockOutline(consumers, poseStack, translucent, state), up to its drawing */
    @CbVanillaReads(value = "CbS26BlockOutlineRouteMixin: submitBlockOutline, after 26.2's and Fabric API's head and camera-read injectors, calls it with the frame's main buffer source, the submit's pose stack, the outline's translucency and the frame's state when a converted mod mixed it; its answer (cancelled or not) stops or keeps 26.2's submit",
        carries = {"Inject@HEAD", "Inject@FIELD", "Inject@INVOKE"})
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
    }

    @Override
    public boolean cbS26$restoredBlockOutline(MultiBufferSource.BufferSource consumers, PoseStack poseStack, boolean translucent, LevelRenderState state) {
        this.cbS26$outlineReached = false;
        this.renderBlockOutline(consumers, poseStack, translucent, state);
        return this.cbS26$outlineReached;
    }
}
