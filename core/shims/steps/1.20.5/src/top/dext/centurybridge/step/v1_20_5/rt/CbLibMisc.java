package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winspool;
import java.util.function.Function;

/**
 * DFU 6.0.8 -> 7.0.14 and JNA 5.13.0 -> 5.14.0 (1.20.4 -> 1.20.5), the members s4fin's DFU bridge (CbFinDfu) leaves:
 * <ul>
 * <li>KeyDispatchCodec's protected (typeKey, keyCodec, type, decoder, encoder, assumeMap) constructor: DFU 6 handed an
 *     assumeMap codec's dispatched coders the whole map (CbFinDfu's unsafe wrapping) and otherwise read a plain coder's
 *     value under "value" (a MapCodecCodec: its own map codec), as its decode / encode did; DFU 7's protected
 *     constructor takes map coders, which these answer;</li>
 * <li>DynamicLike.asBoolean(), abstract in DFU 7 (its asBoolean(boolean) asks it): DFU 6's asBoolean(def) read the
 *     number, {@code asNumber(def ? 1 : 0).intValue() != 0}, which this answers without the default;</li>
 * <li>the Kernel32 / Winspool functions JNA 5.14 added to its interfaces: a 5.13 implementation has none; the
 *     function is the operating system's, which JNA's own INSTANCE calls.</li>
 * </ul>
 */
public final class CbLibMisc {
    private CbLibMisc() {
    }

    // ---- KeyDispatchCodec(typeKey, keyCodec, type, decoder, encoder, assumeMap)

    /** the decoder function, as DFU 6 used it with assumeMap */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Function decoders(Function decoder, boolean assumeMap) {
        if (assumeMap || decoder == null) {
            return decoder == null ? null : CbFinDfu.wholeMapDecoders(decoder);
        }
        return key -> ((DataResult) decoder.apply(key)).map(d -> d instanceof MapCodec.MapCodecCodec m ? m.codec()
            : d instanceof MapDecoder md ? md : ((Decoder) d).fieldOf("value"));
    }

    /** the encoder function, as DFU 6 used it with assumeMap */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Function encoders(Function encoder, boolean assumeMap) {
        if (assumeMap || encoder == null) {
            return encoder == null ? null : CbFinDfu.wholeMapEncoders(encoder);
        }
        return value -> ((DataResult) encoder.apply(value)).map(e -> e instanceof MapCodec.MapCodecCodec m ? m.codec()
            : e instanceof MapEncoder me ? me : ((Encoder) e).fieldOf("value"));
    }

    // ---- DynamicLike.asBoolean()

    /** DynamicLike.asBoolean() for a DFU 6 subclass: its number, non-zero */
    public static DataResult<Boolean> asBoolean(DynamicLike<?> self) {
        return self.asNumber().map(n -> n.intValue() != 0);
    }

    // ---- JNA 5.14's new interface functions

    public static WinDef.DWORD GetPriorityClass(Kernel32 self, WinNT.HANDLE hProcess) {
        return Kernel32.INSTANCE.GetPriorityClass(hProcess);
    }

    public static boolean SetPriorityClass(Kernel32 self, WinNT.HANDLE hProcess, WinDef.DWORD dwPriorityClass) {
        return Kernel32.INSTANCE.SetPriorityClass(hProcess, dwPriorityClass);
    }

    public static int GetThreadPriority(Kernel32 self, WinNT.HANDLE hThread) {
        return Kernel32.INSTANCE.GetThreadPriority(hThread);
    }

    public static boolean SetThreadPriority(Kernel32 self, WinNT.HANDLE hThread, int nPriority) {
        return Kernel32.INSTANCE.SetThreadPriority(hThread, nPriority);
    }

    public static boolean SetJob(Winspool self, WinNT.HANDLE hPrinter, int jobId, int level, Pointer pJob, int command) {
        return Winspool.INSTANCE.SetJob(hPrinter, jobId, level, pJob, command);
    }

    public static boolean SetPrinter(Winspool self, WinNT.HANDLE hPrinter, int level, Pointer pPrinter, int command) {
        return Winspool.INSTANCE.SetPrinter(hPrinter, level, pPrinter, command);
    }
}
