package cb.s9r;

import java.util.List;
import net.minecraft.class_332;
import net.minecraft.class_4371;

/** 1.21.8 Realms RowButton: a row button of the climbed list pressed through RowButton.rowButtonMouseClicked */
public final class RowCheck {
    private RowCheck() {
    }

    static final class TestRowButton extends class_4371 {
        int clicked = -1;

        TestRowButton() {
            super(20, 10, 5, 2);
        }

        @Override
        protected void method_21112(class_332 graphics, int x, int y, boolean hovered) {
        }

        @Override
        public void method_21110(int index) {
            this.clicked = index;
        }
    }

    /** on the render thread, the LegacyScreen open: "clicked selected right bottom" */
    public static String press(Object screen) {
        TestList list = ((LegacyScreen) screen).list;
        TestEntry entry = list.method_25396().get(1);
        TestRowButton button = new TestRowButton();
        double mouseX = list.method_25342() + 10;
        double mouseY = list.method_25337(1) + 5;
        class_4371.method_21114(list, entry, List.of(button), 0, mouseX, mouseY);
        return button.clicked + " " + (list.method_25334() == entry) + " " + button.method_21109() + " " + button.method_21115();
    }
}
