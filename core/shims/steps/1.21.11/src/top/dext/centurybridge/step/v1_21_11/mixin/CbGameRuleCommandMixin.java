package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.class_12279;
import net.minecraft.class_2168;
import net.minecraft.class_3065;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.11's GameRuleCommand.setRule(context, rule): marks the command in progress, so that 1.21.11's set (which the
 * command still calls, and writes) consults the 1.21.10 value's setFromArgument(context, "value") as 1.21.10's
 * command called it (CbGameRules.routeWrite), where a mod's 1.21.10 mixin on setFromArgument / updateFromArgument /
 * onChanged sees and can change the command's write. It merges nothing and only marks the command around 1.21.11's
 * body, so it runs at the default priority among the mods' injectors.
 */
@Mixin(class_3065.class)
public abstract class CbGameRuleCommandMixin {
    @Inject(method = "method_13394", at = @At("HEAD"))
    private static void cb$commandStart(CommandContext<class_2168> context, class_12279<?> rule, CallbackInfoReturnable<Integer> cir) {
        CbGameRules.commandStart(context, rule);
    }

    @Inject(method = "method_13394", at = @At("RETURN"))
    private static void cb$commandEnd(CommandContext<class_2168> context, class_12279<?> rule, CallbackInfoReturnable<Integer> cir) {
        CbGameRules.commandEnd();
    }
}
