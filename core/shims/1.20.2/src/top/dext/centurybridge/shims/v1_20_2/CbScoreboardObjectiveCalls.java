package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbScoreboard;

/**
 * Scoreboard.readNbt(list) (method_1188, restoring saved scores): 1.20.1 looked each objective up
 * through getObjective(name), which a scoreboard subclass could override; 1.20.2 calls
 * getNullableObjective(name). The lookup goes through the 1.20.1 one again.
 */
@Mixin(net.minecraft.class_269.class)
public abstract class CbScoreboardObjectiveCalls {

    @Redirect(method = "method_1188", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_269;method_1170(Ljava/lang/String;)Lnet/minecraft/class_266;", ordinal = 0))
    private net.minecraft.class_266 cb$getObjective(net.minecraft.class_269 board, String name) {
        return CbScoreboard.objectiveAt(board, name);
    }
}
