package top.dext.centurybridge.step.v26_1.rt;

import java.util.function.IntSupplier;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.impl.item.DefaultItemComponentImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;

/**
 * Methods 26.1's Fabric API made abstract on interfaces a 1.21.11 mod class may implement, which that class
 * cannot declare (1.21.11 had no such method): each is answered in terms of the methods the class does
 * declare (abstractFills).
 */
public final class CbFapiFills {
    private CbFapiFills() {
    }

    /**
     * AttachmentRegistry.Builder.syncWith(codec, predicate, maxSyncSize): 26.1's own builder stores the size
     * after calling the two-argument syncWith; a 1.21.11 builder knows only that one, so it is what answers
     */
    public static <A> AttachmentRegistry.Builder<A> syncWith(AttachmentRegistry.Builder<A> builder,
            StreamCodec<? super RegistryFriendlyByteBuf, A> codec, AttachmentSyncPredicate predicate, int maxSyncSize) {
        return builder.syncWith(codec, predicate);
    }

    /**
     * PayloadTypeRegistry.registerLarge(type, codec, maxPacketSize supplier): a 1.21.11 registry takes the
     * size as an int, read here when the payload type is registered
     */
    public static <B extends FriendlyByteBuf, T extends CustomPacketPayload> CustomPacketPayload.TypeAndCodec<? super B, T> registerLarge(
            PayloadTypeRegistry<B> registry, CustomPacketPayload.Type<T> type, StreamCodec<? super B, T> codec, IntSupplier maxPacketSize) {
        return registry.registerLarge(type, codec, maxPacketSize.getAsInt());
    }

    /**
     * DefaultItemComponentEvents.ModifyContext.modify(predicate, ModifyConsumer): a 1.21.11 context
     * implements modify(predicate, BiConsumer), which 26.1's consumer is handed to; the consumer's
     * registry lookup is the one 26.1's Fabric API binds while it builds default item components (the only
     * time a context is given out), null outside that
     */
    public static void modify(DefaultItemComponentEvents.ModifyContext context, Predicate<Item> itemPredicate,
            DefaultItemComponentEvents.ModifyConsumer consumer) {
        context.modify(itemPredicate, legacyConsumer(consumer));
    }

    private static java.util.function.BiConsumer<net.minecraft.core.component.DataComponentMap.Builder, Item> legacyConsumer(
            DefaultItemComponentEvents.ModifyConsumer consumer) {
        return (builder, item) -> consumer.modify(builder, DefaultItemComponentImpl.LOOKUP_PROVIDER_SCOPED_VALUE.orElse(null), item);
    }

    /** 1.21.11's ModifyContext SAM, as a converted lambda implements it (lambdaAdapters) */
    @FunctionalInterface
    public interface LegacyModifyContext {
        void modify(Predicate<Item> itemPredicate,
                    java.util.function.BiConsumer<net.minecraft.core.component.DataComponentMap.Builder, Item> builderConsumer);
    }

    /** a 1.21.11 lambda ModifyContext: 26.1's abstract modify hands it the consumer as {@link #modify} does */
    public static DefaultItemComponentEvents.ModifyContext adapt(LegacyModifyContext legacy) {
        return (itemPredicate, consumer) -> legacy.modify(itemPredicate, legacyConsumer(consumer));
    }

    /**
     * TransferVariant.typeHolder() (26.1's TypedInstance): the holder of getObject() in the built-in registry
     * that holds it (26.1's item and fluid variants answer their object's built-in holder the same way)
     */
    @SuppressWarnings("unchecked")
    public static <O> Holder<O> typeHolder(TransferVariant<O> variant) {
        O object = variant.getObject();
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            Registry<O> typed = (Registry<O>) registry;
            if (typed.getResourceKey(object).isPresent()) { // an identity lookup: no cast of the object
                return typed.wrapAsHolder(object);
            }
        }
        // an object no built-in registry holds: the holder of an unregistered value
        return Holder.direct(object);
    }
}
