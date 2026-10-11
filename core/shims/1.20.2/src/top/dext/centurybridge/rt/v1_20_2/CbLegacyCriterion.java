package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 Criterion.getId() shape. 1.20.2 moved a criterion's id to its
 * registration; a converted trigger that still declares getId gets this interface
 * (adapterHooks), so the registration bridges can ask it its name.
 */
public interface CbLegacyCriterion {

    /** getId() */
    net.minecraft.class_2960 method_794();
}
