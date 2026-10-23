package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelReader;

/**
 * 1.21.11's BlockAndTintGetter (net/minecraft/class_1920) on 26.1, where it split in two:
 * <ul>
 * <li>the light half (getLightEngine, getBrightness, getRawBrightness, canSeeSky) is
 *     world.level.BlockAndLightGetter, which every level still implements, server side included. The
 *     layer renames class_1920 to it, so a 1.21.11 mod's server code that passes a level as one keeps
 *     linking (Animal.isBrightEnoughToSpawn, LevelRenderer.getLightCoords take it as such in 26.1);</li>
 * <li>the tint half (getShade, getBlockTint) moved to the client-only client.renderer.block.BlockAndTintGetter
 *     (getShade answered by its CardinalLighting), which ClientLevel and the render regions implement.</li>
 * </ul>
 * This class keeps no reference to the client type; the client half is CbWallTintClient, reached only on
 * the client. What 1.21.11 answered on the other implementations is kept: ServerLevel, WorldGenRegion and
 * the other server-side BlockAndTintGetters returned 1.0 from getShade, and every LevelReader tinted with
 * its default getBlockTint (the resolver's colour of the biome at the position).
 */
public final class CbWallTint {

    private static final boolean CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    private CbWallTint() {
    }

    /** 1.21.11 BlockAndTintGetter.getShade(Direction, boolean) */
    public static float getShade(BlockAndLightGetter level, Direction direction, boolean shade) {
        if (CLIENT) {
            Float f = CbWallTintClient.shade(level, direction, shade);
            if (f != null) {
                return f;
            }
        }
        return 1.0F;
    }

    /** 1.21.11 BlockAndTintGetter.getBlockTint(BlockPos, ColorResolver) */
    public static int getBlockTint(BlockAndLightGetter level, BlockPos pos, ColorResolver resolver) {
        if (CLIENT) {
            Integer c = CbWallTintClient.tint(level, pos, resolver);
            if (c != null) {
                return c;
            }
        }
        if (level instanceof LevelReader reader) {
            return levelReaderTint(reader, pos, resolver);
        }
        // every 1.21.11 BlockAndTintGetter was a LevelReader or a client-side tint getter; a value that
        // is neither cannot have been one
        throw new IllegalArgumentException(level + " was no 1.21.11 BlockAndTintGetter: it has no block tint");
    }

    /** 1.21.11 LevelReader.getBlockTint, the default every level that did not override it used */
    static int levelReaderTint(LevelReader reader, BlockPos pos, ColorResolver resolver) {
        return resolver.getColor(reader.getBiome(pos).value(), pos.getX(), pos.getZ());
    }
}
