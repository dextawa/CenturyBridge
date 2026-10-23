package top.dext.centurybridge.step.v1_21.facade;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_333;
import net.minecraft.class_4185;
import net.minecraft.class_4905;
import net.minecraft.class_5244;

/**
 * 1.20.6's RealmsLongConfirmationScreen (net/minecraft/class_4396): a Realms screen with a coloured type line
 * (warning / info) and two lines of text, answering yes / no (or only ok) to a callback; Escape answers no.
 * 1.21 replaced it with RealmsPopups and removed the class; a 1.20.6 mod's references are renamed here
 * (classRenames, s6fin-realms.json). 1.20.6's body, member by member, on 1.21's (unchanged) screen API.
 */
@Environment(EnvType.CLIENT)
public class class_4396 extends class_4905 {
    /** "mco.warning" */
    public static final class_2561 field_44906 = class_2561.method_43471("mco.warning");
    /** "mco.info" */
    public static final class_2561 field_44907 = class_2561.method_43471("mco.info");

    /** type */
    public final class_4396$class_4397 field_19895;
    /** line2 */
    public final class_2561 field_19896;
    /** line3 */
    public final class_2561 field_19897;
    /** callback */
    public final BooleanConsumer field_22697;
    /** yesNoQuestion */
    public final boolean field_19899;

    public class_4396(BooleanConsumer callback, class_4396$class_4397 type, class_2561 line2, class_2561 line3, boolean yesNoQuestion) {
        super(class_333.field_18967);
        this.field_22697 = callback;
        this.field_19895 = type;
        this.field_19896 = line2;
        this.field_19897 = line3;
        this.field_19899 = yesNoQuestion;
    }

    /** init: yes / no centred in row 8, or a single ok */
    @Override
    public void method_25426() {
        if (this.field_19899) {
            this.method_37063(class_4185.method_46430(class_5244.field_24336, b -> this.field_22697.accept(true))
                .method_46434(this.field_22789 / 2 - 105, method_25494(8), 100, 20).method_46431());
            this.method_37063(class_4185.method_46430(class_5244.field_24337, b -> this.field_22697.accept(false))
                .method_46434(this.field_22789 / 2 + 5, method_25494(8), 100, 20).method_46431());
        } else {
            this.method_37063(class_4185.method_46430(class_5244.field_44914, b -> this.field_22697.accept(true))
                .method_46434(this.field_22789 / 2 - 50, method_25494(8), 100, 20).method_46431());
        }
    }

    /** getNarrationMessage: the type, then both lines */
    @Override
    public class_2561 method_25435() {
        return class_5244.method_37110(this.field_19895.field_19906, this.field_19896, this.field_19897);
    }

    /** keyPressed: Escape (256) answers no */
    @Override
    public boolean method_25404(int key, int scan, int modifiers) {
        if (key == 256) {
            this.field_22697.accept(false);
            return true;
        }
        return super.method_25404(key, scan, modifiers);
    }

    /** render: the type line in its colour in row 2, the two lines in white in rows 4 and 6 */
    @Override
    public void method_25394(class_332 graphics, int mouseX, int mouseY, float delta) {
        super.method_25394(graphics, mouseX, mouseY, delta);
        graphics.method_27534(this.field_22793, this.field_19895.field_19906, this.field_22789 / 2, method_25494(2), this.field_19895.field_19905);
        graphics.method_27534(this.field_22793, this.field_19896, this.field_22789 / 2, method_25494(4), -1);
        graphics.method_27534(this.field_22793, this.field_19897, this.field_22789 / 2, method_25494(6), -1);
    }
}
