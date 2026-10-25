package cb.clienttest;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_1921;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_327;
import net.minecraft.class_437;
import net.minecraft.class_490;

/**
 * cb-clienttest: a 1.20.1-era client mod (intermediary names, no Fabric API) that checks the client API a
 * climbed mod uses, on the real client, once a world is joined. A driver thread waits for the player,
 * then runs the phases on the render thread: font measurements and render type getters, the GUI screen
 * (GuiScreen: GuiGraphics, Font, RenderType quads), a mod container screen, a vanilla inventory screen
 * subclass, the vanilla inventory screen under a mod's render mixin, and the world view; each screen
 * phase ends with a screenshot through the 1.20.1 Screenshot API (screenshots/cb-<phase>.png). Lines:
 * "CB-CLIENTTEST ok|FAIL <check> <detail>", then "CB-CLIENTTEST summary ok=N fail=M" and
 * "CB-CLIENTTEST N/T"; then the client stops itself.
 *
 * The reference values pinned below (REF_*) are what the same jar measures on a native 1.20.1 client.
 */
public class ClientTestMod implements ClientModInitializer {
    static final String TEXT = GuiScreen.TEXT;
    static final String WRAP = "aaa bbb ccc ddd eee fff";
    static final int REF_WIDTH = 72;      // Font.width("CenturyBridge"), native 1.20.1
    static final int REF_WRAP_LINES = 3;  // Font.split(literal(WRAP), 50).size(), native 1.20.1
    static final long PHASE_MS = 4000;

    @Override
    public void onInitializeClient() {
        Tally.declare("join", "font.lineHeight", "font.width", "font.widthComponent", "font.plainSubstrByWidth",
            "font.split", "font.wordWrapHeight", "rt.getters", "rt.memoized",
            "gui.backdrop", "gui.fill", "gui.fillGradient", "gui.blit", "gui.drawString", "gui.drawCentered",
            "gui.pose", "gui.scissor", "gui.outline", "gui.item", "gui.entity", "gui.tooltip",
            "font.drawInBatch", "font.outline",
            "rt.gui", "rt.entitySolid", "rt.entityCutoutNoCull", "rt.entityTranslucent", "rt.entitySmoothCutout",
            "rt.entityDecal", "rt.itemEntityTranslucentCull", "rt.eyes", "rt.text",
            "container.renderBg", "container.renderLabels", "container.layout",
            "inventorySub.renderBg", "inventorySub.superReturns",
            "mixin.containerRenderHead", "mixin.containerRenderReturn",
            "screenshot.gui", "screenshot.container", "screenshot.inventorySub", "screenshot.inventory",
            "screenshot.world");
        Thread t = new Thread(ClientTestMod::drive, "cb-clienttest");
        t.setDaemon(true);
        t.start();
    }

    private static void drive() {
        try {
            class_310 mc = class_310.method_1551();
            long until = System.currentTimeMillis() + 240_000;
            boolean world = true;
            while (!onRender(() -> mc.field_1724 != null && mc.field_1687 != null)) {
                if (System.currentTimeMillis() > until) {
                    world = false;
                    break;
                }
                Thread.sleep(500);
            }
            if (world) {
                Tally.ok("join", "player in world");
                Thread.sleep(5000); // chunks around the player
                onRender(() -> {
                    mc.field_1724.method_36456(0f);
                    mc.field_1724.method_36457(10f);
                    return null;
                });
            } else {
                // the checks that need no player still run (over whatever screen is up), the rest never run
                Tally.fail("join", "no world joined within 240 s; checks needing a player are skipped");
            }
            onRender(() -> {
                fontChecks(mc.field_1772);
                renderTypeChecks();
                return null;
            });
            screenPhase(mc, "gui", GuiScreen::new, () -> Frames.settle("gui."), () -> {
                Frames.settle("font.");
                Frames.settle("rt.");
            });
            if (!world) {
                finish(mc);
                return;
            }
            screenPhase(mc, "container", () -> new TestContainerScreen(mc.field_1724.field_7498, mc.field_1724.method_31548()), () -> {
                Tally.check("container.renderBg", TestContainerScreen.bgCalls > 0, TestContainerScreen.bgCalls + " calls");
                Tally.check("container.renderLabels", TestContainerScreen.labelCalls > 0, TestContainerScreen.labelCalls + " calls");
                Tally.check("container.layout", TestContainerScreen.bgCalls > 0 && TestContainerScreen.bgProblem == null,
                    TestContainerScreen.bgProblem == null ? "leftPos/topPos as 1.20.1, frame delta <= " + TestContainerScreen.maxDelta
                        : TestContainerScreen.bgProblem);
            }, null);
            screenPhase(mc, "inventorySub", () -> new TestInventoryScreen(mc.field_1724), () -> {
                Tally.check("inventorySub.renderBg", TestInventoryScreen.bgCalls > 0, TestInventoryScreen.bgCalls + " calls");
                Tally.check("inventorySub.superReturns", TestInventoryScreen.superReturned == TestInventoryScreen.bgCalls
                    && TestInventoryScreen.bgCalls > 0, TestInventoryScreen.superReturned + "/" + TestInventoryScreen.bgCalls);
            }, null);
            onRender(() -> {
                ContainerProbe.reset();
                return null;
            });
            screenPhase(mc, "inventory", () -> new class_490(mc.field_1724), () -> {
                Tally.check("mixin.containerRenderHead", ContainerProbe.heads > 0 && ContainerProbe.lastHead == class_490.class,
                    ContainerProbe.heads + " calls, last on " + (ContainerProbe.lastHead == null ? "-" : ContainerProbe.lastHead.getName()));
                Tally.check("mixin.containerRenderReturn", ContainerProbe.rets > 0 && ContainerProbe.rets <= ContainerProbe.heads,
                    ContainerProbe.rets + " returns / " + ContainerProbe.heads + " heads");
            }, null);
            screenPhase(mc, "world", () -> null, null, null);
            finish(mc);
        } catch (Throwable t) {
            Tally.log("driver died: " + t);
            t.printStackTrace();
            try {
                finish(class_310.method_1551());
            } catch (Throwable t2) {
                Tally.log("stop failed: " + t2);
            }
        }
    }

    interface ScreenMaker {
        class_437 make();
    }

    /** opens the screen (null: none), lets it draw, screenshots it, closes it, gives the verdicts */
    private static void screenPhase(class_310 mc, String phase, ScreenMaker maker, Runnable verdicts, Runnable moreVerdicts)
            throws Exception {
        Tally.log("phase " + phase);
        onRender(() -> {
            mc.method_1507(maker.make());
            return null;
        });
        Thread.sleep(PHASE_MS);
        String name = "cb-" + phase + ".png";
        File shot = new File(new File(mc.field_1697, "screenshots"), name);
        shot.delete();
        String[] msg = new String[1];
        try {
            onRender(() -> {
                class_318.method_22690(mc.field_1697, name, mc.method_1522(), c -> msg[0] = c.getString());
                return null;
            });
        } catch (Exception e) {
            Tally.error("screenshot." + phase, e.getCause() == null ? e : e.getCause());
        }
        for (int i = 0; i < 20 && !(shot.isFile() && shot.length() > 0); i++) {
            Thread.sleep(250);
        }
        if (!Tally.reported("screenshot." + phase)) {
            Tally.check("screenshot." + phase, shot.isFile() && shot.length() > 0,
                shot.isFile() ? shot.length() + " bytes; " + msg[0] : "no " + shot + "; " + msg[0]);
        }
        onRender(() -> {
            if (verdicts != null) {
                verdicts.run();
            }
            if (moreVerdicts != null) {
                moreVerdicts.run();
            }
            mc.method_1507(null);
            return null;
        });
        Thread.sleep(500);
    }

    private static void fontChecks(class_327 font) {
        Tally.check("font.lineHeight", font.field_2000 == 9, "lineHeight=" + font.field_2000);
        int w = font.method_1727(TEXT);
        Tally.check("font.width", w == REF_WIDTH, "width(\"" + TEXT + "\")=" + w + " (1.20.1: " + REF_WIDTH + ")");
        int wc = font.method_27525(class_2561.method_43470(TEXT));
        Tally.check("font.widthComponent", wc == w, "width(Component)=" + wc);
        String sub = font.method_27523(TEXT, 40);
        boolean subOk = TEXT.startsWith(sub) && font.method_1727(sub) <= 40
            && (sub.length() == TEXT.length() || font.method_1727(TEXT.substring(0, sub.length() + 1)) > 40);
        Tally.check("font.plainSubstrByWidth", subOk, "\"" + sub + "\"");
        List<?> lines = font.method_1728(class_2561.method_43470(WRAP), 50);
        Tally.check("font.split", lines.size() == REF_WRAP_LINES, lines.size() + " lines (1.20.1: " + REF_WRAP_LINES + ")");
        int hgt = font.method_1713(WRAP, 50);
        Tally.check("font.wordWrapHeight", hgt == lines.size() * font.field_2000, "height=" + hgt);
    }

    private static void renderTypeChecks() {
        Object[] types = {class_1921.method_51784(), class_1921.method_23577(), class_1921.method_23581(),
            class_1921.method_23583(), class_1921.method_29997(), class_1921.method_23594(),
            class_1921.method_23572(GuiScreen.STONE), class_1921.method_23578(GuiScreen.STONE),
            class_1921.method_23580(GuiScreen.STONE), class_1921.method_23584(GuiScreen.STONE),
            class_1921.method_23586(GuiScreen.STONE), class_1921.method_29379(GuiScreen.STONE),
            class_1921.method_23573(GuiScreen.STONE), class_1921.method_23026(GuiScreen.STONE),
            class_1921.method_23028(GuiScreen.STONE), class_1921.method_23030(GuiScreen.STONE)};
        int nulls = 0;
        for (Object o : types) {
            if (o == null) {
                nulls++;
            }
        }
        Tally.check("rt.getters", nulls == 0, types.length + " getters, " + nulls + " null");
        boolean same = class_1921.method_23572(GuiScreen.STONE) == class_1921.method_23572(GuiScreen.STONE)
            && class_1921.method_23580(GuiScreen.STONE) == class_1921.method_23580(GuiScreen.STONE);
        Tally.check("rt.memoized", same, "entitySolid/entityTranslucent of one texture are one instance");
    }

    private static void finish(class_310 mc) throws Exception {
        Tally.finish();
        Thread.sleep(1000);
        onRender(() -> {
            mc.method_1592();
            return null;
        });
    }

    interface Call<T> {
        T call() throws Throwable;
    }

    /** runs on the render thread and waits for the answer (its exception comes back wrapped) */
    static <T> T onRender(Call<T> c) throws Exception {
        CompletableFuture<T> f = new CompletableFuture<>();
        class_310.method_1551().execute(() -> {
            try {
                f.complete(c.call());
            } catch (Throwable t) {
                f.completeExceptionally(t);
            }
        });
        return f.get(60, TimeUnit.SECONDS);
    }
}
