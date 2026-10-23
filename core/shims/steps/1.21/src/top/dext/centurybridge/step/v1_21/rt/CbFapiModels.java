package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.class_1091;
import net.minecraft.class_1100;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_7923;
import top.dext.centurybridge.step.v1_21.compat.fapi.client.model.ExtraModelProvider;
import top.dext.centurybridge.step.v1_21.compat.fapi.client.model.ModelAppender;
import top.dext.centurybridge.step.v1_21.compat.fapi.client.model.ModelLoadingRegistry;
import top.dext.centurybridge.step.v1_21.compat.fapi.client.model.ModelProviderContext;
import top.dext.centurybridge.step.v1_21.compat.fapi.client.model.ModelProviderException;
import top.dext.centurybridge.step.v1_21.compat.fapi.client.model.ModelResourceProvider;
import top.dext.centurybridge.step.v1_21.compat.fapi.client.model.ModelVariantProvider;

/**
 * Fabric API 1.20.6's fabric-models-v0 on 1.21's model loading API, as fabric-models-v0 1.20.6 itself ran on
 * 1.20.6's (ModelLoadingRegistryImpl): one preparable model loading plugin, registered when the registry is made,
 * whose data is the resource manager and which, at every model load,
 * <ul>
 * <li>adds every extra model id (ExtraModelProvider) -- addModels, as 1.20.6;</li>
 * <li>adds every appended model location (ModelAppender) -- 1.20.6 added the location itself, a
 *     ResourceLocation then; 1.21 loads every registered block's and item's model locations anyway and can add
 *     model ids only, so a location 1.21 does not load already cannot be added, and is logged;</li>
 * <li>resolves model ids through every resource provider (ModelResolver), as 1.20.6;</li>
 * <li>answers top-level model locations through every variant provider: 1.20.6 asked them before reading the
 *     blockstate file (an internal event of its model loading); 1.21's Fabric API has no such event, so they are
 *     asked when the location's model has loaded (ModelModifier.OnLoad, top-level ids) and a model they supply
 *     replaces it -- the same model in the end, after 1.21 tried the blockstate file.</li>
 * </ul>
 * Each hook gets a ModelProviderContext loading models through the 1.21 context it runs in. A provider's
 * ModelProviderException is rethrown as a RuntimeException, as 1.20.6 did.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiModels implements ModelLoadingRegistry {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private final List<ExtraModelProvider> modelProviders = new ArrayList<>();
    private final List<ModelAppender> modelAppenders = new ArrayList<>();
    private final List<Function<class_3300, ModelResourceProvider>> resourceProviderSuppliers = new ArrayList<>();
    private final List<Function<class_3300, ModelVariantProvider>> variantProviderSuppliers = new ArrayList<>();

    public CbFapiModels() {
        PreparableModelLoadingPlugin.register((manager, executor) -> CompletableFuture.completedFuture(manager), this::onInitializeModelLoader);
    }

    private void onInitializeModelLoader(class_3300 manager, ModelLoadingPlugin.Context context) {
        for (ExtraModelProvider provider : this.modelProviders) {
            provider.provideExtraModels(manager, id -> context.addModels(id));
        }
        for (ModelAppender appender : this.modelAppenders) {
            appender.appendAll(manager, location -> {
                if (!loadedAnyway(location)) {
                    LOGGER.warn("Fabric API 1.20.6 ModelAppender: 1.21 cannot add model location {} (only model ids, and every block's and item's own locations)", location);
                }
            });
        }
        for (Function<class_3300, ModelResourceProvider> supplier : this.resourceProviderSuppliers) {
            ModelResourceProvider provider = supplier.apply(manager);
            context.resolveModel().register(resolver -> {
                try {
                    return provider.loadModelResource(resolver.id(), resolver::getOrLoadModel);
                } catch (ModelProviderException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        for (Function<class_3300, ModelVariantProvider> supplier : this.variantProviderSuppliers) {
            ModelVariantProvider provider = supplier.apply(manager);
            context.modifyModelOnLoad().register((model, load) -> {
                class_1091 location = load.topLevelId();
                if (location == null) {
                    return model;
                }
                try {
                    ModelProviderContext loader = load::getOrLoadModel;
                    class_1100 supplied = provider.loadModelVariant(location, loader);
                    return supplied != null ? supplied : model;
                } catch (ModelProviderException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /** a location 1.21 loads itself: a registered block's blockstate variant or a registered item's inventory model */
    private static boolean loadedAnyway(class_1091 location) {
        if ("inventory".equals(location.comp_2876())) {
            return class_7923.field_41178.method_10250(location.comp_2875());
        }
        return class_7923.field_41175.method_10250(location.comp_2875());
    }

    @Override
    public void registerModelProvider(ExtraModelProvider provider) {
        this.modelProviders.add(provider);
    }

    @Override
    public void registerAppender(ModelAppender appender) {
        this.modelAppenders.add(appender);
    }

    @Override
    public void registerResourceProvider(Function<class_3300, ModelResourceProvider> providerSupplier) {
        this.resourceProviderSuppliers.add(providerSupplier);
    }

    @Override
    public void registerVariantProvider(Function<class_3300, ModelVariantProvider> providerSupplier) {
        this.variantProviderSuppliers.add(providerSupplier);
    }

    // ------------------------------------------------------------------------------------------------ model-loading v1 contexts

    /**
     * Fabric API 1.20.6 ModelModifier.*.Context.id(): the model's id -- 1.21 names a model loaded from a model file by
     * resourceId() and a top-level one by topLevelId(), a ModelResourceLocation 1.20.6 handed as itself (a
     * ResourceLocation then); 1.21's is no ResourceLocation, so a top-level model gets its location's id.
     */
    public static class_2960 id(ModelModifier.OnLoad.Context context) {
        return context.resourceId() != null ? context.resourceId() : context.topLevelId().comp_2875();
    }

    public static class_2960 id(ModelModifier.BeforeBake.Context context) {
        return context.resourceId() != null ? context.resourceId() : context.topLevelId().comp_2875();
    }

    public static class_2960 id(ModelModifier.AfterBake.Context context) {
        return context.resourceId() != null ? context.resourceId() : context.topLevelId().comp_2875();
    }
}
