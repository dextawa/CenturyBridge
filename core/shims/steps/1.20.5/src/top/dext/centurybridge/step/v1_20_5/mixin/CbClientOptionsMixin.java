package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Options.skipRealms32bitWarning: a public boolean in 1.20.4 (false by default), kept in options.txt under
 * "skipRealms32bitWarning". 1.20.5 dropped it with the 32-bit warning. The field is back, and processOptions
 * -- the one pass that both loads options.txt and writes it -- processes it as 1.20.4's did, so a value a
 * 1.20.4 mod set reads and persists the same way.
 *
 * The pass's FieldAccess (class_315$class_5823) is package-private; the layer's access widener opens it,
 * so the handler takes it as it is and calls its process(String, boolean) in bytecode (a reflective name
 * string would not follow the class as the layer climbs).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_315.class)
public abstract class CbClientOptionsMixin implements top.dext.centurybridge.step.v1_20_5.rt.CbClientRealms.Options32Bit {

    /** 1.20.4 Options.skipRealms32bitWarning */
    @org.spongepowered.asm.mixin.Shadow
    public boolean field_37208;

    @Override
    public boolean cb$skipRealms32bitWarning() {
        return this.field_37208;
    }

    @Override
    public void cb$setSkipRealms32bitWarning(boolean skip) {
        this.field_37208 = skip;
    }

    @Inject(method = "method_33666", at = @At("TAIL"))
    private void cb$processSkipRealms32bitWarning(net.minecraft.class_315.class_5823 access, CallbackInfo ci) {
        this.field_37208 = access.method_33684("skipRealms32bitWarning", this.field_37208);
    }
}
