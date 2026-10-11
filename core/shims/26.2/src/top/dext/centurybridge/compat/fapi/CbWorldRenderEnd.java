package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.End */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderEnd {
    void onEnd(CbWorldRenderContext context);
}
