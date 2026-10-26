package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** {@link CbPrioritySplit}'s client-side counterparts */
@Environment(EnvType.CLIENT)
public final class CbPrioritySplitClient {
    private CbPrioritySplitClient() {
    }

    /** CbClientBreezeWindLayerMixin: 1.20.4's getTextureLocation(breeze), called virtually */
    public interface BreezeWindTexture {
        net.minecraft.class_2960 cb$v1_20_5$textureLocation(net.minecraft.class_8949 breeze);
    }

    /** CbClientFontManagerMixin: 1.20.4's font id renames */
    public interface FontRenames {
        java.util.Map<net.minecraft.class_2960, net.minecraft.class_2960> cb$v1_20_5$renames();
    }

    /** CbClientSelectionListMixin: 1.20.4's renderBackground flag */
    public interface ListBackground {
        boolean cb$v1_20_5$renderBackground();
    }
}
