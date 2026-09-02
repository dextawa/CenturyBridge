package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.DebugRender */
@FunctionalInterface
public interface CbWorldRenderDebugRender {
    void beforeDebugRender(CbWorldRenderContext context);
}
