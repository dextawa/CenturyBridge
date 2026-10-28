package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_1144;
import net.minecraft.class_310;
import net.minecraft.class_339;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_9.rt.CbInput;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * AbstractWidget.mouseClicked(x, y, button), 1.21.8's shape of 1.21.9's mouseClicked(MouseButtonEvent, doubleClick),
 * with 1.21.8's body (javap -c class_339 both versions: active and visible -- 1.21.9's isActive() --, a valid button,
 * isMouseOver(x, y) stored, then the down sound and onClick; 1.21.9's is the same with the event's values). A
 * converted mixin into 1.21.8's method (the climbed 1.21.4 layer's CbGuiScrollWidgetMixin @ModifyVariable on that stored
 * isMouseOver, capturing x, y, button; sruntime2 2026-10-27) lands here; while one does (CbMixedMembers), 1.21.9's
 * mouseClicked runs this body in its place with the event's x, y and button, and onClick gets the event and double-click
 * flag 1.21.9 was given (the route: CbWidgetClickRestoredHooksMixin, at the default priority). Unmixed, 1.21.9's mouseClicked runs untouched. Called directly by 1.21.8 code, onClick gets the
 * click built from (x, y, button) and the double-click flag 1.21.9's MouseHandler would pass (CbInput).
 * The body keeps 1.21.8's local order: isMouseOver is its first boolean store (STORE ordinal 0). isActive / isMouseOver
 * are called, not @Shadow-ed: their declarations are NarratableEntry's / GuiEventListener's, where the wall table keys
 * them, and a shadow of them is stripped when the layer climbs past 26.1.
 */
@Mixin(value = class_339.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWidgetClickRestoredMixin implements CbPrioritySplitClient.WidgetClick {

    @Unique
    private class_11909 cb$routedEvent;

    @Unique
    private boolean cb$routedDoubleClick;

    @Shadow
    protected abstract boolean method_25351(class_11910 button);

    @Shadow
    public abstract void method_25354(class_1144 soundManager);

    @Shadow
    public abstract void method_25348(class_11909 event, boolean doubleClick);

    /** AbstractWidget.mouseClicked(x, y, button) */
    @CbVanillaReads("CbWidgetClickRestoredMixin: 1.21.9's mouseClicked runs it in its place while it is mixed")
    public boolean method_25402(double x, double y, int button) {
        if (!((class_339) (Object) this).method_37303()) {
            return false;
        }
        class_11909 event = this.cb$routedEvent != null ? this.cb$routedEvent : CbInput.cbClick(x, y, button);
        if (this.method_25351(event.comp_4800())) {
            boolean over = ((class_339) (Object) this).method_25405(x, y);
            if (over) {
                this.method_25354(class_310.method_1551().method_1483());
                this.method_25348(event, this.cb$routedEvent != null ? this.cb$routedDoubleClick : CbInput.cbDoubleClick(button));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cb$v1_21_9$routeClick(class_11909 event, boolean doubleClick) {
        class_11909 outerEvent = this.cb$routedEvent;
        boolean outerDoubleClick = this.cb$routedDoubleClick;
        this.cb$routedEvent = event;
        this.cb$routedDoubleClick = doubleClick;
        try {
            return this.method_25402(event.comp_4798(), event.comp_4799(), event.comp_4800().comp_4801());
        } finally {
            this.cb$routedEvent = outerEvent;
            this.cb$routedDoubleClick = outerDoubleClick;
        }
    }
}
