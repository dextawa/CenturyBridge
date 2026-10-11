package top.dext.centurybridge.facade.v26_2;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 1.20.1 net/minecraft/class_1800, ModelPredicateProvider: a float read off an item stack
 * for the old item model "overrides". 1.21.4 made item properties data-driven (select and
 * range_dispatch in item model definitions) and dropped the interface; mods implement and
 * call it, and CbLegacyItemModels reads it for the overrides the converter carries into
 * centurybridge:legacy_overrides definitions. {@code call} is unobfuscated in every release
 * that had it.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@FunctionalInterface
public interface class_1800 {

    float call(ItemStack stack, ClientLevel world, LivingEntity entity, int seed);
}
