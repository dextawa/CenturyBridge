package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelLoot;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelShapes;

/** BlockBehaviour's protected 1.21.2 shape methods and its loot table field, for this layer's bridges */
@Mixin(net.minecraft.class_4970.class)
public interface CbLevelBlockInvoker extends CbLevelShapes.Access, CbLevelLoot.Fields, CbLevelNeighbors.Access {

    @Override
    @Invoker("method_9571")
    net.minecraft.class_265 cb$occlusionShape(net.minecraft.class_2680 state);

    @Override
    @Invoker("method_9505")
    int cb$lightBlock(net.minecraft.class_2680 state);

    @Override
    @Invoker("method_9579")
    boolean cb$propagatesSkylightDown(net.minecraft.class_2680 state);

    @Override
    @Accessor("field_23156")
    java.util.Optional<net.minecraft.class_5321<net.minecraft.class_52>> cb$dropsField();

    @Override
    @Invoker("method_9612")
    void cb$neighborChanged(net.minecraft.class_2680 state, net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
        net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, boolean moved);
}
