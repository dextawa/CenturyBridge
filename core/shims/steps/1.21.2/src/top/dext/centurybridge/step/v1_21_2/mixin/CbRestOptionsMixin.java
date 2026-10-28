package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Options.hideBundleTutorial of 1.21.1: a public flag (false by default) kept in options.txt under
 * "hideBundleTutorial". 1.21.2 dropped it with the bundle tutorial. The field is back, and
 * processOptions -- the one pass that both loads options.txt and writes it -- processes it as 1.21.1's
 * did, so the flag reads and persists the same way.
 *
 * The pass's FieldAccess (class_315$class_5823) is package-private; the layer's access widener opens it,
 * so the handler takes it as it is and calls its process(String, boolean) in bytecode (a reflective name
 * string would not follow the class as the layer climbs).
 */
@Environment(EnvType.CLIENT)
@Mixin(class_315.class)
public abstract class CbRestOptionsMixin implements top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial.BundleOption {

    @org.spongepowered.asm.mixin.Shadow
    public boolean field_28777;

    @Override
    public boolean cb$hideBundleTutorial() {
        return this.field_28777;
    }

    @Override
    public void cb$setHideBundleTutorial(boolean hide) {
        this.field_28777 = hide;
    }

    @Inject(method = "method_33666", at = @At("TAIL"))
    private void cb$processHideBundleTutorial(class_315.class_5823 access, CallbackInfo ci) {
        this.field_28777 = access.method_33684("hideBundleTutorial", this.field_28777);
    }
}
