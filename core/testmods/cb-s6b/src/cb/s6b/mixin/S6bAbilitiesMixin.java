package cb.s6b.mixin;

import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a 1.21.5 mod's mixin on Abilities.addSaveData / loadSaveData: an extra key in the "abilities" compound */
@Mixin(net.minecraft.class_1656.class)
public abstract class S6bAbilitiesMixin {

    @Inject(method = "method_7251(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void cbs6b$save(class_2487 tag, CallbackInfo ci) {
        tag.method_68568("abilities").method_10582("cb_s6b_extra", "kept");
    }

    @Inject(method = "method_7249(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void cbs6b$load(class_2487 tag, CallbackInfo ci) {
        cb.s6b.S6bTestMod.abilitiesExtra = tag.method_68568("abilities").method_68564("cb_s6b_extra", "");
    }
}
