package top.dext.centurybridge.compat.fapi.impl.networking.v0;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.loader.api.FabricLoader;
import top.dext.centurybridge.compat.fapi.event.network.C2SPacketTypeCallback;
import top.dext.centurybridge.compat.fapi.event.network.S2CPacketTypeCallback;
import top.dext.centurybridge.compat.fapi.impl.client.networking.v0.CbFapiClientNetworking;

/**
 * The v0 channel-registration events and their forwarding from 1.20.2's play-channel events
 * (1.20.1's OldNetworkingHooks / OldClientNetworkingHooks). The forwarders are registered when the
 * events are first touched rather than at game start: until a mod touches an event it has no
 * listener, so nothing it would have been told is lost.
 */
public final class CbFapiPacketTypeEvents {

    private static final Event<C2SPacketTypeCallback> C2S_REGISTERED = c2sEvent();
    private static final Event<C2SPacketTypeCallback> C2S_UNREGISTERED = c2sEvent();
    private static final Event<S2CPacketTypeCallback> S2C_REGISTERED = s2cEvent();
    private static final Event<S2CPacketTypeCallback> S2C_UNREGISTERED = s2cEvent();

    static {
        S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) ->
            C2S_REGISTERED.invoker().accept(handler.field_14140, channels));
        S2CPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) ->
            C2S_UNREGISTERED.invoker().accept(handler.field_14140, channels));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            CbFapiClientNetworking.forwardChannelEvents(S2C_REGISTERED, S2C_UNREGISTERED);
        }
    }

    private CbFapiPacketTypeEvents() {
    }

    public static Event<C2SPacketTypeCallback> c2s(boolean registered) {
        return registered ? C2S_REGISTERED : C2S_UNREGISTERED;
    }

    public static Event<S2CPacketTypeCallback> s2c(boolean registered) {
        return registered ? S2C_REGISTERED : S2C_UNREGISTERED;
    }

    private static Event<C2SPacketTypeCallback> c2sEvent() {
        return EventFactory.createArrayBacked(C2SPacketTypeCallback.class, callbacks -> (client, channels) -> {
            for (C2SPacketTypeCallback callback : callbacks) {
                callback.accept(client, channels);
            }
        });
    }

    private static Event<S2CPacketTypeCallback> s2cEvent() {
        return EventFactory.createArrayBacked(S2CPacketTypeCallback.class, callbacks -> channels -> {
            for (S2CPacketTypeCallback callback : callbacks) {
                callback.accept(channels);
            }
        });
    }
}
