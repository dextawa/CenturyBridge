package cb.datatest;

import net.minecraft.class_1295;
import net.minecraft.class_1299;
import net.minecraft.class_1937;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;

/** A subclass of a vanilla entity whose defineSynchedData() chains to vanilla's before defining its own. */
public class ProbeCloud extends class_1295 {

    static final class_2940<Integer> MARK = class_2945.method_12791(ProbeCloud.class, class_2943.field_13327);

    public ProbeCloud(class_1299<? extends ProbeCloud> type, class_1937 level) {
        super(type, level);
    }

    @Override
    protected void method_5693() {
        super.method_5693();
        this.method_5841().method_12784(MARK, 5);
    }
}
