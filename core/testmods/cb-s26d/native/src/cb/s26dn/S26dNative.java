package cb.s26dn;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

/**
 * s26d's block-outline / WorldRenderEvents check on a 26.2 client (driver; native 26.2 mod). Beside it runs cb-s26d,
 * a 1.21.8-era mod with Fabric API 0.136 WorldRenderEvents listeners, climbed to 26.2 (its listeners are served by the
 * 26.1 layer's restored WorldRenderEvents; at 26.2 the outline events reach it through the 26.2 layer's restored
 * LevelRenderer.renderBlockOutline, routed from submitBlockOutline).
 * <p>Letters of a frame: N native submitBlockOutline head, F 26.2 Fabric API BEFORE_BLOCK_OUTLINE, O 0.136
 * BEFORE_BLOCK_OUTLINE, B 0.136 BLOCK_OUTLINE, H native submitHitOutline head (vanilla's outline submitted), S / T
 * native executeSolid / executeTranslucent heads, A 0.136 AFTER_ENTITIES.
 * <p>Phases after the world is joined (the player looks at the ground): 1 nothing cancels; 2 the 0.136 BLOCK_OUTLINE
 * listener answers false (vanilla's outline must not be submitted; the listener draws its own); 3 the 26.2 Fabric API
 * BEFORE_BLOCK_OUTLINE listener answers false (the route must not run: no O / B, no H). Prints
 * "CB-S26D ok|FAIL <check> <detail>", "CB-S26D n/t", then stops the client.
 */
public class S26dNative implements ClientModInitializer {
    static final Map<String, Boolean> OK = new LinkedHashMap<>();
    static final String[] KEYS = {"native.submitBlockOutline", "native.submitHitOutline", "native.executeSolid",
        "native.executeTranslucent", "fapi.before", "old.start", "old.end", "old.before", "old.outline",
        "old.afterEntities", "old.draw"};

    static void log(String s) {
        System.out.println("CB-S26D " + s);
    }

    static void check(String name, boolean pass, String detail) {
        OK.put(name, pass);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    static Map<String, Long> snap() {
        Map<String, Long> m = new LinkedHashMap<>();
        for (String k : KEYS) {
            m.put(k, Probe.get(k));
        }
        return m;
    }

    static Map<String, Long> delta(Map<String, Long> a, Map<String, Long> b) {
        Map<String, Long> d = new LinkedHashMap<>();
        for (String k : KEYS) {
            d.put(k, b.get(k) - a.get(k));
        }
        return d;
    }

    /** equal up to the frame a snapshot fell into (snapshots are not taken between frames) */
    static boolean near(long x, long y) {
        return y > 0 && Math.abs(x - y) <= 1;
    }

    /** whether the letters appear in this order in s (each present) */
    static boolean ordered(String s, String letters) {
        int at = -1;
        for (char c : letters.toCharArray()) {
            int i = s.indexOf(c, at + 1);
            if (i < 0) {
                return false;
            }
            at = i;
        }
        return true;
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onInitializeClient() {
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) -> {
            Probe.bump("fapi.before");
            Probe.seq("F");
            return !"1".equals(System.getProperty("cbs26d.fapicancel"));
        });
        boolean old = FabricLoader.getInstance().isModLoaded("cb_s26d");
        Thread t = new Thread(() -> drive(old), "cb-s26d-driver");
        t.setDaemon(true);
        t.start();
    }

    static void drive(boolean old) {
        Minecraft mc = Minecraft.getInstance();
        while (mc == null || mc.player == null) {
            sleep(500);
            mc = Minecraft.getInstance();
        }
        final Minecraft m = mc;
        Thread look = new Thread(() -> {
            while (true) {
                m.execute(() -> {
                    if (m.player != null) {
                        m.player.setXRot(75.0f);
                    }
                });
                sleep(200);
            }
        }, "cb-s26d-look");
        look.setDaemon(true);
        look.start();
        log("joined; old mod loaded: " + old);
        sleep(6000);

        // phase 1: nothing cancels
        Map<String, Long> a = snap();
        sleep(8000);
        Map<String, Long> d1 = delta(a, snap());
        String seq1 = System.getProperty("cbs26d.lastseq", "");
        log("phase1 " + d1 + " lastseq=" + seq1);
        long n = d1.get("native.submitBlockOutline"), f = d1.get("fapi.before"), h = d1.get("native.submitHitOutline");
        check("native.submitBlockOutlineFires", n > 0, "n=" + n);
        check("fapi.beforeBlockOutlineFires", f > 0, "f=" + f);
        check("vanilla.outlineSubmitted", h >= f && f > 0, "h=" + h + " f=" + f);
        check("native.executeTranslucentFires", d1.get("native.executeTranslucent") > 0, "" + d1.get("native.executeTranslucent"));
        if (old) {
            check("old.startEnd", d1.get("old.start") > 0 && d1.get("old.end") > 0, "start=" + d1.get("old.start") + " end=" + d1.get("old.end"));
            check("old.beforeBlockOutline=fapi", near(d1.get("old.before"), f), "old=" + d1.get("old.before") + " fapi=" + f);
            check("old.blockOutline=before", near(d1.get("old.outline"), d1.get("old.before")), "outline=" + d1.get("old.outline"));
            check("old.afterEntities=executeTranslucent", near(d1.get("old.afterEntities"), d1.get("native.executeTranslucent")),
                "afterEntities=" + d1.get("old.afterEntities") + " executeTranslucent=" + d1.get("native.executeTranslucent"));
            check("order.outline", ordered(seq1, "NFOBH"), seq1);
            check("order.afterEntities", ordered(seq1, "TA"), seq1);
        }

        // phase 2: the 0.136 BLOCK_OUTLINE listener refuses (and draws its own outline)
        System.setProperty("cbs26d.cancel", "1");
        sleep(2000);
        a = snap();
        sleep(8000);
        Map<String, Long> d2 = delta(a, snap());
        String seq2 = System.getProperty("cbs26d.lastseq", "");
        System.clearProperty("cbs26d.cancel");
        log("phase2 " + d2 + " lastseq=" + seq2);
        if (old) {
            check("cancel.old.outlineRuns", d2.get("old.outline") > 0, "" + d2.get("old.outline"));
            check("cancel.vanillaOutlineSkipped", d2.get("native.submitHitOutline") == 0, "h=" + d2.get("native.submitHitOutline"));
            check("cancel.native.submitBlockOutlineStillFires", d2.get("native.submitBlockOutline") > 0, "" + d2.get("native.submitBlockOutline"));
            check("cancel.fapi.beforeStillFires", d2.get("fapi.before") > 0, "" + d2.get("fapi.before"));
            check("cancel.old.drawsOwnOutline", d2.get("old.draw") > 0, "" + d2.get("old.draw"));
        }

        // phase 3: 26.2 Fabric API's BEFORE_BLOCK_OUTLINE refuses: the route must not run
        System.setProperty("cbs26d.fapicancel", "1");
        sleep(2000);
        a = snap();
        sleep(6000);
        Map<String, Long> d3 = delta(a, snap());
        System.clearProperty("cbs26d.fapicancel");
        log("phase3 " + d3);
        check("fapiCancel.vanillaOutlineSkipped", d3.get("native.submitHitOutline") == 0 && d3.get("fapi.before") > 0,
            "h=" + d3.get("native.submitHitOutline") + " f=" + d3.get("fapi.before"));
        if (old) {
            check("fapiCancel.routeNotRun", d3.get("old.before") == 0 && d3.get("old.outline") == 0,
                "before=" + d3.get("old.before") + " outline=" + d3.get("old.outline"));
        }

        long pass = OK.values().stream().filter(b -> b).count();
        log(pass + "/" + OK.size());
        sleep(1000);
        m.execute(m::stop);
    }
}
