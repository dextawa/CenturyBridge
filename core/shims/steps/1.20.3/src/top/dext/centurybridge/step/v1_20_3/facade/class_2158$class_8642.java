package top.dext.centurybridge.step.v1_20_3.facade;

import java.util.List;

/**
 * Facade for 1.20.2's net/minecraft/class_2158$class_8642 (CommandFunction.MacroEntry), gone in 1.20.3: a
 * macro line, cut into its text segments and the names of its $(variables). 1.20.2's body: substitute(values)
 * joins the segments with the values; the entry itself cannot run (1.20.2's macro function instantiated its
 * lines before running them).
 */
public class class_2158$class_8642 implements class_2158$class_2161 {

    /** segments */
    public final List<String> field_45152;
    /** parameters */
    public final List<String> field_45153;

    public class_2158$class_8642(List<String> segments, List<String> parameters) {
        this.field_45152 = segments;
        this.field_45153 = parameters;
    }

    /** getParameters() */
    public List<String> method_52598() {
        return field_45153;
    }

    /** substitute(values): segment, value, segment, value, ..., the trailing segment */
    public String method_52599(List<String> values) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < field_45153.size(); i++) {
            out.append(field_45152.get(i)).append(values.get(i));
        }
        if (field_45152.size() > field_45153.size()) {
            out.append(field_45152.get(field_45152.size() - 1));
        }
        return out.toString();
    }

    @Override
    public void method_9198(net.minecraft.class_2991 manager, net.minecraft.class_2168 source,
                            java.util.Deque<class_2991$class_2992> queue, int depth, int maxChainLength,
                            class_2991$class_6346 tracer) {
        throw new IllegalStateException("Tried to execute an uninstantiated macro");
    }
}
