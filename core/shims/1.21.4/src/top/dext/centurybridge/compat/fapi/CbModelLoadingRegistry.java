package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

/**
 * Stand-in for fabric-api's removed ModelLoadingRegistry (fabric-models-v0).
 * The shape mods compiled against is GETSTATIC INSTANCE + an interface call,
 * so this keeps exactly that surface and adapts the one method the corpus
 * actually uses onto ModelLoadingPlugin. The other legacy hooks are stubbed
 * with a log line rather than silently ignored -- a mod relying on them loses
 * a feature, not the truth about why.
 */
public interface CbModelLoadingRegistry {

    CbModelLoadingRegistry INSTANCE = new CbModelLoadingRegistry() {};

    default void registerModelProvider(CbExtraModelProvider provider) {
        ModelLoadingPlugin.register(ctx -> provider.provideExtraModels(
            net.minecraft.class_310.method_1551().method_1478(), ctx::addModels));
    }

    default void registerAppender(Object appender) {
        System.err.println("[centurybridge] ModelLoadingRegistry.registerAppender has no "
            + "1.21 equivalent; appender ignored: " + appender.getClass().getName());
    }

    default void registerResourceProvider(java.util.function.Function<?, ?> provider) {
        System.err.println("[centurybridge] ModelLoadingRegistry.registerResourceProvider "
            + "has no 1.21 equivalent; provider ignored");
    }

    default void registerVariantProvider(java.util.function.Function<?, ?> provider) {
        System.err.println("[centurybridge] ModelLoadingRegistry.registerVariantProvider "
            + "has no 1.21 equivalent; provider ignored");
    }
}
