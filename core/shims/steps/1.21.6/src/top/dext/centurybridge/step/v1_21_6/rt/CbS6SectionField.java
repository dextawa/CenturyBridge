package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * SectionCompiler.Results.renderedLayers (field_52168) as 1.21.5 declared it: a live Map from chunk
 * RenderType to MeshData over 1.21.6's Map from ChunkSectionLayer to the same MeshData objects.
 * Reads, writes, entry.setValue and iterator removal all reach the backing map; converting a view back
 * unwraps it instead of stacking adapters. Only the five canonical chunk RenderType singletons correspond to native
 * layers. A mod-created RenderType is never classified by its pipeline: it reads as absent and is rejected on a write,
 * never aliased to a canonical entry or silently stored under null.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbS6SectionField {

    private CbS6SectionField() {
    }

    /** W layer -> V chunk RenderType, including the translucent type this step restores. */
    public static net.minecraft.class_1921 cbS6LegacyLayer(Object key) {
        if (key == null || key instanceof net.minecraft.class_1921) {
            return (net.minecraft.class_1921) key;
        }
        if (!(key instanceof net.minecraft.class_11515)) {
            throw new ClassCastException("not a 1.21.6 chunk section layer: " + key);
        }
        if (key == net.minecraft.class_11515.field_60923) {
            return net.minecraft.class_1921.method_23577();
        }
        if (key == net.minecraft.class_11515.field_60924) {
            return net.minecraft.class_1921.method_23579();
        }
        if (key == net.minecraft.class_11515.field_60925) {
            return net.minecraft.class_1921.method_23581();
        }
        if (key == net.minecraft.class_11515.field_60926) {
            return CbChunkTypes.method_23583();
        }
        if (key == net.minecraft.class_11515.field_60927) {
            return net.minecraft.class_1921.method_29997();
        }
        throw new ClassCastException("unknown 1.21.6 chunk section layer: " + key);
    }

    /** V canonical chunk RenderType -> W layer; null for every other RenderType, even with the same pipeline. */
    public static net.minecraft.class_11515 cbS6NativeLayer(Object key) {
        if (key == null || key instanceof net.minecraft.class_11515) {
            return (net.minecraft.class_11515) key;
        }
        if (key == net.minecraft.class_1921.method_23577()) {
            return net.minecraft.class_11515.field_60923;
        }
        if (key == net.minecraft.class_1921.method_23579()) {
            return net.minecraft.class_11515.field_60924;
        }
        if (key == net.minecraft.class_1921.method_23581()) {
            return net.minecraft.class_11515.field_60925;
        }
        if (key == CbChunkTypes.method_23583()) {
            return net.minecraft.class_11515.field_60926;
        }
        if (key == net.minecraft.class_1921.method_29997()) {
            return net.minecraft.class_11515.field_60927;
        }
        if (key instanceof net.minecraft.class_1921) {
            return null;
        }
        throw new ClassCastException("not a 1.21.5 chunk render type: " + key);
    }

    /** The native map with V's keys. Values are not copied or converted. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Map cbS6LegacyLayers(java.util.Map map) {
        if (map == null) {
            return null;
        }
        if (map instanceof LayerMapView) {
            LayerMapView view = (LayerMapView) map;
            return view.legacy ? map : view.backing;
        }
        return new LayerMapView(map, true);
    }

    /** Inverse of {@link #cbS6LegacyLayers}; a converted view unwraps to its original backing map. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Map cbS6NativeLayers(java.util.Map map) {
        if (map == null) {
            return null;
        }
        if (map instanceof LayerMapView) {
            LayerMapView view = (LayerMapView) map;
            return view.legacy ? view.backing : map;
        }
        return new LayerMapView(map, false);
    }

    /**
     * instanceFieldGetters entry for SectionCompiler.Results.field_52166 (1.21.5's globalBlockEntities): the live list
     * CbSectionResultsRestoredMixin keeps. 1.21.6 renders those block entities from ClientLevel's own set, so an entry a
     * mod adds to or removes from the list changes no rendering (release_notes_known.md).
     */
    public static java.util.List<net.minecraft.class_2586> field_52166(net.minecraft.class_9810.class_9811 self) {
        return ((CbPrioritySplit.SectionGlobalBlockEntities) (Object) self).cb$v1_21_6$globalBlockEntities();
    }

    /** instanceFieldGetters entry for SectionCompiler.Results.field_52168. */
    @SuppressWarnings("rawtypes")
    public static java.util.Map field_52168(net.minecraft.class_9810.class_9811 self) {
        return cbS6LegacyLayers(self.field_52168);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class LayerMapView extends java.util.AbstractMap {
        final java.util.Map backing;
        final boolean legacy;

        LayerMapView(java.util.Map backing, boolean legacy) {
            this.backing = backing;
            this.legacy = legacy;
        }

        Object outKey(Object key) {
            return legacy ? cbS6LegacyLayer(key) : cbS6NativeLayer(key);
        }

        Object inKey(Object key) {
            return legacy ? cbS6NativeLayer(key) : cbS6LegacyLayer(key);
        }

        @Override
        public Object get(Object key) {
            Object converted = inKey(key);
            return key != null && converted == null ? null : backing.get(converted);
        }

        @Override
        public boolean containsKey(Object key) {
            Object converted = inKey(key);
            return key == null || converted != null ? backing.containsKey(converted) : false;
        }

        @Override
        public Object put(Object key, Object value) {
            Object converted = inKey(key);
            if (converted == null) {
                throw new IllegalArgumentException("not a chunk render layer: " + key);
            }
            return backing.put(converted, value);
        }

        @Override
        public Object remove(Object key) {
            Object converted = inKey(key);
            return key != null && converted == null ? null : backing.remove(converted);
        }

        @Override
        public boolean containsValue(Object value) {
            return backing.containsValue(value);
        }

        @Override
        public int size() {
            return backing.size();
        }

        @Override
        public boolean isEmpty() {
            return backing.isEmpty();
        }

        @Override
        public void clear() {
            backing.clear();
        }

        @Override
        public java.util.Set entrySet() {
            return new java.util.AbstractSet() {
                @Override
                public int size() {
                    return backing.size();
                }

                @Override
                public void clear() {
                    backing.clear();
                }

                @Override
                public java.util.Iterator iterator() {
                    java.util.Iterator<java.util.Map.Entry> it = backing.entrySet().iterator();
                    return new java.util.Iterator() {
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Object next() {
                            java.util.Map.Entry entry = it.next();
                            return new java.util.Map.Entry() {
                                @Override
                                public Object getKey() {
                                    return outKey(entry.getKey());
                                }

                                @Override
                                public Object getValue() {
                                    return entry.getValue();
                                }

                                @Override
                                public Object setValue(Object value) {
                                    return entry.setValue(value);
                                }

                                @Override
                                public boolean equals(Object other) {
                                    return other instanceof java.util.Map.Entry
                                        && java.util.Objects.equals(getKey(), ((java.util.Map.Entry) other).getKey())
                                        && java.util.Objects.equals(getValue(), ((java.util.Map.Entry) other).getValue());
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
            };
        }
    }
}
