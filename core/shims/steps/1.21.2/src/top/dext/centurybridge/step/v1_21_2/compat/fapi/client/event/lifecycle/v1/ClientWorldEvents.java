package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.event.lifecycle.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * 1.21.1 Fabric API's (0.116.15) ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE, which 1.21.2's Fabric API (0.106.1) does
 * not have (its ClientWorldEvents class is absent): the class and its callback (classRenames),
 * fired where 1.21.1 fired it -- at the end of Minecraft.updateLevelInEngines(level) for a non-null level
 * (CbFapiClientWorldMixin).
 */
@Environment(EnvType.CLIENT)
public final class ClientWorldEvents {

    public static final Event<AfterClientWorldChange> AFTER_CLIENT_WORLD_CHANGE = EventFactory.createArrayBacked(AfterClientWorldChange.class,
        callbacks -> (client, world) -> {
            for (AfterClientWorldChange callback : callbacks) {
                callback.afterWorldChange(client, world);
            }
        });

    private ClientWorldEvents() {
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface AfterClientWorldChange {
        void afterWorldChange(net.minecraft.class_310 client, net.minecraft.class_638 world);
    }
}
