package cb.s26d;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.class_2338;
import net.minecraft.class_4587;
import net.minecraft.class_4588;

/**
 * A 1.21.8-era client mod (intermediary names, Fabric API 0.136) with WorldRenderEvents listeners, counted in system
 * properties ("cbs26d.old.*") and the frame's call order ("cbs26d.seq": O BEFORE_BLOCK_OUTLINE, B BLOCK_OUTLINE,
 * A AFTER_ENTITIES) that the native 26.2 driver cb_s26dn judges. While "cbs26d.cancel" is "1", BLOCK_OUTLINE draws a
 * red cross over the outlined block's top face into the event's vertex consumer and answers false (vanilla's outline
 * must not be drawn).
 */
public class S26dOld implements ClientModInitializer {
    static synchronized void bump(String key) {
        String k = "cbs26d.old." + key;
        System.setProperty(k, String.valueOf(Long.parseLong(System.getProperty(k, "0")) + 1));
    }

    static synchronized void seq(String letter) {
        System.setProperty("cbs26d.seq", System.getProperty("cbs26d.seq", "") + letter);
    }

    static void line(class_4588 vc, class_4587.class_4665 pose, float x0, float y0, float z0, float x1, float y1, float z1) {
        float dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        vc.method_22918(pose.method_23761(), x0, y0, z0).method_39415(0xFFFF0000).method_60831(pose, dx / len, dy / len, dz / len);
        vc.method_22918(pose.method_23761(), x1, y1, z1).method_39415(0xFFFF0000).method_60831(pose, dx / len, dy / len, dz / len);
    }

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.START.register(context -> bump("start"));
        WorldRenderEvents.END.register(context -> bump("end"));
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            bump("afterEntities");
            seq("A");
        });
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, hit) -> {
            bump("before");
            seq("O");
            return true;
        });
        WorldRenderEvents.BLOCK_OUTLINE.register((context, outline) -> {
            bump("outline");
            seq("B");
            if (!"1".equals(System.getProperty("cbs26d.cancel"))) {
                return true;
            }
            class_4587 stack = context.matrixStack();
            class_4588 vc = outline.vertexConsumer();
            if (stack != null && vc != null) {
                class_2338 pos = outline.blockPos();
                float x = (float) (pos.method_10263() - outline.cameraX());
                float y = (float) (pos.method_10264() + 1.002 - outline.cameraY());
                float z = (float) (pos.method_10260() - outline.cameraZ());
                class_4587.class_4665 pose = stack.method_23760();
                line(vc, pose, x, y, z, x + 1, y, z + 1);
                line(vc, pose, x + 1, y, z, x, y, z + 1);
                bump("draw");
            }
            return false;
        });
        System.out.println("CB-S26D old listeners registered");
    }
}
