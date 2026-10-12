package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.compat.fapi.impl.tag.CbFapiTagRemoval;

/**
 * Tag files remove entries from their tag while the tag group loads (1.20.1's tag API
 * TagGroupLoaderMixin; the logic is CbFapiTagRemoval's).
 *
 * <ul>
 * <li>loadTags: each parsed tag file's removals are recorded with its data pack. 1.20.1 read the file
 * id, the tag file and the pack name out of the method's locals after the file's entries were added;
 * plain Mixin cannot read locals without capturing every one of them, so the three calls that produce
 * them in that loop hand them over: the file id passed to toResourceId, the pack name getResourcePackName
 * answers, and the tag file whose entries() is about to be added (the last of the three, per file).</li>
 * <li>buildGroup: each tag's entries are merged with its removals before they go into the dependency
 * tracker, and the tag being resolved is known while the tracker visits it; the records are dropped
 * when the group is built.</li>
 * <li>resolveAll: a removal entry takes its values out of the values collected so far, every other
 * entry adds its values there, and the tag resolves to those values. 1.20.1 wrapped TagEntry.resolve
 * and read the tracked entry from the loop's locals; here the entry() call before it hands the tracked
 * entry over and the resolve call is redirected.</li>
 * </ul>
 */
@Mixin(net.minecraft.class_3503.class)
abstract class CbFapiTagGroupLoaderRemoval {

    @Redirect(method = "method_33174", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7654;method_45115(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2960;"))
    private net.minecraft.class_2960 cb$tagFileId(net.minecraft.class_7654 finder, net.minecraft.class_2960 file) {
        CbFapiTagRemoval.loadingFile(file);
        return finder.method_45115(file);
    }

    @Redirect(method = "method_33174", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3298;method_14480()Ljava/lang/String;"))
    private String cb$tagFileSource(net.minecraft.class_3298 resource) {
        String source = resource.method_14480();
        CbFapiTagRemoval.loadingSource(source);
        return source;
    }

    @Redirect(method = "method_33174", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7475;comp_811()Ljava/util/List;"))
    private List<net.minecraft.class_3497> cb$tagFileEntries(net.minecraft.class_7475 tagFile) {
        CbFapiTagRemoval.loadRemoveEntries(tagFile);
        return tagFile.comp_811();
    }

    @ModifyArg(method = "method_18242", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<net.minecraft.class_2960, List<net.minecraft.class_3503.class_5145>> cb$mergeRemovals(
            BiConsumer<net.minecraft.class_2960, List<net.minecraft.class_3503.class_5145>> addToTracker) {
        return (tagId, entries) -> {
            CbFapiTagRemoval.mergeAddedAndRemovedEntries(tagId, entries);
            addToTracker.accept(tagId, entries);
        };
    }

    @ModifyArg(method = "method_18242", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_8523;method_51487(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<net.minecraft.class_2960, Object> cb$scopeTagId(BiConsumer<net.minecraft.class_2960, Object> resolveTag) {
        return (tagId, dependencies) -> {
            CbFapiTagRemoval.setTagId(tagId);
            try {
                resolveTag.accept(tagId, dependencies);
            } finally {
                CbFapiTagRemoval.clearTagId();
            }
        };
    }

    @Inject(method = "method_18242", at = @At("RETURN"))
    private void cb$removeTagRemovalReferences(Map<net.minecraft.class_2960, List<net.minecraft.class_3503.class_5145>> tags,
                                               CallbackInfoReturnable<Map<net.minecraft.class_2960, Collection<?>>> cir) {
        CbFapiTagRemoval.removeTagRemovalReferences();
    }

    @Inject(method = "method_43952", at = @At("HEAD"))
    private void cb$beginResolve(net.minecraft.class_3497.class_7474<?> valueGetter, List<net.minecraft.class_3503.class_5145> entries,
                                 CallbackInfoReturnable<Either<Collection<net.minecraft.class_3503.class_5145>, Collection<?>>> cir) {
        CbFapiTagRemoval.beginResolve();
    }

    @Redirect(method = "method_43952", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3503$class_5145;comp_324()Lnet/minecraft/class_3497;"))
    private net.minecraft.class_3497 cb$resolvingEntry(net.minecraft.class_3503.class_5145 trackedEntry) {
        CbFapiTagRemoval.resolving(trackedEntry);
        return trackedEntry.comp_324();
    }

    @Redirect(method = "method_43952", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3497;method_26790(Lnet/minecraft/class_3497$class_7474;Ljava/util/function/Consumer;)Z"))
    private boolean cb$resolve(net.minecraft.class_3497 entry, net.minecraft.class_3497.class_7474<Object> valueGetter, Consumer<Object> vanillaAdd) {
        return CbFapiTagRemoval.resolve(entry, valueGetter);
    }

    @Inject(method = "method_43952", at = @At("RETURN"), cancellable = true)
    private void cb$endResolve(net.minecraft.class_3497.class_7474<?> valueGetter, List<net.minecraft.class_3503.class_5145> entries,
                               CallbackInfoReturnable<Either<Collection<net.minecraft.class_3503.class_5145>, Collection<?>>> cir) {
        cir.setReturnValue(CbFapiTagRemoval.endResolve(cir.getReturnValue()));
    }
}
