package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.audio.DeviceList;
import com.mojang.blaze3d.audio.Library;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11's hasDefaultDeviceChanged (CbWallAudioLibraryMixin) starts from the system's default device at init: 26.1's
 * init records it. Default priority: injector into a 26.1 method.
 */
@Environment(EnvType.CLIENT)
@Mixin(Library.class)
public abstract class CbWallAudioLibraryReadsMixin {

    @Inject(method = "init(Ljava/lang/String;Lcom/mojang/blaze3d/audio/DeviceList;Z)V", at = @At("TAIL"))
    private void cb$rememberDefault(String deviceName, DeviceList devices, boolean directionalAudio, CallbackInfo ci) {
        ((CbWallLegacyCalls.AudioLibraryLegacy) (Object) this).cb$rememberDefaultDevice();
    }
}
