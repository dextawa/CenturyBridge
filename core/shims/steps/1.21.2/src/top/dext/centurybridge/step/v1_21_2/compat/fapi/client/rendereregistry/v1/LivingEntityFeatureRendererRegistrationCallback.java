package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendereregistry.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Objects;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * 1.21.1 Fabric API's fabric-renderer-registries-v1 (deprecated, dropped by 1.21.2's Fabric API; classRenames +
 * depRenames) LivingEntityFeatureRendererRegistrationCallback: the module's own body, which delegated to fabric-rendering-v1's same-named API, kept by 1.21.2's.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface LivingEntityFeatureRendererRegistrationCallback {

    Event<LivingEntityFeatureRendererRegistrationCallback> EVENT = Holder.createEvent();

    void registerRenderers(net.minecraft.class_1299<? extends net.minecraft.class_1309> entityType, net.minecraft.class_922<?, ?, ?> entityRenderer,
                           RegistrationHelper registrationHelper, net.minecraft.class_5617.class_5618 context);

    @Environment(EnvType.CLIENT)
    interface RegistrationHelper {
        <T extends net.minecraft.class_10017> void register(net.minecraft.class_3887<T, ? extends net.minecraft.class_583<T>> featureRenderer);
    }

    /** 1.21.1's private static createEvent(), in a holder (an interface's private static would need Java 9 bytecode the mod never sees) */
    final class Holder {
        private Holder() {
        }

        static Event<LivingEntityFeatureRendererRegistrationCallback> createEvent() {
            Event<LivingEntityFeatureRendererRegistrationCallback> event = EventFactory.createArrayBacked(LivingEntityFeatureRendererRegistrationCallback.class,
                callbacks -> (entityType, entityRenderer, registrationHelper, context) -> {
                    for (LivingEntityFeatureRendererRegistrationCallback callback : callbacks) {
                        callback.registerRenderers(entityType, entityRenderer, registrationHelper, context);
                    }
                });
            net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (entityType, entityRenderer, registrationHelper, context) -> {
                    net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper helper =
                        Objects.requireNonNull(registrationHelper);
                    EVENT.invoker().registerRenderers(entityType, entityRenderer, helper::register, context);
                });
            return event;
        }
    }
}
