package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.11's HumanoidModel.setAllVisible(visible), gone in 26.1: it showed or hid the head, hat, body, arms and legs;
 * PlayerModel's override also the sleeves, pants and jacket (26.1's PlayerModel still has them). 1.21.11's
 * AbstractPiglinModel override did the same for its sleeves / pants / jacket, parts 26.1's piglin mesh no longer has.
 */
@Environment(EnvType.CLIENT)
@Mixin(HumanoidModel.class)
public abstract class CbWallHumanoidModelVisibleMixin {

    public void setAllVisible(boolean visible) {
        HumanoidModel<?> self = (HumanoidModel<?>) (Object) this;
        self.head.visible = visible;
        self.hat.visible = visible;
        self.body.visible = visible;
        self.rightArm.visible = visible;
        self.leftArm.visible = visible;
        self.rightLeg.visible = visible;
        self.leftLeg.visible = visible;
        if ((Object) this instanceof PlayerModel player) {
            player.leftSleeve.visible = visible;
            player.rightSleeve.visible = visible;
            player.leftPants.visible = visible;
            player.rightPants.visible = visible;
            player.jacket.visible = visible;
        }
    }
}
