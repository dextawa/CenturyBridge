package top.dext.centurybridge.step.v1_21_10.rt;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * 1.21.10's Fabric API (networking 5.1.0) has PayloadTypeRegistry.registerLarge(id, codec, maxPayloadSize),
 * abstract: register a payload type "split into multiple packets, allowing to send packets larger than
 * vanilla limited size" (javadoc; no default). 1.21.9's Fabric API (5.0.13) had neither the method nor any
 * payload splitting -- every payload kept vanilla's size limit -- and a registry written against it has only
 * register. The registerLarge given to it validates the size as Fabric's does and registers the type the one
 * way that registry can: with 1.21.9's limits, which is what such a payload had in 1.21.9. Fabric's own
 * registries record the size for their packet splitter, which serves only them.
 *
 * 1.21.9's interface, with register alone, was functional: a method reference a 1.21.9 mod made as a
 * registry is built as {@link CbLegacyPayloadTypeRegistry} and adapted ({@link #adapt}).
 */
public final class CbPayloadTypeRegistries {

    private CbPayloadTypeRegistries() {
    }

    public static <B extends net.minecraft.class_2540, T extends net.minecraft.class_8710> net.minecraft.class_8710.class_9155<? super B, T>
            registerLarge(PayloadTypeRegistry<B> registry, net.minecraft.class_8710.class_9154<T> id,
                          net.minecraft.class_9139<? super B, T> codec, int maxPayloadSize) {
        if (maxPayloadSize < 0) {
            throw new IllegalArgumentException("Provided maxPayloadSize needs to be positive!");
        }
        return registry.register(id, codec);
    }

    public static <B extends net.minecraft.class_2540> PayloadTypeRegistry<B> adapt(CbLegacyPayloadTypeRegistry<B> legacy) {
        return new Adapted<>(legacy);
    }

    /** a 1.21.9 registry lambda as 1.21.10's registry */
    static final class Adapted<B extends net.minecraft.class_2540> implements PayloadTypeRegistry<B> {
        private final CbLegacyPayloadTypeRegistry<B> legacy;

        Adapted(CbLegacyPayloadTypeRegistry<B> legacy) {
            this.legacy = legacy;
        }

        @Override
        public <T extends net.minecraft.class_8710> net.minecraft.class_8710.class_9155<? super B, T> register(
                net.minecraft.class_8710.class_9154<T> id, net.minecraft.class_9139<? super B, T> codec) {
            return legacy.register(id, codec);
        }

        @Override
        public <T extends net.minecraft.class_8710> net.minecraft.class_8710.class_9155<? super B, T> registerLarge(
                net.minecraft.class_8710.class_9154<T> id, net.minecraft.class_9139<? super B, T> codec, int maxPayloadSize) {
            return CbPayloadTypeRegistries.registerLarge(this, id, codec, maxPayloadSize);
        }
    }
}
