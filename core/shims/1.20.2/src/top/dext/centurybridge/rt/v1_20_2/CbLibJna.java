package top.dext.centurybridge.rt.v1_20_2;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

/**
 * JNA platform 5.12.1 (1.20.1) -> 5.13.0 (1.20.2): calls the newer Kernel32 / OleAuto / Psapi bindings declare. A mod's
 * implementation of a binding written for 5.12.1 lacks them; it answers what the platform's own binding (INSTANCE) answers,
 * the same Win32 function.
 */
public final class CbLibJna {
    private CbLibJna() {
    }

    public static boolean VirtualLock(Kernel32 self, Pointer address, BaseTSD.SIZE_T size) {
        return Kernel32.INSTANCE.VirtualLock(address, size);
    }

    public static boolean VirtualUnlock(Kernel32 self, Pointer address, BaseTSD.SIZE_T size) {
        return Kernel32.INSTANCE.VirtualUnlock(address, size);
    }

    public static int VariantTimeToSystemTime(OleAuto self, double vtime, WinBase.SYSTEMTIME systemTime) {
        return OleAuto.INSTANCE.VariantTimeToSystemTime(vtime, systemTime);
    }

    public static boolean QueryWorkingSetEx(Psapi self, WinNT.HANDLE process, Pointer pv, int cb) {
        return Psapi.INSTANCE.QueryWorkingSetEx(process, pv, cb);
    }
}
