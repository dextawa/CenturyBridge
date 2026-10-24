package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.DelegatingUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;

/**
 * 1.21.1 Fabric API model-loading context members 1.21.2's dropped (instanceRedirects).
 *
 * <p>1.21.1: one ModelLoader (ModelBakery) loaded, resolved and baked; every context answered it (loader()) and the
 * loading contexts loaded or got an unbaked model by id through it (getOrLoadModel: the json model, or a resolver's,
 * after the on-load modifiers). 1.21.2 loads in stages: block-state models (BlockStateResolver), then a ModelDiscovery
 * that loads and resolves the models (ModelResolver, OnLoad -- Fabric API hooks its getModel), then a ModelBakery that
 * bakes (BeforeBake, AfterBake).
 * <ul>
 * <li>OnLoad / ModelResolver getOrLoadModel(id): the discovery running on this thread (CbFapiModelDiscoveryMixin; it
 *     is built and resolves on one thread) answers its getModel(id) -- 1.21.2's get-or-load, through the resolvers and
 *     on-load modifiers as 1.21.1's was.</li>
 * <li>BlockStateResolver getOrLoadModel(id): no model is loaded yet in that stage (the json models load alongside);
 *     the answer is Fabric API's DelegatingUnbakedModel of the id, which resolves and bakes as that model.</li>
 * <li>BeforeBake / AfterBake loader(): the ModelBakery baking (CbFapiModelBakeryMixin).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbFapiModels {

    private static final ThreadLocal<net.minecraft.class_10097> DISCOVERY = new ThreadLocal<>();
    private static volatile net.minecraft.class_1088 bakery;

    private CbFapiModels() {
    }

    public static void discovery(net.minecraft.class_10097 discovery) {
        DISCOVERY.set(discovery);
    }

    public static void bakery(net.minecraft.class_1088 modelBakery) {
        bakery = modelBakery;
    }

    private static net.minecraft.class_1100 discovered(net.minecraft.class_2960 id) {
        net.minecraft.class_10097 discovery = DISCOVERY.get();
        if (discovery == null) {
            throw new IllegalStateException("getOrLoadModel(" + id + ") outside model discovery");
        }
        return discovery.method_62638(id);
    }

    private static net.minecraft.class_1088 baking() {
        net.minecraft.class_1088 current = bakery;
        if (current == null) {
            throw new IllegalStateException("loader() outside model baking");
        }
        return current;
    }

    /** ModelModifier.OnLoad.Context.getOrLoadModel(id) */
    public static net.minecraft.class_1100 onLoadModel(ModelModifier.OnLoad.Context context, net.minecraft.class_2960 id) {
        return discovered(id);
    }

    /** ModelResolver.Context.getOrLoadModel(id) */
    public static net.minecraft.class_1100 resolverModel(ModelResolver.Context context, net.minecraft.class_2960 id) {
        return discovered(id);
    }

    /** BlockStateResolver.Context.getOrLoadModel(id) */
    public static net.minecraft.class_1100 blockStateModel(BlockStateResolver.Context context, net.minecraft.class_2960 id) {
        return new DelegatingUnbakedModel(id);
    }

    /** ModelModifier.BeforeBake.Context.loader() */
    public static net.minecraft.class_1088 beforeBakeLoader(ModelModifier.BeforeBake.Context context) {
        return baking();
    }

    /** ModelModifier.AfterBake.Context.loader() */
    public static net.minecraft.class_1088 afterBakeLoader(ModelModifier.AfterBake.Context context) {
        return baking();
    }

    /** DelegatingUnbakedModel.getDependencies(): 1.21.1's list of the one delegate, read off resolveDependencies (which keeps nothing) */
    public static Collection<net.minecraft.class_2960> method_4755(DelegatingUnbakedModel model) {
        List<net.minecraft.class_2960> ids = new ArrayList<>();
        model.method_62326(id -> {
            ids.add(id);
            return null;
        });
        return List.copyOf(ids);
    }

    /** DelegatingUnbakedModel.resolveParents(getter): UnbakedModel's 1.21.1 resolveParents, its resolveDependencies with the getter (CbRenderBakeUnbakedMixin) */
    public static void method_45785(DelegatingUnbakedModel model, java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1100> getter) {
        model.method_62326(getter::apply);
    }
}
