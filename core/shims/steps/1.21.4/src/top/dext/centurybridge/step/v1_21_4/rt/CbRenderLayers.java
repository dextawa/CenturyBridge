package top.dext.centurybridge.step.v1_21_4.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3's humanoid arm poses and held-item layers on 1.21.4's.
 * <ul>
 * <li>Arm poses: 1.21.3's HumanoidModel.setupAnim asked getArmPose(state, arm) (virtually: SkeletonModel, DrownedModel and
 *     PlayerModel answered from the state); 1.21.4's renderers compute the same poses into the state and the model reads them.
 *     A 1.21.3 call of getArmPose answers the pose 1.21.4 computed (what 1.21.3's vanilla body answered); a 1.21.3 model
 *     overriding it is asked again where 1.21.3 asked it, and its answer is the pose the model uses (CbLayerHumanoidModelMixin).</li>
 * <li>Held-item layers: their state types narrowed (ArmedEntityRenderState, HoldingEntityRenderState, SnowGolemRenderState); a
 *     1.21.3 call hands the state the 1.21.4 renderer made, which is one. ItemInHandLayer.renderArmWithItem drew a model and a
 *     stack; 1.21.4's draws the hand's item render state -- a 1.21.3 call draws as 1.21.3's body did (CbItemRender), a 1.21.3
 *     override is handed the hand's model and stack the state carries (CbStateLivingMixin), as 1.21.3's render handed them.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderLayers {
    private CbRenderLayers() {
    }

    // ---- arm poses ----

    /** the pose 1.21.4's renderer computed for the arm (1.21.3's vanilla getArmPose answer) */
    public static net.minecraft.class_572.class_573 pose(net.minecraft.class_10034 state, net.minecraft.class_1306 arm) {
        return arm == net.minecraft.class_1306.field_6183 ? state.field_55304 : state.field_55306;
    }

    /** was HumanoidModel.getArmPose(state, arm) (method_62081), called by a mod */
    public static net.minecraft.class_572.class_573 method_62081(net.minecraft.class_572<?> model, net.minecraft.class_10034 state, net.minecraft.class_1306 arm) {
        return pose(state, arm);
    }

    public static net.minecraft.class_572.class_573 method_62117(net.minecraft.class_606<?> model, net.minecraft.class_10066 state, net.minecraft.class_1306 arm) {
        return pose(state, arm);
    }

    public static net.minecraft.class_572.class_573 method_62082(net.minecraft.class_564 model, net.minecraft.class_10086 state, net.minecraft.class_1306 arm) {
        return pose(state, arm);
    }

    public static net.minecraft.class_572.class_573 method_62111(net.minecraft.class_591 model, net.minecraft.class_10055 state, net.minecraft.class_1306 arm) {
        return pose(state, arm);
    }

    /** was PlayerRenderer.getArmPose(state, arm) (method_64258, public then) */
    public static net.minecraft.class_572.class_573 method_64258(net.minecraft.class_10055 state, net.minecraft.class_1306 arm) {
        return pose(state, arm);
    }

    /** a 1.21.3 humanoid model overriding getArmPose (one interface per typed shape, renamed onto these: overrideRenames) */
    public interface CbLegacyArmPose {
        net.minecraft.class_572.class_573 cb$getArmPose(net.minecraft.class_10034 state, net.minecraft.class_1306 arm);
    }

    public interface CbLegacySkeletonArmPose {
        net.minecraft.class_572.class_573 cb$getSkeletonArmPose(net.minecraft.class_10066 state, net.minecraft.class_1306 arm);
    }

    public interface CbLegacyDrownedArmPose {
        net.minecraft.class_572.class_573 cb$getDrownedArmPose(net.minecraft.class_10086 state, net.minecraft.class_1306 arm);
    }

    public interface CbLegacyPlayerArmPose {
        net.minecraft.class_572.class_573 cb$getPlayerArmPose(net.minecraft.class_10055 state, net.minecraft.class_1306 arm);
    }

    /** 1.21.3's setupAnim asked the model for both arms' poses; a 1.21.3 model's answers are the poses it uses */
    public static void legacyArmPoses(Object model, net.minecraft.class_10034 state) {
        net.minecraft.class_572.class_573 left;
        net.minecraft.class_572.class_573 right;
        if (model instanceof CbLegacyPlayerArmPose p && state instanceof net.minecraft.class_10055 s) {
            left = p.cb$getPlayerArmPose(s, net.minecraft.class_1306.field_6182);
            right = p.cb$getPlayerArmPose(s, net.minecraft.class_1306.field_6183);
        } else if (model instanceof CbLegacySkeletonArmPose p && state instanceof net.minecraft.class_10066 s) {
            left = p.cb$getSkeletonArmPose(s, net.minecraft.class_1306.field_6182);
            right = p.cb$getSkeletonArmPose(s, net.minecraft.class_1306.field_6183);
        } else if (model instanceof CbLegacyDrownedArmPose p && state instanceof net.minecraft.class_10086 s) {
            left = p.cb$getDrownedArmPose(s, net.minecraft.class_1306.field_6182);
            right = p.cb$getDrownedArmPose(s, net.minecraft.class_1306.field_6183);
        } else if (model instanceof CbLegacyArmPose p) {
            left = p.cb$getArmPose(state, net.minecraft.class_1306.field_6182);
            right = p.cb$getArmPose(state, net.minecraft.class_1306.field_6183);
        } else {
            return;
        }
        state.field_55306 = left;
        state.field_55304 = right;
    }

    // ---- layer state types ----

    public static net.minecraft.class_10426 cbArmed(net.minecraft.class_10042 state) {
        return (net.minecraft.class_10426) state;
    }

    public static net.minecraft.class_10427 cbHolding(net.minecraft.class_10042 state) {
        return (net.minecraft.class_10427) state;
    }

    public static net.minecraft.class_10429 cbSnowGolem(net.minecraft.class_10042 state) {
        return (net.minecraft.class_10429) state;
    }

    /** a 1.21.3 override of a state-typed method handed 1.21.4's narrower state: the same object */
    public static net.minecraft.class_10042 cbLiving(net.minecraft.class_10429 state) {
        return state;
    }

    /** a 1.21.3 SnowGolemRenderer subclass's createRenderState: 1.21.4's renderer needs a SnowGolemRenderState */
    public static net.minecraft.class_10429 cbSnowGolemState(net.minecraft.class_10042 state) {
        return state instanceof net.minecraft.class_10429 s ? s : new net.minecraft.class_10429();
    }

    /** was WitchItemLayer.render(pose, buffers, light, state, yRot, xRot) (method_62599): CrossedArmsItemLayer's, which 1.21.4's witch layer adjusts */
    public static void method_62599(net.minecraft.class_1005 layer, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light,
        net.minecraft.class_10082 state, float yRot, float xRot) {
        layer.method_4208(pose, buffers, light, state, yRot, xRot);
    }

    // ---- ItemInHandLayer.renderArmWithItem ----

    /** a 1.21.3 ItemInHandLayer overriding renderArmWithItem(state, model, stack, context, arm, ..) */
    public interface CbLegacyArmWithItem {
        void method_4192(net.minecraft.class_10042 state, net.minecraft.class_1087 model, net.minecraft.class_1799 stack, net.minecraft.class_811 context,
            net.minecraft.class_1306 arm, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light);
    }

    public interface CbLegacyPlayerArmWithItem {
        void method_62594(net.minecraft.class_10055 state, net.minecraft.class_1087 model, net.minecraft.class_1799 stack, net.minecraft.class_811 context,
            net.minecraft.class_1306 arm, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light);
    }

    private static boolean right(net.minecraft.class_1306 arm) {
        return arm == net.minecraft.class_1306.field_6183;
    }

    /** 1.21.4's renderArmWithItem on a 1.21.3 override: the hand's model and stack, as 1.21.3's render handed them */
    public static void armWithItem(CbLegacyArmWithItem layer, net.minecraft.class_10426 state, net.minecraft.class_10444 item, net.minecraft.class_1306 arm,
        net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light) {
        CbStateItems items = new CbStateItems(state, arm);
        layer.method_4192(state, items.model, items.stack, items.context, arm, pose, buffers, light);
    }

    public static void playerArmWithItem(CbLegacyPlayerArmWithItem layer, net.minecraft.class_10055 state, net.minecraft.class_10444 item,
        net.minecraft.class_1306 arm, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light) {
        CbStateItems items = new CbStateItems(state, arm);
        layer.method_62594(state, items.model, items.stack, items.context, arm, pose, buffers, light);
    }

    /** the legacy hand fields of a state */
    private static final class CbStateItems {
        final net.minecraft.class_1087 model;
        final net.minecraft.class_1799 stack;
        final net.minecraft.class_811 context;

        CbStateItems(net.minecraft.class_10042 state, net.minecraft.class_1306 arm) {
            CbRenderStates.CbLegacyHands hands = (CbRenderStates.CbLegacyHands) state;
            boolean r = right(arm);
            this.model = r ? hands.cb$rightHandItemModel() : hands.cb$leftHandItemModel();
            this.stack = r ? hands.cb$rightHandItem() : hands.cb$leftHandItem();
            this.context = r ? net.minecraft.class_811.field_4320 : net.minecraft.class_811.field_4323;
        }
    }

    /** was ItemInHandLayer.renderArmWithItem(state, model, stack, context, arm, pose, buffers, light) (method_4192), and its super call: 1.21.3's body */
    public static void method_4192(net.minecraft.class_989<?, ?> layer, net.minecraft.class_10042 state, net.minecraft.class_1087 model,
        net.minecraft.class_1799 stack, net.minecraft.class_811 context, net.minecraft.class_1306 arm, net.minecraft.class_4587 pose,
        net.minecraft.class_4597 buffers, int light) {
        if (model == null || stack.method_7960()) {
            return;
        }
        pose.method_22903();
        ((net.minecraft.class_3881) layer.method_17165()).method_2803(arm, pose);
        pose.method_22907(net.minecraft.class_7833.field_40714.rotationDegrees(-90.0f));
        pose.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(180.0f));
        boolean left = arm == net.minecraft.class_1306.field_6182;
        pose.method_46416((left ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        CbItemRender.render(net.minecraft.class_310.method_1551().method_1480(), stack, context, left, pose, buffers, light,
            net.minecraft.class_4608.field_21444, model);
        pose.method_22909();
    }

    /** PlayerItemInHandLayer's protected 1.21.4 renderArmWithItem (CbLayerPlayerItemInHandAccess) */
    public interface CbPlayerArmWithItem {
        void cb$renderArmWithItem(net.minecraft.class_10055 state, net.minecraft.class_10444 item, net.minecraft.class_1306 arm, net.minecraft.class_4587 pose,
            net.minecraft.class_4597 buffers, int light);
    }

    /**
     * was PlayerItemInHandLayer.renderArmWithItem(state, model, stack, ..) (method_62594), and its super call: 1.21.4's body (the
     * same spyglass special case, then the arm's item) with the hand's item render state when the stack is the hand's -- the
     * state 1.21.4 resolved for it is the model 1.21.3 passed -- else the stack resolved for the context
     */
    public static void method_62594(net.minecraft.class_5697<?, ?> layer, net.minecraft.class_10055 state, net.minecraft.class_1087 model,
        net.minecraft.class_1799 stack, net.minecraft.class_811 context, net.minecraft.class_1306 arm, net.minecraft.class_4587 pose,
        net.minecraft.class_4597 buffers, int light) {
        if (model == null || stack.method_7960()) {
            return;
        }
        net.minecraft.class_10444 item;
        CbRenderStates.CbLegacyHands hands = (CbRenderStates.CbLegacyHands) state;
        if (stack == (right(arm) ? hands.cb$rightHandItem() : hands.cb$leftHandItem())) {
            item = right(arm) ? state.field_55305 : state.field_55307;
        } else {
            item = new net.minecraft.class_10444();
            CbItemRender.cbGameResolver().method_65597(item, stack, context, !right(arm), null);
        }
        ((CbPlayerArmWithItem) layer).cb$renderArmWithItem(state, item, arm, pose, buffers, light);
    }

    // ---- VillagerLikeModel.translateToArms ----

    /**
     * VillagerLikeModel.translateToArms(pose) of a 1.21.3 model: 1.21.3's crossed-arms layer placed the item itself (translate
     * (0, 0.4, -0.4), turn 180 degrees about X); 1.21.4's layer asks the model, then turns 0.75 rad, scales 1.07, translates
     * (0, 0.13, -0.34) and turns pi. This is 1.21.3's placement followed by the inverse of 1.21.4's, so the item lands where 1.21.3
     * put it.
     */
    public static void translateToArms(net.minecraft.class_3884 model, net.minecraft.class_4587 pose) {
        pose.method_46416(0.0f, 0.4f, -0.4f);
        pose.method_46416(0.0f, -0.13f, 0.34f);
        float s = 1.0f / 1.07f;
        pose.method_22905(s, s, s);
        pose.method_22907(net.minecraft.class_7833.field_40714.rotation(-0.75f));
    }

    /** a 1.21.3 VillagerLikeModel lambda (hatVisible(boolean)) */
    @FunctionalInterface
    public interface CbLegacyVillagerLikeModel {
        void method_17150(boolean visible);
    }

    public static net.minecraft.class_3884 adaptVillagerLikeModel(CbLegacyVillagerLikeModel lambda) {
        return new net.minecraft.class_3884() {
            @Override
            public void method_17150(boolean visible) {
                lambda.method_17150(visible);
            }

            @Override
            public void method_65191(net.minecraft.class_4587 pose) {
                translateToArms(this, pose);
            }
        };
    }

    /** was FelineModel.CAT_TRANSFORMER (field_52910): scaling 0.8 */
    public static net.minecraft.class_9953 field_52910() {
        return net.minecraft.class_9953.scaling(0.8f);
    }

    /** a 1.21.3 CubeMap subclass's preload: 1.21.4's registerTextures is where 1.21.3's title screen preloaded the panorama */
    public interface CbLegacyCubeMapPreload {
        java.util.concurrent.CompletableFuture<Void> method_18143(net.minecraft.class_1060 textureManager, java.util.concurrent.Executor executor);
    }

    public static void registerTextures(CbLegacyCubeMapPreload cubeMap, net.minecraft.class_1060 textureManager) {
        cubeMap.method_18143(textureManager, net.minecraft.class_156.method_18349());
    }
}
