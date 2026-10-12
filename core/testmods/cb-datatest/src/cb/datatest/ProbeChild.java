package cb.datatest;

import net.minecraft.class_1299;
import net.minecraft.class_1937;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;

/** A mod subclass whose defineSynchedData() chains to its mod parent's before defining its own. */
public class ProbeChild extends Probe {

    static final class_2940<String> EXTRA = class_2945.method_12791(ProbeChild.class, class_2943.field_13326);

    public ProbeChild(class_1299<? extends ProbeChild> type, class_1937 level) {
        super(type, level);
    }

    @Override
    protected void method_5693() {
        super.method_5693();
        this.field_6011.method_12784(EXTRA, "child");
    }
}
