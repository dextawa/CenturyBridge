package top.dext.centurybridge.facade.v1_21_4;

/**
 * 1.20.1 net/minecraft/class_1800, ModelPredicateProvider: a float read off an item
 * stack for model overrides. 1.21.4 handed the intermediary name to the item-model
 * NumericProperty (a value plus its codec), a different contract, so the provider every
 * older mod implements and calls lives on here. {@code call} is unobfuscated in every
 * release that had it.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface class_1800 {

    float call(net.minecraft.class_1799 stack, net.minecraft.class_638 world, net.minecraft.class_1309 entity,
            int seed);
}
