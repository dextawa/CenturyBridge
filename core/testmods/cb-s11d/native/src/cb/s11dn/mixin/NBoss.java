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
@Mixin(class_337.class)
public abstract class NBoss {
    @Inject(method = "method_1800", at = @At("HEAD"))
    private void cbn$worldFog(CallbackInfoReturnable<Boolean> cir) {
        Counters.hit("BossHealthOverlay.shouldCreateWorldFog");
    }
}
