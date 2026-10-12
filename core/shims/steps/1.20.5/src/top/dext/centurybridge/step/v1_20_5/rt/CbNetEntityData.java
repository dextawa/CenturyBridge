package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_1297;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;
import top.dext.centurybridge.step.v1_20_5.mixin.CbNetEntityDataInvoker;

/**
 * SynchedEntityData, 1.20.4 on 1.20.5 (area s4net).
 *
 * 1.20.4 kept an entity's tracked data in a map that took a {@code define(accessor, value)} at any
 * time: Entity's constructor made the (empty) data object first, then called
 * {@code defineSynchedData()}, where entities define theirs, and a mod could define more later --
 * in its own constructor after {@code super(..)}, say. An id allocated with {@code defineId} but
 * never defined was simply absent: a read of it crashed, a value arriving for it was skipped.
 *
 * 1.20.5 builds the data instead: Entity's constructor fills a {@code SynchedEntityData.Builder}
 * (an array with one slot per id allocated for the entity's class), passes the builder to
 * {@code defineSynchedData(Builder)}, and only then makes the data object -- so
 * {@code this.entityData} is still null while a 1.20.4 {@code defineSynchedData()} body runs (the
 * override hook forwards the new shape to the old one), and {@code build()} refuses any slot left
 * empty.
 *
 * The bridge gives 1.20.4's contract back without a second data object:
 * <ul>
 * <li>As soon as Entity's constructor has its builder, the entity's {@code entityData} is the
 *     SynchedEntityData {@code build()} will answer -- made early, over the builder's own item array
 *     (CbNetEntityDataConstructionBridge, CbNetEntityDataBuilderBridge). A 1.20.4 define during
 *     construction lands in that array exactly as the builder's own defines do, and a read or a
 *     set of an item defined already works, as it did in 1.20.4.</li>
 * <li>{@code build()} answers that object and leaves undefined ids empty (1.20.4 never required
 *     every allocated id to be defined).</li>
 * <li>{@code define} after construction fills the slot (growing the array for an id past the ones
 *     allocated for the entity's class, which 1.20.4's map took as well), with 1.20.4's checks.</li>
 * <li>Every 1.20.5 loop over the items skips an empty slot the way 1.20.4's map iteration never
 *     saw an undefined id, and a value received for an undefined id is skipped as 1.20.4 skipped it
 *     (CbNetEntityDataBridge).</li>
 * </ul>
 */
public final class CbNetEntityData {

    /**
     * {@code new SynchedEntityData(entity)}: 1.20.4 made an empty data object for the entity; 1.20.5
     * takes the item array, which this sizes like the builder does -- one empty slot per id
     * allocated for the entity's class -- so the defines that follow fit without growing.
     */
    public static class_2945.class_2946<?>[] cbNoItems(class_1297 entity) {
        return new class_2945.class_2946<?>[CbNetEntityDataInvoker.cb$idRegistry().method_56940(entity.getClass())];
    }

    /**
     * 1.20.4 {@code define(accessor, value)}'s checks, in its order and with its messages, against
     * the items of one data object: the id cap of the network format, a second definition of one
     * id, a serializer the network does not know.
     */
    public static void cbCheckDefine(class_2945.class_2946<?>[] items, class_2940<?> accessor) {
        int id = accessor.comp_2327();
        if (id > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is 254)");
        }
        if (id < items.length && items[id] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }
        if (class_2943.method_12719(accessor.comp_2328()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + accessor.comp_2328() + " for " + id + "!");
        }
    }

    private CbNetEntityData() {
    }
}
