package cb.s9c;

import com.mojang.authlib.GameProfile;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_1132;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2591;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3730;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_5616;
import net.minecraft.class_5619;
import net.minecraft.class_8685;

/** s9's client checks on a real client (see build.sh). */
public class S9cClient implements ClientModInitializer {
    public static final AtomicInteger RENDERS = new AtomicInteger();
    public static final AtomicInteger LAYERS = new AtomicInteger();
    public static final AtomicInteger BE_CUBE = new AtomicInteger();
    public static final AtomicInteger BE_CHEST = new AtomicInteger();
    public static final AtomicInteger BE_LAMBDA = new AtomicInteger();
    public static volatile int lastEntityLight = -1;
    public static volatile int lastBeLight = -1;
    static volatile float minPartial = 2, maxPartial = -1;
    static final Map<String, Boolean> OK = new LinkedHashMap<>();
    static class_1297 zombie;

    public static void notePartial(float pt) {
        minPartial = Math.min(minPartial, pt);
        maxPartial = Math.max(maxPartial, pt);
    }

    /** six faces of a cube of side s at the pose origin, mod texture, given light */
    public static void cubeFaces(class_4587.class_4665 p, class_4588 vc, float s, int light) {
        float[][] f = {
            {0, 0, 0, 0, s, 0, s, s, 0, s, 0, 0, 0, 0, -1}, {0, 0, s, s, 0, s, s, s, s, 0, s, s, 0, 0, 1},
            {0, 0, 0, 0, 0, s, 0, s, s, 0, s, 0, -1, 0, 0}, {s, 0, 0, s, s, 0, s, s, s, s, 0, s, 1, 0, 0},
            {0, 0, 0, s, 0, 0, s, 0, s, 0, 0, s, 0, -1, 0}, {0, s, 0, 0, s, s, s, s, s, s, s, 0, 0, 1, 0}};
        float[][] uv = {{0, 0}, {0, 1}, {1, 1}, {1, 0}};
        for (float[] q : f) {
            for (int i = 0; i < 4; i++) {
                vc.method_56824(p, q[i * 3], q[i * 3 + 1], q[i * 3 + 2]).method_1336(255, 255, 255, 255).method_22913(uv[i][0], uv[i][1])
                    .method_22922(class_4608.field_21444).method_60803(light).method_60831(p, q[12], q[13], q[14]);
            }
        }
    }

    static void check(String name, boolean pass, String detail) {
        OK.put(name, pass);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    static void log(String s) {
        System.out.println("CB-S9C " + s);
    }

    @Override
    public void onInitializeClient() {
        try {
            // EntityRenderers.register / BlockEntityRenderers.register: private, opened by the mod's access widener
            class_5619.method_32173(class_1299.field_6051, CbZombieRenderer::new);
            class_5616.method_32144(class_2591.field_16411, BeRenderers.Cube::new);
            class_5616.method_32144(class_2591.field_11914, BeRenderers.Chest::new);
            class_5616.method_32144(class_2591.field_11903, BeRenderers::lambda);
            log("renderers registered");
        } catch (Throwable t) {
            log("FAIL register " + t);
            t.printStackTrace();
        }
        Thread t = new Thread(S9cClient::drive, "cb-s9c");
        t.setDaemon(true);
        t.start();
    }

    static <T> T on(Executor ex, Supplier<T> s) throws Exception {
        return CompletableFuture.supplyAsync(s, ex).get(30, TimeUnit.SECONDS);
    }

    static void run(Executor ex, Runnable r) throws Exception {
        CompletableFuture.runAsync(r, ex).get(30, TimeUnit.SECONDS);
    }

    static void shot(class_310 mc, String name) throws Exception {
        run(mc, () -> class_318.method_22690(mc.field_1697, name, mc.method_1522(), 1, c -> { }));
        log("screenshot " + name);
    }

    static void drive() {
        class_310 mc = class_310.method_1551();
        try {
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
            class_1132 server = mc.method_1576();
            UUID id = mc.field_1724.method_5667();

            // the scene: the player looks north; a client-side zombie 4 blocks ahead; barrel, chest, furnace beside it
            int[] at = on(server, () -> {
                class_3222 p = server.method_3760().method_14602(id);
                class_3218 lvl = (class_3218) p.method_37908();
                int x = (int) Math.floor(p.method_23317()), y = (int) Math.floor(p.method_23318()), z = (int) Math.floor(p.method_23321());
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -6; dz <= -1; dz++) {
                        for (int dy = 0; dy <= 3; dy++) {
                            lvl.method_8501(new class_2338(x + dx, y + dy, z + dz), class_2246.field_10124.method_9564());
                        }
                        lvl.method_8501(new class_2338(x + dx, y - 1, z + dz), class_2246.field_10340.method_9564());
                    }
                }
                lvl.method_8501(new class_2338(x - 2, y, z - 3), class_2246.field_16328.method_9564());
                lvl.method_8501(new class_2338(x + 2, y, z - 3), class_2246.field_10034.method_9564());
                lvl.method_8501(new class_2338(x + 1, y, z - 5), class_2246.field_10181.method_9564());
                p.method_48105(lvl, x + 0.5, y, z + 0.5, java.util.Set.of(), 180.0F, 10.0F, true);
                lvl.method_29199(6000);
                return new int[] {x, y, z};
            });
            Thread.sleep(2000);
            run(mc, () -> {
                zombie = class_1299.field_6051.method_5883(mc.field_1687, class_3730.field_16462);
                zombie.method_5808(at[0] + 0.5, at[1], at[2] - 4 + 0.5, 0.0F, 0.0F);
                zombie.method_5665(class_2561.method_43470("cb s9 zombie"));
                zombie.method_5880(true);
                mc.field_1687.method_53875(zombie);
            });
            Thread.sleep(4000);
            int dayLight = lastEntityLight;
            shot(mc, "cb-s9c-day.png");
            check("2a.rendererRuns", RENDERS.get() > 0, "renderer render calls " + RENDERS.get() + " (screenshot cb-s9c-day.png: zombie, diamond quad above it, name tag)");
            check("2a.layerRuns", LAYERS.get() > 0, "layer render calls " + LAYERS.get());
            check("3a.directRenderer", BE_CUBE.get() > 0, "barrel cube renderer calls " + BE_CUBE.get() + ", partialTick range " + minPartial + ".." + maxPartial);
            check("3a.partialTickPasses", maxPartial > minPartial && maxPartial <= 1 && minPartial >= 0, "partialTick seen " + minPartial + ".." + maxPartial);
            check("3a.chestSubclass", BE_CHEST.get() > 0, "chest renderer subclass calls " + BE_CHEST.get());
            check("3a.lambda", BE_LAMBDA.get() > 0, "furnace lambda renderer calls " + BE_LAMBDA.get());

            // a roof and three walls over the scene (open towards the player): the packed light passed to render drops
            run(server, () -> {
                class_3218 lvl = server.method_30002();
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dz = -7; dz <= -1; dz++) {
                        lvl.method_8501(new class_2338(at[0] + dx, at[1] + 4, at[2] + dz), class_2246.field_10340.method_9564());
                        for (int dy = 0; dy <= 3; dy++) {
                            if (dx == -4 || dx == 4 || dz == -7) {
                                lvl.method_8501(new class_2338(at[0] + dx, at[1] + dy, at[2] + dz), class_2246.field_10340.method_9564());
                            }
                        }
                    }
                }
                lvl.method_29199(18000);
            });
            Thread.sleep(5000);
            int nightLight = lastEntityLight;
            shot(mc, "cb-s9c-night.png");
            check("2a.lightFollows", dayLight != nightLight && dayLight >= 0 && nightLight >= 0,
                "packed light passed to render: open sky " + Integer.toHexString(dayLight) + ", under the roof " + Integer.toHexString(nightLight)
                    + " (screenshot cb-s9c-night.png, night under a roof: quad dark like the zombie)");
            run(server, () -> server.method_30002().method_29199(6000));
            run(mc, () -> zombie.method_5834(true));
            Thread.sleep(3000);
            shot(mc, "cb-s9c-glowing.png");
            log("2c glowing: judge cb-s9c-glowing.png (is the mod quad outlined? known gap: expected not)");
            run(mc, () -> zombie.method_5834(false));

            // 4b / 4c: skins (offline client: the default skin)
            GameProfile gp = mc.field_1724.method_7334();
            String skin = on(mc, () -> {
                class_8685 s = mc.method_1582().method_52862(gp);
                class_8685 l = mc.method_1582().method_52858(gp).get();
                return s.comp_1626() + " | url " + s.comp_1911() + " | cape " + s.comp_1627() + " | elytra " + s.comp_1628() + " | model "
                    + s.comp_1629() + " id " + s.comp_1629().method_52856() + " | lookup " + l.comp_1626();
            });
            check("4b.offlineDefault", skin.startsWith("minecraft:textures/entity/player/") && skin.contains("url null"), skin);
            class_2960 given = class_2960.method_60656("textures/entity/player/slim/alex.png");
            class_8685 made = new class_8685(given, null, null, null, class_8685.class_7920.field_41122, false);
            check("4c.recordGivenTexture", made.comp_1626().equals(given) && made.comp_1629().method_52856().equals("slim"),
                "texture " + made.comp_1626() + ", model id " + made.comp_1629().method_52856());

            // batch 9: RenderTarget.viewWidth / viewHeight
            String rt = on(mc, () -> {
                class_276 t = mc.method_1522();
                return t.field_1480 + "x" + t.field_1477 + " / " + t.field_1482 + "x" + t.field_1481;
            });
            String[] parts = rt.split(" / ");
            check("b9.viewSize", parts[0].equals(parts[1]), "viewWidth x viewHeight / width x height: " + rt);
        } catch (Throwable t) {
            log("FAIL driver " + t);
            t.printStackTrace();
        }
        finish(mc);
    }

    static void finish(class_310 mc) {
        int ok = 0;
        for (boolean b : OK.values()) {
            ok += b ? 1 : 0;
        }
        log("summary ok=" + ok + " fail=" + (OK.size() - ok));
        log(ok + "/" + OK.size());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        mc.execute(mc::method_1592);
    }
}
