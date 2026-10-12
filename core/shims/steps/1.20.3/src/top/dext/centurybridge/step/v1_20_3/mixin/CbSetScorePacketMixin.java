package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.rt.CbNet;

/**
 * ClientboundSetScorePacket's 1.20.2 surface on 1.20.3's record: getOwner(), getObjectiveName(), getScore()
 * forward to the record's accessors (virtually, see overrideRenames), and getMethod() answers REMOVE for a
 * packet made as 1.20.2's REMOVE (marked here; {@link CbNet#cbOutgoing} sends a reset for it), CHANGE else.
 */
@Mixin(net.minecraft.class_2757.class)
public abstract class CbSetScorePacketMixin implements CbNet.CbLegacyScorePacket {

    @Unique
    private boolean cb$remove;

    @Override
    public boolean cb$remove() {
        return cb$remove;
    }

    @Override
    public void cb$markRemove() {
        cb$remove = true;
    }

    private net.minecraft.class_2757 cb$self() {
        return (net.minecraft.class_2757) (Object) this;
    }

    public String method_11862() {
        return cb$self().comp_2122();
    }

    public String method_11864() {
        return cb$self().comp_2123();
    }

    public int method_11865() {
        return cb$self().comp_2124();
    }

    public net.minecraft.class_2995.class_2996 method_11863() {
        return cb$remove ? net.minecraft.class_2995.class_2996.field_13430 : net.minecraft.class_2995.class_2996.field_13431;
    }
}
