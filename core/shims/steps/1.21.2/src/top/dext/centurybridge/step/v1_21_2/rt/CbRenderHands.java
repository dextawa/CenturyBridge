package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's PlayerRenderer.renderRightHand / renderLeftHand(poseStack, buffers, light, player) over 1.21.2's
 * (poseStack, buffers, light, skinTexture, sleeveVisible): 1.21.1 drew the arm with the player's skin and showed the
 * sleeve as the player's model-part option said; 1.21.2's caller (ItemInHandRenderer) passes exactly those two from the
 * local player (its skin's texture, isModelPartShown(RIGHT_SLEEVE / LEFT_SLEEVE)). A mod PlayerRenderer's 1.21.1
 * override answers 1.21.2's calls (overrideHooks) with the local player, the player 1.21.1's ItemInHandRenderer passed.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderHands {

    /** a mod PlayerRenderer's 1.21.1 hand methods */
    public interface LegacyRight {
        void method_4220(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, net.minecraft.class_742 player);
    }

    public interface LegacyLeft {
        void method_4221(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, net.minecraft.class_742 player);
    }

    private CbRenderHands() {
    }

    /** renderRightHand(poseStack, buffers, light, player) in 1.21.1's shape (also a mod's super call: 1.21.2's under the hook's guard) */
    public static void method_4220(net.minecraft.class_1007 renderer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                                   net.minecraft.class_742 player) {
        renderer.method_4220(poseStack, buffers, light, player.method_52814().comp_1626(), player.method_7348(net.minecraft.class_1664.field_7570));
    }

    /** renderLeftHand(poseStack, buffers, light, player) in 1.21.1's shape */
    public static void method_4221(net.minecraft.class_1007 renderer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                                   net.minecraft.class_742 player) {
        renderer.method_4221(poseStack, buffers, light, player.method_52814().comp_1626(), player.method_7348(net.minecraft.class_1664.field_7568));
    }

    /** 1.21.2's renderRightHand on a mod PlayerRenderer: its 1.21.1 override, with the local player */
    public static void right(LegacyRight renderer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                             net.minecraft.class_2960 skin, boolean sleeve) {
        renderer.method_4220(poseStack, buffers, light, net.minecraft.class_310.method_1551().field_1724);
    }

    /** 1.21.2's renderLeftHand on a mod PlayerRenderer: its 1.21.1 override, with the local player */
    public static void left(LegacyLeft renderer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                            net.minecraft.class_2960 skin, boolean sleeve) {
        renderer.method_4221(poseStack, buffers, light, net.minecraft.class_310.method_1551().field_1724);
    }
}
