package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** OverworldBiomes' helpers 1.20.6's swamp bodies used (unchanged in 1.21): its sky colour and the swamp features */
@Mixin(net.minecraft.class_5478.class)
public interface CbDataOverworldBiomesInvoker {

    @Invoker("method_30932")
    static int cb$calculateSkyColor(float temperature) {
        throw new AssertionError("mixin invoker");
    }

    @Invoker("method_39153")
    static void cb$globalOverworldGeneration(net.minecraft.class_5485.class_5495 generation) {
        throw new AssertionError("mixin invoker");
    }
}
