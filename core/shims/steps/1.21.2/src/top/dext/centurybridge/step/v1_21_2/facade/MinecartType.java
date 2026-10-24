package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1299;

/**
 * 1.21.1 AbstractMinecart.Type (net/minecraft/class_1688$class_1689), which 1.21.2 removed: each kind of minecart
 * already had its own EntityType, and 1.21.2's createMinecart takes that instead. The constants in 1.21.1's order
 * and with its names (ordinals, switches and name() stay right); the intermediary field names are kept as aliases,
 * which is what 1.21.1 bytecode reads.
 */
public enum MinecartType {
    RIDEABLE,
    CHEST,
    FURNACE,
    TNT,
    SPAWNER,
    HOPPER,
    COMMAND_BLOCK;

    /** RIDEABLE (1.21.1 field) */
    public static final MinecartType field_7674 = RIDEABLE;
    /** CHEST (1.21.1 field) */
    public static final MinecartType field_7678 = CHEST;
    /** FURNACE (1.21.1 field) */
    public static final MinecartType field_7679 = FURNACE;
    /** TNT (1.21.1 field) */
    public static final MinecartType field_7675 = TNT;
    /** SPAWNER (1.21.1 field) */
    public static final MinecartType field_7680 = SPAWNER;
    /** HOPPER (1.21.1 field) */
    public static final MinecartType field_7677 = HOPPER;
    /** COMMAND_BLOCK (1.21.1 field) */
    public static final MinecartType field_7681 = COMMAND_BLOCK;

    /** the entity type 1.21.1's createMinecart constructed for this kind (its switch: new Minecart(..) is MINECART, ...) */
    public class_1299<? extends net.minecraft.class_1688> entityType() {
        return switch (this) {
            case RIDEABLE -> class_1299.field_6096;
            case CHEST -> class_1299.field_6126;
            case FURNACE -> class_1299.field_6080;
            case TNT -> class_1299.field_6053;
            case SPAWNER -> class_1299.field_6142;
            case HOPPER -> class_1299.field_6058;
            case COMMAND_BLOCK -> class_1299.field_6136;
        };
    }
}
