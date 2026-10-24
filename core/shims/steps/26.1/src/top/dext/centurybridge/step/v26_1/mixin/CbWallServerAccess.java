package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** MinecraftServer's player data storage and custom boss events (rt/CbWallWorldData) */
@Mixin(MinecraftServer.class)
public interface CbWallServerAccess {

    @Accessor("playerDataStorage")
    PlayerDataStorage cb$playerDataStorage();

    @Mutable
    @Accessor("customBossEvents")
    void cb$setCustomBossEvents(CustomBossEvents events);
}
