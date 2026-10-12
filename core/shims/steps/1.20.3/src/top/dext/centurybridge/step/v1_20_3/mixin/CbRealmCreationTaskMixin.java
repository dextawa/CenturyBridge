package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbRealms;

/**
 * A RealmCreationTask made with the 1.20.2 constructor (worldId, name, motd, lastScreen) goes to lastScreen
 * once the realm is initialized, as 1.20.2's run() did; 1.20.3's task is a step of the reset flow and
 * leaves the screen to it.
 */
@Mixin(net.minecraft.class_4445.class)
public abstract class CbRealmCreationTaskMixin implements CbRealms.CbLegacyCreationTask {

    @Unique
    private net.minecraft.class_437 cb$lastScreen;

    @Unique
    private boolean cb$legacy;

    @Override
    public void cb$returnTo(net.minecraft.class_437 screen) {
        cb$lastScreen = screen;
        cb$legacy = true;
    }

    @Inject(method = "run", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
        target = "Lnet/minecraft/class_4341;method_20996(JLjava/lang/String;Ljava/lang/String;)V"))
    private void cb$returnToLastScreen(CallbackInfo ci) {
        if (cb$legacy) {
            net.minecraft.class_4358.method_25289(cb$lastScreen);
        }
    }
}
