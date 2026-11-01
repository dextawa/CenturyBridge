package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_12225;
import net.minecraft.class_4264;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.11: AbstractButton.renderDefaultLabel(collector)'s head -- vanilla's text path, per button kind */
@Mixin(class_4264.class)
public class NativeDefaultLabelMixin {
    @Inject(method = "method_75793", at = @At("HEAD"))
    private void cbSmx1dn$defaultLabel(class_12225 collector, CallbackInfo ci) {
        Probe.bumpRender(this.getClass().getName().startsWith("cb.smx1d.") ? "defaultLabel.old" : "defaultLabel.vanilla");
    }
}
