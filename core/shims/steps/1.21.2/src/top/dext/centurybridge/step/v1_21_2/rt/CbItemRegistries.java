package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2385;
import net.minecraft.class_2960;
import net.minecraft.class_3503;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7225;
import net.minecraft.class_7871;
import net.minecraft.class_7876;

/**
 * 1.21.1's Registry / MappedRegistry / RegistryAccess / HolderGetter.Provider / HolderLookup.Provider
 * surface on 1.21.2 (wiring: core/tools/gen_s7item_registries.py).
 *
 * <p>1.21.2 folded Registry into HolderLookup.RegistryLookup. A 1.21.1 member falls in one of two kinds:
 * <ul>
 * <li>it lives on under another member of the same meaning -- key() / registryLifecycle() are the
 *     lookup's, getHolder(key) is get(key), getHolder(id) get(id), holders() listElements(), getTag(tag)
 *     get(tag), getTagNames() listTagIds(), get(id) getValue(id), RegistryAccess.registry(key) lookup(key),
 *     HolderGetter.Provider.lookup(key) lookup(key), RegistryAccess.allRegistriesLifecycle() the
 *     provider's. A call goes to the 1.21.2 member (the statics below); a mod's override answers the
 *     1.21.2 member (an override hook gives the class the new shape forwarding to it, its super call
 *     becoming super's new shape);</li>
 * <li>it is gone -- getOrCreateTag, bindTags, resetTags, holderOwner, asLookup, asTagAddingLookup,
 *     getHolderOrThrow, HolderGetter.Provider.get(registry, key), HolderLookup.Provider.asGetterLookup. A
 *     call takes the static, which answers what 1.21.1's did on 1.21.2's API -- unless the receiver's
 *     class overrides the member (an adapter hook gives such a class the Legacy interface): then its own
 *     method answers, as virtual dispatch did in 1.21.1. Its super call takes the super_ static: 1.21.1's
 *     body, without the dispatch.</li>
 * </ul>
 * What 1.21.2 asks of a 1.21.1 implementation that has no 1.21.1 counterpart is filled (abstractFills):
 * prepareTagReload and WritableRegistry.bindTag through the class's bindTags, HolderSet.isBound (a 1.21.1
 * holder set always had its contents), listTags from getTags.
 */
public final class CbItemRegistries {

    // ---- members that live on under another name: calls ----

    /** get(id): the value (1.21.2's getValue(id); its get(id) answers the holder) */
    public static Object method_10223(class_2378<?> self, class_2960 id) {
        return self.method_63535(id);
    }

    /** key() */
    @SuppressWarnings("rawtypes")
    public static class_5321 method_30517(class_2378<?> self) {
        return self.method_46765();
    }

    /** registryLifecycle() */
    public static Lifecycle method_31138(class_2378<?> self) {
        return self.method_46766();
    }

    /** getHolder(key) -> get(key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_40264(class_2378 self, class_5321 key) {
        return self.method_46746(key);
    }

    /** getHolder(id) -> get(id) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_55841(class_2378 self, class_2960 id) {
        return self.method_10223(id);
    }

    /** holders() -> listElements() */
    @SuppressWarnings("rawtypes")
    public static Stream method_40270(class_2378 self) {
        return self.method_42017();
    }

    /** getTag(tag) -> get(tag) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_40266(class_2378 self, class_6862 tag) {
        return self.method_46733(tag);
    }

    /** getTagNames() -> listTagIds() */
    @SuppressWarnings("rawtypes")
    public static Stream method_40273(class_2378 self) {
        return self.method_46755();
    }

    /** a 1.21.1 RegistryAccess implementation's registry(key) (adapterHooks: a class declaring it) */
    public interface LegacyRegistry {
        @SuppressWarnings("rawtypes")
        Optional method_33310(class_5321 key);
    }

    /**
     * RegistryAccess.registry(key) -> lookup(key); on an object whose class declares its own 1.21.1
     * registry(key), that method (1.21.1 dispatched to it; such a class's lookup, synthesized by the
     * override hook when it had none, forwards to it as well)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_33310(class_5455 self, class_5321 key) {
        return self instanceof LegacyRegistry l ? l.method_33310(key) : self.method_46759(key);
    }

    /** RegistryAccess.allRegistriesLifecycle() -> the provider's */
    public static Lifecycle method_41201(class_5455 self) {
        return self.method_62679();
    }

    /** HolderGetter.Provider.lookup(key) -> lookup(key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_46750(class_7871.class_7872 self, class_5321 key) {
        return self.method_46759(key);
    }

    // ---- Registry.getTags(): pairs in 1.21.1, the sets in 1.21.2 (generic drift) ----

    /** getTags() as 1.21.1 answered it, from 1.21.2's stream of sets (callReshapes ret) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Stream tagPairs(Stream sets) {
        return sets.map(s -> s instanceof class_6885.class_6888 n ? Pair.of(n.method_40251(), n) : s);
    }

    /** what 1.21.2 reads from a mod's getTags() override: its pairs' sets (convertHooks ret) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Stream tagSets(Stream pairs) {
        return pairs.map(p -> p instanceof Pair pair ? pair.getSecond() : p);
    }

    // ---- members that are gone: a mod's override answers (Legacy*), else 1.21.1's body on 1.21.2 ----

    public interface LegacyGetOrCreateTag<T> {
        class_6885.class_6888<T> method_40260(class_6862<T> tag);
    }

    public interface LegacyBindTags<T> {
        void method_40257(Map<class_6862<T>, List<class_6880<T>>> tags);
    }

    public interface LegacyResetTags {
        void method_40278();
    }

    public interface LegacyHolderOwner<T> {
        class_7876<T> method_46770();
    }

    public interface LegacyAsLookup<T> {
        class_7225.class_7226<T> method_46771();
    }

    public interface LegacyAsTagAddingLookup<T> {
        class_7225.class_7226<T> method_46772();
    }

    public interface LegacyGetHolderOrThrow<T> {
        class_6880.class_6883<T> method_40290(class_5321<T> key);
    }

    public interface LegacyProviderGet {
        <T> Optional<class_6880.class_6883<T>> method_58561(class_5321<? extends class_2378<? extends T>> registry, class_5321<T> key);
    }

    public interface LegacyAsGetterLookup {
        class_7871.class_7872 method_46758();
    }

    /** getOrCreateTag(tag) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6885.class_6888 method_40260(class_2378 self, class_6862 tag) {
        return self instanceof LegacyGetOrCreateTag l ? l.method_40260(tag) : super_method_40260(self, tag);
    }

    /**
     * 1.21.1's getOrCreateTag: the bound set when the tag is bound; before the registry froze, the set the
     * registry creates for registration (1.21.2's getOrCreateTagForRegistration, as 1.21.1 created one);
     * once frozen, an empty named set of the registry, what 1.21.1 answered for a tag no data defined
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6885.class_6888 super_method_40260(class_2378 self, class_6862 tag) {
        Optional<class_6885.class_6888> bound = self.method_46733(tag);
        if (bound.isPresent()) {
            return bound.get();
        }
        if (self instanceof class_2370 mapped && !((Frozen) mapped).cb$frozen()) {
            return ((Frozen) mapped).cb$tagForRegistration(tag);
        }
        return class_6885.method_45924((class_7876) self, tag);
    }

    // ---- bindTags as MappedRegistry's restored member, on 1.21.2's tag reload path ----

    /**
     * The PendingTags whose apply() CbItemRegistryPendingTagsMixin is handing to the registry's bindTags right now
     * (vanilla's reload: ReloadableServerResources / the client's tag collector apply each registry's load result).
     */
    private static final ThreadLocal<Object> ROUTED = new ThreadLocal<>();

    /** the PendingTags whose apply() runs as 1.21.2 wrote it (the route steps aside) */
    private static final ThreadLocal<Object> DIRECT = new ThreadLocal<>();

    /** MappedRegistry's anonymous PendingTags (class_2370$3): its registry, load result and named sets */
    public interface PendingTagsAccess {
        class_2370<?> cb$registry();

        @SuppressWarnings("rawtypes")
        Map cb$pending();

        @SuppressWarnings("rawtypes")
        com.google.common.collect.ImmutableMap cb$named();

        @SuppressWarnings("rawtypes")
        void cb$setNamed(com.google.common.collect.ImmutableMap named);
    }

    /** MappedRegistry's private createTag(tag) (CbItemRegistryFrozenAccess) */
    public interface CreateTag {
        @SuppressWarnings("rawtypes")
        class_6885.class_6888 cb$createTag(class_6862 tag);
    }

    /**
     * PendingTags.apply() at its head: whether 1.21.2's own body runs (true), or the bind went through the
     * registry's bindTags(map) -- 1.21.1's read path (ReloadableServerResources.updateRegistryTags and the client's
     * TagCollector called registry.bindTags(load result)) -- which then applied it (false).
     */
    public static boolean applyRuns(Object pending) {
        if (DIRECT.get() == pending || Boolean.getBoolean("cb.step1_21_2.noTagRoute")) {
            return true; // the measurement switch: 1.21.2's reload with no route, for comparison
        }
        PendingTagsAccess p = (PendingTagsAccess) pending;
        ROUTED.set(pending);
        try {
            ((LegacyBindTags) p.cb$registry()).method_40257(p.cb$pending());
        } finally {
            if (ROUTED.get() == pending) {
                ROUTED.remove();
            }
        }
        return false;
    }

    /**
     * MappedRegistry.bindTags(map), the restored member's body. On vanilla's reload path (the routed PendingTags of
     * this registry) the load result is applied as 1.21.2 does -- the same PendingTags, so the named sets its lookup
     * already handed out are the ones bound; when a mod's mixin on bindTags changed the map (lists replaced, tags
     * added or dropped), its map becomes the load result first, and tags it added get named sets of their own.
     * Called any other way (a mod's own call), 1.21.1's bindTags on 1.21.2 ({@link #super_method_40257}).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void bindTags(class_2370 self, Map tags) {
        Object cur = ROUTED.get();
        if (cur instanceof PendingTagsAccess p && p.cb$registry() == self) {
            ROUTED.remove();
            Map pending = p.cb$pending();
            if (tags != pending) {
                Map copy = new java.util.HashMap(tags);
                pending.clear();
                pending.putAll(copy);
            }
            com.google.common.collect.ImmutableMap named = p.cb$named();
            if (!named.keySet().containsAll(pending.keySet())) {
                com.google.common.collect.ImmutableMap.Builder b = com.google.common.collect.ImmutableMap.builder();
                b.putAll(named);
                for (Object k : pending.keySet()) {
                    if (!named.containsKey(k)) {
                        b.put(k, ((CreateTag) self).cb$createTag((class_6862) k));
                    }
                }
                p.cb$setNamed(b.build());
            }
            applyDirect(cur);
            return;
        }
        super_method_40257(self, tags);
    }

    private static void applyDirect(Object pending) {
        Object was = DIRECT.get();
        DIRECT.set(pending);
        try {
            ((class_2378.class_10106<?>) pending).method_62696();
        } finally {
            DIRECT.set(was);
        }
    }

    /** bindTags(map) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_40257(class_2378 self, Map tags) {
        if (self instanceof LegacyBindTags l) {
            l.method_40257(tags);
        } else {
            super_method_40257(self, tags);
        }
    }

    /**
     * 1.21.1's bindTags: every tag of the map bound to its holders, every other known tag to none, the
     * holders' tag sets rebuilt -- 1.21.2's binding of a load result (prepareTagReload(..).apply()) on a
     * frozen registry; before it froze, each tag through WritableRegistry.bindTag
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void super_method_40257(class_2378 self, Map tags) {
        if (self instanceof class_2370 mapped && !((Frozen) mapped).cb$frozen()) {
            tags.forEach((k, v) -> ((class_2385) mapped).method_62681((class_6862) k, (List) v));
            return;
        }
        // applied as 1.21.2 does, past the route: a subclass's super.bindTags must not come back to its override
        applyDirect(self.method_62683(new class_3503.class_6863(self.method_46765(), tags)));
    }

    /** resetTags() */
    public static void method_40278(class_2378<?> self) {
        if (self instanceof LegacyResetTags l) {
            l.method_40278();
        } else {
            super_method_40278(self);
        }
    }

    /** 1.21.1's resetTags: every known tag bound to nothing (1.21.2: bindAllTagsToEmpty before freezing, an empty load result after) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void super_method_40278(class_2378<?> self) {
        if (self instanceof class_2370<?> mapped && !((Frozen) mapped).cb$frozen()) {
            mapped.method_62691();
        } else {
            super_method_40257((class_2378) self, Map.of());
        }
    }

    /** holderOwner() */
    @SuppressWarnings("rawtypes")
    public static class_7876 method_46770(class_2378 self) {
        return self instanceof LegacyHolderOwner l ? l.method_46770() : super_method_46770(self);
    }

    /** 1.21.1's holderOwner: 1.21.2's registry owns its holders itself */
    @SuppressWarnings("rawtypes")
    public static class_7876 super_method_46770(class_2378 self) {
        return self;
    }

    /** asLookup() */
    @SuppressWarnings("rawtypes")
    public static class_7225.class_7226 method_46771(class_2378 self) {
        return self instanceof LegacyAsLookup l ? l.method_46771() : super_method_46771(self);
    }

    /** 1.21.1's asLookup: 1.21.2's registry is its lookup */
    @SuppressWarnings("rawtypes")
    public static class_7225.class_7226 super_method_46771(class_2378 self) {
        return self;
    }

    /** asTagAddingLookup() */
    @SuppressWarnings("rawtypes")
    public static class_7225.class_7226 method_46772(class_2378 self) {
        return self instanceof LegacyAsTagAddingLookup l ? l.method_46772() : super_method_46772(self);
    }

    /** 1.21.1's asTagAddingLookup: the registry's lookup, whose tag lookups create missing tags (getOrCreateTag) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_7225.class_7226 super_method_46772(class_2378 self) {
        return new class_7225.class_7226.class_7875() {
            @Override
            public class_7225.class_7226 method_46725() {
                return self;
            }

            @Override
            public Optional method_46733(class_6862 tag) {
                return Optional.of(method_40260(self, tag));
            }

            @Override
            public class_6885.class_6888 method_46735(class_6862 tag) {
                return method_40260(self, tag);
            }
        };
    }

    /** getHolderOrThrow(key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6880.class_6883 method_40290(class_2378 self, class_5321 key) {
        return self instanceof LegacyGetHolderOrThrow l ? l.method_40290(key) : super_method_40290(self, key);
    }

    /** 1.21.1's getHolderOrThrow: get(key), or IllegalStateException("Missing key in <registry>: <key>") */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6880.class_6883 super_method_40290(class_2378 self, class_5321 key) {
        Optional<class_6880.class_6883> h = self.method_46746(key);
        return h.orElseThrow(() -> new IllegalStateException("Missing key in " + self.method_46765() + ": " + key));
    }

    /** HolderGetter.Provider.get(registry, key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_58561(class_7871.class_7872 self, class_5321 registry, class_5321 key) {
        return self instanceof LegacyProviderGet l ? l.method_58561(registry, key) : super_method_58561(self, registry, key);
    }

    /** 1.21.1's HolderGetter.Provider.get(registry, key): lookup(registry).flatMap(get(key)) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional super_method_58561(class_7871.class_7872 self, class_5321 registry, class_5321 key) {
        return self.method_46759(registry).flatMap(l -> ((class_7871) l).method_46746(key));
    }

    /** HolderLookup.Provider.asGetterLookup() */
    public static class_7871.class_7872 method_46758(class_7225.class_7874 self) {
        return self instanceof LegacyAsGetterLookup l ? l.method_46758() : super_method_46758(self);
    }

    /** 1.21.1's asGetterLookup: the getters of the provider's lookups -- 1.21.2's provider is that getter provider */
    public static class_7871.class_7872 super_method_46758(class_7225.class_7874 self) {
        return self;
    }

    /**
     * a mod's RegistryAccess.lookup(key) override answered Optional&lt;RegistryLookup&gt; in 1.21.1; 1.21.2
     * reads Optional&lt;Registry&gt; (convertHooks ret, taking the mod object). A lookup a 1.21.1 mod got from
     * 1.21.1's API is a registry on 1.21.2 (asLookup answers the registry itself) and passes; any other
     * lookup stands for the registry of its key among the object's registries()
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional registryOf(Object self, Optional found) {
        if (found == null || found.isEmpty() || found.get() instanceof class_2378 || !(found.get() instanceof class_7225.class_7226 l)
                || !(self instanceof class_5455 access)) {
            return found;
        }
        class_5321 key = l.method_46765();
        return access.method_40311().filter(e -> e.comp_350().equals(key)).findFirst().map(e -> e.comp_351());
    }

    /** a 1.21.1 HolderGetter.Provider lambda (its SAM: lookup under the 1.21.1 name) */
    public interface LegacyProviderLambda {
        @SuppressWarnings("rawtypes")
        Optional method_46750(class_5321 key);
    }

    /** the 1.21.2 HolderGetter.Provider of a 1.21.1 lambda (lambdaAdapters) */
    public static class_7871.class_7872 provider(LegacyProviderLambda legacy) {
        return new class_7871.class_7872() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Optional method_46759(class_5321 key) {
                return legacy.method_46750(key);
            }
        };
    }

    // ---- what 1.21.2 asks of a 1.21.1 implementation with no 1.21.1 counterpart (abstractFills) ----

    /** prepareTagReload(result): the class's 1.21.1 bindTags(map) applied when 1.21.2 applies the result */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_2378.class_10106 fill_method_62683(class_2378 self, class_3503.class_6863 result) {
        Map tags = result.comp_329();
        return new class_2378.class_10106() {
            @Override
            public class_5321 method_62693() {
                return result.comp_328();
            }

            @Override
            public class_7225.class_7226 method_62695() {
                return self;
            }

            @Override
            public void method_62696() {
                method_40257(self, tags);
            }

            @Override
            public int method_64925() {
                return tags.size();
            }
        };
    }

    /** WritableRegistry.bindTag(tag, holders): the class's 1.21.1 bindTags of that tag */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void fill_method_62681(class_2385 self, class_6862 tag, List holders) {
        method_40257(self, Map.of(tag, holders));
    }

    /** HolderSet.isBound(): a 1.21.1 holder set always had its contents */
    public static boolean fill_method_62680(class_6885<?> self) {
        return true;
    }

    /** listTags(): the sets of the class's getTags() (its override bridged to sets, convertHooks) */
    @SuppressWarnings("rawtypes")
    public static Stream fill_method_42020(class_2378 self) {
        return self.method_40272();
    }

    /** MappedRegistry's frozen flag and its registration-time tag creation (CbItemRegistryMixin) */
    public interface Frozen {
        boolean cb$frozen();

        @SuppressWarnings("rawtypes")
        class_6885.class_6888 cb$tagForRegistration(class_6862 tag);
    }

    private CbItemRegistries() {
    }
}
