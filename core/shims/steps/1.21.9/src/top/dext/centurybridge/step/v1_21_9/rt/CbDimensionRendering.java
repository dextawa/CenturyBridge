package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_5294;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import top.dext.centurybridge.step.v1_21_9.facade.DimensionRenderingRegistry;

/**
 * The restored DimensionRenderingRegistry's tables (Fabric API 0.136's DimensionRenderingRegistryImpl): first
 * registration per dimension wins, effects go into vanilla's effects-by-id map (DimensionSpecialEffects.forType reads
 * it). Registering a renderer turns the restored frame context on (CbWorldRender.cbActivate), since a renderer is
 * handed the WorldRenderContext.
 */
@Environment(EnvType.CLIENT)
public final class CbDimensionRendering {

    private CbDimensionRendering() {
    }

    private static final Map<class_5321<class_1937>, DimensionRenderingRegistry.SkyRenderer> SKY = new IdentityHashMap<>();
    private static final Map<class_5321<class_1937>, DimensionRenderingRegistry.CloudRenderer> CLOUD = new IdentityHashMap<>();
    private static final Map<class_5321<class_1937>, DimensionRenderingRegistry.WeatherRenderer> WEATHER = new IdentityHashMap<>();

    /** whether any renderer was registered: the mixin's sky / cloud / weather hooks look nothing up before */
    private static volatile boolean any;

    public static void registerSky(class_5321<class_1937> key, DimensionRenderingRegistry.SkyRenderer renderer) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(renderer);
        SKY.putIfAbsent(key, renderer);
        registered();
    }

    public static void registerCloud(class_5321<class_1937> key, DimensionRenderingRegistry.CloudRenderer renderer) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(renderer);
        CLOUD.putIfAbsent(key, renderer);
        registered();
    }

    public static void registerWeather(class_5321<class_1937> key, DimensionRenderingRegistry.WeatherRenderer renderer) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(renderer);
        WEATHER.putIfAbsent(key, renderer);
        registered();
    }

    public static void registerEffects(class_2960 key, class_5294 effects) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(effects);
        class_5294.field_24609.putIfAbsent(key, effects);
    }

    public static DimensionRenderingRegistry.SkyRenderer sky(class_5321<class_1937> key) {
        return SKY.get(key);
    }

    public static DimensionRenderingRegistry.CloudRenderer cloud(class_5321<class_1937> key) {
        return CLOUD.get(key);
    }

    public static DimensionRenderingRegistry.WeatherRenderer weather(class_5321<class_1937> key) {
        return WEATHER.get(key);
    }

    public static class_5294 effects(class_2960 key) {
        return class_5294.field_24609.get(key);
    }

    private static void registered() {
        any = true;
        CbWorldRender.cbActivate();
    }

    /** sky pass HEAD: the dimension's registered renderer draws instead of 1.21.9's sky; false = none registered */
    public static boolean cbRenderSky(CbWorldRenderContextImpl context, class_638 world) {
        if (!any || world == null) {
            return false;
        }
        DimensionRenderingRegistry.SkyRenderer renderer = SKY.get(world.method_27983());
        if (renderer == null) {
            return false;
        }
        renderer.render(context);
        return true;
    }

    public static boolean cbRenderClouds(CbWorldRenderContextImpl context, class_638 world) {
        if (!any || world == null) {
            return false;
        }
        DimensionRenderingRegistry.CloudRenderer renderer = CLOUD.get(world.method_27983());
        if (renderer == null) {
            return false;
        }
        renderer.render(context);
        return true;
    }

    public static boolean cbRenderWeather(CbWorldRenderContextImpl context, class_638 world) {
        if (!any || world == null) {
            return false;
        }
        DimensionRenderingRegistry.WeatherRenderer renderer = WEATHER.get(world.method_27983());
        if (renderer == null) {
            return false;
        }
        renderer.render(context);
        return true;
    }
}
