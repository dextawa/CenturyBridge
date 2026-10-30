package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_1697;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/** 1.21.10 MinecartCommandBlock.MinecartCommandBase.getLevel(): the minecart's level (javap -c) */
@Mixin(targets = "net.minecraft.class_1697$class_1698", priority = CbVanillaReads.PRIORITY)
public abstract class CbMinecartCommandSourceRestoredMixin {

    @Shadow
    @Final
    class_1697 field_7745;

    /** BaseCommandBlock.getLevel() */
    public class_3218 method_8293() {
        return (class_3218) this.field_7745.method_73183();
    }
}
