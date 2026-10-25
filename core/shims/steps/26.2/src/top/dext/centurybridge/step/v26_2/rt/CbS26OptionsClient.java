package top.dext.centurybridge.step.v26_2.rt;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.OptionInstance;

/**
 * 26.1.2 OptionInstance took the value-changed callback as a Consumer (accept(value)); 26.2 takes an
 * OptionInstance.ValueUpdateListener (valueChanged(value)), called where 26.1.2 called accept (javap of both).
 */
@Environment(EnvType.CLIENT)
public final class CbS26OptionsClient {
    private CbS26OptionsClient() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static OptionInstance.ValueUpdateListener toListener(Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        if (consumer instanceof OptionInstance.ValueUpdateListener l) {
            return l;
        }
        return consumer::accept;
    }
}
