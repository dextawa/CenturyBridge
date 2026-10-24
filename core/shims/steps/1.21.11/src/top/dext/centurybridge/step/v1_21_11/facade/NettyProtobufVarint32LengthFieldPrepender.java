package top.dext.centurybridge.step.v1_21_11.facade;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * netty-codec 4.1.118's io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender (1.21.10), which netty 4.2
 * moved to netty-codec-protobuf, an artifact 1.21.11 does not ship. It needs no protobuf class: it prefixes each message
 * with its length as a base-128 varint. Bodies as 4.1.118 had them.
 */
public class NettyProtobufVarint32LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
    public NettyProtobufVarint32LengthFieldPrepender() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int bodyLen = msg.readableBytes();
        int headerLen = computeRawVarint32Size(bodyLen);
        out.ensureWritable(headerLen + bodyLen);
        writeRawVarint32(out, bodyLen);
        out.writeBytes(msg, msg.readerIndex(), bodyLen);
    }

    static void writeRawVarint32(ByteBuf out, int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.writeByte(value);
                return;
            }
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }

    static int computeRawVarint32Size(int value) {
        if ((value & (0xffffffff << 7)) == 0) {
            return 1;
        }
        if ((value & (0xffffffff << 14)) == 0) {
            return 2;
        }
        if ((value & (0xffffffff << 21)) == 0) {
            return 3;
        }
        if ((value & (0xffffffff << 28)) == 0) {
            return 4;
        }
        return 5;
    }
}
