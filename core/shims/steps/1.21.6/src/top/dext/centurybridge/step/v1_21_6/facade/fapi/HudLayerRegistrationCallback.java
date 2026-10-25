package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's HudLayerRegistrationCallback (fabric-rendering-v1). 1.21.5 invoked it once, when Gui was built
 * (InGameHudMixin.registerLayers, from Gui's constructor), with a wrapper over Gui's layers; here it is invoked at the
 * same point (CbGuiHudLayersMixin) with the wrapper over 1.21.6's HudElementRegistry (CbHudLayers).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface HudLayerRegistrationCallback {

    net.fabricmc.fabric.api.event.Event<HudLayerRegistrationCallback> EVENT = net.fabricmc.fabric.api.event.EventFactory
        .createArrayBacked(HudLayerRegistrationCallback.class, callbacks -> wrapper -> {
            for (HudLayerRegistrationCallback callback : callbacks) {
                callback.register(wrapper);
            }
        });

    void register(LayeredDrawerWrapper layeredDrawer);
}
