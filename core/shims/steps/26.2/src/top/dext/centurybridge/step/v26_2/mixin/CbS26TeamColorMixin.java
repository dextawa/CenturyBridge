package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26Team;

/**
 * 26.1.2 styled a team's names with its colour, which could be a format (BOLD, ITALIC, ...); 26.2's applyColor
 * colours them with the team's TeamColor only. A format a 26.1.2 mod set as the team's colour (kept by
 * rt/CbS26Team, the TeamColor then empty) is applied here, where 26.2 colours the names.
 */
@Mixin(PlayerTeam.class)
public abstract class CbS26TeamColorMixin {

    @Inject(method = "applyColor", at = @At("RETURN"), cancellable = true)
    private void cb$legacyFormat(MutableComponent text, CallbackInfoReturnable<MutableComponent> cir) {
        cir.setReturnValue(CbS26Team.applyFormat((PlayerTeam) (Object) this, cir.getReturnValue()));
    }
}
