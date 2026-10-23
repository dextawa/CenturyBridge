package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_2802;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/** Level.setSpawnSettings(spawnEnemies, spawnFriendlies) of 1.21.1 (a cover): the chunk source's, 1.21.1's body */
@Mixin(class_1937.class)
public abstract class CbRestLevelMixin implements CbRestServer.LevelSpawnSettings {

    @Shadow
    public abstract class_2802 method_8398();

    @Override
    public void method_8424(boolean enemies, boolean friendlies) {
        ((CbRestServer.ChunkSpawnSettings) this.method_8398()).method_12128(enemies, friendlies);
    }
}
