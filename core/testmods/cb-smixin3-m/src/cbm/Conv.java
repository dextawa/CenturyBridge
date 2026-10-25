package cbm;

import net.minecraft.class_3999;

/** stands for the layer's converters in this test (bodies are not what is tested) */
public final class Conv {
    public static class_3999 toW(Facade f) {
        return f instanceof class_3999 c ? c : null;
    }

    public static Facade toV(class_3999 x) {
        return x instanceof Facade f ? f : null;
    }
}
