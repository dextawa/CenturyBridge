package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_10712;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2561;

/**
 * 1.21.4's Item.appendHoverText(stack, context, List&lt;Component&gt; lines, flag) is appendHoverText(stack, context,
 * TooltipDisplay, Consumer&lt;Component&gt;, flag) since 1.21.5 (ItemStack.addDetailsToTooltip calls it in the place
 * 1.21.4's getTooltipLines called the old one: after the name, before the components' lines).
 * <ul>
 * <li>A 1.21.4 override gets the new shape (overrideHooks, bridge {@link #hoverText}): the old body fills a list, which
 *     goes to vanilla's consumer in order. The display vanilla passed is kept for the old body's super call.</li>
 * <li>Its super.appendHoverText(stack, context, lines, flag) (superBridge {@link #superHoverText}) asks super's 1.21.5
 *     shape with the display vanilla passed (TooltipDisplay.DEFAULT when the old body was entered directly), adding to
 *     the caller's list.</li>
 * <li>A 1.21.4 call of the old shape reaches the member restored on Item (CbItemTooltipRestoredMixin), which asks the
 *     item's 1.21.5 shape the same way.</li>
 * </ul>
 * 1.21.4 skipped the item's lines under HIDE_ADDITIONAL_TOOLTIP; 1.21.5 removed that component (its lines are hidden
 * per component through TooltipDisplay, the item's own lines never), so nothing stands for the test.
 */
public final class CbItemTooltip {

    private CbItemTooltip() {
    }

    /** the 1.21.4 shape a converted Item subclass overrides (adapter of the override hook) */
    public interface LegacyHoverText {
        void method_7851(class_1799 stack, class_1792.class_9635 context, List<class_2561> lines, class_1836 flag);
    }

    /** super's 1.21.5 shape, reached from such a subclass's super call */
    public interface SuperHoverText {
        void cb$super$method_67187(class_1799 stack, class_1792.class_9635 context, class_10712 display, Consumer<class_2561> lines,
                                   class_1836 flag);
    }

    /** 1.21.5's appendHoverText of a 1.21.4 override */
    public static void hoverText(LegacyHoverText self, class_1799 stack, class_1792.class_9635 context, class_10712 display,
                                 Consumer<class_2561> out, class_1836 flag) {
        List<class_2561> lines = new ArrayList<>();
        Guard.cbPush(self, new Object[] {display});
        try {
            self.method_7851(stack, context, lines, flag);
        } finally {
            Guard.cbPop();
        }
        lines.forEach(out);
    }

    /** a 1.21.4 override's super.appendHoverText(stack, context, lines, flag) */
    public static void superHoverText(SuperHoverText self, class_1799 stack, class_1792.class_9635 context, List<class_2561> lines,
                                      class_1836 flag) {
        class_10712 display = (class_10712) Guard.cbArgOrL(self, 0, class_10712.field_56318);
        self.cb$super$method_67187(stack, context, display, lines::add, flag);
    }

    /** the restored Item.appendHoverText's body: the item's 1.21.5 shape, with the display a hooked call carries */
    public static void restored(class_1792 item, class_1799 stack, class_1792.class_9635 context, List<class_2561> lines, class_1836 flag) {
        class_10712 display = (class_10712) Guard.cbArgOrL(item, 0, class_10712.field_56318);
        item.method_67187(stack, context, display, lines::add, flag);
    }
}
