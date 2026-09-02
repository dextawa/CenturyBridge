package top.dext.centurybridge.compat.fapi;

/** was WorldRenderEvents.BlockOutline (false = skip the vanilla outline) */
@FunctionalInterface
public interface CbWorldRenderBlockOutline {
    boolean onBlockOutline(CbWorldRenderContext worldRenderContext, CbBlockOutlineContext blockOutlineContext);
}
