package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTagDatagen;

/**
 * TagsProvider writes each tag as new TagFile(entries, builder.shouldReplace()): the builder is handed over as
 * its replace flag is read, so the TagFile made next carries its 1.21.11 removal entries (1.21.11's
 * TagsProviderMixin set them on that TagFile; mixin/CbFapiTagFileMixin takes them at construction -- Fabric
 * API's own injection on that constructor call stays untouched).
 */
@Mixin(TagsProvider.class)
public abstract class CbFapiTagsProviderMixin {

    @Redirect(method = "lambda$run$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/tags/TagBuilder;shouldReplace()Z"))
    private boolean cb$handOverRemovals(TagBuilder builder) {
        CbFapiTagDatagen.writing(builder);
        return builder.shouldReplace();
    }
}
