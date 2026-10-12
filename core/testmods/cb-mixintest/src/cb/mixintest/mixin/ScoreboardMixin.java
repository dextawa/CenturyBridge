package cb.mixintest.mixin;

import cb.mixintest.Probes;
import cb.mixintest.T;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.class_269.class)
public abstract class ScoreboardMixin implements Probes.ScoreboardProbe {
    /** getOrCreateObjective: gone in 1.20.2 (step 1 instanceRedirects) */
    @Shadow
    public abstract net.minecraft.class_266 method_1165(String name);

    /** DISPLAY_SLOT_LIST: gone in 1.20.2 (step 1 fieldGetters) */
    @Shadow
    @Final
    public static int field_31886;

    @Override
    public net.minecraft.class_266 cbtest$probeOrCreate(String name) {
        return this.method_1165(name);
    }

    @Override
    public int cbtest$listSlot() {
        return field_31886;
    }

    /** setDisplayObjective(int, Objective): 1.20.2 takes a DisplaySlot (step 1 override hook, wrap slotId) */
    @Inject(method = "setDisplayObjective", at = @At("HEAD"))
    private void cbtest$onSetDisplay(int slot, net.minecraft.class_266 objective, CallbackInfo ci) {
        T.ran("inject-wrap-hook", "slot=" + slot + " obj=" + (objective == null ? null : objective.method_1113()));
    }

    /** addObjective: 1.20.3 adds two arguments (step 2 override hook, argument map) */
    @Inject(method = "addObjective", at = @At("RETURN"))
    private void cbtest$onAdd(String name, net.minecraft.class_274 criterion, net.minecraft.class_2561 title,
                              net.minecraft.class_274.class_275 render, CallbackInfoReturnable<net.minecraft.class_266> cir) {
        T.ran("inject-capture-hook", "name=" + name + " ret=" + cir.getReturnValue().method_1113());
    }

    /** removeObjective calls setDisplayObjective(int, Objective), which 1.20.2 reshaped: the anchor follows */
    @Inject(method = "removeObjective", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/scores/Scoreboard;setDisplayObjective(ILnet/minecraft/world/scores/Objective;)V"))
    private void cbtest$onRemoveClearsSlot(net.minecraft.class_266 objective, CallbackInfo ci) {
        T.ran("at-hook-position", "obj=" + objective.method_1113());
    }
}
