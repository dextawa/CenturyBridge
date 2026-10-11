package top.dext.centurybridge.facade.v1_21_4;

/**
 * 1.20.1 net/minecraft/class_6395, ClampedModelPredicateProvider: a provider whose value
 * is clamped to [0, 1]; mods implement it with lambdas over {@code unclampedCall}, its
 * single abstract method.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface class_6395 extends class_1800 {

    @Override
    default float call(net.minecraft.class_1799 stack, net.minecraft.class_638 world, net.minecraft.class_1309 entity,
            int seed) {
        return net.minecraft.class_3532.method_15363(unclampedCall(stack, world, entity, seed), 0.0F, 1.0F);
    }

    float unclampedCall(net.minecraft.class_1799 stack, net.minecraft.class_638 world,
            net.minecraft.class_1309 entity, int seed);
}
