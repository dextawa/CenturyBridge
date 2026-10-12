package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbTickableTab;

/** Tab.tick(): 1.20.1's empty default, back on the interface (see CbTickableTab). */
@Mixin(net.minecraft.class_8087.class)
public interface CbGuiTabBridge extends CbTickableTab {

    /** 1.20.1 Tab.tick() */
    @Override
    default void method_48613() {
    }
}
