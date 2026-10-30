package top.dext.centurybridge.step.v1_21_11.mixin;

import java.util.function.Supplier;
import net.minecraft.class_5676;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbCycleButtons;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 CycleButton.Builder.withInitialValue(T) and displayOnlyValue(boolean), given back on 1.21.11's Builder
 * (rt/CbCycleButtons). 1.21.11's build reads the initial value from its supplier once (then indexOf in the values, as
 * 1.21.10's withInitialValue looked it up): setting a constant supplier is 1.21.10's field write. displayOnlyValue(true)
 * is 1.21.11's DisplayState VALUE, false NAME_AND_VALUE (1.21.10's build: value alone, or name and value). 1.21.11 never
 * calls either.
 */
@Mixin(value = class_5676.class_5677.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCycleButtonBuilderRestoredMixin<T> implements CbCycleButtons.Builder {
    @Shadow
    @Final
    @Mutable
    private Supplier<T> field_63505;

    @Shadow
    private class_5676.class_5680<T> field_27977;

    @Shadow
    public abstract class_5676.class_5677<T> method_76614(class_5676.class_12346 state);

    /** CycleButton.Builder.withInitialValue(T) */
    @SuppressWarnings("unchecked")
    public class_5676.class_5677<T> method_32619(T value) {
        this.field_63505 = () -> value;
        return (class_5676.class_5677<T>) (Object) this;
    }

    /** CycleButton.Builder.displayOnlyValue(boolean) */
    public class_5676.class_5677<T> method_71805(boolean onlyValue) {
        return this.method_76614(onlyValue ? class_5676.class_12346.field_64540 : class_5676.class_12346.field_64539);
    }

    @Override
    public void cb$v1_21_11$defaultFirst() {
        this.field_63505 = () -> this.field_27977.method_32629().get(0);
    }
}
