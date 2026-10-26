package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.List;
import net.minecraft.class_340;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.step.v1_21_9.rt.CbDebugText;

/**
 * 1.21.9's DebugHud.render gathers the F3 left and right line lists from its debug entries and draws them with
 * renderLines(graphics, lines, left): the left list first, the right one second. The restored GatherDebugTextEvents
 * (Fabric API 0.136) append to each list as it is handed over, after vanilla's lines as 0.136 appended at the return of
 * 1.21.8's getLeftText / getRightText. The list object is passed on unchanged; nothing runs before a converted mod
 * touched the events.
 */
@Mixin(class_340.class)
public abstract class CbDebugTextMixin {

    @ModifyArg(method = "method_1846(Lnet/minecraft/class_332;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_340;method_51745(Lnet/minecraft/class_332;Ljava/util/List;Z)V", ordinal = 0),
        index = 1)
    private List<String> cb$leftText(List<String> lines) {
        return CbDebugText.cbGather(lines, true);
    }

    @ModifyArg(method = "method_1846(Lnet/minecraft/class_332;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_340;method_51745(Lnet/minecraft/class_332;Ljava/util/List;Z)V", ordinal = 1),
        index = 1)
    private List<String> cb$rightText(List<String> lines) {
        return CbDebugText.cbGather(lines, false);
    }
}
