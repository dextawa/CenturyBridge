package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;

/**
 * 26.1.2's vanilla bed sprites (entity/bed/&lt;colour&gt;, 64x64, laid out for BedRenderer's head and foot parts), which
 * 26.2 no longer ships: it draws its beds as block models from per-face block textures (block/&lt;colour&gt;_bed_head_up,
 * _head_east, _head_west, _foot_up, _foot_east, _foot_west, _foot_south, and the shared bed_down and bed_head_north).
 * Those textures are 26.1.2's sprite cut up: every region of the sprite that 26.1.2's bed parts sample (the faces of
 * the head and foot boxes, the faces of the four legs) is one rectangle of one of them, rotated or mirrored -- checked
 * pixel by pixel against 26.1.2's sixteen sprites (s26b, 2026-10-25): identical for fifteen colours; for white the
 * headboard (the head box's end face) is 26.2's shared bed_head_north, as 26.2 draws its white bed (26.1.2's white
 * sprite had its own, 40 pixels apart). This source puts the sprites back together, per colour, from whatever block
 * textures the resource packs give (so a pack retexturing 26.2's beds retextures these alike); pixels no part samples
 * stay transparent. Registered as atlas source type "centurybridge:legacy_beds" (mixin/CbS26BedSpritesMixin); the
 * layer's atlases/blocks.json lists it ahead of 26.1.2's entity/bed directory, so a texture a pack ships under that name
 * wins.
 */
@Environment(EnvType.CLIENT)
public record CbS26LegacyBedSprites() implements SpriteSource {
    public static final MapCodec<CbS26LegacyBedSprites> MAP_CODEC = MapCodec.unit(new CbS26LegacyBedSprites());

    /** the block textures, by index; "*" is the colour */
    private static final String[] TEXTURES = {
        "*_bed_head_up", "*_bed_head_east", "*_bed_head_west", "*_bed_foot_up", "*_bed_foot_east", "*_bed_foot_west",
        "*_bed_foot_south", "bed_down", "bed_head_north"
    };

    /**
     * {x, y, w, h of the 26.1.2 region; block texture; rotation (counter-clockwise degrees) and mirror that turn the
     * region into the texture's rectangle; that rectangle's corner}
     */
    private static final int[][] REGIONS = {
        {6, 0, 16, 6, 8, 180, 0, 0, 7},     // head box end (headboard): bed_head_north
        {0, 6, 6, 16, 2, 90, 0, 0, 7},      // head box side: head_west
        {6, 6, 16, 16, 0, 0, 0, 0, 0},      // head box top: head_up
        {22, 6, 6, 16, 1, 270, 0, 0, 7},    // head box side: head_east
        {28, 6, 16, 16, 7, 180, 0, 0, 0},   // head box bottom: bed_down
        {22, 22, 16, 6, 6, 180, 1, 0, 7},   // foot box end: foot_south
        {0, 28, 6, 16, 5, 90, 0, 0, 7},     // foot box side: foot_west
        {6, 28, 16, 16, 3, 0, 0, 0, 0},     // foot box top: foot_up
        {22, 28, 6, 16, 4, 270, 0, 0, 7},   // foot box side: foot_east
        {28, 28, 16, 16, 7, 180, 0, 0, 0},  // foot box bottom: bed_down
        {56, 6, 3, 3, 2, 90, 0, 6, 13},     // head legs (texOffs 50,6 and 50,18): up, west, north, east, south
        {50, 9, 3, 3, 1, 0, 0, 13, 13},
        {53, 9, 3, 3, 2, 0, 0, 0, 13},
        {56, 9, 3, 3, 2, 0, 0, 3, 13},
        {59, 9, 3, 3, 1, 0, 0, 10, 13},
        {56, 18, 3, 3, 2, 90, 0, 6, 13},
        {50, 21, 3, 3, 1, 0, 0, 13, 13},
        {53, 21, 3, 3, 2, 0, 0, 0, 13},
        {56, 21, 3, 3, 2, 0, 0, 3, 13},
        {59, 21, 3, 3, 1, 0, 0, 10, 13},
        {56, 0, 3, 3, 5, 0, 0, 7, 13},      // foot legs (texOffs 50,0 and 50,12)
        {50, 3, 3, 3, 5, 0, 0, 13, 13},
        {53, 3, 3, 3, 4, 0, 0, 0, 13},
        {56, 3, 3, 3, 4, 0, 0, 3, 13},
        {59, 3, 3, 3, 5, 0, 0, 10, 13},
        {56, 12, 3, 3, 5, 0, 0, 7, 13},
        {50, 15, 3, 3, 5, 0, 0, 13, 13},
        {53, 15, 3, 3, 4, 0, 0, 0, 13},
        {56, 15, 3, 3, 4, 0, 0, 3, 13},
        {59, 15, 3, 3, 5, 0, 0, 10, 13},
    };

    @Override
    public void run(ResourceManager resources, SpriteSource.Output output) {
        for (DyeColor color : DyeColor.values()) {
            Identifier id = Identifier.withDefaultNamespace("entity/bed/" + color.getName());
            output.add(id, loader -> build(resources, id, color));
        }
    }

    private static SpriteContents build(ResourceManager resources, Identifier id, DyeColor color) {
        NativeImage[] textures = new NativeImage[TEXTURES.length];
        try {
            for (int t = 0; t < TEXTURES.length; t++) {
                Identifier file = Identifier.withDefaultNamespace("textures/block/" + TEXTURES[t].replace("*", color.getName()) + ".png");
                Optional<Resource> resource = resources.getResource(file);
                if (resource.isEmpty()) {
                    SpriteResourceLoaderLog.missing(id, file);
                    return null;
                }
                try (InputStream in = resource.get().open()) {
                    textures[t] = NativeImage.read(in);
                }
            }
            // a pack's textures may be finer than 16x16 (all alike): the sprite is made at the same scale
            int scale = Math.max(1, textures[0].getWidth() / 16);
            for (NativeImage t : textures) {
                if (t.getWidth() != 16 * scale || t.getHeight() != 16 * scale) {
                    SpriteResourceLoaderLog.failed(id, new IllegalStateException("bed textures of different sizes"));
                    return null;
                }
            }
            NativeImage sprite = new NativeImage(64 * scale, 64 * scale, true);
            for (int[] r : REGIONS) {
                NativeImage from = textures[r[4]];
                for (int j = 0; j < r[3] * scale; j++) {
                    for (int i = 0; i < r[2] * scale; i++) {
                        int[] at = source(r[2] * scale, r[3] * scale, r[5], r[6] != 0, i, j);
                        sprite.setPixel(r[0] * scale + i, r[1] * scale + j, from.getPixel(r[7] * scale + at[0], r[8] * scale + at[1]));
                    }
                }
            }
            return new SpriteContents(id, new FrameSize(64 * scale, 64 * scale), sprite);
        } catch (IOException | RuntimeException e) {
            SpriteResourceLoaderLog.failed(id, e);
            return null;
        } finally {
            for (NativeImage image : textures) {
                if (image != null) {
                    image.close();
                }
            }
        }
    }

    /** where pixel (i, j) of a w x h region lands once rotated counter-clockwise by rot degrees, then mirrored */
    private static int[] source(int w, int h, int rot, boolean mirror, int i, int j) {
        int a;
        int b;
        int width;
        switch (rot) {
            case 90 -> { a = j; b = w - 1 - i; width = h; }
            case 180 -> { a = w - 1 - i; b = h - 1 - j; width = w; }
            case 270 -> { a = h - 1 - j; b = i; width = h; }
            default -> { a = i; b = j; width = w; }
        }
        return new int[] {mirror ? width - 1 - a : a, b};
    }

    @Override
    public MapCodec<CbS26LegacyBedSprites> codec() {
        return MAP_CODEC;
    }

    /** the atlas loader's own log for a sprite that could not be made (the sprite is then left out, as 26.2's sources do) */
    private static final class SpriteResourceLoaderLog {
        static void missing(Identifier sprite, Identifier file) {
            net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader.LOGGER.warn("Cannot build legacy bed sprite {}: missing {}", sprite, file);
        }

        static void failed(Identifier sprite, Exception e) {
            net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader.LOGGER.error("Cannot build legacy bed sprite {}", sprite, e);
        }
    }
}
