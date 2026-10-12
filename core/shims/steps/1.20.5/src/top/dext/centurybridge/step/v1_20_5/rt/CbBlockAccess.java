package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * The protected BlockBehaviour members 1.20.5 made of what 1.20.4 had public, for this layer's
 * bridges: a call reaches them through the block itself, so a mod's override of the new shape (or
 * a hook synthesized for its override of the old one) answers exactly as a call in 1.20.4 did.
 * Implemented on BlockBehaviour by the CbBlockBehaviourInvoker mixin (virtual invokers).
 */
public interface CbBlockAccess {

    /** BlockBehaviour.useItemOn(stack, state, level, pos, player, hand, hit) */
    net.minecraft.class_9062 cb$useItemOn(net.minecraft.class_1799 stack, net.minecraft.class_2680 state,
        net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_1657 player,
        net.minecraft.class_1268 hand, net.minecraft.class_3965 hit);

    /** BlockBehaviour.useWithoutItem(state, level, pos, player, hit) */
    net.minecraft.class_1269 cb$useWithoutItem(net.minecraft.class_2680 state, net.minecraft.class_1937 level,
        net.minecraft.class_2338 pos, net.minecraft.class_1657 player, net.minecraft.class_3965 hit);

    /** BlockBehaviour.isPathfindable(state, type) */
    boolean cb$isPathfindable(net.minecraft.class_2680 state, net.minecraft.class_10 type);
}
