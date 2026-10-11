package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * fabric-rendering-v1's TooltipComponentCallback as 1.20.x mods implement it (7
 * corpus mods): the SAM became getClientComponent on ClientTooltipComponentCallback.
 * The old name stays the single abstract method and the new one is derived, so an
 * old listener IS a real listener: EVENT is the real event (mods only call
 * register() on it, the invoker type is never seen by them).
 */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbTooltipComponentCallback extends ClientTooltipComponentCallback {

    @SuppressWarnings({"unchecked", "rawtypes"})
    Event<CbTooltipComponentCallback> EVENT = (Event) ClientTooltipComponentCallback.EVENT;

    ClientTooltipComponent getComponent(TooltipComponent data);

    @Override
    default ClientTooltipComponent getClientComponent(TooltipComponent data) {
        return getComponent(data);
    }
}
