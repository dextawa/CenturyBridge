package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's Realms32bitWarningScreen (classRenames), gone in 1.20.5 with 32-bit support: the warning that
 * Realms ends for 32-bit Java, one Done button that remembers "don't show again" (Options.skipRealms32bitWarning,
 * which the layer keeps in options.txt) and returns to the previous screen. 1.20.4's class on facade/WarningScreen.
 */
@Environment(EnvType.CLIENT)
public class Realms32bitWarningScreen extends WarningScreen {

    public static final net.minecraft.class_2561 field_37212 =
        net.minecraft.class_2561.method_43471("title.32bit.deprecation.realms.header").method_27692(net.minecraft.class_124.field_1067);
    public static final net.minecraft.class_2561 field_37213 = net.minecraft.class_2561.method_43471("title.32bit.deprecation.realms");
    public static final net.minecraft.class_2561 field_37214 = net.minecraft.class_2561.method_43471("title.32bit.deprecation.realms.check");
    public static final net.minecraft.class_2561 field_37215 = field_37212.method_27661().method_27693("\n").method_10852(field_37213);

    /** previous */
    public final net.minecraft.class_437 field_39353;

    public Realms32bitWarningScreen(net.minecraft.class_437 previous) {
        super(field_37212, field_37213, field_37214, field_37215);
        this.field_39353 = previous;
    }

    @Override
    public void method_41160(int yOffset) {
        this.method_37063(net.minecraft.class_4185.method_46430(net.minecraft.class_5244.field_24334, b -> {
            if (this.field_37217.method_20372()) {
                ((top.dext.centurybridge.step.v1_20_5.rt.CbClientRealms.Options32Bit) this.field_22787.field_1690).cb$setSkipRealms32bitWarning(true);
                this.field_22787.field_1690.method_1640();
            }
            this.field_22787.method_1507(this.field_39353);
        }).method_46434(this.field_22789 / 2 - 75, 100 + yOffset, 150, 20).method_46431());
    }
}
