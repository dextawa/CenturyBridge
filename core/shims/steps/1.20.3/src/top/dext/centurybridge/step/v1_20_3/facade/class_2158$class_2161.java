package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2's net/minecraft/class_2158$class_2161 (CommandFunction.Entry), gone in 1.20.3 (a
 * function's entries are UnboundEntryActions run by the execution context). The 1.20.2 interface: an entry
 * runs with the function manager, the source, the manager's queue of commands to run next, its depth and
 * the chain limit, and an optional tracer.
 */
public interface class_2158$class_2161 {

    /** execute(manager, source, queue, depth, maxChainLength, tracer) */
    void method_9198(net.minecraft.class_2991 manager, net.minecraft.class_2168 source,
                     java.util.Deque<class_2991$class_2992> queue, int depth, int maxChainLength,
                     class_2991$class_6346 tracer) throws com.mojang.brigadier.exceptions.CommandSyntaxException;
}
