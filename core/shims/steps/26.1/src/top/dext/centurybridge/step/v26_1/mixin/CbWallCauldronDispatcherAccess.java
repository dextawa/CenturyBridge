package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Map;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** a dispatcher's per-item interactions (rt/CbWallCauldrons.addDefaultInteractions) */
@Mixin(CauldronInteraction.Dispatcher.class)
public interface CbWallCauldronDispatcherAccess {

    @Accessor("items")
    Map<Item, CauldronInteraction> cb$items();
}
