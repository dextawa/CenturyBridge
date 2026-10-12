package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockCodecs;

/** BiomeSource's protected codec(), for a mod's own this.codec() (CbBlockCodecs) */
@Mixin(net.minecraft.class_1966.class)
public interface CbBlockBiomeSourceInvoker extends CbBlockCodecs.BiomeSourceCodec {

    @Override
    @Invoker("method_28442")
    com.mojang.serialization.MapCodec<? extends net.minecraft.class_1966> cb$biomeSourceCodec();
}
