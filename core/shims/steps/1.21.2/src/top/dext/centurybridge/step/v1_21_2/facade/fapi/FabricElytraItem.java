package top.dext.centurybridge.step.v1_21_2.facade.fapi;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;

/**
 * FabricElytraItem of 1.21.1's Fabric API (0.116; 0.106.1 dropped it, 1.21.2's elytra flight comes from the
 * glider component), classRenamed. An item implementing it flies its wearer as an elytra: 0.116 answered
 * EntityElytraEvents.CUSTOM with the chest item's useCustomElytra, and so does the layer
 * ({@link #register}, from CbRestInit) -- 0.106.1 still asks CUSTOM where 1.21.2's glider check fails.
 * The defaults are 0.116's bodies: fly while the item is not about to break (1.21.1's
 * ElytraItem.isFlyEnabled: damage below max - 1), and tick as the vanilla elytra (one damage every
 * 20 ticks of flight, the glide game event every 10, on the server).
 */
public interface FabricElytraItem {

    default boolean useCustomElytra(net.minecraft.class_1309 entity, net.minecraft.class_1799 chestStack, boolean tickElytra) {
        if (chestStack.method_7919() < chestStack.method_7936() - 1) {
            if (tickElytra) {
                doVanillaElytraTick(entity, chestStack);
            }
            return true;
        }
        return false;
    }

    default void doVanillaElytraTick(net.minecraft.class_1309 entity, net.minecraft.class_1799 chestStack) {
        int nextRoll = entity.method_6003() + 1;
        if (!entity.method_37908().field_9236 && nextRoll % 10 == 0) {
            if ((nextRoll / 10) % 2 == 0) {
                chestStack.method_7970(1, entity, net.minecraft.class_1304.field_6174);
            }
            entity.method_32876(net.minecraft.class_5712.field_28158);
        }
    }

    /** 0.116's CUSTOM listener: the chest item's useCustomElytra when it is one */
    static void register() {
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            net.minecraft.class_1799 chest = entity.method_6118(net.minecraft.class_1304.field_6174);
            return chest.method_7909() instanceof FabricElytraItem item && item.useCustomElytra(entity, chest, tickElytra);
        });
    }
}
