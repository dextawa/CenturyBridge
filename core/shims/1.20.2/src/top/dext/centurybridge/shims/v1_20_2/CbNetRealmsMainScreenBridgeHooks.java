package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_437;
import net.minecraft.class_4325;
import net.minecraft.class_4877;
import net.minecraft.class_4905;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyRealmsPlay;
import top.dext.centurybridge.rt.v1_20_2.Guard;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbNetRealmsMainScreenBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_4325.class)
public abstract class CbNetRealmsMainScreenBridgeHooks {

    @Inject(method = "method_20853", at = @At("HEAD"), cancellable = true)
    private static void cb$legacyPlay(class_4877 server, class_437 screen, CallbackInfo ci) {
        if (screen instanceof CbNetLegacyRealmsPlay legacy && Guard.cbEnter(screen)) {
            try {
                legacy.method_20853(server, screen);
            } finally {
                Guard.cbExit(screen);
            }
            ci.cancel();
        }
    }
}
