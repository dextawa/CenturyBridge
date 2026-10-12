package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The 1.20.1 shape of ClientChunkManager.unload(x, z) (1.20.2's takes a ChunkPos), given to a mod
 * subclass overriding it so that the 1.20.2 shape reaches its override (CbNetClient.cbDropChunk).
 */
@Environment(EnvType.CLIENT)
public interface CbNetLegacyChunkDrop {

    void method_2859(int x, int z);
}
