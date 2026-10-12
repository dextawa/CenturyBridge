package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1308;
import net.minecraft.class_1408;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyNavigation;

/**
 * 1.20.1's PathNavigation.isInLiquid() back (s1misc navigation bridge), 1.20.1 body: the mob is
 * in water or in lava. Declared public; 1.20.1's was protected. The cb$ methods are the
 * trampolines CbNavigation reaches the navigation through.
 */
@Mixin(class_1408.class)
public abstract class CbNavigationLegacy implements CbLegacyNavigation {

    @Shadow
    @Final
    protected class_1308 field_6684;

    @Shadow
    protected abstract boolean method_6358();

    @Override
    public boolean method_6351() {
        return this.field_6684.method_5816() || this.field_6684.method_5771();
    }

    @Override
    public boolean cb$legacyIsInLiquid() {
        return this.method_6351();
    }

    @Override
    public class_1308 cb$navMob() {
        return this.field_6684;
    }

    @Override
    public boolean cb$canUpdatePath() {
        return this.method_6358();
    }

    @Override
    public boolean cb$amphibiousCanMoveDirectly(class_243 from, class_243 to) {
        return this.method_6351() && CbPathNavigationInvoker.cb$isClearForMovementBetween(this.field_6684, from, to, false);
    }
}
