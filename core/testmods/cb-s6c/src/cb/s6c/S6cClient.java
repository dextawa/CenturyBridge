package cb.s6c;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1132;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_327;
import net.minecraft.class_3481;
import net.minecraft.class_3675;
import net.minecraft.class_374;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5321;
import net.minecraft.class_7529;
import net.minecraft.class_7530;
import net.minecraft.class_765;
import net.minecraft.class_7924;

/**
 * s6b's client checks (D:/tmp/main/client_checks/s6b.md) as a 1.21.5-era mod with Fabric API (see build.sh). Fabric API
 * hooks are registered at init (HUD layers, a world-render text draw, a key binding); a driver thread waits for the
 * joined world, judges what the hooks recorded, takes screenshots of the HUD and the three test screen modes, prints
 * "CB-S6C ok|FAIL <check> <detail>" and "CB-S6C N/T", then stops the client.
 */
public class S6cClient implements ClientModInitializer {
    static final Map<String, Boolean> OK = new LinkedHashMap<>();
    static final AtomicInteger HUD_CALLS = new AtomicInteger();
    static final StringBuilder FRAME = new StringBuilder();
    static volatile String lastFrame = "";
    static volatile String dupId = "not run", missingAnchor = "not run";
    static volatile boolean worldText;
    static volatile int worldRet = Integer.MIN_VALUE, worldWant, worldDraws, worldNoContext;
    static volatile double tx, ty, tz;
    static FabricKeyBinding key;
    static boolean keyRegistered;
    static boolean nativeW;

    static void declare(String... names) {
        for (String n : names) {
            OK.put(n, null);
        }
    }

    static void check(String name, boolean pass, String detail) {
        if (OK.get(name) != null) {
            return;
        }
        OK.put(name, pass);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    static void log(String s) {
        System.out.println("CB-S6C " + s);
    }

    static class_2960 id(String path) {
        return class_2960.method_60655("cb_s6c", path);
    }

    static void mark(String layer) {
        synchronized (FRAME) {
            if (FRAME.length() > 0) {
                FRAME.append(',');
            }
            FRAME.append(layer);
            if (layer.equals("top")) {
                lastFrame = FRAME.toString();
                FRAME.setLength(0);
            }
        }
    }

    @Override
    public void onInitializeClient() {
        nativeW = FabricLoader.getInstance().isModLoaded("cb_s6n");
        declare("join", "fapi.clientTags", "fapi.keyRegistered", "fapi.keyPressed",
            "hud.callbackOnce", "hud.duplicateId", "hud.missingAnchor", "hud.order", "hud.shot",
            "font.drawInBatchReturn", "font.worldShot",
            "widgets.textFieldSetValue", "widgets.editBoxValue", "widgets.toastManager",
            "screen.mode1Shot", "screen.mode2Shot", "screen.mode3Shot");
        if (nativeW) {
            declare("nativeW.guiInit", "nativeW.renderDeferredTooltip");
        }

        KeyBindingRegistry.INSTANCE.addCategory("cb_s6c.cat");
        key = FabricKeyBinding.Builder.create(id("key"), class_3675.class_307.field_1668, 74, "cb_s6c.cat").build();
        keyRegistered = KeyBindingRegistry.INSTANCE.register(key);

        HudLayerRegistrationCallback.EVENT.register(w -> {
            HUD_CALLS.incrementAndGet();
            w.addLayer(IdentifiedLayer.of(id("top"), (g, d) -> {
                g.method_25294(0, 0, 20, 20, 0xFFFF0000);
                mark("top");
            }));
            w.attachLayerAfter(IdentifiedLayer.HOTBAR_AND_BARS, id("after_bars"), (g, d) -> {
                g.method_25294(30, 0, 50, 20, 0xFF00FF00);
                mark("after_bars");
            });
            w.attachLayerBefore(IdentifiedLayer.CHAT, id("before_chat"), (g, d) -> {
                g.method_25294(60, 0, 80, 20, 0xFF0000FF);
                mark("before_chat");
            });
            w.removeLayer(IdentifiedLayer.CROSSHAIR);
            w.replaceLayer(IdentifiedLayer.HOTBAR_AND_BARS, l -> IdentifiedLayer.of(l.id(), (g, d) -> {
                mark("hotbar");
                g.method_51448().method_22903();
                g.method_51448().method_46416(0, -20, 0);
                l.render(g, d);
                g.method_51448().method_22909();
            }));
            try {
                w.addLayer(IdentifiedLayer.of(id("top"), (g, d) -> { }));
                dupId = "no exception";
            } catch (IllegalArgumentException e) {
                dupId = "IllegalArgumentException";
            } catch (Throwable t) {
                dupId = t.toString();
            }
            try {
                w.attachLayerAfter(id("no_such_anchor"), id("orphan"), (g, d) -> { });
                missingAnchor = "no exception";
            } catch (IllegalArgumentException e) {
                missingAnchor = "IllegalArgumentException";
            } catch (Throwable t) {
                missingAnchor = t.toString();
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            if (!worldText) {
                return;
            }
            class_4587 ps = ctx.matrixStack();
            class_4597 bs = ctx.consumers();
            if (ps == null || bs == null) {
                worldNoContext++;
                return;
            }
            class_327 font = class_310.method_1551().field_1772;
            class_243 cam = ctx.camera().method_19326();
            String s = "CB WORLD TEXT";
            float x = -font.method_1727(s) / 2f;
            ps.method_22903();
            ps.method_22904(tx - cam.field_1352, ty - cam.field_1351, tz - cam.field_1350);
            ps.method_22905(0.05f, -0.05f, 0.05f);
            worldRet = font.method_27521(s, x, 0, 0xFFFFFF, true, ps.method_23760().method_23761(), bs,
                class_327.class_6415.values()[0], 0, class_765.field_32767);
            ps.method_22909();
            worldWant = (int) (x + font.method_1727(s)) + 1;
            worldDraws++;
        });

        Thread t = new Thread(S6cClient::drive, "cb-s6c");
        t.setDaemon(true);
        t.start();
    }

    static <T> T on(Executor ex, Supplier<T> s) throws Exception {
        return CompletableFuture.supplyAsync(s, ex).get(20, TimeUnit.SECONDS);
    }

    static void run(Executor ex, Runnable r) throws Exception {
        CompletableFuture.runAsync(r, ex).get(20, TimeUnit.SECONDS);
    }

    static void shot(class_310 mc, String name) throws Exception {
        run(mc, () -> class_318.method_22690(mc.field_1697, name, mc.method_1522(), c -> { }));
        Thread.sleep(500);
    }

    static void drive() {
        try {
            class_310 mc = class_310.method_1551();
            long until = System.currentTimeMillis() + 240_000;
            while ((mc.field_1724 == null || mc.field_1687 == null || mc.method_1576() == null) && System.currentTimeMillis() < until) {
                Thread.sleep(500);
            }
            if (mc.field_1724 == null || mc.method_1576() == null) {
                check("join", false, "no world joined within 240 s");
                finish(mc);
                return;
            }
            Thread.sleep(4000);
            check("join", true, "player in world");
            class_1132 server = mc.method_1576();
            UUID uid = mc.field_1724.method_5667();
            Supplier<class_3222> sp = () -> server.method_3760().method_14602(uid);

            // ---- item 0: removed Fabric API modules
            try {
                boolean in = ClientTags.isInLocal(class_3481.field_15475,
                    class_5321.method_29179(class_7924.field_41254, class_2960.method_60656("stone")));
                check("fapi.clientTags", !in, "ClientTags.isInLocal(LOGS, stone) = " + in);
            } catch (Throwable e) {
                check("fapi.clientTags", false, "threw " + e);
            }
            boolean listed = on(mc, () -> Arrays.asList(mc.field_1690.field_1839).contains(key));
            check("fapi.keyRegistered", keyRegistered && listed, "register() = " + keyRegistered + ", in Options.keyMappings = " + listed
                + " (" + key.method_1431() + ")");

            // ---- item 1a: HUD layer registration
            check("hud.callbackOnce", HUD_CALLS.get() == 1, "HudLayerRegistrationCallback fired " + HUD_CALLS.get() + "x");
            check("hud.duplicateId", dupId.equals("IllegalArgumentException"), "addLayer with a duplicate id: " + dupId);
            check("hud.missingAnchor", missingAnchor.equals("IllegalArgumentException"), "attachLayerAfter a missing anchor: " + missingAnchor);

            // scene: survival, noon, standing on obsidian in the air facing north at an obsidian wall
            double[] at = on(server, () -> {
                class_3222 p = sp.get();
                class_3218 lvl = (class_3218) p.method_37908();
                p.method_7336(class_1934.field_9215);
                lvl.method_29199(6000);
                int bx = (int) Math.floor(p.method_23317()), bz = (int) Math.floor(p.method_23321()), by = 150;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        lvl.method_8501(new class_2338(bx + dx, by - 1, bz + dz), class_2246.field_10540.method_9564());
                    }
                }
                for (int dx = -5; dx <= 5; dx++) {
                    for (int dy = -1; dy <= 5; dy++) {
                        lvl.method_8501(new class_2338(bx + dx, by + dy, bz - 6), class_2246.field_10540.method_9564());
                    }
                }
                p.method_48105(lvl, bx + 0.5, by, bz + 0.5, Set.of(), 180f, 0f, true);
                return new double[] {bx + 0.5, by, bz + 0.5};
            });
            tx = at[0];
            ty = at[1] + 1.62;
            tz = at[2] - 4.5;
            worldText = true;
            Thread.sleep(4000);
            check("hud.order", lastFrame.equals("hotbar,after_bars,before_chat,top"), "our layers in one frame: [" + lastFrame + "]");
            check("font.drawInBatchReturn", worldDraws > 0 && worldRet == worldWant,
                "returned " + worldRet + ", (int)(x + width) + 1 = " + worldWant + ", draws " + worldDraws + ", no context " + worldNoContext);
            shot(mc, "cb-s6c-hud.png");
            check("hud.shot", true, "screenshot cb-s6c-hud.png (judge by eye: red square top-left over everything, green at x 30 and "
                + "blue at x 60, no crosshair, hotbar/bars/held item name 20 px higher, experience number unmoved)");
            check("font.worldShot", true, "screenshot cb-s6c-hud.png (judge by eye: opaque white 'CB WORLD TEXT' with shadow on the "
                + "obsidian wall)");

            // fabric-keybindings-v0: a real J press
            log("input ready: key j");
            boolean pressed = false;
            long keyUntil = System.currentTimeMillis() + 8000;
            while (!pressed && System.currentTimeMillis() < keyUntil) {
                Thread.sleep(200);
                pressed = on(mc, () -> key.method_1436());
            }
            check("fapi.keyPressed", pressed, "consumeClick after a real J press: " + pressed);
            worldText = false;

            // ---- item 1b: widgets and the test screen
            class_327 font = mc.field_1772;
            String tf = on(mc, () -> {
                class_7530 f = new class_7530(font, 100);
                f.method_44414("a\nb\nc");
                return f.method_44421();
            });
            check("widgets.textFieldSetValue", "a\nb\nc".equals(tf), "MultilineTextField.value() = " + tf.replace("\n", "\\n"));
            String toast = on(mc, () -> {
                try {
                    return String.valueOf(new class_374(mc) != null);
                } catch (Throwable e) {
                    return e.toString();
                }
            });
            check("widgets.toastManager", toast.equals("true"), "new ToastManager(minecraft): " + toast);

            for (int mode = 1; mode <= 3; mode++) {
                S6cScreen s = new S6cScreen(mode);
                run(mc, () -> mc.method_1507(s));
                long su = System.currentTimeMillis() + 10_000;
                while (s.frames < 30 && System.currentTimeMillis() < su) {
                    Thread.sleep(200);
                }
                Thread.sleep(500);
                if (mode == 1) {
                    class_7529 box = s.box;
                    String v = box == null ? "no box" : on(mc, box::method_44405);
                    check("widgets.editBoxValue", "multi line\nedit box".equals(v), "MultiLineEditBox.getValue() = " + v.replace("\n", "\\n"));
                }
                shot(mc, "cb-s6c-screen" + mode + ".png");
                String what = mode == 1
                    ? "end portal effect over the whole screen; blue box covering 'UNDER'; a diamond; 'cb positioned tooltip'; the edit box "
                        + "light grey text with shadow, cursor, background and scroll decorations; three splashes: opaque, half-transparent, opaque yellow"
                    : mode == 2 ? "the world blurred behind the screen; tooltip 'cb' at the mouse"
                    : "the title panorama; no 'cb' tooltip";
                check("screen.mode" + mode + "Shot", s.frames > 0, "screenshot cb-s6c-screen" + mode + ".png, " + s.frames
                    + " frames (judge by eye: " + what + ")");
            }
            run(mc, () -> mc.method_1507(null));

            // ---- native-W: a native 1.21.6 mod's HEAD injections still fire with the layer present
            if (nativeW) {
                String gi = System.getProperty("cb.s6n.guiInit", "0"), dt = System.getProperty("cb.s6n.renderDeferredTooltip", "0");
                check("nativeW.guiInit", !gi.equals("0"), "native @Inject(HEAD) Gui.<init> fired " + gi + "x");
                check("nativeW.renderDeferredTooltip", !dt.equals("0"), "native @Inject(HEAD) GuiGraphics.renderDeferredTooltip fired " + dt + "x");
            }
        } catch (Throwable t) {
            log("FAIL driver " + t);
            t.printStackTrace();
        }
        finish(class_310.method_1551());
    }

    static void finish(class_310 mc) {
        int ok = 0, fail = 0;
        for (Map.Entry<String, Boolean> e : OK.entrySet()) {
            if (e.getValue() == null) {
                log("FAIL " + e.getKey() + " never ran");
                fail++;
            } else if (e.getValue()) {
                ok++;
            } else {
                fail++;
            }
        }
        log("summary ok=" + ok + " fail=" + fail);
        log(ok + "/" + (ok + fail));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        mc.execute(mc::method_1592);
    }
}
