package top.dext.centurybridge.step.v1_21_11.facade;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

/**
 * netty-codec 4.1.118's io.netty.handler.codec.xml.XmlFrameDecoder (1.21.10), which netty 4.2 moved to netty-codec-xml, an
 * artifact 1.21.11 does not ship. It needs no XML library: it splits a byte stream into complete top-level XML documents by
 * counting brackets (comments, CDATA sections, processing instructions and leading whitespace handled as 4.1.118 did).
 * Bodies as 4.1.118 had them, read off its bytecode.
 */
public class NettyXmlFrameDecoder extends ByteToMessageDecoder {
    private final int maxFrameLength;

    public NettyXmlFrameDecoder(int maxFrameLength) {
        this.maxFrameLength = ObjectUtil.checkPositive(maxFrameLength, "maxFrameLength");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        boolean openingBracketFound = false;
        boolean atLeastOneXmlElementFound = false;
        boolean inCDATASection = false;
        long openBracketsCount = 0;
        int length = 0;
        int leadingWhiteSpaceCount = 0;
        final int bufferLength = in.writerIndex();
        if (bufferLength > maxFrameLength) {
            in.skipBytes(in.readableBytes());
            fail(bufferLength);
            return;
        }
        for (int i = in.readerIndex(); i < bufferLength; i++) {
            final byte readByte = in.getByte(i);
            if (!openingBracketFound && Character.isWhitespace(readByte)) {
                leadingWhiteSpaceCount++;
            } else if (!openingBracketFound && readByte != '<') {
                fail(ctx);
                in.skipBytes(in.readableBytes());
                return;
            } else if (!inCDATASection && readByte == '<') {
                openingBracketFound = true;
                if (i < bufferLength - 1) {
                    final byte peekAheadByte = in.getByte(i + 1);
                    if (peekAheadByte == '/') {
                        int peekFurtherAheadIndex = i + 2;
                        while (peekFurtherAheadIndex <= bufferLength - 1) {
                            if (in.getByte(peekFurtherAheadIndex) == '>') {
                                openBracketsCount--;
                                break;
                            }
                            peekFurtherAheadIndex++;
                        }
                    } else if (isValidStartCharForXmlElement(peekAheadByte)) {
                        atLeastOneXmlElementFound = true;
                        openBracketsCount++;
                    } else if (peekAheadByte == '!') {
                        if (isCommentBlockStart(in, i)) {
                            openBracketsCount++;
                        } else if (isCDATABlockStart(in, i)) {
                            openBracketsCount++;
                            inCDATASection = true;
                        }
                    } else if (peekAheadByte == '?') {
                        openBracketsCount++;
                    }
                }
            } else if (!inCDATASection && readByte == '/') {
                if (i < bufferLength - 1 && in.getByte(i + 1) == '>') {
                    openBracketsCount--;
                }
            } else if (readByte == '>') {
                length = i + 1;
                if (i - 1 > -1) {
                    final byte peekBehindByte = in.getByte(i - 1);
                    if (!inCDATASection) {
                        if (peekBehindByte == '?') {
                            openBracketsCount--;
                        } else if (peekBehindByte == '-' && i - 2 > -1 && in.getByte(i - 2) == '-') {
                            openBracketsCount--;
                        }
                    } else if (peekBehindByte == ']' && i - 2 > -1 && in.getByte(i - 2) == ']') {
                        openBracketsCount--;
                        inCDATASection = false;
                    }
                }
                if (atLeastOneXmlElementFound && openBracketsCount == 0) {
                    break;
                }
            }
        }
        final int readerIndex = in.readerIndex();
        int xmlElementLength = length - readerIndex;
        if (openBracketsCount == 0 && xmlElementLength > 0) {
            if (readerIndex + xmlElementLength >= bufferLength) {
                xmlElementLength = in.readableBytes();
            }
            final ByteBuf frame = extractFrame(in, readerIndex + leadingWhiteSpaceCount, xmlElementLength - leadingWhiteSpaceCount);
            in.skipBytes(xmlElementLength);
            out.add(frame);
        }
    }

    private void fail(long frameLength) {
        if (frameLength > 0) {
            throw new TooLongFrameException("frame length exceeds " + maxFrameLength + ": " + frameLength + " - discarded");
        } else {
            throw new TooLongFrameException("frame length exceeds " + maxFrameLength + " - discarding");
        }
    }

    private static void fail(ChannelHandlerContext ctx) {
        ctx.fireExceptionCaught(new CorruptedFrameException("frame contains content before the xml starts"));
    }

    private static ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
        return buffer.copy(index, length);
    }

    private static boolean isValidStartCharForXmlElement(final byte b) {
        return b >= 'a' && b <= 'z' || b >= 'A' && b <= 'Z' || b == ':' || b == '_';
    }

    private static boolean isCommentBlockStart(final ByteBuf in, final int i) {
        return i < in.writerIndex() - 3 && in.getByte(i + 2) == '-' && in.getByte(i + 3) == '-';
    }

    private static boolean isCDATABlockStart(final ByteBuf in, final int i) {
        return i < in.writerIndex() - 8 && in.getByte(i + 2) == '[' && in.getByte(i + 3) == 'C' && in.getByte(i + 4) == 'D'
            && in.getByte(i + 5) == 'A' && in.getByte(i + 6) == 'T' && in.getByte(i + 7) == 'A' && in.getByte(i + 8) == '[';
    }
}
