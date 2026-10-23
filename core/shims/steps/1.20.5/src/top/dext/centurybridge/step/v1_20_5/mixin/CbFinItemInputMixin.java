package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinCommands;

/**
 * ItemInput: 1.20.4's ItemInput was a Predicate&lt;ItemStack&gt; testing the item and its NBT (contained in the
 * stack's); 1.20.5's holds components and tests nothing. The predicate and test(stack) back, over the components'
 * 1.20.4 NBT (rt/CbFinCommands).
 */
@Mixin(net.minecraft.class_2290.class)
public abstract class CbFinItemInputMixin implements Predicate<net.minecraft.class_1799> {

    @Shadow
    @Final
    private net.minecraft.class_6880<net.minecraft.class_1792> field_10796;

    @Shadow
    @Final
    private net.minecraft.class_9323 field_49570;

    /** was test(stack) (its intermediary name) */
    public boolean method_9783(net.minecraft.class_1799 stack) {
        return CbFinCommands.test(this.field_10796, this.field_49570, stack);
    }

    /** Predicate.test, 1.20.4's ItemInput being one */
    @Override
    public boolean test(net.minecraft.class_1799 stack) {
        return this.method_9783(stack);
    }
}
