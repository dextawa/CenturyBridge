package cb.s9fin;

import io.netty.buffer.Unpooled;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_10342;
import net.minecraft.class_10344;
import net.minecraft.class_10341;
import net.minecraft.class_10346;
import net.minecraft.class_155;
import net.minecraft.class_161;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1860;
import net.minecraft.class_2403;
import net.minecraft.class_2540;
import net.minecraft.class_2838;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5321;
import net.minecraft.class_7225;
import net.minecraft.class_8779;
import net.minecraft.class_8790;
import net.minecraft.class_9094;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.3-era server mod (intermediary names, Fabric Loader API only) for s9fin's bridges of the 1.21.3 -> 1.21.4 step, run once
 * the server is up. Each check prints "CB-S9TEST ok|FAIL ..."; the last line is "CB-S9TEST DONE passed/checks".
 */
public class S9FinTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-S9TEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
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
        }, "cb-s9fin");
        t.setDaemon(true);
        t.start();
    }

    /** a 1.21.3 mod's subclass of WinterDropRecipeProvider */
    static final class WinterRecipes extends class_10344 {
        WinterRecipes(class_7225.class_7874 registries, class_8790 out) {
            super(registries, out);
        }

        void generate() {
            this.method_10419();
        }
    }

    static void start(MinecraftServer server) {
        class_3218 level = server.method_30002();
        class_7225.class_7874 registries = server.method_30611();

        run("pick packet", () -> {
            class_2838 p = new class_2838(23);
            check(p.method_12293() == 23, "new ServerboundPickItemPacket(23).getSlot() == 23");
            check(p.method_55846() == class_9094.field_48096, "packet.type() is GamePacketTypes.SERVERBOUND_PICK_ITEM");
            check(class_9094.field_48096.comp_2231().toString().equals("minecraft:pick_item"), "SERVERBOUND_PICK_ITEM is minecraft:pick_item");
            class_2540 buf = new class_2540(Unpooled.buffer());
            class_2838.field_48204.encode(buf, p);
            class_2838 back = class_2838.field_48204.decode(buf);
            check(back.method_12293() == 23, "STREAM_CODEC round trip keeps the slot");
        });

        run("pick handler", () -> {
            class_3222 player;
            try {
                player = (class_3222) Class.forName("net.fabricmc.fabric.api.entity.FakePlayer").getMethod("get", class_3218.class).invoke(null, level);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            class_1661 inv = player.method_31548();
            for (int i = 0; i < 36; i++) {
                inv.method_5447(i, class_1799.field_8037);
            }
            inv.field_7545 = 0;
            inv.method_5447(0, new class_1799(class_1802.field_8831, 3));
            inv.method_5447(20, new class_1799(class_1802.field_20391, 5));
            // 1.21.3: pickSlot(20) takes the suitable hotbar slot (an empty one, else the selected) -> slot 1 is empty
            int expect = inv.method_7386();
            player.field_13987.method_12084(new class_2838(20));
            check(inv.field_7545 == expect, "handlePickItem selects the suitable hotbar slot (" + expect + "), got " + inv.field_7545);
            check(inv.method_5438(expect).method_7909() == class_1802.field_20391 && inv.method_5438(expect).method_7947() == 5,
                "handlePickItem swaps the picked stone into the hotbar");
            check(inv.method_5438(20).method_7960(), "slot 20 receives the hotbar slot's (empty) stack");
            check(inv.method_5438(0).method_7909() == class_1802.field_8831, "slot 0 keeps its dirt");

            inv.method_5447(21, new class_1799(class_1802.field_8477, 1));
            int expect2 = inv.method_7386();
            new class_2838(21).method_11054(player.field_13987);
            check(inv.method_5438(expect2).method_7909() == class_1802.field_8477, "packet.handle(listener) runs 1.21.3's handlePickItem");
        });

        run("winter drop block loot", () -> {
            List<String> keys = new ArrayList<>();
            new class_10342(registries).method_10399((key, table) -> keys.add(key.method_29177().toString()));
            check(keys.size() == 22, "WinterDropBlockLoot.generate emits 1.21.3's 22 tables, got " + keys.size());
            check(keys.contains("minecraft:blocks/creaking_heart") && keys.contains("minecraft:blocks/pale_oak_log"),
                "WinterDropBlockLoot has the creaking heart and pale oak log tables");
        });

        run("winter drop recipes", () -> {
            List<String> ids = new ArrayList<>();
            class_8790 out = new class_8790() {
                @Override
                public void method_53819(class_5321<class_1860<?>> key, class_1860<?> recipe, class_8779 advancement) {
                    ids.add(key.method_29177().toString());
                }

                @Override
                public class_161.class_162 method_53818() {
                    return class_161.class_162.method_51698();
                }

                @Override
                public void method_62738() {
                }
            };
            new WinterRecipes(registries, out).generate();
            boolean allPale = !ids.isEmpty() && ids.stream().allMatch(id -> id.contains("pale"));
            check(allPale, "WinterDropRecipeProvider writes only the Winter Drop's recipes (" + ids.size() + "): " + ids);
            check(ids.contains("minecraft:pale_oak_stairs") && ids.contains("minecraft:pale_oak_planks"),
                "WinterDropRecipeProvider includes the pale oak family and planks");
        });

        run("winter drop advancements", () -> {
            List<String> ids = new ArrayList<>();
            new class_10341().method_10335(registries, a -> ids.add(a.comp_1919().toString()));
            check(!ids.isEmpty() && ids.size() == ids.stream().distinct().count(), "WinterDropAdventureAdvancements generates " + ids);
        });

        run("winter drop registries", () -> {
            Object patched = class_10346.method_64951(CompletableFuture.completedFuture(net.minecraft.class_7887.method_46817())).join();
            check(patched != null, "WinterDropRegistries.createLookup(VanillaRegistries.createLookup()) completes, as 1.21.3's data generator ran it");
        });

        run("standard generator", () -> {
            try {
                Path out = Files.createTempDirectory("cb-s9fin-gen");
                class_2403 gen = net.minecraft.data.Main.method_4968(out, List.of(), false, true, false, false, false, class_155.method_16673(), true);
                check(gen != null, "createStandardGenerator(server) builds a generator");
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("CB-S9TEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED: " + FAILS));
    }
}
