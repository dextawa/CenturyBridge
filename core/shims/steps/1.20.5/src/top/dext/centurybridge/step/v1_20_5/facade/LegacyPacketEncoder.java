package top.dext.centurybridge.step.v1_20_5.facade;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import net.minecraft.class_2535;
import net.minecraft.class_2540;
import net.minecraft.class_2545;
import net.minecraft.class_2547;
import net.minecraft.class_2548;
import net.minecraft.class_2596;
import net.minecraft.class_6611;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * 1.20.4's PacketEncoder(attributeKey): an encoder that reads its protocol from a channel attribute for every
 * packet (rt/CbFinNet). A subclass of 1.20.5's PacketEncoder (ctorRedirects, superRenames). 1.20.4's body:
 * the packet's wire id, then its fields as the protocol writes them.
 */
public class LegacyPacketEncoder extends class_2545<class_2547> {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /** protocolKey */
    public final AttributeKey<ConnectionProtocolCodecData<?>> field_45678;

    public LegacyPacketEncoder(AttributeKey<ConnectionProtocolCodecData<?>> protocolKey) {
        super(CbFinNet.cbAttributeProtocol());
        this.field_45678 = protocolKey;
    }

    @Override
    protected void method_10838(ChannelHandlerContext ctx, class_2596<class_2547> packet, ByteBuf out) throws Exception {
        Attribute<ConnectionProtocolCodecData<?>> attribute = ctx.channel().attr(this.field_45678);
        ConnectionProtocolCodecData<?> data = attribute.get();
        if (data == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + packet);
        }
        int id = data.method_52925(packet);
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(class_2535.field_36380, "OUT: [{}:{}] {}", data.method_52922().method_10785(), id, packet.getClass().getName());
            }
            if (id == -1) {
                throw new IOException("Can't serialize unregistered packet");
            }
            class_2540 buf = new class_2540(out);
            buf.method_10804(id);
            int start = buf.writerIndex();
            CbFinNet.cbEncodeFields(packet, buf);
            int size = buf.writerIndex() - start;
            if (size > 8388608) {
                throw new IllegalArgumentException("Packet too big (is " + size + ", should be less than 8388608): " + packet);
            }
            class_6611.field_34923.method_38657(data.method_52922(), packet.method_55846(), ctx.channel().remoteAddress(), size);
        } catch (Throwable t) {
            LOGGER.error("Error receiving packet {}", id, t);
            if (packet.method_11051()) {
                throw new class_2548(t);
            }
            throw t;
        } finally {
            CbFinNet.method_53011(attribute, packet);
        }
    }
}
