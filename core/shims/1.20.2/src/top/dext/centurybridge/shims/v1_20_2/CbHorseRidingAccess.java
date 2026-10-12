package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractHorse's standAnimO: 1.20.1's positionRider raised a rearing horse's riders by 0.15 of it (CbRiding). */
@Mixin(net.minecraft.class_1496.class)
public interface CbHorseRidingAccess {

    @Accessor("field_6963")
    float cb$standAnimO();
}
