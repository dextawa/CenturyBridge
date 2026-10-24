package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_3218;
import net.minecraft.class_4514;
import net.minecraft.class_4517;
import net.minecraft.class_4529;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinGameTests;

/**
 * GameTestBatch: 1.20.4's getTestFunctions() / runBeforeBatchFunction(level) / runAfterBatchFunction(level),
 * which a subclass may override (1.20.5's record answers its components). A batch a 1.20.4 mod built holds
 * TestFunctions (rt/CbFinGameTests); one 1.20.5 built holds GameTestInfos, whose functions it answers.
 */
@Mixin(class_4514.class)
public abstract class CbFinGameTestBatchMixin implements CbFinGameTests.LegacyBatch {

    @Shadow
    public abstract Collection<class_4517> comp_2210();

    @Shadow
    public abstract Consumer<class_3218> comp_2211();

    @Shadow
    public abstract Consumer<class_3218> comp_2212();

    /** was getTestFunctions() */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collection<class_4529> method_22154() {
        Collection held = this.comp_2210();
        boolean functions = true;
        for (Object element : held) {
            if (!(element instanceof class_4529)) {
                functions = false;
                break;
            }
        }
        if (functions) {
            return (Collection<class_4529>) held;
        }
        List<class_4529> out = new ArrayList<>(held.size());
        for (Object element : held) {
            out.add(element instanceof class_4517 info ? info.method_29403() : (class_4529) element);
        }
        return out;
    }

    /** was runBeforeBatchFunction(level) */
    @Override
    public void method_22153(class_3218 level) {
        this.comp_2211().accept(level);
    }

    /** was runAfterBatchFunction(level) */
    @Override
    public void method_32237(class_3218 level) {
        this.comp_2212().accept(level);
    }
}
