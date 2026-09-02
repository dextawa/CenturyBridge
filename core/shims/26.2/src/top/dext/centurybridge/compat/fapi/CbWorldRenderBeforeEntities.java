package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.BeforeEntities */
@FunctionalInterface
public interface CbWorldRenderBeforeEntities {
    void beforeEntities(CbWorldRenderContext context);
}
