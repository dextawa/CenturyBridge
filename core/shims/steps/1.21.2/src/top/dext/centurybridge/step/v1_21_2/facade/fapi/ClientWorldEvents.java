package top.dext.centurybridge.step.v1_21_2.facade.fapi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.class_310;
import net.minecraft.class_638;

/**
 * ClientWorldEvents of 1.21.1's Fabric API (0.116; absent from 1.21.2's 0.106.1), classRenamed: its
 * AFTER_CLIENT_WORLD_CHANGE fires where 0.116's MinecraftClientMixin fired it, at the end of
 * Minecraft.updateLevelInEngines when the new level is not null (CbRestFapiClientMixin).
 */
@Environment(EnvType.CLIENT)
public final class ClientWorldEvents {

    private ClientWorldEvents() {
    }

    /** ClientWorldEvents.AfterClientWorldChange */
    @FunctionalInterface
    public interface AfterClientWorldChange {
        void afterWorldChange(class_310 client, class_638 world);
    }

    public static final Event<AfterClientWorldChange> AFTER_CLIENT_WORLD_CHANGE = EventFactory.createArrayBacked(AfterClientWorldChange.class,
        callbacks -> (client, world) -> {
            for (AfterClientWorldChange callback : callbacks) {
                callback.afterWorldChange(client, world);
            }
        });
}
