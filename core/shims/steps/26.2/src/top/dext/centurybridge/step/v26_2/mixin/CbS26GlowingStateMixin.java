package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26GlowingEntities;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2's public LevelRenderState.haveGlowingEntities, which 26.2 dropped, restored on the class under its own name:
 * mods' reads and writes of the field, and their @Shadow / @Accessor on it, link as on 26.1.2. Vanilla's 26.1.2
 * writes (reset, the entity extraction) and read (the outline composite) are routed by mixin/CbS26GlowingResetMixin,
 * CbS26GlowingExtractMixin and CbS26GlowingFrameMixin (rt/CbS26GlowingEntities).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = LevelRenderState.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26GlowingStateMixin implements CbS26GlowingEntities {
    /** 26.1.2 LevelRenderState.haveGlowingEntities */
    public boolean haveGlowingEntities;

    @Override
    public boolean cbS26$haveGlowingEntities() {
        return this.haveGlowingEntities;
    }

    @Override
    public void cbS26$setHaveGlowingEntities(boolean value) {
        this.haveGlowingEntities = value;
    }
}
