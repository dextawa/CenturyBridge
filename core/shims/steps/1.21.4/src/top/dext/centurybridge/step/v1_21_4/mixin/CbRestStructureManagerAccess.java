package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1936;
import net.minecraft.class_5138;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** StructureManager.level: the level whose dimension 1.21.4's createStructures takes (CbRestLevel). */
@Mixin(class_5138.class)
public interface CbRestStructureManagerAccess {
    @Accessor("field_24404")
    class_1936 cb$level();
}
