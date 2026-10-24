package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendering.v1;

import java.io.IOException;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * 1.21.1 Fabric API's CoreShaderRegistrationCallback (classRenames, with its RegistrationContext), which 1.21.2's
 * Fabric API removed. 1.21.1 fired it every time GameRenderer reloaded its core shaders: each registration loaded the
 * core shader of that id (namespace:shaders/core/&lt;path&gt;.json, FabricShaderProgram = new ShaderInstance(provider,
 * id.toString(), format)) and handed it to the callback after all were loaded; a failure failed the reload. Here it
 * fires every time 1.21.2's ShaderManager has reloaded the shader resources (CbFapiShaderReloadMixin), each
 * registration loading the program of that id through 1.21.1's ShaderInstance constructor as the layer gives it back
 * (CbRenderShaders.create: the ShaderManager's program for namespace:core/&lt;path&gt;).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface CoreShaderRegistrationCallback {

    Event<CoreShaderRegistrationCallback> EVENT = EventFactory.createArrayBacked(CoreShaderRegistrationCallback.class, callbacks -> context -> {
        for (CoreShaderRegistrationCallback callback : callbacks) {
            callback.registerShaders(context);
        }
    });

    void registerShaders(RegistrationContext context) throws IOException;

    @FunctionalInterface
    interface RegistrationContext {
        void register(net.minecraft.class_2960 id, net.minecraft.class_293 vertexFormat, Consumer<net.minecraft.class_5944> loadCallback) throws IOException;
    }
}
