package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_332;
import net.minecraft.class_4264;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.11: AbstractButton.renderDefaultSprite head, for the climbed SpriteIconButton subclass */
@Mixin(class_4264.class)
public class NativeDefaultSpriteMixin {
    @Inject(method = "method_75794", at = @At("HEAD"))
    private void cbSmx1dn$sprite(class_332 g, CallbackInfo ci) {
        if (this.getClass().getName().startsWith("cb.smx1d.Smx1dOld$OldIcon")) {
            Probe.bumpRender("defaultSprite.old");
        }
    }
}
