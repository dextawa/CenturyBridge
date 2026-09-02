package top.dext.centurybridge.rt.v1_21_4;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import top.dext.centurybridge.compat.fapi.CbBlockPickInteractionAware;
import top.dext.centurybridge.compat.fapi.CbEntityPickInteractionAware;
import top.dext.centurybridge.compat.fapi.CbFabricElytraItem;

/**
 * CenturyBridge's own mod entrypoint: the compat layer's event listeners that fapi itself used to register
 * for the 1.20.1 marker interfaces (a mod implementing an interface never triggers that interface's static
 * initializer, so these cannot be lazy).
 */
public final class CbInit implements ModInitializer {

    @Override
    public void onInitialize() {
        // FabricElytraItem: fapi 1.20.1 asked the chest item through this very listener
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            class_1799 chest = entity.method_6118(class_1304.field_6174);
            if (chest.method_7909() instanceof CbFabricElytraItem item) {
                return item.useCustomElytra(entity, chest, tickElytra);
            }
            return false;
        });
        // Block/EntityPickInteractionAware: pick-block moved to the server in 1.21.4 (PlayerPickItemEvents);
        // the old client-side hit result is rebuilt from what the packet carries
        PlayerPickItemEvents.BLOCK.register((player, pos, state, includeData) -> {
            if (state.method_26204() instanceof CbBlockPickInteractionAware aware) {
                return aware.getPickedStack(state, player.method_37908(), pos, player,
                    new class_3965(class_243.method_24953(pos), class_2350.field_11036, pos, false));
            }
            return null;
        });
        PlayerPickItemEvents.ENTITY.register((player, entity, includeData) -> {
            if (entity instanceof CbEntityPickInteractionAware aware) {
                return aware.getPickedStack(player, new class_3966(entity));
            }
            return null;
        });
    }
}
