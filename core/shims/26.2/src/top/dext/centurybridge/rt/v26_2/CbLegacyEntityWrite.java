package top.dext.centurybridge.rt.v26_2;

/**
 * The 1.20.1 Entity.writeCustomDataToNbt shape, added to converted classes that override it (see
 * CbNbtStorage). Past the namespace wall the override carries the mojang name of its
 * intermediary id with the old descriptor, so that is the name declared here.
 */
public interface CbLegacyEntityWrite {

    /** writeCustomDataToNbt(nbt) */
    void addAdditionalSaveData(net.minecraft.nbt.CompoundTag nbt);
}
