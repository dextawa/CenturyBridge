package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.WeightedListInt;
import top.dext.centurybridge.step.v26_1.facade.FloatProviderType;
import top.dext.centurybridge.step.v26_1.facade.IntProviderType;

/**
 * 1.21.11's int and float providers on 26.1's. 1.21.11's IntProvider / FloatProvider were abstract classes
 * answering an IntProviderType / FloatProviderType (an interface around a MapCodec, registered in
 * INT_PROVIDER_TYPE / FLOAT_PROVIDER_TYPE); 26.1's are interfaces answering codec(), its registries hold the
 * codecs, and the codecs and the codec(min, max) helpers moved to IntProviders / FloatProviders.
 * <ul>
 * <li>a type is canonical per codec: the type a mod registered for it (CbWallRegistry remembers it), else one
 *     made for the codec once -- so getType() of a provider, the constants and the values read back from the
 *     registries are the same object for the same codec;</li>
 * <li>getType() of any provider is the type of its codec(); a mod's getType() override answers codec() through
 *     the type's codec (overrideHooks);</li>
 * <li>registering a type registers its codec (CbWallRegistry); a codec read back from the registries meets a cast
 *     to the 1.21.11 type (checkCastRedirects), which answers its canonical type.</li>
 * </ul>
 */
public final class CbWallValueProviders {

    private static final Map<MapCodec<?>, IntProviderType<?>> INT_TYPES = new IdentityHashMap<>();
    private static final Map<MapCodec<?>, FloatProviderType<?>> FLOAT_TYPES = new IdentityHashMap<>();

    private CbWallValueProviders() {
    }

    /** a type made for a 26.1 codec no 1.21.11 code registered a type for */
    private record IntType<P extends IntProvider>(MapCodec<P> codec) implements IntProviderType<P> {
    }

    private record FloatType<P extends FloatProvider>(MapCodec<P> codec) implements FloatProviderType<P> {
    }

    /** the canonical IntProviderType of a codec */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static IntProviderType<?> intTypeOf(MapCodec<?> codec) {
        if (codec == null) {
            return null;
        }
        if (CbWallRegistry.wrapperOf(codec) instanceof IntProviderType<?> t) {
            return t;
        }
        synchronized (INT_TYPES) {
            return INT_TYPES.computeIfAbsent(codec, c -> new IntType(c));
        }
    }

    /** the canonical FloatProviderType of a codec */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static FloatProviderType<?> floatTypeOf(MapCodec<?> codec) {
        if (codec == null) {
            return null;
        }
        if (CbWallRegistry.wrapperOf(codec) instanceof FloatProviderType<?> t) {
            return t;
        }
        synchronized (FLOAT_TYPES) {
            return FLOAT_TYPES.computeIfAbsent(codec, c -> new FloatType(c));
        }
    }

    /** the 1.21.11 constant IntProviderType.&lt;id&gt;: the type of the codec 26.1 registers under the same id */
    public static IntProviderType<?> intType(String id) {
        return intTypeOf(BuiltInRegistries.INT_PROVIDER_TYPE.getValue(Identifier.withDefaultNamespace(id)));
    }

    /** the 1.21.11 constant FloatProviderType.&lt;id&gt; */
    public static FloatProviderType<?> floatType(String id) {
        return floatTypeOf(BuiltInRegistries.FLOAT_PROVIDER_TYPE.getValue(Identifier.withDefaultNamespace(id)));
    }

    /** 1.21.11 IntProviderType.register(name, codec): registered as 1.21.11 did, a type around the codec */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends IntProvider> IntProviderType<P> registerInt(String name, MapCodec<P> codec) {
        IntProviderType<P> type = new IntType<>(codec);
        return (IntProviderType<P>) CbWallRegistry.register((net.minecraft.core.Registry) BuiltInRegistries.INT_PROVIDER_TYPE, name, type);
    }

    /** 1.21.11 FloatProviderType.register(name, codec) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends FloatProvider> FloatProviderType<P> registerFloat(String name, MapCodec<P> codec) {
        FloatProviderType<P> type = new FloatType<>(codec);
        return (FloatProviderType<P>) CbWallRegistry.register((net.minecraft.core.Registry) BuiltInRegistries.FLOAT_PROVIDER_TYPE, name, type);
    }

    // ---- getType()

    /** 1.21.11 IntProvider.getType() of a provider: the type of its codec */
    public static IntProviderType<?> intGetType(IntProvider provider) {
        return intTypeOf(provider.codec());
    }

    /** 1.21.11 FloatProvider.getType() */
    public static FloatProviderType<?> floatGetType(FloatProvider provider) {
        return floatTypeOf(provider.codec());
    }

    // getType() called through a 1.21.11 provider class that declared its own (the call names that class)

    public static IntProviderType<?> intGetType(net.minecraft.util.valueproviders.ConstantInt provider) {
        return intGetType((IntProvider) provider);
    }

    public static IntProviderType<?> intGetType(net.minecraft.util.valueproviders.UniformInt provider) {
        return intGetType((IntProvider) provider);
    }

    public static IntProviderType<?> intGetType(net.minecraft.util.valueproviders.BiasedToBottomInt provider) {
        return intGetType((IntProvider) provider);
    }

    public static IntProviderType<?> intGetType(ClampedInt provider) {
        return intGetType((IntProvider) provider);
    }

    public static IntProviderType<?> intGetType(WeightedListInt provider) {
        return intGetType((IntProvider) provider);
    }

    public static IntProviderType<?> intGetType(net.minecraft.util.valueproviders.ClampedNormalInt provider) {
        return intGetType((IntProvider) provider);
    }

    public static FloatProviderType<?> floatGetType(net.minecraft.util.valueproviders.ConstantFloat provider) {
        return floatGetType((FloatProvider) provider);
    }

    public static FloatProviderType<?> floatGetType(net.minecraft.util.valueproviders.UniformFloat provider) {
        return floatGetType((FloatProvider) provider);
    }

    public static FloatProviderType<?> floatGetType(net.minecraft.util.valueproviders.ClampedNormalFloat provider) {
        return floatGetType((FloatProvider) provider);
    }

    public static FloatProviderType<?> floatGetType(net.minecraft.util.valueproviders.TrapezoidFloat provider) {
        return floatGetType((FloatProvider) provider);
    }

    /** codec() of a 1.21.11 getType() override: its type's codec */
    @SuppressWarnings("rawtypes")
    public static MapCodec intCodec(IntProviderType type) {
        return type == null ? null : type.codec();
    }

    /** codec() of a 1.21.11 FloatProvider getType() override */
    @SuppressWarnings("rawtypes")
    public static MapCodec floatCodec(FloatProviderType type) {
        return type == null ? null : type.codec();
    }

    /** a mod's super.getType() in a ClampedInt subclass: 1.21.11's ClampedInt answered CLAMPED */
    public static IntProviderType<?> clampedType(ClampedInt self) {
        return intTypeOf(ClampedInt.MAP_CODEC);
    }

    /** a mod's super.getType() in a WeightedListInt subclass: 1.21.11's WeightedListInt answered WEIGHTED_LIST */
    public static IntProviderType<?> weightedListType(WeightedListInt self) {
        return intTypeOf(WeightedListInt.MAP_CODEC);
    }

    // ---- read back from the registries

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean registered(net.minecraft.core.Registry registry, Object o) {
        return o instanceof MapCodec<?> c && registry.getKey(c) != null;
    }

    /** a cast to 1.21.11's IntProviderType */
    public static Object castIntType(Object o) {
        return registered(BuiltInRegistries.INT_PROVIDER_TYPE, o) ? intTypeOf((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's IntProviderType */
    public static boolean isIntType(Object o) {
        return o instanceof IntProviderType<?> || registered(BuiltInRegistries.INT_PROVIDER_TYPE, o);
    }

    /** a cast to 1.21.11's FloatProviderType */
    public static Object castFloatType(Object o) {
        return registered(BuiltInRegistries.FLOAT_PROVIDER_TYPE, o) ? floatTypeOf((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's FloatProviderType */
    public static boolean isFloatType(Object o) {
        return o instanceof FloatProviderType<?> || registered(BuiltInRegistries.FLOAT_PROVIDER_TYPE, o);
    }

    // ---- the statics 26.1 moved to IntProviders / FloatProviders

    /** IntProvider.CODEC */
    public static Codec<IntProvider> field_29946() {
        return IntProviders.CODEC;
    }

    /** IntProvider.NON_NEGATIVE_CODEC */
    public static Codec<IntProvider> field_33450() {
        return IntProviders.NON_NEGATIVE_CODEC;
    }

    /** IntProvider.POSITIVE_CODEC */
    public static Codec<IntProvider> field_33451() {
        return IntProviders.POSITIVE_CODEC;
    }

    /** FloatProvider.CODEC */
    public static Codec<FloatProvider> field_29007() {
        return FloatProviders.CODEC;
    }

    /** IntProvider.codec(min, max) */
    public static Codec<IntProvider> intCodec(int min, int max) {
        return IntProviders.codec(min, max);
    }

    /** IntProvider.validateCodec(min, max, codec) */
    public static <T extends IntProvider> Codec<T> validateCodec(int min, int max, Codec<T> codec) {
        return IntProviders.validateCodec(min, max, codec);
    }

    /** FloatProvider.codec(min, max) */
    public static Codec<FloatProvider> floatCodec(float min, float max) {
        return FloatProviders.codec(min, max);
    }
}
