package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_3215;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * ServerChunkCache.setSpawnSettings(spawnEnemies, spawnFriendlies) of 1.21.1 (a cover, overriding
 * ChunkSource's): both flags set, 1.21.1's body. 1.21.2 kept the spawnFriendlies field and its readers --
 * the spawn tick's category filter and the custom spawners, nothing else reads it -- but sets only the
 * hostile flag; the friendly flag is what 1.21.1's caller passed (MinecraftServer.isSpawningAnimals,
 * CbRestMinecraftServerMixin).
 */
@Mixin(class_3215.class)
public abstract class CbRestServerChunkCacheMixin extends CbRestChunkSourceMixin {

    @Shadow
    private boolean field_13929;

    @Shadow
    private boolean field_13941;

    @Override
    public void method_12128(boolean enemies, boolean friendlies) {
        this.field_13929 = enemies;
        this.field_13941 = friendlies;
    }
}
