package cb.s6realms;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_4877;
import net.minecraft.class_4883;

/** Unconverted 1.21.5 control for the fixture's old-map and ping-copy assumptions. */
public final class S6RealmsSlotsVControl {
    private static int checks;

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }

    private static class_4883 ping() {
        return class_4883.method_25076();
    }

    public static void main(String[] args) {
        class_4877 server = new class_4877();
        Map<Integer, class_4883> input = new HashMap<>();
        class_4883 source = ping();
        input.put(1, source);
        Map<Integer, class_4883> copy = server.method_25061(input);
        check(copy != input && copy.get(1) != source, "V control: helper copies the map and ping");
        check(copy.get(1).field_22636 == source.field_22636, "V control: cloned ping keeps its data");

        S6RealmsSlotsOld.writeSlots(server, input);
        check(S6RealmsSlotsOld.readSlots(server) == input, "V control: field PUT/GET is direct");
        Map<Integer, class_4883> fieldCopy = S6RealmsSlotsOld.copySlots(server);
        check(fieldCopy != input && fieldCopy.get(1) != source, "V control: GET/call copies the ping");

        S6RealmsSlotsOld oldServer = new S6RealmsSlotsOld();
        Map<Integer, class_4883> overridden = oldServer.method_25061(input);
        check(S6RealmsSlotsOld.seen == source, "V control: override sees the original ping");
        check(input.get(1).field_22642 == 4242L && input.get(2).field_22642 == 5252L,
            "V control: override puts old pings directly");
        check(overridden.get(1) != input.get(1) && overridden.get(2) != input.get(2),
            "V control: native super copy clones both override-supplied pings");
        System.out.println("S6REALMS_V_CONTROL_OK checks=" + checks);
    }
}
