package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * SelectWorldScreen.updateButtonStatus(boolean active, boolean deleteActive), 1.20.2's body: the play,
 * edit and re-create buttons follow "active", the delete button "deleteActive". 1.20.3 takes the selected
 * world's summary instead (and words the play button by it); a 1.20.2 subclass overriding the old shape
 * is reached from it through {@link CbGui#cbUpdateButtonStatus} (overrideHooks).
 */
@Mixin(net.minecraft.class_526.class)
public abstract class CbSelectWorldScreenMixin implements CbGui.CbLegacyButtonStatus {

    /** selectButton */
    @Shadow
    private net.minecraft.class_4185 field_3224;

    /** renameButton */
    @Shadow
    private net.minecraft.class_4185 field_3215;

    /** copyButton */
    @Shadow
    private net.minecraft.class_4185 field_3216;

    /** deleteButton */
    @Shadow
    private net.minecraft.class_4185 field_3219;

    @Override
    public void method_19940(boolean active, boolean deleteActive) {
        field_3224.field_22763 = active;
        field_3215.field_22763 = active;
        field_3216.field_22763 = active;
        field_3219.field_22763 = deleteActive;
    }
}
