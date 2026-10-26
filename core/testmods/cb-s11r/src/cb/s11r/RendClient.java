package cb.s11r;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.class_1937;
import net.minecraft.class_310;

/**
 * cb-s11r: a 1.21.8-era client mod (Fabric API 0.136's DimensionRenderingRegistry, deleted for 1.21.9 and restored by the
 * 1.21.9 layer) registering a sky, a cloud and a weather renderer for the overworld. Climbed 1.21.8 -> T, it checks after
 * 200 client ticks in a world that T called each renderer: "CB-S11R ok|FAIL <renderer> calls=N", "CB-S11R n/3", then stops
 * the client. The renderers draw nothing (a registered renderer replaces the dimension's own sky / clouds / weather).
 */
public class RendClient implements ClientModInitializer {
    static volatile int sky, clouds, weather;
    private static int ticks;
    private static boolean done;

    @Override
    public void onInitializeClient() {
        DimensionRenderingRegistry.registerSkyRenderer(class_1937.field_25179, context -> sky++);
        DimensionRenderingRegistry.registerCloudRenderer(class_1937.field_25179, context -> clouds++);
        DimensionRenderingRegistry.registerWeatherRenderer(class_1937.field_25179, context -> weather++);
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (done || mc.field_1687 == null || ++ticks < 200) {
                return;
            }
            done = true;
            int ok = check("sky", sky) + check("clouds", clouds) + check("weather", weather);
            System.out.println("CB-S11R " + ok + "/3");
            class_310.method_1551().method_1592();
        });
    }

    private static int check(String name, int calls) {
        System.out.println("CB-S11R " + (calls > 0 ? "ok " : "FAIL ") + name + " calls=" + calls);
        return calls > 0 ? 1 : 0;
    }
}
