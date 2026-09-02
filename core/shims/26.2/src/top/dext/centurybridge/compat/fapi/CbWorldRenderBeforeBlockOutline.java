package top.dext.centurybridge.compat.fapi;

import net.minecraft.world.phys.HitResult;

/** was WorldRenderEvents.BeforeBlockOutline (false = skip the vanilla outline) */
@FunctionalInterface
public interface CbWorldRenderBeforeBlockOutline {
    boolean beforeBlockOutline(CbWorldRenderContext context, HitResult hitResult);
}
