package top.dext.centurybridge.step.v1_21_6.rt;

import net.minecraft.class_2540;
import net.minecraft.class_2632;
import net.minecraft.class_2909;
import net.minecraft.class_2924;
import net.minecraft.class_4210;
import net.minecraft.class_9129;
import net.minecraft.class_9135;
import net.minecraft.class_9139;

/**
 * The packet codec constants whose receiver widened from 1.21.5's FriendlyByteBuf to a plain ByteBuf in 1.21.6.
 * A view declares the old receiver again while delegating to the native codec as one of a supertype of that receiver;
 * no generic cast is involved. StreamCodec's default compositions then see the old receiver too, rather than exposing
 * the native codec's wider type through apply / map / mapStream / dispatch. Each constant has one view, so repeated
 * field reads retain the identity of the old static constants and all writes go to the native codec's buffer.
 */
public final class CbS6NetStreamCodecs {

    private CbS6NetStreamCodecs() {
    }

    private record FriendlyView<V>(class_9139<? super class_2540, V> nativeCodec)
            implements class_9139<class_2540, V> {

        @Override
        public V decode(class_2540 buf) {
            return this.nativeCodec.decode(buf);
        }

        @Override
        public void encode(class_2540 buf, V value) {
            this.nativeCodec.encode(buf, value);
        }
    }

    private static final class ChangeDifficultyView {
        static final class_9139<class_2540, class_2632> INSTANCE = new FriendlyView<>(class_2632.field_47907);
    }

    private static final class LoginDisconnectView {
        static final class_9139<class_2540, class_2909> INSTANCE = new FriendlyView<>(class_2909.field_48237);
    }

    private static final class StatusResponseView {
        static final class_9139<class_2540, class_2924> INSTANCE = new FriendlyView<>(class_2924.field_48258);
    }

    private static final class ServerDifficultyView {
        static final class_9139<class_2540, class_4210> INSTANCE = new FriendlyView<>(class_4210.field_48176);
    }

    /** ClientboundChangeDifficultyPacket.STREAM_CODEC with 1.21.5's receiver */
    public static class_9139<class_2540, class_2632> field_47907() {
        return ChangeDifficultyView.INSTANCE;
    }

    /** ClientboundLoginDisconnectPacket.STREAM_CODEC with 1.21.5's receiver */
    public static class_9139<class_2540, class_2909> field_48237() {
        return LoginDisconnectView.INSTANCE;
    }

    /** ClientboundStatusResponsePacket.STREAM_CODEC with 1.21.5's receiver */
    public static class_9139<class_2540, class_2924> field_48258() {
        return StatusResponseView.INSTANCE;
    }

    /** ServerboundChangeDifficultyPacket.STREAM_CODEC with 1.21.5's receiver */
    public static class_9139<class_2540, class_4210> field_48176() {
        return ServerDifficultyView.INSTANCE;
    }

    /**
     * 1.21.5 ByteBufCodecs.method_68864: a length-delimited codec over RegistryFriendlyByteBuf. 1.21.6 kept that exact
     * operation as method_72187 (its method_68864 is the new plain-ByteBuf variant), including its registry-preserving
     * slice wrappers and size checks, so the old call goes to the native registry operation rather than adapting the
     * plain one or casting its type arguments.
     */
    public static <V> class_9139.class_9140<class_9129, V, V> registryLengthDelimited(int maxBytes) {
        return class_9135.method_72187(maxBytes);
    }
}
