package cb.s7rest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1430;
import net.minecraft.class_155;
import net.minecraft.class_156;
import net.minecraft.class_1646;
import net.minecraft.class_2338;
import net.minecraft.class_2653;
import net.minecraft.class_2820;
import net.minecraft.class_3215;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_3695;
import net.minecraft.class_5253;
import net.minecraft.class_5949;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.1-era server mod (intermediary names, Fabric Loader API only) for s7rest's bridges of the
 * 1.21.1 -> 1.21.2 step. The server runs with spawn-animals=false and spawn-npcs=false in
 * server.properties: MinecraftServer.isSpawningAnimals / areNpcsEnabled must answer false (the dedicated
 * server's properties), the chunk cache's friendly-spawn flag must be off, and a cow and a villager added
 * to the level must be discarded within a few ticks (1.21.1's ServerLevel.shouldDiscardEntity). Then
 * setSpawnSettings(true, true) through Level must turn the friendly flag on again. Every check prints one
 * "CB-RESTTEST ok|FAIL ..." line; the last is "CB-RESTTEST DONE <passed>/<checks>".
 */
public class S7RestTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    /** the server runs with spawn-animals=false and spawn-npcs=false (cbs7rest.off=true), else with the defaults */
    static final boolean OFF = Boolean.parseBoolean(System.getProperty("cbs7rest.off", "true"));
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-RESTTEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static boolean friendly(class_3218 level) throws Exception {
        Field f = class_3215.class.getDeclaredField("field_13941");
        f.setAccessible(true);
        return f.getBoolean(level.method_14178());
    }

    @Override
    public void onInitialize() {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(() -> start(server));
                    return;
                }
            }
        }, "cb-s7rest");
        t.setDaemon(true);
        t.start();
    }

    static class_1430 cow;
    static class_1646 villager;

    static void start(MinecraftServer server) {
        try {
            class_3218 level = server.method_30002();
            check(server.method_3796() == !OFF, "isSpawningAnimals(): " + !OFF + " with spawn-animals=" + !OFF);
            check(server.method_3736() == !OFF, "areNpcsEnabled(): " + !OFF + " with spawn-npcs=" + !OFF);
            check(friendly(level) == !OFF, "the chunk cache's friendly-spawn flag follows isSpawningAnimals (" + !OFF + ")");

            class_2338 spawn = level.method_43126();
            // no player is online: force the spawn chunk so the level ticks its entities
            level.method_17988(spawn.method_10263() >> 4, spawn.method_10260() >> 4, true);
            cow = new class_1430(class_1299.field_6085, level);
            cow.method_5814(spawn.method_10263() + 0.5, spawn.method_10264() + 1, spawn.method_10260() + 0.5);
            villager = new class_1646(class_1299.field_6077, level);
            villager.method_5814(spawn.method_10263() + 2.5, spawn.method_10264() + 1, spawn.method_10260() + 0.5);
            check(level.method_8649(cow), "a cow can be added to the level");
            check(level.method_8649(villager), "a villager can be added to the level");

            // the rest of the checks run after the server ticked the entities a few times
            new Thread(() -> {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    return;
                }
                server.execute(() -> finish(server));
            }, "cb-s7rest-2").start();
        } catch (Throwable e) {
            check(false, "start threw " + e);
            e.printStackTrace(System.out);
            done();
        }
    }

    static void finish(MinecraftServer server) {
        try {
            class_3218 level = server.method_30002();
            System.out.println("CB-RESTTEST info cow tickCount=" + cow.field_6012 + " removed=" + cow.method_31481()
                + ", villager tickCount=" + villager.field_6012 + " removed=" + villager.method_31481());
            check(cow.method_31481() == OFF, OFF ? "the cow was discarded (animals do not spawn here)" : "the cow lives (animals spawn)");
            check(villager.method_31481() == OFF, OFF ? "the villager was discarded (npcs are off)" : "the villager lives (npcs on)");

            level.method_8424(true, true);
            check(friendly(level), "Level.setSpawnSettings(true, true): the friendly flag is on again");
            level.method_8424(true, false);
            check(!friendly(level), "Level.setSpawnSettings(true, false): off");

            // profiler, executors, thread names
            class_3695 profiler = server.method_16044();
            check(profiler != null, "MinecraftServer.getProfiler() answers a profiler");
            profiler.method_15396("cbs7rest");
            profiler.method_15407();
            ExecutorService bg = class_156.method_18349();
            check(bg != null && !bg.isShutdown(), "Util.backgroundExecutor() is a running ExecutorService");
            check(class_156.method_27958() != null, "Util.ioPool()");
            check(class_156.method_55473() != null, "Util.nonCriticalIoPool()");
            Runnable r = () -> { };
            check(class_156.method_33787("x", r) == r, "Util.wrapThreadWithTaskName(runnable) outside an IDE run is the task");
            Supplier<String> s = () -> "v";
            check(class_156.method_37910("x", s).get().equals("v"), "Util.wrapThreadWithTaskName(supplier)");
            check(class_3695.method_24338(profiler, profiler) != null, "ProfilerFiller.tee(a, b)");

            // values
            check(class_3532.method_15353(1.0F, 0.5F, 0.0F) == ((255 << 16) | (127 << 8)), "Mth.color(1, .5, 0) = 0x00FF7F00");
            check(class_5253.class_5254.method_27764(1, 2, 3, 4) == 0x01020304, "FastColor.ARGB32.color(a, r, g, b)");
            check(class_5253.class_5254.method_27763(0xFFFFFFFF, 0x80402010) == 0x80402010, "FastColor.ARGB32.multiply");
            check(class_5253.class_8045.method_60675(0xFF112233) == 0xFF332211, "FastColor.ABGR32.fromArgb32");
            class_3414 eat = class_3417.field_20614;
            check(eat != null && eat.method_14833().method_12832().equals("entity.generic.eat"), "SoundEvents.GENERIC_EAT and its getLocation()");
            check(class_3417.field_15141.method_14833().method_12832().equals("entity.horse.armor"), "SoundEvents.HORSE_ARMOR");
            check(class_3417.field_39025.method_14833().method_12832().equals("item.goat_horn.play"), "SoundEvents.GOAT_HORN_PLAY");
            check(!class_155.field_29694 && !class_155.field_29720, "SharedConstants debug switches: false");
            check(class_2653.field_33324 == -1 && class_2653.field_33325 == -2, "ClientboundContainerSetSlotPacket CARRIED_ITEM / PLAYER_INVENTORY");
            check(class_2820.field_34038 == 4, "ServerboundEditBookPacket.MAX_BYTES_PER_CHAR");
            check(class_3222.field_47708 == 1.0, "ServerPlayer.INTERACTION_DISTANCE_VERIFICATION_BUFFER");
            check(class_5949.field_29552 != null, "MetricCategory.MAIL_BOXES");
        } catch (Throwable e) {
            check(false, "finish threw " + e);
            e.printStackTrace(System.out);
        }
        done();
    }

    static void done() {
        System.out.println("CB-RESTTEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " failed: " + FAILS));
    }
}
