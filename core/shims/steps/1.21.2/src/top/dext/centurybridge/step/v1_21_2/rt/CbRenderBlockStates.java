package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.BlockStateLoadedJson;

/**
 * 1.21.1's BlockStateModelLoader(blockStates, profiler, missingModel, blockColors, output) over 1.21.2's
 * BlockStateModelLoader(missingModel).
 *
 * <p>1.21.1: the loader was built over the blockstate jsons ModelManager had read (id -> LoadedJson per pack) and
 * loadAllBlockStates() loaded the static definitions (item frames) and every registered block: each json parsed to a
 * BlockModelDefinition, the definitions of one block combined into a model per state, each handed to the output under
 * the state's ModelResourceLocation (the missing model for a state no definition covers, all of a block's states when
 * its definitions failed to load); getModelGroups() answered the states grouped by identical models and colouring
 * properties. 1.21.2's loader does the per-block part (loadBlockStateDefinitions(id, stateDefinition, definitions):
 * the same combination and missing-model fallback) and its ModelManager groups the states (buildModelGroups, the same
 * grouping). The 1.21.1 constructor keeps its arguments on the 1.21.2 loader (CbRenderBlockStateLoaderMixin) and
 * loadAllBlockStates / getModelGroups run 1.21.1's loop over those 1.21.2 parts.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBlockStates {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /** 1.21.1 BlockStateModelLoader.BLOCKSTATE_LISTER (fieldRedirects) */
    public static final net.minecraft.class_7654 field_52260 = net.minecraft.class_7654.method_45114("blockstates");

    /** what CbRenderBlockStateLoaderMixin keeps of 1.21.1's constructor */
    public interface Legacy {
        void cb$legacy(Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStates, net.minecraft.class_3695 profiler,
                       net.minecraft.class_324 blockColors, BiConsumer<net.minecraft.class_1091, net.minecraft.class_1100> output);

        /** the states' models loaded so far */
        Map<net.minecraft.class_1091, net.minecraft.class_9824.class_9825> cb$loaded();

        /** 1.21.1 loadAllBlockStates() */
        void method_61051();

        /** 1.21.1 getModelGroups() */
        it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> method_61069();
    }

    /** the models 1.21.1's loadAllBlockStates loaded, as 1.21.2's LoadedModels */
    public static net.minecraft.class_9824.class_10095 loadedModels(net.minecraft.class_9824 loader) {
        return new net.minecraft.class_9824.class_10095(new HashMap<>(((Legacy) loader).cb$loaded()));
    }

    private CbRenderBlockStates() {
    }

    /** 1.21.1 new BlockStateModelLoader(blockStates, profiler, missingModel, blockColors, output) (ctorRedirects) */
    public static net.minecraft.class_9824 create(Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStates, net.minecraft.class_3695 profiler,
                                                  net.minecraft.class_1100 missingModel, net.minecraft.class_324 blockColors,
                                                  BiConsumer<net.minecraft.class_1091, net.minecraft.class_1100> output) {
        net.minecraft.class_9824 loader = new net.minecraft.class_9824(missingModel);
        legacy(loader, blockStates, profiler, blockColors, output);
        return loader;
    }

    /** after super(missingModel) of a 1.21.1 subclass (superCtorReshapes post), or {@link #create} */
    public static void legacy(net.minecraft.class_9824 loader, Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStates,
                              net.minecraft.class_3695 profiler, net.minecraft.class_324 blockColors,
                              BiConsumer<net.minecraft.class_1091, net.minecraft.class_1100> output) {
        ((Legacy) loader).cb$legacy(blockStates, profiler, blockColors, output);
    }

    /**
     * 1.21.1 loadBlockStateDefinitions(id, stateDefinition): the block's jsons parsed and combined by 1.21.2's loader;
     * every state's model handed to the output and kept (for the groups); on a failure, as 1.21.1, a warning and the
     * missing model for every state of the block
     */
    public static void load(net.minecraft.class_9824 loader, Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStates,
                            BiConsumer<net.minecraft.class_1091, net.minecraft.class_1100> output, net.minecraft.class_1100 missingModel,
                            Map<net.minecraft.class_1091, net.minecraft.class_9824.class_9825> loaded, net.minecraft.class_2960 id,
                            net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> stateDefinition) {
        net.minecraft.class_2960 file = field_52260.method_45112(id);
        try {
            top.dext.centurybridge.step.v1_21_2.facade.BlockModelDefinitionContext context = new top.dext.centurybridge.step.v1_21_2.facade.BlockModelDefinitionContext();
            context.method_3426(stateDefinition);
            List<net.minecraft.class_9824.class_10094> definitions = new java.util.ArrayList<>();
            for (BlockStateLoadedJson json : blockStates.getOrDefault(file, List.of())) {
                definitions.add(new net.minecraft.class_9824.class_10094(json.comp_1056(), json.method_61071(id, context)));
            }
            loader.method_62627(id, stateDefinition, definitions).comp_3063().forEach((location, model) -> {
                output.accept(location, model.comp_2871());
                loaded.put(location, model);
            });
        } catch (Exception e) {
            LOGGER.warn("Exception loading blockstate definition: '{}'", id, e);
            for (net.minecraft.class_2680 state : stateDefinition.method_11662()) {
                net.minecraft.class_1091 location = net.minecraft.class_773.method_3336(id, state);
                output.accept(location, missingModel);
                loaded.put(location, new net.minecraft.class_9824.class_9825(state, missingModel));
            }
        }
    }

    /** 1.21.1 getModelGroups(): 1.21.2's grouping of the states loaded so far */
    public static it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> groups(net.minecraft.class_324 blockColors,
                                                                                              Map<net.minecraft.class_1091, net.minecraft.class_9824.class_9825> loaded) {
        return net.minecraft.class_1092.method_62654(blockColors, new net.minecraft.class_9824.class_10095(new HashMap<>(loaded)));
    }
}
