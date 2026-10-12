package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.class_2582;
import net.minecraft.class_7891;
import net.minecraft.class_7446;

/**
 * 1.20.6's UpdateOneTwentyOneBannerPatterns: the banner patterns the 1.21 update experiment added, flow and guster.
 * 1.21 made them vanilla and folded them into BannerPatterns; the step's classRenames put a 1.20.6 mod's
 * references here, and bootstrap registers the same two, as 1.21's BannerPatterns registers them.
 */
public interface class_9367 {

    static void method_58189(class_7891<class_2582> context) {
        class_7446.method_57577(context, class_7446.field_49835);
        class_7446.method_57577(context, class_7446.field_49836);
    }
}
