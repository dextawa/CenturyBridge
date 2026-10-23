package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The public pose fields 1.21.1's models carried and 1.21.2 moved into the render state.
 *
 * 1.21.1 EntityModel: attackTime (field_3447), riding (field_3449), young (field_3448), set by LivingEntityRenderer.render
 * before setupAnim (class_922.method_4054 0..37: this.getAttackAnim(entity, partialTick) -- the renderer's, see
 * CbRenderLiving.attackAnim --, entity.isPassenger(), entity.isBaby())
 * and read by the model's setupAnim. 1.21.2 keeps none of them (the state has attackTime as HumanoidRenderState.attackTime,
 * isPassenger, isBaby). The mixins put the fields back (CbRenderModelFieldsMixin); a 1.21.1 model reads them in its own
 * setupAnim, so they are set, as 1.21.1 set them, right before it runs ({@link #legacy}).
 *
 * The same for the models' own public / protected pose fields:
 * <ul>
 * <li>HumanoidModel rightArmPose / leftArmPose (field_3395 / field_3399): set by 1.21.1's PlayerRenderer
 *   (setModelProperties) and by SkeletonModel / DrownedModel.prepareMobModel; swimAmount (field_3396): set by
 *   HumanoidModel.prepareMobModel (entity.getSwimAmount(partialTick)); crouching (field_3400): set by PlayerRenderer
 *   (player.isCrouching()). 1.21.2: HumanoidRenderState.swimAmount / isCrouching, and the arm poses through the
 *   model's getArmPose(state, arm) (CbRenderHumanoidMixin).</li>
 * <li>EndermanModel carrying / creepy (field_3371 / field_3370): set by EndermanRenderer.render
 *   (getCarriedBlock() != null, isCreepy()); 1.21.2: EndermanRenderState.carriedBlock / isCreepy.</li>
 * <li>FelineModel state (field_3434, initially 1): FelineModel.prepareMobModel sets 0 crouching / 2 sprinting / 1
 *   otherwise, CatModel.prepareMobModel 3 when the cat is in its sitting pose; 1.21.2: FelineRenderState.isCrouching /
 *   isSprinting / isSitting.</li>
 * </ul>
 * Before a 1.21.1 model is posed, the fields take what 1.21.1's renderer would have set ({@link #legacyState}: the
 * values 1.21.2's extraction computes the same way); its prepareMobModel's super calls set what 1.21.1's vanilla
 * prepareMobModel set ({@link #prepare}); and while 1.21.2 code runs for a 1.21.1 call the fields are what it reads
 * ({@link #carry} / {@link #restore}, CbRenderModelCalls.pose).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderModelFields {

    /** EntityModel's attackTime / riding / young, put back on the model */
    public interface Pose {
        void cb$pose(float attackTime, boolean riding, boolean young);

        float cb$attackTime();

        boolean cb$riding();

        boolean cb$young();
    }

    /** HumanoidModel's rightArmPose / leftArmPose / swimAmount / crouching (CbRenderHumanoidMixin) */
    public interface Humanoid {
        net.minecraft.class_572.class_573 cb$rightArmPose();

        net.minecraft.class_572.class_573 cb$leftArmPose();

        void cb$armPoses(net.minecraft.class_572.class_573 right, net.minecraft.class_572.class_573 left);

        float cb$swimAmount();

        void cb$swimAmount(float swimAmount);

        boolean cb$crouching();

        void cb$crouching(boolean crouching);

        /** the 1.21.2 getArmPose(state, arm) of this model, as vanilla's */
        net.minecraft.class_572.class_573 cb$w$armPose(net.minecraft.class_10034 state, net.minecraft.class_1306 arm);
    }

    /** EndermanModel's carrying / creepy (CbRenderEndermanMixin) */
    public interface Enderman {
        boolean cb$carrying();

        boolean cb$creepy();

        void cb$enderman(boolean carrying, boolean creepy);
    }

    /** FelineModel's state (CbRenderFelineMixin) */
    public interface Feline {
        int cb$felineState();

        void cb$felineState(int state);
    }

    private CbRenderModelFields() {
    }

    // ---- setupAttackAnimation: 1.21.1 (entity, ageInTicks), 1.21.2 (state, ageInTicks) ----

    /** HumanoidModel's / PiglinModel's 1.21.2 setupAttackAnimation, public (CbRenderHumanoidMixin, CbRenderPiglinMixin) */
    public interface HumanoidAttack {
        void cb$w$attack(net.minecraft.class_10034 state, float ageInTicks);
    }

    /** a 1.21.1 model under HumanoidModel overriding setupAttackAnimation(LivingEntity, float) */
    public interface AttackLegacy {
        void method_29353(net.minecraft.class_1309 entity, float ageInTicks);
    }

    /** a 1.21.1 model under PiglinModel overriding its setupAttackAnimation(Mob, float) */
    public interface PiglinAttackLegacy {
        void method_29355(net.minecraft.class_1308 entity, float ageInTicks);
    }

    /** PiglinModel's own 1.21.2 setupAttackAnimation(PiglinRenderState, float), public (CbRenderPiglinMixin) */
    public interface PiglinAttack {
        void cb$w$piglinAttack(net.minecraft.class_10054 state, float ageInTicks);
    }

    /**
     * The 1.21.2 setupAttackAnimation(state, ageInTicks) synthesized on a 1.21.1 model overriding the entity shape
     * (overrideHooks, guarded): 1.21.2's HumanoidModel.setupAnim calls it where 1.21.1's called the entity shape with the
     * same ageInTicks; the mod's override gets the entity the state was extracted from. A state no extraction filled
     * falls through to the 1.21.2 implementation (guard held: the synthesized method answers with vanilla's).
     */
    public static void attack(AttackLegacy self, net.minecraft.class_10034 state, float ageInTicks) {
        if (CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1309 e) {
            self.method_29353(e, ageInTicks);
        } else {
            ((HumanoidAttack) self).cb$w$attack(state, ageInTicks);
        }
    }

    /** the same for PiglinModel's setupAttackAnimation(Mob, float) -> (PiglinRenderState, float) */
    public static void piglinAttack(PiglinAttackLegacy self, net.minecraft.class_10054 state, float ageInTicks) {
        if (CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1308 e) {
            self.method_29355(e, ageInTicks);
        } else {
            ((PiglinAttack) self).cb$w$piglinAttack(state, ageInTicks);
        }
    }

    // ---- fields 1.21.2 keeps as other members (instanceFieldGetters / fieldGetters) ----

    /** PartPose x / y / z / xRot / yRot / zRot: 1.21.2's PartPose is a record of the same values (and a scale) */
    public static float field_27702(net.minecraft.class_5603 p) {
        return p.comp_2997();
    }

    public static float field_27703(net.minecraft.class_5603 p) {
        return p.comp_2998();
    }

    public static float field_27704(net.minecraft.class_5603 p) {
        return p.comp_2999();
    }

    public static float field_27705(net.minecraft.class_5603 p) {
        return p.comp_3000();
    }

    public static float field_27706(net.minecraft.class_5603 p) {
        return p.comp_3001();
    }

    public static float field_27707(net.minecraft.class_5603 p) {
        return p.comp_3002();
    }

    /** VillagerModel.root (protected): the part the model was built from, 1.21.2's Model.root() */
    public static net.minecraft.class_630 field_27525(net.minecraft.class_620 m) {
        return m.method_63512();
    }

    /** PiglinModel.rightEar: 1.21.2 keeps it on AbstractPiglinModel, the piglin model's superclass */
    public static net.minecraft.class_630 field_27464(net.minecraft.class_4840 m) {
        return ((net.minecraft.class_9939<?>) m).field_52861;
    }

    /** FelineModel.SITTING_STATE (protected static final int 3), the state CatModel sets for a sitting cat */
    public static int field_32520() {
        return 3;
    }

    /** what 1.21.1's LivingEntityRenderer.render set on its model before posing it */
    public static void legacy(net.minecraft.class_583<?> model, net.minecraft.class_1309 entity, float partialTick) {
        ((Pose) model).cb$pose(CbRenderLiving.attackAnim(model, entity, partialTick), entity.method_5765(), entity.method_6109());
    }

    /** what 1.21.1's renderers set on the model's own pose fields before posing it, read off the extracted state */
    public static void legacyState(Object model, net.minecraft.class_1297 entity, net.minecraft.class_10017 state) {
        if (model instanceof Humanoid h && state instanceof net.minecraft.class_10034 s) {
            // PlayerRenderer.setModelProperties: the poses of the main / off hand (1.21.2's PlayerModel.getArmPose
            // reads the same extraction); SkeletonModel / DrownedModel: their prepareMobModel, whose 1.21.2 getArmPose
            // answers the same; any other humanoid model: EMPTY, which 1.21.2's HumanoidModel.getArmPose answers
            h.cb$armPoses(h.cb$w$armPose(s, net.minecraft.class_1306.field_6183), h.cb$w$armPose(s, net.minecraft.class_1306.field_6182));
            // HumanoidModel.prepareMobModel: entity.getSwimAmount(partialTick), which 1.21.2 extracts into swimAmount
            h.cb$swimAmount(s.field_53403);
            // only 1.21.1's PlayerRenderer set crouching (player.isCrouching(), extracted as isCrouching)
            h.cb$crouching(entity instanceof net.minecraft.class_1657 && s.field_53410);
        }
        if (model instanceof Enderman en && entity instanceof net.minecraft.class_1560 e) {
            // EndermanRenderer.render
            en.cb$enderman(e.method_7027() != null, e.method_7028());
        }
    }

    /** the fields 1.21.1's vanilla prepareMobModel of the model's classes set (their bodies, see the class doc) */
    public static void prepare(Object model, net.minecraft.class_1297 entity, float partialTick) {
        if (model instanceof Humanoid h) {
            net.minecraft.class_572.class_573 pose = null;
            if (model instanceof net.minecraft.class_606 && entity instanceof net.minecraft.class_1308 mob) {
                // SkeletonModel.prepareMobModel: a bow in the main hand of an aggressive mob -> BOW_AND_ARROW
                pose = mob.method_5998(net.minecraft.class_1268.field_5808).method_31574(net.minecraft.class_1802.field_8102) && mob.method_6510()
                    ? net.minecraft.class_572.class_573.field_3403 : null;
                h.cb$armPoses(net.minecraft.class_572.class_573.field_3409, net.minecraft.class_572.class_573.field_3409);
            } else if (model instanceof net.minecraft.class_564 && entity instanceof net.minecraft.class_1308 mob) {
                // DrownedModel.prepareMobModel: a trident in the main hand of an aggressive drowned -> THROW_SPEAR
                pose = mob.method_5998(net.minecraft.class_1268.field_5808).method_31574(net.minecraft.class_1802.field_8547) && mob.method_6510()
                    ? net.minecraft.class_572.class_573.field_3407 : null;
                h.cb$armPoses(net.minecraft.class_572.class_573.field_3409, net.minecraft.class_572.class_573.field_3409);
            }
            if (pose != null) {
                net.minecraft.class_1308 mob = (net.minecraft.class_1308) entity;
                if (mob.method_6068() == net.minecraft.class_1306.field_6183) {
                    h.cb$armPoses(pose, h.cb$leftArmPose());
                } else {
                    h.cb$armPoses(h.cb$rightArmPose(), pose);
                }
            }
            // HumanoidModel.prepareMobModel
            if (entity instanceof net.minecraft.class_1309 living) {
                h.cb$swimAmount(living.method_6024(partialTick));
            }
        }
        if (model instanceof Feline f) {
            // FelineModel.prepareMobModel
            f.cb$felineState(entity.method_18276() ? 0 : entity.method_5624() ? 2 : 1);
            // CatModel.prepareMobModel, after its super call
            if (model instanceof net.minecraft.class_3680 && entity instanceof net.minecraft.class_1321 t && t.method_6172()) {
                f.cb$felineState(3);
            }
        }
    }

    /**
     * Writes the model's 1.21.1 pose fields into the state 1.21.2 code will pose it from; answers what to restore.
     * attackTime / riding / young go to HumanoidRenderState.attackTime / isPassenger and LivingEntityRenderState.isBaby.
     */
    public static Object carry(Object model, net.minecraft.class_1297 entity, net.minecraft.class_10017 state) {
        Object[] saved = new Object[10];
        Pose pose = (Pose) model;
        if (state instanceof net.minecraft.class_10042 l) {
            saved[0] = l.field_53457;
            l.field_53457 = pose.cb$young();
        }
        if (state instanceof net.minecraft.class_10034 h) {
            saved[1] = h.field_53404;
            saved[2] = h.field_53413;
            h.field_53404 = pose.cb$attackTime();
            h.field_53413 = pose.cb$riding();
            if (model instanceof Humanoid hm) {
                saved[3] = h.field_53403;
                saved[4] = h.field_53410;
                h.field_53403 = hm.cb$swimAmount();
                h.field_53410 = hm.cb$crouching();
            }
        }
        if (state instanceof net.minecraft.class_10016 e && model instanceof Enderman em) {
            saved[5] = e.field_53323;
            saved[6] = e.field_53322;
            // 1.21.2's EndermanModel reads carriedBlock != null; the block itself is the held-block layer's, which
            // does not run here: the entity's carried block, or air when only the flag says it carries one
            net.minecraft.class_2680 held = e.field_53323;
            if (held == null && entity instanceof net.minecraft.class_1560 en) {
                held = en.method_7027();
            }
            e.field_53323 = em.cb$carrying() ? (held != null ? held : net.minecraft.class_2246.field_10124.method_9564()) : null;
            e.field_53322 = em.cb$creepy();
        }
        if (state instanceof net.minecraft.class_10024 fs && model instanceof Feline f) {
            saved[7] = fs.field_53361;
            saved[8] = fs.field_53362;
            saved[9] = fs.field_53363;
            int s = f.cb$felineState();
            fs.field_53361 = s == 0;
            fs.field_53362 = s == 2;
            fs.field_53363 = s == 3;
        }
        return saved;
    }

    public static void restore(net.minecraft.class_10017 state, Object o) {
        Object[] saved = (Object[]) o;
        if (state instanceof net.minecraft.class_10042 l && saved[0] != null) {
            l.field_53457 = (Boolean) saved[0];
        }
        if (state instanceof net.minecraft.class_10034 h && saved[1] != null) {
            h.field_53404 = (Float) saved[1];
            h.field_53413 = (Boolean) saved[2];
            if (saved[3] != null) {
                h.field_53403 = (Float) saved[3];
                h.field_53410 = (Boolean) saved[4];
            }
        }
        if (state instanceof net.minecraft.class_10016 e && saved[6] != null) {
            e.field_53323 = (net.minecraft.class_2680) saved[5];
            e.field_53322 = (Boolean) saved[6];
        }
        if (state instanceof net.minecraft.class_10024 fs && saved[7] != null) {
            fs.field_53361 = (Boolean) saved[7];
            fs.field_53362 = (Boolean) saved[8];
            fs.field_53363 = (Boolean) saved[9];
        }
    }
}
