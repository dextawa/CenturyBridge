package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallInteractAt;

/**
 * 1.21.11's Entity.interactAt(player, location, hand), given back (cover): Entity's answered PASS. A mod's
 * override overrides it and its super call lands here; rt/CbWallInteract asks it before 26.1's interact.
 */
@Mixin(value = Entity.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallInteractEntityMixin implements CbWallInteractAt {

    @Override
    public InteractionResult interactAt(Player player, Vec3 location, InteractionHand hand) {
        return InteractionResult.PASS;
    }
}
