package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** BookViewScreen's book access and turn-sound flag, which 1.20.4's BookViewScreen(access) constructor set (rt/CbClientBooks) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_3872.class)
public interface CbClientBookViewScreenAccessor {

    @Accessor("field_17418")
    void cb$setBookAccess(net.minecraft.class_3872.class_3931 access);

    @Mutable
    @Accessor("field_18976")
    void cb$setPlayTurnSound(boolean playTurnSound);
}
