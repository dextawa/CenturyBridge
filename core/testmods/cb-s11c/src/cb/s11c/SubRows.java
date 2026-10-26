package cb.s11c;

import java.util.List;
import net.minecraft.class_1928;
import net.minecraft.class_2561;
import net.minecraft.class_5235;
import net.minecraft.class_5481;

/** check 5: the mod's own rows, built with 1.21.10's super(screen, label, tooltip, narration, value) */
public final class SubRows {
    private SubRows() {
    }

    /** AbstractSelectionList.addEntry (protected, its parameter type protected too), called reflectively */
    public static void add(Object list, Object entry) {
        try {
            for (Class<?> c = list.getClass(); c != null; c = c.getSuperclass()) {
                for (java.lang.reflect.Method m : c.getDeclaredMethods()) {
                    if (m.getName().equals("method_25321") && m.getParameterCount() == 1) {
                        m.setAccessible(true);
                        m.invoke(list, entry);
                        return;
                    }
                }
            }
            throw new IllegalStateException("no addEntry(Entry) on " + list.getClass());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static class SubBool extends class_5235.class_5236 {
        public SubBool(class_5235 screen, class_2561 label, List<class_5481> tooltip, String narration, class_1928.class_4310 value) {
            screen.super(label, tooltip, narration, value);
        }
    }

    public static class SubInt extends class_5235.class_5239 {
        public SubInt(class_5235 screen, class_2561 label, List<class_5481> tooltip, String narration, class_1928.class_4312 value) {
            screen.super(label, tooltip, narration, value);
        }
    }
}
