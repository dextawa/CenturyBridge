package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.collect.MapMaker;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12199;
import net.minecraft.class_12202;
import net.minecraft.class_12206;
import net.minecraft.class_1959;
import net.minecraft.class_243;
import net.minecraft.class_2874;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4543;
import net.minecraft.class_638;
import net.minecraft.class_9848;
import top.dext.centurybridge.step.v1_21_11.facade.DimensionSpecialEffects;

/**
 * Where 1.21.10 drew a dimension by its DimensionSpecialEffects and 1.21.11 draws it by its DimensionType (skybox,
 * cardinal light type, end flashes) and environment attributes, 1.21.11's drawing points keep their own call and ask
 * the level's 1.21.10 effects inside it, only when 1.21.10 would have drawn something 1.21.11's data cannot say:
 * <ul>
 * <li>(a) the level's effects object is not one of the three 1.21.10 vanilla effects the facade made (a mod registered
 * it, through Fabric API's DimensionRenderingRegistry or by writing the effects table), or</li>
 * <li>(b) a converted mod's mixin changed the answer: it mixes the restored ClientLevel.effects(), or the effects
 * member(s) the point reads, or the constructor that set the field such a member returns (the (l) signal,
 * META-INF/centurybridge/mixed/1.21.11.txt, keys in the layer's names).</li>
 * </ul>
 * Otherwise the point returns 1.21.11's own value untouched. The decision is constant per effects object once mods
 * are loaded, so it is computed once per object and point (getShade runs per block face).
 *
 * The points and the 1.21.10 member each consults (1.21.10's readers in parentheses):
 * <ul>
 * <li>SHADE: constantAmbientLight -- ClientLevel.getShade and GameRenderer.setLevel's Lighting.updateLevel
 * (1.21.10 getShade / setLevel); constant ambient light is 1.21.11's NETHER cardinal light type (the same 0.9
 * shades and the same Lighting setup).</li>
 * <li>BRIGHT: forceBrightLightmap -- ClientLevel's EndFlashState (1.21.10 made it only for force-bright effects; 1.21.11
 * for END skyboxes) and LightTexture's sky light color / factor (1.21.10 LightTexture.updateLightTexture's two
 * reads).</li>
 * <li>FOGGY: isFoggyAt -- the world fog GameRenderer.renderLevel and AtmosphericFogEnvironment.setupFog take from the
 * boss overlay (1.21.10 renderLevel: isFoggyAt(camera x, z) || shouldCreateWorldFog).</li>
 * <li>FOG_COLOR: getBrightnessDependentFogColor, isSunriseOrSunset, getSunriseOrSunsetColor -- the fog color and the
 * sunrise tint of AtmosphericFogEnvironment.getBaseColor (1.21.10 AirBasedFogEnvironment.getBaseColor).</li>
 * <li>SUNRISE: isSunriseOrSunset, getSunriseOrSunsetColor -- SkyRenderState.sunriseAndSunsetColor
 * (1.21.10 SkyRenderer.extractRenderState).</li>
 * <li>SKY: skyType -- SkyRenderState.skybox (1.21.10 SkyRenderer.extractRenderState).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbDimensionEffectsRoutes {
    public static final int SHADE = 0;
    public static final int BRIGHT = 1;
    public static final int FOGGY = 2;
    public static final int FOG_COLOR = 3;
    public static final int SUNRISE = 4;
    public static final int SKY = 5;

    private static final String FACADE = "top/dext/centurybridge/step/v1_21_11/facade/DimensionSpecialEffects";
    /** the restored ClientLevel.effects(), as CbClientLevelEffectsMixin merges it */
    static final String LEVEL_EFFECTS = "net/minecraft/class_638.method_28103()L" + FACADE + ";";
    private static final String SKY_TYPE = "method_29992()L" + FACADE + "$SkyType;";
    private static final String CONSTANT_AMBIENT = "method_29993()Z";
    private static final String FORCE_BRIGHT = "method_73235()Z";
    private static final String FOG = "method_28112(Lnet/minecraft/class_243;F)Lnet/minecraft/class_243;";
    private static final String FOGGY_AT = "method_28110(II)Z";
    private static final String IS_SUNRISE = "method_62183(F)Z";
    private static final String SUNRISE_COLOR = "method_62184(F)I";
    /** the constructors that set skyType / constantAmbientLight / forceBrightLightmap: the base one, a subclass's */
    private static final String BASE_INIT = "<init>(L" + FACADE + "$SkyType;ZZ)V";
    private static final String VANILLA_INIT = "<init>()V";

    private static final String[][] MEMBERS = {
        {CONSTANT_AMBIENT, BASE_INIT, VANILLA_INIT},
        {FORCE_BRIGHT, BASE_INIT, VANILLA_INIT},
        {FOGGY_AT},
        {FOG, IS_SUNRISE, SUNRISE_COLOR},
        {IS_SUNRISE, SUNRISE_COLOR},
        {SKY_TYPE, BASE_INIT, VANILLA_INIT},
    };

    /** 1.21.10 LightTexture's sky light color of force-bright effects, (0.9, 0.5, 1.0) */
    private static final int FORCE_BRIGHT_SKY_LIGHT = class_9848.method_61318(1.0F, 0.9F, 0.5F, 1.0F);

    /** the three effects the facade's static initializer made (1.21.10's vanilla effects), by identity */
    private static final Set<DimensionSpecialEffects> VANILLA = Collections.newSetFromMap(new java.util.IdentityHashMap<>());
    /** per effects object: bit p set = point p consults it (computed once) */
    private static final Map<DimensionSpecialEffects, Integer> DECIDED = new MapMaker().weakKeys().makeMap();
    private static volatile Boolean levelEffectsMixed;

    private CbDimensionEffectsRoutes() {
    }

    /** called by the facade's static initializer with its three vanilla effects */
    public static void vanilla(DimensionSpecialEffects... effects) {
        synchronized (VANILLA) {
            Collections.addAll(VANILLA, effects);
        }
    }

    /** the level's effects when point {@code point} consults them, else null (1.21.11's own value stands) */
    public static DimensionSpecialEffects consulted(class_638 level, int point) {
        if (level == null) {
            return null;
        }
        DimensionSpecialEffects effects = ((CbEffectsLevel) level).cb$levelEffects();
        if (effects == null) {
            return null;
        }
        Integer bits = DECIDED.get(effects);
        if (bits == null) {
            bits = decide(effects);
            DECIDED.put(effects, bits);
        }
        return (bits & (1 << point)) != 0 ? effects : null;
    }

    private static int decide(DimensionSpecialEffects effects) {
        boolean vanilla;
        synchronized (VANILLA) {
            vanilla = VANILLA.contains(effects);
        }
        if (!vanilla || levelEffectsMixed()) {
            return (1 << MEMBERS.length) - 1;
        }
        int bits = 0;
        for (int p = 0; p < MEMBERS.length; p++) {
            for (Class<?> c = effects.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                String owner = c.getName().replace('.', '/');
                for (String m : MEMBERS[p]) {
                    if (CbMixedMembers.mixed(owner + "." + m)) {
                        bits |= 1 << p;
                    }
                }
                if (c == DimensionSpecialEffects.class) {
                    break;
                }
            }
        }
        return bits;
    }

    private static boolean levelEffectsMixed() {
        Boolean b = levelEffectsMixed;
        if (b == null) {
            b = CbMixedMembers.mixed(LEVEL_EFFECTS);
            levelEffectsMixed = b;
        }
        return b;
    }

    // ------------------------------------------------------------------ the points

    /** SHADE: 1.21.11's cardinal light type, or the one 1.21.10's constantAmbientLight stands for */
    public static class_2874.class_12325 cardinalLight(class_638 level, class_2874.class_12325 own) {
        DimensionSpecialEffects e = consulted(level, SHADE);
        if (e == null) {
            return own;
        }
        return e.method_29993() ? class_2874.class_12325.field_64381 : class_2874.class_12325.field_64380;
    }

    /** 1.21.11's ClientLevel.getShade body (bytecode 0-127) for a given cardinal light type (1.21.10's, with constantAmbientLight) */
    public static float shade(net.minecraft.class_2350 direction, boolean shade, class_2874.class_12325 type) {
        boolean nether = type == class_2874.class_12325.field_64381;
        if (!shade) {
            return nether ? 0.9F : 1.0F;
        }
        return switch (direction) {
            case field_11033 -> nether ? 0.9F : 0.5F;
            case field_11036 -> nether ? 0.9F : 1.0F;
            case field_11043, field_11035 -> 0.8F;
            case field_11039, field_11034 -> 0.6F;
        };
    }

    /** BRIGHT: whether the level has an EndFlashState (1.21.11: an END skybox; 1.21.10: force-bright effects) */
    public static boolean endFlashes(class_638 level, boolean own) {
        DimensionSpecialEffects e = consulted(level, BRIGHT);
        return e == null ? own : e.method_73235();
    }

    /** BRIGHT: LightTexture's sky light color (1.21.11's SKY_LIGHT_COLOR attribute) */
    public static int skyLightColor(class_638 level, int own) {
        DimensionSpecialEffects e = consulted(level, BRIGHT);
        if (e == null) {
            return own;
        }
        if (e.method_73235()) {
            return FORCE_BRIGHT_SKY_LIGHT;
        }
        // not force-bright: 1.21.10 took the sky darkness here, which 1.21.11's attribute is; an END type's attribute is
        // 1.21.11's force-bright constant instead, so the attribute's default (white, 1.21.10's value at full light)
        return level.method_8597().method_76467() ? class_12206.field_64345.method_75650() : own;
    }

    /** BRIGHT: LightTexture's sky light factor before the end flash is added (1.21.11's SKY_LIGHT_FACTOR attribute) */
    public static float skyLightFactor(class_638 level, float own) {
        DimensionSpecialEffects e = consulted(level, BRIGHT);
        if (e == null) {
            return own;
        }
        if (e.method_73235()) {
            return 0.0F; // 1.21.10: the end flash alone, which 1.21.11 adds after this
        }
        return level.method_8597().method_76467() ? class_12206.field_64346.method_75650() : own;
    }

    /**
     * BRIGHT: the sky light factor LightTexture hands the lightmap (1.21.11: SKY_LIGHT_FACTOR plus the end flash), with
     * 1.21.10's factor in place of the attribute's when consulted; the attribute is read again here, only then
     */
    public static float skyLightFactorSent(class_638 level, class_4184 camera, float partialTick, float own) {
        DimensionSpecialEffects e = consulted(level, BRIGHT);
        if (e == null || camera == null) {
            return own;
        }
        float attribute = camera.method_75756().method_75689(class_12206.field_64346, partialTick);
        return own - attribute + skyLightFactor(level, attribute);
    }

    /** FOGGY: 1.21.11's world fog flag (the boss overlay's), or'd with 1.21.10's isFoggyAt at the camera's column */
    public static boolean worldFog(class_638 level, class_4184 camera, boolean own) {
        if (own) {
            return true;
        }
        DimensionSpecialEffects e = consulted(level, FOGGY);
        if (e == null || camera == null) {
            return false;
        }
        return e.method_28110(camera.method_19328().method_10263(), camera.method_19328().method_10260());
    }

    /**
     * FOG_COLOR: AtmosphericFogEnvironment.getBaseColor's fog color (1.21.11's FOG_COLOR attribute), or 1.21.10's:
     * getBrightnessDependentFogColor of the biome fog colors sampled around the camera (1.21.10's CubicSampler over
     * the biomes' fog colors at quart positions) and the day brightness clamp(cos(sun angle) * 2 + 0.5, 0, 1). The
     * biome fog color is the biome's FOG_COLOR applied to its dimension's, before any timeline (1.21.10's
     * Biome.getFogColor); the sun angle is 1.21.11's SUN_ANGLE, the angle 1.21.11 draws the sun at (1.21.10's
     * getSunAngle = timeOfDay * 2 pi).
     */
    public static int fogColor(class_638 level, class_4184 camera, float partialTick, int own) {
        DimensionSpecialEffects e = consulted(level, FOG_COLOR);
        if (e == null || camera == null) {
            return own;
        }
        float sunAngle = sunAngle(camera, partialTick);
        float brightness = class_3532.method_15363(class_3532.method_15362(sunAngle) * 2.0F + 0.5F, 0.0F, 1.0F);
        class_4543 biomes = level.method_22385();
        int dimensionFog = level.method_8597().comp_5146().method_75663(class_12206.field_63742,
            class_12206.field_63742.method_75650());
        class_243 at = camera.method_71156().method_1023(2.0, 2.0, 2.0).method_1021(0.25);
        class_243 sampled = gaussianSample(at, (x, y, z) -> rgb24(
            biomeFog(biomes.method_24854(x, y, z).comp_349(), dimensionFog)));
        class_243 fog = e.method_28112(sampled, brightness);
        return class_9848.method_61318(1.0F, (float) fog.field_1352, (float) fog.field_1351, (float) fog.field_1350);
    }

    /** FOG_COLOR: AtmosphericFogEnvironment.getBaseColor's sunrise tint (1.21.11's SUNRISE_SUNSET_COLOR attribute) */
    public static int fogSunriseColor(class_638 level, class_4184 camera, float partialTick, int own) {
        DimensionSpecialEffects e = consulted(level, FOG_COLOR);
        if (e == null || camera == null) {
            return own;
        }
        return sunrise(e, sunAngle(camera, partialTick));
    }

    /** SUNRISE: SkyRenderState.sunriseAndSunsetColor, given the state's sun angle (radians) */
    public static int skySunriseColor(class_638 level, float sunAngle, int own) {
        DimensionSpecialEffects e = consulted(level, SUNRISE);
        return e == null ? own : sunrise(e, sunAngle);
    }

    /** SKY: SkyRenderState.skybox, or the one 1.21.10's skyType stands for (rt/CbSkyType) */
    public static class_2874.class_12326 skybox(class_638 level, class_2874.class_12326 own) {
        DimensionSpecialEffects e = consulted(level, SKY);
        return e == null ? own : CbSkyType.toW(e.method_29992());
    }

    /**
     * 1.21.10 drew the sunrise only when isSunriseOrSunset(timeOfDay), with getSunriseOrSunsetColor(timeOfDay); 1.21.11
     * draws its color whenever its alpha is above 0.001, so "not sunrise" is a transparent color. timeOfDay is the
     * sun angle over 2 pi, in [0, 1) as 1.21.10's.
     */
    private static int sunrise(DimensionSpecialEffects e, float sunAngle) {
        float t = sunAngle / 6.2831855F;
        t -= (float) Math.floor(t);
        return e.method_62183(t) ? e.method_62184(t) : 0;
    }

    private static float sunAngle(class_4184 camera, float partialTick) {
        class_12202 probe = camera.method_75756();
        return probe.method_75689(class_12206.field_64340, partialTick) * 0.017453292F;
    }

    /** 1.21.10's Vec3.fromRGB24 (removed by 1.21.11): each 8-bit channel over 255 */
    private static class_243 rgb24(int rgb) {
        return new class_243((rgb >> 16 & 255) / 255.0, (rgb >> 8 & 255) / 255.0, (rgb & 255) / 255.0);
    }

    private static int biomeFog(class_1959 biome, int dimensionFog) {
        class_12199 attributes = biome.method_75734();
        return attributes.method_75663(class_12206.field_63742, dimensionFog);
    }

    @FunctionalInterface
    interface Fetcher {
        class_243 fetch(int x, int y, int z);
    }

    private static final double[] KERNEL = {0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};

    /** 1.21.10's CubicSampler.gaussianSampleVec3 (class_6491.method_24895, removed by 1.21.11), as its bytecode */
    static class_243 gaussianSample(class_243 pos, Fetcher fetcher) {
        int i = class_3532.method_15357(pos.field_1352);
        int j = class_3532.method_15357(pos.field_1351);
        int k = class_3532.method_15357(pos.field_1350);
        double d = pos.field_1352 - i;
        double e = pos.field_1351 - j;
        double f = pos.field_1350 - k;
        double g = 0.0;
        class_243 sum = class_243.field_1353;
        for (int l = 0; l < 6; l++) {
            double h = class_3532.method_16436(d, KERNEL[l + 1], KERNEL[l]);
            int m = i - 2 + l;
            for (int n = 0; n < 6; n++) {
                double o = class_3532.method_16436(e, KERNEL[n + 1], KERNEL[n]);
                int p = j - 2 + n;
                for (int q = 0; q < 6; q++) {
                    double r = class_3532.method_16436(f, KERNEL[q + 1], KERNEL[q]);
                    int s = k - 2 + q;
                    double t = h * o * r;
                    g += t;
                    sum = sum.method_1019(fetcher.fetch(m, p, s).method_1021(t));
                }
            }
        }
        return sum.method_1021(1.0 / g);
    }
}
