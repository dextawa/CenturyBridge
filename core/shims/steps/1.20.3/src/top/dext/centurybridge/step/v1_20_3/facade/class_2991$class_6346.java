package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2's net/minecraft/class_2991$class_6346 (ServerFunctionManager.TraceCallbacks), which
 * 1.20.3 moved to net/minecraft/class_6346 (commands.execution.TraceCallbacks) with onError losing its
 * depth and close() added. The 1.20.2 interface whole; {@link top.dext.centurybridge.step.v1_20_3.rt.CbFunctions}
 * hands a 1.20.2 tracer to 1.20.3's executions through an adapter.
 */
public interface class_2991$class_6346 {

    /** onCommand(depth, command) */
    void method_36349(int depth, String command);

    /** onReturn(depth, command, result) */
    void method_36350(int depth, String command, int result);

    /** onError(depth, message) */
    void method_36352(int depth, String message);

    /** onCall(depth, function, size) */
    void method_36351(int depth, net.minecraft.class_2960 function, int size);
}
