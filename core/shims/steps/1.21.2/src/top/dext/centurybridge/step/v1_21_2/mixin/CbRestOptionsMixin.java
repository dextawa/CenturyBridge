package top.dext.centurybridge.step.v1_21_2.mixin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Options.hideBundleTutorial of 1.21.1: a public flag (false by default) kept in options.txt under
 * "hideBundleTutorial". 1.21.2 dropped it with the bundle tutorial. The field is back, and
 * processOptions -- the one pass that both loads options.txt and writes it -- processes it as 1.21.1's
 * did, so the flag reads and persists the same way.
 *
 * The pass's FieldAccess (class_315$class_5823) is package-private; its process(String, boolean) is
 * reached through a method handle looked up in Options itself (this handler is merged there, in the
 * same package), resolved once.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_315.class)
public abstract class CbRestOptionsMixin {

    public boolean field_28777 = false;

    @Unique
    private static MethodHandle cb$processBoolean;

    @Inject(method = "method_33666", at = @At("TAIL"))
    private void cb$processHideBundleTutorial(@Coerce Object access, CallbackInfo ci) {
        try {
            MethodHandle process = cb$processBoolean;
            if (process == null) {
                process = MethodHandles.lookup().findVirtual(Class.forName("net.minecraft.class_315$class_5823"),
                    "method_33684", MethodType.methodType(boolean.class, String.class, boolean.class));
                cb$processBoolean = process;
            }
            this.field_28777 = (boolean) process.invoke(access, "hideBundleTutorial", this.field_28777);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Options.hideBundleTutorial", t);
        }
    }
}
