package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * Arrow: 1.20.4's setEffectsFromItem(stack): a tipped arrow's potion, effects and color (1.20.5: the stack's
 * potion contents, which carry all three), none for a plain arrow, anything else left as it is.
 */
@Mixin(net.minecraft.class_1667.class)
public abstract class CbEntityArrowMixin implements CbRoutes.ArrowEffectsFromItem {

    @Shadow
    private void method_57316(net.minecraft.class_1844 contents) {
        throw new AssertionError();
    }

    /** was setEffectsFromItem(stack) */
    @Override
    @CbVanillaReads("CbRouteArrowMixin: the arrow constructors that take the ammo stack apply it through this, from an arrow without effects, when a converted mod mixes it")
    public void method_7459(net.minecraft.class_1799 stack) {
        if (stack.method_31574(net.minecraft.class_1802.field_8087)) {
            this.method_57316(stack.method_57825(net.minecraft.class_9334.field_49651, net.minecraft.class_1844.field_49274));
        } else if (stack.method_31574(net.minecraft.class_1802.field_8107)) {
            this.method_57316(net.minecraft.class_1844.field_49274);
        }
    }
}
