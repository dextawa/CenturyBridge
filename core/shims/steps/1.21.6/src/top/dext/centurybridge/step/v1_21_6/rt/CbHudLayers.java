package top.dext.centurybridge.step.v1_21_6.rt;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.HudLayerRegistrationCallback;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.IdentifiedLayer;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.LayeredDrawerWrapper;

/**
 * Fabric API 1.21.5's LayeredDrawerWrapper on 1.21.6's HudElementRegistry. 1.21.5's wrapper edited Gui's layer list
 * (LayeredDrawerWrapperImpl, javap): add appends, attach before / after inserts next to the layer of that id, remove
 * drops it, replace sets the replacer's answer (applied once, at the call) in its place; an id already present or a
 * missing anchor throws IllegalArgumentException. 1.21.6's registry does the same over its vanilla elements and the
 * elements mods added (HudElementRegistryImpl, javap), so a layer becomes an element with its id and its render.
 *
 * <p>Every 1.21.5 vanilla layer id is a 1.21.6 vanilla element id except hotbar_and_bars. 1.21.5's layer drew, in
 * Gui.renderHotbarAndDecorations (method_55805, javap): the spectator menu or hotbar, the contextual bar (jump meter or
 * experience bar), armor / health / food / air, the mount's health, the held item's name and the spectator tooltip --
 * 1.21.6's elements SPECTATOR_MENU, HOTBAR, ARMOR_BAR, HEALTH_BAR, FOOD_BAR, AIR_BAR, MOUNT_HEALTH, INFO_BAR,
 * HELD_ITEM_TOOLTIP, SPECTATOR_TOOLTIP. 1.21.6 draws EXPERIENCE_LEVEL between INFO_BAR and HELD_ITEM_TOOLTIP; 1.21.5
 * drew the whole group before it. So: before hotbar_and_bars is before SPECTATOR_MENU, after it is after INFO_BAR (the
 * group's last piece before EXPERIENCE_LEVEL, as 1.21.5 had it), removing it removes the ten pieces, and replacing it
 * hands the replacer one layer drawing the ten pieces (each as 1.21.6 currently draws it, other mods' replacements
 * included) and draws the answer at INFO_BAR's place, the pieces themselves drawing nothing. The two tooltips are drawn
 * by 1.21.6 after that place, so the group layer draws them as captured in the previous frame.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbHudLayers implements LayeredDrawerWrapper {

    private static final CbHudLayers INSTANCE = new CbHudLayers();
    private static final HudElement NOTHING = (graphics, delta) -> { };

    private static boolean fired;

    /** hotbar_and_bars' pieces, in 1.21.6's order */
    private final net.minecraft.class_2960[] group = {
        VanillaHudElements.SPECTATOR_MENU, VanillaHudElements.HOTBAR, VanillaHudElements.ARMOR_BAR,
        VanillaHudElements.HEALTH_BAR, VanillaHudElements.FOOD_BAR, VanillaHudElements.AIR_BAR,
        VanillaHudElements.MOUNT_HEALTH, VanillaHudElements.INFO_BAR, VanillaHudElements.HELD_ITEM_TOOLTIP,
        VanillaHudElements.SPECTATOR_TOOLTIP};
    /** the index of INFO_BAR, where the group stands */
    private static final int SLOT = 7;

    /** layers added or replaced through this wrapper, by id: what 1.21.5's list held under that id */
    private final java.util.Map<net.minecraft.class_2960, IdentifiedLayer> layers = new java.util.HashMap<>();

    /** the group's pieces as 1.21.6 last handed them over, once the group is replaced */
    private final HudElement[] captured = new HudElement[group.length];
    /** what stands for hotbar_and_bars once replaced; null before */
    private IdentifiedLayer groupLayer;
    private HudElement groupElement;

    private CbHudLayers() {
    }

    /** HudLayerRegistrationCallback, invoked once, when Gui is built (CbGuiHudLayersMixin) */
    public static void fire() {
        if (!fired) {
            fired = true;
            HudLayerRegistrationCallback.EVENT.invoker().register(INSTANCE);
        }
    }

    private static HudElement element(IdentifiedLayer layer) {
        return layer::render;
    }

    private void validate(net.minecraft.class_2960 id) {
        if (IdentifiedLayer.HOTBAR_AND_BARS.equals(id)) {
            throw new IllegalArgumentException("Layer with identifier " + id + " already exists");
        }
    }

    @Override
    public LayeredDrawerWrapper addLayer(IdentifiedLayer layer) {
        validate(layer.id());
        HudElementRegistry.addLast(layer.id(), element(layer));
        layers.put(layer.id(), layer);
        return this;
    }

    @Override
    public LayeredDrawerWrapper attachLayerBefore(net.minecraft.class_2960 beforeThis, IdentifiedLayer layer) {
        validate(layer.id());
        HudElementRegistry.attachElementBefore(IdentifiedLayer.HOTBAR_AND_BARS.equals(beforeThis) ? group[0] : beforeThis,
            layer.id(), element(layer));
        layers.put(layer.id(), layer);
        return this;
    }

    @Override
    public LayeredDrawerWrapper attachLayerAfter(net.minecraft.class_2960 afterThis, IdentifiedLayer layer) {
        validate(layer.id());
        HudElementRegistry.attachElementAfter(IdentifiedLayer.HOTBAR_AND_BARS.equals(afterThis) ? group[SLOT] : afterThis,
            layer.id(), element(layer));
        layers.put(layer.id(), layer);
        return this;
    }

    @Override
    public LayeredDrawerWrapper removeLayer(net.minecraft.class_2960 id) {
        if (IdentifiedLayer.HOTBAR_AND_BARS.equals(id)) {
            for (net.minecraft.class_2960 piece : group) {
                HudElementRegistry.removeElement(piece);
            }
        } else {
            HudElementRegistry.removeElement(id);
            layers.remove(id);
        }
        return this;
    }

    @Override
    public LayeredDrawerWrapper replaceLayer(net.minecraft.class_2960 id, java.util.function.Function<IdentifiedLayer, IdentifiedLayer> replacer) {
        if (IdentifiedLayer.HOTBAR_AND_BARS.equals(id)) {
            replaceGroup(replacer);
            return this;
        }
        HudElement[] current = new HudElement[1];
        IdentifiedLayer before = layers.get(id);
        IdentifiedLayer in = before != null ? before : IdentifiedLayer.of(id, (graphics, delta) -> {
            if (current[0] != null) {
                current[0].render(graphics, delta);
            }
        });
        IdentifiedLayer out = replacer.apply(in);
        HudElement drawn = element(out);
        HudElementRegistry.replaceElement(id, element -> {
            current[0] = element;
            return drawn;
        });
        layers.put(id, out);
        return this;
    }

    private void replaceGroup(java.util.function.Function<IdentifiedLayer, IdentifiedLayer> replacer) {
        boolean first = groupLayer == null;
        IdentifiedLayer in = first ? IdentifiedLayer.of(IdentifiedLayer.HOTBAR_AND_BARS, this::drawGroup) : groupLayer;
        groupLayer = replacer.apply(in);
        groupElement = element(groupLayer);
        if (!first) {
            return;
        }
        for (int i = 0; i < group.length; i++) {
            int k = i;
            HudElementRegistry.replaceElement(group[i], element -> {
                captured[k] = element;
                return k == SLOT ? this::drawReplacedGroup : NOTHING;
            });
        }
    }

    private void drawReplacedGroup(net.minecraft.class_332 graphics, net.minecraft.class_9779 delta) {
        try {
            groupElement.render(graphics, delta);
        } finally {
            java.util.Arrays.fill(captured, null);
        }
    }

    /** the ten pieces, as 1.21.6 handed them over */
    private void drawGroup(net.minecraft.class_332 graphics, net.minecraft.class_9779 delta) {
        for (HudElement piece : captured) {
            if (piece != null) {
                piece.render(graphics, delta);
            }
        }
    }
}
