package cbm;

import net.minecraft.class_3999;
import net.minecraft.class_638;
import net.minecraft.class_703;

public abstract class MyParticle extends class_703 {
    protected MyParticle(class_638 level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Override
    public class_3999 method_18122() {
        return MyType.same(MyType.vanillaPick) ? class_3999.field_17827 : MyType.INSTANCE;
    }

    public static class_3999 typeOf(class_703 p) {
        return p.method_18122();
    }
}
