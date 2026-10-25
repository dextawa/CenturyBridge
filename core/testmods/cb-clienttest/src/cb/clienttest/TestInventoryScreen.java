package cb.clienttest;

import net.minecraft.class_1657;
import net.minecraft.class_332;
import net.minecraft.class_490;

/**
 * A subclass of the vanilla inventory screen that overrides renderBg and calls super first (vanilla's
 * background and player model), then draws a marker strip over the top edge. The check: the override
 * runs and its super call returns.
 */
public class TestInventoryScreen extends class_490 {
    static int bgCalls, superReturned;

    public TestInventoryScreen(class_1657 player) {
        super(player);
    }

    @Override
    protected void method_2389(class_332 ctx, float delta, int mouseX, int mouseY) {
        bgCalls++;
        super.method_2389(ctx, delta, mouseX, mouseY);
        superReturned++;
        ctx.method_25294(this.field_2776, this.field_2800 - 6, this.field_2776 + this.field_2792, this.field_2800 - 2, 0xFF33CC33);
    }
}
