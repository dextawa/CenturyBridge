package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_7094;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityAnimation;

/**
 * AnimationState's 1.21.1 clock, kept beside 1.21.2's startTick: the accumulated time (ms) and the last update
 * time that 1.21.1's updateTime(ageInTicks, speed) advanced and getAccumulatedTime() answered. start, fastForward
 * and copyFrom keep it as 1.21.1's start / fastForward did (CbEntityAnimation).
 */
@Mixin(class_7094.class)
public abstract class CbEntityAnimationStateMixin implements CbEntityAnimation.Clock {

    @Unique
    private long cb$lastTime = Long.MAX_VALUE;

    @Unique
    private long cb$accumulatedTime;

    @Inject(method = "method_41322", at = @At("RETURN"))
    private void cb$start(int tickCount, CallbackInfo ci) {
        // 1.21.1: lastTime = tickCount * 1000 / 20; accumulatedTime = 0
        this.cb$lastTime = (long) tickCount * 1000L / 20L;
        this.cb$accumulatedTime = 0L;
    }

    @Inject(method = "method_41325", at = @At("RETURN"))
    private void cb$stop(CallbackInfo ci) {
        this.cb$lastTime = Long.MAX_VALUE;
    }

    @Inject(method = "method_56988", at = @At("HEAD"))
    private void cb$fastForward(int ticks, float speed, CallbackInfo ci) {
        // 1.21.1: if started, accumulatedTime += (long) (ticks * 1000 * speed) / 20
        if (((class_7094) (Object) this).method_41327()) {
            this.cb$accumulatedTime += (long) ((float) (ticks * 1000) * speed) / 20L;
        }
    }

    @Inject(method = "method_61401", at = @At("RETURN"))
    private void cb$copyFrom(class_7094 other, CallbackInfo ci) {
        CbEntityAnimation.Clock o = (CbEntityAnimation.Clock) other;
        this.cb$lastTime = o.cb$lastTime();
        this.cb$accumulatedTime = o.cb$accumulatedTime();
    }

    @Override
    public long cb$lastTime() {
        return this.cb$lastTime;
    }

    @Override
    public long cb$accumulatedTime() {
        return this.cb$accumulatedTime;
    }

    @Override
    public void cb$set(long lastTime, long accumulatedTime) {
        this.cb$lastTime = lastTime;
        this.cb$accumulatedTime = accumulatedTime;
    }
}
