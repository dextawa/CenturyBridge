package cb.smx1dn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_12206;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_315;

/**
 * Driver of the smx1d check (native 1.21.11). Once the world is joined it hands the climbed 1.21.10 mod cb_smx1d two
 * same-tick references through the system properties -- "cbsmx1d.snapGraphics" (records 1.21.11's improvedTransparency
 * per phase) and "cbsmx1d.expectColors" (1.21.11's SKY_COLOR / CLOUD_COLOR attribute values at a position) -- sets
 * "cbsmx1d.go", waits for "cbsmx1d.old.done", prints every result as "CB-SMX1D ok|FAIL <check> <detail>" (an expected
 * check without a result is a FAIL), the tally "CB-SMX1D n/t", and stops the client.
 */
public class Smx1dNative implements ClientModInitializer {
    static final String[] EXPECTED = {"vertex.callOwn", "vertex.ownSuperReachesNative", "vertex.overrideFromVanilla",
        "vertex.setColorFill", "vertex.bufferBuilderNative", "shapes.renderLineBox62293", "shapes.renderLineBox62292",
        "shapes.renderLineBox62295", "shapes.filledBox62294", "shapes.renderFaceUp62297", "shapes.renderVector62298",
        "shapes.renderShapeCube62296", "cycle.initialValue", "cycle.defaultFirst", "cycle.onOffFalse",
        "cycle.displayOnlyValue", "cycle.nativeBuild", "graphics.getFabulous", "graphics.getNotFabulous",
        "graphics.renderDistanceKept", "graphics.noPresetApplied", "graphics.nativeSetGet", "graphics.statusIds",
        "level.skyColor", "level.cloudColor", "debug.filledBox", "debug.floatingText", "debug.voxelShape", "debug.path",
        "upload.cancelledFirst", "upload.unreachableEmpty", "upload.finished", "upload.secondCallEmpty"};

    static void log(String s) {
        System.out.println("CB-SMX1D " + s);
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
        boolean old = FabricLoader.getInstance().isModLoaded("cb_smx1d");
        Thread t = new Thread(() -> drive(old), "cb-smx1d-driver");
        t.setDaemon(true);
        t.start();
    }

    static void drive(boolean old) {
        class_310 mc = class_310.method_1551();
        while (mc == null || mc.field_1724 == null || mc.field_1687 == null) {
            sleep(500);
            mc = class_310.method_1551();
        }
        final class_310 m = mc;
        log("joined; old mod loaded: " + old);
        sleep(5000);
        Map<String, Boolean> transparency = new TreeMap<>();
        System.getProperties().put("cbsmx1d.snapGraphics", (Runnable) () -> {
            class_315 o = m.field_1690;
            transparency.put(System.getProperty("cbsmx1d.g.phase"), o.method_75337().method_41753());
        });
        System.getProperties().put("cbsmx1d.expectColors", (Function<Object, int[]>) pos -> new int[] {
            m.field_1687.method_75728().method_75695(class_12206.field_63746, (class_243) pos),
            m.field_1687.method_75728().method_75694(class_12206.field_64126)});
        System.setProperty("cbsmx1d.go", "1");
        long until = System.currentTimeMillis() + 120000;
        while (System.getProperty("cbsmx1d.old.done") == null && System.currentTimeMillis() < until) {
            sleep(500);
        }
        List<Boolean> ok = new ArrayList<>();
        for (String k : EXPECTED) {
            String v = System.getProperty("cbsmx1d.r." + k);
            boolean pass = v != null && v.startsWith("ok ");
            ok.add(pass);
            log((pass ? "ok " : "FAIL ") + k + " " + (v == null ? "no result" : v.substring(v.indexOf(' ') + 1)));
        }
        Boolean fab = transparency.get("fabulous"), fancy = transparency.get("fancy");
        boolean p1 = Boolean.TRUE.equals(fab);
        ok.add(p1);
        log((p1 ? "ok " : "FAIL ") + "graphics.improvedTransparencyOn " + fab);
        boolean p2 = Boolean.FALSE.equals(fancy);
        ok.add(p2);
        log((p2 ? "ok " : "FAIL ") + "graphics.improvedTransparencyOff " + fancy);
        log("counters addVertex=" + Probe.get("addVertex") + " optionGet=" + Probe.get("optionGet") + " optionSet="
            + Probe.get("optionSet") + " optionSetGraphics=" + Probe.get("optionSetGraphics") + " applyPreset="
            + Probe.get("applyPreset") + " cycleBuild=" + Probe.get("cycleBuild"));
        log(ok.stream().filter(b -> b).count() + "/" + ok.size());
        sleep(1000);
        m.execute(m::method_1592);
    }
}
