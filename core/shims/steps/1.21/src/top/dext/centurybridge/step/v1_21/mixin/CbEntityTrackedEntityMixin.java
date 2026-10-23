package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_3231;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPackets;

/** ChunkMap.TrackedEntity: its ServerEntity, for a 1.20.6 getAddEntityPacket() call (CbEntityPackets) */
@Mixin(targets = "net.minecraft.class_3898$class_3208")
public abstract class CbEntityTrackedEntityMixin implements CbEntityPackets.TrackedServerEntity {

    @Shadow
    @Final
    class_3231 field_18246;

    @Override
    public class_3231 cb$serverEntity() {
        return this.field_18246;
    }
}
