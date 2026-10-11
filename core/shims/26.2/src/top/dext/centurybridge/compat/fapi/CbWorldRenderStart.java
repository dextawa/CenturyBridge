package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.Start */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderStart {
    void onStart(CbWorldRenderContext context);
}
