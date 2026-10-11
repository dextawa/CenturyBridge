package top.dext.centurybridge.facade.v26_2;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 1.20.1 net/minecraft/class_6395, ClampedModelPredicateProvider: a ModelPredicateProvider
 * whose {@code call} clamps {@code unclampedCall} to [0, 1] (Mth.clamp), the shape every
 * registration through ModelPredicateProviderRegistry takes. Gone with the predicate
 * registry in 1.21.4 (see class_5272). Member names are unobfuscated in 1.20.1.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@FunctionalInterface
public interface class_6395 extends class_1800 {

    float unclampedCall(ItemStack stack, ClientLevel level, LivingEntity entity, int seed);

    @Override
    default float call(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        return net.minecraft.util.Mth.clamp(unclampedCall(stack, level, entity, seed), 0.0F, 1.0F);
    }
}
