package cb.s11.mixin;

import cb.s11.Driver;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.class_1928;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a 1.21.10 mod's mixin on GameRules.Value.setFromArgument: counts the game rule command's writes */
@Mixin(class_1928.class_4315.class)
public abstract class ArgSetMixin {
    @Inject(method = "method_20780(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)V", at = @At("HEAD"), remap = false)
    private void cbs11$countArg(CommandContext<?> context, String name, CallbackInfo ci) {
        Driver.argSets++;
    }
}
