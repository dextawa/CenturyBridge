package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2583;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;

/**
 * ActiveTextCollector.ClickableStyleFinder: the position it looks at (constructor arguments 2 and 3, which its accept /
 * acceptScrolling hit-test as x and y; javap -c) and its own style filter (private method_75776).
 */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_12225$class_12226")
public abstract class CbStyleFinderAccess implements CbTextDrawing.StyleFinder {
    @Shadow
    @Final
    private int field_63839;

    @Shadow
    @Final
    private int field_63840;

    @Shadow
    private void method_75776(class_2583 style) {
    }

    @Override
    public int cb$v1_21_11$x() {
        return this.field_63839;
    }

    @Override
    public int cb$v1_21_11$y() {
        return this.field_63840;
    }

    @Override
    public void cb$v1_21_11$offer(class_2583 style) {
        this.method_75776(style);
    }
}
