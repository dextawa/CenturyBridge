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
 * The injectors into 1.20.2's own methods that go with {@link CbNetRealmsExceptionBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_4355.class)
public abstract class CbNetRealmsExceptionBridgeHooks implements CbNetRealmsAccess.Failure {

    /** was httpResultCode */
    public int field_19604;

    /** was rawResponse */
    public String field_36319;

    @Unique
    private CbNetLegacyRealmsError cb$legacy;

    @Override
    public CbNetLegacyRealmsError cb$legacy() {
        return cb$legacy;
    }

    @Inject(method = "<init>(Lnet/minecraft/class_4345;)V", at = @At("RETURN"))
    private void cb$describe(class_4345 error, CallbackInfo ci) {
        CbNetLegacyRealmsError v = CbNetRealms.cbLegacyView(error, CbNetRealms.cbTakeBody());
        cb$legacy = v;
        field_19604 = v.httpCode();
        field_36319 = v.rawResponse();
    }
}
