package top.dext.centurybridge.step.v1_21_2.rt;

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
 * 1.21.1's Registry / MappedRegistry / RegistryAccess / HolderGetter.Provider surface on 1.21.2.
 *
 * <p>1.21.2 folded Registry into HolderLookup.RegistryLookup: key() and registryLifecycle() are the
 * lookup's (pure renames, methodRenames), getHolder(key) is get(key), getHolder(id) is get(id),
 * holders() listElements(), getTag(tag) get(tag), get(id) getValue(id) (get(id) now answers the holder),
 * holderOwner() / asLookup() the registry itself (it is the lookup and the owner). Tags are bound in one
 * step from a loaded TagLoader result (prepareTagReload(..).apply()); a named HolderSet without a bound
 * list throws. What 1.21.1 did with a tag that was not loaded:
 * <ul>
 * <li>getOrCreateTag(tag): an unbound named set, created and remembered; on 1.21.2 the registry still has
 *     getOrCreateTagForRegistration (before freezing) and answers bound sets after binding; for a tag
 *     no data defines once frozen, 1.21.1's set was empty -- an empty named set of the registry;</li>
 * <li>getTagNames(): the tags known; on 1.21.2 the bound sets' keys (listTags);</li>
 * <li>getTags(): pairs of key and set; on 1.21.2 a stream of the sets (the generic-drift row), turned
 *     back into pairs here;</li>
 * <li>bindTags(map) / resetTags(): binding tags from outside the tag loader; 1.21.2 does it through
 *     prepareTagReload(new TagLoader.LoadResult(key, map)).apply(), which this calls; resetTags binds every
 *     known tag to the empty list (1.21.2's bindAllTagsToEmpty, the registry's own reset).</li>
 * </ul>
 */
public final class CbItemRegistries {

    // ---- calls (instanceRedirects on Registry; MappedRegistry / DefaultedRegistry calls find them by hierarchy) ----

    /** get(id) -> the value (1.21.2: getValue(id)) */
    public static Object method_10223(class_2378<?> self, class_2960 id) {
        return self.method_63535(id);
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

    /** getHolderOrThrow(key) -> getOrThrow(key) (the same "Missing element" failure) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6880.class_6883 method_40290(class_2378 self, class_5321 key) {
        return self.method_46747(key);
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

    /**
     * getOrCreateTag(tag): the bound set when the tag is bound; before the registry froze, the set the
     * registry creates for registration (1.21.2's getOrCreateTagForRegistration, as 1.21.1 created one);
     * otherwise an empty named set, what 1.21.1's answered for a tag no data defined
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6885.class_6888 method_40260(class_2378 self, class_6862 tag) {
        Optional<class_6885.class_6888> bound = self.method_46733(tag);
        if (bound.isPresent()) {
            return bound.get();
        }
        if (self instanceof class_2370 mapped && !((Frozen) mapped).cb$frozen()) {
            return ((Frozen) mapped).cb$tagForRegistration(tag);
        }
        class_6885.class_6888 empty = class_6885.method_45924((class_7876) self, tag);
        return empty;
    }

    /** getTagNames() -> listTagIds() */
    @SuppressWarnings("rawtypes")
    public static Stream method_40273(class_2378 self) {
        return self.method_46755();
    }

    /** getTags() as 1.21.1 answered it: pairs of key and set */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Stream tagPairs(Stream sets) {
        return sets.map(s -> s instanceof class_6885.class_6888 n ? com.mojang.datafixers.util.Pair.of(n.method_40251(), n) : s);
    }

    /** bindTags(map): 1.21.2's binding of a tag load result */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_40257(class_2378 self, Map tags) {
        self.method_62683(new class_3503.class_6863(self.method_46765(), tags)).method_62696();
    }

    /** resetTags(): every known tag bound to nothing (1.21.2's bindAllTagsToEmpty) */
    public static void method_40278(class_2378<?> self) {
        if (self instanceof class_2370<?> mapped) {
            mapped.method_62691();
        } else {
            method_40257(self, Map.of());
        }
    }

    /** holderOwner(): the registry is the owner of its holders */
    @SuppressWarnings("rawtypes")
    public static class_7876 method_46770(class_2378 self) {
        return self;
    }

    /** asLookup(): the registry is its lookup */
    @SuppressWarnings("rawtypes")
    public static class_7225.class_7226 method_46771(class_2378 self) {
        return self;
    }

    /**
     * asTagAddingLookup(): a lookup over the registry that creates missing tags (1.21.1: getOrCreateTag) --
     * before the registry froze, as 1.21.2's registration lookup does
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_7225.class_7226 method_46772(class_2378 self) {
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

    /** RegistryAccess.registry(key) -> lookup(key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_33310(class_5455 self, class_5321 key) {
        return self.method_46759(key);
    }

    /** HolderGetter.Provider.get(registry, key) -> lookup(registry).flatMap(get(key)), 1.21.1's default */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_58561(class_7871.class_7872 self, class_5321 registry, class_5321 key) {
        return self.method_46759(registry).flatMap(l -> ((class_7871) l).method_46746(key));
    }

    /** HolderLookup.Provider.asGetterLookup(): the provider is a getter provider (1.21.2) */
    public static class_7871.class_7872 method_46758(class_7225.class_7874 self) {
        return self;
    }

    // ---- overrides (abstractFills: what 1.21.2 asks of a 1.21.1 Registry implementation) ----

    /** the 1.21.1 shapes a mod's registry implements, reached by the fills below */
    public interface Legacy<T> {
        T method_10223(class_2960 id);

        class_5321<? extends class_2378<T>> method_30517();

        com.mojang.serialization.Lifecycle method_31138();

        Optional<class_6880.class_6883<T>> method_40264(class_5321<T> key);

        Optional<class_6880.class_6883<T>> method_55841(class_2960 id);

        Stream<class_6880.class_6883<T>> method_40270();

        Optional<class_6885.class_6888<T>> method_40266(class_6862<T> tag);

        Stream<com.mojang.datafixers.util.Pair<class_6862<T>, class_6885.class_6888<T>>> method_40272();
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
