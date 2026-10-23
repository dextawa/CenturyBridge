package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 PlayerRenderState.HandState (class_10055$class_10056, gone in 1.21.4): what the player holds in one hand, as the
 * player renderer's arm poses read it. 1.21.4's PlayerRenderer.extractRenderState fills one per hand again
 * (CbStatePlayerRendererMixin), as 1.21.3's did.
 */
@Environment(EnvType.CLIENT)
public class LegacyPlayerHandState {
    /** isEmpty */
    public boolean field_53547 = true;
    /** useAnimation */
    public net.minecraft.class_1839 field_53548;
    /** holdsChargedCrossbow */
    public boolean field_53549;

    public LegacyPlayerHandState() {
    }

    /** 1.21.3 PlayerRenderer.extractHandState(player, state, hand) */
    public static void extract(net.minecraft.class_742 player, LegacyPlayerHandState state, net.minecraft.class_1268 hand) {
        net.minecraft.class_1799 stack = player.method_5998(hand);
        state.field_53547 = stack.method_7960();
        state.field_53548 = !stack.method_7960() ? stack.method_7976() : null;
        state.field_53549 = stack.method_31574(net.minecraft.class_1802.field_8399) && net.minecraft.class_1764.method_7781(stack);
    }
}
