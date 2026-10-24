package top.dext.centurybridge.step.v26_1.rt;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

/**
 * Generic drift at the wall: members whose erased shape 26.1 kept while their type arguments changed. Each
 * converter is adaptive (a value already in the wanted form passes as it is) and the pairs undo each other,
 * because erasure cannot tell which release's code made a value.
 */
public final class CbWallGeneric {

    private CbWallGeneric() {
    }

    /**
     * ChunkGenerator.getTypeNameForDataFixer(): 1.21.11 answered the generator codec's registry key, 26.1 its
     * name (26.1's body maps the same getResourceKey(codec()) to the key's identifier). 26.1's name as 1.21.11's
     * key in the chunk generator registry.
     */
    public static Optional<?> chunkGeneratorKey(Optional<?> name) {
        return name == null ? null : name.map(v -> v instanceof Identifier id
            ? ResourceKey.create(Registries.CHUNK_GENERATOR, id) : v);
    }

    /** the other way: 1.21.11's chunk generator key (a mod's override, a mod's argument) as 26.1's name */
    public static Optional<?> chunkGeneratorName(Optional<?> key) {
        return key == null ? null : key.map(v -> v instanceof ResourceKey<?> k ? k.identifier() : v);
    }

    /**
     * FeatureConfiguration.getSubFeatures() / PlacedFeature.getFeatures(): 1.21.11 streamed the configured
     * features, 26.1 streams their holders. 26.1's holders as their features.
     */
    public static Stream<?> features(Stream<?> holders) {
        return holders == null ? null : holders.map(v -> v instanceof Holder<?> h ? h.value() : v);
    }

    /**
     * the other way: a 1.21.11 configuration's sub features (its override's answer) as 26.1's holders -- a
     * feature 1.21.11 code holds is direct (it has no registry entry to name), so it is a direct holder.
     */
    public static Stream<?> featureHolders(Stream<?> features) {
        return features == null ? null : features.map(v -> v instanceof Holder<?> ? v : Holder.direct(v));
    }
}
