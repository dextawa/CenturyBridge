package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_2593;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/** 1.21.10 CommandBlockEntity's command source (CommandBlockEntity$1).getLevel(): the block entity's level (javap -c) */
@Mixin(targets = "net.minecraft.class_2593$1", priority = CbVanillaReads.PRIORITY)
public abstract class CbCommandBlockEntitySourceRestoredMixin {

    @Shadow
    @Final
    class_2593 field_11921;

    /** BaseCommandBlock.getLevel() */
    public class_3218 method_8293() {
        return (class_3218) this.field_11921.method_10997();
    }
}
