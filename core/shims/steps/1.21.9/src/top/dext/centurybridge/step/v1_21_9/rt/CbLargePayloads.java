package top.dext.centurybridge.step.v1_21_9.rt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.class_2539;
import net.minecraft.class_2543;
import net.minecraft.class_2545;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_2658;
import net.minecraft.class_2817;
import net.minecraft.class_2960;
import net.minecraft.class_8703;
import net.minecraft.class_8710;
import net.minecraft.class_9127;
import net.minecraft.class_9130;
import net.minecraft.class_9139;

/**
 * Fabric API 0.136's (1.21.8) PayloadTypeRegistry.registerLarge, which Fabric API for 1.21.9 (0.134) does not have:
 * a payload type registered with a maximum size above what one custom payload packet may carry is split into
 * fabric:split slices on the way out and merged back on the way in, with 0.136's sizes, wire form and checks
 * (FabricPacketSplitter / FabricPacketMerger). The type itself is registered with 0.134's register; this class keeps
 * 0.136's per-registry table of large types, and CbLargePayloadMixins put the splitter after the encoder and the merger
 * after the decoder of a connection only for a protocol whose table has an entry, so a connection no converted mod
 * registered a large payload for is 1.21.9's.
 */
public final class CbLargePayloads {

    private CbLargePayloads() {
    }

    /** 0.136's FabricPacketSplitter.SAFE_S2C_SPLIT_SIZE / SAFE_C2S_SPLIT_SIZE (the registries' minimal splittable size) */
    public static final int SAFE_S2C_SPLIT_SIZE = 1048576;
    public static final int SAFE_C2S_SPLIT_SIZE = 32767;

    /** large types per registry: configuration C2S, configuration S2C, play C2S, play S2C */
    private static final Object2IntMap<class_2960>[] MAX_SIZES = newTables();

    @SuppressWarnings("unchecked")
    private static Object2IntMap<class_2960>[] newTables() {
        Object2IntMap<class_2960>[] tables = new Object2IntMap[4];
        for (int i = 0; i < 4; i++) {
            tables[i] = new Object2IntOpenHashMap<>();
            tables[i].defaultReturnValue(-1);
        }
        return tables;
    }

    private static final boolean[] SPLIT_REGISTERED = new boolean[4];

    private static int slot(boolean play, boolean clientbound) {
        return (play ? 2 : 0) + (clientbound ? 1 : 0);
    }

    private static int slot(PayloadTypeRegistry<?> registry) {
        if (registry == PayloadTypeRegistry.configurationC2S()) {
            return 0;
        }
        if (registry == PayloadTypeRegistry.configurationS2C()) {
            return 1;
        }
        if (registry == PayloadTypeRegistry.playC2S()) {
            return 2;
        }
        if (registry == PayloadTypeRegistry.playS2C()) {
            return 3;
        }
        throw new IllegalArgumentException("Unknown payload type registry " + registry);
    }

    /** the table of a connection protocol, or -1 for a phase without custom payloads */
    private static int slot(class_9127<?> protocol) {
        class_2539 phase = protocol.comp_2234();
        boolean clientbound = protocol.comp_2235() == class_2598.field_11942;
        if (phase == class_2539.field_20591) {
            return slot(true, clientbound);
        }
        if (phase == class_2539.field_45671) {
            return slot(false, clientbound);
        }
        return -1;
    }

    /** PayloadTypeRegistry.registerLarge(id, codec, maxPayloadSize), 0.136's body */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized <T extends class_8710> class_8710.class_9155 registerLarge(PayloadTypeRegistry registry, class_8710.class_9154<T> id,
            class_9139 codec, int maxPayloadSize) {
        if (maxPayloadSize < 0) {
            throw new IllegalArgumentException("Provided maxPayloadSize needs to be positive!");
        }
        int slot = slot(registry);
        class_8710.class_9155 type = registry.register(id, codec);
        int idSize = ByteBufUtil.utf8MaxBytes(id.comp_2242().toString());
        int maxPacketSize = maxPayloadSize + class_8703.method_53015(idSize) + idSize + 10;
        if (maxPacketSize < 0) {
            maxPacketSize = Integer.MAX_VALUE;
        }
        int minimalSplittableSize = (slot & 1) == 1 ? SAFE_S2C_SPLIT_SIZE : SAFE_C2S_SPLIT_SIZE;
        if (maxPacketSize > minimalSplittableSize) {
            if (!SPLIT_REGISTERED[slot]) {
                registry.register(CbSplitPayload.ID, CbSplitPayload.CODEC);
                SPLIT_REGISTERED[slot] = true;
            }
            MAX_SIZES[slot].put(id.comp_2242(), maxPacketSize);
        }
        return type;
    }

    private static synchronized int maxPacketSize(int slot, class_2960 id) {
        return MAX_SIZES[slot].getInt(id);
    }

    private static synchronized boolean hasLarge(int slot) {
        return slot >= 0 && !MAX_SIZES[slot].isEmpty();
    }

    /** the inbound setup of a protocol (Connection.setupInboundProtocol's task), the merger added after the decoder */
    public static class_9130.class_9132 cbInbound(class_9130.class_9132 task, class_9127<?> protocol) {
        int slot = slot(protocol);
        if (!hasLarge(slot)) {
            return task;
        }
        return task.andThen(ctx -> ctx.pipeline().addAfter("decoder", "fabric:merger",
            new Merger((class_2543<?>) ctx.pipeline().get(class_2543.class), slot)));
    }

    /** the outbound setup of a protocol (Connection.setupOutboundProtocol's task), the splitter added after the encoder */
    public static class_9130.class_9134 cbOutbound(class_9130.class_9134 task, class_9127<?> protocol) {
        int slot = slot(protocol);
        if (!hasLarge(slot)) {
            return task;
        }
        return task.andThen(ctx -> ctx.pipeline().addAfter("encoder", "fabric:splitter",
            new Splitter((class_2545<?>) ctx.pipeline().get(class_2545.class), slot)));
    }

    /** the encoder's write of a CbPassthroughPacket: its bytes as they are; false for every other packet */
    public static boolean cbPassthrough(class_2596<?> packet, ByteBuf out) {
        if (packet instanceof CbPassthroughPacket passthrough) {
            out.writeBytes(passthrough.buf());
            return true;
        }
        return false;
    }

    /** 0.136's FabricPacketSplitter */
    static final class Splitter extends MessageToMessageEncoder<class_2596<?>> {
        private final class_2545<?> encoder;
        private final int slot;

        Splitter(class_2545<?> encoder, int slot) {
            this.encoder = encoder;
            this.slot = slot;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
            class_8710 payload = null;
            Function<class_8710, class_2596<?>> wrap = null;
            int splitSize = 0;
            if (packet instanceof class_2658 s2c) {
                payload = s2c.comp_1646();
                wrap = class_2658::new;
                splitSize = SAFE_S2C_SPLIT_SIZE;
            } else if (packet instanceof class_2817 c2s) {
                payload = c2s.comp_1647();
                wrap = class_2817::new;
                splitSize = SAFE_C2S_SPLIT_SIZE;
            }
            int max = payload == null ? -1 : maxPacketSize(this.slot, payload.method_56479().comp_2242());
            if (max == -1) {
                out.add(packet);
            } else {
                split(payload.method_56479().comp_2242(), ctx, this.encoder, packet, wrap, out::add, splitSize, max);
            }
            if (packet.method_55943()) {
                ctx.pipeline().remove(ctx.name());
            }
        }
    }

    /** 0.136's FabricPacketSplitter.genericPacketSplitter */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void split(class_2960 id, ChannelHandlerContext ctx, class_2545 encoder, class_2596<?> packet, Function<class_8710, class_2596<?>> wrap,
            Consumer<class_2596<?>> out, int splitSize, int maxSize) throws Exception {
        ByteBuf buf = Unpooled.buffer();
        encoder.method_10838(ctx, packet, buf);
        if (buf.readableBytes() < splitSize) {
            out.accept(new CbPassthroughPacket(buf));
            return;
        }
        if (buf.readableBytes() > maxSize) {
            throw new EncoderException("Packet '" + id + "' exceeded max size of " + maxSize);
        }
        ByteBuf first = Unpooled.buffer(splitSize);
        class_8703.method_53017(first, buf.readableBytes());
        first.writeBytes(buf.readSlice(splitSize - first.readableBytes()));
        out.accept(wrap.apply(new CbSplitPayload(first)));
        while (buf.isReadable()) {
            out.accept(wrap.apply(new CbSplitPayload(buf.readSlice(Math.min(buf.readableBytes(), splitSize)))));
        }
    }

    /** 0.136's FabricPacketMerger */
    static final class Merger extends MessageToMessageDecoder<class_2596<?>> {
        private final class_2543<?> decoder;
        private final int slot;
        private Assembly assembly;

        Merger(class_2543<?> decoder, int slot) {
            this.decoder = decoder;
            this.slot = slot;
        }

        private static class_8710 payloadOf(class_2596<?> packet) {
            if (packet instanceof class_2658 s2c) {
                return s2c.comp_1646();
            }
            if (packet instanceof class_2817 c2s) {
                return c2s.comp_1647();
            }
            return null;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
            if (this.assembly != null) {
                ensureNotTransitioning(packet);
                class_8710 payload = payloadOf(packet);
                if (payload == null) {
                    throw new DecoderException("Received '" + packet.method_65080().comp_2231() + "' packet, while merging split packet!");
                }
                if (!(payload instanceof CbSplitPayload split)) {
                    throw new DecoderException("Expected '" + CbSplitPayload.ID.comp_2242() + "' payload packet, but received '"
                        + payload.method_56479().comp_2242() + "' while merging split packet!");
                }
                if (this.assembly.add(ctx, split, out)) {
                    this.assembly = null;
                }
                return;
            }
            if (payloadOf(packet) instanceof CbSplitPayload split) {
                ensureNotTransitioning(packet);
                ByteBuf buf = split.byteBuf();
                int finalSize = class_8703.method_53016(buf);
                int index = buf.readerIndex();
                class_8703.method_53016(buf);
                class_2960 id = class_2960.field_48267.decode(split.byteBuf());
                buf.readerIndex(index);
                int max = maxPacketSize(this.slot, id);
                if (max == -1) {
                    throw new DecoderException("Received '" + id + "' packet, which is not registered as large!");
                }
                if (max < finalSize) {
                    throw new DecoderException("Received '" + id + "' packet, which exceeds its max size! (" + finalSize + " > " + max + ")");
                }
                this.assembly = new Assembly(this.decoder, id, finalSize, packet.method_65080());
                if (this.assembly.add(ctx, split, out)) {
                    throw new DecoderException("Received '" + id + "' packet, which was complete in its first part!");
                }
                return;
            }
            out.add(packet);
            if (packet.method_55943()) {
                ctx.pipeline().remove(ctx.name());
            }
        }

        private static void ensureNotTransitioning(class_2596<?> packet) {
            if (packet.method_55943()) {
                throw new DecoderException("Terminal message received in bundle");
            }
        }
    }

    /** 0.136's FabricPacketMerger.Merger: the slices of one packet, decoded by the vanilla decoder when complete */
    static final class Assembly {
        private final class_2543<?> decoder;
        private final class_2960 packetId;
        private final int finalSize;
        private final ByteBuf byteBuf;
        private final Object carrierType;

        Assembly(class_2543<?> decoder, class_2960 packetId, int finalSize, Object carrierType) {
            this.decoder = decoder;
            this.packetId = packetId;
            this.finalSize = finalSize;
            this.byteBuf = Unpooled.buffer(finalSize);
            this.carrierType = carrierType;
        }

        boolean add(ChannelHandlerContext ctx, CbSplitPayload payload, List<Object> out) throws Exception {
            if (this.byteBuf.readableBytes() + payload.byteBuf().readableBytes() > this.finalSize) {
                throw new DecoderException("Received too much data for packet '" + this.packetId + "'! (" + (this.byteBuf.readableBytes()
                    + payload.byteBuf().readableBytes()) + " > " + this.finalSize + ")");
            }
            this.byteBuf.writeBytes(payload.byteBuf());
            if (this.byteBuf.readableBytes() != this.finalSize) {
                return false;
            }
            List<Object> decoded = new ArrayList<>();
            this.decoder.decode(ctx, this.byteBuf, decoded);
            // 0.136 checked the merged packet's vanilla type against the carrying packet's up front; the decoded packet
            // is checked here: one packet of the carrying type, holding the payload the first slice announced
            if (decoded.size() != 1 || !(decoded.get(0) instanceof class_2596<?> merged) || !merged.method_65080().equals(this.carrierType)
                    || Merger.payloadOf(merged) == null || !Merger.payloadOf(merged).method_56479().comp_2242().equals(this.packetId)) {
                throw new DecoderException("Merged packet '" + this.packetId + "' is not a '" + this.carrierType + "' packet of that payload");
            }
            out.addAll(decoded);
            return true;
        }
    }
}
