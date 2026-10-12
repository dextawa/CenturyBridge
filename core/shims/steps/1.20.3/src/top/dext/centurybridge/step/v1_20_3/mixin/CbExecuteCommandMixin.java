package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbCommands;

/**
 * 1.20.3's command step (ExecuteCommand), which runs a command's contexts:
 * <ul>
 * <li>the brigadier result consumer it reports each context's result through keeps that context at hand,
 *     so a 1.20.2 ResultConsumer on the source is told the context, as 1.20.2's was
 *     ({@link CbCommands#cbReporting});</li>
 * <li>a 1.20.2 CommandRuntimeException a command throws is handled as 1.20.2's Commands.performCommand
 *     handled it -- its message is the failure the source is sent -- by passing it on as the
 *     CommandSyntaxException carrying that message, the one failure 1.20.3's step reports so.</li>
 * </ul>
 */
@Mixin(net.minecraft.class_8865.class)
public abstract class CbExecuteCommandMixin {

    @Redirect(method = "method_54416", at = @At(value = "INVOKE",
        target = "Lcom/mojang/brigadier/context/ContextChain;runExecutable(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/Object;Lcom/mojang/brigadier/ResultConsumer;Z)I"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int cb$runExecutable(CommandContext context, Object source, ResultConsumer consumer, boolean forked)
            throws CommandSyntaxException {
        try {
            return ContextChain.runExecutable(context, source, CbCommands.cbReporting(consumer), forked);
        } catch (top.dext.centurybridge.step.v1_20_3.facade.class_2164 legacy) {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(legacy.method_9199()), legacy.method_9199());
        }
    }
}
