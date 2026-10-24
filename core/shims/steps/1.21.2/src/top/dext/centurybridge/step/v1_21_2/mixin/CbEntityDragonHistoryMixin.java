package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1510;
import net.minecraft.class_9871;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityDragon;

/**
 * 1.21.1's EnderDragon.positions[64][3] kept on 1.21.2's DragonFlightHistory as the samples' storage: written by
 * record as 1.21.1's aiStep wrote it ([0] yRot, [1] y; every slot while the pointer is negative), copied by
 * copyFrom, and answering get(i); the history's owner dragon, when a mod class overriding 1.21.1's getLatencyPos,
 * answers get(i) (as getLatencyPos(i, 1.0), what 1.21.1's aiStep asked) and get(i, partialTick) (CbEntityDragon).
 */
@Mixin(class_9871.class)
public abstract class CbEntityDragonHistoryMixin implements CbEntityDragon.History {

    @Shadow
    private int field_52492;

    @Unique
    private final double[][] cb$positions = new double[64][3];

    @Unique
    private class_1510 cb$owner;

    @Override
    public double[][] cb$positions() {
        return this.cb$positions;
    }

    @Override
    public int cb$pointer() {
        return this.field_52492;
    }

    @Override
    public void cb$pointer(int pointer) {
        this.field_52492 = pointer;
    }

    @Override
    public class_1510 cb$owner() {
        return this.cb$owner;
    }

    @Override
    public void cb$owner(class_1510 owner) {
        this.cb$owner = owner;
    }

    @Inject(method = "method_61483", at = @At("HEAD"))
    private void cb$record(double y, float yRot, CallbackInfo ci) {
        if (this.field_52492 < 0) {
            for (double[] slot : this.cb$positions) {
                slot[0] = (double) yRot;
                slot[1] = y;
            }
        }
        int next = this.field_52492 + 1 == 64 ? 0 : this.field_52492 + 1;
        this.cb$positions[next][0] = (double) yRot;
        this.cb$positions[next][1] = y;
    }

    @Inject(method = "method_61486", at = @At("RETURN"))
    private void cb$copyFrom(class_9871 other, CallbackInfo ci) {
        CbEntityDragon.History from = (CbEntityDragon.History) other;
        double[][] src = from.cb$positions();
        for (int i = 0; i < 64; i++) {
            System.arraycopy(src[i], 0, this.cb$positions[i], 0, 3);
        }
        this.cb$owner = from.cb$owner();
    }

    @Inject(method = "method_61484", at = @At("HEAD"), cancellable = true)
    private void cb$get(int index, CallbackInfoReturnable<class_9871.class_9872> cir) {
        if (this.cb$owner instanceof CbEntityDragon.LegacyLatency legacy) {
            cir.setReturnValue(CbEntityDragon.sample(legacy.method_6817(index, 1.0F)));
            return;
        }
        double[] slot = this.cb$positions[this.field_52492 - index & 63];
        cir.setReturnValue(new class_9871.class_9872(slot[1], (float) slot[0]));
    }

    @Inject(method = "method_61485", at = @At("HEAD"), cancellable = true)
    private void cb$getPartial(int index, float partialTick, CallbackInfoReturnable<class_9871.class_9872> cir) {
        if (this.cb$owner instanceof CbEntityDragon.LegacyLatency legacy) {
            cir.setReturnValue(CbEntityDragon.sample(legacy.method_6817(index, partialTick)));
        }
    }
}
