package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2802;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/** ChunkSource.setSpawnSettings(spawnEnemies, spawnFriendlies) of 1.21.1 (a cover): nothing, 1.21.1's body */
@Mixin(class_2802.class)
public abstract class CbRestChunkSourceMixin implements CbRestServer.ChunkSpawnSettings {

    @Override
    public void method_12128(boolean enemies, boolean friendlies) {
    }
}
