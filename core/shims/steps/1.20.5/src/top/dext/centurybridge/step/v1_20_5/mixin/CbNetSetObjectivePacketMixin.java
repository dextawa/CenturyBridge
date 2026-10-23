package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * ClientboundSetObjectivePacket: 1.20.4's getNumberFormat() (the format, or null; 1.20.5 answers an Optional)
 * beside 1.20.5's, reading the packet's field.
 */
@Mixin(net.minecraft.class_2751.class)
public abstract class CbNetSetObjectivePacketMixin {

    @Shadow
    @Final
    private Optional<net.minecraft.class_9022> field_47519;

    /** was getNumberFormat() */
    public net.minecraft.class_9022 method_55336() {
        return this.field_47519.orElse(null);
    }
}
