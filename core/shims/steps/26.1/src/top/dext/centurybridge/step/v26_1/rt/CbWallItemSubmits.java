package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Item quads and their render type. 1.21.11 submitted an item layer's quads under one render type
 * (OrderedSubmitNodeCollector.submitItem(.., quads, renderType, foil), the layer's setRenderType, the ItemSubmit
 * record); 26.1 takes each quad's render type from the quad (BakedQuad.MaterialInfo.itemRenderType). A 1.21.11
 * render type is given to the quads -- copies whose material names it, the rest of the material kept -- and a
 * 1.21.11 collector implementation is handed 26.1's quads one run of a single render type at a time.
 */
@Environment(EnvType.CLIENT)
public final class CbWallItemSubmits {

    private CbWallItemSubmits() {
    }

    /** the old shape of submitItem, which a 1.21.11 collector implementation declares (the wall renames it) */
    public interface LegacySubmitItem {
        void submitItem(PoseStack pose, ItemDisplayContext displayContext, int light, int overlay, int outline, int[] tints,
                        List<BakedQuad> quads, RenderType renderType, ItemStackRenderState.FoilType foil);
    }

    /** the quads under the render type: each quad whose material names another is copied with it */
    public static List<BakedQuad> retype(List<BakedQuad> quads, RenderType renderType) {
        if (renderType == null) {
            return quads;
        }
        List<BakedQuad> out = null;
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad q = quads.get(i);
            BakedQuad.MaterialInfo m = q.materialInfo();
            if (m.itemRenderType() != renderType) {
                if (out == null) {
                    out = new ArrayList<>(quads.subList(0, i));
                }
                q = new BakedQuad(q.position0(), q.position1(), q.position2(), q.position3(), q.packedUV0(), q.packedUV1(),
                    q.packedUV2(), q.packedUV3(), q.direction(),
                    new BakedQuad.MaterialInfo(m.sprite(), m.layer(), renderType, m.tintIndex(), m.shade(), m.lightEmission()));
            }
            if (out != null) {
                out.add(q);
            }
        }
        return out != null ? out : quads;
    }

    /** a 1.21.11 call of submitItem(pose, context, light, overlay, outline, tints, quads, renderType, foil) */
    public static void submitItem(OrderedSubmitNodeCollector collector, PoseStack pose, ItemDisplayContext displayContext, int light, int overlay,
                                  int outline, int[] tints, List<BakedQuad> quads, RenderType renderType, ItemStackRenderState.FoilType foil) {
        collector.submitItem(pose, displayContext, light, overlay, outline, tints, retype(quads, renderType), foil);
    }

    /**
     * super.submitItem(.., renderType, foil) in a 1.21.11 subclass of SubmitNodeStorage / SubmitNodeCollection: 26.1's
     * shape, which the subclass's own synthesized 26.1 shape passes to its superclass while the subclass's old method
     * runs (the hook's guard)
     */
    public static void superSubmitItem(SubmitNodeStorage self, PoseStack pose, ItemDisplayContext displayContext, int light, int overlay,
                                       int outline, int[] tints, List<BakedQuad> quads, RenderType renderType, ItemStackRenderState.FoilType foil) {
        self.submitItem(pose, displayContext, light, overlay, outline, tints, retype(quads, renderType), foil);
    }

    /** the same for SubmitNodeCollection */
    public static void superSubmitItem(net.minecraft.client.renderer.SubmitNodeCollection self, PoseStack pose, ItemDisplayContext displayContext,
                                       int light, int overlay, int outline, int[] tints, List<BakedQuad> quads, RenderType renderType,
                                       ItemStackRenderState.FoilType foil) {
        self.submitItem(pose, displayContext, light, overlay, outline, tints, retype(quads, renderType), foil);
    }

    /** 26.1's submitItem on a 1.21.11 implementation: its old method once per run of quads sharing a render type */
    public static void submitItem(LegacySubmitItem collector, PoseStack pose, ItemDisplayContext displayContext, int light, int overlay,
                                  int outline, int[] tints, List<BakedQuad> quads, ItemStackRenderState.FoilType foil) {
        int start = 0;
        while (start < quads.size()) {
            RenderType type = quads.get(start).materialInfo().itemRenderType();
            int end = start + 1;
            while (end < quads.size() && quads.get(end).materialInfo().itemRenderType() == type) {
                end++;
            }
            collector.submitItem(pose, displayContext, light, overlay, outline, tints, quads.subList(start, end), type, foil);
            start = end;
        }
    }

    /** 1.21.11 new SubmitNodeStorage.ItemSubmit(pose, context, light, overlay, outline, tints, quads, renderType, foil) */
    public static SubmitNodeStorage.ItemSubmit itemSubmit(PoseStack.Pose pose, ItemDisplayContext displayContext, int light, int overlay, int outline,
                                                          int[] tints, List<BakedQuad> quads, RenderType renderType,
                                                          ItemStackRenderState.FoilType foil) {
        return new SubmitNodeStorage.ItemSubmit(pose, displayContext, light, overlay, outline, tints, retype(quads, renderType), foil);
    }

    /** ItemSubmit.renderType(): the render type its quads are drawn with (the first quad's; none without quads) */
    public static RenderType renderType(SubmitNodeStorage.ItemSubmit submit) {
        List<BakedQuad> quads = submit.quads();
        return quads.isEmpty() ? null : quads.get(0).materialInfo().itemRenderType();
    }

    /** 1.21.11 ItemStackRenderState.pickParticleIcon(random): the sprite of 26.1's particle material */
    public static TextureAtlasSprite pickParticleIcon(ItemStackRenderState state, RandomSource random) {
        net.minecraft.client.resources.model.sprite.Material.Baked m = state.pickParticleMaterial(random);
        return m == null ? null : m.sprite();
    }

    /** a 1.21.11 subclass's pickParticleIcon answer as 26.1's particle material (not forced translucent) */
    public static net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial(TextureAtlasSprite sprite) {
        return sprite == null ? null : new net.minecraft.client.resources.model.sprite.Material.Baked(sprite, false);
    }
}
