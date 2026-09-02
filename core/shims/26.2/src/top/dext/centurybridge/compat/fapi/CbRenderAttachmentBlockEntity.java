package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.blockgetter.v2.RenderDataBlockEntity;

/** was rendering-data-attachment-v1's RenderAttachmentBlockEntity: v2 renamed the getter; derived here so the block entity is a real 26.2 render-data provider */
public interface CbRenderAttachmentBlockEntity extends RenderDataBlockEntity {

    Object getRenderAttachmentData();

    @Override
    default Object getRenderData() {
        return getRenderAttachmentData();
    }
}
