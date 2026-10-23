package top.dext.centurybridge.step.v26_1.mixin;

import java.util.SequencedSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTagRemovals;

/**
 * 1.21.11 Fabric API's tag removal on 26.1's TagLoader (1.21.11's TagLoaderMixin, its MixinExtras injections
 * written as plain Mixin ones: the layer is compiled without MixinExtras). The logic is rt/CbFapiTagRemovals.
 */
@Mixin(TagLoader.class)
public abstract class CbFapiTagLoaderMixin<T> {

    // load: per file, fileToId(location); per pack's copy of it, sourcePackId() and then the parsed file's
    // entries() -- each read once, in that order. 1.21.11 captured the file location, the parsed file and the
    // pack as locals; here the three calls hand them over (no local capture: slots without a local-variable
    // entry make it fragile)

    @Redirect(method = "load", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/resources/FileToIdConverter;fileToId(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/Identifier;"))
    private Identifier cb$fileLocation(FileToIdConverter lister, Identifier location) {
        CbFapiTagRemovals.loadingFile(location);
        return lister.fileToId(location);
    }

    @Redirect(method = "load", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/server/packs/resources/Resource;sourcePackId()Ljava/lang/String;"))
    private String cb$filePack(Resource resource) {
        return CbFapiTagRemovals.loadingPack(resource.sourcePackId());
    }

    /** a parsed file's entries are about to be added: record its removals and its pack */
    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/tags/TagFile;entries()Ljava/util/List;"))
    private List<TagEntry> cb$loadRemoveEntries(TagFile file) {
        CbFapiTagRemovals.loadRemoveEntries(file);
        return file.entries();
    }

    /** build: each tag's removals merged into its entries before the dependency sort sees them */
    @ModifyArg(method = "build", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<Identifier, List<TagLoader.EntryWithSource>> cb$mergeRemovals(BiConsumer<Identifier, List<TagLoader.EntryWithSource>> consumer) {
        return CbFapiTagRemovals.mergingRemovals(consumer);
    }

    /** build: each tag built with its id known */
    @ModifyArg(method = "build", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/util/DependencySorter;orderByDependencies(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<Identifier, Object> cb$scopeTagId(BiConsumer<Identifier, Object> consumer) {
        return CbFapiTagRemovals.scopedTagId(consumer);
    }

    /** tryBuildTag: the element set it fills (its only SequencedSet local, "values") */
    @ModifyVariable(method = "tryBuildTag", at = @At("STORE"), ordinal = 0)
    private SequencedSet<T> cb$buildingSet(SequencedSet<T> values) {
        return CbFapiTagRemovals.building(values);
    }

    /** tryBuildTag: a removal entry takes its elements out instead of adding them */
    @Redirect(method = "tryBuildTag", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/tags/TagEntry;build(Lnet/minecraft/tags/TagEntry$Lookup;Ljava/util/function/Consumer;)Z"))
    private boolean cb$removeEntries(TagEntry entry, TagEntry.Lookup<T> lookup, Consumer<T> consumer) {
        return CbFapiTagRemovals.build(entry, lookup, consumer);
    }

    @Inject(method = "build", at = @At("RETURN"))
    private void cb$clearRemovals(Map<Identifier, List<TagLoader.EntryWithSource>> builders, CallbackInfoReturnable<Map<Identifier, List<T>>> cir) {
        CbFapiTagRemovals.removeTagRemovalReferences();
    }
}
