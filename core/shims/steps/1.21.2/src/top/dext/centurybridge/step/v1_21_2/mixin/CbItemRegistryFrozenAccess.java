package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2370;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** MappedRegistry's frozen flag: which lookup resolves a tag now (CbItemTags.lookup) */
@Mixin(class_2370.class)
public interface CbItemRegistryFrozenAccess {

    @Accessor("field_36463")
    boolean cb$frozen();
}
