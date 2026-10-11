package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.DebugRender */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderDebugRender {
    void beforeDebugRender(CbWorldRenderContext context);
}
