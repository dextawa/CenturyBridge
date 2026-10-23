package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_310;
import net.minecraft.class_4358;
import net.minecraft.class_437;
import net.minecraft.class_4388;
import net.minecraft.class_4398;
import net.minecraft.class_4435;
import net.minecraft.class_4437;
import net.minecraft.class_4877;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * RealmsConfigureWorldScreen's 1.20.6 openTheWorld(join, lastScreen) and closeTheWorld(lastScreen): 1.21's take
 * no screen and return to a fresh configure screen (getNewScreen()); a 1.20.6 caller's screen is kept. 1.20.6's
 * bodies on 1.21's (unchanged) long-running task screen and open / close server tasks.
 */
@Mixin(class_4388.class)
public abstract class CbRealmsConfigureMixin {

    @Shadow
    private class_4877 field_20493;

    /** 1.20.6 openTheWorld(join, lastScreen): the open-server task on a long-running task screen returning to lastScreen */
    public void method_21218(boolean join, class_437 lastScreen) {
        class_310 minecraft = class_310.method_1551();
        minecraft.method_1507(new class_4398(lastScreen, new class_4358[]{new class_4437(this.field_20493, lastScreen, join, minecraft)}));
    }

    /** 1.20.6 closeTheWorld(lastScreen): the close-server task on a long-running task screen returning to lastScreen */
    public void method_21217(class_437 lastScreen) {
        class_310.method_1551().method_1507(new class_4398(lastScreen, new class_4358[]{new class_4435(this.field_20493, (class_4388) (Object) this)}));
    }
}
