package top.dext.centurybridge.step.v26_2.rt;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Hud;

/**
 * 26.1.2 OptionInstance took the value-changed callback as a Consumer (accept(value)); 26.2 takes an
 * OptionInstance.ValueUpdateListener (valueChanged(value)), called where 26.1.2 called accept (javap of both).
 * 26.1.2's Options.hideGui (F1, read by the HUD and the hand renderer) is 26.2's Hud.isHidden: KeyboardHandler's F1
 * calls hud.toggle(), Hud.extractRenderState reads isHidden where 26.1.2's Gui read options.hideGui.
 */
@Environment(EnvType.CLIENT)
public final class CbS26OptionsClient {
    private CbS26OptionsClient() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static OptionInstance.ValueUpdateListener toListener(Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        if (consumer instanceof OptionInstance.ValueUpdateListener l) {
            return l;
        }
        return consumer::accept;
    }

    /** 26.1.2 options.hideGui (instanceFieldGetters): whether the HUD of the Minecraft holding these options is hidden */
    public static boolean hideGui(Options options) {
        return hud(options).isHidden();
    }

    /** 26.1.2 options.hideGui = value (instanceFieldSetters): the HUD hidden or shown, as F1 toggles it */
    public static void setHideGui(Options options, boolean value) {
        Hud hud = hud(options);
        if (hud.isHidden() != value) {
            hud.toggle();
        }
    }

    private static Hud hud(Options options) {
        return options.minecraft.gui.hud;
    }
}
