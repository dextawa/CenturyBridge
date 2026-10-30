package top.dext.centurybridge.step.v1_20_3.rt;

import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
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

    /**
     * class_1257.field_5762 (WorldUpgrader.progressMap) as 1.20.2's Object2FloatMap: 1.20.2 built it as
     * Object2FloatOpenCustomHashMap(Util.identityStrategy()), 1.20.3 as Reference2FloatOpenHashMap (javap of both
     * constructors); written by the upgrade thread per dimension, read by dimensionProgress
     */
    public static Object2FloatMap field_5762(net.minecraft.class_1257 o) {
        return o.field_5762 == null ? null : new FloatView<>(o.field_5762);
    }

    /** an Object2FloatMap writing through to a Reference2FloatMap */
    static final class FloatView<K> extends AbstractObject2FloatMap<K> {
        private final Reference2FloatMap<K> m;

        FloatView(Reference2FloatMap<K> m) {
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
        public float getFloat(Object k) {
            return m.getFloat(k);
        }

        @Override
        public float put(K k, float v) {
            return m.put(k, v);
        }

        @Override
        public float removeFloat(Object k) {
            return m.removeFloat(k);
        }

        @Override
        public boolean containsKey(Object k) {
            return m.containsKey(k);
        }

        @Override
        public boolean containsValue(float v) {
            return m.containsValue(v);
        }

        @Override
        public float defaultReturnValue() {
            return m.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(float rv) {
            m.defaultReturnValue(rv);
        }

        @Override
        public ObjectSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
            return new AbstractObjectSet<>() {
                @Override
                public ObjectIterator<Object2FloatMap.Entry<K>> iterator() {
                    Iterator<Reference2FloatMap.Entry<K>> it = m.reference2FloatEntrySet().iterator();
                    return new AbstractObjectIterator<>() {
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Object2FloatMap.Entry<K> next() {
                            Reference2FloatMap.Entry<K> e = it.next();
                            return new Object2FloatMap.Entry<>() {
                                @Override
                                public K getKey() {
                                    return e.getKey();
                                }

                                @Override
                                public float getFloatValue() {
                                    return e.getFloatValue();
                                }

                                @Override
                                public float setValue(float v) {
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
