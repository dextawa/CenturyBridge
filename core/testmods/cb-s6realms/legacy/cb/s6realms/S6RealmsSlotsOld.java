package cb.s6realms;

import java.util.Map;
import net.minecraft.class_4877;
import net.minecraft.class_4883;

/** 1.21.5-side fixture: converted before the native harness runs. */
public class S6RealmsSlotsOld extends class_4877 {
    public static class_4883 seen;
    public static Map<Integer, class_4883> seenMap;

    @Override
    public Map<Integer, class_4883> method_25061(Map<Integer, class_4883> slots) {
        seenMap = slots;
        seen = slots.get(1);

        class_4883 replacement = seen.method_25083();
        replacement.field_22642 = 4242L;
        slots.put(1, replacement);

        class_4883 added = seen.method_25083();
        added.field_22642 = 5252L;
        slots.put(2, added);

        // Exercises the call reshape as well as this override's convertHook.
        return super.method_25061(slots);
    }

    public static Map<Integer, class_4883> readSlots(class_4877 server) {
        return server.field_22607;
    }

    public static void writeSlots(class_4877 server, Map<Integer, class_4883> slots) {
        server.field_22607 = slots;
    }

    public static Map<Integer, class_4883> copySlots(class_4877 server) {
        return server.method_25061(server.field_22607);
    }
}
