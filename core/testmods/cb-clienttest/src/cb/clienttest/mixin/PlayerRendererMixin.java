package cb.clienttest.mixin;

import cb.clienttest.CbLayer;
import net.minecraft.class_1007;
import net.minecraft.class_5617;
import net.minecraft.class_591;
import net.minecraft.class_742;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A 1.20.1 mod adding its own feature renderer (RenderLayer) to the player renderer, from its constructor. */
@Mixin(class_1007.class)
public abstract class PlayerRendererMixin extends class_922<class_742, class_591<class_742>> {
    protected PlayerRendererMixin(class_5617.class_5618 ctx, class_591<class_742> model, float shadow) {
        super(ctx, model, shadow);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cbClientTest$layer(class_5617.class_5618 ctx, boolean slim, CallbackInfo ci) {
        this.method_4046(new CbLayer(this));
    }
}
