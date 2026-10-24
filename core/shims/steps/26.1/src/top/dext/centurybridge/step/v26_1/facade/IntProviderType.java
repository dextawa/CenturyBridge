package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistry;
import top.dext.centurybridge.step.v26_1.rt.CbWallValueProviders;

/**
 * 1.21.11's IntProviderType (net/minecraft/class_6018): an interface around the MapCodec of an IntProvider kind,
 * registered in BuiltInRegistries.INT_PROVIDER_TYPE. 26.1 registers the MapCodec itself and asks a provider's
 * codec(). Kept an interface, as 1.21.11 had it, so a mod's lambda or class implementing it still does; it
 * registers as its codec (CbWallRegistry), and a codec read back from the registry is the type the mod
 * registered, else one made for the codec once (CbWallValueProviders.intType), so identity holds.
 */
public interface IntProviderType<P extends net.minecraft.util.valueproviders.IntProvider> extends CbWallRegistry.Wrapper {

    IntProviderType<?> field_29947 = CbWallValueProviders.intType("constant");
    IntProviderType<?> field_29948 = CbWallValueProviders.intType("uniform");
    IntProviderType<?> field_33452 = CbWallValueProviders.intType("biased_to_bottom");
    IntProviderType<?> field_33453 = CbWallValueProviders.intType("clamped");
    IntProviderType<?> field_35034 = CbWallValueProviders.intType("weighted_list");
    IntProviderType<?> field_35357 = CbWallValueProviders.intType("clamped_normal");

    MapCodec<P> codec();

    @Override
    default Object cb$unwrap() {
        return codec();
    }

    /** 1.21.11 IntProviderType.register(name, codec): the codec registered under the name, its type answered */
    static <P extends net.minecraft.util.valueproviders.IntProvider> IntProviderType<P> method_35014(String name, MapCodec<P> codec) {
        return CbWallValueProviders.registerInt(name, codec);
    }
}
