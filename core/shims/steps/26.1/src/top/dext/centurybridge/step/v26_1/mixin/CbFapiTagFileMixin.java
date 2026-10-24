package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.compat.fapi.tag.v1.FabricTagFile;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTagDatagen;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTagFileHooks;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTagRemovals;

/**
 * TagFile carries 1.21.11's "fabric:remove" entries again and is a 1.21.11 FabricTagFile: its CODEC reads and
 * writes the list (1.21.11's TagFileMixin; rt/CbFapiTagRemovals.modifyTagFileCodec).
 */
@Mixin(TagFile.class)
public abstract class CbFapiTagFileMixin implements FabricTagFile, CbFapiTagFileHooks {

    @Unique
    private List<TagEntry> cb$remove = Collections.emptyList();

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
        target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<TagFile> cb$removeCodec(Function<RecordCodecBuilder.Instance<TagFile>, ? extends com.mojang.datafixers.kinds.App<RecordCodecBuilder.Mu<TagFile>, TagFile>> builder) {
        return CbFapiTagRemovals.modifyTagFileCodec(RecordCodecBuilder.create(builder));
    }

    /** a TagFile TagsProvider makes for a builder with 1.21.11 removal entries carries them (CbFapiTagsProviderMixin) */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$datagenRemovals(List<TagEntry> entries, boolean replace, CallbackInfo ci) {
        List<TagEntry> removals = CbFapiTagDatagen.takeRemovals();
        if (removals != null) {
            this.cb$remove = removals;
        }
    }

    @Override
    public List<TagEntry> remove() {
        return Collections.unmodifiableList(this.cb$remove);
    }

    @Override
    public void cb$setRemove(List<TagEntry> remove) {
        this.cb$remove = remove;
    }
}
