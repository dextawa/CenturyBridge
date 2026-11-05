package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12246;
import net.minecraft.class_12247;
import net.minecraft.class_12249;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_2960;

/**
 * 1.21.10 RenderType's private static render types and memoized factory functions (read through a mod's access widener),
 * which 1.21.11 keeps in rendertype/RenderTypes: each field is the one the paired factory reads in both releases (javap
 * -c -p of both code jars: the 1.21.10 factory's only GETSTATIC and its CbRenderTypes pair's only GETSTATIC, same type).
 * CUTOUT_MIPPED (field_9175) is CUTOUT, as the factory pair (1.21.11 folded the layer).
 * Where the paired 1.21.11 factory only reads the field (javap -c -p 1.21.11: GETSTATIC [+ apply]), the value is taken
 * through that public factory instead: 26.1 dropped or renamed those fields (ENTITY_SMOOTH_CUTOUT, ENTITY_DECAL,
 * DRAGON_EXPLOSION_ALPHA, DRAGON_RAYS_DEPTH, TRIPWIRE_MOVING_BLOCk, WEATHER_*; TEXT_INTENSITY* became TEXT_GRAYSCALE* at
 * 26.2) while the wall and later steps route the factories.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTypeStatics {
    private CbRenderTypeStatics() {
    }

    /** 1.21.10 RenderType.field_9178: 1.21.11 RenderTypes.field_64044  */
    public static class_1921 field_9178() {
        return class_12249.field_64044;
    }

    /** 1.21.10 RenderType.field_9175: 1.21.11 RenderTypes.field_64045  */
    public static class_1921 field_9175() {
        return class_12249.field_64045;
    }

    /** 1.21.10 RenderType.field_9174: 1.21.11 RenderTypes.field_64045  */
    public static class_1921 field_9174() {
        return class_12249.field_64045;
    }

    /** 1.21.10 RenderType.field_25285: 1.21.11 RenderTypes.field_64046  */
    public static class_1921 field_25285() {
        return class_12249.field_64046;
    }

    /** 1.21.10 RenderType.field_29622: 1.21.11 RenderTypes.field_64047  */
    public static java.util.function.Function<class_2960, class_1921> field_29622() {
        return class_12249.field_64047;
    }

    /** 1.21.10 RenderType.field_54962: 1.21.11 RenderTypes.field_64048  */
    public static java.util.function.Function<class_2960, class_1921> field_54962() {
        return class_12249.field_64048;
    }

    /** 1.21.10 RenderType.field_29623: 1.21.11 RenderTypes.field_64049  */
    public static java.util.function.Function<class_2960, class_1921> field_29623() {
        return class_12249.field_64049;
    }

    /** 1.21.10 RenderType.field_53130: 1.21.11 RenderTypes.field_64050  */
    public static java.util.function.Function<class_2960, class_1921> field_53130() {
        return class_12249.field_64050;
    }

    /** 1.21.10 RenderType.field_29624: 1.21.11 RenderTypes.field_64051  */
    public static java.util.function.Function<class_2960, class_1921> field_29624() {
        return class_12249.field_64051;
    }

    /** 1.21.10 RenderType.field_29625: 1.21.11 RenderTypes.field_64052  */
    public static java.util.function.BiFunction<class_2960, Boolean, class_1921> field_29625() {
        return class_12249.field_64052;
    }

    /** 1.21.10 RenderType.field_29626: 1.21.11 RenderTypes.field_64053  */
    public static java.util.function.BiFunction<class_2960, Boolean, class_1921> field_29626() {
        return class_12249.field_64053;
    }

    /** 1.21.10 RenderType.field_29627: 1.21.11 RenderTypes.field_64054  */
    public static java.util.function.Function<class_2960, class_1921> field_29627() {
        return class_12249.field_64054;
    }

    /** 1.21.10 RenderType.field_29629: 1.21.11 RenderTypes.field_64055  */
    public static java.util.function.BiFunction<class_2960, Boolean, class_1921> field_29629() {
        return class_12249.field_64055;
    }

    /** 1.21.10 RenderType.field_38345: 1.21.11 RenderTypes.field_64056  */
    public static java.util.function.BiFunction<class_2960, Boolean, class_1921> field_38345() {
        return class_12249.field_64056;
    }

    /** 1.21.10 RenderType.field_29630: 1.21.11 RenderTypes.field_64057, read through method_76004  */
    public static java.util.function.Function<class_2960, class_1921> field_29630() {
        return texture -> class_12249.method_76004(texture);
    }

    /** 1.21.10 RenderType.field_29631: 1.21.11 RenderTypes.field_64058  */
    public static java.util.function.BiFunction<class_2960, Boolean, class_1921> field_29631() {
        return class_12249.field_64058;
    }

    /** 1.21.10 RenderType.field_29632: 1.21.11 RenderTypes.field_64059, read through method_76006  */
    public static java.util.function.Function<class_2960, class_1921> field_29632() {
        return texture -> class_12249.method_76006(texture);
    }

    /** 1.21.10 RenderType.field_29633: 1.21.11 RenderTypes.field_64060  */
    public static java.util.function.Function<class_2960, class_1921> field_29633() {
        return class_12249.field_64060;
    }

    /** 1.21.10 RenderType.field_29634: 1.21.11 RenderTypes.field_64061  */
    public static java.util.function.Function<class_2960, class_1921> field_29634() {
        return class_12249.field_64061;
    }

    /** 1.21.10 RenderType.field_29635: 1.21.11 RenderTypes.field_64062, read through method_76012  */
    public static java.util.function.Function<class_2960, class_1921> field_29635() {
        return texture -> class_12249.method_76012(texture);
    }

    /** 1.21.10 RenderType.field_29636: 1.21.11 RenderTypes.field_64063  */
    public static java.util.function.Function<class_2960, class_1921> field_29636() {
        return class_12249.field_64063;
    }

    /** 1.21.10 RenderType.field_20964: 1.21.11 RenderTypes.field_64064  */
    public static class_1921 field_20964() {
        return class_12249.field_64064;
    }

    /** 1.21.10 RenderType.field_20965: 1.21.11 RenderTypes.field_64065  */
    public static class_1921 field_20965() {
        return class_12249.field_64065;
    }

    /** 1.21.10 RenderType.field_24473: 1.21.11 RenderTypes.field_64066  */
    public static class_1921 field_24473() {
        return class_12249.field_64066;
    }

    /** 1.21.10 RenderType.field_26373: 1.21.11 RenderTypes.field_64015  */
    public static class_1921 field_26373() {
        return class_12249.field_64015;
    }

    /** 1.21.10 RenderType.field_20967: 1.21.11 RenderTypes.field_64016  */
    public static class_1921 field_20967() {
        return class_12249.field_64016;
    }

    /** 1.21.10 RenderType.field_20968: 1.21.11 RenderTypes.field_64017  */
    public static class_1921 field_20968() {
        return class_12249.field_64017;
    }

    /** 1.21.10 RenderType.field_29637: 1.21.11 RenderTypes.field_64018  */
    public static java.util.function.Function<class_2960, class_1921> field_29637() {
        return class_12249.field_64018;
    }

    /** 1.21.10 RenderType.field_29638: 1.21.11 RenderTypes.field_64019  */
    public static java.util.function.Function<class_2960, class_1921> field_29638() {
        return class_12249.field_64019;
    }

    /** 1.21.10 RenderType.field_42519: 1.21.11 RenderTypes.field_64020  */
    public static class_1921 field_42519() {
        return class_12249.field_64020;
    }

    /** 1.21.10 RenderType.field_33630: 1.21.11 RenderTypes.field_64021, read through method_76024  */
    public static java.util.function.Function<class_2960, class_1921> field_33630() {
        return texture -> class_12249.method_76024(texture);
    }

    /** 1.21.10 RenderType.field_34002: 1.21.11 RenderTypes.field_64022  */
    public static java.util.function.Function<class_2960, class_1921> field_34002() {
        return class_12249.field_64022;
    }

    /** 1.21.10 RenderType.field_34003: 1.21.11 RenderTypes.field_64023, read through method_76027  */
    public static java.util.function.Function<class_2960, class_1921> field_34003() {
        return texture -> class_12249.method_76027(texture);
    }

    /** 1.21.10 RenderType.field_29639: 1.21.11 RenderTypes.field_64024  */
    public static java.util.function.Function<class_2960, class_1921> field_29639() {
        return class_12249.field_64024;
    }

    /** 1.21.10 RenderType.field_42520: 1.21.11 RenderTypes.field_64025  */
    public static class_1921 field_42520() {
        return class_12249.field_64025;
    }

    /** 1.21.10 RenderType.field_33631: 1.21.11 RenderTypes.field_64026, read through method_76029  */
    public static java.util.function.Function<class_2960, class_1921> field_33631() {
        return texture -> class_12249.method_76029(texture);
    }

    /** 1.21.10 RenderType.field_20970: 1.21.11 RenderTypes.field_64027  */
    public static class_1921 field_20970() {
        return class_12249.field_64027;
    }

    /** 1.21.10 RenderType.field_52255: 1.21.11 RenderTypes.field_64028  */
    public static class_1921 field_52255() {
        return class_12249.field_64028;
    }

    /** 1.21.10 RenderType.field_52307: 1.21.11 RenderTypes.field_64029, read through method_76007 (via CbRenderTypes.method_61157)  */
    public static class_1921 field_52307() {
        return CbRenderTypes.method_61157();
    }

    /** 1.21.10 RenderType.field_25644: 1.21.11 RenderTypes.field_64030, read through method_76009  */
    public static class_1921 field_25644() {
        return class_12249.method_76009();
    }

    /** 1.21.10 RenderType.field_29457: 1.21.11 RenderTypes.field_64031  */
    public static class_1921 field_29457() {
        return class_12249.field_64031;
    }

    /** 1.21.10 RenderType.field_29458: 1.21.11 RenderTypes.field_64032  */
    public static class_1921 field_29458() {
        return class_12249.field_64032;
    }

    /** 1.21.10 RenderType.field_42522: 1.21.11 RenderTypes.field_64033  */
    public static class_1921 field_42522() {
        return class_12249.field_64033;
    }

    /** 1.21.10 RenderType.field_42523: 1.21.11 RenderTypes.field_64035  */
    public static class_1921 field_42523() {
        return class_12249.field_64035;
    }

    /** 1.21.10 RenderType.field_53131: 1.21.11 RenderTypes.field_64036  */
    public static class_1921 field_53131() {
        return class_12249.field_64036;
    }

    /** 1.21.10 RenderType.field_55143: 1.21.11 RenderTypes.field_64037, read through method_75992(texture, true)  */
    public static java.util.function.Function<class_2960, class_1921> field_55143() {
        return texture -> class_12249.method_75992(texture, true);
    }

    /** 1.21.10 RenderType.field_55144: 1.21.11 RenderTypes.field_64038, read through method_75992(texture, false)  */
    public static java.util.function.Function<class_2960, class_1921> field_55144() {
        return texture -> class_12249.method_75992(texture, false);
    }

    /** 1.21.10 RenderType.field_55150: 1.21.11 RenderTypes.field_64039  */
    public static java.util.function.Function<class_2960, class_1921> field_55150() {
        return class_12249.field_64039;
    }

    /** 1.21.10 RenderType.field_55151: 1.21.11 RenderTypes.field_64040  */
    public static java.util.function.Function<class_2960, class_1921> field_55151() {
        return class_12249.field_64040;
    }

    /**
     * 1.21.10 RenderType.createWeather(pipeline) helper (method_65230): a memoized "weather" type per texture on the
     * pipeline (javap -c -p 1.21.10: lambda method_65228 -- texture as Sampler0, weather target, lightmap). Built on the
     * public RenderSetup builder and RenderType.create: 1.21.11's private copy (RenderTypes.method_75970) is gone at 26.1.
     */
    public static java.util.function.Function<class_2960, class_1921> method_65230(com.mojang.blaze3d.pipeline.RenderPipeline pipeline) {
        return class_156.method_34866(texture -> class_1921.method_75940("weather", class_12247.method_75927(pipeline)
                .method_75934("Sampler0", texture)
                .method_75931(class_12246.field_63982)
                .method_75928()
                .method_75938()));
    }
}
