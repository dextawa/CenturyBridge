package top.dext.centurybridge.step.v26_1_1.rt;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.render.ChunkSectionLayerHelper;
import net.fabricmc.fabric.api.client.renderer.v1.render.FabricOrderedSubmitNodeCollector;
import net.fabricmc.fabric.api.client.renderer.v1.render.FabricSubmitNodeCollection;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * 26.1.1's Fabric renderer API (13.0.0) replaced the one RenderType of a block model submit with a render
 * type function of the quads' chunk layers plus a translucent flag -- in
 * FabricOrderedSubmitNodeCollector.submitBlockModel and in the ExtendedBlockModelSubmit record -- and
 * documents the equivalence: "to render to a single render type like the vanilla submit, pass a render type
 * function that always returns the same value and that render type's RenderType#hasBlending() value as the
 * translucent parameter". 26.1's renderer drew such a submit with its render type, in the pass of its
 * blending (12.0.0's BlockFeatureRendererMixin). So:
 * <ul>
 * <li>a 26.1 call and a 26.1 record ({@link #renderTypeFunction}, {@link #translucent}) pass that function --
 *     a {@link FixedRenderType}, which keeps the render type -- and that flag; a call through an interface
 *     reaches a collector that has no 26.1.1 submitBlockModel of its own with 26.1's default
 *     ({@link #callSubmitBlockModel}): Fabric's 26.1.1 default flattens to one block sheet type;</li>
 * <li>what 26.1 code reads back as one render type ({@link #renderType}): the type a FixedRenderType keeps;
 *     for a submit 26.1.1 made with a function of its own, the type Fabric's 26.1.1 default
 *     submitBlockModel itself flattens the function to for a collector that takes one render type
 *     (ChunkSectionLayerHelper.getRenderType(translucent));</li>
 * <li>a 26.1 class overriding the 26.1 shape gets the 26.1.1 one, which hands it that render type
 *     ({@link #submitBlockModel}); its super call -- 26.1's default, the vanilla submit with the render
 *     type, ignoring the mesh -- keeps meaning that ({@link #superSubmitBlockModel}), where 26.1.1's default
 *     would flatten the type to the block sheet of its blending. The super call names
 *     FabricOrderedSubmitNodeCollector, or the vanilla interface it is injected into
 *     (OrderedSubmitNodeCollector, and SubmitNodeCollector, which extends it); SubmitNodeStorage and
 *     SubmitNodeCollection, the vanilla classes, implement the method themselves (Fabric's mixins, in
 *     both versions), so a super call into them reaches that implementation in the 26.1.1 shape.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbBlockModelSubmits {

    private CbBlockModelSubmits() {
    }

    /** the render type function a 26.1 render type stands for: every chunk layer to that type */
    @Environment(EnvType.CLIENT)
    public record FixedRenderType(RenderType renderType) implements Function<ChunkSectionLayer, RenderType> {
        @Override
        public RenderType apply(ChunkSectionLayer layer) {
            return renderType;
        }
    }

    public static Function<ChunkSectionLayer, RenderType> renderTypeFunction(RenderType renderType) {
        return new FixedRenderType(renderType);
    }

    public static boolean translucent(RenderType renderType) {
        return renderType.hasBlending();
    }

    /** one render type for a 26.1.1 function and flag */
    public static RenderType renderType(Function<ChunkSectionLayer, RenderType> renderTypeFunction, boolean translucent) {
        return renderTypeFunction instanceof FixedRenderType fixed ? fixed.renderType()
            : ChunkSectionLayerHelper.getRenderType(translucent);
    }

    /** 26.1's ExtendedBlockModelSubmit.renderType() */
    public static RenderType renderType(FabricSubmitNodeCollection.ExtendedBlockModelSubmit submit) {
        return renderType(submit.renderTypeFunction(), submit.translucent());
    }

    /** the 26.1.1 shape synthesized on a 26.1 implementation: its own 26.1 method, with one render type */
    public static void submitBlockModel(CbLegacyBlockModelSubmitter self, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                        Function<ChunkSectionLayer, RenderType> renderTypeFunction, boolean translucent,
                                        List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts, Mesh mesh,
                                        int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        self.submitBlockModel(poseStack, renderType(renderTypeFunction, translucent), parts, mesh, tintLayers,
            lightCoords, overlayCoords, outlineColor);
    }

    /**
     * a 26.1 call through an interface (the call reshaped to the 26.1.1 shape, the render type made its
     * {@link FixedRenderType}): a collector with a 26.1.1 submitBlockModel of its own -- Fabric's
     * SubmitNodeStorage / SubmitNodeCollection, a 26.1 class through its bridge, a 26.1.1 class -- takes the
     * function and flag; one that inherits Fabric's default gets 26.1's default, the vanilla submit with the
     * render type, where 26.1.1's would submit the block sheet type of its blending instead
     */
    public static void callSubmitBlockModel(FabricOrderedSubmitNodeCollector self, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                            Function<ChunkSectionLayer, RenderType> renderTypeFunction, boolean translucent,
                                            List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts, Mesh mesh,
                                            int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        if (renderTypeFunction instanceof FixedRenderType fixed && !OWN_SUBMIT.get(self.getClass())) {
            ((OrderedSubmitNodeCollector) self).submitBlockModel(poseStack, fixed.renderType(), parts, tintLayers,
                lightCoords, overlayCoords, outlineColor);
            return;
        }
        self.submitBlockModel(poseStack, renderTypeFunction, translucent, parts, mesh, tintLayers, lightCoords,
            overlayCoords, outlineColor);
    }

    /** whether a collector class has a 26.1.1 submitBlockModel of its own rather than Fabric's interface default */
    private static final ClassValue<Boolean> OWN_SUBMIT = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                return type.getMethod("submitBlockModel", com.mojang.blaze3d.vertex.PoseStack.class, Function.class,
                    boolean.class, List.class, Mesh.class, int[].class, int.class, int.class, int.class)
                    .getDeclaringClass() != FabricOrderedSubmitNodeCollector.class;
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(type + " is a FabricOrderedSubmitNodeCollector without submitBlockModel", e);
            }
        }
    };

    /** a 26.1 implementation's super.submitBlockModel(..): 26.1's default, the vanilla submit with the render type */
    public static void superSubmitBlockModel(FabricOrderedSubmitNodeCollector self, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                             Function<ChunkSectionLayer, RenderType> renderTypeFunction, boolean translucent,
                                             List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts, Mesh mesh,
                                             int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        ((OrderedSubmitNodeCollector) self).submitBlockModel(poseStack, renderType(renderTypeFunction, translucent), parts,
            tintLayers, lightCoords, overlayCoords, outlineColor);
    }

    /**
     * the same super call where the 26.1 class implements a vanilla collector interface the collector API is injected
     * into (OrderedSubmitNodeCollector.super / SubmitNodeCollector.super): it reached the same 26.1 default
     */
    public static void superSubmitBlockModel(OrderedSubmitNodeCollector self, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                             Function<ChunkSectionLayer, RenderType> renderTypeFunction, boolean translucent,
                                             List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts, Mesh mesh,
                                             int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        self.submitBlockModel(poseStack, renderType(renderTypeFunction, translucent), parts, tintLayers, lightCoords,
            overlayCoords, outlineColor);
    }

    /** see {@link #superSubmitBlockModel(OrderedSubmitNodeCollector, com.mojang.blaze3d.vertex.PoseStack, Function, boolean, List, Mesh, int[], int, int, int)} */
    public static void superSubmitBlockModel(net.minecraft.client.renderer.SubmitNodeCollector self, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                             Function<ChunkSectionLayer, RenderType> renderTypeFunction, boolean translucent,
                                             List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts, Mesh mesh,
                                             int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        self.submitBlockModel(poseStack, renderType(renderTypeFunction, translucent), parts, tintLayers, lightCoords,
            overlayCoords, outlineColor);
    }
}
