package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2561;
import net.minecraft.class_4358;
import net.minecraft.class_4398;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.facade.v1_20_2.class_5221;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyTask;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbNetRealmsTaskBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_4358.class)
public abstract class CbNetRealmsTaskBridgeHooks {
    /** a 1.20.1 task reports a failure to its screen, as 1.20.1's error(message) did */
    @Inject(method = "method_21067", at = @At("HEAD"), cancellable = true)
    private void cb$legacyError(class_2561 message, CallbackInfo ci) {
        class_4398 screen = ((CbNetRealmsAccess.Task) (Object) this).cb$screen();
        if ((Object) this instanceof CbNetLegacyTask && screen != null) {
            ((CbNetRealmsAccess.TaskScreen) screen).method_21067(message);
            ci.cancel();
        }
    }
}
