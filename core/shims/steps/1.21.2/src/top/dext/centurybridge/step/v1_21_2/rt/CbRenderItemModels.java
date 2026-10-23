package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.1 ItemModelShaper state kept on 1.21.2's (CbRenderItemShaperMixin). */
@Environment(EnvType.CLIENT)
public final class CbRenderItemModels {

    /** what the mixin keeps */
    public interface Shaper {
        Int2ObjectMap<net.minecraft.class_1091> cb$shapes();
    }

    private CbRenderItemModels() {
    }

    /** 1.21.1 ItemModelShaper.shapes (instanceFieldGetters): the item id -> registered model location map, live */
    public static Int2ObjectMap<net.minecraft.class_1091> field_4129(net.minecraft.class_763 shaper) {
        return ((Shaper) shaper).cb$shapes();
    }
}
