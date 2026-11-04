package top.dext.centurybridge.step.v1_21_6.rt;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Live 1.21.5 views of GL render-pass values whose generic type changed at 1.21.6. */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbS6GlViews {

    private CbS6GlViews() {
    }

    /* A view holds the native map, not the pass, so this cache does not keep a closed pass alive. */
    private static final Map<net.minecraft.class_10866, HashMap<String, GpuTexture>> SAMPLER_VIEWS = new WeakHashMap<>();

    /**
     * GlRenderPass.samplers (field_57874): 1.21.5 stored textures, 1.21.6 stores views of them. Reads unwrap the
     * current native entry; writes use the existing view when it already views that texture (so a native mip view
     * survives a native -> legacy -> native round trip), or the texture's cached full view (CbTextures.view) for a
     * texture the old code actually binds. Removal, iteration and entry.setValue all write through to the native map.
     */
    public static HashMap<String, GpuTexture> field_57874(net.minecraft.class_10866 pass) {
        synchronized (SAMPLER_VIEWS) {
            return SAMPLER_VIEWS.computeIfAbsent(pass, p -> textureMap(p.field_57874));
        }
    }

    /** The same conversion over a supplied native map, public for the no-GL round-trip harness. */
    public static HashMap<String, GpuTexture> textureMap(HashMap<String, GpuTextureView> nativeMap) {
        return nativeMap == null ? null : new TextureMapView(nativeMap);
    }

    /**
     * HashMap is the erased field type, so an AbstractMap alone is not enough. Every HashMap public operation that
     * would otherwise consult its own (empty) table is implemented here; inherited collection defaults in turn use
     * these live iterators. No legacy value is ever inserted into the native map under an erased Object signature.
     */
    private static final class TextureMapView extends HashMap<String, GpuTexture> {
        private final HashMap<String, GpuTextureView> nativeMap;
        private final Set<Map.Entry<String, GpuTexture>> entries = new EntrySet();
        private final Collection<GpuTexture> values = new Values();

        TextureMapView(HashMap<String, GpuTextureView> nativeMap) {
            this.nativeMap = nativeMap;
        }

        private static GpuTexture legacy(Object value) {
            return value == null ? null : ((GpuTextureView) value).texture();
        }

        private static GpuTextureView nativeValue(Object value, GpuTextureView current) {
            if (value == null || value instanceof GpuTextureView) {
                return (GpuTextureView) value; // adaptive for a raw map value already in native form
            }
            if (current != null && current.texture() == value) {
                return current;
            }
            return CbTextures.view((GpuTexture) value);
        }

        private GpuTextureView nativeValue(String key, GpuTexture value) {
            return nativeValue(value, nativeMap.get(key));
        }

        @Override
        public int size() {
            return nativeMap.size();
        }

        @Override
        public boolean isEmpty() {
            return nativeMap.isEmpty();
        }

        @Override
        public GpuTexture get(Object key) {
            return legacy(nativeMap.get(key));
        }

        @Override
        public GpuTexture getOrDefault(Object key, GpuTexture fallback) {
            GpuTexture value = get(key);
            return value != null || nativeMap.containsKey(key) ? value : fallback;
        }

        @Override
        public boolean containsKey(Object key) {
            return nativeMap.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            for (GpuTextureView nativeValue : nativeMap.values()) {
                if (Objects.equals(legacy(nativeValue), value)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public GpuTexture put(String key, GpuTexture value) {
            GpuTextureView current = nativeMap.get(key);
            GpuTexture previous = legacy(current);
            nativeMap.put(key, nativeValue(value, current));
            return previous;
        }

        @Override
        public GpuTexture putIfAbsent(String key, GpuTexture value) {
            GpuTexture previous = get(key);
            if (previous == null) {
                put(key, value);
            }
            return previous;
        }

        @Override
        public void putAll(Map<? extends String, ? extends GpuTexture> map) {
            for (Map.Entry<? extends String, ? extends GpuTexture> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public GpuTexture remove(Object key) {
            return legacy(nativeMap.remove(key));
        }

        @Override
        public boolean remove(Object key, Object value) {
            if (!nativeMap.containsKey(key) || !Objects.equals(get(key), value)) {
                return false;
            }
            nativeMap.remove(key);
            return true;
        }

        @Override
        public boolean replace(String key, GpuTexture expected, GpuTexture value) {
            if (!nativeMap.containsKey(key) || !Objects.equals(get(key), expected)) {
                return false;
            }
            put(key, value);
            return true;
        }

        @Override
        public GpuTexture replace(String key, GpuTexture value) {
            return nativeMap.containsKey(key) ? put(key, value) : null;
        }

        @Override
        public GpuTexture merge(String key, GpuTexture value,
                BiFunction<? super GpuTexture, ? super GpuTexture, ? extends GpuTexture> remapping) {
            Objects.requireNonNull(remapping);
            Objects.requireNonNull(value);
            GpuTexture previous = get(key);
            GpuTexture next = previous == null ? value : remapping.apply(previous, value);
            if (next == null) {
                nativeMap.remove(key);
            } else {
                put(key, next);
            }
            return next;
        }

        @Override
        public GpuTexture computeIfAbsent(String key, Function<? super String, ? extends GpuTexture> mapping) {
            Objects.requireNonNull(mapping);
            GpuTexture previous = get(key);
            if (previous != null) {
                return previous;
            }
            GpuTexture next = mapping.apply(key);
            if (next != null) {
                put(key, next);
            }
            return next;
        }

        @Override
        public GpuTexture computeIfPresent(String key,
                BiFunction<? super String, ? super GpuTexture, ? extends GpuTexture> remapping) {
            Objects.requireNonNull(remapping);
            GpuTexture previous = get(key);
            if (previous == null) {
                return null;
            }
            GpuTexture next = remapping.apply(key, previous);
            if (next == null) {
                nativeMap.remove(key);
            } else {
                put(key, next);
            }
            return next;
        }

        @Override
        public GpuTexture compute(String key,
                BiFunction<? super String, ? super GpuTexture, ? extends GpuTexture> remapping) {
            Objects.requireNonNull(remapping);
            GpuTexture next = remapping.apply(key, get(key));
            if (next == null) {
                nativeMap.remove(key);
            } else {
                put(key, next);
            }
            return next;
        }

        @Override
        public void replaceAll(BiFunction<? super String, ? super GpuTexture, ? extends GpuTexture> function) {
            Objects.requireNonNull(function);
            for (Map.Entry<String, GpuTexture> entry : entrySet()) {
                entry.setValue(function.apply(entry.getKey(), entry.getValue()));
            }
        }

        @Override
        public void forEach(BiConsumer<? super String, ? super GpuTexture> action) {
            Objects.requireNonNull(action);
            nativeMap.forEach((key, value) -> action.accept(key, legacy(value)));
        }

        @Override
        public void clear() {
            nativeMap.clear();
        }

        @Override
        public Set<String> keySet() {
            return nativeMap.keySet();
        }

        @Override
        public Collection<GpuTexture> values() {
            return values;
        }

        @Override
        public Set<Map.Entry<String, GpuTexture>> entrySet() {
            return entries;
        }

        @Override
        public Object clone() {
            return new HashMap<String, GpuTexture>(this);
        }

        private final class EntryView implements Map.Entry<String, GpuTexture> {
            private final Map.Entry<String, GpuTextureView> nativeEntry;

            EntryView(Map.Entry<String, GpuTextureView> nativeEntry) {
                this.nativeEntry = nativeEntry;
            }

            @Override
            public String getKey() {
                return nativeEntry.getKey();
            }

            @Override
            public GpuTexture getValue() {
                return legacy(nativeEntry.getValue());
            }

            @Override
            public GpuTexture setValue(GpuTexture value) {
                GpuTextureView current = nativeEntry.getValue();
                GpuTexture previous = legacy(current);
                nativeEntry.setValue(nativeValue(value, current));
                return previous;
            }

            @Override
            public boolean equals(Object other) {
                return other instanceof Map.Entry<?, ?> entry
                    && Objects.equals(getKey(), entry.getKey()) && Objects.equals(getValue(), entry.getValue());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
            }

            @Override
            public String toString() {
                return getKey() + "=" + getValue();
            }
        }

        private final class EntrySet extends AbstractSet<Map.Entry<String, GpuTexture>> {
            @Override
            public int size() {
                return nativeMap.size();
            }

            @Override
            public boolean isEmpty() {
                return nativeMap.isEmpty();
            }

            @Override
            public void clear() {
                nativeMap.clear();
            }

            @Override
            public Iterator<Map.Entry<String, GpuTexture>> iterator() {
                Iterator<Map.Entry<String, GpuTextureView>> iterator = nativeMap.entrySet().iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Map.Entry<String, GpuTexture> next() {
                        return new EntryView(iterator.next());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }

            @Override
            public boolean contains(Object object) {
                if (!(object instanceof Map.Entry<?, ?> entry)) {
                    return false;
                }
                return nativeMap.containsKey(entry.getKey())
                    && Objects.equals(get(entry.getKey()), entry.getValue());
            }

            @Override
            public boolean remove(Object object) {
                if (!contains(object)) {
                    return false;
                }
                nativeMap.remove(((Map.Entry<?, ?>) object).getKey());
                return true;
            }
        }

        private final class Values extends AbstractCollection<GpuTexture> {
            @Override
            public int size() {
                return nativeMap.size();
            }

            @Override
            public boolean isEmpty() {
                return nativeMap.isEmpty();
            }

            @Override
            public void clear() {
                nativeMap.clear();
            }

            @Override
            public boolean contains(Object value) {
                return containsValue(value);
            }

            @Override
            public Iterator<GpuTexture> iterator() {
                Iterator<GpuTextureView> iterator = nativeMap.values().iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public GpuTexture next() {
                        return legacy(iterator.next());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        }
    }
}
