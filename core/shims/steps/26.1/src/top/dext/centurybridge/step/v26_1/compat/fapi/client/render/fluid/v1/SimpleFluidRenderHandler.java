package top.dext.centurybridge.step.v26_1.compat.fapi.client.render.fluid.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.material.FluidState;

import java.util.Objects;

/**
 * 1.21.11's net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler, which 26.1's Fabric API
 * removed (a fluid's textures are now its FluidModel's materials): fixed textures looked up in the block atlas
 * at each reload and a fixed tint, member for member 1.21.11's.
 */
@Environment(EnvType.CLIENT)
public class SimpleFluidRenderHandler implements FluidRenderHandler {
    public static final Identifier WATER_STILL = Identifier.withDefaultNamespace("block/water_still");
    public static final Identifier WATER_FLOWING = Identifier.withDefaultNamespace("block/water_flow");
    public static final Identifier WATER_OVERLAY = Identifier.withDefaultNamespace("block/water_overlay");
    public static final Identifier LAVA_STILL = Identifier.withDefaultNamespace("block/lava_still");
    public static final Identifier LAVA_FLOWING = Identifier.withDefaultNamespace("block/lava_flow");

    protected final Identifier stillTexture;
    protected final Identifier flowingTexture;
    protected final Identifier overlayTexture;
    protected final TextureAtlasSprite[] sprites;
    protected final int tint;

    public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture, int tint) {
        this.stillTexture = Objects.requireNonNull(stillTexture, "stillTexture");
        this.flowingTexture = Objects.requireNonNull(flowingTexture, "flowingTexture");
        this.overlayTexture = overlayTexture;
        this.sprites = new TextureAtlasSprite[overlayTexture == null ? 2 : 3];
        this.tint = tint;
    }

    public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture) {
        this(stillTexture, flowingTexture, overlayTexture, -1);
    }

    public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, int tint) {
        this(stillTexture, flowingTexture, null, tint);
    }

    public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture) {
        this(stillTexture, flowingTexture, null, -1);
    }

    public static SimpleFluidRenderHandler coloredWater(int tint) {
        return new SimpleFluidRenderHandler(WATER_STILL, WATER_FLOWING, WATER_OVERLAY, tint);
    }

    @Override
    public TextureAtlasSprite[] getFluidSprites(BlockAndLightGetter view, BlockPos pos, FluidState state) {
        return sprites;
    }

    @Override
    public void reloadTextures(TextureAtlas textureAtlas) {
        sprites[0] = textureAtlas.getSprite(stillTexture);
        sprites[1] = textureAtlas.getSprite(flowingTexture);
        if (overlayTexture != null) {
            sprites[2] = textureAtlas.getSprite(overlayTexture);
        }
    }

    @Override
    public int getFluidColor(BlockAndLightGetter view, BlockPos pos, FluidState state) {
        return tint;
    }
}
