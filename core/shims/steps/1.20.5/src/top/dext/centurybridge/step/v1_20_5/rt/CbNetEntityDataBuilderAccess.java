package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2945;

/**
 * What CbNetEntityDataBuilderBridge adds to a {@code SynchedEntityData.Builder}: the data object
 * the entity under construction reads while its builder is filled (see {@link CbNetEntityData}).
 */
public interface CbNetEntityDataBuilderAccess {

    /** the SynchedEntityData over this builder's item array -- made once, and what build() answers */
    class_2945 cb$openView();

    /** the view grew its item array during construction: the builder goes on with that array */
    void cb$replaceItems(class_2945.class_2946<?>[] items);
}
