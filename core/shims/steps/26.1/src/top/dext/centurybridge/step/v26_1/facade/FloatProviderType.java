package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistry;
import top.dext.centurybridge.step.v26_1.rt.CbWallValueProviders;

/**
 * 1.21.11's FloatProviderType (net/minecraft/class_5864): an interface around the MapCodec of a FloatProvider
 * kind, registered in BuiltInRegistries.FLOAT_PROVIDER_TYPE, which 26.1 makes a registry of the codecs. As
 * {@link IntProviderType}.
 */
public interface FloatProviderType<P extends net.minecraft.util.valueproviders.FloatProvider> extends CbWallRegistry.Wrapper {

    FloatProviderType<?> field_29008 = CbWallValueProviders.floatType("constant");
    FloatProviderType<?> field_29009 = CbWallValueProviders.floatType("uniform");
    FloatProviderType<?> field_29010 = CbWallValueProviders.floatType("clamped_normal");
    FloatProviderType<?> field_29011 = CbWallValueProviders.floatType("trapezoid");

    MapCodec<P> codec();

    @Override
    default Object cb$unwrap() {
        return codec();
    }

    /** 1.21.11 FloatProviderType.register(name, codec) */
    static <P extends net.minecraft.util.valueproviders.FloatProvider> FloatProviderType<P> method_33925(String name, MapCodec<P> codec) {
        return CbWallValueProviders.registerFloat(name, codec);
    }
}
