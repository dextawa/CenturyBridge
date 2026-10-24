package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.audio.DeviceList;
import com.mojang.blaze3d.audio.Library;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallAudio;

/**
 * 1.21.11's Library instance members 26.1 replaced with DeviceList / DeviceTracker (rt/CbWallAudio):
 * init(deviceName, directional) (class_4225.method_19661), hasDefaultDeviceChanged() (method_38723: whether the
 * system's default device differs from the one last seen, remembering the new one -- as 1.21.11 did, starting from
 * the default at init) and getAvailableSoundDevices() (method_38502).
 */
@Environment(EnvType.CLIENT)
@Mixin(Library.class)
public abstract class CbWallAudioLibraryMixin {

    @Unique
    private String cb$defaultDevice;

    @Shadow
    public abstract void init(String deviceName, DeviceList devices, boolean directionalAudio);

    @Inject(method = "init(Ljava/lang/String;Lcom/mojang/blaze3d/audio/DeviceList;Z)V", at = @At("TAIL"))
    private void cb$rememberDefault(String deviceName, DeviceList devices, boolean directionalAudio, CallbackInfo ci) {
        cb$defaultDevice = CbWallAudio.getDefaultDeviceName();
    }

    /** 1.21.11 Library.init(deviceName, directional) (class_4225.method_19661) */
    public void init(String deviceName, boolean directionalAudio) {
        init(deviceName, CbWallAudio.deviceList(), directionalAudio);
    }

    /** 1.21.11 Library.hasDefaultDeviceChanged() (method_38723) */
    public synchronized boolean hasDefaultDeviceChanged() {
        String now = CbWallAudio.getDefaultDeviceName();
        if (Objects.equals(cb$defaultDevice, now)) {
            return false;
        }
        cb$defaultDevice = now;
        return true;
    }

    /** 1.21.11 Library.getAvailableSoundDevices() (method_38502) */
    public List<String> getAvailableSoundDevices() {
        return CbWallAudio.getAvailableSoundDevices();
    }
}
