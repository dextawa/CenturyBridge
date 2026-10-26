package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_310;
import net.minecraft.class_4325;
import net.minecraft.class_7580;
import net.minecraft.class_8656;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbNetRealmsPopupBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_4325.class)
public abstract class CbNetRealmsPopupBridgeHooks {

    @Shadow
    private volatile boolean field_19500;

    @Inject(method = "method_44615", at = @At("TAIL"))
    private void cb$serversFetched(CallbackInfo ci) {
        ((CbNetRealmsAccess.Popup) (Object) this).cb$markServersFetched();
    }

    @Inject(method = "method_52639", at = @At("HEAD"), cancellable = true)
    private void cb$trialUpdateWhileCreated(Boolean available, CallbackInfo ci) {
        if (((CbNetRealmsAccess.Popup) (Object) this).cb$createdTrial()) {
            ci.cancel();
        }
    }

    @Redirect(method = "method_25394", at = @At(value = "FIELD", target = "Lnet/minecraft/class_4325;field_19500:Z", opcode = 180))
    private boolean cb$trialBadge(class_4325 self) {
        return this.field_19500 && !((CbNetRealmsAccess.Popup) (Object) this).cb$createdTrial();
    }

    @Redirect(method = "method_53791", at = @At(value = "FIELD", target = "Lnet/minecraft/class_4325;field_19500:Z", opcode = 180))
    private boolean cb$trialOffer(class_4325 self) {
        return this.field_19500 && !((CbNetRealmsAccess.Popup) (Object) this).cb$createdTrial();
    }
}
