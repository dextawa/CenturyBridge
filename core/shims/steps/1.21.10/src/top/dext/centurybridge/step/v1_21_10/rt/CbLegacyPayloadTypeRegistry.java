package top.dext.centurybridge.step.v1_21_10.rt;

/**
 * 1.21.9's PayloadTypeRegistry as a lambda saw it: one abstract method, register(id, codec). A lambda or
 * method reference a 1.21.9 mod built as a registry is built as this interface instead (lambdaAdapters) and
 * handed on through {@link CbPayloadTypeRegistries#adapt}.
 */
@FunctionalInterface
public interface CbLegacyPayloadTypeRegistry<B extends net.minecraft.class_2540> {
    <T extends net.minecraft.class_8710> net.minecraft.class_8710.class_9155<? super B, T> register(
        net.minecraft.class_8710.class_9154<T> id, net.minecraft.class_9139<? super B, T> codec);
}
