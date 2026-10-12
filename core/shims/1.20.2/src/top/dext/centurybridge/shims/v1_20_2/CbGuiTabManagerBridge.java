package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbTickableTab;

/**
 * TabManager.tickCurrent(): 1.20.1 ticked the current tab (Tab.tick); 1.20.2 dropped the call with the
 * widget ticking it served. The 1.20.1 body. 1.20.1's own caller was CreateWorldScreen.tick, for the
 * vanilla world-creation tabs, whose ticks only advanced their text boxes' cursor blink -- which 1.20.2
 * derives from time -- so nothing in vanilla 1.20.2 needs it; a mod screen ticking its TabManager does.
 */
@Mixin(net.minecraft.class_8088.class)
public abstract class CbGuiTabManagerBridge {

    /** 1.20.1 TabManager.tickCurrent() */
    public void method_48617() {
        net.minecraft.class_8087 tab = ((net.minecraft.class_8088) (Object) this).method_48614();
        if (tab != null) {
            ((CbTickableTab) tab).method_48613();
        }
    }
}
