package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_2561;
import net.minecraft.class_5676;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.11: CycleButton.Builder.build(x, y, w, h, caption, onValueChange)'s head (every build form ends there) */
@Mixin(class_5676.class_5677.class)
public class NativeCycleBuildMixin {
    @Inject(method = "method_32617", at = @At("HEAD"))
    private void cbSmx1dn$build(int x, int y, int w, int h, class_2561 caption, class_5676.class_5678<?> onChange,
                                CallbackInfoReturnable<class_5676<?>> cir) {
        Probe.bumpRender("cycleBuild");
    }
}
