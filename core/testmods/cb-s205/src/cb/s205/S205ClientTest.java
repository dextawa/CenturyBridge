package cb.s205;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Client side: 1.20.4's GameRenderer.getPositionTexColorNormalShader() / getPositionTexLightmapColorShader(), two core
 * shaders 1.20.5 dropped and the 1.20.5 layer gives back (loaded with each resource reload). Once a world is joined:
 * both present ("CB-S205C ok load ..."), then a resource reload, after which both are present again and are new
 * programs ("CB-S205C ok reload ..."). Ends "CB-S205C summary ok=N fail=M".
 */
public class S205ClientTest implements ClientModInitializer {
    private static int ticks;
    private static int ok;
    private static int fail;
    private static net.minecraft.class_5944 normal0;
    private static net.minecraft.class_5944 lightmap0;
    private static boolean reloading;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.field_1687 == null || reloading) {
                return;
            }
            ticks++;
            if (ticks == 40) {
                normal0 = net.minecraft.class_757.method_34549();
                lightmap0 = net.minecraft.class_757.method_35764();
                check("load", "position_tex_color_normal", normal0);
                check("load", "position_tex_lightmap_color", lightmap0);
                reloading = true;
                client.method_1521().whenComplete((v, t) -> client.execute(() -> {
                    if (t != null) {
                        fail++;
                        System.out.println("CB-S205C FAIL reload threw " + t);
                    }
                    net.minecraft.class_5944 normal = net.minecraft.class_757.method_34549();
                    net.minecraft.class_5944 lightmap = net.minecraft.class_757.method_35764();
                    check("reload", "position_tex_color_normal", normal);
                    check("reload", "position_tex_lightmap_color", lightmap);
                    boolean fresh = normal != null && lightmap != null && normal != normal0 && lightmap != lightmap0;
                    if (fresh) ok++; else fail++;
                    System.out.println("CB-S205C " + (fresh ? "ok" : "FAIL") + " reload new-programs=" + fresh);
                    System.out.println("CB-S205C summary ok=" + ok + " fail=" + fail);
                }));
            }
        });
    }

    private static void check(String when, String name, net.minecraft.class_5944 shader) {
        boolean pass = shader != null && name.equals(shader.method_35787());
        if (pass) ok++; else fail++;
        System.out.println("CB-S205C " + (pass ? "ok" : "FAIL") + " " + when + " " + name + " -> "
            + (shader == null ? "null" : shader.method_35787()));
    }
}
