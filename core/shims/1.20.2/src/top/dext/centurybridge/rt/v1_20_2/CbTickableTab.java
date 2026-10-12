package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 Tab.tick(), the per-tick hook TabManager.tickCurrent() called on the current tab; 1.20.2
 * dropped both. CbGuiTabBridge makes Tab extend this interface with 1.20.1's empty default, so
 * CbGuiTabManagerBridge reaches a mod tab's override with an ordinary interface call.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbTickableTab {

    void method_48613();
}
