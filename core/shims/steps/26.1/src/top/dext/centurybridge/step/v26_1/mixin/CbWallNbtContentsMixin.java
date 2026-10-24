package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.util.CompilableString;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** an NbtContents a 1.21.11 constructor made with a path that does not compile resolves to nothing, as 1.21.11's did */
@Mixin(NbtContents.class)
public abstract class CbWallNbtContentsMixin {

    @Shadow
    @Final
    private CompilableString<NbtPathArgument.NbtPath> nbtPath;

    @Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
    private void cb$uncompiled(ResolutionContext context, int depth, CallbackInfoReturnable<MutableComponent> cir) {
        if (this.nbtPath.compiled() == null) {
            cir.setReturnValue(Component.empty());
        }
    }
}
