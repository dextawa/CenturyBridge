package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.AfterEntities */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderAfterEntities {
    void afterEntities(CbWorldRenderContext context);
}
