package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_4345;
import net.minecraft.class_4355;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyRealmsError;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealms;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * RealmsServiceException: 1.20.1's carried the HTTP status and the response text beside the parsed
 * RealmsError; 1.20.2's carries the RealmsError only. Each exception gets its 1.20.1 description when
 * it is made (CbNetRealms.cbLegacyView), and the 1.20.1 fields and realmsErrorCodeOrDefault answer
 * from it.
 */
@Mixin(value = class_4355.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsExceptionBridge {

    /** was realmsErrorCodeOrDefault(fallback): the service's error code when 1.20.1 parsed a RealmsError */
    public int method_39980(int fallback) {
        CbNetLegacyRealmsError legacy = ((CbNetRealmsAccess.Failure) (Object) this).cb$legacy();
        return legacy.parsed() ? legacy.errorCode() : fallback;
    }
}
