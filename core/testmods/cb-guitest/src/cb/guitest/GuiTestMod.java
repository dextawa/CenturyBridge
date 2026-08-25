package cb.guitest;

import net.fabricmc.api.ClientModInitializer;

/** 1.20.1-era client mod: opens a DrawContext-drawing screen shortly after boot. */
public class GuiTestMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Thread t = new Thread(() -> {
            try { Thread.sleep(12000L); } catch (InterruptedException ignored) { return; }
            net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
            mc.execute(() -> mc.method_1507(new TestScreen()));
        }, "cb-guitest");
        t.setDaemon(true);
        t.start();
    }
}
