package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * RenderChunkRegion (the chunk section builder's view) is a 1.21.5 RenderAttachedBlockView, as Fabric API 1.21.5's
 * fabric-rendering-data-attachment-v1 made it (its client ChunkRendererRegionMixin); the interface only adds a default
 * method, which 1.21.6's block-view-api-v2 answers for the region.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_853.class)
public abstract class CbRenderAttachedRegionMixin implements top.dext.centurybridge.step.v1_21_6.facade.fapi.RenderAttachedBlockView {
}
