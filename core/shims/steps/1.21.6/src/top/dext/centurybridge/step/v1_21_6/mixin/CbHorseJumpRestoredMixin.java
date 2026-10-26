package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1496;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's AbstractHorse jump flag (javap), which 1.21.6 dropped for LivingEntity's own isJumping(): the protected
 * field isJumping (field_6968), isJumping() (method_6763) and setIsJumping(boolean) (method_6758), restored on
 * AbstractHorse with 1.21.5's bodies. 1.21.5 set it in executeRidersJump and cleared it in tickRidden on landing,
 * right before reading it for the next jump; CbHorseJumpRouteMixin does the same on 1.21.6's paths. Priority below
 * every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1496.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbHorseJumpRestoredMixin implements top.dext.centurybridge.step.v1_21_6.rt.CbHorses.LegacyJump {

    protected boolean field_6968;

    @CbVanillaReads("CbHorseJumpRouteMixin: tickRidden asks it on landing, after clearing the flag, and keeps 1.21.6's "
        + "jump only when it answers false (1.21.5's !isJumping() check)")
    @Override
    public boolean method_6763() {
        return this.field_6968;
    }

    @CbVanillaReads("CbHorseJumpRouteMixin: executeRidersJump sets it after the jump velocity, tickRidden clears it on "
        + "landing, as 1.21.5's did")
    @Override
    public void method_6758(boolean jumping) {
        this.field_6968 = jumping;
    }
}
