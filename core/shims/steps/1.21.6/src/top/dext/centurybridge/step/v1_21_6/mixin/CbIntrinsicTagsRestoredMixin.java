package top.dext.centurybridge.step.v1_21_6.mixin;

import java.util.function.Function;
import net.minecraft.class_2474;
import net.minecraft.class_3495;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_7784;
import net.minecraft.class_7889;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_6.facade.IntrinsicTagAppender;
import top.dext.centurybridge.step.v1_21_6.facade.TagAppender;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's IntrinsicHolderTagsProvider.tag(TagKey) (method_46827, answering IntrinsicTagAppender) and its override of
 * TagsProvider.tag (method_10512, the bridge to it), restored with 1.21.5's bodies (javap). The appender (the layer's
 * facade, classRenames of class_7889$class_7890) adds values through the provider's key extractor (field_40954, still in
 * 1.21.6) into the tag's raw builder. 1.21.6's own method_46827 (answering the TagAppender interface, class_11389) has
 * another descriptor and stays as it is. The mixin extends TagsProvider for its protected getOrCreateRawBuilder (a
 * shadow does not climb the hierarchy). Priority below every mod's.
 */
@Mixin(value = class_7889.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbIntrinsicTagsRestoredMixin<T> extends class_2474<T> {

    @Shadow
    @Final
    private Function<T, class_5321<T>> field_40954;

    private CbIntrinsicTagsRestoredMixin(class_7784 output) {
        super(output, null, null);
    }

    /** tag(TagKey), 1.21.5's IntrinsicHolderTagsProvider shape */
    protected IntrinsicTagAppender<T> method_46827(class_6862<T> tag) {
        class_3495 builder = this.method_27169(tag);
        return new IntrinsicTagAppender<>(builder, this.field_40954);
    }

    /** tag(TagKey), 1.21.5's override of TagsProvider.tag */
    protected TagAppender<T> method_10512(class_6862<T> tag) {
        return this.method_46827(tag);
    }
}
