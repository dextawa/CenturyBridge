package cb.mixintest.mixin;

import cb.mixintest.Probes;
import cb.mixintest.T;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.class_1690.class)
public abstract class BoatMixin implements Probes.BoatProbe {
    /** setHurtTime / getHurtTime: moved to VehicleEntity and renamed in 1.20.3 (step 2 methodRenames) */
    @Shadow
    public abstract void method_7553(int t);

    @Shadow
    public abstract int method_7533();

    @Override
    public void cbtest$setHurt(int t) {
        this.method_7553(t);
    }

    @Override
    public int cbtest$hurt() {
        return this.method_7533();
    }

    /** tick's call of setHurtTime, renamed in 1.20.3: the anchor follows the rename */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setHurtTime(I)V"))
    private void cbtest$tickHurt(net.minecraft.class_1690 self, int t) {
        T.ran("at-rename-redirect", "t=" + t);
        self.method_7553(t);
    }
}
