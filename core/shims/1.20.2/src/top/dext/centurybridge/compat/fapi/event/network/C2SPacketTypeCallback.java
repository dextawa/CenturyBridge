package top.dext.centurybridge.compat.fapi.event.network;

import java.util.Collection;
import net.fabricmc.fabric.api.event.Event;

/**
 * Fabric API networking v0's C2SPacketTypeCallback (server side): a client registered or unregistered
 * channels it can receive. Fired from 1.20.2's play-channel events, as 1.20.1's v0 module forwarded them.
 */
@FunctionalInterface
public interface C2SPacketTypeCallback {

    Event<C2SPacketTypeCallback> REGISTERED = top.dext.centurybridge.compat.fapi.impl.networking.v0.CbFapiPacketTypeEvents.c2s(true);

    Event<C2SPacketTypeCallback> UNREGISTERED = top.dext.centurybridge.compat.fapi.impl.networking.v0.CbFapiPacketTypeEvents.c2s(false);

    void accept(net.minecraft.class_1657 client, Collection<net.minecraft.class_2960> channels);
}
