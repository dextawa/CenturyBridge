package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_332;

/**
 * Fabric API 0.136's (1.21.8) HUD element VanillaHudElements.DEBUG, which Fabric API for 1.21.9 dropped with the F3
 * overlay leaving the HUD (1.21.9 draws it from GameRenderer and the debug options screen through Gui.renderDebugOverlay).
 * The layer keeps 0.136's root layer for it: the vanilla overlay plus the elements converted mods attach before or
 * after it (or after / before those), with 0.136's remove and replace. A converted mod's HudElementRegistry calls come
 * here (staticRedirects); one whose anchor is not in this root goes on to Fabric API unchanged. CbDebugHudMixin draws
 * the root at Gui.renderDebugOverlay; while nothing was attached, removed or replaced the overlay is 1.21.9's.
 */
@Environment(EnvType.CLIENT)
public final class CbHud {

    private CbHud() {
    }

    /** VanillaHudElements.DEBUG (0.136: Identifier.ofVanilla("debug")) */
    public static final class_2960 DEBUG = class_2960.method_60656("debug");

    /** one layer of the root: an id, the element (function of the vanilla element for the vanilla layer), removed */
    private static final class Layer {
        final class_2960 id;
        Function<HudElement, HudElement> element;
        boolean removed;

        Layer(class_2960 id, Function<HudElement, HudElement> element) {
            this.id = id;
            this.element = element;
        }
    }

    private static final List<Layer> ROOT = new ArrayList<>();
    private static volatile boolean modified;

    static {
        ROOT.add(new Layer(DEBUG, Function.identity()));
    }

    private static synchronized int indexOf(class_2960 id) {
        for (int i = 0; i < ROOT.size(); i++) {
            if (ROOT.get(i).id.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private static void validateUnique(class_2960 id) {
        if (indexOf(id) >= 0) {
            throw new IllegalArgumentException("ID " + id + " already exists in the HUD element registry");
        }
    }

    /** HudElementRegistry.attachElementBefore(beforeThis, identifier, element) */
    public static synchronized void attachElementBefore(class_2960 beforeThis, class_2960 identifier, HudElement element) {
        int at = indexOf(beforeThis);
        if (at < 0) {
            HudElementRegistry.attachElementBefore(beforeThis, identifier, element);
            return;
        }
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(element, "element");
        validateUnique(identifier);
        ROOT.add(at, new Layer(identifier, vanilla -> element));
        modified = true;
    }

    /** HudElementRegistry.attachElementAfter(afterThis, identifier, element) */
    public static synchronized void attachElementAfter(class_2960 afterThis, class_2960 identifier, HudElement element) {
        int at = indexOf(afterThis);
        if (at < 0) {
            HudElementRegistry.attachElementAfter(afterThis, identifier, element);
            return;
        }
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(element, "element");
        validateUnique(identifier);
        ROOT.add(at + 1, new Layer(identifier, vanilla -> element));
        modified = true;
    }

    /** HudElementRegistry.removeElement(identifier) */
    public static synchronized void removeElement(class_2960 identifier) {
        int at = indexOf(identifier);
        if (at < 0) {
            HudElementRegistry.removeElement(identifier);
            return;
        }
        ROOT.get(at).removed = true;
        modified = true;
    }

    /** HudElementRegistry.replaceElement(identifier, replacer): the replacer gets the element as it stands */
    public static synchronized void replaceElement(class_2960 identifier, Function<HudElement, HudElement> replacer) {
        int at = indexOf(identifier);
        if (at < 0) {
            HudElementRegistry.replaceElement(identifier, replacer);
            return;
        }
        Objects.requireNonNull(replacer, "replacer");
        Layer layer = ROOT.get(at);
        Function<HudElement, HudElement> previous = layer.element;
        layer.element = vanilla -> Objects.requireNonNull(replacer.apply(previous.apply(vanilla)), "replacement element");
        modified = true;
    }

    private static boolean drawingVanilla;

    /**
     * Gui.renderDebugOverlay HEAD: false = draw 1.21.9's overlay as it is (nothing attached, removed or replaced, or the
     * vanilla layer's own call). Otherwise the root is drawn here, the vanilla layer calling renderDebugOverlay again.
     */
    public static boolean cbDrawRoot(class_329 gui, class_332 graphics) {
        if (!modified || drawingVanilla) {
            return false;
        }
        List<Layer> layers;
        synchronized (CbHud.class) {
            layers = new ArrayList<>(ROOT);
        }
        HudElement vanilla = (g, tickCounter) -> {
            drawingVanilla = true;
            try {
                gui.method_70838(g);
            } finally {
                drawingVanilla = false;
            }
        };
        net.minecraft.class_9779 tickCounter = class_310.method_1551().method_61966();
        for (Layer layer : layers) {
            if (!layer.removed) {
                layer.element.apply(vanilla).render(graphics, tickCounter);
            }
        }
        return true;
    }
}
