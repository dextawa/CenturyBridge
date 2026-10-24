package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbFapiGameRules;

/**
 * 26.1's edit-rules screen groups (and filters) a rule by its category: a rule in a 1.21.11 custom category is
 * listed under the category's stand-in, headed by the custom name, as 1.21.11's screen listed it
 * (rt/CbFapiGameRules.screenCategory)
 */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.AbstractGameRulesScreen$RuleList$1")
public abstract class CbFapiGameRulesScreenMixin {

    @Redirect(method = "addEntry", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRule;category()Lnet/minecraft/world/level/gamerules/GameRuleCategory;"))
    private GameRuleCategory cb$screenCategory(GameRule<?> rule) {
        return CbFapiGameRules.screenCategory(rule);
    }
}
