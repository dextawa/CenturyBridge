package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1688;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_9883;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityMinecartPhysics;

/**
 * OldMinecartBehavior (1.21.1's rail physics, moved off AbstractMinecart) asks a mod minecart's own 1.21.1
 * moveAlongTrack / getPos / getPosOffs overrides where 1.21.1's AbstractMinecart called them virtually; a super
 * call from such an override (CbEntityMinecartPhysics) runs this class's body once, moveAlongTrack on the pos and
 * state the override passed in place of the ones looked up.
 */
@Mixin(class_9883.class)
public abstract class CbEntityMinecartOldMixin implements CbEntityMinecartPhysics.OldState {

    @Unique
    private class_2338 cb$trackPos;

    @Unique
    private class_2680 cb$trackState;

    @Unique
    private boolean cb$skipPos;

    @Unique
    private boolean cb$skipPosOffs;

    @Unique
    private class_1688 cb$cart() {
        return ((CbEntityMinecartBehaviorAccess) (Object) this).cb$minecart();
    }

    @Override
    public void cb$track(class_2338 pos, class_2680 state) {
        this.cb$trackPos = pos;
        this.cb$trackState = state;
    }

    @Override
    public void cb$skipPos() {
        this.cb$skipPos = true;
    }

    @Override
    public void cb$skipPosOffs() {
        this.cb$skipPosOffs = true;
    }

    @Inject(method = "method_61589", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTrack(class_3218 level, CallbackInfo ci) {
        if (this.cb$trackPos == null && this.cb$cart() instanceof CbEntityMinecartPhysics.LegacyTrack) {
            // 1.21.1's tick called the minecart's moveAlongTrack(pos, state); its hook answers moveAlongTrack(level)
            ((CbEntityMinecartInvoker) this.cb$cart()).cb$moveAlongTrack(level);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "method_61589", at = @At(value = "INVOKE_ASSIGN",
        target = "Lnet/minecraft/class_1688;method_62825()Lnet/minecraft/class_2338;"))
    private class_2338 cb$trackPos(class_2338 pos) {
        class_2338 given = this.cb$trackPos;
        if (given == null) {
            return pos;
        }
        this.cb$trackPos = null;
        return given;
    }

    @ModifyVariable(method = "method_61589", at = @At(value = "INVOKE_ASSIGN",
        target = "Lnet/minecraft/class_1937;method_8320(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;"))
    private class_2680 cb$trackState(class_2680 state) {
        class_2680 given = this.cb$trackState;
        if (given == null) {
            return state;
        }
        this.cb$trackState = null;
        return given;
    }

    @Inject(method = "method_61620", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPos(double x, double y, double z, CallbackInfoReturnable<class_243> cir) {
        if (this.cb$skipPos) {
            this.cb$skipPos = false;
            return;
        }
        if (this.cb$cart() instanceof CbEntityMinecartPhysics.LegacyPos legacy) {
            cir.setReturnValue(legacy.method_7508(x, y, z));
        }
    }

    @Inject(method = "method_61619", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPosOffs(double x, double y, double z, double offset, CallbackInfoReturnable<class_243> cir) {
        if (this.cb$skipPosOffs) {
            this.cb$skipPosOffs = false;
            return;
        }
        if (this.cb$cart() instanceof CbEntityMinecartPhysics.LegacyPosOffs legacy) {
            cir.setReturnValue(legacy.method_7505(x, y, z, offset));
        }
    }
}
