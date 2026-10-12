package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2's net/minecraft/class_2991$class_2992 (ServerFunctionManager.QueuedCommand), gone in
 * 1.20.3: an entry queued with its source and depth. 1.20.2's body.
 */
public class class_2991$class_2992 {

    /** source */
    public final net.minecraft.class_2168 field_13424;
    /** depth */
    public final int field_33549;
    /** entry */
    public final class_2158$class_2161 field_13425;

    public class_2991$class_2992(net.minecraft.class_2168 source, int depth, class_2158$class_2161 entry) {
        this.field_13424 = source;
        this.field_33549 = depth;
        this.field_13425 = entry;
    }

    /**
     * execute(manager, queue, maxChainLength, tracer): the entry runs; a failure is reported to the tracer at
     * the entry's depth (the raw message of a syntax exception, else the exception's message) and the queue
     * goes on
     */
    public void method_12914(net.minecraft.class_2991 manager, java.util.Deque<class_2991$class_2992> queue, int maxChainLength,
                             class_2991$class_6346 tracer) {
        try {
            field_13425.method_9198(manager, field_13424, queue, field_33549, maxChainLength, tracer);
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            if (tracer != null) {
                tracer.method_36352(field_33549, e.getRawMessage().getString());
            }
        } catch (Exception e) {
            if (tracer != null) {
                tracer.method_36352(field_33549, e.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return field_13425.toString();
    }
}
