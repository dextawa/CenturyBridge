package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.BeforeEntities */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderBeforeEntities {
    void beforeEntities(CbWorldRenderContext context);
}
