package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiMinecraftBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_310.class)
public abstract class CbGuiMinecraftBridgeHooks {
    /** the frame duration 1.20.1 pushed into frameTimer, as 1.20.2 logs it */
    @ModifyArg(method = "method_1523", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_340;method_53534(J)V"))
    private long cb$logFrame(long nanos) {
        ((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.FrameTimer) (Object) this).cb$frameTimer().method_15247(nanos);
        return nanos;
    }
}
