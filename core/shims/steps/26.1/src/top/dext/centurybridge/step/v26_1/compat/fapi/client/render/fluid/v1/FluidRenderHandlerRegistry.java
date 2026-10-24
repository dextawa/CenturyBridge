package top.dext.centurybridge.step.v26_1.compat.fapi.client.render.fluid.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import top.dext.centurybridge.step.v26_1.rt.CbFapiFluids;

/**
 * 1.21.11's net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry, which 26.1's Fabric API
 * replaced with the static FluidRenderingRegistry (registration now takes a FluidModel). INSTANCE is
 * rt/CbFapiFluids' registry, 1.21.11's FluidRenderHandlerRegistryImpl on 26.1; the default body is 1.21.11's.
 */
@Environment(EnvType.CLIENT)
public interface FluidRenderHandlerRegistry {
    FluidRenderHandlerRegistry INSTANCE = CbFapiFluids.registry();

    FluidRenderHandler get(Fluid fluid);

    FluidRenderHandler getOverride(Fluid fluid);

    void register(Fluid fluid, FluidRenderHandler renderer);

    default void register(Fluid still, Fluid flow, FluidRenderHandler renderer) {
        register(still, renderer);
        register(flow, renderer);
    }

    void setBlockTransparency(Block block, boolean transparent);

    boolean isBlockTransparent(Block block);
}
