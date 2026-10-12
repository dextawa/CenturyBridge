package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.brigadier.ParseResults;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbCommands;

/**
 * Commands.performCommand(ParseResults, String) and performPrefixedCommand(source, command) as 1.20.2 had
 * them, answering the command's result (1.20.3's answer nothing) -- {@link CbCommands}. performPrefixedCommand
 * goes through this class's performCommand, as 1.20.2's did, so a 1.20.2 subclass's override is reached.
 */
@Mixin(net.minecraft.class_2170.class)
public abstract class CbCommandsMixin {

    public int method_9249(ParseResults<net.minecraft.class_2168> parse, String command) {
        return CbCommands.method_9249((net.minecraft.class_2170) (Object) this, parse, command);
    }

    public int method_44252(net.minecraft.class_2168 source, String command) {
        String line = command.startsWith("/") ? command.substring(1) : command;
        return method_9249(((net.minecraft.class_2170) (Object) this).method_9235().parse(line, source), line);
    }
}
