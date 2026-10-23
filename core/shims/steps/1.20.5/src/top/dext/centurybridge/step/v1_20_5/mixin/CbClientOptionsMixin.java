package top.dext.centurybridge.step.v1_20_5.mixin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Options.skipRealms32bitWarning: a public boolean in 1.20.4 (false by default), kept in options.txt under
 * "skipRealms32bitWarning". 1.20.5 dropped it with the 32-bit warning. The field is back, and processOptions
 * -- the one pass that both loads options.txt and writes it -- processes it as 1.20.4's did, so a value a
 * 1.20.4 mod set reads and persists the same way.
 *
 * The pass's FieldAccess (class_315$class_5823) is package-private; its process(String, boolean) is reached
 * through a method handle looked up in Options itself (this handler is merged there, in the same package),
 * resolved once -- as the 1.21.2 step's Options.hideBundleTutorial does.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_315.class)
public abstract class CbClientOptionsMixin {

    /** 1.20.4 Options.skipRealms32bitWarning */
    public boolean field_37208 = false;

    @Unique
    private static MethodHandle cb$processBoolean;

    @Inject(method = "method_33666", at = @At("TAIL"))
    private void cb$processSkipRealms32bitWarning(@Coerce Object access, CallbackInfo ci) {
        try {
            MethodHandle process = cb$processBoolean;
            if (process == null) {
                process = MethodHandles.lookup().findVirtual(Class.forName("net.minecraft.class_315$class_5823"),
                    "method_33684", MethodType.methodType(boolean.class, String.class, boolean.class));
                cb$processBoolean = process;
            }
            this.field_37208 = (boolean) process.invoke(access, "skipRealms32bitWarning", this.field_37208);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Options.skipRealms32bitWarning", t);
        }
    }
}
