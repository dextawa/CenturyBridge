package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_332;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's AbstractSelectionList.Entry.renderBack(graphics, index, top, left, width, height, mouseX, mouseY, hovered,
 * delta): empty in 1.21.8, drawn by 1.21.8's renderItem before the selection box; 1.21.9 dropped it. Restored empty, so a
 * 1.21.8 override's super call links; a 1.21.8 override is drawn where 1.21.8 drew it (CbSelectionListSelectedRouteMixin,
 * at the head of 1.21.9's renderItem, for an entry whose class declares it).
 */
@Mixin(value = class_350.class_351.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSelectionEntryRestoredMixin {

    /** Entry.renderBack(..), 1.21.8's empty body */
    @CbVanillaReads("CbSelectionListSelectedRouteMixin: 1.21.9's renderItem calls it first for an entry whose class overrides it")
    public void method_49568(class_332 graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY,
                             boolean hovered, float delta) {
    }
}
