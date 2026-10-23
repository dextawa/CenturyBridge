package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's EntityModel.setupAnim(entity, ...) / prepareMobModel(entity, ...) called on a 1.21.2 model.
 *
 * 1.21.2 poses a model from the render state alone: setupAnim(S state). The generated mixins (gen_s7render.py family 5,
 * CbRenderModel<n>Mixin) put each 1.21.1 erasure back; they come here, which runs the model's 1.21.2 setupAnim on a
 * state of the entity -- the entity's renderer's own when it holds this extraction (the state being drawn, so a mod
 * model's super.setupAnim(entity, ...) poses from exactly what the game extracted), else a new extraction -- with the
 * numbers the 1.21.1 caller passed written into it for the call and restored after:
 *   limbSwing -> walkAnimationPos (1.21.1's renderer passed it x3 for a baby: divided back),
 *   limbSwingAmount -> walkAnimationSpeed, ageInTicks -> ageInTicks, netHeadYaw -> yRot, headPitch -> xRot,
 *   and the model's own pose fields (1.21.1 callers and prepareMobModel set them on the model first,
 *   CbRenderModelFields) -> the state fields 1.21.2 reads instead: attackTime / riding / young ->
 *   HumanoidRenderState.attackTime / isPassenger, LivingEntityRenderState.isBaby; HumanoidModel's swimAmount /
 *   crouching -> HumanoidRenderState.swimAmount / isCrouching (its arm poses are answered by getArmPose while the call
 *   runs, CbRenderHumanoidMixin); EndermanModel's carrying / creepy -> EndermanRenderState.carriedBlock / isCreepy;
 *   FelineModel's state -> FelineRenderState.isCrouching / isSprinting / isSitting.
 * The guard is held for the call, so a mod model's synthesized setupAnim(state) falls through to vanilla's instead of
 * re-entering the mod's override.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderModelCalls {

    /** EntityModel's 1.21.2 setupAnim, public (CbRenderModelFieldsMixin), and the prepareMobModel record */
    public interface Model {
        void cb$w$setupAnim(net.minecraft.class_10017 state);

        void cb$prepared(net.minecraft.class_1297 entity, float partialTick);

        /** the partial tick of the latest prepareMobModel of this entity on this model, or NaN */
        float cb$preparedPartialTick(net.minecraft.class_1297 entity);
    }

    /** the model a 1.21.1 call is posing right now (its put-back fields answer what 1.21.2 reads from the state) */
    private static Object legacy;

    private CbRenderModelCalls() {
    }

    /** whether a 1.21.1 call (setupAnim / setupAttackAnimation put back) is running 1.21.2 code on this model */
    public static boolean legacy(Object model) {
        return legacy == model;
    }

    public static void prepare(Object model, net.minecraft.class_1297 entity, float partialTick) {
        ((Model) model).cb$prepared(entity, partialTick);
        CbRenderModelFields.prepare(model, entity, partialTick);
    }

    public static void setupAnim(Object model, net.minecraft.class_1297 entity, float limbSwing, float limbSwingAmount,
                                 float ageInTicks, float netHeadYaw, float headPitch, Class<?> stateType) {
        Model m = (Model) model;
        pose(model, entity, stateType, s -> {
            float[] f = new float[5];
            boolean[] z = new boolean[1];
            f[0] = s.field_53328;
            s.field_53328 = ageInTicks;
            if (s instanceof net.minecraft.class_10042 l) {
                boolean baby = entity instanceof net.minecraft.class_1309 living && living.method_6109();
                f[1] = l.field_53450;
                f[2] = l.field_53451;
                f[3] = l.field_53447;
                f[4] = l.field_53448;
                l.field_53450 = baby ? limbSwing / 3.0F : limbSwing;
                l.field_53451 = limbSwingAmount;
                l.field_53447 = netHeadYaw;
                l.field_53448 = headPitch;
            }
            try {
                m.cb$w$setupAnim(s);
            } finally {
                s.field_53328 = f[0];
                if (s instanceof net.minecraft.class_10042 l) {
                    l.field_53450 = f[1];
                    l.field_53451 = f[2];
                    l.field_53447 = f[3];
                    l.field_53448 = f[4];
                }
            }
        });
    }

    /**
     * Runs 1.21.2 code of a model (run) on a state of the entity carrying the model's 1.21.1 pose fields, guarded and
     * marked as a 1.21.1 call; the state is restored afterwards.
     */
    public static void pose(Object model, net.minecraft.class_1297 entity, Class<?> stateType, Consumer<net.minecraft.class_10017> run) {
        Model m = (Model) model;
        float pt = m.cb$preparedPartialTick(entity);
        if (Float.isNaN(pt)) {
            pt = CbRenderTex.partialTick();
        }
        net.minecraft.class_10017 s = CbRenderLiving.stateOf(CbRenderLiving.rendererOf(entity), entity, pt);
        if (!stateType.isInstance(s)) {
            // the entity's renderer draws another kind of state than this model reads (a model used outside the
            // renderer made for it): a state of the model's own kind, carrying what the 1.21.1 call passed
            s = newState(stateType);
        }
        Object saved = CbRenderModelFields.carry(model, entity, s);
        Object outer = legacy;
        legacy = model;
        boolean in = Guard.cbEnter(model);
        try {
            run.accept(s);
        } finally {
            if (in) {
                Guard.cbExit(model);
            }
            legacy = outer;
            CbRenderModelFields.restore(s, saved);
        }
    }

    private static net.minecraft.class_10017 newState(Class<?> stateType) {
        try {
            return (net.minecraft.class_10017) stateType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("setupAnim: no state of " + stateType.getName() + " to pose the model with", e);
        }
    }
}
