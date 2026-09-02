package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.class_2540;
import net.minecraft.class_2960;

/**
 * Client half of the raw-channel compat -- separate class so a dedicated
 * server never touches class_310. See CbNetworking.
 */
public final class CbClientNetworking {

    public static boolean registerGlobalReceiver(class_2960 channel, CbClientPlayHandler handler) {
        CbNetworking.ensureS2C(channel);
        return ClientPlayNetworking.registerGlobalReceiver(CbNetworking.idOf(channel),
            (payload, ctx) -> handler.receive(ctx.client(), ctx.client().method_1562(),
                CbNetworking.wrap(payload), ctx.responseSender()));
    }

    public static void send(class_2960 channel, class_2540 buf) {
        CbNetworking.ensureC2S(channel);
        ClientPlayNetworking.send(new CbNetworking.RawPayload(channel, CbNetworking.drain(buf)));
    }

    public static boolean registerReceiver(class_2960 channel, CbClientPlayHandler handler) {
        CbNetworking.ensureS2C(channel);
        return ClientPlayNetworking.registerReceiver(CbNetworking.idOf(channel),
            (payload, ctx) -> handler.receive(ctx.client(), ctx.client().method_1562(),
                CbNetworking.wrap(payload), ctx.responseSender()));
    }

    /** was ClientPlayNetworking.createC2SPacket(Identifier, PacketByteBuf) */
    public static net.minecraft.class_2596 createC2SPacket(class_2960 channel, class_2540 buf) {
        CbNetworking.ensureC2S(channel);
        return ClientPlayNetworking.createC2SPacket(new CbNetworking.RawPayload(channel, CbNetworking.drain(buf)));
    }

    // ---- old typed-packet API (FabricPacket / PacketType, gone in 1.20.5) ----

    public static void send(CbFabricPacket packet) {
        class_2540 buf = new class_2540(io.netty.buffer.Unpooled.buffer());
        packet.write(buf);
        send(packet.getType().getId(), buf);
    }

    public static <T extends CbFabricPacket> boolean registerGlobalReceiver(CbPacketType<T> type, CbClientPlayPacketHandler<T> handler) {
        CbNetworking.ensureS2C(type.getId());
        return ClientPlayNetworking.registerGlobalReceiver(CbNetworking.idOf(type.getId()),
            (payload, ctx) -> handler.receive(type.read(CbNetworking.wrap(payload)), ctx.player(), ctx.responseSender()));
    }

    private CbClientNetworking() {
    }
}
