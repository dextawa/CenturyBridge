package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestItems;

/**
 * Per-item state 1.21.3's constructors took that 1.21.4 moved to client item models: a spawn egg's two
 * colors, a bundle's open front / back models (CbRestItems). Set only for an item a 1.21.3 constructor call
 * built; vanilla's items keep the defaults, which no 1.21.4 code reads.
 */
@Mixin(class_1792.class)
public abstract class CbRestItemStateMixin implements CbRestItems.LegacyState {

    @Unique
    private int cb$backgroundColor;

    @Unique
    private int cb$highlightColor;

    @Unique
    private class_2960 cb$openFrontModel;

    @Unique
    private class_2960 cb$openBackModel;

    @Override
    public void cb$setEggColors(int background, int highlight) {
        this.cb$backgroundColor = background;
        this.cb$highlightColor = highlight;
    }

    @Override
    public int cb$eggColor(int tintIndex) {
        return tintIndex == 0 ? this.cb$backgroundColor : this.cb$highlightColor;
    }

    @Override
    public void cb$setBundleModels(class_2960 front, class_2960 back) {
        this.cb$openFrontModel = front;
        this.cb$openBackModel = back;
    }

    @Override
    public class_2960 cb$openFrontModel() {
        return this.cb$openFrontModel;
    }

    @Override
    public class_2960 cb$openBackModel() {
        return this.cb$openBackModel;
    }
}
