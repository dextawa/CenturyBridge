package cb.s6t;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.class_1299;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2474;
import net.minecraft.class_2591;
import net.minecraft.class_2960;
import net.minecraft.class_3612;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_7225;
import net.minecraft.class_7784;
import net.minecraft.class_7805;
import net.minecraft.class_7889;
import net.minecraft.class_7924;

/**
 * s6h2 fixture: a 1.21.5-era datagen entrypoint exercising the tag datagen API that 1.21.6 rebuilt:
 * <ul>
 * <li>vanilla TagsProvider.tag / TagAppender;</li>
 * <li>IntrinsicHolderTagsProvider.tag / IntrinsicTagAppender;</li>
 * <li>ItemTagsProvider.copy;</li>
 * <li>Fabric API 0.128.2+1.21.5's FabricTagProvider: getOrCreateTagBuilder, every FabricTagBuilder member,
 *     reverseLookup and a mod override of it, the per-registry providers, EnchantmentTagProvider and copy.</li>
 * </ul>
 * Run with Fabric API datagen on a native 1.21.5 server, then climbed on a 1.21.6 server. The generated tag files must
 * be byte-identical.
 */
public final class S6tDatagen implements DataGeneratorEntrypoint {

    static class_2960 id(String path) {
        return class_2960.method_60655("cb_s6t", path);
    }

    static <T> class_6862<T> tag(class_5321<? extends class_2378<T>> registry, String path) {
        return class_6862.method_40092(registry, id(path));
    }

    static <T> class_5321<T> key(class_5321<? extends class_2378<T>> registry, String ns, String path) {
        return class_5321.method_29179(registry, class_2960.method_60655(ns, path));
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        VBlocks vblocks = pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<VBlocks>) VBlocks::new);
        pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<VItems>) (out, lookup) -> new VItems(out, lookup, vblocks.method_49662()));
        pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<VEntities>) VEntities::new);
        // provider names are unique per pack (one per registry): the Fabric API providers and the custom item provider get
        // packs of their own
        FabricDataGenerator.Pack fpack = generator.createBuiltinResourcePack(id("fapi"));
        FBlocks fblocks = fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FBlocks>) FBlocks::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FItems>) (out, lookup) -> new FItems(out, lookup, fblocks));
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FFluids>) FFluids::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FEntities>) FEntities::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FBlockEntities>) FBlockEntities::new);
        fpack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FEnchantments>) FEnchantments::new);
        generator.createBuiltinResourcePack(id("custom")).addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<FCustomItems>) FCustomItems::new);
    }

    /** vanilla TagsProvider: tag(TagKey) and every TagAppender member */
    static final class VBlocks extends class_2474<class_2248> {
        VBlocks(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, class_7924.field_41254, lookup);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.method_10512(tag(class_7924.field_41254, "v_blocks"))
                .method_46835(key(class_7924.field_41254, "minecraft", "stone"))
                .method_40565(key(class_7924.field_41254, "minecraft", "dirt"), key(class_7924.field_41254, "minecraft", "granite"))
                .method_55842(List.of(key(class_7924.field_41254, "minecraft", "oak_planks")))
                .method_35922(class_2960.method_60655("othermod", "optional_block"))
                .method_26792(tag(class_7924.field_41254, "v_copy_src"))
                .method_35923(class_2960.method_60655("othermod", "optional_tag"));
            this.method_10512(tag(class_7924.field_41254, "v_copy_src"))
                .method_46835(key(class_7924.field_41254, "minecraft", "dirt"))
                .method_35923(class_2960.method_60655("othermod", "copied_optional_tag"));
            this.method_10512(tag(class_7924.field_41254, "v_leaf")).method_46835(key(class_7924.field_41254, "minecraft", "granite"));
        }
    }

    /** vanilla ItemTagsProvider: copy plus IntrinsicTagAppender */
    static final class VItems extends class_7805 {
        VItems(class_7784 output, CompletableFuture<class_7225.class_7874> lookup, CompletableFuture<class_2474.class_8211<class_2248>> blocks) {
            super(output, lookup, blocks);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.method_46218(tag(class_7924.field_41254, "v_copy_src"), tag(class_7924.field_41197, "v_copy_dst"));
            this.method_46827(tag(class_7924.field_41197, "v_items"))
                .method_46829(class_1802.field_8477)
                .method_46830(class_1802.field_8687, class_1802.field_8600)
                .method_46828(tag(class_7924.field_41197, "v_items_via_tag"))
                .method_46835(key(class_7924.field_41197, "minecraft", "gold_ingot"));
            this.method_10512(tag(class_7924.field_41197, "v_items_via_tag")).method_26792(tag(class_7924.field_41197, "v_copy_dst"));
        }
    }

    /** vanilla IntrinsicHolderTagsProvider of entity types */
    static final class VEntities extends class_7889<class_1299<?>> {
        VEntities(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, class_7924.field_41266, lookup, type -> type.method_40124().method_40237());
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.method_46827(tag(class_7924.field_41266, "v_entities")).method_46829(class_1299.field_6093).method_46830(class_1299.field_6085);
        }
    }

    /** Fabric API BlockTagProvider: every FabricTagBuilder member */
    static final class FBlocks extends FabricTagProvider.BlockTagProvider {
        FBlocks(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, lookup);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.getOrCreateTagBuilder(tag(class_7924.field_41254, "f_blocks"))
                .add(class_2246.field_10340)
                .add(class_2246.field_10566, class_2246.field_10474)
                .add(key(class_7924.field_41254, "minecraft", "oak_log"))
                .add(class_2960.method_60655("minecraft", "birch_log"))
                .addOptional(class_2960.method_60655("othermod", "optional_block"))
                .addOptional(key(class_7924.field_41254, "othermod", "optional_block2"))
                .addTag(tag(class_7924.field_41254, "f_copy_src"))
                .addTags(tag(class_7924.field_41254, "f_replace"))
                .addOptionalTag(class_2960.method_60655("othermod", "optional_tag"))
                .addOptionalTag(tag(class_7924.field_41254, "optional_tag2"))
                .forceAddTag(tag(class_7924.field_41254, "forced"))
                .forceAddTags(tag(class_7924.field_41254, "forced2"))
                .add(class_2960.method_60655("minecraft", "spruce_log"), class_2960.method_60655("minecraft", "jungle_log"))
                .add(key(class_7924.field_41254, "minecraft", "acacia_log"), key(class_7924.field_41254, "minecraft", "dark_oak_log"));
            this.getOrCreateTagBuilder(tag(class_7924.field_41254, "f_replace")).setReplace(true).add(class_2246.field_10340);
            this.method_10512(tag(class_7924.field_41254, "f_via_tag")).method_46835(key(class_7924.field_41254, "minecraft", "dirt"));
            this.getOrCreateTagBuilder(tag(class_7924.field_41254, "f_copy_src")).add(class_2246.field_10566).addOptionalTag(class_2960.method_60655("othermod", "copied_optional_tag"));
            this.getOrCreateTagBuilder(tag(class_7924.field_41254, "forced")).add(class_2246.field_10340);
            this.getOrCreateTagBuilder(tag(class_7924.field_41254, "forced2")).add(class_2246.field_10566);
        }
    }

    /** Fabric API ItemTagProvider with copy */
    static final class FItems extends FabricTagProvider.ItemTagProvider {
        FItems(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup, FabricTagProvider.BlockTagProvider blocks) {
            super(output, lookup, blocks);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.copy(tag(class_7924.field_41254, "f_copy_src"), tag(class_7924.field_41197, "f_copy_dst"));
            this.getOrCreateTagBuilder(tag(class_7924.field_41197, "f_items")).add(class_1802.field_8477).add(class_1802.field_8687, class_1802.field_8600);
        }
    }

    static final class FFluids extends FabricTagProvider.FluidTagProvider {
        FFluids(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, lookup);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.getOrCreateTagBuilder(tag(class_7924.field_41270, "f_fluids")).add(class_3612.field_15910).add(class_3612.field_15908);
        }
    }

    static final class FEntities extends FabricTagProvider.EntityTypeTagProvider {
        FEntities(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, lookup);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.getOrCreateTagBuilder(tag(class_7924.field_41266, "f_entities")).add(class_1299.field_6093).add(class_1299.field_6085);
        }
    }

    static final class FBlockEntities extends FabricTagProvider.BlockEntityTypeTagProvider {
        FBlockEntities(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, lookup);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.getOrCreateTagBuilder(tag(class_7924.field_41255, "f_block_entities")).add(class_2591.field_11914).add(class_2591.field_11903);
        }
    }

    static final class FEnchantments extends FabricTagProvider.EnchantmentTagProvider {
        FEnchantments(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, lookup);
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.getOrCreateTagBuilder(tag(class_7924.field_41265, "f_enchantments"))
                .add(class_1893.field_9118)
                .addOptional(class_2960.method_60655("othermod", "optional_enchantment"));
        }
    }

    /** a mod's own FabricTagProvider overriding reverseLookup: the stick answers the diamond's key, visible in the output */
    static final class FCustomItems extends FabricTagProvider<class_1792> {
        FCustomItems(FabricDataOutput output, CompletableFuture<class_7225.class_7874> lookup) {
            super(output, class_7924.field_41197, lookup);
        }

        @Override
        protected class_5321<class_1792> reverseLookup(class_1792 item) {
            return item == class_1802.field_8600 ? key(class_7924.field_41197, "minecraft", "diamond") : item.method_40131().method_40237();
        }

        @Override
        protected void method_10514(class_7225.class_7874 lookup) {
            this.getOrCreateTagBuilder(tag(class_7924.field_41197, "f_custom")).add(class_1802.field_8600).add(class_1802.field_8687);
        }
    }
}
