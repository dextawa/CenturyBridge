package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.rabbit.RabbitModel;
import net.minecraft.client.model.geom.ModelPart;

/**
 * 1.21.11 RabbitModel leg fields (class_596.field_27482..27485, opened by access wideners): 1.21.11's constructor took
 * them as root.getChild("left_haunch" / "right_haunch" / "left_front_leg" / "right_front_leg"). 26.1 (javap of
 * AdultRabbitModel and BabyRabbitModel createBodyLayer) builds parts with the same names in both models, nested:
 * root > body > frontlegs > left_front_leg / right_front_leg, root > backlegs > left_hind_leg > left_haunch and
 * root > backlegs > right_hind_leg > right_haunch. Each read answers that part of the model's own root.
 */
@Environment(EnvType.CLIENT)
public final class CbWallRabbitParts {
    private CbWallRabbitParts() {
    }

    public static ModelPart leftHaunch(RabbitModel m) {
        return m.root().getChild("backlegs").getChild("left_hind_leg").getChild("left_haunch");
    }

    public static ModelPart rightHaunch(RabbitModel m) {
        return m.root().getChild("backlegs").getChild("right_hind_leg").getChild("right_haunch");
    }

    public static ModelPart leftFrontLeg(RabbitModel m) {
        return m.root().getChild("body").getChild("frontlegs").getChild("left_front_leg");
    }

    public static ModelPart rightFrontLeg(RabbitModel m) {
        return m.root().getChild("body").getChild("frontlegs").getChild("right_front_leg");
    }
}
