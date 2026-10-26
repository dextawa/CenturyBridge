package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * Level.setSpawnSettings(spawnEnemies, spawnFriendlies) of 1.21.1 (a cover): the chunk source's, 1.21.1's
 * body. getChunkSource() is LevelAccessor's (Level declares it abstract only through the interface), so it
 * is called on the level, not shadowed.
 */
@Mixin(value = class_1937.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestLevelMixin implements CbRestServer.LevelSpawnSettings {

    @Override
    public void method_8424(boolean enemies, boolean friendlies) {
        ((CbRestServer.ChunkSpawnSettings) ((class_1937) (Object) this).method_8398()).method_12128(enemies, friendlies);
    }
}
