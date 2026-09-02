package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_5712;

/**
 * fabric-entity-events-v1's FabricElytraItem (removed in 1.21.2 when elytra flight became the glider
 * component). Items implementing it keep their override; CbInit registers the EntityElytraEvents.CUSTOM
 * listener fapi itself used to ship, which asks the chest item (4 corpus mods).
 * classRename: net/fabricmc/fabric/api/entity/event/v1/FabricElytraItem -> this interface.
 */
public interface CbFabricElytraItem {

    default boolean useCustomElytra(class_1309 entity, class_1799 chestStack, boolean tickElytra) {
        if (tickElytra) {
            doVanillaElytraTick(entity, chestStack);
        }
        return true;
    }

    /** the 1.20.1 default: damage the chest item every 20 ticks of flight and emit the glide game event */
    default void doVanillaElytraTick(class_1309 entity, class_1799 chestStack) {
        int nextRoll = entity.method_6003() + 1;
        if (!entity.method_37908().field_9236 && nextRoll % 10 == 0) {
            if ((nextRoll / 10) % 2 == 0) {
                chestStack.method_7970(1, entity, class_1304.field_6174);
            }
            entity.method_32876(class_5712.field_28158);
        }
    }
}
