package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2's net/minecraft/class_2158$class_2162 (CommandFunction.FunctionEntry), gone in 1.20.3:
 * a call of another function. 1.20.2's body -- the called function's entries go to the front of the queue,
 * one level deeper, as many as the chain limit leaves room for, and the tracer hears the call (size -1 when
 * the function is missing). 1.20.2 held the function in a CacheableFunction made from it, already resolved:
 * the function itself here.
 */
public class class_2158$class_2162 implements class_2158$class_2161 {

    /** the function called */
    private final net.minecraft.class_2158<?> function;

    public class_2158$class_2162(net.minecraft.class_2158<?> function) {
        this.function = function;
    }

    @Override
    public void method_9198(net.minecraft.class_2991 manager, net.minecraft.class_2168 source,
                            java.util.Deque<class_2991$class_2992> queue, int depth, int maxChainLength,
                            class_2991$class_6346 tracer) {
        if (function == null) {
            if (tracer != null) {
                tracer.method_36351(depth, null, -1);
            }
            return;
        }
        class_2158$class_2161[] entries = top.dext.centurybridge.step.v1_20_3.rt.CbFunctions.method_9193(function);
        if (tracer != null) {
            tracer.method_36351(depth, function.comp_1994(), entries.length);
        }
        int room = maxChainLength - queue.size();
        int n = Math.min(entries.length, room);
        for (int i = n - 1; i >= 0; i--) {
            queue.addFirst(new class_2991$class_2992(source, depth + 1, entries[i]));
        }
    }

    /** the function this entry calls */
    public net.minecraft.class_2158<?> cb$function() {
        return function;
    }

    @Override
    public String toString() {
        return "function " + (function == null ? null : function.comp_1994());
    }
}
