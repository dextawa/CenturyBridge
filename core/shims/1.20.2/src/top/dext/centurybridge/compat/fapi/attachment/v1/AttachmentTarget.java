package top.dext.centurybridge.compat.fapi.attachment.v1;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Something data can be attached to: Fabric API data attachment v1's AttachmentTarget, as 1.20.1's
 * 0.92 defined it. Entities, block entities, worlds and chunks implement it through this layer's
 * mixins (impl/attachment/AttachmentTargetImpl), as Fabric API's mixins made them in 1.20.1; the
 * defaults here are 1.20.1's, those the mixins implement throw for any other implementor as 1.20.1's did.
 */
public interface AttachmentTarget {

    String NBT_ATTACHMENT_KEY = "fabric:attachments";

    default <A> A getAttached(AttachmentType<A> type) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    default <A> A getAttachedOrThrow(AttachmentType<A> type) {
        return Objects.requireNonNull(getAttached(type), "No value was attached");
    }

    default <A> A getAttachedOrSet(AttachmentType<A> type, A defaultValue) {
        Objects.requireNonNull(defaultValue, "default value cannot be null");
        A current = getAttached(type);
        if (current != null) {
            return current;
        }
        setAttached(type, defaultValue);
        return defaultValue;
    }

    default <A> A getAttachedOrCreate(AttachmentType<A> type, Supplier<A> initializer) {
        A current = getAttached(type);
        if (current != null) {
            return current;
        }
        A initialized = Objects.requireNonNull(initializer.get(), "initializer result cannot be null");
        setAttached(type, initialized);
        return initialized;
    }

    default <A> A getAttachedOrCreate(AttachmentType<A> type) {
        Supplier<A> initializer = type.initializer();
        if (initializer == null) {
            throw new IllegalArgumentException("Single-argument getAttachedOrCreate is reserved for attachment types with default initializers");
        }
        return getAttachedOrCreate(type, initializer);
    }

    default <A> A getAttachedOrElse(AttachmentType<A> type, A defaultValue) {
        A current = getAttached(type);
        return current == null ? defaultValue : current;
    }

    default <A> A getAttachedOrGet(AttachmentType<A> type, Supplier<A> defaultValueSupplier) {
        Objects.requireNonNull(defaultValueSupplier, "default value supplier cannot be null");
        A current = getAttached(type);
        return current == null ? defaultValueSupplier.get() : current;
    }

    default <A> A setAttached(AttachmentType<A> type, A value) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    default boolean hasAttached(AttachmentType<?> type) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    default <A> A removeAttached(AttachmentType<A> type) {
        return setAttached(type, null);
    }

    default <A> A modifyAttached(AttachmentType<A> type, UnaryOperator<A> modifier) {
        return setAttached(type, modifier.apply(getAttached(type)));
    }
}
