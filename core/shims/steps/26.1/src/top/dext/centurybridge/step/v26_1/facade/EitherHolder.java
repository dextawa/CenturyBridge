package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

/**
 * 1.21.11's EitherHolder: a registry value given as a holder or as a key resolved when used. 26.1 dropped it --
 * its jukebox, instrument and trim material components hold resolved holders, and items name keys through
 * delayed components (Item.Properties.jukeboxPlayable(key), ...). 1.21.11's record, its codecs (a key encodes;
 * decoding one is an error, "Cannot parse as key without registry") and lookups; the components it stood in
 * convert through rt/CbWallEitherHolders. Members keep their intermediary names, the component field is public
 * (access wideners never reach this class).
 */
public final class EitherHolder<T> {

    public final Either<Holder<T>, ResourceKey<T>> comp_3636;

    public EitherHolder(Holder<T> holder) {
        this(Either.left(holder));
    }

    public EitherHolder(ResourceKey<T> key) {
        this(Either.right(key));
    }

    public EitherHolder(Either<Holder<T>, ResourceKey<T>> contents) {
        this.comp_3636 = contents;
    }

    /** codec(registryKey, holderCodec) */
    public static <T> Codec<EitherHolder<T>> method_60736(ResourceKey<Registry<T>> registryKey, Codec<Holder<T>> holderCodec) {
        return Codec.either(holderCodec,
                ResourceKey.codec(registryKey).<ResourceKey<T>>comapFlatMap(key -> DataResult.error(() -> "Cannot parse as key without registry"),
                    Function.identity()))
            .xmap(EitherHolder::new, EitherHolder::comp_3636);
    }

    /** streamCodec(registryKey, holderCodec) */
    public static <T> StreamCodec<RegistryFriendlyByteBuf, EitherHolder<T>> method_60737(ResourceKey<Registry<T>> registryKey,
        StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holderCodec) {
        return StreamCodec.composite(ByteBufCodecs.either(holderCodec, ResourceKey.streamCodec(registryKey)), EitherHolder::comp_3636, EitherHolder::new);
    }

    /** unwrap(registry) */
    public Optional<T> method_60740(Registry<T> registry) {
        return this.comp_3636.map(holder -> Optional.of(holder.value()), registry::getOptional);
    }

    /** unwrap(registries) */
    public Optional<Holder<T>> method_60739(HolderLookup.Provider registries) {
        return this.comp_3636.map(Optional::of, key -> registries.get(key).map(reference -> reference));
    }

    /** key() */
    public Optional<ResourceKey<T>> method_67385() {
        return this.comp_3636.map(Holder::unwrapKey, Optional::of);
    }

    /** contents() */
    public Either<Holder<T>, ResourceKey<T>> comp_3636() {
        return this.comp_3636;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EitherHolder<?> h && Objects.equals(this.comp_3636, h.comp_3636);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.comp_3636);
    }

    @Override
    public String toString() {
        return "class_9791[comp_3636=" + this.comp_3636 + "]";
    }
}
