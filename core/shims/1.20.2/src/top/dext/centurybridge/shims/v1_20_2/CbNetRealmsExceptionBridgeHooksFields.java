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
 * The fields of CbNetRealmsExceptionBridgeHooks that restore V's fields on the target, at the layer's CbVanillaReads.PRIORITY (mixin_priority.py
 * rule 3: below every earlier layer's mixin, so a climbing earlier layer's @Shadow of them resolves); CbNetRealmsExceptionBridgeHooks
 * keeps its injectors at the default priority and shadows these.
 */
@org.spongepowered.asm.mixin.Mixin(value = class_4355.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsExceptionBridgeHooksFields {

    /** was httpResultCode */
    public int field_19604;

    /** was rawResponse */
    public String field_36319;
}
