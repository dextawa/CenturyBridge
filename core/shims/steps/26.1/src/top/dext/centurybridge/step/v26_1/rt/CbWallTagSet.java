package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

/**
 * A tag standing where 26.1 holds a HolderSet but 1.21.11 code gave a TagKey before any registries were loaded
 * (an item's BlocksAttacks.bypassedBy, made with the item): 1.21.11 looked the tag up when it was used, so this
 * set is the tag's set in the registries in use whenever it is asked (CbWallRegistries.access), and names the
 * tag (unwrap / unwrapKey: it encodes as the tag, as 1.21.11's TagKey did). Equal by tag.
 */
public final class CbWallTagSet<T> implements HolderSet<T> {

    private final TagKey<T> tag;

    public CbWallTagSet(TagKey<T> tag) {
        this.tag = tag;
    }

    /** a 1.21.11 tag as 26.1's holder set: the registries' named set when they are loaded, else this lazy one */
    public static <T> HolderSet<T> of(TagKey<T> tag) {
        var access = CbWallRegistries.accessOrNull();
        return access != null ? access.getOrThrow(tag) : new CbWallTagSet<>(tag);
    }

    private HolderSet.Named<T> set() {
        return CbWallRegistries.access().getOrThrow(this.tag);
    }

    @Override
    public Stream<Holder<T>> stream() {
        return set().stream();
    }

    @Override
    public int size() {
        return set().size();
    }

    @Override
    public boolean isBound() {
        var access = CbWallRegistries.accessOrNull();
        return access != null && access.getOrThrow(this.tag).isBound();
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.left(this.tag);
    }

    @Override
    public Optional<Holder<T>> getRandomElement(RandomSource random) {
        return set().getRandomElement(random);
    }

    @Override
    public Holder<T> get(int index) {
        return set().get(index);
    }

    @Override
    public boolean contains(Holder<T> holder) {
        return holder.is(this.tag);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        return set().canSerializeIn(owner);
    }

    @Override
    public Optional<TagKey<T>> unwrapKey() {
        return Optional.of(this.tag);
    }

    @Override
    public Iterator<Holder<T>> iterator() {
        return set().iterator();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof CbWallTagSet<?> t && this.tag.equals(t.tag);
    }

    @Override
    public int hashCode() {
        return this.tag.hashCode();
    }

    @Override
    public String toString() {
        return "CbWallTagSet[" + this.tag + "]";
    }
}
