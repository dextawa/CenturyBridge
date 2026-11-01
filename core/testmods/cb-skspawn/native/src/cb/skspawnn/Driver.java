package cb.skspawnn;

/**
 * Native 1.20.5 control (never converted, its own access widener opens SpawnPlacements.Data and the map).
 * <ul>
 * <li>vanilla-field-read: vanilla's own SpawnPlacements.getPlacementType reads Data.comp_2254 with GETFIELD; this mod's
 *     mixin at that field read (NativeFieldReadMixin) must fire, with the step layer loaded and without it -- the step's
 *     routes leave vanilla's path untouched.</li>
 * <li>mixin-on-accessor: this mod's mixin on the record accessor placementType() fires on W without any conversion (the
 *     read the step's CbSpawnData makes goes through that accessor, so the converted mod's "accessor-through-record" is
 *     judged against a mixin known to work).</li>
 * </ul>
 * Logs "CB-SKSPAWNN ok|FAIL <case> <detail>" and a summary.
 */
public final class Driver {
    public static volatile int placementReads;
    public static volatile int fieldReads;
    private static int ok;
    private static int fail;

    private static void check(boolean pass, String what, Object detail) {
        if (pass) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SKSPAWNN " + (pass ? "ok " : "FAIL ") + what + " " + detail);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void run() {
        int f0 = fieldReads;
        net.minecraft.class_9168 t = net.minecraft.class_1317.method_6159(net.minecraft.class_1299.field_6087);
        check(t == net.minecraft.class_9169.field_48743, "vanilla-type", "dolphin " + (t == net.minecraft.class_9169.field_48743 ? "IN_WATER" : t));
        check(fieldReads == f0 + 1, "vanilla-field-read", f0 + " -> " + fieldReads);
        java.util.Map<net.minecraft.class_1299<?>, net.minecraft.class_1317.class_1318> map = (java.util.Map) net.minecraft.class_1317.field_6313;
        int before = placementReads;
        net.minecraft.class_9168 direct = map.get(net.minecraft.class_1299.field_6087).comp_2254();
        check(placementReads == before + 1 && direct == t, "mixin-on-accessor", before + " -> " + placementReads);
        System.out.println("CB-SKSPAWNN summary ok=" + ok + " fail=" + fail);
    }
}
