package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluids;

/**
 * 1.21.11 LiquidBlockRenderer sprite fields lavaStill / lavaFlowing / waterStill / waterFlowing / waterOverlay
 * (class_775.field_64568..64571 / field_4164, opened by access wideners): 1.21.11's constructor took them from the
 * material set for ModelBakery.LAVA_STILL / LAVA_FLOW / WATER_STILL / WATER_FLOW / WATER_OVERLAY (block/lava_still,
 * block/lava_flow, block/water_still, block/water_flow, block/water_overlay). 26.1 (javap) bakes the same five
 * sprite ids into FluidStateModelSet (WATER_MODEL still/flow/overlay, LAVA_MODEL still/flow), which the renderer
 * holds as fluidModels; each read answers that model's baked sprite.
 */
@Environment(EnvType.CLIENT)
public final class CbWallFluidSprites {
    private CbWallFluidSprites() {
    }

    private static FluidModel lava(FluidRenderer r) {
        return r.fluidModels.get(Fluids.LAVA.defaultFluidState());
    }

    private static FluidModel water(FluidRenderer r) {
        return r.fluidModels.get(Fluids.WATER.defaultFluidState());
    }

    public static TextureAtlasSprite lavaStill(FluidRenderer r) {
        return lava(r).stillMaterial().sprite();
    }

    public static TextureAtlasSprite lavaFlowing(FluidRenderer r) {
        return lava(r).flowingMaterial().sprite();
    }

    public static TextureAtlasSprite waterStill(FluidRenderer r) {
        return water(r).stillMaterial().sprite();
    }

    public static TextureAtlasSprite waterFlowing(FluidRenderer r) {
        return water(r).flowingMaterial().sprite();
    }

    public static TextureAtlasSprite waterOverlay(FluidRenderer r) {
        return water(r).overlayMaterial().sprite();
    }
}
