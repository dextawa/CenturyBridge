package top.dext.centurybridge.step.v1_20_5.facade;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import java.util.List;
import net.minecraft.class_2535;
import net.minecraft.class_2540;
import net.minecraft.class_2543;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_6611;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * 1.20.4's PacketDecoder(attributeKey): a decoder that reads its protocol from a channel attribute for every
 * packet (rt/CbFinNet). A subclass of 1.20.5's PacketDecoder, so it stands where 1.20.5 expects one (new sites:
 * ctorRedirects; a 1.20.4 subclass extends it: superRenames). 1.20.4's body; packet ids are 1.20.5's wire ids.
 */
public class LegacyPacketDecoder extends class_2543<class_2547> {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /** protocolKey */
    public final AttributeKey<ConnectionProtocolCodecData<?>> field_45677;

    public LegacyPacketDecoder(AttributeKey<ConnectionProtocolCodecData<?>> protocolKey) {
        super(CbFinNet.cbAttributeProtocol());
        this.field_45677 = protocolKey;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int bytes = in.readableBytes();
        if (bytes == 0) {
            return;
        }
        Attribute<ConnectionProtocolCodecData<?>> attribute = ctx.channel().attr(this.field_45677);
        ConnectionProtocolCodecData<?> data = attribute.get();
        class_2540 buf = new class_2540(in);
        int id = buf.method_10816();
        class_2596<?> packet = data.method_52923(id, buf);
        if (packet == null) {
            throw new IOException("Bad packet id " + id);
        }
        class_6611.field_34923.method_38656(data.method_52922(), packet.method_55846(), ctx.channel().remoteAddress(), bytes);
        if (buf.readableBytes() > 0) {
            throw new IOException("Packet " + data.method_52922().method_10785() + "/" + id + " (" + packet.getClass().getSimpleName()
                + ") was larger than I expected, found " + buf.readableBytes() + " bytes extra whilst reading packet " + id);
        }
        out.add(packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(class_2535.field_36379, " IN: [{}:{}] {}", data.method_52922().method_10785(), id, packet.getClass().getName());
        }
        CbFinNet.method_53011(attribute, packet);
    }
}
