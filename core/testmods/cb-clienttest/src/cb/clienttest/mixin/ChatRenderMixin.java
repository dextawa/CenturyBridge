package cb.clienttest.mixin;

import cb.clienttest.Hooks;
import net.minecraft.class_332;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A 1.20.1 mod's injector at ChatComponent.render(GuiGraphics, tickCount, mouseX, mouseY) HEAD and RETURN. */
@Mixin(class_338.class)
public abstract class ChatRenderMixin {
    @Inject(method = "method_1805(Lnet/minecraft/class_332;III)V", at = @At("HEAD"))
    private void cbClientTest$head(class_332 ctx, int tick, int mouseX, int mouseY, CallbackInfo ci) {
        Hooks.head(Hooks.CHAT, this);
    }

    @Inject(method = "method_1805(Lnet/minecraft/class_332;III)V", at = @At("RETURN"))
    private void cbClientTest$ret(class_332 ctx, int tick, int mouseX, int mouseY, CallbackInfo ci) {
        Hooks.ret(Hooks.CHAT, this);
    }
}
