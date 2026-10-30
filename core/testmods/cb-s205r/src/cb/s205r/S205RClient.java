package cb.s205r;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/** 60 ticks after a world is joined: the client recipe manager's copy of cb_s205r:legacy_a (S205RTestMod.run) */
public class S205RClient implements ClientModInitializer {
    private static int ticks;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.field_1687 == null || client.method_1562() == null || ++ticks != 60) {
                return;
            }
            S205RTestMod.run(client.method_1562().method_2877(), "client");
        });
    }
}
