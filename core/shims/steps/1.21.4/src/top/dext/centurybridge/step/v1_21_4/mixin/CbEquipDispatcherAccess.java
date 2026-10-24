package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EntityRenderDispatcher's equipment asset manager (private): the game's, which 1.21.3's Minecraft held itself */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_898.class)
public interface CbEquipDispatcherAccess {
    @Accessor("field_55290")
    net.minecraft.class_10201 cb$equipmentAssets();
}
