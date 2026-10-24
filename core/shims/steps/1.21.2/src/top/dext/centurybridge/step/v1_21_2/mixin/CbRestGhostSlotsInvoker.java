package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10302;
import net.minecraft.class_10352;
import net.minecraft.class_1735;
import net.minecraft.class_9934;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** GhostSlots.setInput (protected): a 1.21.1 addItemToSlot on a 1.21.2 component (facade/RecipeBookComponent.Vanilla) */
@Environment(EnvType.CLIENT)
@Mixin(class_9934.class)
public interface CbRestGhostSlotsInvoker {

    @Invoker("method_64872")
    void cb$setInput(class_1735 slot, class_10352 context, class_10302 display);
}
