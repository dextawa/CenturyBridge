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
 * shutdownEffect(). Then the client mixin on loadEffect (loadEffectMixin). Ends "CB-S205C summary ok=N fail=M".
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
                loadEffectMixin(client, renderer);
                System.out.println("CB-S205C summary ok=" + ok + " fail=" + fail);
            }
        });
    }

    /**
     * cb.s205.mixin.LoadEffectMixin on loadEffect: the cycle's loads reached it with 1.20.4's spelling (invert among
     * them); checkEntityPostEffect for a creeper camera (1.20.4's vanilla caller of loadEffect) reaches it once more with
     * minecraft:shaders/post/creeper.json and makes creeper the current effect; every call reached TAIL. With the native
     * 1.21.2 mod cb-s205n loaded, its @WrapOperation of checkEntityPostEffect's setPostEffect call and its HEAD injector on
     * setPostEffect saw minecraft:creeper (read by reflection). Then shutdownEffect.
     */
    private static void loadEffectMixin(net.minecraft.class_310 client, net.minecraft.class_757 renderer) {
        boolean cycle = S205Seen.LOADS.contains("minecraft:shaders/post/invert.json");
        tally(cycle, "loadEffect cycle loads=" + S205Seen.LOADS.size() + " invert-seen=" + cycle);
        int before = S205Seen.LOADS.size();
        renderer.method_3167(new net.minecraft.class_1548(net.minecraft.class_1299.field_6046, client.field_1687));
        String last = S205Seen.LOADS.isEmpty() ? null : S205Seen.LOADS.get(S205Seen.LOADS.size() - 1);
        boolean creeper = S205Seen.LOADS.size() == before + 1 && "minecraft:shaders/post/creeper.json".equals(last);
        tally(creeper, "loadEffect creeper-camera calls=" + (S205Seen.LOADS.size() - before) + " location=" + last);
        effect("creeper", renderer, "minecraft:shaders/post/creeper.json", "minecraft:creeper");
        tally(S205Seen.tails == S205Seen.LOADS.size(), "loadEffect tails=" + S205Seen.tails + " heads=" + S205Seen.LOADS.size());
        try {
            Class<?> n = Class.forName("cb.s205n.S205NSeen");
            Object wrapped = n.getField("WRAPPED").get(null);
            Object set = n.getField("SET").get(null);
            tally(String.valueOf(wrapped).contains("minecraft:creeper"), "native wrap setPostEffect " + wrapped);
            tally(String.valueOf(set).contains("minecraft:creeper"), "native setPostEffect head " + set);
        } catch (ClassNotFoundException e) {
            System.out.println("CB-S205C native cb-s205n not loaded");
        } catch (ReflectiveOperationException e) {
            tally(false, "native read " + e);
        }
        renderer.method_3207();
        effect("shutdown2", renderer, null, null);
    }

    private static void tally(boolean pass, String what) {
        if (pass) ok++; else fail++;
        System.out.println("CB-S205C " + (pass ? "ok" : "FAIL") + " " + what);
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
