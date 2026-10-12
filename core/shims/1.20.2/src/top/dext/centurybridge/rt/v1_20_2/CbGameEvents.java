package top.dext.centurybridge.rt.v1_20_2;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_2960;
import net.minecraft.class_5712;
import net.minecraft.class_7923;

/**
 * 1.20.1 GameEvent names on 1.20.2 (s1misc).
 *
 * 1.20.1's GameEvent(name, radius) kept the name it was given, and getName() answered it -- mods
 * registered their events under it (Registry.register(GAME_EVENT, event.getName(), event)), before
 * the event was in any registry. 1.20.2's GameEvent(radius) has no name; its id is the registry
 * key. The name a converted mod constructs an event with is kept here, per event; getName()
 * answers it, else the registry path (1.20.1's names of vanilla events were their registry paths).
 */
public final class CbGameEvents {

    private static final Map<class_5712, String> NAMES = Collections.synchronizedMap(new WeakHashMap<>());

    /** a converted subclass's super(name, radius), stashed until GameEvent(radius) returns (CbGameEventName) */
    private static final ThreadLocal<String> PENDING = new ThreadLocal<>();

    /** new GameEvent(name, radius) */
    public static class_5712 create(String name, int radius) {
        class_5712 event = new class_5712(radius);
        NAMES.put(event, name);
        return event;
    }

    /** the name argument a converted subclass passes to the 1.20.1 constructor (superCtorReshapes drop sink) */
    public static void stash(String name) {
        PENDING.set(name);
    }

    /** GameEvent(radius) returned: the event a stashed name belongs to */
    public static void claim(class_5712 event) {
        String name = PENDING.get();
        if (name != null) {
            PENDING.remove();
            NAMES.put(event, name);
        }
    }

    /** getName() on any event: a converted class's own override, else the 1.20.1 name */
    public static String method_32938(class_5712 event) {
        if (event instanceof CbLegacyGameEventName legacy) {
            return legacy.method_32938();
        }
        return super_method_32938(event);
    }

    /** GameEvent's own getName(): the name it was constructed with, else its registry path */
    public static String super_method_32938(class_5712 event) {
        String name = NAMES.get(event);
        if (name != null) {
            return name;
        }
        class_2960 id = class_7923.field_41171.method_10221(event);
        return id == null ? "unknown" : id.method_12832();
    }

    /** GameEvent.ENTITY_ROAR: 1.20.2 reports a roar as ENTITY_ACTION */
    public static class_5712 field_28728() {
        return class_5712.field_45148;
    }

    /** GameEvent.ENTITY_SHAKE: 1.20.2 reports a shake as ENTITY_ACTION */
    public static class_5712 field_28180() {
        return class_5712.field_45148;
    }

    private CbGameEvents() {
    }
}
