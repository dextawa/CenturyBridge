package cb.mixintest.mixin;

import cb.mixintest.Probes;
import cb.mixintest.T;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.class_1297.class)
public abstract class EntityMixin implements Probes.EntityProbe {
    /** getTeam: 1.20.3 answers a PlayerTeam (step 2 callReshapes, ret keep) */
    @Shadow
    public abstract net.minecraft.class_270 method_5781();

    /** ignoreExplosion(): 1.20.3 takes the explosion (step 2 callReshapes, push) */
    @Shadow
    public abstract boolean method_5659();

    @Override
    public Object cbtest$team() {
        return this.method_5781();
    }

    @Override
    public boolean cbtest$ignoresExplosion() {
        return this.method_5659();
    }

    /** isAlliedTo(Entity) calls getTeam(), whose shape 1.20.3 changed: the anchor follows */
    @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/Entity;getTeam()Lnet/minecraft/world/scores/Team;"))
    private void cbtest$onAllied(net.minecraft.class_1297 other, CallbackInfoReturnable<Boolean> cir) {
        T.ran("at-reshape-position", "allied-with=" + other.method_5845());
    }
}
