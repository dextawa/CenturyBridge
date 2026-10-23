package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3302;
import net.minecraft.class_3695;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_6903;
import net.minecraft.class_7225;
import top.dext.centurybridge.step.v1_21_2.mixin.CbRestHolderLookupAdapterAccessor;

/**
 * Fabric API resource APIs of 1.21.1 (0.116) on 1.21.2's last Fabric API (0.106.1).
 *
 * SimpleResourceReloadListener: 0.116's load / apply took a ProfilerFiller (and its reload the two
 * profilers), 0.106.1's take none (1.21.2 listeners profile on the running thread, Profiler.get()). A
 * 1.21.1 listener class's own load / apply are run with the thread's profiler when 0.106.1 asks; a 1.21.1
 * call passes its profiler on to a 1.21.1 listener and drops it for any other.
 *
 * ResourceCondition.test: 0.116 handed a HolderLookup.Provider, 0.106.1 hands the RegistryInfoLookup of the
 * ops the data is read with. That lookup is (when the data is read with registry ops over a provider, as
 * 0.116 got its provider) the provider's adapter, whose provider a 1.21.1 condition gets; otherwise it gets a
 * provider answering the same registries through the lookup.
 *
 * ResourceReloadListenerKeys.TAGS: 0.116's id of the tag reload listener; 1.21.2 loads tags before any
 * reload listener runs (WorldLoader / ReloadableServerResources), so a listener depending on it is ordered
 * after the tags as in 1.21.1. The id is 0.116's; no 1.21.2 listener carries it, and Fabric's ordering
 * leaves a dependency nobody provides unconstrained.
 */
public final class CbRestFapiResources {

    private CbRestFapiResources() {
    }

    /** ResourceReloadListenerKeys.TAGS of 0.116 */
    public static final class_2960 TAGS = class_2960.method_60656("tags");

    // ------------------------------------------------------------------ SimpleResourceReloadListener

    /** load(manager, profiler, executor) of a 1.21.1 SimpleResourceReloadListener class (override hook adapter) */
    public interface LegacySimpleLoad {
        CompletableFuture<?> load(class_3300 manager, class_3695 profiler, Executor executor);
    }

    /** apply(data, manager, profiler, executor) of a 1.21.1 SimpleResourceReloadListener class (override hook adapter) */
    public interface LegacySimpleApply {
        CompletableFuture<Void> apply(Object data, class_3300 manager, class_3695 profiler, Executor executor);
    }

    /** 0.106.1's load of a 1.21.1 listener class: its own load, profiling on the running thread */
    @SuppressWarnings("rawtypes")
    public static CompletableFuture load(LegacySimpleLoad listener, class_3300 manager, Executor executor) {
        return listener.load(manager, net.minecraft.class_10209.method_64146(), executor);
    }

    /** 0.106.1's apply of a 1.21.1 listener class: its own apply, profiling on the running thread */
    public static CompletableFuture<Void> apply(LegacySimpleApply listener, Object data, class_3300 manager, Executor executor) {
        return listener.apply(data, manager, net.minecraft.class_10209.method_64146(), executor);
    }

    /** a 1.21.1 call of load(manager, profiler, executor) on any listener */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture load(SimpleResourceReloadListener listener, class_3300 manager, class_3695 profiler, Executor executor) {
        if (listener instanceof LegacySimpleLoad legacy) {
            return legacy.load(manager, profiler, executor);
        }
        return listener.load(manager, executor);
    }

    /** a 1.21.1 call of apply(data, manager, profiler, executor) on any listener */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture<Void> apply(SimpleResourceReloadListener listener, Object data, class_3300 manager, class_3695 profiler,
                                                Executor executor) {
        if (listener instanceof LegacySimpleApply legacy) {
            return legacy.apply(data, manager, profiler, executor);
        }
        return listener.apply(data, manager, executor);
    }

    /** a 1.21.1 call of the six-argument reload on a SimpleResourceReloadListener (as on any listener, CbRestReload) */
    public static CompletableFuture<Void> method_25931(SimpleResourceReloadListener<?> listener, class_3302.class_4045 barrier, class_3300 manager,
                                                       class_3695 prepProfiler, class_3695 applyProfiler, Executor background, Executor game) {
        return CbRestReload.reload(listener, barrier, manager, prepProfiler, applyProfiler, background, game);
    }

    /**
     * super.reload(barrier, manager, prepProfiler, applyProfiler, background, game) of a 1.21.1 listener class
     * reaching 0.116's default: load with the preparation profiler, wait at the barrier, apply with the
     * application profiler -- 0.116's body
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture<Void> superReload(SimpleResourceReloadListener listener, class_3302.class_4045 barrier, class_3300 manager,
                                                      class_3695 prepProfiler, class_3695 applyProfiler, Executor background, Executor game) {
        return load(listener, manager, prepProfiler, background)
            .thenCompose(barrier::method_18352)
            .thenCompose(data -> apply(listener, data, manager, applyProfiler, game));
    }

    // ------------------------------------------------------------------ RegistryEntryAddedCallback.allEntries

    /**
     * RegistryEntryAddedCallback.allEntries(registry, consumer) of 0.116: the consumer gets every entry added from
     * now on (0.106.1's entry-added event, the entry's holder) and then every entry already there -- 0.116's body
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> void allEntries(class_2378<T> registry, java.util.function.Consumer<class_6880.class_6883<T>> consumer) {
        net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback.event(registry)
            .register((raw, id, object) -> consumer.accept(registry.method_10223(id).orElseThrow()));
        registry.method_42017().toList().forEach(consumer);
    }

    // ------------------------------------------------------------------ ResourceCondition

    /** the provider a 1.21.1 ResourceCondition.test is asked with, for 0.106.1's registry info lookup (null passes) */
    public static class_7225.class_7874 provider(class_6903.class_7863 lookup) {
        if (lookup == null) {
            return null;
        }
        if (lookup instanceof CbRestHolderLookupAdapterAccessor adapter) {
            return adapter.cb$provider();
        }
        return new InfoProvider(lookup);
    }

    /**
     * A provider answering through a registry info lookup that does not wrap one: each registry's lookup is
     * the info's getter when that is a full lookup (as for loaded registries), else a lookup that finds
     * elements and tags through the getter and cannot list them (an info lookup has no listing). It cannot
     * list the registries either: an info lookup is asked by key only.
     */
    private record InfoProvider(class_6903.class_7863 lookup) implements class_7225.class_7874 {
        @Override
        public Stream<class_5321<? extends class_2378<?>>> method_55282() {
            throw new UnsupportedOperationException("CenturyBridge: a 1.21.1 resource condition listed the registries of a"
                + " 1.21.2 registry info lookup, which answers registries by key only");
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> Optional<? extends class_7225.class_7226<T>> method_46759(class_5321<? extends class_2378<? extends T>> key) {
            return this.lookup.method_46623(key).map(info -> info.comp_1131() instanceof class_7225.class_7226 full
                ? (class_7225.class_7226<T>) full : new InfoRegistryLookup<>((class_5321) key, info));
        }
    }

    private record InfoRegistryLookup<T>(class_5321<? extends class_2378<? extends T>> key, class_6903.class_7862<T> info)
            implements class_7225.class_7226<T> {
        @Override
        public class_5321<? extends class_2378<? extends T>> method_46765() {
            return this.key;
        }

        @Override
        public com.mojang.serialization.Lifecycle method_46766() {
            return this.info.comp_1132();
        }

        @Override
        public Optional<class_6880.class_6883<T>> method_46746(class_5321<T> element) {
            return this.info.comp_1131().method_46746(element);
        }

        @Override
        public Optional<class_6885.class_6888<T>> method_46733(class_6862<T> tag) {
            return this.info.comp_1131().method_46733(tag);
        }

        @Override
        public Stream<class_6880.class_6883<T>> method_42017() {
            throw new UnsupportedOperationException("CenturyBridge: a 1.21.1 resource condition listed " + this.key
                + " through a 1.21.2 registry info lookup, which finds elements by key only");
        }

        @Override
        public Stream<class_6885.class_6888<T>> method_42020() {
            throw new UnsupportedOperationException("CenturyBridge: a 1.21.1 resource condition listed the tags of " + this.key
                + " through a 1.21.2 registry info lookup, which finds tags by key only");
        }
    }

    /** test(lookup) of 0.106.1 for a 1.21.1 condition class: its test(provider) (override hook adapter / bridge) */
    public interface LegacyCondition {
        boolean test(class_7225.class_7874 provider);
    }

    public static boolean test(LegacyCondition condition, class_6903.class_7863 lookup) {
        return condition.test(provider(lookup));
    }

    /** a 1.21.1 call of condition.test(provider): a 1.21.1 condition answers itself, any other through the provider's lookup */
    public static boolean test(net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition condition, class_7225.class_7874 provider) {
        if (condition instanceof LegacyCondition legacy) {
            return legacy.test(provider);
        }
        return condition.test(provider == null ? null : CbRestHolderLookupAdapterAccessor.cb$new(provider));
    }
}
