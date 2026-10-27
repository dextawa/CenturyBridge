package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's AbstractSelectionList.removeEntry(entry) and removeEntryFromTop(entry) answered whether the entry was in
 * the list; 1.21.9's answer nothing (javap: the same removal -- the first equal entry, the selection cleared when it was
 * the selected one -- plus 1.21.9's repositionEntries, and removeEntryFromTop's keeping of the distance to the bottom).
 * Restored in 1.21.8's shape beside 1.21.9's (same name, another return type): 1.21.9's method does the removal, the
 * answer is whether the list held the entry, which is exactly when List.remove removed one. A 1.21.8 override of either
 * is reached from 1.21.9's calls through the step's override hooks; its super call lands here.
 */
@Mixin(value = class_350.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSelectionListRemoveRestoredMixin<E extends class_350.class_351<E>> {

    /** AbstractSelectionList.removeEntry(entry), 1.21.8's shape */
    protected boolean method_25330(E entry) {
        boolean held = ((class_350<E>) (Object) this).method_25396().contains(entry);
        ((CbSelectionListRemoveInvoker) this).cb$removeEntry(entry);
        return held;
    }

    /** AbstractSelectionList.removeEntryFromTop(entry), 1.21.8's shape */
    protected boolean method_44650(E entry) {
        boolean held = ((class_350<E>) (Object) this).method_25396().contains(entry);
        ((CbSelectionListRemoveInvoker) this).cb$removeEntryFromTop(entry);
        return held;
    }
}
