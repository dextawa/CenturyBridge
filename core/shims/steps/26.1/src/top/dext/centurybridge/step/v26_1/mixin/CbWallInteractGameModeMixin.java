package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.rt.CbWallInteract;
import top.dext.centurybridge.step.v26_1.rt.CbWallInteractAt;
import top.dext.centurybridge.step.v26_1.rt.CbWallInteractClient;

/**
 * 1.21.11's MultiPlayerGameMode.interactAt(player, entity, hit, hand) and interact(player, entity, hand),
 * given back (covers) on 26.1's one interact packet, which the server answers with interactAt (for an
 * entity of a 1.21.11 class, rt/CbWallInteract) and then interactOn:
 * <ul>
 * <li>interactAt: 1.21.11 sent INTERACT_AT and asked the entity's interactAt locally (PASS for a
 *     spectator). Here the local answer decides: when it consumes the action 1.21.11's client sent no
 *     INTERACT, so the packet goes now (the server's interactAt consumes it the same way); otherwise the
 *     location is kept for the interact that follows (rt/CbWallInteractClient.pend).</li>
 * <li>interact: 1.21.11 sent INTERACT and ran interactOn locally (PASS for a spectator). The packet carries
 *     the location the interactAt before it was given, else rt/CbWallInteract.location; the local
 *     interactOn skips the interactAt step the client already asked or 1.21.11's interact never asked.</li>
 * </ul>
 * 26.1 has no packet for INTERACT alone: a mod calling interact without interactAt before it has the server
 * ask an entity of a 1.21.11 class interactAt first, as 26.1's own clicks do.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = MultiPlayerGameMode.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallInteractGameModeMixin {

    @Shadow
    @Final
    private ClientPacketListener connection;

    @Shadow
    private GameType localPlayerMode;

    @Shadow
    private void ensureHasSentCarriedItem() {
        throw new AssertionError();
    }

    public InteractionResult interactAt(Player player, Entity entity, EntityHitResult hit, InteractionHand hand) {
        this.ensureHasSentCarriedItem();
        Vec3 location = hit.getLocation().subtract(entity.getX(), entity.getY(), entity.getZ());
        InteractionResult result = this.localPlayerMode == GameType.SPECTATOR
            ? InteractionResult.PASS
            : ((CbWallInteractAt) entity).interactAt(player, location, hand);
        if (result.consumesAction()) {
            CbWallInteractClient.clear();
            this.connection.send(new ServerboundInteractPacket(entity.getId(), hand, location, player.isShiftKeyDown()));
        } else {
            CbWallInteractClient.pend(entity, hand, location);
        }
        return result;
    }

    public InteractionResult interact(Player player, Entity entity, InteractionHand hand) {
        this.ensureHasSentCarriedItem();
        Vec3 location = CbWallInteractClient.take(entity, hand);
        if (location == null) {
            location = CbWallInteract.location(entity, player, hand);
        }
        this.connection.send(new ServerboundInteractPacket(entity.getId(), hand, location, player.isShiftKeyDown()));
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        CbWallInteract.atDone(entity);
        try {
            return player.interactOn(entity, hand, location);
        } finally {
            CbWallInteract.atDoneClear();
        }
    }
}
