package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.AfterTranslucent */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderAfterTranslucent {
    void afterTranslucent(CbWorldRenderContext context);
}
