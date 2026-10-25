package cb.clienttest;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_6382;

/**
 * s9 client check 1 as 1.20.1 has it: a mod screen whose input overrides record their arguments (and the
 * Screen modifier helpers inside keyPressed) and call super, a focused vanilla EditBox, a mod widget with an
 * onClick override and a vanilla button. Real X input comes from boot_client_remote.sh (client_input.py),
 * which plays the sequence the "input ready" line names.
 */
public class InputScreen extends class_437 {
    /** gui coordinates (GUI scale 2 on a 1280x720 window: window pixel = 2 * gui unit) */
    static final int WIDGET_X = 300, WIDGET_Y = 100, WIDGET_W = 60, WIDGET_H = 20;
    static final int DRAG_X = 100, DRAG_Y = 200, DRAG_DX = 50, DRAG_DY = 25;
    static final int RIGHT_X = 200, RIGHT_Y = 150, SHIFT_X = 250, SHIFT_Y = 160;
    static final int MARKER_KEY = 298; // F9 ends the sequence
    /** the sequence for client_input.py, in window pixels */
    static final String SEQUENCE = "key a; key shift+a; key ctrl+a; char U+E9; char U+1F600; "
        + "move " + 2 * (WIDGET_X + WIDGET_W / 2) + " " + 2 * (WIDGET_Y + WIDGET_H / 2) + "; click 1; "
        + "move " + 2 * RIGHT_X + " " + 2 * RIGHT_Y + "; click 3; "
        + "move " + 2 * DRAG_X + " " + 2 * DRAG_Y + "; down 1; drag " + 2 * (DRAG_X + DRAG_DX) + " " + 2 * (DRAG_Y + DRAG_DY)
        + " 10; up 1; move " + 2 * SHIFT_X + " " + 2 * SHIFT_Y + "; shiftclick 1; key F9";

    final List<int[]> pressed = new ArrayList<>();   // key, scancode, mods
    final List<int[]> released = new ArrayList<>();
    final List<boolean[]> helpers = new ArrayList<>(); // for key 65: shift, control, selectAll
    final List<int[]> chars = new ArrayList<>();      // char, mods
    final List<double[]> clicks = new ArrayList<>();  // x, y, button, shift
    final List<double[]> releases = new ArrayList<>();
    double dragDx, dragDy;
    int drags;
    String highlightedAfterSelectAll;
    volatile boolean done;
    class_342 edit;
    ModWidget widget;
    class_4185 button;
    int buttonPresses;

    public InputScreen() {
        super(class_2561.method_43470("CB client test: input"));
    }

    /** a 1.20.1 mod widget: renderWidget, narration and an onClick override */
    static class ModWidget extends class_339 {
        final List<double[]> onClicks = new ArrayList<>();

        ModWidget() {
            super(WIDGET_X, WIDGET_Y, WIDGET_W, WIDGET_H, class_2561.method_43470("cb widget"));
        }

        @Override
        protected void method_48579(class_332 ctx, int mouseX, int mouseY, float delta) {
            ctx.method_25294(method_46426(), method_46427(), method_46426() + field_22758, method_46427() + field_22759, 0xFF3366CC);
        }

        @Override
        protected void method_47399(class_6382 out) {
        }

        @Override
        public void method_25348(double x, double y) {
            onClicks.add(new double[] {x, y});
        }
    }

    @Override
    protected void method_25426() {
        edit = new class_342(field_22793, 20, 40, 200, 20, class_2561.method_43470("cb edit"));
        edit.method_1880(64);
        method_37063(edit);
        widget = new ModWidget();
        method_37063(widget);
        button = class_4185.method_46430(class_2561.method_43470("cb button"), b -> buttonPresses++).method_46434(20, 70, 100, 20).method_46431();
        method_37063(button);
        method_25395(edit);
    }

    @Override
    public boolean method_25421() {
        return false;
    }

    @Override
    public boolean method_25404(int key, int scancode, int mods) {
        pressed.add(new int[] {key, scancode, mods});
        if (key == 65) {
            helpers.add(new boolean[] {method_25442(), method_25441(), method_25439(key)});
        }
        if (key == MARKER_KEY) {
            done = true;
        }
        boolean r = super.method_25404(key, scancode, mods);
        if (key == 65 && (mods & 2) != 0) {
            highlightedAfterSelectAll = edit.method_1866();
        }
        return r;
    }

    @Override
    public boolean method_16803(int key, int scancode, int mods) {
        released.add(new int[] {key, scancode, mods});
        return super.method_16803(key, scancode, mods);
    }

    @Override
    public boolean method_25400(char c, int mods) {
        chars.add(new int[] {c, mods});
        return super.method_25400(c, mods);
    }

    @Override
    public boolean method_25402(double x, double y, int button) {
        clicks.add(new double[] {x, y, button, method_25442() ? 1 : 0});
        return super.method_25402(x, y, button);
    }

    @Override
    public boolean method_25406(double x, double y, int button) {
        releases.add(new double[] {x, y, button});
        return super.method_25406(x, y, button);
    }

    @Override
    public boolean method_25403(double x, double y, int button, double dx, double dy) {
        drags++;
        dragDx += dx;
        dragDy += dy;
        return super.method_25403(x, y, button, dx, dy);
    }

    @Override
    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
        ctx.method_25294(0, 0, field_22789, field_22790, 0xFF202830);
        super.method_25394(ctx, mouseX, mouseY, delta);
    }
}
