package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.Collection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbGameTest;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * GameTestInfo's 1.20.2 spawnStructure(pos, padding) back on the class (a cover: calls, a mod
 * subclass's override and its super call bind to it again), with its 1.20.2 body: the test's
 * structure spawned the 1.20.2 way (CbGameTest.spawnStructure), the structure block named after the
 * test, the start button added, every listener told the structure loaded. 1.20.3's own
 * prepareTestStructure(pos) is untouched; a subclass overriding spawnStructure is reconnected to it
 * by an override hook (CbGameTest.prepareTestStructure).
 */
@Mixin(value = net.minecraft.class_4517.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGameTestInfoMixin implements CbGameTest.LegacyTestInfo {

    @Shadow
    private net.minecraft.class_2338 field_20560;

    @Shadow
    @Final
    private net.minecraft.class_3218 field_20561;

    @Shadow
    @Final
    private Collection<net.minecraft.class_4518> field_20562;

    @Shadow
    private net.minecraft.class_2633 field_27805;

    @Shadow
    public abstract String method_23638();

    @Shadow
    public abstract net.minecraft.class_2470 method_29402();

    @Shadow
    public abstract String method_22169();

    /** spawnStructure(pos, padding) */
    @Override
    public void method_22166(net.minecraft.class_2338 pos, int padding) {
        this.field_27805 = CbGameTest.spawnStructure(method_23638(), pos, method_29402(), padding, field_20561, false);
        this.field_20560 = field_27805.method_11016();
        this.field_27805.method_11343(method_22169());
        net.minecraft.class_4525.method_22248(field_20560, new net.minecraft.class_2338(1, 0, -1), method_29402(), field_20561);
        net.minecraft.class_4517 self = (net.minecraft.class_4517) (Object) this;
        this.field_20562.forEach(listener -> listener.method_22188(self));
    }
}
