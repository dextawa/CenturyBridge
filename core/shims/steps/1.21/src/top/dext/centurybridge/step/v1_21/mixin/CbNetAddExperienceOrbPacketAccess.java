package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * ClientboundAddExperienceOrbPacket's position, set after a 1.20.6-shaped construction to the orb's own
 * coordinates (CbNet.cbOrbPacketBuilt).
 */
@Mixin(net.minecraft.class_2606.class)
public interface CbNetAddExperienceOrbPacketAccess {

    @Mutable
    @Accessor("field_11972")
    void cb$setX(double x);

    @Mutable
    @Accessor("field_11971")
    void cb$setY(double y);

    @Mutable
    @Accessor("field_11970")
    void cb$setZ(double z);
}
