package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * The value side of 1.21.5's {@code MemoryModuleType.NEAREST_VISIBLE_ADULT} (field_25359).
 *
 * <p>The memory key is the same registered object on 1.21.6; only its value widened from AgeableMob to
 * LivingEntity. The key therefore passes through unchanged. At the typed Brain entry points a 1.21.5
 * value going in still is a LivingEntity and keeps its identity. A value coming out is projected onto
 * 1.21.5's domain: an AgeableMob (and the Optional holding it) passes through, while a LivingEntity that
 * 1.21.5 could never have stored reads as no memory, rather than reaching the mod's CHECKCAST.</p>
 *
 * <p>The redirects take the key as well as the value. A return-only converter cannot do this: filtering
 * every Brain memory to AgeableMob would corrupt all the unrelated memory types.</p>
 */
public final class CbS6MemoryFields {

    private CbS6MemoryFields() {
    }

    private static boolean nearestAdult(Object type) {
        return type == net.minecraft.class_4140.field_25359;
    }

    /* Brain returns the same backing map on every getMemories call; keep the view's identity stable too.
     * Both key and value are weak: a remembered entity can itself own a Brain, so a strong view value could
     * indirectly keep the weak key alive. A live view held by a mod remains the identity-stable result. */
    private static final java.util.Map<net.minecraft.class_4095, java.lang.ref.WeakReference<java.util.Map>>
        MEMORY_VIEWS = new java.util.WeakHashMap<>();

    /** W -> V: the old domain's view of an Optional memory value; representable values keep their identity. */
    public static java.util.Optional cbS6AdultOut(net.minecraft.class_4140 type, java.util.Optional value) {
        if (value == null || !nearestAdult(type) || value.isEmpty() || value.get() instanceof net.minecraft.class_1296) {
            return value;
        }
        return java.util.Optional.empty();
    }

    /** V -> W: a value the old typed API can contain is already a LivingEntity; preserve it and its Optional. */
    public static Object cbS6AdultIn(net.minecraft.class_4140 type, Object value) {
        if (nearestAdult(type) && value != null && !(value instanceof net.minecraft.class_1296)) {
            throw new ClassCastException("1.21.5 nearest_visible_adult requires AgeableMob, got "
                + value.getClass().getName());
        }
        return value;
    }

    /** The Optional form of {@link #cbS6AdultIn}; no copy, so a valid in/out round trip is the same object. */
    public static java.util.Optional cbS6AdultOptionalIn(net.minecraft.class_4140 type, java.util.Optional value) {
        if (value != null && value.isPresent()) {
            cbS6AdultIn(type, value.get());
        }
        return value;
    }

    /** Brain.getOptionalMemory */
    public static java.util.Optional method_18904(net.minecraft.class_4095 self, net.minecraft.class_4140 type) {
        return cbS6AdultOut(type, self.method_18904(type));
    }

    /** Brain.getRegisteredMemory */
    public static java.util.Optional method_46873(net.minecraft.class_4095 self, net.minecraft.class_4140 type) {
        return cbS6AdultOut(type, self.method_46873(type));
    }

    /** Brain.setMemory(type, value) */
    public static void method_18878(net.minecraft.class_4095 self, net.minecraft.class_4140 type, Object value) {
        self.method_18878(type, cbS6AdultIn(type, value));
    }

    /** Brain.setMemoryWithExpiry */
    public static void method_24525(net.minecraft.class_4095 self, net.minecraft.class_4140 type, Object value,
            long expiry) {
        self.method_24525(type, cbS6AdultIn(type, value), expiry);
    }

    /** Brain.setMemory(type, Optional) */
    public static void method_18879(net.minecraft.class_4095 self, net.minecraft.class_4140 type,
            java.util.Optional value) {
        self.method_18879(type, cbS6AdultOptionalIn(type, value));
    }

    /** Brain.isMemoryValue */
    public static boolean method_29519(net.minecraft.class_4095 self, net.minecraft.class_4140 type, Object value) {
        return self.method_29519(type, cbS6AdultIn(type, value));
    }

    /** A raw Optional hand-out still has its memory key, unlike ExpirableValue.getValue on its own. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static java.util.Optional cbS6MemoryOut(Object type, java.util.Optional value) {
        if (value == null || value.isEmpty() || !nearestAdult(type)) {
            return value;
        }
        Object held = ((net.minecraft.class_4831) value.get()).method_24637();
        return held == null || held instanceof net.minecraft.class_1296 ? value : java.util.Optional.empty();
    }

    /** Validate a raw Optional hand-in without copying it or replacing its ExpirableValue. */
    @SuppressWarnings("rawtypes")
    private static java.util.Optional cbS6MemoryIn(Object type, java.util.Optional value) {
        if (value != null && value.isPresent() && nearestAdult(type)) {
            cbS6AdultIn(net.minecraft.class_4140.field_25359,
                ((net.minecraft.class_4831) value.get()).method_24637());
        }
        return value;
    }

    /**
     * Brain.getMemories. Every value escape (get, entries, values and the default Map operations built on them)
     * applies the same keyed projection as the typed getters. Writes unwrap nothing: valid V values already are W
     * values, so their original Optional and ExpirableValue identities reach the backing map. In particular, a mod
     * cannot fetch a widened LivingEntity here and then expose it through ExpirableValue.getValue.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Map method_35058(net.minecraft.class_4095 self) {
        synchronized (MEMORY_VIEWS) {
            java.lang.ref.WeakReference<java.util.Map> reference = MEMORY_VIEWS.get(self);
            java.util.Map view = reference == null ? null : reference.get();
            if (view == null) {
                view = new MemoryMapView(self.method_35058());
                MEMORY_VIEWS.put(self, new java.lang.ref.WeakReference<>(view));
            }
            return view;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class MemoryMapView extends java.util.AbstractMap {
        final java.util.Map backing;

        MemoryMapView(java.util.Map backing) {
            this.backing = backing;
        }

        @Override
        public Object get(Object key) {
            return cbS6MemoryOut(key, (java.util.Optional) backing.get(key));
        }

        @Override
        public boolean containsKey(Object key) {
            return backing.containsKey(key);
        }

        @Override
        public Object put(Object key, Object value) {
            java.util.Optional old = (java.util.Optional) backing.put(key,
                cbS6MemoryIn(key, (java.util.Optional) value));
            return cbS6MemoryOut(key, old);
        }

        @Override
        public Object remove(Object key) {
            return cbS6MemoryOut(key, (java.util.Optional) backing.remove(key));
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
                public boolean isEmpty() {
                    return backing.isEmpty();
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
                            Object key = entry.getKey();
                            return new java.util.Map.Entry() {
                                @Override
                                public Object getKey() {
                                    return entry.getKey();
                                }

                                @Override
                                public Object getValue() {
                                    return cbS6MemoryOut(key, (java.util.Optional) entry.getValue());
                                }

                                @Override
                                public Object setValue(Object value) {
                                    java.util.Optional old = (java.util.Optional) entry.setValue(
                                        cbS6MemoryIn(key, (java.util.Optional) value));
                                    return cbS6MemoryOut(key, old);
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
