package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 1.20.4 data members (area s4fin data, resources, nbt, util/worldupdate, particles) on 1.20.5:
 * <ul>
 * <li>Data providers take the registries (a future of a lookup provider) in 1.20.5: the running world's, already
 *     there.</li>
 * <li>OverworldBiomes' biomes take extra spawns in 1.20.5 on top of the ones 1.20.4 added itself: none.</li>
 * <li>RegistryCodecs.networkCodec / fullCodec (1.20.5 dropped both): 1.20.4's codecs of a whole registry -- a
 *     list of (name, id, element) entries, or a map of name to element -- read into a frozen registry. 1.20.5
 *     registers entries only at the next id, so the network form's entries are registered in id order and must
 *     be numbered from 0 without gaps, as every registry 1.20.4 wrote was.</li>
 * <li>WorldUpgrader takes the registries in 1.20.5 where 1.20.4 took the level stem registry: registries holding
 *     that one; 1.20.4 never recreated region files.</li>
 * <li>Particle types 1.20.5 renamed: 1.20.4's gust_dust is gone (the breeze's small_gust stands for it), and its
 *     gust_emitter is 1.20.5's large gust emitter.</li>
 * </ul>
 */
public final class CbFinData {

    /** a data provider's registries: the running world's */
    public static CompletableFuture<net.minecraft.class_7225.class_7874> providerFuture() {
        return CompletableFuture.completedFuture(CbRegistries.current());
    }

    /** OverworldBiomes' extra spawns: none */
    public static java.util.function.Consumer<net.minecraft.class_5483.class_5496> noSpawns() {
        return spawns -> {
        };
    }

    /** the registries 1.20.5's WorldUpgrader reads the level stems from: the one registry 1.20.4 passed */
    public static net.minecraft.class_5455 levelStems(net.minecraft.class_2378<?> stems) {
        return new net.minecraft.class_5455.class_6891(List.of(stems)).method_40316();
    }

    /** WorldUpgrader's recreateRegionFiles: 1.20.4 never did */
    public static boolean noRecreate() {
        return false;
    }

    public static net.minecraft.class_2400 field_47492() {
        return net.minecraft.class_2398.field_50252;
    }

    public static net.minecraft.class_2400 field_47495() {
        return net.minecraft.class_2398.field_49140;
    }

    // ---------------------------------------------------------------- RegistryCodecs

    private record Entry<T>(net.minecraft.class_5321<T> key, int id, T value) {
    }

    /** RegistryCodecs.networkCodec(key, lifecycle, elementCodec) */
    public static <T> Codec<net.minecraft.class_2378<T>> networkCodec(net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> registryKey,
                                                                       Lifecycle lifecycle, Codec<T> elementCodec) {
        Codec<Entry<T>> entry = RecordCodecBuilder.create(i -> i.group(
            net.minecraft.class_5321.method_39154(registryKey).fieldOf("name").forGetter(Entry::key),
            Codec.INT.fieldOf("id").forGetter(Entry::id),
            elementCodec.fieldOf("element").forGetter(Entry::value)).apply(i, Entry::new));
        return entry.listOf().xmap(list -> {
            List<Entry<T>> sorted = new ArrayList<>(list);
            sorted.sort(Comparator.comparingInt(Entry::id));
            net.minecraft.class_2370<T> registry = new net.minecraft.class_2370<>(registryKey, lifecycle);
            for (Entry<T> e : sorted) {
                if (e.id() != registry.method_10204()) {
                    throw new IllegalStateException("registry " + registryKey + ": entry " + e.key() + " has id " + e.id()
                        + ", 1.20.5 can only register it at " + registry.method_10204());
                }
                registry.method_10272(e.key(), e.value(), new net.minecraft.class_9248(java.util.Optional.empty(), lifecycle));
            }
            return registry; // 1.20.4's network codec left the registry unfrozen
        }, registry -> {
            List<Entry<T>> out = new ArrayList<>();
            for (Map.Entry<net.minecraft.class_5321<T>, T> e : registry.method_29722()) {
                out.add(new Entry<>(e.getKey(), registry.method_10206(e.getValue()), e.getValue()));
            }
            return out;
        });
    }

    /** RegistryCodecs.fullCodec(key, lifecycle, elementCodec) */
    public static <T> Codec<net.minecraft.class_2378<T>> fullCodec(net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> registryKey,
                                                                    Lifecycle lifecycle, Codec<T> elementCodec) {
        return Codec.unboundedMap(net.minecraft.class_5321.method_39154(registryKey), elementCodec).xmap(map -> {
            net.minecraft.class_2370<T> registry = new net.minecraft.class_2370<>(registryKey, lifecycle);
            map.forEach((k, v) -> registry.method_10272(k, v, new net.minecraft.class_9248(java.util.Optional.empty(), lifecycle)));
            return registry.method_40276();
        }, registry -> {
            Map<net.minecraft.class_5321<T>, T> map = new java.util.LinkedHashMap<>();
            for (Map.Entry<net.minecraft.class_5321<T>, T> e : registry.method_29722()) {
                map.put(e.getKey(), e.getValue());
            }
            return com.google.common.collect.ImmutableMap.copyOf(map);
        });
    }

    /** TagNetworkSerialization.NetworkPayload.isEmpty(): 1.20.4's body, whether it carries no tag */
    public static boolean method_40108(net.minecraft.class_6864.class_5748 payload) {
        return payload.method_56531() == 0;
    }

    /**
     * KeyDispatchDataCodec.of(codec): 1.20.5 takes the map codec a dispatching codec inlines; a 1.20.4
     * codec becomes the map codec that writes it as 1.20.4's dispatch did (CbCodecs.toMapCodec).
     */
    public static <A> net.minecraft.class_7243<A> method_42115(Codec<A> codec) {
        return net.minecraft.class_7243.method_42116(CbCodecs.toMapCodec(codec));
    }

    private CbFinData() {
    }
}
