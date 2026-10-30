package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

/**
 * 1.21.1's view of AbstractFurnaceBlockEntity.recipesUsed (class_2609.field_11986): 1.21.1 kept an
 * Object2IntOpenHashMap keyed by the recipe's ResourceLocation, 1.21.2 a Reference2IntOpenHashMap keyed by its
 * ResourceKey (javap of both: same role -- addTo on each smelt, cleared when the experience is awarded, saved as
 * "RecipesUsed" by the key's location). The field's V type is the concrete class, so the view is one: an
 * Object2IntOpenHashMap whose own table stays empty and whose every public operation goes to W's map, converting keys
 * (ResourceLocation -> ResourceKey of Registries.RECIPE in, the key's location out; a ResourceKey passes unchanged).
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbFurnaceRecipesUsed extends Object2IntOpenHashMap<net.minecraft.class_2960> {
    private final Reference2IntOpenHashMap m;

    private CbFurnaceRecipesUsed(Reference2IntOpenHashMap m) {
        this.m = m;
    }

    /** class_2609.field_11986 as 1.21.1's Object2IntOpenHashMap */
    public static Object2IntOpenHashMap field_11986(net.minecraft.class_2609 o) {
        return o.field_11986 == null ? null : new CbFurnaceRecipesUsed(o.field_11986);
    }

    private static Object in(Object k) {
        return k instanceof net.minecraft.class_2960 l
            ? net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_52178, l) : k;
    }

    private static net.minecraft.class_2960 out(Object k) {
        return k instanceof net.minecraft.class_5321 r ? r.method_29177() : (net.minecraft.class_2960) k;
    }

    @Override public int size() { return m.size(); }
    @Override public boolean isEmpty() { return m.isEmpty(); }
    @Override public void clear() { m.clear(); }
    @Override public int getInt(Object k) { return m.getInt(in(k)); }
    @Override public boolean containsKey(Object k) { return m.containsKey(in(k)); }
    @Override public boolean containsValue(int v) { return m.containsValue(v); }
    @Override public int put(net.minecraft.class_2960 k, int v) { return m.put(in(k), v); }
    @Override public int addTo(net.minecraft.class_2960 k, int v) { return m.addTo(in(k), v); }
    @Override public int removeInt(Object k) { return m.removeInt(in(k)); }
    @Override public int getOrDefault(Object k, int d) { return m.getOrDefault(in(k), d); }
    @Override public int putIfAbsent(net.minecraft.class_2960 k, int v) { return m.putIfAbsent(in(k), v); }
    @Override public boolean remove(Object k, int v) { return m.remove(in(k), v); }
    @Override public boolean replace(net.minecraft.class_2960 k, int o, int v) { return m.replace(in(k), o, v); }
    @Override public int replace(net.minecraft.class_2960 k, int v) { return m.replace(in(k), v); }
    @Override public int defaultReturnValue() { return m.defaultReturnValue(); }
    @Override public void defaultReturnValue(int rv) { m.defaultReturnValue(rv); }

    @Override
    public int computeIfAbsent(net.minecraft.class_2960 k, ToIntFunction<? super net.minecraft.class_2960> f) {
        return m.computeIfAbsent(in(k), (ToIntFunction) x -> f.applyAsInt(out(x)));
    }

    @Override
    public int computeIntIfPresent(net.minecraft.class_2960 k, BiFunction<? super net.minecraft.class_2960, ? super Integer, ? extends Integer> f) {
        return m.computeIntIfPresent(in(k), (BiFunction) (x, v) -> f.apply(out(x), (Integer) v));
    }

    @Override
    public int computeInt(net.minecraft.class_2960 k, BiFunction<? super net.minecraft.class_2960, ? super Integer, ? extends Integer> f) {
        return m.computeInt(in(k), (BiFunction) (x, v) -> f.apply(out(x), (Integer) v));
    }

    @Override
    public int merge(net.minecraft.class_2960 k, int v, BiFunction<? super Integer, ? super Integer, ? extends Integer> f) {
        return m.merge(in(k), v, f);
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (Object o : m.reference2IntEntrySet()) {
            Reference2IntMap.Entry e = (Reference2IntMap.Entry) o;
            h += out(e.getKey()).hashCode() ^ e.getIntValue();
        }
        return h;
    }

    @Override
    public Object2IntMap.FastEntrySet<net.minecraft.class_2960> object2IntEntrySet() {
        final class Entries extends AbstractObjectSet<Object2IntMap.Entry<net.minecraft.class_2960>>
                implements Object2IntMap.FastEntrySet<net.minecraft.class_2960> {
            @Override
            public ObjectIterator<Object2IntMap.Entry<net.minecraft.class_2960>> iterator() {
                Iterator it = m.reference2IntEntrySet().iterator();
                return new AbstractObjectIterator<>() {
                    @Override public boolean hasNext() { return it.hasNext(); }
                    @Override public void remove() { it.remove(); }

                    @Override
                    public Object2IntMap.Entry<net.minecraft.class_2960> next() {
                        Reference2IntMap.Entry e = (Reference2IntMap.Entry) it.next();
                        return new Object2IntMap.Entry<>() {
                            @Override public net.minecraft.class_2960 getKey() { return out(e.getKey()); }
                            @Override public int getIntValue() { return e.getIntValue(); }
                            @Override public int setValue(int v) { return e.setValue(v); }
                        };
                    }
                };
            }

            @Override public ObjectIterator<Object2IntMap.Entry<net.minecraft.class_2960>> fastIterator() { return iterator(); }
            @Override public int size() { return m.size(); }
        }
        return new Entries();
    }

    @Override
    public ObjectSet<net.minecraft.class_2960> keySet() {
        return new AbstractObjectSet<>() {
            @Override
            public ObjectIterator<net.minecraft.class_2960> iterator() {
                Iterator it = m.keySet().iterator();
                return new AbstractObjectIterator<>() {
                    @Override public boolean hasNext() { return it.hasNext(); }
                    @Override public net.minecraft.class_2960 next() { return out(it.next()); }
                    @Override public void remove() { it.remove(); }
                };
            }

            @Override public int size() { return m.size(); }
            @Override public boolean contains(Object k) { return m.containsKey(in(k)); }
            @Override public boolean remove(Object k) { return m.keySet().remove(in(k)); }
        };
    }

    @Override
    public IntCollection values() {
        return new AbstractIntCollection() {
            @Override public IntIterator iterator() { return m.values().iterator(); }
            @Override public int size() { return m.size(); }
        };
    }
}
