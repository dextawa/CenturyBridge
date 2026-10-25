package cb.s11;

import net.minecraft.class_1928;
import net.minecraft.class_7699;

/** a 1.21.10 mod's GameRules subclass overriding getBoolean: block drops always off */
public class MyRules extends class_1928 {
    public MyRules(class_7699 features) {
        super(features);
    }

    @Override
    public boolean method_8355(class_1928.class_4313<class_1928.class_4310> key) {
        return key == class_1928.field_19392 ? false : super.method_8355(key);
    }
}
