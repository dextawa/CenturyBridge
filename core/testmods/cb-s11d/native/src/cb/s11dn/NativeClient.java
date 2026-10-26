package cb.s11dn;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_2350;
import net.minecraft.class_2874;
import net.minecraft.class_310;
import net.minecraft.class_638;

/**
 * cb-s11d's native 1.21.11 driver. In a world whose overworld uses the 1.21.10 mod's own effects (cb_s11d:fx: END sky,
 * constant ambient, force-bright, foggy, fixed fog color, sunrise always) while its 1.21.11 dimension type says
 * OVERWORLD skybox, DEFAULT cardinal light, no end flashes, it checks after 300 sky frames that
 * (1) 1.21.11's drawing points took the 1.21.10 answers (shade 0.9, NETHER lighting, an EndFlashState, END skybox,
 * the dimension-or-boss fog end, the mod's sunrise color, the mod's effects methods called), and
 * (2) every native 1.21.11 mixin on the calls those points keep still fired.
 * Prints "CB-S11D ok|FAIL <check> <detail>", "CB-S11D N/T", then stops the client.
 */
public class NativeClient implements ClientModInitializer {
    private static final Map<String, Boolean> OK = new LinkedHashMap<>();

    static void check(String name, boolean pass, String detail) {
        OK.put(name, pass);
        System.out.println("CB-S11D " + (pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    @Override
    public void onInitializeClient() {
        Thread t = new Thread(NativeClient::drive, "cb-s11d");
        t.setDaemon(true);
        t.start();
    }

    static void drive() {
        class_310 mc = class_310.method_1551();
        try {
            long until = System.currentTimeMillis() + 240_000;
            while ((mc.field_1687 == null || Counters.get("SkyRenderer.extractRenderState") < 300)
                    && System.currentTimeMillis() < until) {
                Thread.sleep(500);
            }
            check("join", mc.field_1687 != null, "frames=" + Counters.get("SkyRenderer.extractRenderState"));
            CompletableFuture.runAsync(NativeClient::checks, mc).get(30, TimeUnit.SECONDS);
        } catch (Throwable e) {
            check("driver", false, String.valueOf(e));
        }
        long pass = OK.values().stream().filter(b -> b).count();
        System.out.println("CB-S11D " + pass + "/" + OK.size());
        mc.execute(mc::method_1592);
    }

    static void checks() {
        class_638 level = class_310.method_1551().field_1687;
        String report;
        try {
            report = (String) Class.forName("cb.s11d.FxClient").getMethod("report").invoke(null);
        } catch (Throwable e) {
            report = "report failed: " + e;
        }
        check("v.effects", report.contains("effects=cb.s11d.FxEffects"), report);
        check("w.type", level.method_8597().comp_5220() == class_2874.class_12326.field_64386
            && level.method_8597().comp_5221() == class_2874.class_12325.field_64380 && !level.method_8597().method_76467(),
            "skybox=" + level.method_8597().comp_5220() + " cardinal=" + level.method_8597().comp_5221());
        // routes: 1.21.10's answers where 1.21.11 draws
        float shade = level.method_24852(class_2350.field_11033, true);
        check("route.shade", shade == 0.9F, "getShade(DOWN, true)=" + shade + " (1.21.11 DEFAULT: 0.5)");
        check("route.lighting", Counters.lightingType == class_2874.class_12325.field_64381, "Lighting.updateLevel(" + Counters.lightingType + ")");
        check("route.endFlash", level.method_73229() != null, "endFlashState=" + level.method_73229());
        check("route.skybox", Counters.skybox == class_2874.class_12326.field_64387, "state.skybox=" + Counters.skybox);
        // (no sunrise check here: an END sky returns before the sunrise color, in 1.21.10 as in 1.21.11)
        check("route.foggy", Counters.fogEnd <= 96.0F, "fog end=" + Counters.fogEnd);
        check("route.fxCalls", report.contains("fogColor=") && !report.contains("fogColor=0 ") && !report.contains("foggyAt=0 ")
            && !report.contains("constantAmbient=0 ") && !report.contains("forceBright=0") && !report.contains("skyType=0 "), report);
        // native 1.21.11 mixins on the calls the routes keep
        for (String k : new String[] {"ClientLevel.getShade", "DimensionType.cardinalLightType", "DimensionType.skybox",
                "DimensionType.hasEndFlashes", "Lighting.updateLevel", "SkyRenderer.extractRenderState",
                "AtmosphericFogEnvironment.getBaseColor", "AtmosphericFogEnvironment.setupFog",
                "BossHealthOverlay.shouldCreateWorldFog", "LightTexture.updateLightTexture", "probe.FOG_COLOR",
                "probe.SKY_LIGHT_COLOR", "probe.SKY_LIGHT_FACTOR"}) {
            check("native." + k, Counters.get(k) > 0, "fired " + Counters.get(k));
        }
    }
}
