package top.dext.centurybridge.step.v1_21.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.class_3898;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPackets;

/** ChunkMap: the tracked entities, for a 1.20.6 getAddEntityPacket() call (CbEntityPackets) */
@Mixin(class_3898.class)
public abstract class CbEntityTrackingMixin implements CbEntityPackets.TrackedEntities {

    @Shadow
    @Final
    private Int2ObjectMap<?> field_18242;

    @Override
    public Int2ObjectMap<?> cb$entityMap() {
        return this.field_18242;
    }
}
