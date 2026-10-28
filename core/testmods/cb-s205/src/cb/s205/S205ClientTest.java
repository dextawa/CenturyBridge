package cb.s205;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Client side: 1.20.4's GameRenderer.getPositionTexColorNormalShader() / getPositionTexLightmapColorShader(), two core
 * shaders 1.20.5 dropped and the 1.20.5 layer gives back (loaded with each resource reload). Once a world is joined:
 * both present ("CB-S205C ok load ..."), then a resource reload, after which both are present again and are new
 * programs ("CB-S205C ok reload ..."). Then 1.20.4's cycleEffect() (dropped by 1.20.5, given back by the 1.20.5 layer)
 * through its post effects with currentEffect() and shutdownEffect(): from no effect, one step loads notch (no longer
 * shipped: logged, no effect); ten steps reach invert, which is the current effect (and, from 1.21.2 on, the game's post
 * effect id, read by reflection); after 40 rendered ticks, fifteen more steps reach no effect again, which cycleEffect
 * sets by writing GameRenderer.postEffect = null (from 1.21.2 on the id follows that write); then one step and
 * shutdownEffect(). Ends "CB-S205C summary ok=N fail=M".
 */
public class S205ClientTest implements ClientModInitializer {
    private static int ticks;
    private static int ok;
    private static int fail;
    private static net.minecraft.class_5944 normal0;
    private static net.minecraft.class_5944 lightmap0;
    private static boolean reloading;
    private static int cycleAt = -1;

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
                    cycleAt = ticks + 20;
                    reloading = false;
                }));
            }
            if (cycleAt >= 0 && ticks == cycleAt) {
                net.minecraft.class_757 renderer = client.field_1773;
                renderer.method_35771();
                effect("notch", renderer, null, null);
                for (int i = 0; i < 9; i++) {
                    renderer.method_35771();
                }
                effect("invert", renderer, "minecraft:shaders/post/invert.json", "minecraft:invert");
            }
            if (cycleAt >= 0 && ticks == cycleAt + 40) {
                net.minecraft.class_757 renderer = client.field_1773;
                for (int i = 0; i < 15; i++) {
                    renderer.method_35771();
                }
                effect("none", renderer, null, null);
                renderer.method_35771();
                renderer.method_3207();
                effect("shutdown", renderer, null, null);
                System.out.println("CB-S205C summary ok=" + ok + " fail=" + fail);
            }
        });
    }

    private static void effect(String when, net.minecraft.class_757 renderer, String chain, String id) {
        net.minecraft.class_279 current = renderer.method_3183();
        String name = current == null ? null : current.method_1260();
        boolean pass = java.util.Objects.equals(chain, name);
        String nativeId = "-";
        try {
            // 1.21.2's GameRenderer.getPostEffect(): absent before 1.21.2
            Object v = net.minecraft.class_757.class.getMethod("method_62906").invoke(renderer);
            nativeId = String.valueOf(v);
            pass &= java.util.Objects.equals(String.valueOf(id), nativeId);
        } catch (NoSuchMethodException e) {
            // before 1.21.2
        } catch (ReflectiveOperationException e) {
            pass = false;
            nativeId = e.toString();
        }
        if (pass) ok++; else fail++;
        System.out.println("CB-S205C " + (pass ? "ok" : "FAIL") + " cycle " + when + " current=" + name + " id=" + nativeId);
    }

    private static void check(String when, String name, net.minecraft.class_5944 shader) {
        boolean pass = shader != null && name.equals(shader.method_35787());
        if (pass) ok++; else fail++;
        System.out.println("CB-S205C " + (pass ? "ok" : "FAIL") + " " + when + " " + name + " -> "
            + (shader == null ? "null" : shader.method_35787()));
    }
}
