package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.function.IntFunction;
import net.minecraft.class_1299;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_3542;
import net.minecraft.class_7995;

/**
 * 1.21.1 Boat.Type (net/minecraft/class_1690$class_1692), which 1.21.2 removed: a boat's wood is its EntityType
 * there (OAK_BOAT ... BAMBOO_RAFT, and the chest ones). The constants in 1.21.1's order, with its names and planks;
 * the intermediary field and method names are kept, which is what 1.21.1 bytecode reads. CODEC / byId / byName as
 * 1.21.1 built them (StringRepresentable.fromEnum, ByIdMap.continuous with the ZERO strategy, byName falling back to
 * OAK).
 */
public enum BoatType implements class_3542 {
    OAK(class_2246.field_10161, "oak"),
    SPRUCE(class_2246.field_9975, "spruce"),
    BIRCH(class_2246.field_10148, "birch"),
    JUNGLE(class_2246.field_10334, "jungle"),
    ACACIA(class_2246.field_10218, "acacia"),
    CHERRY(class_2246.field_42751, "cherry"),
    DARK_OAK(class_2246.field_10075, "dark_oak"),
    MANGROVE(class_2246.field_37577, "mangrove"),
    BAMBOO(class_2246.field_40294, "bamboo");

    public static final BoatType field_7727 = OAK;
    public static final BoatType field_7728 = SPRUCE;
    public static final BoatType field_7729 = BIRCH;
    public static final BoatType field_7730 = JUNGLE;
    public static final BoatType field_7725 = ACACIA;
    public static final BoatType field_42681 = CHERRY;
    public static final BoatType field_7723 = DARK_OAK;
    public static final BoatType field_37506 = MANGROVE;
    public static final BoatType field_40161 = BAMBOO;

    public static final class_3542.class_7292<BoatType> field_41599 = class_3542.method_28140(BoatType::values);
    public static final IntFunction<BoatType> field_41675 = class_7995.method_47914(Enum::ordinal, values(), class_7995.class_7996.field_41664);

    public final String field_7726;
    public final class_2248 field_7731;

    BoatType(class_2248 planks, String name) {
        this.field_7726 = name;
        this.field_7731 = planks;
    }

    @Override
    public String method_15434() {
        return this.field_7726;
    }

    /** getName() */
    public String method_7559() {
        return this.field_7726;
    }

    /** getPlanks() */
    public class_2248 method_7560() {
        return this.field_7731;
    }

    @Override
    public String toString() {
        return this.field_7726;
    }

    /** byId(int) */
    public static BoatType method_7558(int id) {
        return field_41675.apply(id);
    }

    /** byName(String) */
    public static BoatType method_7561(String name) {
        return field_41599.method_47920(name, OAK);
    }

    /** the 1.21.2 entity type of a boat (or chest boat) of this wood */
    public class_1299<?> entityType(boolean chest) {
        return switch (this) {
            case OAK -> chest ? class_1299.field_54411 : class_1299.field_54410;
            case SPRUCE -> chest ? class_1299.field_54417 : class_1299.field_54416;
            case BIRCH -> chest ? class_1299.field_54421 : class_1299.field_54420;
            case JUNGLE -> chest ? class_1299.field_54413 : class_1299.field_54412;
            case ACACIA -> chest ? class_1299.field_54415 : class_1299.field_54408;
            case CHERRY -> chest ? class_1299.field_54423 : class_1299.field_54422;
            case DARK_OAK -> chest ? class_1299.field_54407 : class_1299.field_54406;
            case MANGROVE -> chest ? class_1299.field_54409 : class_1299.field_54414;
            case BAMBOO -> chest ? class_1299.field_54418 : class_1299.field_54419;
        };
    }

    /** 1.21.1 Boat.getDropItem / ChestBoat.getDropItem of this variant */
    public class_1792 item(boolean chest) {
        return switch (this) {
            case OAK -> chest ? class_1802.field_38216 : class_1802.field_8533;
            case SPRUCE -> chest ? class_1802.field_38217 : class_1802.field_8486;
            case BIRCH -> chest ? class_1802.field_38218 : class_1802.field_8442;
            case JUNGLE -> chest ? class_1802.field_38212 : class_1802.field_8730;
            case ACACIA -> chest ? class_1802.field_38213 : class_1802.field_8094;
            case CHERRY -> chest ? class_1802.field_42707 : class_1802.field_42706;
            case DARK_OAK -> chest ? class_1802.field_38214 : class_1802.field_8138;
            case MANGROVE -> chest ? class_1802.field_38215 : class_1802.field_37531;
            case BAMBOO -> chest ? class_1802.field_40225 : class_1802.field_40224;
        };
    }

    /** the variant of a 1.21.2 boat entity type, or null for a type 1.21.1 had no variant for (PALE_OAK, a mod's) */
    public static BoatType of(class_1299<?> type) {
        for (BoatType t : values()) {
            if (t.entityType(false) == type || t.entityType(true) == type) {
                return t;
            }
        }
        return null;
    }
}
