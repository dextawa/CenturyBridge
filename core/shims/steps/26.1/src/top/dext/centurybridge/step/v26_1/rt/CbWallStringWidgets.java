package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

/**
 * 1.21.11's StringWidget.clipText(text, font, width), which 26.1 removed: 1.21.11's body (javap
 * class_7842.method_52728) cut the text to the width less an ellipsis and added the ellipsis, in visual order.
 */
@Environment(EnvType.CLIENT)
public final class CbWallStringWidgets {
    private CbWallStringWidgets() {
    }

    /** StringWidget.clipText(text, font, width) */
    public static FormattedCharSequence clipText(Component text, Font font, int width) {
        FormattedText clipped = font.substrByWidth(text, width - font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(clipped, CommonComponents.ELLIPSIS));
    }
}
