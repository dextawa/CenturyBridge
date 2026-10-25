package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * 26.1.2's surface rule API on 26.2 (javap of both).
 *
 * <ul>
 * <li>codec(): ConditionSource and RuleSource answered a KeyDispatchDataCodec in 26.1.2, the MapCodec it holds in
 *     26.2 (both registries held the MapCodec in both releases). A call gets 26.2's codec wrapped
 *     ({@link #toKeyDispatch}); a 26.1.2 implementation's codec() answers 26.2's with the MapCodec it holds
 *     ({@link #toMapCodec}).</li>
 * <li>isBiome(keys): 26.1.2 kept the biome keys and tested the biome at the rule's position by key
 *     (Holder.is(Set.copyOf(keys)::contains)); 26.2 resolves the keys to holders through a biome HolderGetter when the
 *     rule is built, which 26.1.2 code building its rules at initialisation does not have. {@link BiomeKeys} is
 *     26.1.2's condition: the same keys, the same test, the same "biome_is" list of keys, registered as
 *     centurybridge:biome_is (26.2's minecraft:biome decodes a holder set).</li>
 * <li>overworld() / overworldLike(...) / nether(): 26.2's rules (SurfaceRuleData is 26.2's vanilla surface), their
 *     biome conditions 26.1.2's key conditions: built with {@link #KEYS}, a getter that answers nothing, which
 *     SurfaceRules.isBiome turns into a {@link BiomeKeys} (mixin/CbS26SurfaceRulesMixin). SurfaceRuleData reaches
 *     the getter only through isBiome.</li>
 * </ul>
 */
public final class CbS26SurfaceRules {
    private CbS26SurfaceRules() {}

    /** callReshapes ret conv: 26.2's codec() handed out as 26.1.2's KeyDispatchDataCodec */
    public static <A> KeyDispatchDataCodec<A> toKeyDispatch(MapCodec<A> codec) {
        return KeyDispatchDataCodec.of(codec);
    }

    /** overrideHooks unwrap: a 26.1.2 codec() answering 26.2's codec() */
    public static <A> MapCodec<A> toMapCodec(KeyDispatchDataCodec<A> codec) {
        return codec.codec();
    }

    /** 26.1.2 SurfaceRules.isBiome(ResourceKey...) */
    @SafeVarargs
    public static SurfaceRules.ConditionSource isBiome(ResourceKey<Biome>... biomes) {
        return new BiomeKeys(List.of(biomes));
    }

    /** 26.1.2 SurfaceRuleData.overworld() */
    public static SurfaceRules.RuleSource overworld() {
        return SurfaceRuleData.overworld(KEYS);
    }

    /** 26.1.2 SurfaceRuleData.overworldLike(abovePreliminarySurface, bedrockRoof, bedrockFloor) */
    public static SurfaceRules.RuleSource overworldLike(boolean abovePreliminarySurface, boolean bedrockRoof,
                                                        boolean bedrockFloor) {
        return SurfaceRuleData.overworldLike(KEYS, abovePreliminarySurface, bedrockRoof, bedrockFloor);
    }

    /** 26.1.2 SurfaceRuleData.nether() */
    public static SurfaceRules.RuleSource nether() {
        return SurfaceRuleData.nether(KEYS);
    }

    /** The getter of 26.1.2's key conditions: SurfaceRules.isBiome given it answers a {@link BiomeKeys}. */
    public static final HolderGetter<Biome> KEYS = new HolderGetter<>() {
        @Override
        public Optional<Holder.Reference<Biome>> get(ResourceKey<Biome> key) {
            throw new IllegalStateException("26.1.2 surface rules test biomes by key; nothing resolves " + key);
        }

        @Override
        public Optional<HolderSet.Named<Biome>> get(TagKey<Biome> tag) {
            throw new IllegalStateException("26.1.2 surface rules test biomes by key; nothing resolves " + tag);
        }
    };

    /** mixin/CbS26SurfaceRulesMixin: SurfaceRules.isBiome(getter, keys) when the getter is {@link #KEYS} */
    public static SurfaceRules.ConditionSource keyCondition(HolderGetter<Biome> getter, ResourceKey<Biome>[] biomes) {
        return getter == KEYS ? new BiomeKeys(List.of(biomes)) : null;
    }

    /** mixin/CbS26SurfaceRulesMixin: from the end of the built-in registries' bootstrap, before they freeze */
    public static void register() {
        Registry.register(BuiltInRegistries.MATERIAL_CONDITION, Identifier.fromNamespaceAndPath("centurybridge", "biome_is"),
                BiomeKeys.CODEC);
    }

    /** 26.1.2's SurfaceRules.BiomeConditionSource */
    public static final class BiomeKeys implements SurfaceRules.ConditionSource {
        static final MapCodec<BiomeKeys> CODEC = ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biome_is")
                .xmap(BiomeKeys::new, b -> b.biomes);

        private final List<ResourceKey<Biome>> biomes;
        private final Predicate<ResourceKey<Biome>> biomeNameTest;

        BiomeKeys(List<ResourceKey<Biome>> biomes) {
            this.biomes = biomes;
            Set<ResourceKey<Biome>> set = Set.copyOf(biomes);
            this.biomeNameTest = set::contains;
        }

        @Override
        public MapCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRules.Condition apply(SurfaceRules.Context context) {
            return () -> context.getBiome().is(biomeNameTest);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof BiomeKeys b && biomes.equals(b.biomes);
        }

        @Override
        public int hashCode() {
            return biomes.hashCode();
        }

        @Override
        public String toString() {
            return "BiomeConditionSource[biomes=" + biomes + "]";
        }
    }
}
