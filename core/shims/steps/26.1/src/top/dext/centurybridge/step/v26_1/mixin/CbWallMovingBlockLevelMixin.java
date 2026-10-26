package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.world.level.BlockAndLightGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbWallRenderStateViews;

/**
 * The level a 1.21.11 write of MovingBlockRenderState.level named (rt/CbWallRenderStateViews#movingBlockLevel).
 * Adds no 1.21.11 member: default priority.
 */
@Environment(EnvType.CLIENT)
@Mixin(MovingBlockRenderState.class)
public abstract class CbWallMovingBlockLevelMixin implements CbWallRenderStateViews.KeptLevel {

    @Unique
    private BlockAndLightGetter cb$keptLevel;

    @Override
    public BlockAndLightGetter cb$keptLevel() {
        return this.cb$keptLevel;
    }

    @Override
    public void cb$keepLevel(BlockAndLightGetter level) {
        this.cb$keptLevel = level;
    }
}
