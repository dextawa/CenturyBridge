package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * SubtitleOverlay.Subtitle with 1.20.6's getLocation() / getTime() (the last refresh, as 1.20.6 kept it); for a
 * 1.20.6 subclass, 1.21's overlay calls (getClosest, purgeOldInstances, isStillActive) answer from the subclass's
 * own getters, the way 1.20.6's overlay read them (arrow from getLocation(), dropped 3 s times the display time
 * after getTime(); CbClientSubtitleMixinHooks, default priority). 1.21 has no Subtitle subclass of its own, so a
 * subclass is a 1.20.6 one.
 */
@Mixin(targets = "net.minecraft.class_359$class_360", priority = CbVanillaReads.PRIORITY)
public abstract class CbClientSubtitleMixin implements CbPrioritySplit.Subtitle {

    @Unique
    private net.minecraft.class_243 cb$location;

    @Unique
    private long cb$time;

    /** getLocation() of 1.20.6 */
    public net.minecraft.class_243 method_1959() {
        return this.cb$location;
    }

    /** getTime() of 1.20.6 */
    public long method_1961() {
        return this.cb$time;
    }

    /** the location and time of the last play (the constructor, refresh: CbClientSubtitleMixinHooks) */
    @Override
    public void cb$v1_21$played(net.minecraft.class_243 location, long time) {
        this.cb$location = location;
        this.cb$time = time;
    }

    /** 1.20.6's getLocation called virtually, for 1.21's overlay calls (CbClientSubtitleMixinHooks) */
    @Override
    public net.minecraft.class_243 cb$v1_21$location() {
        return this.method_1959();
    }

    /** 1.20.6's getTime called virtually, for 1.21's overlay calls (CbClientSubtitleMixinHooks) */
    @Override
    public long cb$v1_21$time() {
        return this.method_1961();
    }
}
