package top.dext.centurybridge.step.v26_1.compat.fapi.client.rendering.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * 1.21.11's net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback, which 26.1's Fabric API removed
 * (HudElementRegistry replaces it). The same event and invoker; fired, as 1.21.11's GuiMixin fired it, at the
 * end of Gui's HUD pass (Gui.extractRenderState, 1.21.11's Gui.render: mixin/CbFapiHudRenderCallbackMixin).
 */
@Environment(EnvType.CLIENT)
public interface HudRenderCallback {
    Event<HudRenderCallback> EVENT = EventFactory.createArrayBacked(HudRenderCallback.class, listeners -> (graphics, tickCounter) -> {
        for (HudRenderCallback listener : listeners) {
            listener.onHudRender(graphics, tickCounter);
        }
    });

    void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker tickCounter);
}
