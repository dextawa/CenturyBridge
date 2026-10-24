package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.model.loading.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 Fabric API's PreparableModelLoadingPlugin (classRenames), its method onInitializeModelLoader renamed initialize
 * in 1.21.2: as ModelLoadingPlugin here; the data loader is 1.21.2's (unchanged) DataLoader.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface PreparableModelLoadingPlugin<T> {

    static <T> void register(net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin.DataLoader<T> loader,
                             PreparableModelLoadingPlugin<T> plugin) {
        java.util.Objects.requireNonNull(plugin, "plugin must not be null");
        net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin.register(loader, plugin::onInitializeModelLoader);
    }

    void onInitializeModelLoader(T data, net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.Context pluginContext);
}
