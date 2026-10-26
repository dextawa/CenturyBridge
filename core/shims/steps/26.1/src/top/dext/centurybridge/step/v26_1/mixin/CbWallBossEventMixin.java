package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Set;
import java.util.UUID;
import net.minecraft.server.bossevents.CustomBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.11's CustomBossEvent.addOfflinePlayer(uuid), given back (cover): the player joins the event's player
 * set, which 26.1 keeps under the same name and saves; 1.21.11's loading called it for each saved player
 * (26.1's restores them from the packed event). Marks the event dirty, as 26.1's other changes do.
 */
@Mixin(value = CustomBossEvent.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallBossEventMixin {

    @Shadow
    @Final
    private Set<UUID> players;

    @Shadow
    public abstract void setDirty();

    public void addOfflinePlayer(UUID player) {
        this.players.add(player);
        this.setDirty();
    }
}
