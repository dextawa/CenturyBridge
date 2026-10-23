package top.dext.centurybridge.step.v1_21_4.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractScrollArea's scroll-bar drag state (private field_39498 "scrolling"): what 1.21.4 updateScrolling answers */
@Mixin(net.minecraft.class_7528.class)
public interface CbGuiScrollAreaAccess {
    @Accessor("field_39498")
    boolean cb$scrolling();
}
