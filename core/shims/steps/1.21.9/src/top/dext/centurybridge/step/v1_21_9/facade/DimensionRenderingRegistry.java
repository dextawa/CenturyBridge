package top.dext.centurybridge.step.v1_21_9.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_5294;
import net.minecraft.class_5321;
import top.dext.centurybridge.step.v1_21_9.rt.CbDimensionRendering;

/**
 * Fabric API 0.136's (1.21.8) DimensionRenderingRegistry, deleted by Fabric API for 1.21.9: per-dimension sky, cloud
 * and weather renderers taking the restored WorldRenderContext, and dimension effects by id. The layer's
 * CbWorldRenderEventsMixin consults a renderer only for a dimension one was registered for; every other dimension
 * renders 1.21.9's sky, clouds and weather.
 */
@Environment(EnvType.CLIENT)
public interface DimensionRenderingRegistry {

    static void registerSkyRenderer(class_5321<class_1937> key, SkyRenderer renderer) {
        CbDimensionRendering.registerSky(key, renderer);
    }

    static void registerWeatherRenderer(class_5321<class_1937> key, WeatherRenderer renderer) {
        CbDimensionRendering.registerWeather(key, renderer);
    }

    static void registerDimensionEffects(class_2960 key, class_5294 effects) {
        CbDimensionRendering.registerEffects(key, effects);
    }

    static void registerCloudRenderer(class_5321<class_1937> key, CloudRenderer renderer) {
        CbDimensionRendering.registerCloud(key, renderer);
    }

    static SkyRenderer getSkyRenderer(class_5321<class_1937> key) {
        return CbDimensionRendering.sky(key);
    }

    static CloudRenderer getCloudRenderer(class_5321<class_1937> key) {
        return CbDimensionRendering.cloud(key);
    }

    static WeatherRenderer getWeatherRenderer(class_5321<class_1937> key) {
        return CbDimensionRendering.weather(key);
    }

    static class_5294 getDimensionEffects(class_2960 key) {
        return CbDimensionRendering.effects(key);
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    interface SkyRenderer {
        void render(WorldRenderContext context);
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    interface CloudRenderer {
        void render(WorldRenderContext context);
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    interface WeatherRenderer {
        void render(WorldRenderContext context);
    }
}
