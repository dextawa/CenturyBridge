package cb.s205r;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1263;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1865;
import net.minecraft.class_1937;
import net.minecraft.class_2378;
import net.minecraft.class_2540;
import net.minecraft.class_2788;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_3956;
import net.minecraft.class_5455;
import net.minecraft.class_7923;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.1-era mod (intermediary names, Fabric Loader API only) with its own recipe type and its own legacy recipe
 * serializer: read(id, json), read(id, buf), write(buf, recipe), each recipe keeping the id it was read with (getId).
 * Datapack recipe cb_s205r:legacy_a is {"type": "cb_s205r:legacy", "item": "minecraft:diamond", "count": 3}.
 * Once the server ticks: RecipeManager.byKey(cb_s205r:legacy_a) is this mod's recipe, read through its read(id, json)
 * with that id, its result 3 diamonds ("CB-S205R ..."). The client (S205RClient) checks the synced copy, which the
 * recipe-sync packet decoded through read(id, buf). Ends "CB-S205R summary ok=N fail=M".
 */
public class S205RTestMod implements ModInitializer {
    public static final class_2960 ID = new class_2960("cb_s205r", "legacy_a");
    public static final class_3956<LegacyRecipe> TYPE = new class_3956<>() {
        @Override
        public String toString() {
            return "cb_s205r:legacy";
        }
    };
    public static final Serializer SERIALIZER = new Serializer();
    /** what the serializer saw: "json <id>" / "buf <id>" per call */
    public static final java.util.List<String> READS = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

    static int ok;
    static int fail;

    @Override
    public void onInitialize() {
        class_2378.method_10230(class_7923.field_41188, new class_2960("cb_s205r", "legacy"), TYPE);
        class_2378.method_10230(class_7923.field_41189, new class_2960("cb_s205r", "legacy"), SERIALIZER);
        if (FabricLoader.getInstance().getEnvironmentType() != net.fabricmc.api.EnvType.SERVER) {
            return;
        }
        Thread t = new Thread(() -> {
            for (int i = 0; i < 900; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3780() > 40) {
                    server.execute(() -> run(server.method_3772(), "server"));
                    return;
                }
            }
            System.out.println("CB-S205R summary no server ticked");
        }, "cb-s205r");
        t.setDaemon(true);
        t.start();
    }

    /** the recipe manager's copy of cb_s205r:legacy_a */
    public static void run(class_1863 recipes, String side) {
        try {
            java.util.Optional<? extends class_1860<?>> r = recipes.method_8130(ID);
            tally(r.isPresent(), side + " byKey present=" + r.isPresent() + " reads=" + READS);
            if (r.isPresent()) {
                class_1860<?> recipe = r.get();
                tally(recipe instanceof LegacyRecipe, side + " recipe class " + recipe.getClass().getName());
                if (recipe instanceof LegacyRecipe l) {
                    tally(ID.equals(l.method_8114()), side + " recipe id " + l.method_8114());
                    tally(l.result.method_7947() == 3 && "minecraft:diamond".equals(class_7923.field_41178.method_10221(l.result.method_7909()).toString()),
                        side + " result " + l.result.method_7947() + " " + class_7923.field_41178.method_10221(l.result.method_7909()));
                    if (side.equals("server")) {
                        packet(l);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            tally(false, side + " threw " + e);
        }
        System.out.println("CB-S205R summary " + side + " ok=" + ok + " fail=" + fail);
    }

    /**
     * the recipe-sync packet with this recipe written and read back (1.20.1: write puts serializer id, recipe id, then
     * write(buf, recipe); the reading constructor reads them and calls read(recipeId, buf) with the id it read)
     */
    static void packet(LegacyRecipe recipe) {
        int before = READS.size();
        class_2540 buf = new class_2540(io.netty.buffer.Unpooled.buffer());
        new class_2788(java.util.List.of(recipe)).method_11052(buf);
        java.util.List<class_1860<?>> back = new class_2788(buf).method_11998();
        class_1860<?> r = back.size() == 1 ? back.get(0) : null;
        String read = READS.size() > before ? READS.get(READS.size() - 1) : "none";
        tally(r instanceof LegacyRecipe l && ID.equals(l.method_8114()) && l.result.method_7947() == 3 && read.equals("buf " + ID),
            "packet round trip " + back.size() + " recipe(s) " + (r == null ? "-" : r.getClass().getName() + " " + r.method_8114()) + " read=" + read);
    }

    static void tally(boolean pass, String what) {
        if (pass) ok++; else fail++;
        System.out.println("CB-S205R " + (pass ? "ok" : "FAIL") + " " + what);
    }

    public static final class LegacyRecipe implements class_1860<class_1263> {
        final class_2960 id;
        final class_1799 result;

        LegacyRecipe(class_2960 id, class_1799 result) {
            this.id = id;
            this.result = result;
        }

        @Override
        public boolean method_8115(class_1263 inventory, class_1937 world) {
            return false;
        }

        @Override
        public class_1799 method_8116(class_1263 inventory, class_5455 registries) {
            return this.result.method_7972();
        }

        @Override
        public boolean method_8113(int width, int height) {
            return true;
        }

        @Override
        public class_1799 method_8110(class_5455 registries) {
            return this.result;
        }

        @Override
        public class_2960 method_8114() {
            return this.id;
        }

        @Override
        public class_1865<?> method_8119() {
            return SERIALIZER;
        }

        @Override
        public class_3956<?> method_17716() {
            return TYPE;
        }

        @Override
        public boolean method_8118() {
            return true;
        }
    }

    public static final class Serializer implements class_1865<LegacyRecipe> {
        @Override
        public LegacyRecipe method_8121(class_2960 id, JsonObject json) {
            READS.add("json " + id);
            class_2960 item = new class_2960(class_3518.method_15265(json, "item"));
            return new LegacyRecipe(id, new class_1799(class_7923.field_41178.method_10223(item), class_3518.method_15282(json, "count", 1)));
        }

        @Override
        public LegacyRecipe method_8122(class_2960 id, class_2540 buf) {
            READS.add("buf " + id);
            class_2960 item = new class_2960(buf.method_19772());
            return new LegacyRecipe(id, new class_1799(class_7923.field_41178.method_10223(item), buf.method_10816()));
        }

        @Override
        public void method_8124(class_2540 buf, LegacyRecipe recipe) {
            buf.method_10814(class_7923.field_41178.method_10221(recipe.result.method_7909()).toString());
            buf.method_10804(recipe.result.method_7947());
        }
    }
}
