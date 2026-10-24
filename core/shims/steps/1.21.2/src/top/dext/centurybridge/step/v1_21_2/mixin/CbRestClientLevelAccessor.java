package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ClientLevel.tickDayTime (private): whether the day advances, which 1.21.1's client kept as the daylight game rule (CbRestClient) */
@Environment(EnvType.CLIENT)
@Mixin(class_638.class)
public interface CbRestClientLevelAccessor {

    @Accessor("field_54500")
    boolean cb$tickDayTime();
}
