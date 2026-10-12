package top.dext.centurybridge.step.v1_20_4.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

/**
 * 1.20.4's Fabric API gave FluidRenderHandlerRegistry an abstract getOverride(Fluid): "a
 * FluidRenderHandler for a given Fluid, if it is not the default implementation" (javadoc). Fabric's own
 * registry answers the handler a mod registered for the fluid and null where only its built-in handler
 * (the water and lava handlers it registers itself) renders it. A registry written against 1.20.3 has
 * only get(fluid); the method synthesized for it answers what get answers, except where that is the
 * default implementation -- the handler Fabric's registry gives the fluid when nothing overrides it there
 * -- which is null, as the contract says. No Fabric API default exists for the method (it is abstract),
 * so the javadoc's contract is what is implemented.
 */
@Environment(EnvType.CLIENT)
public final class CbFluidRenderRegistries {

    private CbFluidRenderRegistries() {
    }

    public static FluidRenderHandler getOverride(FluidRenderHandlerRegistry registry, net.minecraft.class_3611 fluid) {
        FluidRenderHandler handler = registry.get(fluid);
        if (handler == null) {
            return null;
        }
        // the default implementation: what Fabric's registry renders the fluid with when nothing overrides it there
        FluidRenderHandlerRegistry fabric = FluidRenderHandlerRegistry.INSTANCE;
        boolean isDefault = fabric.getOverride(fluid) == null && handler == fabric.get(fluid);
        return isDefault ? null : handler;
    }
}
