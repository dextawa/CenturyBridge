package top.dext.centurybridge.step.v1_21_11.facade;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_2874;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_9848;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffects;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffectsRoutes;

/**
 * 1.21.10's DimensionSpecialEffects (class_5294), deleted by 1.21.11: what a dimension looks like from the client (sky
 * type, constant ambient light, bright lightmap, fog color by brightness, foggy columns, sunrise color), chosen by the
 * dimension type's effects id. 1.21.11 keeps the same choices in DimensionType (skybox, cardinal lighting) and the
 * environment attributes (fog / sky / sunrise colors and distances). Members, bodies and the three vanilla effects are
 * 1.21.10's; the effects map is 1.21.10's (Fabric API's DimensionRenderingRegistry, restored by the 1.21.9 layer, puts a
 * mod's effects in it); forType reads the dimension type's effects id as rt/CbDimensionEffects restores it.
 */
@Environment(EnvType.CLIENT)
public abstract class DimensionSpecialEffects {
    public static final Object2ObjectMap<class_2960, DimensionSpecialEffects> field_24609 = new Object2ObjectArrayMap<>();
    public final SkyType field_25637;
    public final boolean field_25638;
    public final boolean field_62030;

    static {
        OverworldEffects overworld = new OverworldEffects();
        NetherEffects nether = new NetherEffects();
        EndEffects end = new EndEffects();
        ((Object2ObjectArrayMap<class_2960, DimensionSpecialEffects>) field_24609).defaultReturnValue(overworld);
        field_24609.put(CbDimensionEffects.OVERWORLD_EFFECTS, overworld);
        field_24609.put(CbDimensionEffects.NETHER_EFFECTS, nether);
        field_24609.put(CbDimensionEffects.END_EFFECTS, end);
        // 1.21.11's drawing points consult a level's effects only when they are not these (rt/CbDimensionEffectsRoutes)
        CbDimensionEffectsRoutes.vanilla(overworld, nether, end);
    }

    public DimensionSpecialEffects(SkyType skyType, boolean constantAmbientLight, boolean forceBrightLightmap) {
        this.field_25637 = skyType;
        this.field_25638 = constantAmbientLight;
        this.field_62030 = forceBrightLightmap;
    }

    /** forType: the effects named by the dimension type's effects id (1.21.10: DimensionType.effectsLocation) */
    public static DimensionSpecialEffects method_28111(class_2874 type) {
        return field_24609.get(CbDimensionEffects.comp_655(type));
    }

    /** isSunriseOrSunset */
    public boolean method_62183(float timeOfDay) {
        return false;
    }

    /** getSunriseOrSunsetColor */
    public int method_62184(float timeOfDay) {
        return 0;
    }

    /** getBrightnessDependentFogColor */
    public abstract class_243 method_28112(class_243 fogColor, float brightness);

    /** isFoggyAt */
    public abstract boolean method_28110(int x, int z);

    /** skyType */
    public SkyType method_29992() {
        return this.field_25637;
    }

    /** constantAmbientLight */
    public boolean method_29993() {
        return this.field_25638;
    }

    /** forceBrightLightmap */
    public boolean method_73235() {
        return this.field_62030;
    }

    /** 1.21.10's DimensionSpecialEffects.SkyType (class_5294$class_5401), constants under their intermediary names */
    @Environment(EnvType.CLIENT)
    public enum SkyType {
        /** NONE */
        field_25639,
        /** NORMAL */
        field_25640,
        /** END */
        field_25641
    }

    /** 1.21.10's OverworldEffects (class_5294$class_5297) */
    @Environment(EnvType.CLIENT)
    public static class OverworldEffects extends DimensionSpecialEffects {
        /** SUNRISE_AND_SUNSET_TIMESPAN */
        public static final float field_53064 = 0.4F;

        public OverworldEffects() {
            super(SkyType.field_25640, false, false);
        }

        @Override
        public boolean method_62183(float timeOfDay) {
            float f = class_3532.method_15362(timeOfDay * 6.2831855F);
            return f >= -0.4F && f <= 0.4F;
        }

        @Override
        public int method_62184(float timeOfDay) {
            float f = class_3532.method_15362(timeOfDay * 6.2831855F);
            float g = f / 0.4F * 0.5F + 0.5F;
            float h = class_3532.method_27285(1.0F - (1.0F - class_3532.method_15374(g * 3.1415927F)) * 0.99F);
            return class_9848.method_61318(h, g * 0.3F + 0.7F, g * g * 0.7F + 0.2F, 0.2F);
        }

        @Override
        public class_243 method_28112(class_243 fogColor, float brightness) {
            return fogColor.method_18805(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F, brightness * 0.91F + 0.09F);
        }

        @Override
        public boolean method_28110(int x, int z) {
            return false;
        }
    }

    /** 1.21.10's NetherEffects (class_5294$class_5296) */
    @Environment(EnvType.CLIENT)
    public static class NetherEffects extends DimensionSpecialEffects {
        public NetherEffects() {
            super(SkyType.field_25639, true, false);
        }

        @Override
        public class_243 method_28112(class_243 fogColor, float brightness) {
            return fogColor;
        }

        @Override
        public boolean method_28110(int x, int z) {
            return true;
        }
    }

    /** 1.21.10's EndEffects (class_5294$class_5295) */
    @Environment(EnvType.CLIENT)
    public static class EndEffects extends DimensionSpecialEffects {
        public EndEffects() {
            super(SkyType.field_25641, false, true);
        }

        @Override
        public class_243 method_28112(class_243 fogColor, float brightness) {
            return fogColor.method_1021(0.15F);
        }

        @Override
        public boolean method_28110(int x, int z) {
            return false;
        }
    }
}
