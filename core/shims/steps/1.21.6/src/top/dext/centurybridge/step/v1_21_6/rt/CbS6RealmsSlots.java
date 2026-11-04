package top.dext.centurybridge.step.v1_21_6.rt;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.class_11306;
import net.minecraft.class_4877;
import net.minecraft.class_4883;

/**
 * RealmsServer's slot maps, 1.21.5 {@code Map<Integer, RealmsServerPing>} viewed over 1.21.6's
 * {@code Map<Integer, RealmsServerPlayerList>} and back (javap of both code jars). The 1.21.6 value
 * wraps the old ping as field_60206 and adds the slot number and online players; method_25061 is the
 * same map-copy helper in both releases, except that it copies those wrappers instead of bare pings.
 *
 * <p>The converters are adaptive and undo each other. A native view of an old map owns a paired old view;
 * converting that pair in either direction returns the original view, never the raw ping backing map (which
 * cannot carry the native wrappers or their player lists). Converting an old view of a plain native map back
 * to native unwraps that plain map. Both views are live. An old view of a native map returns the wrapper's
 * actual ping, and replacing that ping changes the existing wrapper rather than discarding its player list.
 * A native view of an old map keeps one wrapper per slot, follows replacement of the backing ping, and writes
 * native replacements back as the old ping. Iteration, entry.setValue, removal and clear write through.</p>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbS6RealmsSlots {

    private CbS6RealmsSlots() {
    }

    /** RealmsServer.slots as 1.21.5 code reads it. */
    public static Map field_22607(class_4877 server) {
        return cbLegacySlots(server.field_22607);
    }

    /** RealmsServer.slots as 1.21.5 code writes it. */
    public static void field_22607(class_4877 server, Map slots) {
        server.field_22607 = cbNativeSlots(slots);
    }

    /** A native slot map as the old ping-valued map; null passes, an inverse view is restored. */
    public static Map cbLegacySlots(Map slots) {
        if (slots == null || slots instanceof LegacySlots) {
            return slots;
        }
        if (slots instanceof NativeSlots nativeSlots) {
            // The raw ping map has no place for NativeSlots' wrappers and player lists. Keep the pair.
            return nativeSlots.legacyView();
        }
        return new LegacySlots(slots);
    }

    /** An old ping-valued slot map as 1.21.6's wrapper-valued map; the inverse of cbLegacySlots. */
    public static Map cbNativeSlots(Map slots) {
        if (slots == null || slots instanceof NativeSlots) {
            return slots;
        }
        if (slots instanceof LegacySlots legacySlots) {
            return legacySlots.nativeMap;
        }
        return new NativeSlots(slots);
    }

    private static class_4883 ping(Object value) {
        return value instanceof class_11306 wrapper ? wrapper.field_60206 : (class_4883) value;
    }

    private static int slot(Object key) {
        return (Integer) key;
    }

    /** A new native value for a previously unrepresented old entry. The old API has no player list. */
    private static class_11306 newWrapper(Object key, Object value) {
        return value == null ? null : new class_11306(slot(key), ping(value), new ArrayList<>());
    }

    /** The old view. Its backing values are native wrappers; the wrapper itself owns value identity. */
    private static final class LegacySlots extends AbstractMap<Object, Object> {
        private final Map<Object, Object> nativeMap;

        private LegacySlots(Map<?, ?> nativeMap) {
            this.nativeMap = (Map<Object, Object>) nativeMap;
        }

        @Override
        public Object get(Object key) {
            return ping(nativeMap.get(key));
        }

        @Override
        public boolean containsKey(Object key) {
            return nativeMap.containsKey(key);
        }

        @Override
        public Object put(Object key, Object value) {
            if (nativeMap instanceof NativeSlots) {
                // Let the paired native view update its raw ping and reuse its cached wrapper together.
                Object previousPing = ping(nativeMap.get(key));
                nativeMap.put(key, value);
                return previousPing;
            }
            Object previous = nativeMap.get(key);
            Object previousPing = ping(previous);
            if (previous instanceof class_11306 wrapper) {
                // Keep the native wrapper and its slot/player data: only the old value is replaced.
                wrapper.field_60206 = ping(value);
            } else {
                nativeMap.put(key, newWrapper(key, value));
            }
            return previousPing;
        }

        @Override
        public Object remove(Object key) {
            return ping(nativeMap.remove(key));
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
        public void clear() {
            nativeMap.clear();
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<Object, Object>> iterator() {
                    Iterator<Entry<Object, Object>> iterator = nativeMap.entrySet().iterator();
                    return new Iterator<>() {
                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Entry<Object, Object> next() {
                            Entry<Object, Object> entry = iterator.next();
                            return new Entry<>() {
                                @Override
                                public Object getKey() {
                                    return entry.getKey();
                                }

                                @Override
                                public Object getValue() {
                                    return ping(entry.getValue());
                                }

                                @Override
                                public Object setValue(Object value) {
                                    Object previousPing = ping(entry.getValue());
                                    if (nativeMap instanceof NativeSlots) {
                                        // The paired entry updates its raw ping and cached wrapper atomically.
                                        entry.setValue(value);
                                        return previousPing;
                                    }
                                    Object previous = entry.getValue();
                                    if (previous instanceof class_11306 wrapper) {
                                        wrapper.field_60206 = ping(value);
                                    } else {
                                        entry.setValue(newWrapper(getKey(), value));
                                    }
                                    return previousPing;
                                }

                                @Override
                                public boolean equals(Object other) {
                                    return other instanceof Entry<?, ?> otherEntry
                                        && Objects.equals(getKey(), otherEntry.getKey())
                                        && Objects.equals(getValue(), otherEntry.getValue());
                                }

                                @Override
                                public int hashCode() {
                                    return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
                                }

                                @Override
                                public String toString() {
                                    return getKey() + "=" + getValue();
                                }
                            };
                        }

                        @Override
                        public void remove() {
                            iterator.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return nativeMap.size();
                }

                @Override
                public void clear() {
                    nativeMap.clear();
                }
            };
        }
    }

    /** The native view of an old map. Its paired old view and wrappers retain all native-only state. */
    private static final class NativeSlots extends AbstractMap<Object, Object> {
        private final Map<Object, Object> legacy;
        private final Map<Object, class_11306> wrappers = new HashMap<>();
        private LegacySlots legacyView;

        private NativeSlots(Map<?, ?> legacy) {
            this.legacy = (Map<Object, Object>) legacy;
        }

        private LegacySlots legacyView() {
            if (legacyView == null) {
                legacyView = new LegacySlots(this);
            }
            return legacyView;
        }

        private class_11306 wrapper(Object key, Object value) {
            if (value == null) {
                wrappers.remove(key);
                return null;
            }
            if (value instanceof class_11306 wrapper) {
                wrappers.put(key, wrapper);
                return wrapper;
            }
            class_4883 ping = ping(value);
            class_11306 wrapper = wrappers.get(key);
            if (wrapper == null) {
                wrapper = new class_11306(slot(key), ping, new ArrayList<>());
                wrappers.put(key, wrapper);
            } else {
                // The old map may have been changed directly, outside this view.
                wrapper.field_60206 = ping;
            }
            return wrapper;
        }

        private class_11306 remember(Object key, Object value) {
            if (value == null) {
                wrappers.remove(key);
                return null;
            }
            class_11306 wrapper;
            if (value instanceof class_11306 nativeValue) {
                wrapper = nativeValue;
            } else {
                // A write from the paired old view replaces only the ping, not native-only slot state.
                wrapper = wrappers.get(key);
                if (wrapper == null) {
                    wrapper = newWrapper(key, value);
                } else {
                    wrapper.field_60206 = ping(value);
                }
            }
            wrappers.put(key, wrapper);
            return wrapper;
        }

        @Override
        public Object get(Object key) {
            if (!legacy.containsKey(key)) {
                wrappers.remove(key);
                return null;
            }
            return wrapper(key, legacy.get(key));
        }

        @Override
        public boolean containsKey(Object key) {
            return legacy.containsKey(key);
        }

        @Override
        public Object put(Object key, Object value) {
            Object previous = legacy.containsKey(key) ? wrapper(key, legacy.get(key)) : null;
            legacy.put(key, ping(value));
            remember(key, value);
            return previous;
        }

        @Override
        public Object remove(Object key) {
            Object previous = legacy.containsKey(key) ? wrapper(key, legacy.get(key)) : null;
            legacy.remove(key);
            wrappers.remove(key);
            return previous;
        }

        @Override
        public int size() {
            return legacy.size();
        }

        @Override
        public boolean isEmpty() {
            return legacy.isEmpty();
        }

        @Override
        public void clear() {
            legacy.clear();
            wrappers.clear();
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<Object, Object>> iterator() {
                    Iterator<Entry<Object, Object>> iterator = legacy.entrySet().iterator();
                    return new Iterator<>() {
                        private Entry<Object, Object> current;

                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Entry<Object, Object> next() {
                            Entry<Object, Object> entry = iterator.next();
                            current = entry;
                            return new Entry<>() {
                                @Override
                                public Object getKey() {
                                    return entry.getKey();
                                }

                                @Override
                                public Object getValue() {
                                    return wrapper(getKey(), entry.getValue());
                                }

                                @Override
                                public Object setValue(Object value) {
                                    Object previous = wrapper(getKey(), entry.getValue());
                                    entry.setValue(ping(value));
                                    remember(getKey(), value);
                                    return previous;
                                }

                                @Override
                                public boolean equals(Object other) {
                                    return other instanceof Entry<?, ?> otherEntry
                                        && Objects.equals(getKey(), otherEntry.getKey())
                                        && Objects.equals(getValue(), otherEntry.getValue());
                                }

                                @Override
                                public int hashCode() {
                                    return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
                                }

                                @Override
                                public String toString() {
                                    return getKey() + "=" + getValue();
                                }
                            };
                        }

                        @Override
                        public void remove() {
                            if (current == null) {
                                throw new IllegalStateException();
                            }
                            Object key = current.getKey();
                            iterator.remove();
                            wrappers.remove(key);
                            current = null;
                        }
                    };
                }

                @Override
                public int size() {
                    return legacy.size();
                }

                @Override
                public void clear() {
                    legacy.clear();
                    wrappers.clear();
                }
            };
        }
    }
}
