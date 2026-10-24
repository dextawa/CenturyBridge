package cb.swall;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1928;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_4095;
import net.minecraft.class_4140;
import net.minecraft.class_4831;
import net.minecraft.server.MinecraftServer;

/**
 * Real-server check of swall's 1.21.11 -> 26.1 server-side bridges (written against 1.21.11, converted one
 * step): ExpirableValue (own values, a subclass's inherited calls, the codec), Brain (getMemories as a live
 * view of the slots, serializeStart / Brain.codec round trip, copyWithoutBehaviors sharing the memories) and
 * WorldData.getGameRules. Prints "CBTEST PASS|FAIL name" lines and a summary.
 */
public class SwallTestMod implements ModInitializer {

    private static final List<String> FAILS = new ArrayList<>();
    private static int passed;

    private static void check(String name, boolean ok, Object detail) {
        if (ok) {
            passed++;
            System.out.println("CBTEST PASS " + name);
        } else {
            FAILS.add(name);
            System.out.println("CBTEST FAIL " + name + " : " + detail);
        }
    }

    /** a 1.21.11 subclass: its calls of inherited ExpirableValue members name this class */
    static class Sub extends class_4831<String> {
        Sub() {
            super("sub", 5L);
        }

        long tickTwice() {
            method_24913();
            method_24913();
            return method_35127();
        }

        @Override
        public boolean method_24634() {
            return super.method_24634() || method_35127() < 2L;
        }
    }

    @Override
    public void onInitialize() {
        // ---- ExpirableValue, no server needed
        class_4831<String> ev = class_4831.method_24636("x", 3L);
        ev.method_24913();
        check("expirable.tick", ev.method_35127() == 2L && !ev.method_24634() && ev.method_24914(), ev);
        class_4831<String> forever = class_4831.method_28355("y");
        forever.method_24913();
        check("expirable.never", !forever.method_24914() && "y".equals(forever.method_24637()), forever);
        Sub sub = new Sub();
        check("expirable.subclass", sub.tickTwice() == 3L && !sub.method_24634(), sub);
        Codec<class_4831<String>> codec = class_4831.method_28353(Codec.STRING);
        com.google.gson.JsonElement json = codec.encodeStart(JsonOps.INSTANCE, class_4831.method_24636("z", 7L)).getOrThrow();
        class_4831<String> back = codec.parse(JsonOps.INSTANCE, json).getOrThrow();
        check("expirable.codec", "z".equals(back.method_24637()) && back.method_35127() == 7L
                && json.toString().contains("\"ttl\":7"), json);

        ServerLifecycleEvents.SERVER_STARTED.register(SwallTestMod::onStarted);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void onStarted(MinecraftServer server) {
        try {
            List<class_4140<?>> memories = List.of(class_4140.field_22334);
            // 1.21.11's saved brain: {"memories": {...}} (its codec requires the key, as 26.1's does)
            net.minecraft.class_2487 empty = new net.minecraft.class_2487();
            empty.method_10566("memories", new net.minecraft.class_2487());
            class_4095 brain = class_4095.method_28313(memories, List.of()).parse(class_2509.field_11560, empty).getOrThrow();
            brain.method_24525(class_4140.field_22334, Boolean.TRUE, 10L);
            Map<class_4140<?>, Optional<? extends class_4831<?>>> map = brain.method_35058();
            class_4831<?> mem = map.get(class_4140.field_22334).orElseThrow();
            check("brain.getMemories", mem.method_35127() == 10L && Boolean.TRUE.equals(mem.method_24637())
                    && map.containsKey(class_4140.field_22334), map);
            mem.method_24913();
            check("brain.memoryTicksSlot", brain.method_36978(class_4140.field_22334) == 9L, brain.method_36978(class_4140.field_22334));

            DataResult<class_2520> saved = brain.method_28310(class_2509.field_11560);
            class_2520 tag = saved.getOrThrow();
            class_4095 loaded = class_4095.method_28313(memories, List.of()).parse(class_2509.field_11560, tag).getOrThrow();
            check("brain.serializeRoundTrip", tag.toString().contains("memories") && loaded.method_18896(class_4140.field_22334)
                    && loaded.method_36978(class_4140.field_22334) == 9L, tag);

            class_4095 copy = brain.method_18911();
            check("brain.copyWithoutBehaviors", copy.method_18896(class_4140.field_22334)
                    && copy.method_36978(class_4140.field_22334) == 9L, copy);

            class_1928 rules = server.method_27728().method_146();
            check("worldData.getGameRules", rules == server.method_30002().method_64395(), rules);
        } catch (Throwable t) {
            t.printStackTrace();
            check("server.exception", false, t);
        }
        System.out.println("CBTEST SUMMARY " + passed + "/" + (passed + FAILS.size()) + " passed" + (FAILS.isEmpty() ? "" : " fails=" + FAILS));
    }
}
