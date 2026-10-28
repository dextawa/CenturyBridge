package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_11895;
import net.minecraft.class_2359;
import net.minecraft.class_2814;
import net.minecraft.class_2816;
import net.minecraft.class_2834;
import net.minecraft.class_2837;
import net.minecraft.class_3532;
import net.minecraft.class_6563;
import net.minecraft.class_6564;

/**
 * 1.21.8's PalettedContainer.Strategy (class_2841$class_6563, gone in 1.21.9), its bodies as 1.21.8 had them
 * (javap -c): a registry-free description of a container's shape -- SECTION_STATES (16^3, sizeBits 4) and
 * SECTION_BIOMES (4^3, sizeBits 2) -- whose getConfiguration(registry, bits) picked the palette per bit count.
 * 1.21.9 made the strategy a top-level class holding the registry (class_6563, Strategy.createForBlockStates /
 * createForBiomes(registry)); {@link #cb$forRegistry} gives the 1.21.9 strategy of the same kind for a registry, the
 * one 1.21.8's calls hand 1.21.9's methods (one per kind and registry, kept as long as the registry lives).
 * <p>getConfiguration answers 1.21.9's Configuration.Simple, the record 1.21.8's Configuration moved to (same
 * (factory, bits) constructor and components); 1.21.8's global choice was a Configuration of the global palette
 * factory, which in 1.21.9 needs the registry: the factory here builds the GlobalPalette of the registry asked for.
 */
public abstract class PalettedContainerStrategy {
    /** Strategy.SINGLE_VALUE_PALETTE_FACTORY */
    public static final class_2837.class_6559 field_34566 = class_6564::method_38316;
    /** Strategy.LINEAR_PALETTE_FACTORY */
    public static final class_2837.class_6559 field_34567 = class_2834::method_38295;
    /** Strategy.HASHMAP_PALETTE_FACTORY */
    public static final class_2837.class_6559 field_34568 = class_2814::method_38287;

    /** Strategy.SECTION_STATES */
    public static final PalettedContainerStrategy field_34569 = new PalettedContainerStrategy(4) {
        @Override
        public class_11895.class_6560 method_38314(class_2359<?> registry, int bits) {
            return switch (bits) {
                case 0 -> new class_11895.class_6560(field_34566, bits);
                case 1, 2, 3, 4 -> new class_11895.class_6560(field_34567, 4);
                case 5, 6, 7, 8 -> new class_11895.class_6560(field_34568, bits);
                default -> global(registry);
            };
        }

        @Override
        protected <T> class_6563<T> cb$create(class_2359<T> registry) {
            return class_6563.method_74162(registry);
        }
    };

    /** Strategy.SECTION_BIOMES */
    public static final PalettedContainerStrategy field_34570 = new PalettedContainerStrategy(2) {
        @Override
        public class_11895.class_6560 method_38314(class_2359<?> registry, int bits) {
            return switch (bits) {
                case 0 -> new class_11895.class_6560(field_34566, bits);
                case 1, 2, 3 -> new class_11895.class_6560(field_34567, bits);
                default -> global(registry);
            };
        }

        @Override
        protected <T> class_6563<T> cb$create(class_2359<T> registry) {
            return class_6563.method_74165(registry);
        }
    };

    /** Strategy.sizeBits */
    public final int field_34572;

    private final Map<class_2359<?>, class_6563<?>> cb$strategies = new WeakHashMap<>();

    public PalettedContainerStrategy(int sizeBits) {
        this.field_34572 = sizeBits;
    }

    /** the global factories handed out, each the GlobalPalette of one registry (1.21.8's one GLOBAL factory took it per call) */
    private static final Map<class_2837.class_6559, Boolean> cb$globals = java.util.Collections.synchronizedMap(new WeakHashMap<>());

    /** the global choice: 1.21.8's Configuration(GLOBAL_PALETTE_FACTORY, ceillog2(registry size)) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static class_11895.class_6560 global(class_2359 registry) {
        class_2837.class_6559 factory = new class_2837.class_6559() {
            @Override
            public <A> class_2837<A> create(int bits, java.util.List<A> values) {
                return new class_2816(registry);
            }
        };
        cb$globals.put(factory, Boolean.TRUE);
        return new class_11895.class_6560(factory, class_3532.method_15342(registry.method_10204()));
    }

    /** Strategy.size() */
    public int method_38312() {
        return 1 << this.field_34572 * 3;
    }

    /** Strategy.getIndex(x, y, z) */
    public int method_38313(int x, int y, int z) {
        return (y << this.field_34572 | z) << this.field_34572 | x;
    }

    /** Strategy.getConfiguration(registry, bits) */
    public abstract class_11895.class_6560 method_38314(class_2359<?> registry, int bits);

    /**
     * Strategy.calculateBitsForSerialization(registry, entries): the configuration's bits, or ceillog2(entries) where
     * the choice is the global palette (javap -c: 1.21.8 compared the factory with GLOBAL_PALETTE_FACTORY)
     */
    public int method_38315(class_2359<?> registry, int entries) {
        int bits = class_3532.method_15342(entries);
        class_11895.class_6560 configuration = this.method_38314(registry, bits);
        return cb$globals.containsKey(configuration.comp_72()) ? bits : configuration.comp_73();
    }

    protected abstract <T> class_6563<T> cb$create(class_2359<T> registry);

    /** the 1.21.9 strategy of this kind for a registry */
    @SuppressWarnings("unchecked")
    public <T> class_6563<T> cb$forRegistry(class_2359<T> registry) {
        synchronized (this.cb$strategies) {
            return (class_6563<T>) this.cb$strategies.computeIfAbsent(registry, r -> this.cb$create(registry));
        }
    }

    /** the 1.21.8 strategy of a 1.21.9 one's kind (its size: 4096 cells for states, 64 for biomes) */
    public static PalettedContainerStrategy cb$legacy(class_6563<?> strategy) {
        return strategy.method_74161() == field_34569.method_38312() ? field_34569 : field_34570;
    }
}
