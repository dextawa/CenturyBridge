package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.AfterSetup */
@FunctionalInterface
public interface CbWorldRenderAfterSetup {
    void afterSetup(CbWorldRenderContext context);
}
