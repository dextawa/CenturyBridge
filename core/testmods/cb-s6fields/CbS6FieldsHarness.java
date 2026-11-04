package cb.s6fields;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_11515;
import net.minecraft.class_1296;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_4140;
import top.dext.centurybridge.step.v1_21_6.rt.CbS6MemoryFields;
import top.dext.centurybridge.step.v1_21_6.rt.CbS6SectionField;

/** Focused JVM behavior checks for s6fields; no game boot and no claim of in-game coverage. */
public final class CbS6FieldsHarness {
    private static int checks;

    /** AgeableMob test value. */
    private static final class TestAdult extends class_1296 {
        TestAdult() {
            super(null, null);
        }

        @Override
        public class_1296 method_5613(net.minecraft.class_3218 level, class_1296 other) {
            return null;
        }
    }

    /** LivingEntity but not AgeableMob; only an instanceof operand too. */
    private static final class TestOtherLiving extends class_1309 {
        TestOtherLiving() {
            super(null, null);
        }

        @Override
        public class_1306 method_6068() {
            return null;
        }
    }

    private static void check(boolean ok, String what) {
        checks++;
        if (!ok) {
            throw new AssertionError("cb-s6fields: " + what);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void memoryProjection() {
        Object adult = new TestAdult();
        Object otherLiving = new TestOtherLiving();
        Optional valid = Optional.of(adult);
        Optional widened = Optional.of(otherLiving);
        Optional empty = Optional.empty();

        check(CbS6MemoryFields.cbS6AdultOut(class_4140.field_25359, valid) == valid,
            "AgeableMob Optional keeps identity");
        check(CbS6MemoryFields.cbS6AdultOut(class_4140.field_25359, empty) == empty,
            "empty Optional keeps identity");
        check(CbS6MemoryFields.cbS6AdultOut(class_4140.field_25359, widened).isEmpty(),
            "non-AgeableMob LivingEntity reads as absent, not a generic CCE");
        check(CbS6MemoryFields.cbS6AdultOut(class_4140.field_18448, widened) == widened,
            "another memory key is not filtered");
        check(CbS6MemoryFields.cbS6AdultIn(class_4140.field_25359, adult) == adult,
            "V to W value keeps entity identity");
        check(CbS6MemoryFields.cbS6AdultOptionalIn(class_4140.field_25359, valid) == valid,
            "V to W Optional keeps identity");
        check(CbS6MemoryFields.cbS6AdultOptionalIn(class_4140.field_25359,
                CbS6MemoryFields.cbS6AdultOut(class_4140.field_25359, valid)) == valid,
            "representable value round-trips without a copy");
        boolean rejected = false;
        try {
            CbS6MemoryFields.cbS6AdultIn(class_4140.field_25359, otherLiving);
        } catch (ClassCastException expected) {
            rejected = true;
        }
        check(rejected, "raw invalid old-domain input is rejected explicitly");

        net.minecraft.class_4095 brain = new net.minecraft.class_4095();
        brain.answer = widened;
        check(CbS6MemoryFields.method_18904(brain, class_4140.field_25359).isEmpty(),
            "Brain getOptionalMemory redirect applies the key-sensitive projection");
        brain.answer = valid;
        check(CbS6MemoryFields.method_46873(brain, class_4140.field_25359) == valid,
            "Brain getRegisteredMemory redirect preserves a valid Optional");
        CbS6MemoryFields.method_18878(brain, class_4140.field_25359, adult);
        check(brain.answer == valid || brain.answer.get() == adult, "Brain setMemory redirect preserves the entity");
        CbS6MemoryFields.method_18879(brain, class_4140.field_25359, valid);
        check(brain.answer == valid, "Brain Optional setMemory redirect preserves the Optional");
        CbS6MemoryFields.method_24525(brain, class_4140.field_25359, adult, 37L);
        check(brain.answer.get() == adult && brain.expiry == 37L,
            "Brain expiring setMemory redirect preserves value and expiry");
        check(CbS6MemoryFields.method_29519(brain, class_4140.field_25359, adult),
            "Brain isMemoryValue redirect keeps identity comparison");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void rawMemoryMap() {
        Object adult = new TestAdult();
        Object otherLiving = new TestOtherLiving();
        net.minecraft.class_4095 brain = new net.minecraft.class_4095();
        Optional widened = Optional.of(new net.minecraft.class_4831(otherLiving, 19L));
        Optional unrelated = Optional.of(new net.minecraft.class_4831(otherLiving, 23L));
        Optional valid = Optional.of(new net.minecraft.class_4831(adult, 37L));
        brain.memories.put(class_4140.field_25359, widened);
        brain.memories.put(class_4140.field_18448, unrelated);
        Map memories = CbS6MemoryFields.method_35058(brain);

        check(((Optional) brain.memories.get(class_4140.field_25359)).isPresent(),
            "counterexample is present in the unconverted native backing map");
        check(memories == CbS6MemoryFields.method_35058(brain), "raw memory view keeps V's stable map identity");
        check(((Optional) memories.get(class_4140.field_25359)).isEmpty(),
            "raw map cannot expose a widened nearest-adult value through ExpirableValue.getValue");
        check(memories.get(class_4140.field_18448) == unrelated,
            "raw map leaves another memory key's Optional and value untouched");

        boolean entryChecked = false;
        for (Object o : memories.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if (entry.getKey() == class_4140.field_25359) {
                entryChecked = true;
                check(((Optional) entry.getValue()).isEmpty(), "raw entry view applies the keyed projection");
            }
        }
        check(entryChecked, "raw nearest-adult entry remains present as an empty V value");
        boolean valuesProjected = false;
        for (Object value : memories.values()) {
            valuesProjected |= value instanceof Optional && ((Optional) value).isEmpty();
        }
        check(valuesProjected, "raw values view cannot leak the widened Optional");
        check(!memories.containsValue(widened) && memories.containsValue(Optional.empty()),
            "containsValue judges the V view, not the hidden native Optional");

        Object old = memories.put(class_4140.field_25359, valid);
        check(((Optional) old).isEmpty() && brain.memories.get(class_4140.field_25359) == valid,
            "raw put converts its old value and writes the original valid Optional through");
        Optional held = (Optional) memories.get(class_4140.field_25359);
        check(((net.minecraft.class_4831) held.get()).method_24637() == adult,
            "ExpirableValue.getValue keeps a representable entity's identity");

        brain.memories.put(class_4140.field_25359, widened);
        check(((Optional) memories.get(class_4140.field_25359)).isEmpty(),
            "raw view follows a later native-map update");
        boolean entryWrite = false;
        for (Object o : memories.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if (entry.getKey() == class_4140.field_25359) {
                entryWrite = true;
                check(((Optional) entry.setValue(valid)).isEmpty(),
                    "entry.setValue also converts the previous raw value");
            }
        }
        check(entryWrite && brain.memories.get(class_4140.field_25359) == valid,
            "entry.setValue writes the original valid Optional through");

        boolean rejected = false;
        try {
            memories.put(class_4140.field_25359, widened);
        } catch (ClassCastException expected) {
            rejected = true;
        }
        check(rejected, "raw nearest-adult input outside V's domain is rejected explicitly");
        brain.memories.put(class_4140.field_25359, widened);
        check(((Optional) memories.remove(class_4140.field_25359)).isEmpty()
                && !brain.memories.containsKey(class_4140.field_25359),
            "raw remove projects the old value and writes through");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void sectionMap() {
        EnumMap<class_11515, Object> nativeMap = new EnumMap<>(class_11515.class);
        Object solidMesh = new Object();
        Object translucentMesh = new Object();
        Object replacement = new Object();
        nativeMap.put(class_11515.field_60923, solidMesh);
        net.minecraft.class_9810.class_9811 results = new net.minecraft.class_9810.class_9811();
        results.field_52168 = nativeMap;
        Map legacy = CbS6SectionField.field_52168(results);

        check(legacy.get(class_1921.method_23577()) == solidMesh, "solid key and MeshData identity");
        class_1921 impostor = class_1921.modCreated(class_11515.field_60923);
        check(impostor.layer == class_1921.method_23577().layer,
            "counterexample RenderType really shares the canonical solid pipeline");
        check(top.dext.centurybridge.step.v1_21_6.rt.CbSectionMeshes.layer(impostor)
                == class_11515.field_60923,
            "the old pipeline-only classifier would conflate this counterexample");
        check(CbS6SectionField.cbS6NativeLayer(impostor) == null,
            "a mod-created RenderType is not classified as a canonical layer by pipeline");
        check(legacy.get(impostor) == null && !legacy.containsKey(impostor),
            "same-pipeline mod key cannot alias the native solid entry");
        boolean impostorRejected = false;
        try {
            legacy.put(impostor, solidMesh);
        } catch (IllegalArgumentException expected) {
            impostorRejected = true;
        }
        check(impostorRejected && nativeMap.get(class_11515.field_60923) == solidMesh,
            "same-pipeline mod key cannot overwrite the native solid entry");
        check(CbS6SectionField.cbS6LegacyLayer(class_11515.field_60924) == class_1921.method_23579(),
            "cutout-mipped inverse");
        check(CbS6SectionField.cbS6LegacyLayer(class_11515.field_60925) == class_1921.method_23581(),
            "cutout inverse");
        check(CbS6SectionField.cbS6LegacyLayer(class_11515.field_60927) == class_1921.method_29997(),
            "tripwire inverse");

        Object oldTranslucent = legacy.put(CbS6SectionField.cbS6LegacyLayer(class_11515.field_60926), translucentMesh);
        check(oldTranslucent == null && nativeMap.get(class_11515.field_60926) == translucentMesh,
            "restored translucent key writes through");
        check(CbS6SectionField.cbS6NativeLayer(CbS6SectionField.cbS6LegacyLayer(class_11515.field_60926))
                == class_11515.field_60926, "translucent key round-trip");

        boolean entryChecked = false;
        for (Object o : legacy.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if (entry.getKey() == CbS6SectionField.cbS6LegacyLayer(class_11515.field_60926)) {
                entryChecked = true;
                check(entry.getValue() == translucentMesh && entry.setValue(replacement) == translucentMesh,
                    "entry value identity and setValue return");
            }
        }
        check(entryChecked && nativeMap.get(class_11515.field_60926) == replacement,
            "entry.setValue writes through");
        check(legacy.remove(class_1921.method_23577()) == solidMesh && !nativeMap.containsKey(class_11515.field_60923),
            "remove writes through and returns the old value");

        nativeMap.put(class_11515.field_60924, solidMesh);
        Iterator it = legacy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getKey() == class_1921.method_23579()) {
                it.remove();
            }
        }
        check(!nativeMap.containsKey(class_11515.field_60924), "entry iterator removal writes through");
        check(CbS6SectionField.cbS6NativeLayers(legacy) == nativeMap, "inverse conversion unwraps the original map");
        check(CbS6SectionField.cbS6LegacyLayers(legacy) == legacy, "same-direction conversion is idempotent");
        check(legacy.get(class_1921.method_23589()) == null && !legacy.containsKey(class_1921.method_23589()),
            "a non-chunk RenderType reads as absent");
        boolean rejected = false;
        try {
            legacy.put(class_1921.method_23589(), solidMesh);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "an unrepresentable key is never written under null");
        legacy.clear();
        check(nativeMap.isEmpty(), "clear writes through");
    }

    public static void main(String[] args) throws Exception {
        memoryProjection();
        rawMemoryMap();
        sectionMap();
        System.out.println("CB-S6FIELDS PASS " + checks + " focused JVM checks (not a game boot)");
    }
}
