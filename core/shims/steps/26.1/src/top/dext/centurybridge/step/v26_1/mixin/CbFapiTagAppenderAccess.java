package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.tags.TagBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** the TagBuilder behind vanilla's TagAppender.forBuilder appender (TagAppender$1) */
@Mixin(targets = "net.minecraft.data.tags.TagAppender$1")
public interface CbFapiTagAppenderAccess {

    @Accessor("val$builder")
    TagBuilder cb$builder();
}
