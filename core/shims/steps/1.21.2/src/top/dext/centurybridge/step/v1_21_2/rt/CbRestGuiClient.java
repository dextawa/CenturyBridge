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

    /** RecipeButton.TICKS_TO_SWAP: 30 */
    public static int field_32413() {
        return 30;
    }

    /**
     * ReportReason.GENERIC ("I want to report them"): 1.21.2 renamed the constant I_WANT_TO_REPORT_THEM, the
     * same reason with the same text.
     */
    public static net.minecraft.class_7573 field_46063() {
        return net.minecraft.class_7573.field_53035;
    }

    /**
     * ParticleRenderType.PARTICLE_SHEET_LIT: in 1.21.1 the opaque particle sheet without its own shader call
     * (the engine set the particle shader for every sheet), drawn right after PARTICLE_SHEET_OPAQUE in the
     * same state. 1.21.2 dropped it; its particles are the opaque sheet's.
     */
    public static net.minecraft.class_3999 field_17830() {
        return net.minecraft.class_3999.field_17828;
    }
}
