package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2's net/minecraft/class_2158$class_2160 (CommandFunction.CommandEntry), gone in 1.20.3:
 * one parsed command of a function. The command runs through 1.20.3's execution
 * ({@link top.dext.centurybridge.step.v1_20_3.rt.CbFunctions#runParsed}), at once, as 1.20.2's dispatcher ran
 * it -- 1.20.3's own commands (function, return, execute's forks) only run there -- and is traced and its
 * failure reported the way 1.20.3's execution does it: to the tracer, and to the source unless it forked.
 */
public class class_2158$class_2160 implements class_2158$class_2161 {

    /** parse */
    public final com.mojang.brigadier.ParseResults<net.minecraft.class_2168> field_9811;

    public class_2158$class_2160(com.mojang.brigadier.ParseResults<net.minecraft.class_2168> parse) {
        this.field_9811 = parse;
    }

    /**
     * execute(manager, source, queue, depth, maxChainLength, tracer): the command, run now with this source at
     * this depth; the tracer hears it as 1.20.3's execution traces a function line (the command, its result,
     * an error)
     */
    @Override
    public void method_9198(net.minecraft.class_2991 manager, net.minecraft.class_2168 source,
                            java.util.Deque<class_2991$class_2992> queue, int depth, int maxChainLength,
                            class_2991$class_6346 tracer) {
        top.dext.centurybridge.step.v1_20_3.rt.CbFunctions.runParsed(source, field_9811, tracer, depth);
    }

    /** execute(manager, source): the command, run now with this source; its result */
    public int method_36318(net.minecraft.class_2991 manager, net.minecraft.class_2168 source) {
        return top.dext.centurybridge.step.v1_20_3.rt.CbFunctions.runParsed(source, field_9811, null, 0);
    }

    @Override
    public String toString() {
        return field_9811.getReader().getString();
    }
}
