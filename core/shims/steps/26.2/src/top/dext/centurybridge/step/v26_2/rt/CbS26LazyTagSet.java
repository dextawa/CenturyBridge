package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

/**
 * A built-in registry's tag, read when asked. 26.1.2 code that hard-wired a tag (DripstoneUtils:
 * state.is(BlockTags.DRIPSTONE_REPLACEABLE)) consulted it at use; 26.2 asks for a HolderSet when the object is built,
 * and the registry's own named set (getOrThrow(tag)) throws "Tags not bound" until the server loads its tags, which a
 * 26.1.2 mod building the object at initialisation never met. Every member asks the registry's named set at the call,
 * so the answer is the tag as bound then, as 26.1.2's state.is(tag) was; unwrap / unwrapKey name the tag itself and so
 * encode as "#namespace:path" like the registry's set.
 */
public final class CbS26LazyTagSet<T> implements HolderSet<T> {
    private final Registry<T> registry;
    private final TagKey<T> key;

    public CbS26LazyTagSet(Registry<T> registry, TagKey<T> key) {
        this.registry = registry;
        this.key = key;
    }

    private HolderSet.Named<T> bound() {
        return registry.getOrThrow(key);
    }

    @Override public Stream<Holder<T>> stream() { return bound().stream(); }
    @Override public int size() { return bound().size(); }
    @Override public boolean isBound() { return bound().isBound(); }
    @Override public Either<TagKey<T>, List<Holder<T>>> unwrap() { return Either.left(key); }
    @Override public Optional<Holder<T>> getRandomElement(RandomSource random) { return bound().getRandomElement(random); }
    @Override public Holder<T> get(int index) { return bound().get(index); }
    @Override public boolean contains(Holder<T> holder) { return holder.is(key); }
    @Override public boolean canSerializeIn(HolderOwner<T> owner) { return bound().canSerializeIn(owner); }
    @Override public Optional<TagKey<T>> unwrapKey() { return Optional.of(key); }
    @Override public Iterator<Holder<T>> iterator() { return bound().iterator(); }

    @Override
    public boolean equals(Object o) {
        return o instanceof CbS26LazyTagSet<?> s && s.registry == registry && s.key.equals(key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "LazyTag[" + key + "]";
    }
}
