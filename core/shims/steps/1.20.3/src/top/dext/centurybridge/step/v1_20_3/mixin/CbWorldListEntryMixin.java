package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/** WorldSelectionList.WorldListEntry.isSelectable() (method_43465), 1.20.2's body: its world is not disabled */
@Mixin(value = net.minecraft.class_528.class_4272.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldListEntryMixin {

    /** summary */
    @Shadow
    @Final
    net.minecraft.class_34 field_19138;

    public boolean method_43465() {
        return !field_19138.method_33784();
    }
}
