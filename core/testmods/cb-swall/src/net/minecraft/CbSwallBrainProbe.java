package net.minecraft;

import com.mojang.serialization.Dynamic;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * cb-swall's probe of the 1.21.11 <Mob>Ai.makeBrain statics (rt/CbWallAiBrains after the climb). In package
 * net.minecraft so that 1.21.11's protected statics compile here as a same-package call would (the climb rewrites
 * the call to the layer's public static). For each of the 18 brained mobs, three brains are compared:
 * off  -- the brain 26.1 gave the freshly created mob;
 * on   -- makeBrain(the same brain): 1.21.11's route over a brain that has its activities already;
 * redo -- makeBrain(copyWithoutBehaviors(the brain)): 1.21.11's route over a brain with memories and sensors and no
 *         activities, as a 1.21.11 mod's own provider would hand it.
 * A brain is read (by reflection on 26.1's Brain, whose fields have their real names at runtime) as its behaviours
 * per priority and activity (by class), activity requirements and memories erased when stopped, core, default and
 * active activities, memory types with whether each holds a value, and sensor types.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class CbSwallBrainProbe {

    private CbSwallBrainProbe() {
    }

    public record Result(String mob, String off, String on, String redo) {
    }

    static String read(class_4095 brain) {
        try {
            StringBuilder b = new StringBuilder();
            Map<Integer, Map<Object, Collection<?>>> byPriority = (Map) field(brain, "availableBehaviorsByPriority");
            TreeSet<String> behaviours = new TreeSet<>();
            byPriority.forEach((p, m) -> m.forEach((a, s) -> {
                List<String> names = new ArrayList<>();
                for (Object o : s) {
                    names.add(o.getClass().getName().replaceAll("/0x[0-9a-f]+", ""));
                }
                names.sort(null);
                behaviours.add(p + ":" + a + "=" + names);
            }));
            b.append("behaviours ").append(behaviours);
            b.append(" requirements ").append(new TreeSet<>(stringKeys((Map) field(brain, "activityRequirements"), true)));
            b.append(" erase ").append(new TreeSet<>(stringKeys((Map) field(brain, "activityMemoriesToEraseWhenStopped"), true)));
            b.append(" core ").append(new TreeSet<>(strings((Collection) field(brain, "coreActivities"))));
            b.append(" default ").append(field(brain, "defaultActivity"));
            b.append(" active ").append(new TreeSet<>(strings((Collection) field(brain, "activeActivities"))));
            Map<Object, Object> memories = (Map) field(brain, "memories");
            TreeSet<String> mem = new TreeSet<>();
            memories.forEach((k, v) -> mem.add(k + "=" + (v == null ? "absent" : String.valueOf(slotHolds(v)))));
            b.append(" memories ").append(mem);
            b.append(" sensors ").append(new TreeSet<>(stringKeys((Map) field(brain, "sensors"), false)));
            return b.toString();
        } catch (ReflectiveOperationException e) {
            return "unreadable: " + e;
        }
    }

    private static Object slotHolds(Object slot) {
        for (String n : new String[] {"value", "valueOrEmpty"}) {
            try {
                java.lang.reflect.Method m = slot.getClass().getMethod(n);
                m.setAccessible(true);
                Object v = m.invoke(slot);
                return v instanceof java.util.Optional<?> o ? (o.isPresent() ? "present" : "empty") : (v == null ? "empty" : "present");
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return slot.getClass().getSimpleName();
    }

    private static List<String> strings(Collection<?> c) {
        List<String> out = new ArrayList<>();
        for (Object o : c) {
            out.add(String.valueOf(o));
        }
        return out;
    }

    private static List<String> stringKeys(Map<?, ?> m, boolean withValues) {
        List<String> out = new ArrayList<>();
        m.forEach((k, v) -> out.add(withValues ? k + "=" + new TreeSet<>(strings(v instanceof Collection<?> c ? c : List.of(v))) : String.valueOf(k)));
        return out;
    }

    private static Object field(Object o, String name) throws ReflectiveOperationException {
        for (Class<?> c = o.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(o);
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static <E extends class_1309> Result probe(String name, class_1937 level, class_1299<E> type, BiFunction<E, class_4095, class_4095> makeBrain) {
        E mob = type.method_5883(level, class_3730.field_16462);
        class_4095 brain = mob.method_18868();
        String off = read(brain);
        String on = read(makeBrain.apply(mob, brain));
        String redo = read(makeBrain.apply(mob, brain.method_18911()));
        return new Result(name, off, on, redo);
    }

    private static <E extends class_1309> Result probe(String name, class_1937 level, class_1299<E> type, Function<class_4095, class_4095> makeBrain) {
        return probe(name, level, type, (BiFunction<E, class_4095, class_4095>) (m, b) -> makeBrain.apply(b));
    }

    public static List<Result> all(class_1937 level) {
        List<Result> out = new ArrayList<>();
        out.add(probe("frog", level, class_1299.field_37419, b -> class_7107.method_41368(b)));
        out.add(probe("tadpole", level, class_1299.field_37420, b -> class_7111.method_41400(b)));
        out.add(probe("goat", level, class_1299.field_30052, b -> class_6054.method_35183(b)));
        out.add(probe("axolotl", level, class_1299.field_28315, b -> class_5768.method_33243(b)));
        out.add(probe("allay", level, class_1299.field_38384, b -> class_7299.method_42660(b)));
        out.add(probe("camel", level, class_1299.field_40116, b -> class_7691.method_45365(b)));
        out.add(probe("sniffer", level, class_1299.field_42622, b -> class_8155.method_49152(b)));
        out.add(probe("armadillo", level, class_1299.field_47754, b -> class_9071.method_55731(b)));
        out.add(probe("hoglin", level, class_1299.field_21973, b -> class_4835.method_24668(b)));
        out.add(probe("piglin", level, class_1299.field_22281, (class_4836 m, class_4095 b) -> class_4838.method_24732(m, b)));
        out.add(probe("piglin_brute", level, class_1299.field_25751, (class_5419 m, class_4095 b) -> class_5420.method_30252(m, b)));
        out.add(probe("breeze", level, class_1299.field_47244, (class_8949 m, class_4095 b) -> class_8950.method_55016(m, b)));
        out.add(probe("creaking", level, class_1299.field_54560, (class_10275 m, class_4095 b) -> class_10280.method_64639(m, b)));
        out.add(probe("happy_ghast", level, class_1299.field_59668, b -> class_11192.method_70707(b)));
        out.add(probe("copper_golem", level, class_1299.field_61221, b -> class_11574.method_72481(b)));
        out.add(probe("nautilus", level, class_1299.field_63289, b -> class_12118.method_75172(b)));
        out.add(probe("zombie_nautilus", level, class_1299.field_63290, b -> class_12120.method_75188(b)));
        // WardenAi.makeBrain(warden, dynamic) builds the brain itself: off is the warden's own, on and redo the one it
        // builds from an empty Dynamic (a fresh warden's brain has no memories either)
        class_7260 warden = class_1299.field_38095.method_5883(level, class_3730.field_16462);
        String off = read(warden.method_18868());
        String built = read(class_7261.method_42230(warden, new Dynamic<>(class_2509.field_11560, new class_2487())));
        out.add(new Result("warden", off, built, built));
        return out;
    }
}
