package cb.s9.mixin;

import net.minecraft.class_1918;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a 1.21.8 mixin on BaseCommandBlock.sendSystemMessage(message): records what the block is told (CommandBlockTest) */
@Mixin(class_1918.class)
public abstract class CommandBlockOutputMixin {
    @Inject(method = "method_43496(Lnet/minecraft/class_2561;)V", at = @At("HEAD"))
    private void cbs9$seen(class_2561 message, CallbackInfo ci) {
        cb.s9.CommandBlockTest.SEEN.add(message);
    }
}
