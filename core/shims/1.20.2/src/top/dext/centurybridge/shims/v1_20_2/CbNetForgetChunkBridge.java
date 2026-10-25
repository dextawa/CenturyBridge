package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2666;
import org.spongepowered.asm.mixin.Mixin;

/**
 * UnloadChunkS2CPacket: (x, z) in 1.20.1, a record around a ChunkPos in 1.20.2. getX/getZ answer the
 * position's coordinates, for converted calls and a subclass's super calls (extendable again);
 * vanilla 1.20.2 reads pos().
 */
@Mixin(value = class_2666.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetForgetChunkBridge {

    /** was getX() */
    public int method_11487() {
        return ((class_2666) (Object) this).comp_1726().field_9181;
    }

    /** was getZ() */
    public int method_11485() {
        return ((class_2666) (Object) this).comp_1726().field_9180;
    }
}
