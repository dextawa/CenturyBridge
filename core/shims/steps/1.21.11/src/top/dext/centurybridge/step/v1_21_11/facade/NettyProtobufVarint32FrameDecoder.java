package top.dext.centurybridge.step.v1_21_11.facade;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

/**
 * netty-codec 4.1.118's io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder (1.21.10), which netty 4.2 moved to
 * netty-codec-protobuf, an artifact 1.21.11 does not ship. It needs no protobuf class: it splits frames prefixed with a
 * base-128 varint length. Bodies as 4.1.118 had them (the 4-byte little-endian fast path included).
 */
public class NettyProtobufVarint32FrameDecoder extends ByteToMessageDecoder {
    public NettyProtobufVarint32FrameDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int preIndex = in.readerIndex();
        int length = readRawVarint32(in);
        if (preIndex == in.readerIndex()) {
            return;
        }
        if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
        }
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
        } else {
            out.add(in.readRetainedSlice(length));
        }
    }

    static int readRawVarint32(ByteBuf buffer) {
        if (buffer.readableBytes() < 4) {
            return readRawVarint24(buffer);
        }
        int wholeOrMore = buffer.getIntLE(buffer.readerIndex());
        int firstOneOnStop = ~wholeOrMore & 0x80808080;
        if (firstOneOnStop == 0) {
            return readRawVarint40(buffer, wholeOrMore);
        }
        int bitsToKeep = Integer.numberOfTrailingZeros(firstOneOnStop) + 1;
        buffer.skipBytes(bitsToKeep >> 3);
        int thisVarintMask = firstOneOnStop ^ (firstOneOnStop - 1);
        int wholeWithContinuations = wholeOrMore & thisVarintMask;
        wholeWithContinuations = (wholeWithContinuations & 0x7F007F) | ((wholeWithContinuations & 0x7F007F00) >> 1);
        return (wholeWithContinuations & 0x3FFF) | ((wholeWithContinuations & 0x3FFF0000) >> 2);
    }

    private static int readRawVarint40(ByteBuf buffer, int wholeOrMore) {
        byte lastByte;
        if (buffer.readableBytes() == 4 || (lastByte = buffer.getByte(buffer.readerIndex() + 4)) < 0) {
            throw new CorruptedFrameException("malformed varint.");
        }
        buffer.skipBytes(5);
        return wholeOrMore & 0x7F | ((wholeOrMore >> 8) & 0x7F) << 7 | ((wholeOrMore >> 16) & 0x7F) << 14
            | ((wholeOrMore >> 24) & 0x7F) << 21 | lastByte << 28;
    }

    private static int readRawVarint24(ByteBuf buffer) {
        if (!buffer.isReadable()) {
            return 0;
        }
        buffer.markReaderIndex();
        byte tmp = buffer.readByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7F;
        if (!buffer.isReadable()) {
            buffer.resetReaderIndex();
            return 0;
        }
        if ((tmp = buffer.readByte()) >= 0) {
            return result | tmp << 7;
        }
        result |= (tmp & 0x7F) << 7;
        if (!buffer.isReadable()) {
            buffer.resetReaderIndex();
            return 0;
        }
        if ((tmp = buffer.readByte()) >= 0) {
            return result | tmp << 14;
        }
        return result | (tmp & 0x7F) << 14;
    }
}
