package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.client.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;

/**
 * Fabric API 0.97.3's (1.20.4) ClientPlayerBlockBreakEvents: 1.20.5 made the class final with a private
 * constructor, keeping the event. A 1.20.4 mod's every reference to the class names this one instead
 * (classRenames) -- a subclass extends it, {@code new} builds it, and its AFTER is Fabric API's own event
 * object, so the event reached through it is the same one. The nested After listener is unchanged and
 * stays Fabric API's.
 */
@Environment(EnvType.CLIENT)
public class ClientPlayerBlockBreakEvents {

    public static final Event<net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents.After> AFTER =
        net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents.AFTER;

    public ClientPlayerBlockBreakEvents() {
    }
}
