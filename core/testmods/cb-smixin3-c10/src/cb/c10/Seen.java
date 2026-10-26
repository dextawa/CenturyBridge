package cb.c10;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public final class Seen {
    public static Gui last;

    public static void gui(Gui g) {
        last = g;
    }

    public static int ticks(Minecraft mc) {
        return ((cb.c10.mixin.GuiAccessor) mc.gui).cbc10$getTickCount();
    }
}
