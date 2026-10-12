package top.dext.centurybridge.shims.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbScoreboard;

/**
 * The client's scoreboard packet handlers (ClientPlayNetworkHandler): where 1.20.1 asked the
 * scoreboard getObjective(name) -- team updates (method_11118), display-slot updates (method_11159)
 * -- or containsObjective(name) before getNullableObjective(name) -- objective updates
 * (method_11144) -- 1.20.2 calls getNullableObjective(name) alone. A scoreboard subclass's 1.20.1
 * overrides are consulted there again.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_634.class)
public abstract class CbScoreboardClientObjectiveCalls {

    @Redirect(method = "method_11118", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_269;method_1170(Ljava/lang/String;)Lnet/minecraft/class_266;", ordinal = 0))
    private net.minecraft.class_266 cb$teamObjective(net.minecraft.class_269 board, String name) {
        return CbScoreboard.objectiveAt(board, name);
    }

    @Redirect(method = "method_11159", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_269;method_1170(Ljava/lang/String;)Lnet/minecraft/class_266;", ordinal = 0))
    private net.minecraft.class_266 cb$displayObjective(net.minecraft.class_269 board, String name) {
        return CbScoreboard.objectiveAt(board, name);
    }

    @Redirect(method = "method_11144", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_269;method_1170(Ljava/lang/String;)Lnet/minecraft/class_266;", ordinal = 0))
    private net.minecraft.class_266 cb$objectiveUpdate(net.minecraft.class_269 board, String name) {
        return CbScoreboard.objectiveIfContained(board, name);
    }
}
