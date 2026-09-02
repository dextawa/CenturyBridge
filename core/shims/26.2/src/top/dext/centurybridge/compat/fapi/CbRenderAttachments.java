package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.blockgetter.v2.FabricBlockGetter;
import net.minecraft.core.BlockPos;

/** rendering-data-attachment-v1's RenderAttachedBlockView.getBlockEntityRenderAttachment(pos), on the 26.2 interface-injected FabricBlockGetter (instanceRedirect: receiver first) */
public final class CbRenderAttachments {

    public static Object getBlockEntityRenderAttachment(FabricBlockGetter self, BlockPos pos) {
        return self.getBlockEntityRenderData(pos);
    }

    private CbRenderAttachments() {
    }
}
