package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * SpawnPlacements.Data (net/minecraft/class_1317$class_1318), which 1.20.5 made a record: its placement-type
 * component (comp_2254) holds 1.20.5's SpawnPlacementType where 1.20.4's field_6315 held the SpawnPlacements.Type
 * enum. Heightmap and predicate kept their types (fieldRenames field_6314 / field_19349 -> comp_2253 / comp_2255).
 */
public final class CbSpawnData {

    /**
     * A read of 1.20.4's Data.field_6315 (a mod's GETFIELD, or its @Accessor: Kilt's SpawnPlacementsDataAccessor):
     * the record's own accessor, so a mixin on it sees the read, as the 1.20.4 enum constant of that placement type
     * (the facade's of). Judgment: null for a type no 1.20.4 constant names (a SpawnPlacementType a 1.20.5 mod
     * made); 1.20.4 had no value for it, and null is what of answers for one.
     */
    public static top.dext.centurybridge.step.v1_20_5.facade.class_1317$class_1319 field_6315(net.minecraft.class_1317.class_1318 data) {
        return top.dext.centurybridge.step.v1_20_5.facade.class_1317$class_1319.of(data.comp_2254());
    }

    private CbSpawnData() {
    }
}
