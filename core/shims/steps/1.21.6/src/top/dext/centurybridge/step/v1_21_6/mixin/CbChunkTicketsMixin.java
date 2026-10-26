package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * Records each ServerChunkCache's TicketStorage with its ChunkMap once the constructor has built both (1.21.6 javap:
 * field_55594 from the level's data storage, then field_17254), for CbChunkTickets.chunkMap. Observes only.
 */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_3215.class)
public abstract class CbChunkTicketsMixin {

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private net.minecraft.class_10592 field_55594;

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    public net.minecraft.class_3898 field_17254;

    @org.spongepowered.asm.mixin.injection.Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void cb$recordTickets(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_6.rt.CbChunkTickets.owned(this.field_55594, this.field_17254);
    }
}
