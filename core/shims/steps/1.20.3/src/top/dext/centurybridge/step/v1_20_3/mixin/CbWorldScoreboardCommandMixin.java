package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.Collection;
import net.minecraft.class_2995;
import net.minecraft.class_3115;
import net.minecraft.class_9015;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldScores;

/**
 * /scoreboard players list (no target) lists the scoreboard's tracked players: a scoreboard class converted from
 * 1.20.2 that overrides getTrackedPlayers() answers names, handed on as holders (CbWorldScores.trackedHolders).
 */
@Mixin(class_3115.class)
public abstract class CbWorldScoreboardCommandMixin {

    @Redirect(method = "method_13589", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2995;method_1178()Ljava/util/Collection;"))
    private static Collection<class_9015> cb$trackedHolders(class_2995 scoreboard) {
        return CbWorldScores.trackedHolders(scoreboard);
    }
}
