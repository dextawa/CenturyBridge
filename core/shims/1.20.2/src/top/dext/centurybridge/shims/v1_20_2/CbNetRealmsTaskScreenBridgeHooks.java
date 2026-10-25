package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_4358;
import net.minecraft.class_437;
import net.minecraft.class_4394;
import net.minecraft.class_4398;
import net.minecraft.class_4905;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.facade.v1_20_2.class_5221;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyTask;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbNetRealmsTaskScreenBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_4398.class)
public abstract class CbNetRealmsTaskScreenBridgeHooks {

    @Inject(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4358;method_53808()Lnet/minecraft/class_2561;"), require = 1)
    private void cb$handOver(class_437 lastScreen, class_4358 task, CallbackInfo ci) {
        if (task instanceof CbNetLegacyTask) {
            ((CbNetRealmsAccess.Task) task).method_21066((class_4398) (Object) this);
        }
    }
}
