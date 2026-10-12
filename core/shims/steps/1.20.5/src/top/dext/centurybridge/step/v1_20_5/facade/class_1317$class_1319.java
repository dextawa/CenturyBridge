package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4 SpawnPlacements.Type (net/minecraft/class_1317$class_1319), the enum of where a mob may spawn,
 * which 1.20.5 replaced by the SpawnPlacementType interface and its four SpawnPlacementTypes constants
 * (same names, same rules: 1.20.4's NaturalSpawner switch over the enum is 1.20.5's constants' own
 * isSpawnPositionOk). The constants in 1.20.4's order (ordinals and switches stay right); the
 * intermediary field names are kept as aliases, which is what 1.20.4 bytecode reads.
 */
public enum class_1317$class_1319 {
    ON_GROUND,
    IN_WATER,
    NO_RESTRICTIONS,
    IN_LAVA;

    /** ON_GROUND (1.20.4 field) */
    public static final class_1317$class_1319 field_6317 = ON_GROUND;
    /** IN_WATER (1.20.4 field) */
    public static final class_1317$class_1319 field_6318 = IN_WATER;
    /** NO_RESTRICTIONS (1.20.4 field) */
    public static final class_1317$class_1319 field_19350 = NO_RESTRICTIONS;
    /** IN_LAVA (1.20.4 field) */
    public static final class_1317$class_1319 field_23221 = IN_LAVA;

    /** the 1.20.5 placement type of this name (SpawnPlacementTypes) */
    public net.minecraft.class_9168 toPlacementType() {
        return switch (this) {
            case ON_GROUND -> net.minecraft.class_9169.field_48745;
            case IN_WATER -> net.minecraft.class_9169.field_48743;
            case NO_RESTRICTIONS -> net.minecraft.class_9169.field_48742;
            case IN_LAVA -> net.minecraft.class_9169.field_48744;
        };
    }

    /** a 1.20.4 type as its 1.20.5 placement type; null for null */
    public static net.minecraft.class_9168 placementType(class_1317$class_1319 type) {
        return type == null ? null : type.toPlacementType();
    }

    /**
     * the 1.20.4 constant of a 1.20.5 placement type: one of the four SpawnPlacementTypes constants;
     * null for null and for a type a 1.20.5 mod made (1.20.4 had no constant for it)
     */
    public static class_1317$class_1319 of(net.minecraft.class_9168 type) {
        for (class_1317$class_1319 t : values()) {
            if (t.toPlacementType() == type) {
                return t;
            }
        }
        return null;
    }
}
