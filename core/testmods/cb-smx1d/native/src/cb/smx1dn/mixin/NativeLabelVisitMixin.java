package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_11735;
import net.minecraft.class_12225;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.11: MultiLineLabel's visitLines(align, x, y, lineHeight, collector) head (the lines' implementation, $2) */
@Mixin(targets = "net.minecraft.class_5489$2")
public class NativeLabelVisitMixin {
    @Inject(method = "method_75816", at = @At("HEAD"))
    private void cbSmx1dn$visit(class_11735 align, int x, int y, int lineHeight, class_12225 collector,
                                CallbackInfoReturnable<Integer> cir) {
        Probe.bumpRender("labelVisit");
    }
}
