package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 1.20.4's loot table data providers on 1.20.5's. A LootTableSubProvider's generate(consumer) became
 * generate(registries, consumer), and the consumer takes loot table keys where it took locations:
 * <ul>
 * <li>a mod class with 1.20.4's generate(consumer) gets 1.20.5's shape synthesized (a guarded bridged
 * override hook, adapter {@link Generate}): {@link #generate} hands the old method a consumer of
 * locations over vanilla's consumer of keys (CbItemGenerics.lootLocationConsumer) and keeps the
 * registries vanilla passed for its super call;</li>
 * <li>its super call into a vanilla provider ({@code superGenerate}) runs that provider's 1.20.5 generate
 * with those registries -- calling the method again, which the guard sends on to super's;</li>
 * <li>a 1.20.4 lambda provider is adapted the same way ({@link #adapt}); a 1.20.4 call of generate(consumer)
 * reaches a mod's own method, or a vanilla provider with the running world's registries.</li>
 * </ul>
 * Data generation hands the registries; outside it the running world's (CbRegistries) are used.
 */
public final class CbLoot {

    /** a 1.20.4 loot table provider: generate(consumer of (location, table builder)) */
    public interface Generate {
        void method_10399(BiConsumer consumer);
    }

    /** the registries vanilla passed to a 1.20.4 provider's generate, for its super call: (provider, registries) */
    private static final ThreadLocal<ArrayDeque<Object[]>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);

    private static net.minecraft.class_7225.class_7874 registriesOf(Object provider) {
        for (Object[] f : FRAMES.get()) {
            if (f[0] == provider) {
                return (net.minecraft.class_7225.class_7874) f[1];
            }
        }
        return CbRegistries.current();
    }

    /** 1.20.5's generate(registries, consumer) of a mod class with 1.20.4's generate(consumer) (the bridge) */
    public static void generate(Generate provider, net.minecraft.class_7225.class_7874 registries, BiConsumer consumer) {
        ArrayDeque<Object[]> frames = FRAMES.get();
        frames.push(new Object[] {provider, registries});
        try {
            provider.method_10399(CbItemGenerics.lootLocationConsumer(consumer));
        } finally {
            frames.pop();
        }
    }

    /** a 1.20.4 lambda (or other implementation spun as {@link Generate}) as 1.20.5's provider */
    public static net.minecraft.class_7791 adapt(Generate provider) {
        return (registries, consumer) -> generate(provider, registries, consumer);
    }

    /** provider.generate(consumer): the class's own 1.20.4 method, else 1.20.5's with the world's registries */
    public static void call(net.minecraft.class_7791 provider, BiConsumer consumer) {
        if (provider instanceof Generate g) {
            g.method_10399(consumer);
        } else {
            provider.method_10399(registriesOf(provider), CbItemGenerics.lootKeyConsumer(consumer));
        }
    }

    /** a super call into a vanilla provider's generate: its 1.20.5 body, reached past the mod's synthesized one */
    private static void superCall(net.minecraft.class_7791 provider, BiConsumer consumer) {
        boolean entered = Guard.cbEnter(provider);
        try {
            provider.method_10399(registriesOf(provider), CbItemGenerics.lootKeyConsumer(consumer));
        } finally {
            if (entered) {
                Guard.cbExit(provider);
            }
        }
    }

    /** super.generate(consumer) in a mod subclass of BlockLootSubProvider */
    public static void superGenerate(net.minecraft.class_7788 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of EntityLootSubProvider */
    public static void superGenerate(net.minecraft.class_7789 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of VanillaChestLoot */
    public static void superGenerate(net.minecraft.class_2432 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of VanillaFishingLoot */
    public static void superGenerate(net.minecraft.class_2437 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of VanillaGiftLoot */
    public static void superGenerate(net.minecraft.class_3714 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of VanillaPiglinBarterLoot */
    public static void superGenerate(net.minecraft.class_4845 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of VanillaArchaeologyLoot */
    public static void superGenerate(net.minecraft.class_8255 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of TradeRebalanceChestLoot */
    public static void superGenerate(net.minecraft.class_8794 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    /** ... of UpdateOneTwentyOneChestLoot */
    public static void superGenerate(net.minecraft.class_8994 provider, BiConsumer consumer) {
        superCall(provider, consumer);
    }

    // ---------------------------------------------------------------- providers built without registries

    /** the registries a 1.20.4 data provider construction had none to pass for: the running world's (CbRegistries) */
    public static java.util.concurrent.CompletableFuture<net.minecraft.class_7225.class_7874> registriesFuture() {
        return java.util.concurrent.CompletableFuture.completedFuture(CbRegistries.current());
    }

    /**
     * new LootTableProvider(output, requiredTables, subProviders): 1.20.4's set of required table locations
     * as 1.20.5's set of keys, read through (the one CbItemGenerics.lootTableLocationSet hands 1.20.4 code
     * comes back as the set of keys it reads)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Set lootTableKeySet(Set locations) {
        if (locations == null) {
            return null;
        }
        Set back = CbItemGenerics.keysBehind(locations);
        if (back != null) {
            return back;
        }
        return new java.util.AbstractSet<Object>() {
            @Override
            public Iterator<Object> iterator() {
                Iterator it = locations.iterator();
                return new Iterator<Object>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Object next() {
                        return CbItemGenerics.toLootKey(it.next());
                    }
                };
            }

            @Override
            public boolean contains(Object o) {
                return locations.contains(CbItemGenerics.toLocation(o));
            }

            @Override
            public int size() {
                return locations.size();
            }
        };
    }

    // ---------------------------------------------------------------- entity loot

    /** EntityLootSubProvider.killedByFrogVariant(variant): 1.20.5 takes the variant's key */
    public static net.minecraft.class_5321<net.minecraft.class_7106> frogKey(net.minecraft.class_7106 variant) {
        return net.minecraft.class_7923.field_41164.method_29113(variant).orElseThrow(
            () -> new IllegalArgumentException("frog variant not registered: " + variant));
    }

    /** the reverse, for a 1.20.4 override of killedByFrogVariant(variant): the variant of the key */
    public static net.minecraft.class_7106 frogVariant(net.minecraft.class_5321<net.minecraft.class_7106> key) {
        return net.minecraft.class_7923.field_41164.method_29107(key);
    }

    private CbLoot() {
    }
}
