package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.BlockOutline (false = skip the vanilla outline) */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderBlockOutline {
    boolean onBlockOutline(CbWorldRenderContext worldRenderContext, CbBlockOutlineContext blockOutlineContext);
}
