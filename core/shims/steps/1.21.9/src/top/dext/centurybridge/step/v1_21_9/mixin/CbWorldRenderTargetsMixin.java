package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_761;
import net.minecraft.class_9960;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbWorldRenderTargets;

/**
 * LevelRenderer's frame-target bundle for the restored AFTER_TRANSLUCENT pass (rt/CbWorldRender), which, as Fabric API
 * 0.136's pass did, writes the bundle's main target so the frame graph orders it after translucent and before clouds.
 * 1.21.9 has no getter for the bundle; this accessor is kept apart from CbWorldRenderEventsMixin so a later release
 * that drops the field strips only this class (the pass is then skipped), not the events.
 */
@Mixin(class_761.class)
public abstract class CbWorldRenderTargetsMixin implements CbWorldRenderTargets {

    @Shadow
    @Final
    private class_9960 field_53081;

    @Override
    public class_9960 cb$targets() {
        return this.field_53081;
    }
}
