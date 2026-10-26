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
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallAudio;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11's Library instance members 26.1 replaced with DeviceList / DeviceTracker (rt/CbWallAudio):
 * init(deviceName, directional) (class_4225.method_19661), hasDefaultDeviceChanged() (method_38723: whether the
 * system's default device differs from the one last seen, remembering the new one -- as 1.21.11 did, starting from
 * the default at init, which mixin/CbWallAudioLibraryReadsMixin records) and getAvailableSoundDevices() (method_38502).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = Library.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallAudioLibraryMixin implements CbWallLegacyCalls.AudioLibraryLegacy {

    @Unique
    private String cb$defaultDevice;

    @Shadow
    public abstract void init(String deviceName, DeviceList devices, boolean directionalAudio);

    @Override
    public synchronized void cb$rememberDefaultDevice() {
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
