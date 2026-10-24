package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.model.loading.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 Fabric API's ModelLoadingPlugin (classRenames): 1.21.2's renamed its one method onInitializeModelLoader to
 * initialize. A 1.21.1 mod's plugin (a class or a lambda) implements this interface; register hands 1.21.2's
 * registry a 1.21.2 plugin running it -- 1.21.1's register handed the plugin itself to the same registry (no
 * entrypoint loads plugins in either release).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ModelLoadingPlugin {

    static void register(ModelLoadingPlugin plugin) {
        java.util.Objects.requireNonNull(plugin, "plugin must not be null");
        net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.register(plugin::onInitializeModelLoader);
    }

    void onInitializeModelLoader(net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.Context pluginContext);
}
