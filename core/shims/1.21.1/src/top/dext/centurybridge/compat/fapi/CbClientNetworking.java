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

    private CbClientNetworking() {
    }
}
