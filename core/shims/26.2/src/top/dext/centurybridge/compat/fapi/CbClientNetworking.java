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

    public static void send(net.minecraft.resources.Identifier channel, net.minecraft.network.FriendlyByteBuf buf) {
        CbNetworking.ensureC2S(channel);
        ClientPlayNetworking.send(new CbNetworking.RawPayload(channel, CbNetworking.drain(buf)));
    }

    private CbClientNetworking() {
    }
}
