package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.Collection;
import net.minecraft.class_266;
import net.minecraft.class_2995;
import net.minecraft.class_9011;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldScores;

/**
 * The scores ServerScoreboard sends a player who starts watching an objective: a scoreboard class converted from
 * 1.20.2 that overrides getPlayerScores(objective) answers Scores, handed on as entries (CbWorldScores.scoreEntries).
 */
@Mixin(class_2995.class)
public abstract class CbWorldServerScoreboardMixin {

    @Redirect(method = "method_12937", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2995;method_1184(Lnet/minecraft/class_266;)Ljava/util/Collection;"))
    private Collection<class_9011> cb$scoreEntries(class_2995 scoreboard, class_266 objective) {
        return CbWorldScores.scoreEntries(scoreboard, objective);
    }
}
