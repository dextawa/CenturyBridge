package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 ItemRenderer / ItemInHandRenderer API on 1.21.2's (staticRedirects, instanceRedirects, fieldGetters, the
 * constructor conversions).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderItems {

    private static final net.minecraft.class_1091 TRIDENT_MODEL = net.minecraft.class_1091.method_61078(net.minecraft.class_2960.method_60656("trident_in_hand"));

    private static final net.minecraft.class_1091 SPYGLASS_MODEL = net.minecraft.class_1091.method_61078(net.minecraft.class_2960.method_60656("spyglass_in_hand"));

    private CbRenderItems() {
    }

    /** 1.21.1 ItemRenderer.TRIDENT_IN_HAND_MODEL (fieldGetters): ModelResourceLocation.inventory("trident_in_hand"), as 1.21.1 built it */
    public static net.minecraft.class_1091 field_40530() {
        return TRIDENT_MODEL;
    }

    /** 1.21.1 ItemRenderer.SPYGLASS_IN_HAND_MODEL (fieldGetters) */
    public static net.minecraft.class_1091 field_40531() {
        return SPYGLASS_MODEL;
    }

    /**
     * 1.21.1 ItemRenderer.getFoilBufferDirect(buffers, type, noEntity, withGlint) (staticRedirects): the type's buffer,
     * with the glint layered over it. 1.21.1 used the "direct" glint types (glint() for noEntity, entityGlintDirect()
     * otherwise); 1.21.2 dropped entityGlintDirect (its rendertype_entity_glint_direct shader is gone) and draws, where
     * 1.21.1 drew it, entityGlint() -- 1.21.2's getFoilBuffer does so for the same items.
     */
    public static net.minecraft.class_4588 method_29711(net.minecraft.class_4597 buffers, net.minecraft.class_1921 type, boolean noEntity, boolean withGlint) {
        if (!withGlint) {
            return buffers.getBuffer(type);
        }
        net.minecraft.class_1921 glint = noEntity ? net.minecraft.class_1921.method_23590() : net.minecraft.class_1921.method_23591();
        return net.minecraft.class_4720.method_24037(buffers.getBuffer(glint), buffers.getBuffer(type));
    }

    /**
     * 1.21.1 EntityRendererProvider.Context.getItemInHandRenderer() (instanceRedirects): the game's ItemInHandRenderer,
     * the one 1.21.1's EntityRenderDispatcher put in every context (1.21.2's GameRenderer keeps it, public final)
     */
    public static net.minecraft.class_759 method_43338(net.minecraft.class_5617.class_5618 context) {
        return net.minecraft.class_310.method_1551().field_1773.field_4012;
    }

    /**
     * An ItemInHandRenderer, where 1.21.1 layers (ItemInHandLayer, CustomHeadLayer) drew items with it, as the
     * ItemRenderer 1.21.2's take: the game's (1.21.1's ItemInHandRenderer drew a layer's items through it)
     */
    public static net.minecraft.class_918 itemRenderer(net.minecraft.class_759 inHand) {
        return net.minecraft.class_310.method_1551().method_1480();
    }
}
