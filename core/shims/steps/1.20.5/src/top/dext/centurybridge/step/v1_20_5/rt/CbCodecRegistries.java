package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The six registries 1.20.5 turned from codecs into map codecs -- BIOME_SOURCE, CHUNK_GENERATOR,
 * MATERIAL_CONDITION, MATERIAL_RULE, DENSITY_FUNCTION_TYPE, POOL_ALIAS_BINDING_TYPE -- as 1.20.4 code
 * holds them: a registry of codecs. Each is mirrored by a registry of its own ({@link Mirror}) holding,
 * id for id under the same keys, the codec CbCodecs makes of each map codec (identity-preserving both
 * ways, so a codec handed in comes back out as itself). A 1.20.4 registration into the mirror registers
 * the map codec in the real registry, and every registration into the real one (vanilla's, a 1.20.5
 * mod's, the mirror's own) is mirrored at once (mixin/CbCodecRegistryMixin). The registries' keys are the
 * same objects in both releases; 1.20.4 code reaches the mirror through the BuiltInRegistries fields and
 * through RegistryAccess / HolderLookup.Provider lookups of those keys, whose answers are converted here.
 */
public final class CbCodecRegistries {

    /** the mirror of each real registry, by identity */
    private static final Map<net.minecraft.class_2378<?>, Mirror<?>> BY_REAL = new IdentityHashMap<>();

    /** each mirror by its lookup (MappedRegistry keeps one lookup object) */
    private static final Map<net.minecraft.class_7225.class_7226<?>, Mirror<?>> BY_LOOKUP = new IdentityHashMap<>();

    /** a registry of codecs standing for a 1.20.5 registry of map codecs */
    public static final class Mirror<T> extends net.minecraft.class_2370<Codec<? extends T>> {

        final net.minecraft.class_2378<MapCodec<? extends T>> real;

        @SuppressWarnings({"unchecked", "rawtypes"})
        Mirror(net.minecraft.class_2378<MapCodec<? extends T>> real) {
            super((net.minecraft.class_5321) real.method_30517(), real.method_31138());
            this.real = real;
            for (int id = 0; id < real.method_10204(); id++) {
                MapCodec<? extends T> value = real.method_10200(id);
                net.minecraft.class_5321 key = real.method_29113(value).orElseThrow();
                mirror(key, value, (net.minecraft.class_9248) real.method_57058(key).orElse(net.minecraft.class_9248.field_49136));
            }
        }

        /**
         * A 1.20.4 registration (Registry.register on the registry of codecs): the map codec of the codec
         * goes into the real registry -- which fails as 1.20.4's did once the registry is frozen -- and
         * the mirror takes it from there.
         */
        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public net.minecraft.class_6880.class_6883<Codec<? extends T>> method_10272(
                net.minecraft.class_5321<Codec<? extends T>> key, Codec<? extends T> value, net.minecraft.class_9248 info) {
            ((net.minecraft.class_2385) real).method_10272((net.minecraft.class_5321) key, CbCodecs.toMapCodec(value), info);
            return method_40290(key);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        void mirror(net.minecraft.class_5321 key, Object value, net.minecraft.class_9248 info) {
            super.method_10272(key, CbCodecs.fromMapCodec((MapCodec) value), info);
        }

        /** the real registry of map codecs */
        public net.minecraft.class_2378<MapCodec<? extends T>> real() {
            return real;
        }
    }

    /** the mirror of one of the six registries (created at first use, with every entry so far) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static synchronized <T> Mirror<T> mirrorOf(net.minecraft.class_2378 real) {
        Mirror<T> m = (Mirror<T>) BY_REAL.get(real);
        if (m == null) {
            m = new Mirror<>(real);
            BY_REAL.put(real, m);
            BY_LOOKUP.put(m.method_46771(), m);
        }
        return m;
    }

    /** mixin/CbCodecRegistryMixin: something was registered in a registry; mirrored when it is one of the six */
    public static void registered(net.minecraft.class_2378<?> registry, net.minecraft.class_5321<?> key, Object value,
                                  net.minecraft.class_9248 info) {
        Mirror<?> m;
        synchronized (CbCodecRegistries.class) {
            m = BY_REAL.get(registry);
        }
        if (m != null) {
            m.mirror(key, value, info);
        }
    }

    private static boolean isCodecRegistry(net.minecraft.class_5321<?> key) {
        return key == net.minecraft.class_7924.field_41253 || key == net.minecraft.class_7924.field_41260
            || key == net.minecraft.class_7924.field_41204 || key == net.minecraft.class_7924.field_41205
            || key == net.minecraft.class_7924.field_41264 || key == net.minecraft.class_7924.field_46913;
    }

    // ---------------------------------------------------------------- the BuiltInRegistries fields

    /** BuiltInRegistries.BIOME_SOURCE */
    public static net.minecraft.class_2378<?> biomeSource() {
        return mirrorOf(net.minecraft.class_7923.field_41156);
    }

    /** BuiltInRegistries.CHUNK_GENERATOR */
    public static net.minecraft.class_2378<?> chunkGenerator() {
        return mirrorOf(net.minecraft.class_7923.field_41157);
    }

    /** BuiltInRegistries.MATERIAL_CONDITION */
    public static net.minecraft.class_2378<?> materialCondition() {
        return mirrorOf(net.minecraft.class_7923.field_41158);
    }

    /** BuiltInRegistries.MATERIAL_RULE */
    public static net.minecraft.class_2378<?> materialRule() {
        return mirrorOf(net.minecraft.class_7923.field_41159);
    }

    /** BuiltInRegistries.DENSITY_FUNCTION_TYPE */
    public static net.minecraft.class_2378<?> densityFunctionType() {
        return mirrorOf(net.minecraft.class_7923.field_41160);
    }

    /** BuiltInRegistries.POOL_ALIAS_BINDING_TYPE */
    public static net.minecraft.class_2378<?> poolAliasBindingType() {
        return mirrorOf(net.minecraft.class_7923.field_46912);
    }

    // ---------------------------------------------------------------- lookups by key

    /**
     * A registry 1.20.4 code fetched by key (RegistryAccess.registryOrThrow): one of the six as its mirror,
     * any other as it is.
     */
    public static net.minecraft.class_2378<?> view(net.minecraft.class_2378<?> registry) {
        if (registry == null || registry instanceof Mirror || !isCodecRegistry(registry.method_30517())) {
            return registry;
        }
        return mirrorOf(registry);
    }

    /** the reverse of {@link #view}: a registry 1.20.4 code answers (a mod's RegistryAccess) as 1.20.5's */
    public static net.minecraft.class_2378<?> real(net.minecraft.class_2378<?> registry) {
        return registry instanceof Mirror<?> m ? m.real() : registry;
    }

    /** RegistryAccess.registry(key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional viewOptional(Optional registry) {
        return registry == null ? null : registry.map(r -> view((net.minecraft.class_2378<?>) r));
    }

    /** the reverse of {@link #viewOptional} */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional realOptional(Optional registry) {
        return registry == null ? null : registry.map(r -> real((net.minecraft.class_2378<?>) r));
    }

    /**
     * A registry lookup 1.20.4 code fetched by key (HolderLookup.Provider.lookupOrThrow): for one of the six
     * built-in registries, the mirror's lookup.
     */
    public static net.minecraft.class_7225.class_7226<?> viewLookup(net.minecraft.class_7225.class_7226<?> lookup) {
        if (lookup == null || !isCodecRegistry(lookup.method_46765())) {
            return lookup;
        }
        net.minecraft.class_2378<?> real = net.minecraft.class_7923.field_41167.method_10223(lookup.method_46765().method_29177());
        return real == null ? lookup : mirrorOf(real).method_46771();
    }

    /** the reverse of {@link #viewLookup}: a mirror's lookup as its real registry's */
    public static net.minecraft.class_7225.class_7226<?> realLookup(net.minecraft.class_7225.class_7226<?> lookup) {
        Mirror<?> m;
        synchronized (CbCodecRegistries.class) {
            m = BY_LOOKUP.get(lookup);
        }
        return m != null ? m.real().method_46771() : lookup;
    }

    /** HolderLookup.Provider.lookup(key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional viewLookupOptional(Optional lookup) {
        return lookup == null ? null : lookup.map(l -> viewLookup((net.minecraft.class_7225.class_7226<?>) l));
    }

    /** the reverse of {@link #viewLookupOptional} */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional realLookupOptional(Optional lookup) {
        return lookup == null ? null : lookup.map(l -> realLookup((net.minecraft.class_7225.class_7226<?>) l));
    }

    private CbCodecRegistries() {
    }
}
