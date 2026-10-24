package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendering.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateEntity;

/**
 * 1.21.1 Fabric API's LivingEntityFeatureRenderEvents (classRenames, with its AllowCapeRender): ALLOW_CAPE_RENDER asked
 * each listener with the player whose cape was about to be drawn, and the cape was skipped when one answered false.
 * 1.21.2's event asks with the player's render state. This event is 1.21.1's (the same array-backed invoker); a
 * listener on 1.21.2's event asks it with the player the state was extracted from (CbRenderStateEntity), registered
 * when the class is first used. A state without a player was never asked about in 1.21.1: it is allowed.
 */
@Environment(EnvType.CLIENT)
public final class LivingEntityFeatureRenderEvents {

    public static final Event<AllowCapeRender> ALLOW_CAPE_RENDER = EventFactory.createArrayBacked(AllowCapeRender.class, listeners -> player -> {
        for (AllowCapeRender listener : listeners) {
            if (!listener.allowCapeRender(player)) {
                return false;
            }
        }
        return true;
    });

    static {
        net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(state ->
            !(CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_742 player) || ALLOW_CAPE_RENDER.invoker().allowCapeRender(player));
    }

    private LivingEntityFeatureRenderEvents() {
    }

    @FunctionalInterface
    public interface AllowCapeRender {
        boolean allowCapeRender(net.minecraft.class_742 player);
    }
}
