package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_350;
import net.minecraft.class_4280;
import top.dext.centurybridge.step.v1_21_9.rt.CbSelectionLists;

/**
 * 1.21.8's Realms RowButton (class_4371), which 1.21.9 removed with the Realms list rows that used it. 1.21.8's bodies
 * (javap -c): a button area (x, y relative to a row, width, height) that draws itself with whether the mouse is over
 * it, and two statics a list's row used to draw its buttons (those whose right edge fits the row width) and to press
 * the one under a click (selecting the row first, through the list's setSelectedIndex: 1.21.9 dropped it, the layer
 * restores it, CbSelectionListRestoredMixin).
 */
@Environment(EnvType.CLIENT)
public abstract class RowButton {
    public final int field_19690;
    public final int field_19691;
    public final int field_19692;
    public final int field_19693;

    public RowButton(int width, int height, int xOffset, int yOffset) {
        this.field_19690 = width;
        this.field_19691 = height;
        this.field_19692 = xOffset;
        this.field_19693 = yOffset;
    }

    public void method_21111(class_332 graphics, int x, int y, int mouseX, int mouseY) {
        int left = x + this.field_19692;
        int top = y + this.field_19693;
        boolean hovered = mouseX >= left && mouseX <= left + this.field_19690 && mouseY >= top && mouseY <= top + this.field_19691;
        this.method_21112(graphics, left, top, hovered);
    }

    public abstract void method_21112(class_332 graphics, int x, int y, boolean hovered);

    public int method_21109() {
        return this.field_19692 + this.field_19690;
    }

    public int method_21115() {
        return this.field_19693 + this.field_19691;
    }

    public abstract void method_21110(int index);

    public static void method_21113(class_332 graphics, List<RowButton> buttons, class_350<?> list, int x, int y, int mouseX, int mouseY) {
        for (RowButton button : buttons) {
            if (list.method_25322() > button.method_21109()) {
                button.method_21111(graphics, x, y, mouseX, mouseY);
            }
        }
    }

    public static void method_21114(class_350<?> list, class_4280.class_4281<?> entry, List<RowButton> buttons, int button,
                                    double mouseX, double mouseY) {
        int index = list.method_25396().indexOf(entry);
        if (index > -1) {
            ((CbSelectionLists.LegacySelectedIndex) list).method_64847(index);
            int left = list.method_25342();
            int top = list.method_25337(index);
            int x = (int) (mouseX - left);
            int y = (int) (mouseY - top);
            for (RowButton rowButton : buttons) {
                if (x >= rowButton.field_19692 && x <= rowButton.method_21109() && y >= rowButton.field_19693 && y <= rowButton.method_21115()) {
                    rowButton.method_21110(index);
                }
            }
        }
    }
}
