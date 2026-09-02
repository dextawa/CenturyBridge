package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.Identifier;
import top.dext.centurybridge.facade.v26_2.GuiGraphics;

/**
 * fabric-rendering-v1's HudRenderCallback as 1.20.x mods implement it (16
 * corpus mods): onHudRender(DrawContext, tickDelta) after the vanilla HUD.
 * Since 1.21.6 the HUD is a retained-mode element list (HudElementRegistry,
 * extractRenderState(GuiGraphicsExtractor, DeltaTracker)); one element added
 * last forwards into the old-shape event with the extractor wrapped in the
 * DrawContext facade the converter already hands to every mod screen.
 */
@FunctionalInterface
public interface CbHudRenderCallback {

    Event<CbHudRenderCallback> EVENT = Bridge.create();

    void onHudRender(GuiGraphics drawContext, float tickDelta);

    final class Bridge {
        private Bridge() {
        }

        static Event<CbHudRenderCallback> create() {
            Event<CbHudRenderCallback> ev = EventFactory.createArrayBacked(CbHudRenderCallback.class,
                listeners -> (ctx, delta) -> {
                    for (CbHudRenderCallback l : listeners) {
                        l.onHudRender(ctx, delta);
                    }
                });
            HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("centurybridge", "legacy_hud_render"),
                (extractor, tracker) -> ev.invoker().onHudRender(new GuiGraphics(extractor), tracker.getGameTimeDeltaPartialTick(false)));
            return ev;
        }
    }
}
