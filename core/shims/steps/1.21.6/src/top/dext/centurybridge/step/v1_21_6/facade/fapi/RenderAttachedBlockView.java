package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's fabric-rendering-data-attachment-v1 RenderAttachedBlockView, a module 1.21.6's Fabric API
 * removed. In 1.21.5 every BlockAndTintGetter was one (the module's interface mixin) and its
 * getBlockEntityRenderAttachment was fabric-block-view-api-v2's getBlockEntityRenderData (javap); block-view-api-v2 is
 * in 1.21.6 with the same method, and CbRenderAttachedViewMixin / CbRenderAttachedRegionMixin give them this interface again.
 */
public interface RenderAttachedBlockView extends net.minecraft.class_1920 {

    default Object getBlockEntityRenderAttachment(net.minecraft.class_2338 pos) {
        return ((net.fabricmc.fabric.api.blockview.v2.FabricBlockView) this).getBlockEntityRenderData(pos);
    }
}
