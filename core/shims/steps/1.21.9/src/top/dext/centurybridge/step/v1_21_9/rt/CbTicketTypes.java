package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_3230;
import top.dext.centurybridge.step.v1_21_9.facade.class_3230$class_10558;

/**
 * 1.21.8's TicketType(long timeout, boolean persist, TicketUse use) on 1.21.9's TicketType(long timeout, int flags)
 * (javap -c of both class_3230 and of TicketStorage class_10592 / ServerChunkCache class_3215 / ServerLevel):
 * <ul>
 * <li>1 = persist: 1.21.9's save filter tests method_72273 (&amp;1) where 1.21.8's tested comp_3475.</li>
 * <li>2 = loading, 4 = simulation: 1.21.9's method_66025 / method_66027 test the bits where 1.21.8's tested the use
 * (LOADING or LOADING_AND_SIMULATION / SIMULATION or LOADING_AND_SIMULATION); same callers, same level choice.</li>
 * <li>8 = keeps the dimension ticking (TicketStorage method_72544 -> ServerLevel's empty-time reset). 1.21.8 kept a
 * level awake by its forced-chunk set, filled by tickets whose type equals FORCED (record equality, method_66380). A
 * 1.21.8 type equal to FORCED, (0, true, LOADING_AND_SIMULATION), therefore gets 8; it then equals 1.21.9's FORCED
 * (0, 15), which 1.21.9's forced-chunk paths test the same way. Every other 1.21.8 type never kept a level awake.</li>
 * <li>16 = a timed ticket counts down while its chunk is loaded (method_72543). 1.21.8's method_66371 froze only
 * simulation tickets there; so a timed LOADING ticket gets 16. With timeout 0 it changes nothing 1.21.8 reached.</li>
 * </ul>
 * Checked against the vanilla types both versions declare: unknown (1, false, LOADING) -> 18, 1.21.9's own; forced
 * -> 15, 1.21.9's own; dragon / player_loading / player_simulation -> 6 / 2 / 4 (1.21.9 adds 8 to player_simulation,
 * its own new rule). 1.21.8's START (0, false, LOADING_AND_SIMULATION; the spawn-chunk ticket 1.21.9 dropped with spawn
 * chunks, not 1.21.9's timed player_spawn / spawn_search) comes back as its own type (0, 6).
 */
public final class CbTicketTypes {
    private CbTicketTypes() {
    }

    private static final class_3230 START = ticketType(0L, false, class_3230$class_10558.LOADING_AND_SIMULATION);

    /** 1.21.8's flags, as 1.21.9 reads them */
    public static int flags(long timeout, boolean persist, class_3230$class_10558 use) {
        boolean loading = use != class_3230$class_10558.SIMULATION;
        boolean simulation = use != class_3230$class_10558.LOADING;
        int flags = (persist ? 1 : 0) | (loading ? 2 : 0) | (simulation ? 4 : 0);
        if (timeout == 0L && persist && use == class_3230$class_10558.LOADING_AND_SIMULATION) {
            flags |= 8; // equal to 1.21.8's FORCED, and so treated as FORCED
        }
        if (timeout != 0L && use == class_3230$class_10558.LOADING) {
            flags |= 16;
        }
        return flags;
    }

    /** new TicketType(timeout, persist, use) */
    public static class_3230 ticketType(long timeout, boolean persist, class_3230$class_10558 use) {
        return new class_3230(timeout, flags(timeout, persist, use));
    }

    /** TicketType.persist() */
    public static boolean comp_3475(class_3230 type) {
        return type.method_72273();
    }

    /** TicketType.use(): the 1.21.8 constant for the loading / simulation bits */
    public static class_3230$class_10558 comp_3476(class_3230 type) {
        boolean loading = type.method_66025();
        boolean simulation = type.method_66027();
        if (loading && simulation) {
            return class_3230$class_10558.LOADING_AND_SIMULATION;
        }
        if (loading) {
            return class_3230$class_10558.LOADING;
        }
        if (simulation) {
            return class_3230$class_10558.SIMULATION;
        }
        throw new IllegalStateException("ticket type " + type + " neither loads nor simulates; 1.21.8 had no use for it");
    }

    /** TicketType.START */
    public static class_3230 field_14030() {
        return START;
    }
}
