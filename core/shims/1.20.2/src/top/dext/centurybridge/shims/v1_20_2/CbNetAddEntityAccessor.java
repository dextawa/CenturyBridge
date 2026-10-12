package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2604;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The packed rotation bytes EntitySpawnS2CPacket stores (1.20.2's getters answer them in degrees):
 * what 1.20.1's PlayerSpawnS2CPacket.getYaw()/getPitch() answered, read without a virtual call.
 */
@Mixin(class_2604.class)
public interface CbNetAddEntityAccessor {

    @Accessor("field_11957")
    byte cb$yaw();

    @Accessor("field_11947")
    byte cb$pitch();
}
