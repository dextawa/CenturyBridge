package top.dext.centurybridge.step.v1_20_5.facade;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import net.minecraft.class_2596;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * PacketFlowValidator (gone in 1.20.5, whose codecs only know their own protocol's packets): passes a packet
 * on only when the protocol in the channel attribute of its direction has its type, logging any other, and
 * swaps the protocol after a terminal packet -- 1.20.4's handler as it was, on rt/CbFinNet's attributes.
 */
public class PacketFlowValidator extends MessageToMessageCodec<class_2596<?>, class_2596<?>> {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /** receivingCodecKey */
    public final AttributeKey<ConnectionProtocolCodecData<?>> field_45680;
    /** sendingCodecKey */
    public final AttributeKey<ConnectionProtocolCodecData<?>> field_45681;

    public PacketFlowValidator(AttributeKey<ConnectionProtocolCodecData<?>> receivingCodecKey,
            AttributeKey<ConnectionProtocolCodecData<?>> sendingCodecKey) {
        this.field_45680 = receivingCodecKey;
        this.field_45681 = sendingCodecKey;
    }

    private static void cbValidate(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out,
            AttributeKey<ConnectionProtocolCodecData<?>> key) {
        Attribute<ConnectionProtocolCodecData<?>> attribute = ctx.channel().attr(key);
        ConnectionProtocolCodecData<?> data = attribute.get();
        if (!data.method_52927(packet)) {
            LOGGER.error("Unrecognized packet in pipeline {}:{} - {}", data.method_52922().method_10785(), data.method_52926(), packet);
        }
        ReferenceCountUtil.retain(packet);
        out.add(packet);
        CbFinNet.method_53011(attribute, packet);
    }

    /** decode(ctx, packet, out): validated against the receiving protocol */
    public void method_53008(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
        cbValidate(ctx, packet, out, this.field_45680);
    }

    /** encode(ctx, packet, out): validated against the sending protocol */
    public void method_53010(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
        cbValidate(ctx, packet, out, this.field_45681);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
        this.method_53008(ctx, packet, out);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
        this.method_53010(ctx, packet, out);
    }
}
