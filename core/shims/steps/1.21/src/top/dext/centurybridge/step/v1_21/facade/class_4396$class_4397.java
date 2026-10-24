package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;

/**
 * 1.20.6's RealmsLongConfirmationScreen.Type (net/minecraft/class_4396$class_4397): WARNING ("mco.warning", red
 * -65536) and INFO ("mco.info", 8226750), the type line's text and colour. Renamed here with its screen. The
 * constants keep 1.20.6's names (name(), valueOf and the order are what a 1.20.6 mod sees); the fields under
 * their intermediary names are what its bytecode reads.
 */
@Environment(EnvType.CLIENT)
public enum class_4396$class_4397 {
    WARNING(class_4396.field_44906, -65536),
    INFO(class_4396.field_44907, 8226750);

    public static final class_4396$class_4397 field_19903 = WARNING;
    public static final class_4396$class_4397 field_19904 = INFO;

    /** colorCode */
    public final int field_19905;
    /** text */
    public final class_2561 field_19906;

    class_4396$class_4397(class_2561 text, int color) {
        this.field_19906 = text;
        this.field_19905 = color;
    }
}
