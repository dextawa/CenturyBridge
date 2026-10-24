package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Arguments 1.21.2's renderer / layer constructors take that 1.21.1's did not, or took in another form
 * (superCtorReshapes, for a mod's new site and a mod subclass's super call alike):
 * - an ItemRenderer where 1.21.1 took the ItemInHandRenderer that drew the items: that renderer's own ItemRenderer;
 * - an EquipmentLayerRenderer (1.21.2's armour drawing): built as 1.21.2's EntityRendererProvider.Context builds it,
 *   from the game's equipment models and the armour-trim atlas of the model manager -- the given ModelManager where
 *   1.21.1 took one (HumanoidArmorLayer), else the game's;
 * - the game's EntityModelSet / equipment models / map renderer where 1.21.2 asks for them and 1.21.1's class took
 *   them from the game itself (CapeLayer, Deadmau5EarsLayer, the renderer context, EntityRenderDispatcher).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderCtors {

    /** ItemInHandRenderer's ItemRenderer (CbRenderItemInHandAccess) */
    public interface ItemInHand {
        net.minecraft.class_918 cb$itemRenderer();
    }

    private CbRenderCtors() {
    }

    public static net.minecraft.class_918 itemRenderer(net.minecraft.class_759 itemInHandRenderer) {
        return ((ItemInHand) itemInHandRenderer).cb$itemRenderer();
    }

    public static net.minecraft.class_10197 equipmentRenderer(net.minecraft.class_1092 modelManager) {
        return new net.minecraft.class_10197(net.minecraft.class_310.method_1551().method_64036(),
            modelManager.method_24153(net.minecraft.class_4722.field_42071));
    }

    public static net.minecraft.class_10197 equipmentRenderer() {
        return equipmentRenderer(net.minecraft.class_310.method_1551().method_1554());
    }

    public static net.minecraft.class_5599 modelSet() {
        return net.minecraft.class_310.method_1551().method_31974();
    }

    public static net.minecraft.class_10201 equipmentAssets() {
        return net.minecraft.class_310.method_1551().method_64036();
    }

    public static net.minecraft.class_330 mapRenderer() {
        return net.minecraft.class_310.method_1551().method_61965();
    }

    /**
     * an EntityRendererProvider.Context where 1.21.2's layer constructor asks for one and 1.21.1's took none (its layers
     * reached the game's model set themselves): the context 1.21.2's EntityRenderDispatcher builds on reload, from the same
     * game objects (its item renderer, map renderer, block renderer, resource manager, model set, equipment assets, font)
     */
    public static net.minecraft.class_5617.class_5618 rendererContext() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        return new net.minecraft.class_5617.class_5618(mc.method_1561(), mc.method_1480(), mapRenderer(), mc.method_1541(), mc.method_1478(), modelSet(),
            equipmentAssets(), mc.field_1772);
    }

    /**
     * new BakedGlyph.Effect(x0, y0, x1, y1, depth, r, g, b, a): 1.21.2's record keeps the colour packed. 1.21.1 drew the
     * effect with VertexConsumer.setColor(r, g, b, a), which stored (int) (channel * 255) of each as a byte: packed the
     * same way.
     */
    public static net.minecraft.class_382.class_328 glyphEffect(float x0, float y0, float x1, float y1, float depth, float r, float g, float b,
                                                               float a) {
        return new net.minecraft.class_382.class_328(x0, y0, x1, y1, depth, glyphColor(r, g, b, a));
    }

    /**
     * 1.21.1 EffectInstance's four colour floats as 1.21.2's ARGB colour: 1.21.1 handed them to VertexConsumer.setColor
     * (floats), which packs each as (int) (f * 255) -- the same bytes
     */
    public static int glyphColor(float r, float g, float b, float a) {
        return ((int) (a * 255.0F) & 0xFF) << 24 | ((int) (r * 255.0F) & 0xFF) << 16 | ((int) (g * 255.0F) & 0xFF) << 8
            | (int) (b * 255.0F) & 0xFF;
    }

    /** 1.21.1 EffectInstance's protected fields (instanceFieldGetters), read off 1.21.2's record */
    public static float effectX0(net.minecraft.class_382.class_328 e) {
        return e.comp_3307();
    }

    public static float effectY0(net.minecraft.class_382.class_328 e) {
        return e.comp_3308();
    }

    public static float effectX1(net.minecraft.class_382.class_328 e) {
        return e.comp_3309();
    }

    public static float effectY1(net.minecraft.class_382.class_328 e) {
        return e.comp_3310();
    }

    public static float effectDepth(net.minecraft.class_382.class_328 e) {
        return e.comp_3311();
    }

    public static float effectR(net.minecraft.class_382.class_328 e) {
        return (e.comp_3312() >> 16 & 0xFF) / 255.0F;
    }

    public static float effectG(net.minecraft.class_382.class_328 e) {
        return (e.comp_3312() >> 8 & 0xFF) / 255.0F;
    }

    public static float effectB(net.minecraft.class_382.class_328 e) {
        return (e.comp_3312() & 0xFF) / 255.0F;
    }

    public static float effectA(net.minecraft.class_382.class_328 e) {
        return (e.comp_3312() >> 24 & 0xFF) / 255.0F;
    }

    /**
     * the light emission 1.21.2's BakedQuad / BlockElement / FaceBakery.bakeQuad take: 1.21.1's quads and elements had
     * none (no light_emission), which 1.21.2's 0 means
     */
    public static int noLightEmission() {
        return 0;
    }

    /** FaceBakery.bakeQuad(from, to, face, sprite, direction, modelState, rotation, shade): 1.21.2's with no emission */
    public static net.minecraft.class_777 method_3468(net.minecraft.class_796 bakery, org.joml.Vector3f from, org.joml.Vector3f to,
                                                    net.minecraft.class_783 face, net.minecraft.class_1058 sprite, net.minecraft.class_2350 direction,
                                                    net.minecraft.class_3665 modelState, net.minecraft.class_789 rotation, boolean shade) {
        return bakery.method_3468(from, to, face, sprite, direction, modelState, rotation, shade, 0);
    }

    /**
     * after new PiglinRenderer(context, layer, inner, outer, noRightEar): 1.21.1 hid the model's right ear when asked
     * (method_24875: rightEar.visible = false, the head's "right_ear" part); the same part of the adult and baby models
     */
    public static void piglinEars(net.minecraft.class_4842 renderer, boolean noRightEar) {
        if (noRightEar) {
            for (net.minecraft.class_583<?> model : java.util.List.of(renderer.field_53177, renderer.field_53178)) {
                model.method_62104("right_ear").ifPresent(part -> part.field_3665 = false);
            }
        }
    }

    /**
     * after new SkeletonRenderer(context, body, inner, outer): 1.21.1's SkeletonRenderer took the layers of its model and
     * armour; 1.21.2's builds the skeleton's own. The renderer gets 1.21.1's: the model from the body layer and the armour
     * layer from the inner/outer layers, as 1.21.2's AbstractSkeletonRenderer(context, body, inner, outer) builds them
     */
    public static void skeletonLayers(net.minecraft.class_946 renderer, net.minecraft.class_5617.class_5618 context,
                                      net.minecraft.class_5601 body, net.minecraft.class_5601 inner, net.minecraft.class_5601 outer) {
        skeletonModel(renderer, context, inner, outer, new net.minecraft.class_606<>(context.method_32167(body)));
    }

    /** after new SkeletonRenderer(context, inner, outer, model): the model and armour layers 1.21.1 was handed */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void skeletonModel(net.minecraft.class_946 renderer, net.minecraft.class_5617.class_5618 context,
                                     net.minecraft.class_5601 inner, net.minecraft.class_5601 outer, net.minecraft.class_606 model) {
        net.minecraft.class_9990 ageable = renderer;
        ageable.field_53177 = model;
        ageable.field_53178 = model;
        ((net.minecraft.class_922) renderer).field_4737 = model;
        java.util.List layers = renderer.field_4738;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i) instanceof net.minecraft.class_970) {
                layers.set(i, new net.minecraft.class_970(renderer, new net.minecraft.class_606<>(context.method_32167(inner)),
                    new net.minecraft.class_606<>(context.method_32167(outer)), context.method_64072()));
            }
        }
    }

    /**
     * after new CamelRenderer(context, layer): 1.21.1's took the model layer, 1.21.2's builds the camel's own; the
     * renderer gets the layer's model, and the baby model from it under CamelModel.BABY_TRANSFORMER (CbRenderBabyLayers)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void camelLayer(net.minecraft.class_7763 renderer, net.minecraft.class_5617.class_5618 context, net.minecraft.class_5601 layer) {
        net.minecraft.class_9990 ageable = renderer;
        ageable.field_53177 = new net.minecraft.class_7751(context.method_32167(layer));
        ageable.field_53178 = new net.minecraft.class_7751(context.method_32167(CbRenderBabyLayers.camelBaby(layer)));
        ((net.minecraft.class_922) renderer).field_4737 = ageable.field_53177;
    }

    /** an equine model as the EntityModel 1.21.2's AbstractHorseRenderer constructor declares (the same object) */
    public static net.minecraft.class_583<?> equineModel(net.minecraft.class_549<?> model) {
        return model;
    }

    /** new RenderSection(dispatcher, index, x, y, z): 1.21.2 takes the SectionPos node of the section holding that block origin */
    public static long sectionNode(int x, int y, int z) {
        return net.minecraft.class_4076.method_18685(net.minecraft.class_4076.method_18675(x), net.minecraft.class_4076.method_18675(y),
            net.minecraft.class_4076.method_18675(z));
    }
}
