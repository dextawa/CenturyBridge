package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2487;
import net.minecraft.class_31;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** PrimaryLevelData's loaded player tag and level data version, which 1.20.2's parse took from its caller */
@Mixin(class_31.class)
public interface CbWorldPrimaryLevelDataAccess {

    @Mutable
    @Accessor("field_170")
    void cb$setLoadedPlayerTag(class_2487 tag);

    @Mutable
    @Accessor("field_158")
    void cb$setVersion(int version);
}
