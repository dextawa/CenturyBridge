package cb.skspawn;

import java.util.Map;
import net.minecraft.class_1299;
import net.minecraft.class_1317;
import net.minecraft.class_2902;

/**
 * A 1.20.4-era mod (intermediary names) doing what Kilt 20.1.22 does with SpawnPlacements.Data: reads a Data's placement
 * type through an @Accessor on field_6315 (cb.skspawn.mixin.DataAccessor) and builds a Data of its own with
 * new Data(heightmap, Type, predicate), putting it in SpawnPlacements' map. After the climb to 1.20.5 the read must
 * answer the 1.20.4 constant of vanilla's placement type and go through the record's accessor (cb-skspawnn, native
 * 1.20.5, counts comp_2254 calls; read by reflection), and 1.20.5's getPlacementType must answer the type the mod built.
 * Logs "CB-SKSPAWN ok|FAIL <case> <detail>" and "CB-SKSPAWN summary ok=N fail=M".
 */
public final class Driver {
    private static int ok;
    private static int fail;

    private static void check(boolean pass, String what, Object detail) {
        if (pass) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SKSPAWN " + (pass ? "ok " : "FAIL ") + what + " " + detail);
    }

    /** cb-skspawnn's count of 1.20.5 Data.comp_2254 calls, or -1 when it is absent */
    private static int nativeReads() {
        try {
            return Class.forName("cb.skspawnn.Driver").getField("placementReads").getInt(null);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void run() {
        try {
            Map<class_1299<?>, class_1317.class_1318> map = (Map) class_1317.field_6313;
            class_1317.class_1318 dolphin = map.get(class_1299.field_6087);
            int before = nativeReads();
            class_1317.class_1319 t = ((cb.skspawn.mixin.DataAccessor) (Object) dolphin).cbskspawn$placement();
            check(t == class_1317.class_1319.field_6318, "accessor-vanilla", "dolphin " + t);
            int after = nativeReads();
            check(before < 0 || after > before, "accessor-through-record", before + " -> " + after);

            class_1317.class_1318 old = map.get(class_1299.field_6131);
            class_1317.class_4306 pred = (type, level, reason, pos, random) -> true;
            class_1317.class_1318 mine = new class_1317.class_1318(class_2902.class_2903.field_13203,
                class_1317.class_1319.field_23221, pred);
            map.put(class_1299.field_6131, mine);
            try {
                class_1317.class_1319 back = ((cb.skspawn.mixin.DataAccessor) (Object) mine).cbskspawn$placement();
                check(back == class_1317.class_1319.field_23221, "constructed-accessor", back);
                class_1317.class_1319 vt = class_1317.method_6159(class_1299.field_6131);
                check(vt == class_1317.class_1319.field_23221, "constructed-getPlacementType", vt);
            } finally {
                if (old == null) {
                    map.remove(class_1299.field_6131);
                } else {
                    map.put(class_1299.field_6131, old);
                }
            }
        } catch (Throwable e) {
            check(false, "exception", e);
            e.printStackTrace();
        }
        System.out.println("CB-SKSPAWN summary ok=" + ok + " fail=" + fail);
    }
}
