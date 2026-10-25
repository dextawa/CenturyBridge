package cb.c10.mixin;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "tick(Z)V", at = @At("HEAD"))
    private void cbc10$tick(boolean paused, CallbackInfo ci) {
        Gui self = (Gui) (Object) this;
        cb.c10.Seen.gui(self);
    }
}
