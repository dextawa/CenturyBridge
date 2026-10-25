package cbm;

import net.minecraft.class_1060;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_3999;

/** a mod's own ParticleRenderType (1.21.3 interface) */
public final class MyType implements class_3999 {
    public static final class_3999 INSTANCE = new MyType();
    public static class_3999 vanillaPick = class_3999.field_17828;

    @Override
    public class_287 method_18130(class_289 tess, class_1060 tex) {
        return null;
    }

    public static boolean same(class_3999 a) {
        return a == INSTANCE;
    }
}
