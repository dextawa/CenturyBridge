package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * Minecraft's 1.21.3 getEquipmentModels(): 1.21.4's Minecraft makes the same manager and hands it to the entity render
 * dispatcher, which keeps it.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_310.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEquipMinecraftMixin {

    @Shadow
    public abstract net.minecraft.class_898 method_1561();

    /** was getEquipmentModels() */
    public net.minecraft.class_10201 method_64036() {
        return ((CbEquipDispatcherAccess) this.method_1561()).cb$equipmentAssets();
    }
}
