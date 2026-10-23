package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Supplier;
import net.minecraft.class_10255;
import net.minecraft.class_10256;
import net.minecraft.class_1299;
import net.minecraft.class_1690;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.class_4048;
import net.minecraft.class_7264;
import top.dext.centurybridge.step.v1_21_2.facade.BoatType;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityTypeFieldAccess;

/**
 * 1.21.1's Boat API on 1.21.2.
 *
 * <p>1.21.2 split 1.21.1's Boat into AbstractBoat (every boat, rafts and chest boats included -- what a 1.21.1
 * "Boat" was) and one EntityType per wood; ChestBoat into AbstractChestBoat. A converted mod names AbstractBoat /
 * AbstractChestBoat where it named Boat / ChestBoat (classRenames), the moved members by their new names
 * (gen_s7entity_boat.py). What died is here:
 * <ul>
 * <li>the variant: 1.21.1 kept it in synched data (default OAK) and drew the wood, the drop and the saved "Type"
 *     from it. On 1.21.2 the wood is the entity type, so {@link #getVariant} answers the type's wood, and
 *     {@link #setVariant} gives a boat of a vanilla boat type the variant's type (the type 1.21.2 spawns for that
 *     wood; a boat its level already holds is sent to its watchers again). A boat of another type (a mod's own)
 *     keeps the variant itself, saved as 1.21.1 saved it ("Type", CbEntityBoatMixin).</li>
 * <li>getDropItem (overridable in 1.21.1, final and fed by a supplier in 1.21.2): a boat built through a 1.21.1
 *     constructor gets a supplier answering 1.21.1's getDropItem -- the mod class's own override, else the
 *     variant's boat (or chest boat) item -- so 1.21.2's drop and pick result follow it.</li>
 * <li>the constructors, which in 1.21.2 take the drop supplier; 1.21.1's (level, x, y, z) ones were the BOAT /
 *     CHEST_BOAT type (1.21.2: OAK_BOAT / OAK_CHEST_BOAT, the variant 1.21.1 started from) at that position.</li>
 * </ul>
 */
public final class CbEntityBoats {

    /** what CbEntityBoatMixin gives AbstractBoat */
    public interface BoatFields {
        BoatType cb$variant();

        void cb$setVariant(BoatType variant);

        void cb$setDropItem(Supplier<class_1792> drop);
    }

    /** a mod boat class's own 1.21.1 getVariant (adapter hook) */
    public interface LegacyVariant {
        BoatType method_47885();
    }

    /** a mod boat class's own 1.21.1 getDropItem / getPickResult, renamed (1.21.2's are final) */
    public interface LegacyDrop {
        class_1792 cb$method_7557();
    }

    public interface LegacyPick {
        class_1799 cb$method_31480();
    }

    // ------------------------------------------------------------------------------------------ variant

    /** 1.21.1 Boat.getVariant(), called */
    public static BoatType getVariant(class_10255 boat) {
        if (boat instanceof LegacyVariant legacy) {
            return legacy.method_47885();
        }
        return superGetVariant(boat);
    }

    /** 1.21.1 Boat's own getVariant: the variant kept on the boat, else its type's wood, else OAK (the data default) */
    public static BoatType superGetVariant(class_10255 boat) {
        BoatType kept = ((BoatFields) boat).cb$variant();
        if (kept != null) {
            return kept;
        }
        BoatType t = BoatType.of(boat.method_5864());
        return t != null ? t : BoatType.OAK;
    }

    /** a mod boat class's own 1.21.1 setVariant (adapter hook) */
    public interface LegacySetVariant {
        void method_47884(BoatType variant);
    }

    /** 1.21.1 Boat.setVariant(type), called: the mod class's own override, else Boat's */
    public static void setVariant(class_10255 boat, BoatType variant) {
        if (boat instanceof LegacySetVariant legacy) {
            legacy.method_47884(variant);
            return;
        }
        superSetVariant(boat, variant);
    }

    /** 1.21.1 Boat's own setVariant (see the class javadoc) */
    public static void superSetVariant(class_10255 boat, BoatType variant) {
        if (BoatType.of(boat.method_5864()) == null) {
            ((BoatFields) boat).cb$setVariant(variant);
            return;
        }
        class_1299<?> type = variant.entityType(boat instanceof class_10256);
        if (type == boat.method_5864()) {
            return;
        }
        ((CbEntityTypeFieldAccess) boat).cb$setType(type);
        if (boat.method_37908() instanceof class_3218 level && level.method_14190(boat.method_5667()) == boat) {
            // its watchers made the boat by its old type: send it to them again
            level.method_14178().method_18753(boat);
            level.method_14178().method_18755(boat);
        }
    }

    // ------------------------------------------------------------------------------------------ drop / pick

    /** 1.21.1 Boat.getDropItem(), called */
    public static class_1792 getDropItem(class_10255 boat) {
        if (boat instanceof LegacyDrop legacy) {
            return legacy.cb$method_7557();
        }
        return superGetDropItem(boat);
    }

    /** 1.21.1 Boat's (and ChestBoat's) own getDropItem: the variant's boat or chest boat item */
    public static class_1792 superGetDropItem(class_10255 boat) {
        return getVariant(boat).item(boat instanceof class_10256);
    }

    /** ChestBoat.getDropItem(), called (1.21.2's is protected final) */
    public static class_1792 getDropItem(class_10256 boat) {
        return getDropItem((class_10255) boat);
    }

    /** ChestBoat.destroy(source): 1.21.1 VehicleEntity.destroy, the level threaded in (CbEntityLevelsGen) */
    public static void method_7516(class_10256 boat, net.minecraft.class_1282 source) {
        CbEntityLevelsGen.method_7516(boat, source);
    }

    public static class_1792 superGetDropItem(class_10256 boat) {
        return superGetDropItem((class_10255) boat);
    }

    /** a mod Boat's super.getPickResult(): 1.21.1's new ItemStack(getDropItem()) */
    public static class_1799 superGetPickResult(class_10255 boat) {
        return new class_1799(getDropItem(boat));
    }

    /** from the mixin on 1.21.2's final getPickResult: the mod class's own 1.21.1 override, or null */
    public static class_1799 pickResult(class_10255 boat) {
        return boat instanceof LegacyPick legacy ? legacy.cb$method_31480() : null;
    }

    // ------------------------------------------------------------------------------------------ constructors

    /** the supplier a 1.21.1 constructor's reshaped call passes; {@link #built} replaces it with the boat's own */
    public static Supplier<class_1792> noDrop() {
        return () -> class_1802.field_8533;
    }

    /** after a 1.21.1 constructor: the drop answers 1.21.1's getDropItem of this boat */
    public static void built(class_10255 boat) {
        ((BoatFields) boat).cb$setDropItem(() -> getDropItem(boat));
    }

    public static void built(class_10256 boat) {
        built((class_10255) boat);
    }

    /** after 1.21.1's (level, x, y, z) constructor: its position (1.21.2's setInitialPos, same body) */
    public static void at(class_10255 boat, double x, double y, double z) {
        boat.method_64498(x, y, z);
        built(boat);
    }

    public static void at(class_10256 boat, double x, double y, double z) {
        at((class_10255) boat, x, y, z);
    }

    /*
     * The same, typed as 1.21.1's Boat / ChestBoat: the wiring names the constructor's own class, and the
     * converted call names AbstractBoat / AbstractChestBoat once the class renames apply. Both shapes resolve.
     */
    public static void built(class_1690 boat) {
        built((class_10255) boat);
    }

    public static void built(class_7264 boat) {
        built((class_10255) boat);
    }

    public static void at(class_1690 boat, double x, double y, double z) {
        at((class_10255) boat, x, y, z);
    }

    public static void at(class_7264 boat, double x, double y, double z) {
        at((class_10255) boat, x, y, z);
    }

    public static double rideHeight(class_1690 boat, class_4048 dimensions) {
        return rideHeight((class_10255) boat, dimensions);
    }

    public static class_1299<?> oakBoat() {
        return class_1299.field_54410;
    }

    public static class_1299<?> oakChestBoat() {
        return class_1299.field_54411;
    }

    /** new Boat(type, level); typed as AbstractBoat, the class a converted mod names for 1.21.1's Boat */
    @SuppressWarnings("unchecked")
    public static class_10255 newBoat(class_1299<?> type, class_1937 level) {
        class_1690 boat = new class_1690((class_1299<? extends class_1690>) type, level, noDrop());
        built(boat);
        return boat;
    }

    /** new Boat(level, x, y, z): this(EntityType.BOAT, level) at x, y, z */
    public static class_10255 newBoat(class_1937 level, double x, double y, double z) {
        class_10255 boat = newBoat(class_1299.field_54410, level);
        boat.method_64498(x, y, z);
        return boat;
    }

    /** new ChestBoat(type, level) */
    @SuppressWarnings("unchecked")
    public static class_10256 newChestBoat(class_1299<?> type, class_1937 level) {
        class_7264 boat = new class_7264((class_1299<? extends class_7264>) type, level, noDrop());
        built(boat);
        return boat;
    }

    public static class_10256 newChestBoat(class_1937 level, double x, double y, double z) {
        class_10256 boat = newChestBoat(class_1299.field_54411, level);
        boat.method_64498(x, y, z);
        return boat;
    }

    /** AbstractBoat.rideHeight of a mod class of 1.21.1's Boat: 1.21.1's passenger height, a raft's for BAMBOO */
    public static double rideHeight(class_10255 boat, class_4048 dimensions) {
        return getVariant(boat) == BoatType.BAMBOO ? (double) (dimensions.comp_2186() * 0.8888889F)
            : (double) (dimensions.comp_2186() / 3.0F);
    }

    /** 1.21.1 EntityType.BOAT / CHEST_BOAT: the type a 1.21.1 boat started as (variant OAK) */
    public static class_1299<?> field_6121() {
        return class_1299.field_54410;
    }

    public static class_1299<?> field_38096() {
        return class_1299.field_54411;
    }

    /** Boat's public constants, now AbstractBoat's (same values) */
    public static int field_30697() {
        return class_10255.field_54427;
    }

    public static int field_30698() {
        return class_10255.field_54445;
    }

    public static double field_30699() {
        return class_10255.field_54446;
    }

    public static int field_30700() {
        return class_10255.field_54447;
    }

    private CbEntityBoats() {
    }
}
