package cb.clienttest;

import net.minecraft.class_1661;
import net.minecraft.class_1723;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_465;

/**
 * A mod's own container screen over the player's inventory menu: renderBg draws a backdrop and then the
 * inventory texture at leftPos/topPos, renderLabels the title. The checks: renderBg and renderLabels run
 * (1.20.1's AbstractContainerScreen.render calls both every frame) and renderBg sees 1.20.1's leftPos,
 * topPos ((width - imageWidth) / 2, (height - imageHeight) / 2). Its float is what 1.20.1's GameRenderer
 * hands every screen: the last frame's duration in ticks (Minecraft.getDeltaFrameTime, not a partial
 * tick: it exceeds 1 on slow frames), so the check is only that it is finite and not negative; the
 * largest value seen is logged.
 */
public class TestContainerScreen extends class_465<class_1723> {
    static final class_2960 INVENTORY = new class_2960("textures/gui/container/inventory.png");
    static int bgCalls, labelCalls;
    static String bgProblem;
    static float maxDelta;

    public TestContainerScreen(class_1723 menu, class_1661 inventory) {
        super(menu, inventory, class_2561.method_43470("CB container"));
    }

    @Override
    public boolean method_25421() {
        return false;
    }

    @Override
    protected void method_2389(class_332 ctx, float delta, int mouseX, int mouseY) {
        bgCalls++;
        int left = (this.field_22789 - this.field_2792) / 2, top = (this.field_22790 - this.field_2779) / 2;
        if (bgProblem == null && (this.field_2776 != left || this.field_2800 != top)) {
            bgProblem = "leftPos/topPos " + this.field_2776 + "," + this.field_2800 + " expected " + left + "," + top;
        }
        if (bgProblem == null && !(delta >= 0f && Float.isFinite(delta))) {
            bgProblem = "frame delta " + delta;
        }
        maxDelta = Math.max(maxDelta, delta);
        ctx.method_25294(0, 0, this.field_22789, this.field_22790, 0xFF302020);
        ctx.method_25302(INVENTORY, this.field_2776, this.field_2800, 0, 0, this.field_2792, this.field_2779);
    }

    @Override
    protected void method_2388(class_332 ctx, int mouseX, int mouseY) {
        labelCalls++;
        ctx.method_51433(this.field_22793, "CB container labels", 8, -12, 0xFFFFFF00, true);
    }

    @Override
    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
        super.method_25394(ctx, mouseX, mouseY, delta);
        this.method_2380(ctx, mouseX, mouseY);
    }
}
