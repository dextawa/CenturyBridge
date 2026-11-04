package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_2540;
import net.minecraft.class_2735;
import net.minecraft.class_9139;

/**
 * ClientboundSetHeldSlotPacket.STREAM_CODEC with 1.21.3's FriendlyByteBuf receiver.
 *
 * <p>W widened the native codec's receiver to ByteBuf. This one cached view declares V's receiver and delegates the
 * native wire coding as one of a supertype of that receiver, without a generic
 * cast. Consequently StreamCodec's default apply / map / mapStream / dispatch compositions also see FriendlyByteBuf,
 * and repeated old-field reads retain the old static constant's identity.</p>
 */
public final class CbS9NetStreamCodecs {

    private CbS9NetStreamCodecs() {
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

    private static final class SetHeldSlotView {
        static final class_9139<class_2540, class_2735> INSTANCE = new FriendlyView<>(class_2735.field_47974);
    }

    /** ClientboundSetHeldSlotPacket.STREAM_CODEC with 1.21.3's receiver and constant identity. */
    public static class_9139<class_2540, class_2735> field_47974() {
        return SetHeldSlotView.INSTANCE;
    }
}
