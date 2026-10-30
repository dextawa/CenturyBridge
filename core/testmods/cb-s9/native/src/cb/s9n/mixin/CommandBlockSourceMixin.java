package cb.s9n.mixin;

import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A native 1.21.9 mixin on the per-run command block source's sendSystemMessage (BaseCommandBlock.CloseableCommandBlockSource),
 * the call the layer's route answers from the restored 1.21.8 member: counts its runs in the system property
 * cb.s9n.sends, read by cb-s9's CommandBlockTest.
 */
@Mixin(targets = "net.minecraft.class_1918$class_11874")
public abstract class CommandBlockSourceMixin {
    @Inject(method = "method_43496(Lnet/minecraft/class_2561;)V", at = @At("HEAD"))
    private void cbs9n$counted(class_2561 message, CallbackInfo ci) {
        System.setProperty("cb.s9n.sends", String.valueOf(Integer.getInteger("cb.s9n.sends", 0) + 1));
    }
}
