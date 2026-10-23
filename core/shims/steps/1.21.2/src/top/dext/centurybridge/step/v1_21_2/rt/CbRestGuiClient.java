package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1921;
import net.minecraft.class_2960;

/**
 * Client-side values the s7rest GUI bridges hand to 1.21.2 where 1.21.1 had no such argument.
 */
@Environment(EnvType.CLIENT)
public final class CbRestGuiClient {

    private CbRestGuiClient() {
    }

    /**
     * The render type of a GUI blit when a 1.21.1 GuiGraphics subclass's own blit / blitSprite override calls
     * super outside a draw vanilla started (frameDefaults): 1.21.1 drew every such blit as a textured quad in
     * the GUI's state, which is 1.21.2's RenderType.guiTextured -- the type 1.21.2's own GUI passes for them.
     */
    public static Function<class_2960, class_1921> guiTextured() {
        return class_1921::method_62277;
    }
}
