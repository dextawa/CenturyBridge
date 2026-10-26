package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.BinaryOperator;
import java.util.function.IntConsumer;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbCommands;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * CommandSourceStack's 1.20.2 callback members on 1.20.3's CommandResultCallback ({@link CbCommands}):
 * withCallback(ResultConsumer[, merge]), onCommandComplete(context, success, result), and the return-value
 * consumer's withReturnValueConsumer / getReturnValueConsumer.
 */
@Mixin(value = net.minecraft.class_2168.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCommandSourceStackMixin {

    private net.minecraft.class_2168 cb$self() {
        return (net.minecraft.class_2168) (Object) this;
    }

    public net.minecraft.class_2168 method_9231(ResultConsumer<net.minecraft.class_2168> consumer) {
        return CbCommands.method_9231(cb$self(), consumer);
    }

    public net.minecraft.class_2168 method_9209(ResultConsumer<net.minecraft.class_2168> consumer,
                                                BinaryOperator<ResultConsumer<net.minecraft.class_2168>> merge) {
        return CbCommands.method_9209(cb$self(), consumer, merge);
    }

    /**
     * onCommandComplete(context, success, result), 1.20.2's: the source's consumer told -- a 1.20.2 consumer
     * with this very context, 1.20.3's callback with the result
     */
    public void method_9215(CommandContext<net.minecraft.class_2168> context, boolean success, int result) {
        CbCommands.method_9215(cb$self(), context, success, result);
    }

    public net.minecraft.class_2168 method_51411(IntConsumer returnValues) {
        return CbCommands.method_51411(cb$self(), returnValues);
    }

    public IntConsumer method_51413() {
        return CbCommands.method_51413(cb$self());
    }
}
