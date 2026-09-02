package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;

/**
 * SpawnRestriction (class_1317) as the corpus names it. The class itself
 * survives as SpawnPlacements; only its Location enum (class_1319) died in
 * 1.20.5, replaced by the SpawnPlacementType interface and the constants on
 * SpawnPlacementTypes. 23 corpus mods read the enum constants (ON_GROUND /
 * IN_WATER / NO_RESTRICTIONS / IN_LAVA) to register a mob's spawn placement;
 * the converter renames the dead nested enum here and Statics.method_20637
 * turns it back into the vanilla constant.
 */
public final class class_1317 {

    private class_1317() {
    }

    /** was SpawnRestriction.Location */
    public enum class_1319 {
        /** ON_GROUND */
        field_6317(SpawnPlacementTypes.ON_GROUND),
        /** IN_WATER */
        field_6318(SpawnPlacementTypes.IN_WATER),
        /** NO_RESTRICTIONS */
        field_19350(SpawnPlacementTypes.NO_RESTRICTIONS),
        /** IN_LAVA */
        field_23221(SpawnPlacementTypes.IN_LAVA);

        private final SpawnPlacementType vanilla;

        class_1319(SpawnPlacementType vanilla) {
            this.vanilla = vanilla;
        }

        public SpawnPlacementType cbToVanilla() {
            return vanilla;
        }

        public static class_1319 cbFrom(SpawnPlacementType type) {
            for (class_1319 v : values()) {
                if (v.vanilla == type) {
                    return v;
                }
            }
            return field_19350;
        }
    }
}
