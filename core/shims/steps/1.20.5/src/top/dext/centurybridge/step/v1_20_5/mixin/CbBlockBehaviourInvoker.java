package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockAccess;

/** BlockBehaviour's protected interaction and pathfinding methods, called virtually, for this layer's bridges */
@Mixin(net.minecraft.class_4970.class)
public interface CbBlockBehaviourInvoker extends CbBlockAccess {

    @Override
    @Invoker("method_55765")
    net.minecraft.class_9062 cb$useItemOn(net.minecraft.class_1799 stack, net.minecraft.class_2680 state,
        net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_1657 player,
        net.minecraft.class_1268 hand, net.minecraft.class_3965 hit);

    @Override
    @Invoker("method_55766")
    net.minecraft.class_1269 cb$useWithoutItem(net.minecraft.class_2680 state, net.minecraft.class_1937 level,
        net.minecraft.class_2338 pos, net.minecraft.class_1657 player, net.minecraft.class_3965 hit);

    @Override
    @Invoker("method_9516")
    boolean cb$isPathfindable(net.minecraft.class_2680 state, net.minecraft.class_10 type);
}
