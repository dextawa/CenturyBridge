package top.dext.centurybridge.step.v1_20_3.rt;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import java.util.Iterator;

/**
 * 1.20.2's view of the id maps 1.20.3 retyped from Object2IntMap to Reference2IntMap: IdMapper.tToId (class_2361.field_11100)
 * and MappedRegistry.toId (class_2370.field_26683). 1.20.2 built both as Object2IntOpenCustomHashMap(Util.identityStrategy())
 * (javap of 1.20.2's constructors), so a reference map holds exactly what the identity-keyed map held. A mod reading the
 * field -- through the vanilla class or a subclass of its own (Kilt's GameData$ClearableObjectIntIdentityMap) -- gets a live
 * Object2IntMap over W's map: reads, writes, removals and clear() reach the map vanilla reads.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbFastutilViews {
    private CbFastutilViews() {
    }

    /** class_2361.field_11100 (IdMapper.tToId) as 1.20.2's Object2IntMap */
    public static Object2IntMap field_11100(net.minecraft.class_2361 o) {
        return view(o.field_11100);
    }

    /** class_2370.field_26683 (MappedRegistry.toId) as 1.20.2's Object2IntMap */
    public static Object2IntMap field_26683(net.minecraft.class_2370 o) {
        return view(o.field_26683);
    }

    static <K> Object2IntMap<K> view(Reference2IntMap<K> m) {
        return m == null ? null : new View<>(m);
    }

    /** an Object2IntMap writing through to a Reference2IntMap */
    static final class View<K> extends AbstractObject2IntMap<K> {
        private final Reference2IntMap<K> m;

        View(Reference2IntMap<K> m) {
            this.m = m;
        }

        @Override
        public int size() {
            return m.size();
        }

        @Override
        public void clear() {
            m.clear();
        }

        @Override
        public int getInt(Object k) {
            return m.getInt(k);
        }

        @Override
        public int put(K k, int v) {
            return m.put(k, v);
        }

        @Override
        public int removeInt(Object k) {
            return m.removeInt(k);
        }

        @Override
        public boolean containsKey(Object k) {
            return m.containsKey(k);
        }

        @Override
        public boolean containsValue(int v) {
            return m.containsValue(v);
        }

        @Override
        public int defaultReturnValue() {
            return m.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(int rv) {
            m.defaultReturnValue(rv);
        }

        @Override
        public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
            return new AbstractObjectSet<>() {
                @Override
                public ObjectIterator<Object2IntMap.Entry<K>> iterator() {
                    Iterator<Reference2IntMap.Entry<K>> it = m.reference2IntEntrySet().iterator();
                    return new AbstractObjectIterator<>() {
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Object2IntMap.Entry<K> next() {
                            Reference2IntMap.Entry<K> e = it.next();
                            return new Object2IntMap.Entry<>() {
                                @Override
                                public K getKey() {
                                    return e.getKey();
                                }

                                @Override
                                public int getIntValue() {
                                    return e.getIntValue();
                                }

                                @Override
                                public int setValue(int v) {
                                    return e.setValue(v);
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
                    return m.size();
                }
            };
        }
    }
}
