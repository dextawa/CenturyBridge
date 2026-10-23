package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Minecraft's own DeltaTracker.Timer, read past a subclass's getTimer() (CbClientTime.cbLegacyTimer) */
@Mixin(net.minecraft.class_310.class)
public interface CbClientMinecraftTimerAccess {

    @Accessor("field_1728")
    net.minecraft.class_9779.class_9781 cb$timer();
}
