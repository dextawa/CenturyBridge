package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockChunks;

/** MinecraftServer's protected storage access (its level id), for CbBlockChunks */
@Mixin(net.minecraft.server.MinecraftServer.class)
public interface CbBlockServerStorageAccessor extends CbBlockChunks.ServerStorage {

    @Override
    @Accessor("field_23784")
    net.minecraft.class_32.class_5143 cb$storageSource();
}
