package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * TicketStorage.purgeStaleTickets(), 1.21.5's no-argument shape, restored on TicketStorage: 1.21.6's purge takes the
 * ChunkMap of the level whose tickets the storage holds (CbChunkTickets: the one its ServerChunkCache was built with,
 * the one ServerChunkCache itself passes). 1.21.5's body was the same removal pass with no holder check (javap); the
 * restored member runs 1.21.6's with that chunk map. Vanilla 1.21.6 never calls it.
 */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_10592.class)
public abstract class CbTicketStorageRestoredMixin {

    @org.spongepowered.asm.mixin.Shadow
    public abstract void method_66381(net.minecraft.class_3898 chunkMap);

    /** purgeStaleTickets() */
    public void method_66381() {
        this.method_66381(top.dext.centurybridge.step.v1_21_6.rt.CbChunkTickets.chunkMap((net.minecraft.class_10592) (Object) this));
    }
}
