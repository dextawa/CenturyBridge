package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallLevelSettings;

/**
 * The game rules a 1.21.11 caller built a LevelSettings with (rt/CbWallLevelSettings), kept beside 26.1's record.
 * 1.21.11's withGameType / withDifficulty / withDataConfiguration kept the same rules and copy() copied them; 26.1's
 * withers and copy() run unchanged, and the new record gets the rules after (RETURN). Settings no 1.21.11 caller made
 * carry none: nothing happens.
 */
@Mixin(LevelSettings.class)
public abstract class CbWallLevelSettingsMixin implements CbWallLevelSettings.Rules {

    @Unique
    private GameRules cb$gameRules;

    @Override
    public GameRules cb$gameRules() {
        return this.cb$gameRules;
    }

    @Override
    public void cb$setGameRules(GameRules rules) {
        this.cb$gameRules = rules;
    }

    @Unique
    private void cb$carry(CallbackInfoReturnable<LevelSettings> cir, boolean copy) {
        GameRules rules = this.cb$gameRules;
        LevelSettings next = cir.getReturnValue();
        if (rules != null && next != null && (Object) next != this) {
            GameRules carried = copy ? rules.copy(next.dataConfiguration().enabledFeatures()) : rules;
            CbWallLevelSettings.carried(rules, carried);
            ((CbWallLevelSettings.Rules) (Object) next).cb$setGameRules(carried);
        }
    }

    @Inject(method = "withGameType", at = @At("RETURN"))
    private void cb$withGameType(GameType gameType, CallbackInfoReturnable<LevelSettings> cir) {
        cb$carry(cir, false);
    }

    @Inject(method = "withDifficulty", at = @At("RETURN"))
    private void cb$withDifficulty(Difficulty difficulty, CallbackInfoReturnable<LevelSettings> cir) {
        cb$carry(cir, false);
    }

    @Inject(method = "withDifficultyLock", at = @At("RETURN"))
    private void cb$withDifficultyLock(boolean locked, CallbackInfoReturnable<LevelSettings> cir) {
        cb$carry(cir, false);
    }

    @Inject(method = "withDataConfiguration", at = @At("RETURN"))
    private void cb$withDataConfiguration(WorldDataConfiguration configuration, CallbackInfoReturnable<LevelSettings> cir) {
        cb$carry(cir, false);
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void cb$copy(CallbackInfoReturnable<LevelSettings> cir) {
        cb$carry(cir, true);
    }
}
