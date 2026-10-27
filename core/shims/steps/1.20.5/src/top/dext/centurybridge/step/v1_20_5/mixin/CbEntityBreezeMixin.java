package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * Breeze: 1.20.4 members 1.20.5 dropped, back with 1.20.4's bodies (calls, super calls and overrides link):
 * withinOuterCircleRange / withinMiddleCircleRange, and emitGroundParticles' twenty puffs at the breeze's feet.
 * 1.20.4's gust_dust particle type is gone in 1.20.5 (no such registry entry, no client renderer), so the puffs
 * are its small_gust, the particle 1.20.5's breeze gusts use.
 */
@Mixin(value = net.minecraft.class_8949.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityBreezeMixin {

    private net.minecraft.class_1297 cbS4$self() {
        return (net.minecraft.class_1297) (Object) this;
    }

    /** was emitGroundParticles() */
    public net.minecraft.class_8949 method_55006() {
        net.minecraft.class_243 at = cbS4$self().method_19538().method_1031(0.0, 0.10000000149011612, 0.0);
        for (int i = 0; i < 20; i++) {
            cbS4$self().method_37908().method_8406(net.minecraft.class_2398.field_50252, at.field_1352, at.field_1351, at.field_1350, 0.0, 0.0, 0.0);
        }
        return (net.minecraft.class_8949) (Object) this;
    }

    /** was withinOuterCircleRange(pos) */
    public boolean method_55011(net.minecraft.class_243 pos) {
        net.minecraft.class_243 center = cbS4$self().method_24515().method_46558();
        return pos.method_55230(center, 20.0, 10.0) && !pos.method_55230(center, 8.0, 10.0);
    }

    /** was withinMiddleCircleRange(pos) */
    public boolean method_55012(net.minecraft.class_243 pos) {
        net.minecraft.class_243 center = cbS4$self().method_24515().method_46558();
        return pos.method_55230(center, 8.0, 10.0) && !pos.method_55230(center, 4.0, 10.0);
    }
}
