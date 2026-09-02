package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.AfterTranslucent */
@FunctionalInterface
public interface CbWorldRenderAfterTranslucent {
    void afterTranslucent(CbWorldRenderContext context);
}
