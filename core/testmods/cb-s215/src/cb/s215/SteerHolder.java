package cb.s215;

import java.util.List;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;
import net.minecraft.class_4980;
import net.minecraft.class_9221;

/** A 1.21.4 synched-data holder with a boost time and a saddle accessor, as a mod's steerable entity defines them */
public final class SteerHolder implements class_9221 {
    public static final class_2940<Integer> BOOST = class_2945.method_12791(SteerHolder.class, class_2943.field_13327);
    public static final class_2940<Boolean> SADDLE = class_2945.method_12791(SteerHolder.class, class_2943.field_13323);
    public final class_2945 data = new class_2945.class_9222(this).method_56912(BOOST, 0).method_56912(SADDLE, false).method_56911();
    public int synced;

    @Override
    public void method_5674(class_2940<?> accessor) {
        synced++;
    }

    @Override
    public void method_48850(List<class_2945.class_7834<?>> values) {
    }

    /** a mod's own ItemBasedSteering subclass (1.21.4 constructor through super) */
    public static final class Steering extends class_4980 {
        public int saves;

        public Steering(class_2945 data, class_2940<Integer> boost, class_2940<Boolean> saddle) {
            super(data, boost, saddle);
        }

        @Override
        public void method_26309(net.minecraft.class_2487 tag) {
            saves++;
            super.method_26309(tag);
        }
    }
}
