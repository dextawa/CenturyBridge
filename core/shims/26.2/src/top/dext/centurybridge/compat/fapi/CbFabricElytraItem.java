package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * fabric-entity-events-v1's FabricElytraItem (4 corpus mods, 3 implementing armor
 * items): the interface is gone in 26.x, only the EntityElytraEvents.CUSTOM hook
 * that backed it remains. Same defaults as 1.20.1 (fly while the item is not about
 * to break, damage the chest item every 20 flight ticks on the server), and the
 * hook that consults the chest item's class is registered here as Fabric API did.
 */
public interface CbFabricElytraItem {

    Object HOOK = Hook.install();

    default boolean useCustomElytra(LivingEntity entity, ItemStack chestStack, boolean tickElytra) {
        if (chestStack.isDamageableItem() && chestStack.getDamageValue() >= chestStack.getMaxDamage() - 1) {
            return false;
        }
        if (tickElytra) {
            doVanillaElytraTick(entity, chestStack);
        }
        return true;
    }

    default void doVanillaElytraTick(LivingEntity entity, ItemStack chestStack) {
        if (!entity.level().isClientSide() && (entity.getFallFlyingTicks() + 1) % 20 == 0) {
            chestStack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
        }
    }

    final class Hook {
        private Hook() {
        }

        static Object install() {
            EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
                ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
                return chest.getItem() instanceof CbFabricElytraItem elytra && elytra.useCustomElytra(entity, chest, tickElytra);
            });
            return Boolean.TRUE;
        }
    }
}
