package cb.s26t;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagAppender;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.HolderTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.data.tags.PotionTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * s26g fixture: a 26.1.2-era datagen entrypoint exercising the tag datagen API that 26.2 rebuilt (javap of both):
 * <ul>
 * <li>KeyTagProvider, IntrinsicHolderTagsProvider and HolderTagProvider (deleted at 26.2), each subclassed by the mod, and
 *     the vanilla providers 26.1.2 derived from them (BiomeTagsProvider, EntityTypeTagsProvider, PotionTagsProvider);</li>
 * <li>TagAppender&lt;E, T&gt; (26.2: TagAppender&lt;T&gt; of keys only): add / add... / addAll(Collection) / addAll(Stream) /
 *     addOptional / addTag / addOptionalTag / map, tag(TagKey, boolean);</li>
 * <li>Fabric API 0.155.3+26.1.2: FabricTagsProvider.builder, FabricIntrinsicHolderTagsProvider (deleted at 26.2) and its
 *     valueLookupBuilder through the per-registry providers, FabricTagAppender&lt;E, T&gt; (setReplace, forceAddTag, remove,
 *     remove..., removeAll(Collection), removeAll(Stream), removeTag), ItemTagsProvider.copy.</li>
 * </ul>
 * Fabric API injects FabricTagAppender into TagAppender at runtime (and loom at compile time); compiled here with plain
 * javac, those calls go through a cast to FabricTagAppender. Run with Fabric API datagen on a native 26.1.2 server, then
 * climbed on a 26.2 server. The generated files must be byte-identical.
 */
public final class S26tDatagen implements DataGeneratorEntrypoint {

    static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("cb_s26t", path);
    }

    static <T> TagKey<T> tagKey(ResourceKey<? extends net.minecraft.core.Registry<T>> registry, String path) {
        return TagKey.create(registry, id(path));
    }

    /** the appender as the FabricTagAppender Fabric API makes every TagAppender at runtime */
    @SuppressWarnings("unchecked")
    static <E, T> FabricTagAppender<E, T> fabric(TagAppender<E, T> appender) {
        return (FabricTagAppender<E, T>) (Object) appender;
    }

    static <T> ResourceKey<T> key(ResourceKey<? extends net.minecraft.core.Registry<T>> registry, String ns, String path) {
        return ResourceKey.create(registry, Identifier.fromNamespaceAndPath(ns, path));
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<KeyBiomes>) KeyBiomes::new);
        pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<IntrinsicItems>) IntrinsicItems::new);
        pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<HolderPotions>) HolderPotions::new);
        pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<VEntities>) VEntities::new);
        FabricDataGenerator.Pack vpack = generator.createBuiltinResourcePack(id("vanilla_derived"));
        vpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<VBiomes>) VBiomes::new);
        vpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<VPotions>) VPotions::new);
        FabricDataGenerator.Pack fpack = generator.createBuiltinResourcePack(id("fapi"));
        FBlocks fblocks = fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FBlocks>) FBlocks::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FItems>) (out, lookup) -> new FItems(out, lookup, fblocks));
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FFluids>) FFluids::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FEntities>) FEntities::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FBlockEntities>) FBlockEntities::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FPotions>) FPotions::new);
        FabricDataGenerator.Pack ipack = generator.createBuiltinResourcePack(id("intrinsic"));
        ipack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FIntrinsicItems>) FIntrinsicItems::new);
    }

    /** a mod's own KeyTagProvider: tag(TagKey) and tag(TagKey, boolean), every TagAppender member over keys */
    static final class KeyBiomes extends KeyTagProvider<Biome> {
        KeyBiomes(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, Registries.BIOME, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.tag(tagKey(Registries.BIOME, "k_biomes"))
                .add(Biomes.PLAINS)
                .add(Biomes.DESERT, Biomes.FOREST)
                .addAll(List.of(Biomes.TAIGA))
                .addAll(Stream.of(Biomes.SWAMP, Biomes.JUNGLE))
                .addOptional(key(Registries.BIOME, "othermod", "optional_biome"))
                .addTag(tagKey(Registries.BIOME, "k_leaf"))
                .addOptionalTag(tagKey(Registries.BIOME, "k_optional_tag"));
            this.tag(tagKey(Registries.BIOME, "k_leaf")).add(Biomes.BEACH);
            this.tag(tagKey(Registries.BIOME, "k_replace"), true).add(Biomes.OCEAN);
            this.tag(tagKey(Registries.BIOME, "k_mapped")).map((String s) -> key(Registries.BIOME, "minecraft", s)).add("river").addOptional("frozen_river");
        }
    }

    /** a mod's own IntrinsicHolderTagsProvider of items keyed by their built-in holder */
    static final class IntrinsicItems extends IntrinsicHolderTagsProvider<Item> {
        IntrinsicItems(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, Registries.ITEM, lookup, item -> item.builtInRegistryHolder().key());
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.tag(tagKey(Registries.ITEM, "i_items"))
                .add(Items.STICK)
                .add(Items.APPLE, Items.DIAMOND)
                .addAll(List.of(Items.GOLD_INGOT))
                .addOptional(Items.EMERALD)
                .addTag(tagKey(Registries.ITEM, "i_leaf"));
            this.tag(tagKey(Registries.ITEM, "i_leaf")).add(Items.IRON_INGOT);
            this.tag(tagKey(Registries.ITEM, "i_mapped")).map((Block b) -> b.asItem()).add(Blocks.STONE).addOptional(Blocks.DIRT);
        }
    }

    /** a mod's own HolderTagProvider of potions */
    static final class HolderPotions extends HolderTagProvider<Potion> {
        HolderPotions(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, Registries.POTION, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.tag(tagKey(Registries.POTION, "h_potions")).add(Potions.LEAPING).add(Potions.SWIFTNESS, Potions.HEALING).addOptional(Potions.WATER);
        }
    }

    /** vanilla EntityTypeTagsProvider (26.1.2: an IntrinsicHolderTagsProvider) */
    static final class VEntities extends EntityTypeTagsProvider {
        VEntities(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.tag(tagKey(Registries.ENTITY_TYPE, "v_entities")).add(EntityType.PIG).add(EntityType.COW, EntityType.SHEEP);
        }
    }

    /** vanilla BiomeTagsProvider (26.1.2: a KeyTagProvider) */
    static final class VBiomes extends BiomeTagsProvider {
        VBiomes(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.tag(tagKey(Registries.BIOME, "v_biomes")).add(Biomes.PLAINS).addTag(tagKey(Registries.BIOME, "v_leaf"));
            this.tag(tagKey(Registries.BIOME, "v_leaf")).add(Biomes.BEACH);
        }
    }

    /** vanilla PotionTagsProvider (26.1.2: a HolderTagProvider) */
    static final class VPotions extends PotionTagsProvider {
        VPotions(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.tag(tagKey(Registries.POTION, "v_potions")).add(Potions.LEAPING);
        }
    }

    /** Fabric API BlockTagsProvider: valueLookupBuilder and every FabricTagAppender member */
    static final class FBlocks extends FabricTagsProvider.BlockTagsProvider {
        FBlocks(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            TagAppender<Block, Block> blocks = this.valueLookupBuilder(tagKey(Registries.BLOCK, "f_blocks"))
                .add(Blocks.STONE)
                .add(Blocks.DIRT, Blocks.GRANITE)
                .addOptional(Blocks.OAK_LOG)
                .addTag(tagKey(Registries.BLOCK, "f_copy_src"));
            fabric(blocks).forceAddTag(tagKey(Registries.BLOCK, "forced"));
            fabric(blocks).remove(Blocks.DIORITE);
            fabric(blocks).remove(Blocks.ANDESITE, Blocks.TUFF);
            fabric(blocks).removeAll(List.of(Blocks.CALCITE));
            fabric(blocks).removeAll(Stream.of(Blocks.DRIPSTONE_BLOCK));
            fabric(blocks).removeTag(tagKey(Registries.BLOCK, "removed_tag"));
            fabric(this.valueLookupBuilder(tagKey(Registries.BLOCK, "f_replace"))).setReplace(true).add(Blocks.STONE);
            TagAppender<ResourceKey<Block>, Block> keys = this.builder(tagKey(Registries.BLOCK, "f_keys")).add(key(Registries.BLOCK, "minecraft", "birch_log"));
            fabric(keys).remove(key(Registries.BLOCK, "minecraft", "spruce_log"));
            this.valueLookupBuilder(tagKey(Registries.BLOCK, "f_copy_src")).add(Blocks.DIRT).addOptionalTag(tagKey(Registries.BLOCK, "copied_optional_tag"));
            this.valueLookupBuilder(tagKey(Registries.BLOCK, "forced")).add(Blocks.STONE);
        }
    }

    /** Fabric API ItemTagsProvider with copy */
    static final class FItems extends FabricTagsProvider.ItemTagsProvider {
        FItems(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup, FabricTagsProvider.BlockTagsProvider blocks) {
            super(output, lookup, blocks);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.copy(tagKey(Registries.BLOCK, "f_copy_src"), tagKey(Registries.ITEM, "f_copy_dst"));
            this.valueLookupBuilder(tagKey(Registries.ITEM, "f_items")).add(Items.STICK).add(Items.APPLE, Items.DIAMOND);
        }
    }

    static final class FFluids extends FabricTagsProvider.FluidTagsProvider {
        FFluids(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.valueLookupBuilder(tagKey(Registries.FLUID, "f_fluids")).add(Fluids.WATER).add(Fluids.LAVA);
        }
    }

    static final class FEntities extends FabricTagsProvider.EntityTypeTagsProvider {
        FEntities(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.valueLookupBuilder(tagKey(Registries.ENTITY_TYPE, "f_entities")).add(EntityType.PIG).add(EntityType.COW);
        }
    }

    static final class FBlockEntities extends FabricTagsProvider.BlockEntityTypeTagsProvider {
        FBlockEntities(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.valueLookupBuilder(tagKey(Registries.BLOCK_ENTITY_TYPE, "f_block_entities")).add(BlockEntityType.CHEST).add(BlockEntityType.FURNACE);
        }
    }

    /** a mod's own FabricTagsProvider of keys: builder over keys, removal by key */
    static final class FPotions extends FabricTagsProvider<Potion> {
        FPotions(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, Registries.POTION, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            fabric(this.builder(tagKey(Registries.POTION, "f_potions")).add(Potions.LEAPING.key())).remove(Potions.WATER.key());
        }
    }

    /** a mod's own FabricIntrinsicHolderTagsProvider, with its own key function */
    static final class FIntrinsicItems extends FabricTagsProvider.FabricIntrinsicHolderTagsProvider<Item> {
        FIntrinsicItems(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, Registries.ITEM, lookup, item -> item == Items.STICK ? key(Registries.ITEM, "minecraft", "diamond") : item.builtInRegistryHolder().key());
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            this.valueLookupBuilder(tagKey(Registries.ITEM, "f_custom")).add(Items.STICK).add(Items.APPLE);
        }
    }
}
