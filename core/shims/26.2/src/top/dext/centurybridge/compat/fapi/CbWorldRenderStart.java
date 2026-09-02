package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.Start */
@FunctionalInterface
public interface CbWorldRenderStart {
    void onStart(CbWorldRenderContext context);
}
