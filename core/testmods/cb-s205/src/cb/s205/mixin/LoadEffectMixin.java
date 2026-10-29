package cb.s205.mixin;

import cb.s205.S205Seen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.4's private GameRenderer.loadEffect(location) (class_757.method_3168): HEAD records the location it was given
 * (1.20.4 spells it &lt;ns&gt;:shaders/post/&lt;name&gt;.json), TAIL counts the calls that reached the end. Selectors spell
 * the descriptor, as a refmap gives them (no refmap here: intermediary names).
 */
@Mixin(net.minecraft.class_757.class)
public abstract class LoadEffectMixin {
    private static final String LOAD = "method_3168(Lnet/minecraft/class_2960;)V";

    @Inject(method = LOAD, at = @At("HEAD"))
    private void cb$s205$head(net.minecraft.class_2960 location, CallbackInfo ci) {
        S205Seen.LOADS.add(String.valueOf(location));
    }

    @Inject(method = LOAD, at = @At("TAIL"))
    private void cb$s205$tail(net.minecraft.class_2960 location, CallbackInfo ci) {
        S205Seen.tails++;
    }
}
