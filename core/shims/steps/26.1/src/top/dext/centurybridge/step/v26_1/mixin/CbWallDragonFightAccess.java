package top.dext.centurybridge.step.v26_1.mixin;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.end.DragonRespawnStage;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EnderDragonFight's state 1.21.11's EndDragonFight.Data carried (rt/CbWallDragonFight.saveData) */
@Mixin(EnderDragonFight.class)
public interface CbWallDragonFightAccess {

    @Accessor("needsStateScanning")
    boolean cb$needsStateScanning();

    @Accessor("dragonKilled")
    boolean cb$dragonKilled();

    @Accessor("respawnStage")
    DragonRespawnStage cb$respawnStage();

    @Accessor("dragonUUID")
    UUID cb$dragonUUID();

    @Accessor("exitPortalLocation")
    BlockPos cb$exitPortalLocation();

    @Accessor("gateways")
    List<Integer> cb$gateways();
}
