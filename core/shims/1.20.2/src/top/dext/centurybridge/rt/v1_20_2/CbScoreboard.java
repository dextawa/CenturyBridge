package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_124;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_8646;

/**
 * The 1.20.1 Scoreboard API on 1.20.2's. 1.20.2 turned the int display slots (0 list, 1 sidebar,
 * 2 below name, 3..18 team sidebars by team colour) into the DisplaySlot enum (class_8646, same
 * ids) and dropped getObjective(name) / containsObjective(name), which 1.20.1 answered from the
 * objective map that getNullableObjective(name) still reads.
 */
public final class CbScoreboard {

    // ---- the slot constants (1.20.1 ConstantValue attributes)

    /** LIST_DISPLAY_SLOT_ID */
    public static int field_31886() {
        return 0;
    }

    /** SIDEBAR_DISPLAY_SLOT_ID */
    public static int field_31887() {
        return 1;
    }

    /** BELOW_NAME_DISPLAY_SLOT_ID */
    public static int field_31888() {
        return 2;
    }

    /** MIN_SIDEBAR_TEAM_DISPLAY_SLOT_ID */
    public static int field_31889() {
        return 3;
    }

    /** MAX_SIDEBAR_TEAM_DISPLAY_SLOT_ID */
    public static int field_31890() {
        return 18;
    }

    /** DISPLAY_SLOT_COUNT */
    public static int field_31891() {
        return 19;
    }

    private static String[] slotNames;

    /** getDisplaySlotName(slot), 1.20.1 body */
    public static String method_1167(int slot) {
        switch (slot) {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                if (slot >= 3 && slot <= 18) {
                    class_124 colour = class_124.method_534(slot - 3);
                    if (colour != null && colour != class_124.field_1070) {
                        return "sidebar.team." + colour.method_537();
                    }
                }
                return null;
        }
    }

    /** getDisplaySlotId(name), 1.20.1 body; -1 for an unknown name */
    public static int method_1192(String name) {
        if ("list".equalsIgnoreCase(name)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase(name)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase(name)) {
            return 2;
        }
        if (name.startsWith("sidebar.team.")) {
            class_124 colour = class_124.method_533(name.substring("sidebar.team.".length()));
            if (colour != null && colour.method_536() >= 0) {
                return colour.method_536() + 3;
            }
        }
        return -1;
    }

    /** getDisplaySlotNames(), 1.20.1 body */
    public static String[] method_1186() {
        if (slotNames == null) {
            String[] names = new String[19];
            for (int i = 0; i < 19; i++) {
                names[i] = method_1167(i);
            }
            slotNames = names;
        }
        return slotNames;
    }

    /**
     * the DisplaySlot of a 1.20.1 slot id (ordinal = id, javap of DisplaySlot.<clinit>); an id outside 0..18 throws
     * ArrayIndexOutOfBoundsException, as 1.20.1's displayObjectives[slot] did
     */
    public static class_8646 slotOf(int slot) {
        return class_8646.values()[slot];
    }

    /** a DisplaySlot where 1.20.1 code holds the slot id */
    public static int slotId(class_8646 slot) {
        return slot.method_52621();
    }

    /** a DisplaySlot where 1.20.1 code holds the boxed slot id (ScoreboardSlotArgument.parse) */
    public static Integer boxedSlotId(class_8646 slot) {
        return slot == null ? null : slot.method_52621();
    }

    // ---- getObjective(name) / containsObjective(name)

    /** scoreboard.getObjective(name): the scoreboard's own override when it declares one */
    public static class_266 method_1165(class_269 self, String name) {
        if (self instanceof CbLegacyScoreboardObjective own) {
            return own.method_1165(name);
        }
        return self.method_1170(name);
    }

    /** scoreboard.containsObjective(name): the scoreboard's own override when it declares one */
    public static boolean method_1181(class_269 self, String name) {
        if (self instanceof CbLegacyScoreboardHasObjective own) {
            return own.method_1181(name);
        }
        return self.method_1170(name) != null;
    }

    /** super.getObjective(name): 1.20.1 read the objective map, which getNullableObjective reads */
    public static class_266 super_method_1165(class_269 self, String name) {
        return self.method_1170(name);
    }

    /** super.containsObjective(name) */
    public static boolean super_method_1181(class_269 self, String name) {
        return self.method_1170(name) != null;
    }

    /**
     * The objective lookup at a place 1.20.1 called getObjective(name) and 1.20.2 calls
     * getNullableObjective(name): a scoreboard overriding getObjective is consulted again there.
     */
    public static class_266 objectiveAt(class_269 board, String name) {
        if (board instanceof CbLegacyScoreboardObjective own) {
            return own.method_1165(name);
        }
        return board.method_1170(name);
    }

    /**
     * The objective lookup where 1.20.1 asked containsObjective(name) first and fetched the
     * objective only when it was there (1.20.2 fetches and null-checks).
     */
    public static class_266 objectiveIfContained(class_269 board, String name) {
        if (board instanceof CbLegacyScoreboardHasObjective own && !own.method_1181(name)) {
            return null;
        }
        return board.method_1170(name);
    }

    private CbScoreboard() {
    }
}
