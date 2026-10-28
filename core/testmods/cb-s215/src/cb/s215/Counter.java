package cb.s215;

import net.minecraft.class_2487;
import net.minecraft.class_7225;

/** a 1.21.4 mod's own SavedData: one counter, saved as {"count": n} (converted: extends the layer's facade SavedData) */
public final class Counter extends net.minecraft.class_18 {
    public int count;

    public Counter() {
    }

    public static Counter load(class_2487 tag, class_7225.class_7874 registries) {
        Counter c = new Counter();
        c.count = tag.method_10550("count");
        return c;
    }

    @Override
    public class_2487 method_75(class_2487 tag, class_7225.class_7874 registries) {
        tag.method_10569("count", this.count);
        return tag;
    }
}
