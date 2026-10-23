package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's entity model and render-layer API on 1.20.5 (s4client, renderers and models): the breeze's three
 * model layers and its own-model eyes/wind layers, the stray clothing layer's fixed model and texture.
 */
@Environment(EnvType.CLIENT)
public final class CbClientModels {

    private CbClientModels() {
    }

    // ------------------------------------------------------------------ breeze model layers

    /** 1.20.4 ModelLayers.BREEZE_WIND ("minecraft:breeze#wind"), registered with its 1.20.4 definition (mixin/CbClientLayerDefinitionsMixin) */
    public static final net.minecraft.class_5601 BREEZE_WIND =
        new net.minecraft.class_5601(new net.minecraft.class_2960("minecraft", "breeze"), "wind");
    /** 1.20.4 ModelLayers.BREEZE_EYES ("minecraft:breeze#eyes") */
    public static final net.minecraft.class_5601 BREEZE_EYES =
        new net.minecraft.class_5601(new net.minecraft.class_2960("minecraft", "breeze"), "eyes");

    /** 1.20.4 ModelLayers.BREEZE_WIND */
    public static net.minecraft.class_5601 field_47444() {
        return BREEZE_WIND;
    }

    /** 1.20.4 ModelLayers.BREEZE_EYES */
    public static net.minecraft.class_5601 field_47445() {
        return BREEZE_EYES;
    }

    /**
     * 1.20.4 BreezeModel.createBodyLayer(): the body, rods and head on the 32x32 breeze texture, the wind parts
     * empty. 1.20.4's body, cube for cube. 1.20.5's BreezeModel constructor also looks up head/eyes, which
     * this layer did not have: it gets an empty eyes part, which draws nothing.
     */
    public static net.minecraft.class_5607 method_55233() {
        var l0 = new net.minecraft.class_5609();
        var l1 = l0.method_32111();
        var l2 = l1.method_32117("body", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 0.0F, 0.0F));
        var l3 = l2.method_32117("rods", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 8.0F, 0.0F));
        l3.method_32117("rod_1", net.minecraft.class_5606.method_32108().method_32101(0, 17).method_32098(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32091(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F));
        l3.method_32117("rod_2", net.minecraft.class_5606.method_32108().method_32101(0, 17).method_32098(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32091(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F));
        l3.method_32117("rod_3", net.minecraft.class_5606.method_32108().method_32101(0, 17).method_32098(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32091(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F));
        var l4 = l2.method_32117("head", net.minecraft.class_5606.method_32108().method_32101(0, 0).method_32098(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32090(0.0F, 4.0F, 0.0F));
        emptyEyes(l4);
        windParts(l1);
        return net.minecraft.class_5607.method_32110(l0, 32, 32);
    }

    /** 1.20.4 BreezeModel.createEyesLayer(): only the eyes carry cubes (32x32, the breeze_eyes texture) */
    public static net.minecraft.class_5607 method_55234() {
        var l0 = new net.minecraft.class_5609();
        var l1 = l0.method_32111();
        var l2 = l1.method_32117("body", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 0.0F, 0.0F));
        l2.method_32117("rods", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 8.0F, 0.0F));
        var l4 = l2.method_32117("head", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 4.0F, 0.0F));
        l4.method_32117("eyes", net.minecraft.class_5606.method_32108().method_32101(4, 24).method_32098(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new net.minecraft.class_5605(0.0F)).method_32101(7, 16).method_32098(-4.0F, -2.0F, -4.0F, 8.0F, 1.0F, 1.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32090(0.0F, 0.0F, 0.0F));
        windParts(l1);
        return net.minecraft.class_5607.method_32110(l0, 32, 32);
    }

    /** 1.20.4 BreezeModel.createWindLayer(): only the three wind rings carry cubes (128x128, the breeze_wind texture); empty eyes as in {@link #method_55233} */
    public static net.minecraft.class_5607 method_55235() {
        var l0 = new net.minecraft.class_5609();
        var l1 = l0.method_32111();
        var l2 = l1.method_32117("body", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 0.0F, 0.0F));
        l2.method_32117("rods", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 8.0F, 0.0F));
        var l3 = l2.method_32117("head", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 4.0F, 0.0F));
        emptyEyes(l3);
        var l4 = l1.method_32117("wind_body", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 0.0F, 0.0F));
        var l5 = l4.method_32117("wind_bottom", net.minecraft.class_5606.method_32108().method_32101(1, 83).method_32098(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32090(0.0F, 24.0F, 0.0F));
        var l6 = l5.method_32117("wind_mid", net.minecraft.class_5606.method_32108().method_32101(74, 28).method_32098(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new net.minecraft.class_5605(0.0F)).method_32101(78, 32).method_32098(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new net.minecraft.class_5605(0.0F)).method_32101(49, 71).method_32098(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32090(0.0F, -7.0F, 0.0F));
        l6.method_32117("wind_top", net.minecraft.class_5606.method_32108().method_32101(0, 0).method_32098(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new net.minecraft.class_5605(0.0F)).method_32101(6, 6).method_32098(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new net.minecraft.class_5605(0.0F)).method_32101(105, 57).method_32098(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new net.minecraft.class_5605(0.0F)), net.minecraft.class_5603.method_32090(0.0F, -6.0F, 0.0F));
        return net.minecraft.class_5607.method_32110(l0, 128, 128);
    }

    /** the empty wind parts of 1.20.4's body and eyes layers */
    private static void windParts(net.minecraft.class_5610 root) {
        var body = root.method_32117("wind_body", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 0.0F, 0.0F));
        var bottom = body.method_32117("wind_bottom", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 24.0F, 0.0F));
        var mid = bottom.method_32117("wind_mid", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, -7.0F, 0.0F));
        mid.method_32117("wind_top", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, -6.0F, 0.0F));
    }

    /** the part 1.20.5's BreezeModel constructor requires under the head, without cubes */
    private static void emptyEyes(net.minecraft.class_5610 head) {
        head.method_32117("eyes", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.method_32090(0.0F, 0.0F, 0.0F));
    }

    /** 1.20.4's registered definitions of its two breeze layers that 1.20.5 has no location for */
    public static java.util.Map<net.minecraft.class_5601, net.minecraft.class_5607> legacyLayers() {
        java.util.Map<net.minecraft.class_5601, net.minecraft.class_5607> out = new java.util.LinkedHashMap<>();
        out.put(BREEZE_WIND, method_55235());
        out.put(BREEZE_EYES, method_55234());
        return out;
    }

    // ------------------------------------------------------------------ breeze render layers

    /**
     * A breeze eyes/wind layer built with 1.20.4's constructor (parent, modelSet, texture): it draws with its
     * own model baked from 1.20.4's layer and its own texture, as 1.20.4's layer did (the layer mixins).
     * Implemented by mixin/CbClientBreezeEyesLayerMixin and CbClientBreezeWindLayerMixin.
     */
    public interface LegacyBreezeLayer {
        net.minecraft.class_8973<net.minecraft.class_8949> cb$legacyModel();

        net.minecraft.class_2960 cb$legacyTexture();

        void cb$setLegacy(net.minecraft.class_8973<net.minecraft.class_8949> model, net.minecraft.class_2960 texture);
    }

    /** after 1.20.4 BreezeEyesLayer(parent, modelSet, texture): its model from ModelLayers.BREEZE_EYES and its texture */
    public static void cbBreezeEyesBuilt(net.minecraft.class_8988 layer, net.minecraft.class_5599 models, net.minecraft.class_2960 texture) {
        ((LegacyBreezeLayer) layer).cb$setLegacy(new net.minecraft.class_8973<>(models.method_32072(BREEZE_EYES)), texture);
    }

    /** after 1.20.4 BreezeWindLayer(parent, modelSet, texture): its model from ModelLayers.BREEZE_WIND and its texture */
    public static void cbBreezeWindBuilt(net.minecraft.class_8989 layer, net.minecraft.class_5599 models, net.minecraft.class_2960 texture) {
        ((LegacyBreezeLayer) layer).cb$setLegacy(new net.minecraft.class_8973<>(models.method_32072(BREEZE_WIND)), texture);
    }

    // ------------------------------------------------------------------ stray clothing

    /** 1.20.4 SkeletonClothingLayer(parent, modelSet) always took ModelLayers.STRAY_OUTER_LAYER */
    public static net.minecraft.class_5601 strayOuterLayer() {
        return net.minecraft.class_5602.field_27663;
    }

    /** ... and textures/entity/skeleton/stray_overlay.png */
    public static net.minecraft.class_2960 strayOverlay() {
        return new net.minecraft.class_2960("textures/entity/skeleton/stray_overlay.png");
    }
}
