package top.dext.centurybridge.compat.fapi.event.network;

import java.util.Collection;
import net.fabricmc.fabric.api.event.Event;

/**
 * Fabric API networking v0's S2CPacketTypeCallback (client side): the server registered or unregistered
 * channels it can receive. Fired on a client from 1.20.2's play-channel events, as 1.20.1's v0 module
 * forwarded them; on a dedicated server the events exist and never fire, as in 1.20.1.
 */
@FunctionalInterface
public interface S2CPacketTypeCallback {

    Event<S2CPacketTypeCallback> REGISTERED = top.dext.centurybridge.compat.fapi.impl.networking.v0.CbFapiPacketTypeEvents.s2c(true);

    Event<S2CPacketTypeCallback> UNREGISTERED = top.dext.centurybridge.compat.fapi.impl.networking.v0.CbFapiPacketTypeEvents.s2c(false);

    void accept(Collection<net.minecraft.class_2960> channels);
}
