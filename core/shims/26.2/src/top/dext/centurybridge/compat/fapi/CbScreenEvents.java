package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screens.Screen;

/** fabric-screen-api-v1's ScreenEvents.afterRender(screen) (3 corpus mods): 26.2 split render into extract/background/foreground; after-foreground is the 1.20.1 "after render" moment (staticRedirect on the surviving ScreenEvents class). */
public final class CbScreenEvents {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Event<CbScreenAfterRender> afterRender(Screen screen) {
        return (Event) ScreenEvents.afterForeground(screen);
    }

    private CbScreenEvents() {
    }
}
