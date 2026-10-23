package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7876;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import top.dext.centurybridge.step.v1_21_2.mixin.CbItemIngredientInvoker;

/**
 * 1.21.1's Ingredient surface on 1.21.2.
 *
 * <p>1.21.1 had an empty ingredient (EMPTY, of(), of() of no stacks), matching only the empty stack: the gaps of a
 * shaped pattern, the padding of getIngredients(). 1.21.2's factories refuse one (they throw "Ingredients can not
 * be empty") and use Optional.empty() where 1.21.1 put it. {@link #field_9017} is one Ingredient made through the
 * private constructor with an item set that holds nothing; the set presents itself as the tag centurybridge:empty,
 * which keeps the constructor's emptiness check (made for direct lists) off it. CbItemIngredientMixin makes it match
 * the empty stack. Where 1.21.2 takes Optional ingredients the recipe bridges turn it into Optional.empty().
 * getItems() / getStackingIds() / isEmpty() and the stack / tag factories are 1.21.1's on 1.21.2's item holders
 * (1.21.1's ingredient matched by item; its stacks' counts and components were never compared).
 */
public final class CbItemIngredients {

    private static volatile class_1856 empty;

    /** EMPTY */
    public static class_1856 field_9017() {
        class_1856 e = empty;
        if (e == null) {
            synchronized (CbItemIngredients.class) {
                if (empty == null) {
                    empty = CbItemIngredientInvoker.cb$create(new NoItems());
                }
                e = empty;
            }
        }
        return e;
    }

    /** whether an object is the empty ingredient (without creating it) */
    public static boolean isEmptyIngredient(Object ingredient) {
        return ingredient != null && ingredient == empty;
    }

    /** of(): EMPTY */
    public static class_1856 method_35226() {
        return field_9017();
    }

    /** isEmpty() */
    public static boolean method_8103(class_1856 ingredient) {
        return isEmptyIngredient(ingredient);
    }

    /** getItems(): a stack of each item */
    public static class_1799[] method_8105(class_1856 ingredient) {
        return ingredient.method_8105().stream().map(class_1799::new).toArray(class_1799[]::new);
    }

    /** getStackingIds(): the items' registry ids, sorted (1.21.1's StackedContents index) */
    public static IntList method_8100(class_1856 ingredient) {
        IntArrayList ids = new IntArrayList();
        for (class_6880<class_1792> h : ingredient.method_8105()) {
            ids.add(class_7923.field_41178.method_10206(h.comp_349()));
        }
        ids.sort(null);
        return ids;
    }

    /** of(stacks...): their items; EMPTY when there are none (empty stacks left out) */
    public static class_1856 method_8101(class_1799... stacks) {
        return method_26964(Stream.of(stacks));
    }

    /** of(items...): EMPTY for none, as 1.21.1 */
    public static class_1856 method_8091(class_1935... items) {
        return items.length == 0 ? field_9017() : class_1856.method_8091(items);
    }

    /**
     * ofStacks(stream): 1.21.1 passed stacks where 1.21.2 takes item likes (the same erased descriptor): each
     * element's item, the empty stacks left out; EMPTY when nothing remains
     */
    public static class_1856 method_26964(Stream<?> stream) {
        List<class_1935> items = stream.<class_1935>mapMulti((o, out) -> {
            if (o instanceof class_1799 s) {
                if (!s.method_7960()) {
                    out.accept(s.method_7909());
                }
            } else if (o instanceof class_1935 i) {
                out.accept(i);
            }
        }).toList();
        return items.isEmpty() ? field_9017() : class_1856.method_26964(items.stream());
    }

    /** of(tag) */
    public static class_1856 method_8106(class_6862<class_1792> tag) {
        return class_1856.method_8106(top.dext.centurybridge.step.v1_21_2.rt.CbItemTags.lookup(class_7923.field_41178).method_46735(tag));
    }

    /** an item set holding nothing, presenting itself as a tag (the empty ingredient's) */
    static final class NoItems implements class_6885<class_1792> {
        static final class_6862<class_1792> TAG = class_6862.method_40092(class_7924.field_41197,
            class_2960.method_60655("centurybridge", "empty"));

        @Override
        public Stream<class_6880<class_1792>> method_40239() {
            return Stream.empty();
        }

        @Override
        public int method_40247() {
            return 0;
        }

        @Override
        public boolean method_62680() {
            return true;
        }

        @Override
        public Either<class_6862<class_1792>, List<class_6880<class_1792>>> method_40248() {
            return Either.left(TAG);
        }

        @Override
        public Optional<class_6880<class_1792>> method_40243(class_5819 random) {
            return Optional.empty();
        }

        @Override
        public class_6880<class_1792> method_40240(int i) {
            throw new IndexOutOfBoundsException(i);
        }

        @Override
        public boolean method_40241(class_6880<class_1792> holder) {
            return false;
        }

        @Override
        public boolean method_46768(class_7876<class_1792> owner) {
            return true;
        }

        @Override
        public Optional<class_6862<class_1792>> method_45925() {
            return Optional.of(TAG);
        }

        @Override
        public Iterator<class_6880<class_1792>> iterator() {
            return Collections.emptyIterator();
        }
    }

    private CbItemIngredients() {
    }
}
