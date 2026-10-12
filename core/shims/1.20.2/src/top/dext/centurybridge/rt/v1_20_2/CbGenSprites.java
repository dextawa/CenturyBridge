package top.dext.centurybridge.rt.v1_20_2;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_7764;
import net.minecraft.class_7766;
import net.minecraft.class_7948;
import net.minecraft.class_8684;

/**
 * SpriteSourceList.list(resourceManager) (class_7947.method_47667) on 1.20.2: 1.20.1 answered a list of
 * Supplier&lt;SpriteContents&gt;, 1.20.2 a list of Function&lt;SpriteResourceLoader, SpriteContents&gt; --
 * the same erased List, so the elements are what changed (generic drift). The list 1.20.2 builds holds a
 * missing-texture function first and the sources' SpriteSuppliers (class_7948.class_7950, a Function in
 * 1.20.2, a Supplier in 1.20.1) after it, as 1.20.1's did.
 *
 * <p>A 1.20.2 function is read as a Supplier that applies it to the default sprite loader: the one
 * SpriteLoader.loadAndStitch(manager, atlas, mipLevel, executor) uses, built from
 * DEFAULT_METADATA_SECTIONS (the animation section alone) -- the sections 1.20.1 read a sprite with
 * (SpriteLoader.loadSprite read the animation section only). A SpriteSupplier read this way stays a
 * SpriteSupplier (a 1.20.1 mod may cast an element to it and call get() or discard(): get() reaches
 * {@link CbGuiSprites#get} and so this supplier, discard() the function's own). A 1.20.1 Supplier
 * handed the other way (an override's answer) is a Function that ignores the loader, as the supplier
 * took none.
 *
 * <p>Adaptive both ways: a value that is already a Supplier (a 1.20.1 one, or s1client's adapters,
 * which are both) passes as a Supplier, one that is already a Function passes as a Function; and the
 * two directions undo each other (an element wrapped one way unwraps when handed back, a list view
 * handed back is its original list). The lists are live views of the original (1.20.2's is an
 * ImmutableList, as 1.20.1's was, so writes fail the same way).
 */
@Environment(EnvType.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbGenSprites {

    /** 1.20.2's default sprite loader (SpriteLoader.loadAndStitch's four-argument form) */
    static class_8684 defaultLoader() {
        return class_8684.create(class_7766.field_45632);
    }

    // ---------------------------------------------------------------- one element

    /** a 1.20.2 sprite function as the Supplier 1.20.1 code held (a Supplier, or a view's original, passes) */
    public static Object supplier(Object value) {
        if (value instanceof LegacyFunction f) {
            return f.legacy;
        }
        if (value instanceof Supplier<?> || value == null) {
            return value;
        }
        if (value instanceof class_7948.class_7950 spriteSupplier) {
            return new SpriteSupplierView(spriteSupplier);
        }
        if (value instanceof Function<?, ?> function) {
            return new FunctionView((Function<class_8684, class_7764>) function);
        }
        return value;
    }

    /** a 1.20.1 sprite supplier as the Function 1.20.2 applies (a Function, or a view's original, passes) */
    public static Object function(Object value) {
        if (value instanceof SupplierView v) {
            return v.original();
        }
        if (value instanceof Function<?, ?> || value == null) {
            return value;
        }
        if (value instanceof Supplier<?> supplier) {
            return new LegacyFunction(supplier);
        }
        return value;
    }

    /** the views of 1.20.2 functions as 1.20.1 suppliers */
    private interface SupplierView {
        Object original();
    }

    /** a 1.20.2 SpriteSupplier read by 1.20.1 code: still a SpriteSupplier, and a Supplier of its contents */
    private static final class SpriteSupplierView implements class_7948.class_7950, Supplier<class_7764>, SupplierView {
        private final class_7948.class_7950 original;

        SpriteSupplierView(class_7948.class_7950 original) {
            this.original = original;
        }

        @Override
        public Object original() {
            return this.original;
        }

        @Override
        public class_7764 apply(class_8684 loader) {
            return (class_7764) this.original.apply(loader);
        }

        @Override
        public void method_47676() {
            this.original.method_47676();
        }

        @Override
        public class_7764 get() {
            return (class_7764) this.original.apply(defaultLoader());
        }
    }

    /** any other 1.20.2 sprite function read by 1.20.1 code (the missing-texture one): a Supplier of its contents */
    private static final class FunctionView implements Function<class_8684, class_7764>, Supplier<class_7764>, SupplierView {
        private final Function<class_8684, class_7764> original;

        FunctionView(Function<class_8684, class_7764> original) {
            this.original = original;
        }

        @Override
        public Object original() {
            return this.original;
        }

        @Override
        public class_7764 apply(class_8684 loader) {
            return this.original.apply(loader);
        }

        @Override
        public class_7764 get() {
            return this.original.apply(defaultLoader());
        }
    }

    /** a 1.20.1 supplier handed to 1.20.2: a function of the loader, which the supplier never took */
    private static final class LegacyFunction implements Function<class_8684, class_7764>, Supplier<class_7764> {
        final Supplier<?> legacy;

        LegacyFunction(Supplier<?> legacy) {
            this.legacy = legacy;
        }

        @Override
        public class_7764 apply(class_8684 loader) {
            return (class_7764) this.legacy.get();
        }

        @Override
        public class_7764 get() {
            return (class_7764) this.legacy.get();
        }
    }

    // ---------------------------------------------------------------- the list (live views)

    /** callReshapes ret: SpriteSourceList.list's 1.20.2 functions as 1.20.1's suppliers */
    public static List suppliers(List value) {
        return view(value, false);
    }

    /** convertHooks ret: a 1.20.1 override's suppliers as the functions 1.20.2 applies */
    public static List functions(List value) {
        return view(value, true);
    }

    private static List view(List value, boolean functions) {
        if (value == null) {
            return null;
        }
        if (value instanceof ListView v) {
            return v.functions == functions ? value : v.base;
        }
        return value instanceof RandomAccess ? new RandomAccessListView(value, functions) : new ListView(value, functions);
    }

    private static class ListView extends AbstractList<Object> {
        final List<Object> base;
        /** true: shows a 1.20.1 list's suppliers as functions; false: a 1.20.2 list's functions as suppliers */
        final boolean functions;

        ListView(List<Object> base, boolean functions) {
            this.base = base;
            this.functions = functions;
        }

        private Object out(Object e) {
            return this.functions ? function(e) : supplier(e);
        }

        private Object in(Object e) {
            return this.functions ? supplier(e) : function(e);
        }

        @Override
        public Object get(int index) {
            return out(this.base.get(index));
        }

        @Override
        public Object set(int index, Object element) {
            return out(this.base.set(index, in(element)));
        }

        @Override
        public void add(int index, Object element) {
            this.base.add(index, in(element));
        }

        @Override
        public Object remove(int index) {
            return out(this.base.remove(index));
        }

        @Override
        public int size() {
            return this.base.size();
        }
    }

    private static final class RandomAccessListView extends ListView implements RandomAccess {
        RandomAccessListView(List<Object> base, boolean functions) {
            super(base, functions);
        }
    }

    private CbGenSprites() {
    }
}
