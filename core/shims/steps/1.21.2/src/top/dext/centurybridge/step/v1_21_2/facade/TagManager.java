package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3302;
import net.minecraft.class_3503;
import net.minecraft.class_3695;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_7924;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's TagManager (net/minecraft/class_3505, classRenames), gone in 1.21.2 (tags load with the registries):
 * a reload listener loading every registry's tags of a registry access, the results kept for getResult(). 1.21.1's
 * body on 1.21.2's TagLoader (each registry's holders by id, its tags directory), one task per registry on the
 * background executor, the results stored on the game executor after the barrier. Its fields are public under their
 * intermediary names (an access widener never reaches a CenturyBridge class).
 */
public class TagManager implements class_3302 {

    /** registryAccess */
    public final class_5455 field_28312;
    /** results */
    public List<LoadResult<?>> field_36396 = List.of();

    public TagManager(class_5455 registryAccess) {
        this.field_28312 = registryAccess;
    }

    /** getResult() */
    public List<LoadResult<?>> method_40096() {
        return field_36396;
    }

    @Override
    public CompletableFuture<Void> method_25931(class_3302.class_4045 barrier, class_3300 resources, Executor background, Executor game) {
        List<? extends CompletableFuture<? extends LoadResult<?>>> loads =
            field_28312.method_40311().map(entry -> method_33178(resources, background, entry)).toList();
        return CompletableFuture.allOf(loads.toArray(CompletableFuture[]::new))
            .thenCompose(barrier::method_18352)
            .thenAcceptAsync(v -> method_40098(loads, v), game);
    }

    /**
     * 1.21.1's apply step (the lambda its reload ran on the game executor after the barrier): the results stored. A
     * method of its own, as 1.21.1's was, so a 1.21.1 mixin into it (Fabric API 1.21.1's TagManagerLoaderMixin and
     * mods written like it, which read the results at its RETURN) applies here.
     */
    @CbVanillaReads("CbRestTagResultsMixin: each 1.21.2 ReloadableServerResources, made with the tags its reload loaded"
        + " (postponedTags), runs a TagManager's apply step over them (ofPending) before any reload listener prepares")
    public void method_40098(List<? extends CompletableFuture<? extends LoadResult<?>>> loads, Void unused) {
        field_36396 = loads.stream().<LoadResult<?>>map(CompletableFuture::join).toList();
    }

    /**
     * The TagManager of a 1.21.2 reload: 1.21.1 loaded the tags among the reload listeners (this class) and kept them on
     * ReloadableServerResources; 1.21.2 loads them before (TagLoader, as MinecraftServer and WorldLoader call it) and
     * hands them to ReloadableServerResources as pending tags, bound to the registries only after the reload
     * (updateStaticRegistryTags; reading a pending tag's holder set before throws "unbound tag"). Each registry's
     * loaded tags as 1.21.1's LoadResult (the tag ids with their entries, as TagLoader loaded them: {@link #loaded}),
     * stored through the apply step. registryAccess is the composite access 1.21.1 built its TagManager on
     * (ReloadableServerResources' holder is made of it in both).
     */
    public static TagManager ofPending(class_5455 registryAccess, List<net.minecraft.class_2378.class_10106<?>> pending) {
        TagManager manager = new TagManager(registryAccess);
        List<CompletableFuture<LoadResult<?>>> loads = new java.util.ArrayList<>(pending.size());
        for (net.minecraft.class_2378.class_10106<?> tags : pending) {
            LoadResult<?> r = loadResult(tags);
            if (r != null) {
                loads.add(CompletableFuture.completedFuture(r));
            }
        }
        manager.method_40098(loads, null);
        return manager;
    }

    /**
     * the tags a registry's prepareTagReload was handed, by the pending tags it made (CbRestPendingTagsMixin): 1.21.2's
     * pending tags keep them in a field of an anonymous class; this is the same map, recorded where it is passed in
     */
    private static final Map<net.minecraft.class_2378.class_10106<?>, Map<?, ? extends List<?>>> LOADED =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /** CbRestPendingTagsMixin: MappedRegistry.prepareTagReload(result) returned these pending tags of result's tags */
    public static void loaded(net.minecraft.class_2378.class_10106<?> pending, net.minecraft.class_3503.class_6863<?> result) {
        LOADED.put(pending, result.comp_329());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> LoadResult<T> loadResult(net.minecraft.class_2378.class_10106<T> pending) {
        Map<?, ? extends List<?>> loaded = LOADED.get(pending);
        if (loaded == null) {
            // pending tags no MappedRegistry made (vanilla has no other Registry implementation): their entries are not known
            org.slf4j.LoggerFactory.getLogger("centurybridge").warn("TagManager: the pending tags of {} were not made by"
                + " MappedRegistry.prepareTagReload; left out of the reload's tag results", pending.method_62693());
            return null;
        }
        Map<class_2960, java.util.Collection<net.minecraft.class_6880<T>>> tags = new java.util.LinkedHashMap<>();
        loaded.forEach((key, holders) -> tags.put(((net.minecraft.class_6862<T>) key).comp_327(), (List) holders));
        return new LoadResult<>((class_5321) pending.method_62693(), tags);
    }

    /** 1.21.1's reload with profilers (the profilers 1.21.2 dropped are not used) */
    public CompletableFuture<Void> method_25931(class_3302.class_4045 barrier, class_3300 resources, class_3695 prepare, class_3695 apply,
                                                Executor background, Executor game) {
        return method_25931(barrier, resources, background, game);
    }

    /** createLoader(resources, executor, entry): one registry's tags, loaded on the background executor (public: see above) */
    public <T> CompletableFuture<LoadResult<T>> method_33178(class_3300 resources, Executor background, class_5455.class_6892<T> entry) {
        class_5321<? extends net.minecraft.class_2378<T>> key = entry.comp_350();
        net.minecraft.class_2378<T> registry = entry.comp_351();
        class_3503<net.minecraft.class_6880<T>> loader = new class_3503<>((id, required) -> registry.method_10223(id),
            class_7924.method_60916(key));
        return CompletableFuture.supplyAsync(() -> new LoadResult<>(key, (Map) loader.method_18242(loader.method_33174(resources))), background);
    }

    /**
     * 1.21.1's TagManager.LoadResult (net/minecraft/class_3505$class_6863, classRenames): a registry's key and its
     * loaded tags. The record written out, its fields public.
     */
    public static final class LoadResult<T> {
        /** key */
        public final class_5321<? extends net.minecraft.class_2378<T>> comp_328;
        /** tags */
        public final Map<class_2960, java.util.Collection<net.minecraft.class_6880<T>>> comp_329;

        public LoadResult(class_5321<? extends net.minecraft.class_2378<T>> key, Map<class_2960, java.util.Collection<net.minecraft.class_6880<T>>> tags) {
            this.comp_328 = key;
            this.comp_329 = tags;
        }

        public class_5321<? extends net.minecraft.class_2378<T>> comp_328() {
            return comp_328;
        }

        public Map<class_2960, java.util.Collection<net.minecraft.class_6880<T>>> comp_329() {
            return comp_329;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof LoadResult<?> r && Objects.equals(comp_328, r.comp_328) && Objects.equals(comp_329, r.comp_329);
        }

        @Override
        public int hashCode() {
            return 31 * Objects.hashCode(comp_328) + Objects.hashCode(comp_329);
        }

        @Override
        public String toString() {
            return "LoadResult[key=" + comp_328 + ", tags=" + comp_329 + "]";
        }
    }
}
