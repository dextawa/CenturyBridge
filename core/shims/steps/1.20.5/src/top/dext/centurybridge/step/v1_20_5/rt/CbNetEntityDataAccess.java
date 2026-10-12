package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2945;

/**
 * What CbNetEntityDataBridge adds to a SynchedEntityData made over a builder's item array (see
 * {@link CbNetEntityData}): the builder it shares the array with, until build() hands it over.
 */
public interface CbNetEntityDataAccess {

    /** the builder whose item array this data object reads, while the entity is being constructed */
    void cb$attachBuilder(class_2945.class_9222 builder);

    /** build() answered this object: from now on its item array is its own */
    void cb$detachBuilder();
}
