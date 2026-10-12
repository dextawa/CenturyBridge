package top.dext.centurybridge.step.v1_21_8.rt;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * 1.21.8's Fabric API (networking 5.1.0) gave PayloadTypeRegistry an abstract registerLarge(id, codec,
 * maxPayloadSize): register a payload type "split into multiple packets, allowing to send packets larger
 * than vanilla limited size" (javadoc; abstract, no default). Fabric's own registries register the type and
 * record the size for their packet splitter, which serves only them. 1.21.7's Fabric API (5.0.1) had no
 * splitting at all -- every payload kept vanilla's size limit -- and a registry written against it has only
 * register. The registerLarge given to it validates the size as Fabric's does and registers the type the one
 * way that registry can: with 1.21.7's limits, which is what such a payload had in 1.21.7.
 *
 * 1.21.7's interface, with register alone, was functional: a method reference a 1.21.7 mod made as a
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

    /** a 1.21.7 registry lambda as 1.21.8's registry */
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
