package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_5676;

/**
 * 1.21.10 CycleButton.Builder kept its initial value in a mutable field (withInitialValue(T), default: the first of the
 * default values list) and the "only the value" flag as a boolean; 1.21.11's Builder takes the initial value as a final
 * Supplier in its constructor (read once, in build, before indexOf) and a DisplayState (javap -c -p both). The
 * layer gives withInitialValue / displayOnlyValue(boolean) back on the Builder (mixin/CbCycleButtonBuilderRestoredMixin);
 * the old one-argument constructor and factories reach 1.21.11's with 1.21.10's default: the first default value, read
 * when the button is built, as 1.21.10's build read list.get(0).
 */
@Environment(EnvType.CLIENT)
public final class CbCycleButtons {
    private CbCycleButtons() {
    }

    /** implemented by CbCycleButtonBuilderRestoredMixin */
    public interface Builder {
        /** the initial value as 1.21.10 defaulted it: the first of the default values, when built */
        void cb$v1_21_11$defaultFirst();
    }

    /** 1.21.10 CycleButton.builder(Function): a builder with 1.21.10's default initial value */
    public static <T> class_5676.class_5677<T> method_32606(Function<T, class_2561> stringifier) {
        class_5676.class_5677<T> b = new class_5676.class_5677<>(stringifier, unset());
        ((Builder) (Object) b).cb$v1_21_11$defaultFirst();
        return b;
    }

    /** the old constructor's supplier until its post step (defaultFirst) puts 1.21.10's default in */
    public static <T> Supplier<T> unset() {
        return () -> {
            throw new IllegalStateException("CycleButton.Builder initial value not set");
        };
    }

    /** post step of a converted new Builder(Function) / super(Function) */
    public static void defaultFirst(class_5676.class_5677<?> builder, Function<?, ?> stringifier) {
        ((Builder) (Object) builder).cb$v1_21_11$defaultFirst();
    }

    /** 1.21.10's boolean builders started at BOOLEAN_OPTIONS.get(0), TRUE */
    public static boolean initialTrue() {
        return true;
    }
}
