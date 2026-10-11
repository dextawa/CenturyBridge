package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.Last */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderLast {
    void onLast(CbWorldRenderContext context);
}
