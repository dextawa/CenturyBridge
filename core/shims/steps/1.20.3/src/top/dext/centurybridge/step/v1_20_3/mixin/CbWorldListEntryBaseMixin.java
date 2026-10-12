package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * WorldSelectionList.Entry.isSelectable() (method_43465), gone in 1.20.3. 1.20.2 declared it abstract and
 * asked it when an entry was selected (to enable the play buttons); 1.20.2's two entries answered it --
 * a world entry when its world is not disabled, the loading header never (see the mixins on them). An
 * entry that answers nothing else is not selectable.
 */
@Mixin(net.minecraft.class_528.class_7414.class)
public abstract class CbWorldListEntryBaseMixin {

    public boolean method_43465() {
        return false;
    }
}
