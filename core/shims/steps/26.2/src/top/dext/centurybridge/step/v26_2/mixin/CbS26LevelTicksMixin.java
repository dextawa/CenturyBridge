package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26LevelTicks;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2's private LevelRenderer.ticks, which 26.2 dropped, restored on the class under its own name so mods'
 * @Shadow / @Accessor on it link. Its value is 26.2's clock for what 26.1.2 counted with it (rt/CbS26LevelTicks).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = LevelRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26LevelTicksMixin implements CbS26LevelTicks {
    /** 26.1.2 LevelRenderer.ticks: (int) ClientLevel.getGameTime() of the level being drawn */
    private int ticks;

    @Override
    public void cbS26$setTicks(int ticks) {
        this.ticks = ticks;
    }
}
