package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import top.dext.centurybridge.step.v26_1.mixin.CbWallItemFeatureRendererAccess;

/**
 * 1.21.11's ItemRenderer (net/minecraft/class_918), a holder of static item-drawing helpers by then, which
 * 26.1 moved into ItemFeatureRenderer (the glint textures and constants, the foil buffers; drawing a list of
 * quads is ItemFeatureRenderer's renderItem for a submitted item). Minecraft.getItemRenderer() answers the
 * one instance (CbWallMinecraftMixin). Member names are 1.21.11's intermediary names.
 */
@Environment(EnvType.CLIENT)
public class ItemRenderer {

    /** ENCHANTED_GLINT_ARMOR */
    public static final Identifier field_43086 = ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR;
    /** ENCHANTED_GLINT_ITEM */
    public static final Identifier field_43087 = ItemFeatureRenderer.ENCHANTED_GLINT_ITEM;
    /** SPECIAL_FOIL_UI_SCALE, SPECIAL_FOIL_FIRST_PERSON_SCALE, SPECIAL_FOIL_TEXTURE_SCALE, NO_TINT (1.21.11 values) */
    public static final float field_60154 = 0.5F;
    public static final float field_60155 = 0.75F;
    public static final float field_60156 = 0.0078125F;
    public static final int field_55295 = -1;

    /** the game's instance (Minecraft.getItemRenderer()) */
    public static final ItemRenderer CB$INSTANCE = new ItemRenderer();

    public ItemRenderer() {
    }

    /**
     * renderItem(context, poseStack, buffers, light, overlay, tintLayers, quads, renderType, foilType): every
     * quad into the one render type's buffer (with the foil layer it asks for), coloured by its tint layer
     * when tinted -- 1.21.11's body on 26.1's quad instance (ItemFeatureRenderer draws a submitted item the same
     * way, each quad in its own material's render type).
     */
    public static void method_62476(ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers, int light,
            int overlay, int[] tintLayers, List<BakedQuad> quads, RenderType renderType, ItemStackRenderState.FoilType foilType) {
        VertexConsumer buffer;
        if (foilType == ItemStackRenderState.FoilType.SPECIAL) {
            PoseStack.Pose decal = CbWallItemFeatureRendererAccess.cb$computeFoilDecalPose(context, poseStack.last());
            buffer = CbWallItemFeatureRendererAccess.cb$getFoilBuffer(buffers, renderType, decal);
        } else {
            buffer = ItemFeatureRenderer.getFoilBuffer(buffers, renderType, true, foilType != ItemStackRenderState.FoilType.NONE);
        }
        QuadInstance instance = new QuadInstance();
        instance.setLightCoords(light);
        instance.setOverlayCoords(overlay);
        PoseStack.Pose pose = poseStack.last();
        for (BakedQuad quad : quads) {
            instance.setColor(CbWallItemFeatureRendererAccess.cb$getLayerColorSafe(tintLayers, quad.materialInfo()));
            buffer.putBakedQuad(pose, quad, instance);
        }
    }

    /** getFoilBuffer(buffers, renderType, sheeted, hasFoil) */
    public static VertexConsumer method_23181(MultiBufferSource buffers, RenderType renderType, boolean sheeted, boolean hasFoil) {
        return ItemFeatureRenderer.getFoilBuffer(buffers, renderType, sheeted, hasFoil);
    }

    /** getFoilRenderTypes(renderType, sheeted, hasFoil): the render type, then its glint when it has foil */
    public static List<RenderType> method_72988(RenderType renderType, boolean sheeted, boolean hasFoil) {
        return hasFoil ? List.of(renderType, ItemFeatureRenderer.getFoilRenderType(renderType, sheeted)) : List.of(renderType);
    }
}
