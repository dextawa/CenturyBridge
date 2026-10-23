package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbFapiGameRules;

/**
 * A rule built in a 1.21.11 custom game rule category: tied to the category's stand-in, its own category() MISC
 * as 1.21.11 left it (rt/CbFapiGameRules.constructed)
 */
@Mixin(GameRule.class)
public abstract class CbFapiGameRuleMixin {
    @Shadow
    @Final
    @Mutable
    private GameRuleCategory category;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$customCategory(CallbackInfo ci) {
        this.category = CbFapiGameRules.constructed((GameRule<?>) (Object) this, this.category);
    }
}
