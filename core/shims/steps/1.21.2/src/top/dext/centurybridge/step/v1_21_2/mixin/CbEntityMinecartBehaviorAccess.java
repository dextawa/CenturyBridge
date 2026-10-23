package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** MinecartBehavior's minecart (protected final), read by CbEntityMinecartOldMixin. */
@Mixin(net.minecraft.class_9878.class)
public interface CbEntityMinecartBehaviorAccess {

    @Accessor("field_52520")
    net.minecraft.class_1688 cb$minecart();
}
