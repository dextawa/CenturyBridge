package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.BlockStateLoadedJson;

/**
 * 1.21.1's ModelBakery(blockColors, profiler, modelResources, blockStateResources) over 1.21.2's ModelBakery(topModels,
 * referencedModels, missingModel).
 *
 * <p>1.21.1's constructor did the whole discovery: the missing model, every blockstate's models (BlockStateModelLoader
 * over blockStateResources), the item and special models, then every parent and dependency, from the block models read
 * (modelResources: model file -> BlockModel); getModelGroups() answered the blockstate loader's groups. 1.21.2 splits
 * that: BlockStateModelLoader per block, ModelDiscovery (registerStandardModels: the blockstate models plus the item
 * and special ones; discoverDependencies), then the bakery over the discovery's top and referenced models -- the order
 * 1.21.2's ModelManager runs. Here 1.21.1's arguments run that order ({@link #discover}): the blockstates through
 * 1.21.1's loader (CbRenderBlockStates), the block models keyed by model id as 1.21.2's discovery takes them
 * (MODEL_LISTER.fileToId). The groups go to the bakery (CbRenderBakeryMixin). A 1.21.1 subclass's super(...) takes the
 * three 1.21.2 arguments from one discovery ({@link #topModels} then the pending ones; {@link #setup} after).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBakery {

    /** what CbRenderBakeryMixin keeps: 1.21.1's model groups */
    public interface Legacy {
        void cb$groups(it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> groups);
    }

    private record Discovered(Map<net.minecraft.class_1091, net.minecraft.class_1100> topModels,
                              Map<net.minecraft.class_2960, net.minecraft.class_1100> referencedModels, net.minecraft.class_1100 missingModel,
                              it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> groups) {
    }

    private static final ThreadLocal<Discovered> PENDING = new ThreadLocal<>();

    private CbRenderBakery() {
    }

    private static Discovered discover(net.minecraft.class_324 blockColors, net.minecraft.class_3695 profiler,
                                       Map<net.minecraft.class_2960, net.minecraft.class_793> modelResources,
                                       Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStateResources) {
        net.minecraft.class_1100 missing = net.minecraft.class_10096.method_62629();
        net.minecraft.class_9824 loader = CbRenderBlockStates.create(blockStateResources, profiler, missing, blockColors, (location, model) -> {
        });
        CbRenderBlockStates.Legacy legacy = (CbRenderBlockStates.Legacy) loader;
        legacy.method_61051();
        it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> groups = legacy.method_61069();
        Map<net.minecraft.class_2960, net.minecraft.class_1100> models = new HashMap<>();
        net.minecraft.class_7654 lister = net.minecraft.class_7654.method_45114("models");
        modelResources.forEach((file, model) -> models.put(lister.method_45115(file), model));
        net.minecraft.class_10097 discovery = new net.minecraft.class_10097(models, missing);
        discovery.method_62632(CbRenderBlockStates.loadedModels(loader));
        discovery.method_62630();
        return new Discovered(discovery.method_62637(), discovery.method_62639(), missing, groups);
    }

    /** 1.21.1 new ModelBakery(blockColors, profiler, modelResources, blockStateResources) (ctorRedirects) */
    public static net.minecraft.class_1088 create(net.minecraft.class_324 blockColors, net.minecraft.class_3695 profiler,
                                                  Map<net.minecraft.class_2960, net.minecraft.class_793> modelResources,
                                                  Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStateResources) {
        Discovered d = discover(blockColors, profiler, modelResources, blockStateResources);
        net.minecraft.class_1088 bakery = new net.minecraft.class_1088(d.topModels(), d.referencedModels(), d.missingModel());
        ((Legacy) bakery).cb$groups(d.groups());
        return bakery;
    }

    /** super(blockColors, profiler, modelResources, blockStateResources) of a 1.21.1 subclass: the top models (conv:0,1,2,3) */
    public static Map<net.minecraft.class_1091, net.minecraft.class_1100> topModels(net.minecraft.class_324 blockColors, net.minecraft.class_3695 profiler,
                                                                                    Map<net.minecraft.class_2960, net.minecraft.class_793> modelResources,
                                                                                    Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStateResources) {
        Discovered d = discover(blockColors, profiler, modelResources, blockStateResources);
        PENDING.set(d);
        return d.topModels();
    }

    /** the same discovery's referenced models (push) */
    public static Map<net.minecraft.class_2960, net.minecraft.class_1100> referencedModels() {
        return PENDING.get().referencedModels();
    }

    /** the same discovery's missing model (push) */
    public static net.minecraft.class_1100 missingModel() {
        return PENDING.get().missingModel();
    }

    /** right after the subclass's super(...) (superCtorReshapes post): the groups to the bakery */
    public static void setup(net.minecraft.class_1088 bakery) {
        Discovered d = PENDING.get();
        PENDING.remove();
        if (d == null) {
            throw new IllegalStateException("1.21.1 ModelBakery: no discovery ran for this constructor");
        }
        ((Legacy) bakery).cb$groups(d.groups());
    }
}
