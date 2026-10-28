package cb.s205n;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Without cb-s205 (whose climbed driver does this and reads S205NSeen): 60 ticks after a world is joined,
 * checkEntityPostEffect for a creeper camera; this mod's wrapper and HEAD injector saw minecraft:creeper, and the game's
 * post effect id is minecraft:creeper. Then clearPostEffect. Ends "CB-S205N summary ok=N fail=M".
 */
public class S205NDriver implements ClientModInitializer {
    private static int ticks;
    private static int ok;
    private static int fail;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("cb_s205")) {
            return;
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.field_1687 == null || ++ticks != 60) {
                return;
            }
            net.minecraft.class_757 renderer = client.field_1773;
            renderer.method_3167(new net.minecraft.class_1548(net.minecraft.class_1299.field_6046, client.field_1687));
            tally(S205NSeen.WRAPPED.equals(java.util.List.of("minecraft:creeper")), "wrap " + S205NSeen.WRAPPED);
            tally(S205NSeen.SET.equals(java.util.List.of("minecraft:creeper")), "head " + S205NSeen.SET);
            tally("minecraft:creeper".equals(String.valueOf(renderer.method_62906())), "id " + renderer.method_62906());
            renderer.method_62905();
            System.out.println("CB-S205N summary ok=" + ok + " fail=" + fail);
        });
    }

    private static void tally(boolean pass, String what) {
        if (pass) ok++; else fail++;
        System.out.println("CB-S205N " + (pass ? "ok" : "FAIL") + " " + what);
    }
}
