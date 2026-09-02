package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.End */
@FunctionalInterface
public interface CbWorldRenderEnd {
    void onEnd(CbWorldRenderContext context);
}
