package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.step.v26_1.rt.CbWallTraderId;

/** 1.21.11's spawner recorded each wandering trader it spawned as the level data's wandering trader id (rt/CbWallTraderId) */
@Mixin(WanderingTraderSpawner.class)
public abstract class CbWallTraderSpawnerMixin {

    @ModifyArg(method = "spawn", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/npc/wanderingtrader/WanderingTraderSpawner;tryToSpawnLlamaFor(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/wanderingtrader/WanderingTrader;I)V"),
        index = 1)
    private WanderingTrader cb$recordTrader(WanderingTrader trader) {
        CbWallTraderId.of(((net.minecraft.server.level.ServerLevel) trader.level()).getServer()).setId(trader.getUUID());
        return trader;
    }
}
