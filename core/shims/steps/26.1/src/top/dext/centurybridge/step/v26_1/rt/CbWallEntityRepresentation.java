package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;

/**
 * The 1.21.11 ItemStack entity representation members the layer restores on ItemStack
 * (mixin/CbWallItemStackRepresentationMixin), as an interface ItemStack implements, so the layer's own mixins on item
 * frames, item entities and item displays can set and read it.
 */
public interface CbWallEntityRepresentation {

    void setEntityRepresentation(Entity entity);

    ItemFrame getFrame();
}
