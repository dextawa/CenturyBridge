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
@Mixin(class_4355.class)
public abstract class CbNetRealmsExceptionBridge implements CbNetRealmsAccess.Failure {

    /** was httpResultCode */
    public int field_19604;

    /** was rawResponse */
    public String field_36319;

    @Unique
    private CbNetLegacyRealmsError cb$legacy;

    @Inject(method = "<init>(Lnet/minecraft/class_4345;)V", at = @At("RETURN"))
    private void cb$describe(class_4345 error, CallbackInfo ci) {
        CbNetLegacyRealmsError v = CbNetRealms.cbLegacyView(error, CbNetRealms.cbTakeBody());
        cb$legacy = v;
        field_19604 = v.httpCode();
        field_36319 = v.rawResponse();
    }

    @Override
    public CbNetLegacyRealmsError cb$legacy() {
        return cb$legacy;
    }

    /** was realmsErrorCodeOrDefault(fallback): the service's error code when 1.20.1 parsed a RealmsError */
    public int method_39980(int fallback) {
        return cb$legacy.parsed() ? cb$legacy.errorCode() : fallback;
    }
}
