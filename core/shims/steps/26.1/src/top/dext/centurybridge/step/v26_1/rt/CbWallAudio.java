package top.dext.centurybridge.step.v26_1.rt;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.blaze3d.audio.DeviceList;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALUtil;

/**
 * 1.21.11's OpenAL device queries on Library, which 26.1 moved to DeviceList / DeviceTracker. The queries are 1.21.11's
 * own bodies (plain OpenAL calls, which 26.1's LWJGL keeps); CbWallAudioLibraryMixin gives the instance members back.
 */
@Environment(EnvType.CLIENT)
public final class CbWallAudio {

    /** ALC_DEFAULT_ALL_DEVICES_SPECIFIER / ALC_ALL_DEVICES_SPECIFIER */
    private static final int DEFAULT_ALL_DEVICES = 4114;
    private static final int ALL_DEVICES = 4115;

    private CbWallAudio() {
    }

    /** 1.21.11 Library.getDefaultDeviceName(): the system's default device, null without the enumeration extension */
    public static String getDefaultDeviceName() {
        if (!ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
            return null;
        }
        ALUtil.getStringList(0L, ALL_DEVICES);
        return ALC10.alcGetString(0L, DEFAULT_ALL_DEVICES);
    }

    /** 1.21.11 Library.getAvailableSoundDevices() */
    public static List<String> getAvailableSoundDevices() {
        List<String> devices = ALUtil.getStringList(0L, ALL_DEVICES);
        return devices == null ? Collections.emptyList() : devices;
    }

    /** the device list 26.1's Library.init takes: the devices present now, as 1.21.11's init found them itself */
    public static DeviceList deviceList() {
        return DeviceList.query();
    }
}
