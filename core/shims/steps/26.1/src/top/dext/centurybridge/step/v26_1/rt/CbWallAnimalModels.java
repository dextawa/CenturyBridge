package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.definitions.ArmadilloAnimation;
import net.minecraft.client.animation.definitions.CamelAnimation;
import net.minecraft.client.animation.definitions.RabbitAnimation;
import net.minecraft.client.model.animal.armadillo.AdultArmadilloModel;
import net.minecraft.client.model.animal.armadillo.ArmadilloModel;
import net.minecraft.client.model.animal.camel.AdultCamelModel;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.rabbit.AdultRabbitModel;
import net.minecraft.client.model.animal.rabbit.RabbitModel;
import net.minecraft.client.model.animal.turtle.AdultTurtleModel;
import net.minecraft.client.model.animal.turtle.TurtleModel;
import net.minecraft.client.model.geom.ModelPart;

/**
 * 1.21.11's CamelModel(root), RabbitModel(root) and ArmadilloModel(root) baked their animations from the
 * vanilla definitions themselves (javap class_7751 / class_596 / class_9082); 26.1's take the definitions as
 * constructor arguments, and its AdultCamelModel / AdultRabbitModel / AdultArmadilloModel(root) pass the
 * definitions 1.21.11's models used (javap). A 1.21.11 `new` gets the adult model; a subclass's super
 * constructor gets the same definitions pushed (superCtorReshapes).
 */
@Environment(EnvType.CLIENT)
public final class CbWallAnimalModels {
    private CbWallAnimalModels() {
    }

    /** new CamelModel(root) */
    public static CamelModel camel(ModelPart root) {
        return new AdultCamelModel(root);
    }

    /** new RabbitModel(root) */
    public static RabbitModel rabbit(ModelPart root) {
        return new AdultRabbitModel(root);
    }

    /** new ArmadilloModel(root) */
    public static ArmadilloModel armadillo(ModelPart root) {
        return new AdultArmadilloModel(root);
    }

    /**
     * new TurtleModel(root): 1.21.11's TurtleModel is 26.1's AdultTurtleModel (render type, egg belly and setupAnim,
     * javap class_614 / AdultTurtleModel); a subclass extends facade/LegacyTurtleModel, the same class.
     */
    public static TurtleModel turtle(ModelPart root) {
        return new AdultTurtleModel(root);
    }

    // ---- the definitions AdultCamelModel passes, in CamelModel's order ----

    public static AnimationDefinition camelWalk() {
        return CamelAnimation.CAMEL_WALK;
    }

    public static AnimationDefinition camelSit() {
        return CamelAnimation.CAMEL_SIT;
    }

    public static AnimationDefinition camelSitPose() {
        return CamelAnimation.CAMEL_SIT_POSE;
    }

    public static AnimationDefinition camelStandup() {
        return CamelAnimation.CAMEL_STANDUP;
    }

    public static AnimationDefinition camelIdle() {
        return CamelAnimation.CAMEL_IDLE;
    }

    public static AnimationDefinition camelDash() {
        return CamelAnimation.CAMEL_DASH;
    }

    // ---- AdultRabbitModel's ----

    public static AnimationDefinition rabbitHop() {
        return RabbitAnimation.HOP;
    }

    public static AnimationDefinition rabbitIdleHeadTilt() {
        return RabbitAnimation.IDLE_HEAD_TILT;
    }

    // ---- AdultArmadilloModel's ----

    public static AnimationDefinition armadilloWalk() {
        return ArmadilloAnimation.ARMADILLO_WALK;
    }

    public static AnimationDefinition armadilloRollOut() {
        return ArmadilloAnimation.ARMADILLO_ROLL_OUT;
    }

    public static AnimationDefinition armadilloRollUp() {
        return ArmadilloAnimation.ARMADILLO_ROLL_UP;
    }

    public static AnimationDefinition armadilloPeek() {
        return ArmadilloAnimation.ARMADILLO_PEEK;
    }
}
