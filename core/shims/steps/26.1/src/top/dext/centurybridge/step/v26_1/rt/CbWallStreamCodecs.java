package top.dext.centurybridge.step.v26_1.rt;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Packet stream codecs whose buffer type 26.1 changed: 1.21.11's ClientboundSetEntityMotionPacket and
 * ServerboundInteractPacket codecs read a FriendlyByteBuf (26.1's a plain ByteBuf), ClientboundSetTimePacket's a
 * FriendlyByteBuf (26.1's a RegistryFriendlyByteBuf). The buffer type flows both ways through a codec: into its
 * decode / encode, and on into whatever a mod composes over it (dispatch hands the same buffer to the codecs
 * its function picks, apply / mapStream / cast build codecs over it). 1.21.11 code holds such a codec as one of
 * its own buffer type, so the codec it reads is a view that turns every buffer handed in into that type --
 * a plain ByteBuf wrapped as a FriendlyByteBuf, a FriendlyByteBuf without registries as a RegistryFriendlyByteBuf
 * over the registries in use -- before 26.1's codec or a composed one sees it; a buffer already of the type
 * passes as it is, and every codec composed over the view is a view too.
 */
public final class CbWallStreamCodecs {

    private CbWallStreamCodecs() {
    }

    /** a buffer as a FriendlyByteBuf (1.21.11's buffer type of the motion and interact packet codecs) */
    public static ByteBuf friendly(ByteBuf buf) {
        return buf == null || buf instanceof FriendlyByteBuf ? buf : new FriendlyByteBuf(buf);
    }

    /**
     * a buffer as a RegistryFriendlyByteBuf over the registries in use (26.1's set-time codec reads its clock
     * holders through them); with no registries loaded the buffer passes and 26.1's codec refuses it, as it would
     */
    public static ByteBuf registryFriendly(ByteBuf buf) {
        if (buf == null || buf instanceof RegistryFriendlyByteBuf) {
            return buf;
        }
        RegistryAccess access = CbWallRegistries.accessOrNull();
        return access == null ? buf : new RegistryFriendlyByteBuf(buf, access);
    }

    /** a codec every buffer handed to which is first converted, compositions included */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private record View<V>(StreamCodec inner, Function<ByteBuf, ByteBuf> convert) implements StreamCodec<ByteBuf, V> {

        @Override
        public V decode(ByteBuf buf) {
            return (V) this.inner.decode(this.convert.apply(buf));
        }

        @Override
        public void encode(ByteBuf buf, V value) {
            this.inner.encode(this.convert.apply(buf), value);
        }

        private <O> StreamCodec<ByteBuf, O> view(StreamCodec composed) {
            return new View<>(composed, this.convert);
        }

        @Override
        public <O> StreamCodec<ByteBuf, O> apply(StreamCodec.CodecOperation<ByteBuf, V, O> operation) {
            return view(StreamCodec.super.apply(operation));
        }

        @Override
        public <O> StreamCodec<ByteBuf, O> map(Function<? super V, ? extends O> to, Function<? super O, ? extends V> from) {
            return view(StreamCodec.super.map(to, from));
        }

        @Override
        public <O extends ByteBuf> StreamCodec<O, V> mapStream(Function<O, ? extends ByteBuf> function) {
            return (StreamCodec<O, V>) view(StreamCodec.super.mapStream(function));
        }

        @Override
        public <U> StreamCodec<ByteBuf, U> dispatch(Function<? super U, ? extends V> type,
                Function<? super V, ? extends StreamCodec<? super ByteBuf, ? extends U>> codec) {
            return view(StreamCodec.super.dispatch(type, codec));
        }

        @Override
        public <S extends ByteBuf> StreamCodec<S, V> cast() {
            return (StreamCodec<S, V>) this;
        }
    }

    /** a 26.1 codec as 1.21.11's FriendlyByteBuf codec */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static StreamCodec asFriendly(StreamCodec codec) {
        return codec == null || codec instanceof View ? codec : new View<>(codec, CbWallStreamCodecs::friendly);
    }

    /** the three views, made once (one codec object per field, as 1.21.11's constants were) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class Views {
        static final StreamCodec MOTION = asFriendly(net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket.STREAM_CODEC);
        static final StreamCodec INTERACT = asFriendly(net.minecraft.network.protocol.game.ServerboundInteractPacket.STREAM_CODEC);
        static final StreamCodec TIME = new View<>(net.minecraft.network.protocol.game.ClientboundSetTimePacket.STREAM_CODEC,
            CbWallStreamCodecs::registryFriendly);
    }

    /** ClientboundSetEntityMotionPacket.STREAM_CODEC */
    @SuppressWarnings("rawtypes")
    public static StreamCodec field_47981() {
        return Views.MOTION;
    }

    /** ServerboundInteractPacket.STREAM_CODEC */
    @SuppressWarnings("rawtypes")
    public static StreamCodec field_48195() {
        return Views.INTERACT;
    }

    /** ClientboundSetTimePacket.STREAM_CODEC */
    @SuppressWarnings("rawtypes")
    public static StreamCodec field_47991() {
        return Views.TIME;
    }
}
