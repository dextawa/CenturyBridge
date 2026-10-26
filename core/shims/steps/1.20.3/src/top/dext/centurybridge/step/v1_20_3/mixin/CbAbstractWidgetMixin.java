package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * AbstractWidget's 1.20.2 contract on 1.20.3:
 * <ul>
 * <li>render(GuiGraphics, int, int, float) is final since 1.20.3. A 1.20.2 widget overriding it has its
 *     override renamed (overrideRenames) and marked with {@link CbGui.CbLegacyWidgetRender}; the final
 *     render hands the call to it, as vanilla called the override in 1.20.2. The widget's own
 *     super.render(..) comes back here and runs vanilla's body (the guard).</li>
 * <li>createTooltipPositioner() (protected in 1.20.2) is gone: 1.20.3 lets the Tooltip choose. It is
 *     given back with 1.20.2's choice, and a widget that overrides it (marked with
 *     {@link CbGui.CbLegacyTooltipPositioner}) has its tooltip placed by its own answer again.</li>
 * </ul>
 * The injectors into 1.20.3's render are in CbAbstractWidgetMixinHooks (default priority).
 */
@Mixin(value = net.minecraft.class_339.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAbstractWidgetMixin {

    /** was createTooltipPositioner() (method_47937): below or above the widget when focused by keyboard, else by the mouse */
    protected net.minecraft.class_8000 method_47937() {
        return CbGui.cbTooltipPositioner((net.minecraft.class_339) (Object) this);
    }
}
