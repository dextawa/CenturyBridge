package top.dext.centurybridge.step.v1_21_2.mixin;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import net.minecraft.class_3497;
import net.minecraft.class_3503;
import net.minecraft.class_7475;
import net.minecraft.class_7654;
import net.minecraft.class_8523;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricTagFile;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiTags;

/**
 * fabric-tag-api-v1's TagGroupLoaderMixin (0.116, Apache-2.0, FabricMC) on 1.21.2's TagLoader, whose load /
 * tryBuildTag / build are 1.21.1's (bytecode checked). 0.116 read the file's id, parsed contents and source
 * pack from load's locals after the entries were added; here each is taken as load computes it -- the tag id
 * (fileToId), the parsed file (its replace() check), the source pack (sourcePackId, the first call of each
 * file, before the entries are added) -- and the removal entries are recorded right after the entries'
 * forEach, as 0.116 did. tryBuildTag's entries are resolved into one ordered set, a removal entry taking its
 * values out; the built tag is that set (1.21.2's tag is a List where 1.21.1's was a Set: the same elements in
 * the same order).
 */
@Mixin(class_3503.class)
public abstract class CbRestFapiTagLoaderMixin {

    @Unique
    private static final ThreadLocal<LinkedHashSet<Object>> CB_CURRENT_VALUES = ThreadLocal.withInitial(LinkedHashSet::new);
    @Unique
    private static final ThreadLocal<class_2960> CB_LOAD_ID = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<class_7475> CB_LOAD_FILE = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<String> CB_LOAD_SOURCE = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<class_3503.class_5145> CB_ENTRY = new ThreadLocal<>();

    @Redirect(method = "method_33174", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7654;method_45115(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2960;"))
    private class_2960 cb$tagId(class_7654 converter, class_2960 file) {
        class_2960 id = converter.method_45115(file);
        CB_LOAD_ID.set(id);
        return id;
    }

    @Redirect(method = "method_33174", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7475;comp_812()Z"))
    private boolean cb$parsedFile(class_7475 file) {
        CB_LOAD_FILE.set(file);
        return file.comp_812();
    }

    @Redirect(method = "method_33174", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3298;method_14480()Ljava/lang/String;", ordinal = 0))
    private String cb$source(class_3298 resource) {
        String source = resource.method_14480();
        CB_LOAD_SOURCE.set(source);
        return source;
    }

    @Inject(method = "method_33174", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void cb$loadRemoveEntries(class_3300 resources, CallbackInfoReturnable<Map<class_2960, List<class_3503.class_5145>>> cir) {
        class_2960 normalizedId = CbRestFapiTags.normalizeTagResourceId(CB_LOAD_ID.get());
        String sourceId = CB_LOAD_SOURCE.get();
        for (class_3497 entry : ((FabricTagFile) (Object) CB_LOAD_FILE.get()).remove()) {
            CbRestFapiTags.addRemoveEntry(normalizedId, new class_3503.class_5145(entry, sourceId));
        }
        CbRestFapiTags.addTagSource(normalizedId, sourceId);
    }

    @Redirect(method = "method_18242", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_8523;method_51487(Ljava/util/function/BiConsumer;)V"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void cb$scopeIdAroundOrderByDependencies(class_8523 sorter, BiConsumer consumer) {
        sorter.method_51487((BiConsumer) (id, contents) -> {
            CbRestFapiTags.setTagId((class_2960) id);
            try {
                consumer.accept(id, contents);
            } finally {
                CbRestFapiTags.clearTagId();
            }
        });
    }

    @ModifyArg(method = "method_18242", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private BiConsumer cb$addTagRemovalReferences(BiConsumer forEach) {
        return (id, entries) -> {
            CbRestFapiTags.mergeAddedAndRemovedEntries((class_2960) id, (List<class_3503.class_5145>) entries);
            forEach.accept(id, entries);
        };
    }

    @Inject(method = "method_43952", at = @At("HEAD"))
    private void cb$initCurrentValues(class_3497.class_7474<?> getter, List<class_3503.class_5145> entries, CallbackInfoReturnable<Either<?, ?>> cir) {
        CB_CURRENT_VALUES.get().clear();
    }

    @Redirect(method = "method_43952", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3503$class_5145;comp_324()Lnet/minecraft/class_3497;"))
    private class_3497 cb$currentEntry(class_3503.class_5145 entry) {
        CB_ENTRY.set(entry);
        return entry.comp_324();
    }

    @Redirect(method = "method_43952", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3497;method_26790(Lnet/minecraft/class_3497$class_7474;Ljava/util/function/Consumer;)Z"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean cb$removeEntriesFromTags(class_3497 instance, class_3497.class_7474 lookup, Consumer output) {
        LinkedHashSet<Object> values = CB_CURRENT_VALUES.get();
        if (CbRestFapiTags.isEntryRemove(CB_ENTRY.get())) {
            instance.method_26790(lookup, values::remove);
            return true;
        }
        return instance.method_26790(lookup, (Consumer) values::add);
    }

    @Inject(method = "method_43952", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void cb$replaceRightReturn(class_3497.class_7474<?> getter, List<class_3503.class_5145> entries, CallbackInfoReturnable<Either> cir) {
        Either returnValue = cir.getReturnValue();
        if (returnValue != null && returnValue.right().isPresent()) {
            cir.setReturnValue(Either.right(List.copyOf(ImmutableSet.copyOf(CB_CURRENT_VALUES.get()))));
        }
        CB_CURRENT_VALUES.get().clear();
        CB_ENTRY.remove();
    }

    @Inject(method = "method_18242", at = @At("RETURN"))
    private void cb$removeTagRemovalReferencesWhenFinished(Map<class_2960, List<class_3503.class_5145>> builders, CallbackInfoReturnable<Map<class_2960, List<?>>> cir) {
        CbRestFapiTags.removeTagRemovalReferences();
    }
}
