package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
/**
 * Client half of the raw-channel compat -- separate class so a dedicated
 * server never touches net.minecraft.client.Minecraft. See CbNetworking.
 */
public final class CbClientNetworking {

    public static boolean registerGlobalReceiver(net.minecraft.resources.Identifier channel, CbClientPlayHandler handler) {
        CbNetworking.ensureS2C(channel);
        return ClientPlayNetworking.registerGlobalReceiver(CbNetworking.idOf(channel),
            (payload, ctx) -> handler.receive(ctx.client(), ctx.client().getConnection(),
                CbNetworking.wrap(payload), ctx.responseSender()));
    }

    public static boolean registerReceiver(net.minecraft.resources.Identifier channel, CbClientPlayHandler handler) {
        CbNetworking.ensureS2C(channel);
        return ClientPlayNetworking.registerReceiver(CbNetworking.idOf(channel),
            (payload, ctx) -> handler.receive(ctx.client(), ctx.client().getConnection(),
                CbNetworking.wrap(payload), ctx.responseSender()));
    }

    public static void send(net.minecraft.resources.Identifier channel, net.minecraft.network.FriendlyByteBuf buf) {
        CbNetworking.ensureC2S(channel);
        ClientPlayNetworking.send(new CbNetworking.RawPayload(channel, CbNetworking.drain(buf)));
    }

    /** was ClientPlayNetworking.createC2SPacket(Identifier, PacketByteBuf) */
    public static net.minecraft.network.protocol.Packet createC2SPacket(net.minecraft.resources.Identifier channel, net.minecraft.network.FriendlyByteBuf buf) {
        CbNetworking.ensureC2S(channel);
        return ClientPlayNetworking.createServerboundPacket(new CbNetworking.RawPayload(channel, CbNetworking.drain(buf)));
    }

    // ---- old typed-packet API (FabricPacket / PacketType, gone in 1.20.5) ----

    public static void send(CbFabricPacket packet) {
        net.minecraft.network.FriendlyByteBuf buf = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
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
