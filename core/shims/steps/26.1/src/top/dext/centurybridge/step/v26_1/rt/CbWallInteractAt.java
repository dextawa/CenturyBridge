package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11's Entity.interactAt(player, location, hand), given back on Entity (mixin/CbWallInteractEntityMixin,
 * cover): the layer reaches it through this interface, a mod's override of it implements it.
 */
public interface CbWallInteractAt {

    InteractionResult interactAt(Player player, Vec3 location, InteractionHand hand);
}
