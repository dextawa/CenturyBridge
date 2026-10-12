package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Converters for 1.20.4 -> 1.20.5 generic drift in area s4rest: members whose erased descriptor stayed
 * while their type arguments changed (core/segments/damage-generic). The JVM links such a member, so
 * the first element a 1.20.4 mod takes out is a ClassCastException unless the value is converted where
 * it crosses (same-descriptor callReshapes, convertHooks, field getters).
 * <p>
 * Every converter is adaptive -- erasure cannot tell who handed the value over, so an element already
 * in the wanted form passes through -- and each pair undoes the other: a view handed back is unwrapped
 * to the object it views. Maps vanilla keeps (and a 1.20.4 mod may change) are converted to live views
 * that read and write through to the original.
 */
public final class CbRestGeneric {

    // ------------------------------------------------------------------ key-converting map views

    /**
     * A live map view whose keys are converted: {@code outer} turns an inner key into the key the view
     * shows, {@code inner} turns a key handed to the view into the inner map's key (both adaptive).
     * Values pass through. Iteration, removal through iterators and entry.setValue write through.
     */
    static class KeyView<V> extends AbstractMap<Object, V> {
        final Map<Object, V> map;
        final Function<Object, Object> outer;
        final Function<Object, Object> inner;

        KeyView(Map<Object, V> map, Function<Object, Object> outer, Function<Object, Object> inner) {
            this.map = map;
            this.outer = outer;
            this.inner = inner;
        }

        @Override
        public V get(Object key) {
            return map.get(inner.apply(key));
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(inner.apply(key));
        }

        @Override
        public V put(Object key, V value) {
            return map.put(inner.apply(key), value);
        }

        @Override
        public V remove(Object key) {
            return map.remove(inner.apply(key));
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public Set<Entry<Object, V>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<Object, V>> iterator() {
                    Iterator<Entry<Object, V>> it = map.entrySet().iterator();
                    return new Iterator<>() {
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Entry<Object, V> next() {
                            Entry<Object, V> e = it.next();
                            return new Entry<>() {
                                @Override
                                public Object getKey() {
                                    return outer.apply(e.getKey());
                                }

                                @Override
                                public V getValue() {
                                    return e.getValue();
                                }

                                @Override
                                public V setValue(V value) {
                                    return e.setValue(value);
                                }

                                @Override
                                public boolean equals(Object o) {
                                    return o instanceof Entry<?, ?> x && java.util.Objects.equals(getKey(), x.getKey())
                                        && java.util.Objects.equals(getValue(), x.getValue());
                                }

                                @Override
                                public int hashCode() {
                                    return java.util.Objects.hashCode(getKey()) ^ java.util.Objects.hashCode(getValue());
                                }
                            };
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return map.size();
                }

                @Override
                public void clear() {
                    map.clear();
                }
            };
        }
    }

    /** the map {@code m} viewed with converted keys; a view of the opposite direction is unwrapped */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static Map keyView(Map m, Class<?> viewTag, Function<Object, Object> outer, Function<Object, Object> inner) {
        if (m == null) {
            return null;
        }
        if (m instanceof Tagged t && t.tag() != viewTag) {
            return t.viewed(); // a view the other way round: hand back what it views
        }
        if (m instanceof Tagged t && t.tag() == viewTag) {
            return m; // already this view
        }
        return new TaggedView(m, viewTag, outer, inner);
    }

    interface Tagged {
        Class<?> tag();

        Map<?, ?> viewed();
    }

    static final class TaggedView<V> extends KeyView<V> implements Tagged {
        private final Class<?> tag;

        TaggedView(Map<Object, V> map, Class<?> tag, Function<Object, Object> outer, Function<Object, Object> inner) {
            super(map, outer, inner);
            this.tag = tag;
        }

        @Override
        public Class<?> tag() {
            return tag;
        }

        @Override
        public Map<?, ?> viewed() {
            return map;
        }
    }

    /** view tags: which way a view converts */
    private static final class ByValue {
    }

    private static final class ByHolder {
    }

    private static Object holderValue(Object k) {
        return k instanceof net.minecraft.class_6880<?> h ? h.comp_349() : k;
    }

    private static Object effectHolderKey(Object k) {
        return k instanceof net.minecraft.class_1291 e ? CbHolders.mobEffect(e) : k;
    }

    private static Object attributeHolderKey(Object k) {
        return k instanceof net.minecraft.class_1320 a ? CbRestHolders.cbAttributeHolder(a) : k;
    }

    // ------------------------------------------------------------------ MobEffect

    /**
     * LivingEntity.getActiveEffectsMap() as 1.20.4 answered it: the entity's live effect map keyed by
     * MobEffect (1.20.5 keys it by Holder&lt;MobEffect&gt;). Reads, writes, iteration and removal go
     * through to the entity's map.
     */
    @SuppressWarnings("rawtypes")
    public static Map cbEffectsByValue(Map holderKeyed) {
        return keyView(holderKeyed, ByValue.class, CbRestGeneric::holderValue, CbRestGeneric::effectHolderKey);
    }

    /** a 1.20.4 MobEffect-keyed effect map as the Holder-keyed map 1.20.5 reads (the inverse of the above) */
    @SuppressWarnings("rawtypes")
    public static Map cbEffectsByHolder(Map valueKeyed) {
        return keyView(valueKeyed, ByHolder.class, CbRestGeneric::effectHolderKey, CbRestGeneric::holderValue);
    }

    /** BeaconMenu.updateEffects' Optional&lt;MobEffect&gt; as the Optional&lt;Holder&lt;MobEffect&gt;&gt; 1.20.5 takes */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional cbEffectHolderOptional(Optional effect) {
        return effect == null ? null : effect.map(CbRestGeneric::effectHolderKey);
    }

    /** a 1.20.5 Optional&lt;Holder&lt;MobEffect&gt;&gt; as the Optional&lt;MobEffect&gt; 1.20.4 handed over */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional cbEffectValueOptional(Optional holder) {
        return holder == null ? null : holder.map(CbRestGeneric::holderValue);
    }

    // ------------------------------------------------------------------ Attribute

    /**
     * new AttributeSupplier(Map&lt;Attribute, AttributeInstance&gt;) as 1.20.5's constructor takes it
     * (keyed by Holder&lt;Attribute&gt;). 1.20.4's constructor kept an immutable copy of the map it was
     * handed; 1.20.5's keeps the map itself (its builder hands it an immutable one), so the converter
     * makes that copy: what the supplier holds does not follow later changes to the mod's map, as in
     * 1.20.4. A key already a holder passes through.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map cbAttributesByHolder(Map valueKeyed) {
        if (valueKeyed == null) {
            return null;
        }
        com.google.common.collect.ImmutableMap.Builder b = com.google.common.collect.ImmutableMap.builder();
        for (Object o : valueKeyed.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            b.put(attributeHolderKey(e.getKey()), e.getValue());
        }
        return b.build();
    }

    // ------------------------------------------------------------------ packs

    /**
     * Pack.Position.insert's 1.20.4 function (an element to its Pack) as the 1.20.5 one (an element to
     * the PackSelectionConfig the position is read from): the pack's selection config, which carries
     * the fixed-position flag and default position insert looks at -- what 1.20.4 read off the Pack.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Function cbPackSelection(Function toPack) {
        if (toPack == null || toPack instanceof PackSelection) {
            return toPack;
        }
        return (PackSelection) t -> {
            Object r = toPack.apply(t);
            return r instanceof net.minecraft.class_3288 pack ? pack.method_56934() : r;
        };
    }

    /** marks a function already converted by {@link #cbPackSelection} */
    @FunctionalInterface
    interface PackSelection extends Function<Object, Object> {
    }

    // ------------------------------------------------------------------ game events

    private static volatile ToIntFunction<Object> vibrationFrequency;

    /**
     * VibrationSystem.VIBRATION_FREQUENCY_FOR_EVENT as 1.20.4 held it, a function of the GameEvent:
     * 1.20.5 keys the same table by the event's ResourceKey. A registered event answers its key's
     * frequency; an event that was never registered has no key, and answers 0 -- what 1.20.4's table
     * (an Object2IntOpenHashMap, default 0) answered for an event it did not list. A key handed in
     * passes through.
     */
    @SuppressWarnings("unchecked")
    public static ToIntFunction<Object> field_44639() {
        ToIntFunction<Object> f = vibrationFrequency;
        if (f == null) {
            ToIntFunction<Object> byKey = (ToIntFunction<Object>) (ToIntFunction<?>) net.minecraft.class_8514.field_44639;
            vibrationFrequency = f = event -> {
                if (event instanceof net.minecraft.class_5712 ev) {
                    return net.minecraft.class_7923.field_41171.method_29113(ev).map(byKey::applyAsInt).orElse(0);
                }
                return byKey.applyAsInt(holderValueKey(event));
            };
        }
        return f;
    }

    /** a Holder handed where a key is read: its key (a direct holder has none and is passed as is) */
    private static Object holderValueKey(Object k) {
        if (k instanceof net.minecraft.class_6880<?> h) {
            return h.method_40230().map(x -> (Object) x).orElse(k);
        }
        return k;
    }

    private CbRestGeneric() {
    }
}
