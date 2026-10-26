package cb.s11dn.mixin;

import cb.s11dn.Counters;
import net.minecraft.class_11398;
import net.minecraft.class_12076;
import net.minecraft.class_12197;
import net.minecraft.class_12202;
import net.minecraft.class_12206;
import net.minecraft.class_2874;
import net.minecraft.class_308;
import net.minecraft.class_337;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_765;
import net.minecraft.class_9779;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.11 mixin kept call counter (cb-s11d) */
@Mixin(class_12202.class)
public abstract class NProbe {
    @Inject(method = "method_75689", at = @At("HEAD"))
    private void cbn$get(class_12197<?> attribute, float partialTick, CallbackInfoReturnable<Object> cir) {
        if (attribute == class_12206.field_63742) {
            Counters.hit("probe.FOG_COLOR");
        } else if (attribute == class_12206.field_64339) {
            Counters.hit("probe.SUNRISE_SUNSET_COLOR");
        } else if (attribute == class_12206.field_64345) {
            Counters.hit("probe.SKY_LIGHT_COLOR");
        } else if (attribute == class_12206.field_64346) {
            Counters.hit("probe.SKY_LIGHT_FACTOR");
        }
    }
}
