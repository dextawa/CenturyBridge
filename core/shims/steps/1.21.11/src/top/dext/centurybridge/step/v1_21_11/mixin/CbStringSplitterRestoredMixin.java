package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2583;
import net.minecraft.class_5225;
import net.minecraft.class_5481;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 StringSplitter.componentStyleAtWidth(FormattedCharSequence, int) (method_30876), gone from 1.21.11 (style
 * lookup moved to ActiveTextCollector's ClickableStyleFinder; javap -c -p both): 1.21.10's body -- a WidthLimitedCharSink
 * of the width takes the sequence's characters, each costing widthProvider.getWidth(codepoint, style); the style of the
 * first character that no longer fits is the answer, null when every character fits. 1.21.11 never calls it.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_5225.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbStringSplitterRestoredMixin implements CbTextDrawing.SplitterStyle {
    @Shadow
    @Final
    class_5225.class_5231 field_24216;

    /** StringSplitter.componentStyleAtWidth(sequence, width) */
    public class_2583 method_30876(class_5481 seq, int width) {
        float[] left = {width};
        class_2583[] at = {null};
        seq.accept((index, style, codepoint) -> {
            left[0] -= this.field_24216.getWidth(codepoint, style);
            if (left[0] >= 0.0F) {
                return true;
            }
            at[0] = style;
            return false;
        });
        return at[0];
    }

    @Override
    public class_2583 cb$v1_21_11$styleAtWidth(class_5481 seq, int width) {
        return this.method_30876(seq, width);
    }
}
