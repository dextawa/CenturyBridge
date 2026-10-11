package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.AfterSetup */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderAfterSetup {
    void afterSetup(CbWorldRenderContext context);
}
