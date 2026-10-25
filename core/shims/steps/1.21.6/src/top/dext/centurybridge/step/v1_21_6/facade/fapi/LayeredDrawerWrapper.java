package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's LayeredDrawerWrapper (fabric-rendering-v1), on 1.21.6's HudElementRegistry (CbHudLayers). The two
 * default methods are 1.21.5's (javap): IdentifiedLayer.of, then the abstract overload.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface LayeredDrawerWrapper {

    LayeredDrawerWrapper addLayer(IdentifiedLayer layer);

    LayeredDrawerWrapper attachLayerBefore(net.minecraft.class_2960 beforeThis, IdentifiedLayer layer);

    default LayeredDrawerWrapper attachLayerBefore(net.minecraft.class_2960 beforeThis, net.minecraft.class_2960 id,
            top.dext.centurybridge.step.v1_21_6.facade.LayeredDraw.Layer layer) {
        return this.attachLayerBefore(beforeThis, IdentifiedLayer.of(id, layer));
    }

    LayeredDrawerWrapper attachLayerAfter(net.minecraft.class_2960 afterThis, IdentifiedLayer layer);

    default LayeredDrawerWrapper attachLayerAfter(net.minecraft.class_2960 afterThis, net.minecraft.class_2960 id,
            top.dext.centurybridge.step.v1_21_6.facade.LayeredDraw.Layer layer) {
        return this.attachLayerAfter(afterThis, IdentifiedLayer.of(id, layer));
    }

    LayeredDrawerWrapper removeLayer(net.minecraft.class_2960 id);

    LayeredDrawerWrapper replaceLayer(net.minecraft.class_2960 id, java.util.function.Function<IdentifiedLayer, IdentifiedLayer> replacer);
}
