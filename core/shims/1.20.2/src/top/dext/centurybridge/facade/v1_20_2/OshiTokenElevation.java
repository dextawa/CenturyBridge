package top.dext.centurybridge.facade.v1_20_2;

import com.sun.jna.platform.win32.WinNT;

/**
 * OSHI 6.2.2's oshi.jna.platform.windows.WinNT.TOKEN_ELEVATION (gone in 6.4.5): JNA's TOKEN_ELEVATION structure that a
 * try-with-resources frees, close() being 6.2.2's oshi.util.Util.freeMemory(getPointer())
 */
public class OshiTokenElevation extends WinNT.TOKEN_ELEVATION implements AutoCloseable {
    public OshiTokenElevation() {
    }

    @Override
    public void close() {
        oshi.util.Util.freeMemory(getPointer());
    }
}
