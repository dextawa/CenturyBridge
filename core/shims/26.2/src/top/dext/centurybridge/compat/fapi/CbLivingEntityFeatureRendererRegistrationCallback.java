package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/**
 * fabric-rendering-v1's LivingEntityFeatureRendererRegistrationCallback (9 corpus
 * mods): 26.x renamed it LivingEntityRenderLayerRegistrationCallback with the SAM
 * registerRenderers -> registerLayers; the RegistrationHelper kept its shape and is
 * a pure rename. Old SAM stays abstract, new one derived; EVENT is the real event.
 */
@FunctionalInterface
public interface CbLivingEntityFeatureRendererRegistrationCallback extends LivingEntityRenderLayerRegistrationCallback {

    @SuppressWarnings({"unchecked", "rawtypes"})
    Event<CbLivingEntityFeatureRendererRegistrationCallback> EVENT = (Event) LivingEntityRenderLayerRegistrationCallback.EVENT;

    void registerRenderers(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer,
                           RegistrationHelper registrationHelper, EntityRendererProvider.Context context);

    @Override
    default void registerLayers(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer,
                                RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        registerRenderers(entityType, entityRenderer, registrationHelper, context);
    }
}
