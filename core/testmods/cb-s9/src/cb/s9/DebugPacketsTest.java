package cb.s9;

import cb.s9.mixin.DebugPacketsInvoker;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import net.minecraft.class_3730;
import net.minecraft.class_4140;
import net.minecraft.class_4209;
import net.minecraft.class_7923;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.8 DebugPackets: the send statics a mod calls (empty in a released 1.21.8) are there and reach the mod's own
 * mixin on them; the private getMemoryDescriptions(entity, gameTime) answers 1.21.8's "memory: value" lines, sorted,
 * through an @Invoker, and a RETURN mixin on it sees the same list.
 */
public final class DebugPacketsTest {
    public static final AtomicInteger BRAIN_CALLS = new AtomicInteger();
    public static volatile List<String> MEMORIES_SEEN;

    private DebugPacketsTest() {
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();
        S9TestMod.run("debug packets: 1.21.8 statics", () -> {
            class_1309 villager = (class_1309) class_1299.field_6077.method_5883(level, class_3730.values()[0]);
            class_4209.method_19774(villager);
            class_4209.method_19776(level, class_2338.field_10980);
            class_4209.method_19775(level, new net.minecraft.class_1923(0, 0));
            S9TestMod.check(BRAIN_CALLS.get() == 1,
                "DebugPackets.sendEntityBrain(entity) is there and reaches the 1.21.8 mod's HEAD mixin on it, got " + BRAIN_CALLS.get() + " calls");
            long now = level.method_8510();
            String key = class_7923.field_41129.method_10221(class_4140.field_19009).method_12832();
            boolean has = villager.method_18868().method_35058().containsKey(class_4140.field_19009);
            if (has) {
                villager.method_18868().method_18878(class_4140.field_19009, now - 5);
            }
            List<String> lines = DebugPacketsInvoker.cbs9$memories(villager, now);
            List<String> sorted = new ArrayList<>(lines);
            sorted.sort(String::compareTo);
            boolean shape = !lines.isEmpty() && lines.equals(sorted) && lines.stream().allMatch(l -> l.contains(": "));
            S9TestMod.check(has && shape && lines.contains(key + ": 5 ticks ago") && lines.stream().anyMatch(l -> l.endsWith(": -")),
                "DebugPackets.getMemoryDescriptions(villager, gameTime): sorted \"memory: value\" lines with " + key + ": 5 ticks ago, got "
                    + lines.size() + " lines " + lines.stream().filter(l -> l.startsWith(key)).toList());
            S9TestMod.check(lines == MEMORIES_SEEN, "a 1.21.8 RETURN mixin on getMemoryDescriptions sees the list it answers");
        });
    }
}
