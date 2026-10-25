package cb.s6b;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1642;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2595;
import net.minecraft.class_2945;
import net.minecraft.class_3218;
import net.minecraft.class_5455;
import net.minecraft.class_8942;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.5-era server mod (intermediary names, Fabric Loader API only) for s6b's bridges of the 1.21.5 -> 1.21.6
 * step: the CompoundTag save data on 1.21.6's ValueInput / ValueOutput (a Zombie subclass and a plain Entity
 * subclass with 1.21.5 read/addAdditionalSaveData overrides, saved and loaded through 1.21.5 calls that reach
 * vanilla's 1.21.6 path; a chest's saveWithId / loadWithComponents), Component.Serializer, ProblemReporter strings.
 * Every check prints "CB-S6B ok|FAIL ..."; the last line is "CB-S6B DONE <passed>/<checks>".
 */
public class S6bTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;
    static int saves;
    static int reads;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-S6B " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    /** a 1.21.5 Zombie subclass with its own save data, calling super */
    static class CbZombie extends class_1642 {
        int marker = -1;

        CbZombie(class_1299<? extends class_1642> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public void method_5652(class_2487 tag) {
            super.method_5652(tag);
            tag.method_10569("cb_s6b", marker);
            // what vanilla saved before is in the same tag
            if (tag.method_10545("Health")) {
                saves++;
            }
        }

        @Override
        public void method_5749(class_2487 tag) {
            super.method_5749(tag);
            marker = tag.method_68083("cb_s6b", -2);
            reads++;
        }
    }

    /** a plain 1.21.5 Entity implementing the abstract save methods itself */
    static class PlainEntity extends class_1297 {
        String note = "";

        PlainEntity(class_1299<?> type, class_1937 level) {
            super(type, level);
        }

        @Override
        protected void method_5693(class_2945.class_9222 builder) {
        }

        @Override
        public boolean method_64397(class_3218 level, class_1282 source, float amount) {
            return false;
        }

        @Override
        protected void method_5749(class_2487 tag) {
            note = tag.method_68564("cb_note", "missing");
        }

        @Override
        protected void method_5652(class_2487 tag) {
            tag.method_10582("cb_note", note);
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
        }, "cb-s6b");
        t.setDaemon(true);
        t.start();
    }

    static void start(MinecraftServer server) {
        class_3218 level = server.method_30002();
        try {
            entities(level);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "entities threw " + e);
        }
        try {
            chest(level);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "chest threw " + e);
        }
        try {
            text(level.method_30349());
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "text threw " + e);
        }
        try {
            problems();
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "problems threw " + e);
        }
        System.out.println("CB-S6B DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS " + FAILS));
    }

    static void entities(class_3218 level) {
        CbZombie z = new CbZombie(class_1299.field_6051, level);
        z.marker = 42;
        z.method_6033(7.0F);
        class_2487 tag = z.method_5647(new class_2487());
        check(tag.method_68083("cb_s6b", -1) == 42, "Zombie subclass: saveWithoutId carries the mod's addAdditionalSaveData");
        check(tag.method_10545("Health"), "Zombie subclass: vanilla's save data in the same tag");
        check(saves == 1, "Zombie subclass: the mod's override saw vanilla's data after its super call (" + saves + ")");
        CbZombie z2 = new CbZombie(class_1299.field_6051, level);
        z2.method_5651(tag);
        check(z2.marker == 42, "Zombie subclass: load gives the mod's readAdditionalSaveData its value (" + z2.marker + ")");
        check(Math.abs(z2.method_6032() - 7.0F) < 1e-4, "Zombie subclass: vanilla's read through the mod's super call (health " + z2.method_6032() + ")");
        check(reads == 1, "Zombie subclass: read once (" + reads + ")");

        class_2487 direct = new class_2487();
        z.method_5652(direct);
        check(direct.method_68083("cb_s6b", -1) == 42 && direct.method_10545("Health"),
            "Zombie subclass: a direct 1.21.5 addAdditionalSaveData call reaches the mod's override and vanilla's");

        PlainEntity p = new PlainEntity(class_1299.field_6051, level);
        p.note = "hello";
        class_2487 pt = p.method_5647(new class_2487());
        check("hello".equals(pt.method_68564("cb_note", "")), "plain Entity: saved through vanilla's saveWithoutId");
        check(pt.method_10545("Pos"), "plain Entity: vanilla's Entity data in the same tag");
        PlainEntity p2 = new PlainEntity(class_1299.field_6051, level);
        p2.method_5651(pt);
        check("hello".equals(p2.note), "plain Entity: loaded through vanilla's load (" + p2.note + ")");
    }

    static void chest(class_3218 level) {
        class_2595 chest = new class_2595(class_2338.field_10980, class_2246.field_10034.method_9564());
        class_2487 tag = new class_2487();
        tag.method_10582("CustomName", "Box");
        chest.method_58690(tag, level.method_30349());
        class_2561 name = chest.method_5797();
        check(name != null && "Box".equals(name.getString()), "chest: loadWithComponents reads the custom name (" + name + ")");
        class_2487 saved = chest.method_38243(level.method_30349());
        check("minecraft:chest".equals(saved.method_68564("id", "")), "chest: saveWithId writes the id");
        check(saved.method_10545("CustomName"), "chest: saveWithId writes the custom name");
    }

    static void text(class_5455 registries) {
        class_2561 hi = class_2561.method_43470("hi");
        String json = class_2561.class_2562.method_10867(hi, registries);
        check("\"hi\"".equals(json), "Component.Serializer.toJson (" + json + ")");
        class_2561 back = class_2561.class_2562.method_10877("{\"text\":\"yo\"}", registries);
        check(back != null && "yo".equals(back.getString()), "Component.Serializer.fromJson");
        // 1.21.5: JsonParser gives a JsonNull (not null) for "null", which the codec refuses with a JsonParseException
        boolean threw = false;
        try {
            class_2561.class_2562.method_10877("null", registries);
        } catch (com.google.gson.JsonParseException e) {
            threw = true;
        }
        check(threw, "Component.Serializer.fromJson(\"null\") throws JsonParseException as 1.21.5 did");
    }

    static void problems() {
        class_8942.class_8943 collector = new class_8942.class_8943();
        collector.method_54946(".pools").method_54946("[0]").method_54947("bad entry");
        String report = collector.method_59906().orElse("");
        check(report.contains("bad entry") && report.contains(".pools[0]"), "ProblemReporter: 1.21.5 strings path and report (" + report + ")");
        check(collector.method_54948().containsValue("bad entry"), "ProblemReporter.Collector.get()");
        check(new class_8942.class_8943().method_59906().isEmpty(), "ProblemReporter: empty report is empty");
    }
}
