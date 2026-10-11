package top.dext.centurybridge.facade.v26_2;

/** Facade for 1.20.1 net/minecraft/class_6395 (ClampedItemPropertyFunction),
 *  deleted on 26.2 (item model properties are data-driven). Legacy item model
 *  predicates implement it; default call clamps unclampedCall. Member names are
 *  intermediary tokens. */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface class_6395 {
    /** unclampedCall (method_unclamped) */
    float unclampedCall(net.minecraft.world.item.ItemStack stack,
            net.minecraft.client.multiplayer.ClientLevel level,
            net.minecraft.world.entity.LivingEntity entity, int seed);

    /** call (clamped) */
    default float call(net.minecraft.world.item.ItemStack stack,
            net.minecraft.client.multiplayer.ClientLevel level,
            net.minecraft.world.entity.LivingEntity entity, int seed) {
        float v = unclampedCall(stack, level, entity, seed);
        return v < 0.0F ? 0.0F : (v > 1.0F ? 1.0F : v);
    }
}
