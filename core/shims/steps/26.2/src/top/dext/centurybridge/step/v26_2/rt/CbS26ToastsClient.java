package top.dext.centurybridge.step.v26_2.rt;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** 26.1.2 toast members 26.2 dropped (javap of both). */
@Environment(EnvType.CLIENT)
public final class CbS26ToastsClient {
    private CbS26ToastsClient() {}

    /**
     * 26.1.2 SystemToast.multiline(minecraft, id, title, message): the title on one line, the message split at 200,
     * the width the widest message line (at least 200) plus 30. 26.2's toast always wraps title and message at 200
     * and sizes on the widest of both (at least 160); the layer builds 26.2's toast and sets 26.1.2's lines and width
     * (fields opened by the layer's access widener). A later reset or update re-lays it out as 26.2 does, as 26.1.2's
     * reset re-laid out a multiline toast as a single-line one.
     */
    public static SystemToast multiline(Minecraft minecraft, SystemToast.SystemToastId id, Component title, Component message) {
        Font font = minecraft.font;
        List<FormattedCharSequence> lines = font.split(message, 200);
        int width = Math.max(200, lines.stream().mapToInt(font::width).max().orElse(200));
        SystemToast toast = new SystemToast(id, title, message);
        toast.titleLines = List.of(title.getVisualOrderText());
        toast.messageLines = lines;
        toast.width = width + 30;
        return toast;
    }
}
