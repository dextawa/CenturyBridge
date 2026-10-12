package top.dext.centurybridge.compat.fapi.attachment.v1;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.function.Supplier;

/** Fabric API data attachment v1's AttachmentRegistry: creating and registering attachment types, as in 1.20.1's 0.92. */
public final class AttachmentRegistry {

    private AttachmentRegistry() {
    }

    public static <A> AttachmentType<A> create(net.minecraft.class_2960 id) {
        Objects.requireNonNull(id, "identifier cannot be null");
        return AttachmentRegistry.<A>builder().buildAndRegister(id);
    }

    public static <A> AttachmentType<A> createDefaulted(net.minecraft.class_2960 id, Supplier<A> initializer) {
        Objects.requireNonNull(id, "identifier cannot be null");
        Objects.requireNonNull(initializer, "initializer cannot be null");
        return AttachmentRegistry.<A>builder().initializer(initializer).buildAndRegister(id);
    }

    public static <A> AttachmentType<A> createPersistent(net.minecraft.class_2960 id, Codec<A> codec) {
        Objects.requireNonNull(id, "identifier cannot be null");
        Objects.requireNonNull(codec, "codec cannot be null");
        return AttachmentRegistry.<A>builder().persistent(codec).buildAndRegister(id);
    }

    public static <A> Builder<A> builder() {
        return top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentRegistryImpl.builder();
    }

    public interface Builder<A> {

        Builder<A> persistent(Codec<A> codec);

        Builder<A> copyOnDeath();

        Builder<A> initializer(Supplier<A> initializer);

        AttachmentType<A> buildAndRegister(net.minecraft.class_2960 id);
    }
}
