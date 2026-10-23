package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 entity models over 1.21.2's state-driven ones.
 *
 * 1.21.1's LivingEntityRenderer.render posed the model itself, from the entity (bytecode of class_922.method_4054):
 * <pre>
 *   model.attackTime = getAttackAnim(entity, partialTick); model.riding = entity.isPassenger(); model.young = entity.isBaby();
 *   ...
 *   walkSpeed = 0, walkPos = 0
 *   if (!entity.isPassenger() && entity.isAlive()) {
 *       walkSpeed = entity.walkAnimation.speed(partialTick); walkPos = entity.walkAnimation.position(partialTick);
 *       if (entity.isBaby()) walkPos *= 3; if (walkSpeed > 1) walkSpeed = 1;
 *   }
 *   model.prepareMobModel(entity, walkPos, walkSpeed, partialTick);
 *   model.setupAnim(entity, walkPos, walkSpeed, getBob(entity, partialTick), headYaw - bodyYaw, xRot);
 * </pre>
 * 1.21.2 calls model.setupAnim(state) instead (class_922.method_4054 at 111). A 1.21.1 model's setupAnim and
 * prepareMobModel take the entity; the hooks (overrideHooks, bridge mode) synthesize the 1.21.2 setupAnim(state) on
 * such a model and come here, which hands the old methods exactly the values 1.21.1 computed, from the entity the
 * state was extracted from (CbRenderStateEntity) and the state's own extraction of the same numbers where 1.21.2
 * keeps them (yRot/xRot/walkAnimationPos/walkAnimationSpeed are computed exactly as 1.21.1 did, class_922.method_62355;
 * 1.21.2 dropped only the baby x3 and the clamp of the speed, applied here).
 *
 * A state no extraction filled (a state a 1.21.2 renderer builds for a part, ParrotOnShoulderLayer's parrot) has no
 * entity to hand the old method: the 1.21.2 setupAnim of the class above runs instead (the guard's fall-through), as
 * if the model had not been converted.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderModels {

    /** calls a 1.21.1 model's prepareMobModel and setupAnim at its own erasure (CbRenderModelsGen) */
    @FunctionalInterface
    public interface Legacy {
        void animate(net.minecraft.class_1297 entity, Anim anim);
    }

    private CbRenderModels() {
    }

    /** the five setupAnim numbers and the partial tick 1.21.1's render computed for an entity */
    public record Anim(float walkPos, float walkSpeed, float bob, float headYaw, float headPitch, float partialTick) {
    }

    /**
     * What 1.21.1's LivingEntityRenderer.render passed setupAnim, for a living entity; for any other entity the
     * renderers that posed a model (BoatRenderer, MinecartRenderer, LlamaSpitRenderer...) passed their own values:
     * 0, 0, -0.1 / 0, 0 / ageInTicks / yaw / pitch -- see {@link #setupAnim}.
     */
    public static Anim living(net.minecraft.class_1309 e, float partialTick, Object state) {
        float walkSpeed = 0.0F;
        float walkPos = 0.0F;
        if (!e.method_5765() && e.method_5805()) {
            walkSpeed = e.field_42108.method_48570(partialTick);
            walkPos = e.field_42108.method_48572(partialTick);
            if (e.method_6109()) {
                walkPos *= 3.0F;
            }
            if (walkSpeed > 1.0F) {
                walkSpeed = 1.0F;
            }
        }
        float headYaw;
        float headPitch;
        if (state instanceof net.minecraft.class_10042 s) {
            // 1.21.2 extracts both exactly as 1.21.1 computed them (rotLerp of head/body, the vehicle clamp, the
            // upside-down flip, wrapDegrees): class_922.method_62355 0..97
            headYaw = s.field_53447;
            headPitch = s.field_53448;
        } else {
            headYaw = 0.0F;
            headPitch = 0.0F;
        }
        return new Anim(walkPos, walkSpeed, (float) e.field_6012 + partialTick, headYaw, headPitch, partialTick);
    }

    /**
     * The 1.21.2 setupAnim(state) synthesized on a 1.21.1 model (guarded): 1.21.2's reset to the layer pose, then
     * 1.21.1's prepareMobModel and setupAnim with the extracted entity and the values 1.21.1's renderer computed.
     * A state no extraction filled leaves the model at its layer pose, as 1.21.2's EntityModel.setupAnim does.
     */
    public static void setupAnim(Object self, net.minecraft.class_10017 state, Legacy legacy) {
        // 1.21.2's EntityModel.setupAnim resets every part to its layer-definition pose first (class_583.method_2819);
        // a 1.21.1 model set every part it animates each frame, so starting from that pose changes nothing it drew
        ((net.minecraft.class_3879) self).method_63514();
        net.minecraft.class_1297 e = CbRenderStateEntity.entityOf(state);
        if (e == null) {
            return;
        }
        float pt = CbRenderStateEntity.partialTickOf(state);
        if (e instanceof net.minecraft.class_1309 living) {
            // 1.21.1's LivingEntityRenderer.render set attackTime / riding / young on its model first (CbRenderModelFields)
            if (self instanceof net.minecraft.class_583<?> model) {
                CbRenderModelFields.legacy(model, living, pt);
            }
            // the bob is the renderer's getBob, as 1.21.1's LivingEntityRenderer.render asked it (CbRenderLiving.anim)
            legacy.animate(e, CbRenderLiving.anim(self, e, pt, state));
        } else {
            // a renderer of a non-living entity posed its model itself, with zeros (MinecartRenderer, LeashKnotRenderer:
            // setupAnim(entity, 0, 0, 0, 0, 0); BoatRenderer (entity, partialTick, 0, -0.1, 0, 0)); a mod's own renderer
            // passed what it chose, which its own render (hooked separately) still does. Here, the state's age.
            legacy.animate(e, new Anim(0.0F, 0.0F, state.field_53328, 0.0F, 0.0F, pt));
        }
    }
}
