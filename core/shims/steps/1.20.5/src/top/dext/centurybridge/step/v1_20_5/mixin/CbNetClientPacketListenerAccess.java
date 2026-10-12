package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ClientPacketListener's record of whether its server enforces secure chat (1.20.5: set from the login packet). */
@Mixin(class_634.class)
public interface CbNetClientPacketListenerAccess {

    @Accessor("field_48403")
    boolean cb$serverEnforcesSecureChat();
}
