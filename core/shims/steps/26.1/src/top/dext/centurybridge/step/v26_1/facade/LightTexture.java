package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.util.RandomSource;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import top.dext.centurybridge.step.v26_1.rt.CbWallLight;

/**
 * 1.21.11's LightTexture (net/minecraft/class_765) on 26.1, which split it in two: the
 * LightmapRenderStateExtractor (tick's block-light flicker and update flag, the lighting computation of
 * updateLightTexture) and the Lightmap (the texture, rendered from the extracted state).
 * <p>
 * One a mod makes owns all three parts, as its 1.21.11 LightTexture owned its texture and state. The game
 * renderer's own (GameRenderer.lightTexture(), {@link #cb$of}) is made of the renderer's parts: ticking
 * or updating it ticks or updates the renderer's lightmap, as calling 1.21.11's renderer-owned
 * LightTexture did. The static helpers are rt/CbWallLight's (staticRedirects). Member names are
 * 1.21.11's intermediary names.
 * <p>
 * 1.21.11's private fields that are fixed objects are this class's, taken from the parts (javap 26.1): texture /
 * textureView / ubo (field_57927 / field_60581 / field_60112) the Lightmap's, renderer / minecraft / randomSource
 * (field_4134 / field_4137 / field_64675) the extractor's. updateLightTexture and blockLightRedFlicker are the
 * extractor's live state and LIGHTMAP_UBO_SIZE describes 1.21.11's uniform layout, which 26.1 changed: facadeGaps.
 */
@Environment(EnvType.CLIENT)
public class LightTexture implements AutoCloseable {

    /** FULL_BRIGHT, FULL_SKY, FULL_BLOCK (1.21.11 values) */
    public static final int field_32767 = 15728880;
    public static final int field_32768 = 15728640;
    public static final int field_32769 = 240;

    private final Lightmap cb$lightmap;
    private final LightmapRenderStateExtractor cb$extractor;
    private final LightmapRenderState cb$state;
    /** whether this owns its lightmap (closing closes it) */
    private final boolean cb$own;
    public final GpuTexture field_57927;
    public final GpuTextureView field_60581;
    public final MappableRingBuffer field_60112;
    public final GameRenderer field_4134;
    public final Minecraft field_4137;
    public final RandomSource field_64675;

    /** 1.21.11 LightTexture(GameRenderer, Minecraft) */
    public LightTexture(GameRenderer renderer, Minecraft minecraft) {
        this(new Lightmap(), new LightmapRenderStateExtractor(renderer, minecraft), new LightmapRenderState(), true);
    }

    private LightTexture(Lightmap lightmap, LightmapRenderStateExtractor extractor, LightmapRenderState state, boolean own) {
        this.cb$lightmap = lightmap;
        this.cb$extractor = extractor;
        this.cb$state = state;
        this.cb$own = own;
        this.field_57927 = lightmap.texture;
        this.field_60581 = lightmap.textureView;
        this.field_60112 = lightmap.ubo;
        this.field_4134 = extractor.renderer;
        this.field_4137 = extractor.minecraft;
        this.field_64675 = extractor.randomSource;
    }

    /** the game renderer's own lightmap as 1.21.11's LightTexture of it */
    public static LightTexture cb$of(Lightmap lightmap, LightmapRenderStateExtractor extractor, LightmapRenderState state) {
        return new LightTexture(lightmap, extractor, state, false);
    }

    /** getTextureView() */
    public GpuTextureView method_71650() {
        return cb$lightmap.getTextureView();
    }

    @Override
    public void close() {
        if (cb$own) {
            cb$lightmap.close();
        }
    }

    /** tick(): the block-light flicker, and the lightmap marked for an update */
    public void method_3314() {
        cb$extractor.tick();
    }

    /** updateLightTexture(partialTicks): computed and rendered when marked, as 1.21.11 did */
    public void method_3313(float partialTicks) {
        cb$extractor.extract(cb$state, partialTicks);
        cb$lightmap.render(cb$state);
    }

    /** getBrightness(dimensionType, level) */
    public static float method_23284(net.minecraft.world.level.dimension.DimensionType type, int level) {
        return CbWallLight.getBrightness(type, level);
    }

    /** getBrightness(ambientLight, level) */
    public static float method_62226(float ambientLight, int level) {
        return CbWallLight.getBrightness(ambientLight, level);
    }

    /** pack(block, sky) */
    public static int method_23687(int block, int sky) {
        return CbWallLight.pack(block, sky);
    }

    /** block(packed) */
    public static int method_24186(int packed) {
        return CbWallLight.block(packed);
    }

    /** sky(packed) */
    public static int method_24187(int packed) {
        return CbWallLight.sky(packed);
    }

    /** lightCoordsWithEmission(packed, emission) */
    public static int method_62228(int packed, int emission) {
        return CbWallLight.lightCoordsWithEmission(packed, emission);
    }
}
