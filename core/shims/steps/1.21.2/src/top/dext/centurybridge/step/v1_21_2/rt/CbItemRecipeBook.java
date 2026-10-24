package top.dext.centurybridge.step.v1_21_2.rt;

import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_3439;
import net.minecraft.class_3441;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.1's RecipeBook kept the known and highlighted recipes as ids (Sets of ResourceLocation, protected fields
 * {@code known} / {@code highlight}) and answered add / contains / remove / willHighlight / addHighlight /
 * removeHighlight / copyOverData from them; 1.21.2's RecipeBook keeps only the book settings, the sets moved to
 * ServerRecipeBook as ResourceKeys. CbItemRecipeBookMixin gives RecipeBook back 1.21.1's fields and methods, bodies
 * 1.21.1's: on a ServerRecipeBook the fields are live id views of its key sets ({@link #known} / {@link #highlight}),
 * so 1.21.1's methods change what 1.21.2's server book holds; on any other RecipeBook (a mod's own, the client's)
 * they are plain sets, as 1.21.1's were.
 */
public final class CbItemRecipeBook {
    private CbItemRecipeBook() {
    }

    /** the field 1.21.1's RecipeBook constructor initialised {@code known} with, for this book */
    public static Set<class_2960> known(class_3439 book) {
        return book instanceof class_3441 server ? new KeyView(() -> server.field_54550) : new HashSet<>();
    }

    /** the same for {@code highlight} */
    public static Set<class_2960> highlight(class_3439 book) {
        return book instanceof class_3441 server ? new KeyView(() -> server.field_54551) : new HashSet<>();
    }

    /** a recipe id as 1.21.2's ResourceKey */
    public static class_5321<class_1860<?>> key(class_2960 id) {
        return class_5321.method_29179(class_7924.field_52178, id);
    }

    // ---- ServerRecipeBook ----

    private static volatile WeakReference<MinecraftServer> server = new WeakReference<>(null);

    /** the running server (CbItemInit: ServerLifecycleEvents) */
    static void server(MinecraftServer s) {
        server = new WeakReference<>(s);
    }

    static void stopped(MinecraftServer s) {
        if (server.get() == s) {
            server = new WeakReference<>(null);
        }
    }

    /**
     * What 1.21.1's ServerRecipeBook() constructor gives 1.21.2's ServerRecipeBook(displayResolver): the recipe
     * displays the running server's recipe manager lists for a recipe -- the resolver 1.21.2's ServerPlayer builds
     * its book with. A 1.21.1 ServerRecipeBook knew no displays; it is asked only when the book sends recipes to a
     * player (addRecipes / removeRecipes / sendInitialRecipeBook), on the server.
     */
    public static class_3441.class_10271 displayResolver() {
        return (key, output) -> {
            MinecraftServer s = server.get();
            if (s == null) {
                throw new IllegalStateException("a 1.21.1 ServerRecipeBook sending recipes with no server running");
            }
            s.method_3772().method_64679(key, output);
        };
    }

    /**
     * 1.21.1 ServerRecipeBook.fromNbt(tag, recipeManager): the known and highlighted recipes of the tag that the
     * recipe manager has (an unknown one logged and skipped) -- 1.21.2's fromNbt with the recipe manager's byKey as
     * the test.
     */
    public static void fromNbt(class_3441 book, class_2487 tag, class_1863 recipes) {
        book.method_14901(tag, key -> recipes.method_8130(key).isPresent());
    }

    /** a mod ServerRecipeBook declaring 1.21.1's fromNbt(tag, recipeManager) */
    public interface LegacyFromNbt {
        void method_14901(class_2487 tag, class_1863 recipes);
    }

    /** the 1.21.2 shape past such an override (its super call goes through {@link #superFromNbtBridge}) */
    public interface SuperFromNbt {
        void cb$super$method_14901(class_2487 tag, Predicate<class_5321<class_1860<?>>> recognised);
    }

    /**
     * 1.21.2's fromNbt(tag, recognised) of a mod ServerRecipeBook overriding 1.21.1's: the override with the running
     * server's recipe manager -- the one 1.21.2's ServerPlayer builds the test from.
     */
    public static void fromNbtBridge(LegacyFromNbt self, class_2487 tag, Predicate<class_5321<class_1860<?>>> recognised) {
        MinecraftServer s = server.get();
        if (s == null) {
            throw new IllegalStateException("a 1.21.1 ServerRecipeBook.fromNbt with no server running");
        }
        self.method_14901(tag, s.method_3772());
    }

    /** a mod ServerRecipeBook's super.fromNbt(tag, recipeManager): 1.21.2's body past the mod's own 1.21.2 shape */
    public static void superFromNbtBridge(SuperFromNbt self, class_2487 tag, class_1863 recipes) {
        self.cb$super$method_14901(tag, key -> recipes.method_8130(key).isPresent());
    }

    /**
     * A set of recipe ids over a ServerRecipeBook's set of recipe keys, read when used: the book's fields are set
     * after RecipeBook's constructor (where 1.21.1's fields are initialised) returns.
     */
    static final class KeyView extends AbstractSet<class_2960> {
        private final Supplier<Set<class_5321<class_1860<?>>>> keys;

        KeyView(Supplier<Set<class_5321<class_1860<?>>>> keys) {
            this.keys = keys;
        }

        @Override
        public Iterator<class_2960> iterator() {
            Iterator<class_5321<class_1860<?>>> it = keys.get().iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public class_2960 next() {
                    return it.next().method_29177();
                }

                @Override
                public void remove() {
                    it.remove();
                }
            };
        }

        @Override
        public int size() {
            return keys.get().size();
        }

        @Override
        public boolean contains(Object o) {
            return o instanceof class_2960 id && keys.get().contains(key(id));
        }

        @Override
        public boolean add(class_2960 id) {
            return keys.get().add(key(id));
        }

        @Override
        public boolean remove(Object o) {
            return o instanceof class_2960 id && keys.get().remove(key(id));
        }

        @Override
        public void clear() {
            keys.get().clear();
        }
    }
}
