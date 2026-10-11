package top.dext.centurybridge.compat.fapi;

import net.minecraft.world.phys.HitResult;

/** was WorldRenderEvents.BeforeBlockOutline (false = skip the vanilla outline) */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbWorldRenderBeforeBlockOutline {
    boolean beforeBlockOutline(CbWorldRenderContext context, HitResult hitResult);
}
