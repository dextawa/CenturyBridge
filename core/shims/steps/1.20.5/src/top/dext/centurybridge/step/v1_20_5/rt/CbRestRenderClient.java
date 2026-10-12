package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client values 1.20.4 calls did not pass and 1.20.5 takes as parameters, each what 1.20.5's own caller
 * hands over (or what 1.20.4's body computed for itself); used by call reshapes and as frames defaults.
 */
@Environment(EnvType.CLIENT)
public final class CbRestRenderClient {

    /**
     * the frame's partial tick (Minecraft.getFrameTime()), what 1.20.5's EntityRenderer.render hands
     * renderNameTag: 1.20.4's renderNameTag took none (it used it for nothing; 1.20.5 turns the name tag
     * attachment by the entity's interpolated yaw)
     */
    public static float frameTime() {
        return net.minecraft.class_310.method_1551().method_1488();
    }

    /** 1.20.4 ChatComponent.render read isChatFocused() itself; 1.20.5 takes it as a parameter */
    public static boolean chatFocused(net.minecraft.class_338 chat) {
        return chat.method_1819();
    }

    /** 1.20.4 ChatComponent.render(graphics, tickCount, mouseX, mouseY): focused as it read it */
    public static void method_1805(net.minecraft.class_338 chat, net.minecraft.class_332 graphics, int tickCount,
                                   int mouseX, int mouseY) {
        chat.method_1805(graphics, tickCount, mouseX, mouseY, chat.method_1819());
    }

    /**
     * the generic receiving-level screen (ReceivingLevelScreen.Reason.OTHER): 1.20.4 had no reason, its
     * screen was the plain "loading terrain" one; 1.20.5 picks portal art for the other two reasons
     */
    public static net.minecraft.class_434.class_9678 otherReason() {
        return net.minecraft.class_434.class_9678.field_51489;
    }

    private CbRestRenderClient() {
    }
}
