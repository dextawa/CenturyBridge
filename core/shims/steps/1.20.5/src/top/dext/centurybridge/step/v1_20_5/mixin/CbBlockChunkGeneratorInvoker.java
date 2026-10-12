package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockCodecs;

/** ChunkGenerator's protected codec(), for a mod's own this.codec() (CbBlockCodecs) */
@Mixin(net.minecraft.class_2794.class)
public interface CbBlockChunkGeneratorInvoker extends CbBlockCodecs.ChunkGeneratorCodec {

    @Override
    @Invoker("method_28506")
    com.mojang.serialization.MapCodec<? extends net.minecraft.class_2794> cb$chunkGeneratorCodec();
}
