package top.dext.centurybridge.step.v1_21_2.rt;

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
 *   and the model's own attackTime / riding / young (1.21.1 callers set them on the model first, CbRenderModelFields)
 *   -> HumanoidRenderState.attackTime / isPassenger, LivingEntityRenderState.isBaby.
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

    private CbRenderModelCalls() {
    }

    public static void prepare(Object model, net.minecraft.class_1297 entity, float partialTick) {
        ((Model) model).cb$prepared(entity, partialTick);
    }

    public static void setupAnim(Object model, net.minecraft.class_1297 entity, float limbSwing, float limbSwingAmount,
                                 float ageInTicks, float netHeadYaw, float headPitch, Class<?> stateType) {
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
        float[] f = new float[6];
        boolean[] z = new boolean[2];
        f[0] = s.field_53328;
        s.field_53328 = ageInTicks;
        CbRenderModelFields.Pose pose = (CbRenderModelFields.Pose) model;
        if (s instanceof net.minecraft.class_10042 l) {
            boolean baby = entity instanceof net.minecraft.class_1309 living && living.method_6109();
            f[1] = l.field_53450;
            f[2] = l.field_53451;
            f[3] = l.field_53447;
            f[4] = l.field_53448;
            z[0] = l.field_53457;
            l.field_53450 = baby ? limbSwing / 3.0F : limbSwing;
            l.field_53451 = limbSwingAmount;
            l.field_53447 = netHeadYaw;
            l.field_53448 = headPitch;
            l.field_53457 = pose.cb$young();
        }
        if (s instanceof net.minecraft.class_10034 h) {
            f[5] = h.field_53404;
            z[1] = h.field_53413;
            h.field_53404 = pose.cb$attackTime();
            h.field_53413 = pose.cb$riding();
        }
        boolean in = Guard.cbEnter(model);
        try {
            m.cb$w$setupAnim(s);
        } finally {
            if (in) {
                Guard.cbExit(model);
            }
            s.field_53328 = f[0];
            if (s instanceof net.minecraft.class_10042 l) {
                l.field_53450 = f[1];
                l.field_53451 = f[2];
                l.field_53447 = f[3];
                l.field_53448 = f[4];
                l.field_53457 = z[0];
            }
            if (s instanceof net.minecraft.class_10034 h) {
                h.field_53404 = f[5];
                h.field_53413 = z[1];
            }
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
