package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.datafixers.util.Pair;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.Set;
import net.minecraft.class_1860;
import net.minecraft.class_8786;

/**
 * Recipe (class_1860) <-> RecipeHolder (class_8786) for the values whose erased type stayed at
 * 1.20.2 while their type arguments moved from the recipe to its holder: RecipeManager's lookups,
 * the recipe-sync packet, the recipe books, furnaces, campfires, the stonecutter. 1.20.1 code reads
 * and writes recipes there; 1.20.2 code reads and writes holders. The callReshapes / convertHooks
 * entries of those members (generic drift, core/segments/damage-generic) convert through here, in
 * both directions, and this is the one place the step converts them.
 *
 * <p>Ids come from {@link CbRecipeIds} alone (the id every recipe was loaded, synced or built with,
 * kept by recipe identity): {@link #holder} wraps a recipe as {@code new RecipeHolder(id, recipe)}.
 * That holder is equal to the one vanilla holds for the recipe -- RecipeHolder's equals and hashCode
 * compare the id alone (1.20.2 bytecode) -- so it is found in vanilla's sets and maps, and what
 * vanilla reads off it (id(), value()) is what 1.20.1 read off the recipe (getId(), the recipe).
 * {@link #recipe} answers a holder's recipe and remembers the holder's id with it, so the recipe's
 * 1.20.1 getId() keeps answering.
 *
 * <p>Every converter is adaptive -- erasure cannot tell who built a value, so a value already in the
 * wanted form passes through: holder() leaves a holder alone, recipe() a recipe, and a container
 * view converts element by element the same way. The two directions undo each other: a view handed
 * back the other way unwraps to the very container it was made from, and a view asked for its own
 * direction again is itself. Containers convert as:
 * <ul>
 * <li>Optional: a mapped copy (it is immutable; nothing can write through it);</li>
 * <li>List, Collection, Set, Iterable: live views over the original -- reads convert the original's
 *     element, writes (add / set / remove, through the view or its iterators) convert the element
 *     into the original's form and change the original. A collection 1.20.2 code keeps and changes
 *     (the recipe-sync packet's list, which getRecipes() answers as is in both releases) is changed
 *     the way 1.20.1 code changed it; one it copies (the packet constructor, RecipeCollection,
 *     replaceRecipes) is copied from the view. A view keeps the original's kind: a list stays a
 *     List (RandomAccess when the original is), a set a Set, anything else iterable an Iterable.</li>
 * </ul>
 * Lookups through a view (contains, indexOf, remove(Object)) compare the view's elements, so a
 * holder view finds a holder by id (RecipeHolder.equals) and a recipe view finds the recipe itself
 * (recipes compare by identity), as each side's own collections do.
 *
 * <p>Side-neutral: nothing here touches a client class, so server-loaded bridges use it as well.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbRecipeHolders {

    // ---------------------------------------------------------------- one value

    /** a 1.20.1 Recipe where 1.20.2 has its RecipeHolder: the recipe under its id; a holder (or null) passes */
    public static Object holder(Object value) {
        return value instanceof class_1860<?> recipe ? CbRecipeIds.entry(recipe) : value;
    }

    /** a 1.20.2 RecipeHolder where 1.20.1 had the Recipe: its recipe, the id kept with it; a recipe (or null) passes */
    public static Object recipe(Object value) {
        return value instanceof class_8786<?> holder ? CbRecipeIds.value(holder) : value;
    }

    // ---------------------------------------------------------------- Optional (a mapped copy)

    /** Optional of a recipe -> Optional of its holder (RecipeManager.getRecipeFor / byKey answered by a 1.20.1 override) */
    public static Optional holders(Optional value) {
        return value == null ? null : value.map(CbRecipeHolders::holder);
    }

    /** Optional of a holder -> Optional of its recipe (RecipeManager.getRecipeFor / byKey, CachedCheck.getRecipeFor) */
    public static Optional recipes(Optional value) {
        return value == null ? null : value.map(CbRecipeHolders::recipe);
    }

    /**
     * RecipeManager.getRecipeFor(type, inventory, level, lastId) answered by a 1.20.1 override:
     * Optional of Pair(id it was found under, recipe) -> the same pair holding the recipe's holder.
     * The pair's first stays what it was (the lookup's cache key in both releases).
     */
    public static Optional pairHolders(Optional value) {
        return value == null ? null : value.map(p -> p instanceof Pair<?, ?> pair ? Pair.of(pair.getFirst(), holder(pair.getSecond())) : p);
    }

    /** ... and back: 1.20.2's Optional of Pair(id, holder) as 1.20.1's Optional of Pair(id, recipe) */
    public static Optional pairRecipes(Optional value) {
        return value == null ? null : value.map(p -> p instanceof Pair<?, ?> pair ? Pair.of(pair.getFirst(), recipe(pair.getSecond())) : p);
    }

    // ---------------------------------------------------------------- live views

    /** a 1.20.1 list of recipes as 1.20.2's list of holders (a live view; a holder view of holders is itself) */
    public static List holders(List value) {
        return (List) list(value, true);
    }

    /** a 1.20.2 list of holders as 1.20.1's list of recipes (a live view) */
    public static List recipes(List value) {
        return (List) list(value, false);
    }

    /** a 1.20.1 collection of recipes as 1.20.2's collection of holders (a live view; a list stays a List, a set a Set) */
    public static Collection holders(Collection value) {
        return collection(value, true);
    }

    /** a 1.20.2 collection of holders as 1.20.1's collection of recipes (a live view) */
    public static Collection recipes(Collection value) {
        return collection(value, false);
    }

    /** a 1.20.1 iterable of recipes as 1.20.2's iterable of holders (a live view, of the original's kind) */
    public static Iterable holders(Iterable value) {
        return iterable(value, true);
    }

    /** a 1.20.2 iterable of holders as 1.20.1's iterable of recipes (a live view) */
    public static Iterable recipes(Iterable value) {
        return iterable(value, false);
    }

    private static Iterable iterable(Iterable value, boolean holders) {
        if (value instanceof Collection<?> c) {
            return collection(c, holders);
        }
        Object known = known(value, holders);
        if (known != null || value == null) {
            return (Iterable) known;
        }
        return new IterableView(value, holders);
    }

    private static Collection collection(Collection value, boolean holders) {
        if (value instanceof List<?> l) {
            return (Collection) list(l, holders);
        }
        Object known = known(value, holders);
        if (known != null || value == null) {
            return (Collection) known;
        }
        return value instanceof Set ? new SetView(value, holders) : new CollectionView(value, holders);
    }

    private static Object list(List value, boolean holders) {
        Object known = known(value, holders);
        if (known != null || value == null) {
            return known;
        }
        return value instanceof RandomAccess ? new RandomAccessListView(value, holders) : new ListView(value, holders);
    }

    /** a view asked for its own direction is itself; asked for the other, it unwraps to its original */
    private static Object known(Object value, boolean holders) {
        if (value instanceof View v) {
            return v.holders() == holders ? value : v.base();
        }
        return null;
    }

    /** reads: the original's element in the view's form */
    static Object out(Object element, boolean holders) {
        return holders ? holder(element) : recipe(element);
    }

    /** writes: the view's element in the original's form */
    static Object in(Object element, boolean holders) {
        return holders ? recipe(element) : holder(element);
    }

    /** a container presenting another's elements converted: holders() true shows a recipe container's holders */
    private interface View {
        Object base();

        boolean holders();
    }

    private static final class IterableView implements Iterable<Object>, View {
        private final Iterable<Object> base;
        private final boolean holders;

        IterableView(Iterable<Object> base, boolean holders) {
            this.base = base;
            this.holders = holders;
        }

        @Override
        public Object base() {
            return this.base;
        }

        @Override
        public boolean holders() {
            return this.holders;
        }

        @Override
        public Iterator<Object> iterator() {
            return new ViewIterator(this.base.iterator(), this.holders);
        }
    }

    private static final class ViewIterator implements Iterator<Object> {
        private final Iterator<Object> base;
        private final boolean holders;

        ViewIterator(Iterator<Object> base, boolean holders) {
            this.base = base;
            this.holders = holders;
        }

        @Override
        public boolean hasNext() {
            return this.base.hasNext();
        }

        @Override
        public Object next() {
            return out(this.base.next(), this.holders);
        }

        @Override
        public void remove() {
            this.base.remove();
        }
    }

    private static class CollectionView extends AbstractCollection<Object> implements View {
        final Collection<Object> base;
        final boolean holders;

        CollectionView(Collection<Object> base, boolean holders) {
            this.base = base;
            this.holders = holders;
        }

        @Override
        public Object base() {
            return this.base;
        }

        @Override
        public boolean holders() {
            return this.holders;
        }

        @Override
        public Iterator<Object> iterator() {
            return new ViewIterator(this.base.iterator(), this.holders);
        }

        @Override
        public int size() {
            return this.base.size();
        }

        @Override
        public boolean isEmpty() {
            return this.base.isEmpty();
        }

        @Override
        public boolean add(Object e) {
            return this.base.add(in(e, this.holders));
        }

        @Override
        public void clear() {
            this.base.clear();
        }
    }

    /** a view of a Set is a Set (equals / hashCode per the Set contract, over the view's elements) */
    private static final class SetView extends CollectionView implements Set<Object> {
        SetView(Collection<Object> base, boolean holders) {
            super(base, holders);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Set<?> other) || other.size() != this.size()) {
                return false;
            }
            return this.containsAll(other);
        }

        @Override
        public int hashCode() {
            int h = 0;
            for (Object e : this) {
                h += e == null ? 0 : e.hashCode();
            }
            return h;
        }
    }

    private static class ListView extends AbstractList<Object> implements View {
        final List<Object> base;
        final boolean holders;

        ListView(List<Object> base, boolean holders) {
            this.base = base;
            this.holders = holders;
        }

        @Override
        public Object base() {
            return this.base;
        }

        @Override
        public boolean holders() {
            return this.holders;
        }

        @Override
        public Object get(int index) {
            return out(this.base.get(index), this.holders);
        }

        @Override
        public Object set(int index, Object element) {
            return out(this.base.set(index, in(element, this.holders)), this.holders);
        }

        @Override
        public void add(int index, Object element) {
            this.base.add(index, in(element, this.holders));
        }

        @Override
        public Object remove(int index) {
            return out(this.base.remove(index), this.holders);
        }

        @Override
        public int size() {
            return this.base.size();
        }

        @Override
        public boolean isEmpty() {
            return this.base.isEmpty();
        }

        @Override
        public void clear() {
            this.base.clear();
        }

        @Override
        public Iterator<Object> iterator() {
            return this.listIterator(0);
        }

        @Override
        public ListIterator<Object> listIterator(int index) {
            ListIterator<Object> it = this.base.listIterator(index);
            boolean h = this.holders;
            return new ListIterator<>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Object next() {
                    return out(it.next(), h);
                }

                @Override
                public boolean hasPrevious() {
                    return it.hasPrevious();
                }

                @Override
                public Object previous() {
                    return out(it.previous(), h);
                }

                @Override
                public int nextIndex() {
                    return it.nextIndex();
                }

                @Override
                public int previousIndex() {
                    return it.previousIndex();
                }

                @Override
                public void remove() {
                    it.remove();
                }

                @Override
                public void set(Object e) {
                    it.set(in(e, h));
                }

                @Override
                public void add(Object e) {
                    it.add(in(e, h));
                }
            };
        }
    }

    private static final class RandomAccessListView extends ListView implements RandomAccess {
        RandomAccessListView(List<Object> base, boolean holders) {
            super(base, holders);
        }
    }

    private CbRecipeHolders() {
    }
}
