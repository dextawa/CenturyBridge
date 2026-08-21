package top.dext.centurybridge.compat.fapi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_8710;
import net.minecraft.class_9129;
import net.minecraft.class_9139;

/**
 * The raw-channel half of fabric-api's networking v1, rebuilt on the 1.20.5+
 * typed-payload API. Old mods think in (Identifier channel, PacketByteBuf);
 * the new API wants a registered CustomPayload type per channel. Each old
 * channel gets one lazily-registered RawPayload type that just carries bytes,
 * so the old contract survives byte-for-byte.
 *
 * Server side lives here; the client statics are in CbClientNetworking so a
 * dedicated server never loads client classes.
 */
public final class CbNetworking {

    static final Map<class_2960, class_8710.class_9154<RawPayload>> IDS = new ConcurrentHashMap<>();
    static final Map<class_2960, class_9139<class_9129, RawPayload>> CODECS = new ConcurrentHashMap<>();
    static final Set<class_2960> REG_C2S = ConcurrentHashMap.newKeySet();
    static final Set<class_2960> REG_S2C = ConcurrentHashMap.newKeySet();

    /** One payload class for every legacy channel: an id plus opaque bytes. */
    public static final class RawPayload implements class_8710 {
        final class_2960 channel;
        final byte[] data;

        public RawPayload(class_2960 channel, byte[] data) {
            this.channel = channel;
            this.data = data;
        }

        @Override
        public class_8710.class_9154<? extends class_8710> method_56479() {
            return idOf(channel);
        }
    }

    static class_8710.class_9154<RawPayload> idOf(class_2960 ch) {
        return IDS.computeIfAbsent(ch, class_8710.class_9154::new);
    }

    static class_9139<class_9129, RawPayload> codecOf(class_2960 ch) {
        return CODECS.computeIfAbsent(ch, c -> class_8710.method_56484(
            (payload, buf) -> buf.writeBytes(payload.data),
            buf -> {
                byte[] d = new byte[buf.readableBytes()];
                buf.readBytes(d);
                return new RawPayload(c, d);
            }));
    }

    static void ensureC2S(class_2960 ch) {
        if (REG_C2S.add(ch)) {
            PayloadTypeRegistry.playC2S().register(idOf(ch), codecOf(ch));
        }
    }

    static void ensureS2C(class_2960 ch) {
        if (REG_S2C.add(ch)) {
            PayloadTypeRegistry.playS2C().register(idOf(ch), codecOf(ch));
        }
    }

    static byte[] drain(class_2540 buf) {
        byte[] d = new byte[buf.readableBytes()];
        buf.readBytes(d);
        return d;
    }

    static class_2540 wrap(RawPayload p) {
        return new class_2540(io.netty.buffer.Unpooled.wrappedBuffer(p.data));
    }

    // ---- old ServerPlayNetworking statics, exact legacy descriptors ----

    public static boolean registerGlobalReceiver(class_2960 channel, CbServerPlayHandler handler) {
        ensureC2S(channel);
        return ServerPlayNetworking.registerGlobalReceiver(idOf(channel),
            (payload, ctx) -> handler.receive(ctx.server(), ctx.player(),
                ctx.player().field_13987, wrap(payload), ctx.responseSender()));
    }

    public static void send(class_3222 player, class_2960 channel, class_2540 buf) {
        ensureS2C(channel);
        ServerPlayNetworking.send(player, new RawPayload(channel, drain(buf)));
    }

    private CbNetworking() {
    }
}
