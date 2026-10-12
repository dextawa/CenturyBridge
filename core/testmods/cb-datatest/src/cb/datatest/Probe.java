package cb.datatest;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1937;
import net.minecraft.class_2487;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;

/** Tracked data the 1.20.4 ways: in defineSynchedData(), and in the constructor after super(). */
public class Probe extends class_1297 {

    static final class_2940<Integer> COUNTER = class_2945.method_12791(Probe.class, class_2943.field_13327);
    static final class_2940<Integer> LATE = class_2945.method_12791(Probe.class, class_2943.field_13327);
    /** vanilla's own (Entity.DATA_SHARED_FLAGS_ID, protected), for the test to read */
    static final class_2940<Byte> SHARED_FLAGS_FOR_TEST = field_5990;

    public Probe(class_1299<? extends Probe> type, class_1937 level) {
        super(type, level);
        this.field_6011.method_12784(LATE, 7);
    }

    @Override
    protected void method_5693() {
        this.field_6011.method_12784(COUNTER, 41);
    }

    @Override
    protected void method_5749(class_2487 tag) {
    }

    @Override
    protected void method_5652(class_2487 tag) {
    }
}
