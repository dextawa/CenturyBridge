package cb.rendertest;

import net.fabricmc.api.ClientModInitializer;

/**
 * 1.20.1-era client mod (intermediary names, no fabric-api) exercising the entity render
 * bridges: a MobEntityRenderer subclass with its own EntityModel for pigs (createRenderState,
 * getTexture, render + super.render, setAngles, the model's own render, a model super()
 * without a root), and a plain EntityRenderer for arrows that writes raw vertices into the
 * buffer source (the record/replay path). Once the player is in a world it adds one of each
 * entity, client-side only, in front of the camera, and logs each bridge the first time it
 * runs ("CB-RENDERTEST ...").
 */
public class RenderTestMod implements ClientModInitializer {

    static final java.util.Set<String> SEEN = java.util.concurrent.ConcurrentHashMap.newKeySet();

    static void once(String what) {
        if (SEEN.add(what)) {
            System.out.println("CB-RENDERTEST " + what);
        }
    }

    @Override
    public void onInitializeClient() {
        // EntityRenderers.register is private: opened by this mod's access widener
        net.minecraft.class_5619.method_32173(net.minecraft.class_1299.field_6093, TestPigRenderer::new);
        net.minecraft.class_5619.method_32173(net.minecraft.class_1299.field_6122, TestArrowRenderer::new);
        // BlockEntityRendererFactories.register is private too
        net.minecraft.class_5616.method_32144(net.minecraft.class_2591.field_11914, TestChestRenderer::new);
        once("registered renderers");
        Thread t = new Thread(() -> {
            net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                if (mc.field_1687 != null && mc.field_1724 != null) {
                    try {
                        Thread.sleep(3000L); // let the first chunks arrive
                    } catch (InterruptedException e) {
                        return;
                    }
                    mc.execute(RenderTestMod::spawn);
                    return;
                }
            }
        }, "cb-rendertest");
        t.setDaemon(true);
        t.start();
    }

    private static void spawn() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_638 world = mc.field_1687;
        net.minecraft.class_746 player = mc.field_1724;
        if (world == null || player == null) {
            return;
        }
        // look up at open sky, so terrain never hides the test entities
        player.method_36457(-35.0F);
        net.minecraft.class_243 look = player.method_5720();
        double x = player.method_23317() + look.field_1352 * 4.0;
        double y = player.method_23320() + look.field_1351 * 4.0 - 0.5;
        double z = player.method_23321() + look.field_1350 * 4.0;
        net.minecraft.class_1452 pig = new net.minecraft.class_1452(net.minecraft.class_1299.field_6093, world);
        pig.method_5814(x, y, z);
        pig.method_5875(true);
        world.method_2942(-424242, pig);
        net.minecraft.class_1667 arrow = new net.minecraft.class_1667(net.minecraft.class_1299.field_6122, world);
        arrow.method_5814(x + look.field_1350 * 1.5, y + 0.5, z - look.field_1352 * 1.5);
        arrow.method_5875(true);
        world.method_2942(-424243, arrow);
        once("spawned pig + arrow at " + (int) x + " " + (int) y + " " + (int) z);
        // a chest, placed through the integrated server so its block entity exists and syncs
        net.minecraft.class_1132 server = mc.method_1576();
        if (server != null) {
            net.minecraft.class_2338 at = net.minecraft.class_2338.method_49637(x - look.field_1350 * 1.5, y - 0.5, z + look.field_1352 * 1.5);
            server.execute(() -> {
                server.method_30002().method_8501(at, net.minecraft.class_2246.field_10034.method_9564());
                once("placed chest at " + at);
            });
        }
    }
}
