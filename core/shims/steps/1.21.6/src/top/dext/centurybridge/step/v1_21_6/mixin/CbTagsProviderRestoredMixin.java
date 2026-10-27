package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_2474;
import net.minecraft.class_3495;
import net.minecraft.class_6862;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_6.facade.TagAppender;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's TagsProvider.tag(TagKey) (method_10512), restored with 1.21.5's body (javap): a TagAppender (the layer's
 * facade, classRenames of class_2474$class_5124) over the tag's raw builder, getOrCreateRawBuilder (method_27169, still
 * in 1.21.6). 1.21.6 moved tag() to KeyTagProvider (class_11388.method_71551), answering the new TagAppender interface;
 * nothing in 1.21.6 calls this one, so it serves converted 1.21.5 providers' calls and their overrides only. Priority
 * below every mod's.
 */
@Mixin(value = class_2474.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbTagsProviderRestoredMixin<T> {

    @Shadow
    protected abstract class_3495 method_27169(class_6862<T> tag);

    /** tag(TagKey) */
    protected TagAppender<T> method_10512(class_6862<T> tag) {
        class_3495 builder = this.method_27169(tag);
        return new TagAppender<>(builder);
    }
}
