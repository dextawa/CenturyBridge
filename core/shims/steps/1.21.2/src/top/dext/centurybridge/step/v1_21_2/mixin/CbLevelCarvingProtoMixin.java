package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_6643;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelCarving;

/** ProtoChunk's LIQUID carving mask, 1.21.1's second carving step (CbLevelCarving) */
@Mixin(net.minecraft.class_2839.class)
public abstract class CbLevelCarvingProtoMixin implements CbLevelCarving.ProtoState {

    @Unique
    private class_6643 cb$liquidMask;

    @Override
    public class_6643 cb$liquidMask() {
        return this.cb$liquidMask;
    }

    @Override
    public void cb$liquidMask(class_6643 mask) {
        this.cb$liquidMask = mask;
    }
}
