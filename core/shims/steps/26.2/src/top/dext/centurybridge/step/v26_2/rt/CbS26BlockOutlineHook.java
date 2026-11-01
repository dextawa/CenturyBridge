package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;

/**
 * LevelRenderer's restored 26.1.2 renderBlockOutline as the route reaches it (mixin/CbS26BlockOutlineRestoreMixin
 * implements it, mixin/CbS26BlockOutlineRouteMixin calls it).
 */
@Environment(EnvType.CLIENT)
public interface CbS26BlockOutlineHook {

    /** the key of the restored member in the conversion-time signal (CbMixedMembers) */
    String MEMBER = "net/minecraft/client/renderer/LevelRenderer.renderBlockOutline(Ltop/dext/centurybridge/step/v26_2/facade/MultiBufferSource$BufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;ZLnet/minecraft/client/renderer/state/level/LevelRenderState;)V";

    /**
     * Runs the restored renderBlockOutline(consumers, poseStack, translucent, state); true when it reached the point
     * where 26.1.2 drew the outline (no mixin on it cancelled before), false when a mixin cancelled it.
     */
    /** the key of the restored renderHitOutline in the conversion-time signal (CbMixedMembers) */
    String HIT_MEMBER = "net/minecraft/client/renderer/LevelRenderer.renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;DDDLnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;IF)V";

    /** no route ran for this outline: 26.2's submitHitOutline calls go through untouched */
    void cbS26$noOutlineRoute();

    /**
     * For 26.2's next submitHitOutline of the routed outline: whether the restored renderHitOutline call it stands for
     * (the same position in the same order) reached 26.1.2's drawing; true when no route ran.
     */
    boolean cbS26$nextHitDrawn();

    boolean cbS26$restoredBlockOutline(MultiBufferSource.BufferSource consumers, PoseStack poseStack, boolean translucent, LevelRenderState state);
}
