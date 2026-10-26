package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.math.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbWallRenderStateViews;

/**
 * What a 1.21.11 write of BannerRenderState.angle or SkullBlockRenderState.direction / rotationDegrees put into the
 * state's 26.1 transformation (rt/CbWallRenderStateViews): the transformation built and the 1.21.11 values it stands
 * for. Adds no 1.21.11 member: default priority.
 */
@Environment(EnvType.CLIENT)
@Mixin({BannerRenderState.class, SkullBlockRenderState.class})
public abstract class CbWallBlockEntityPoseMixin implements CbWallRenderStateViews.KeptPose {

    @Unique
    private Transformation cb$keptPose;
    @Unique
    private Direction cb$keptDirection;
    @Unique
    private float cb$keptDegrees;

    @Override
    public Transformation cb$keptPose() {
        return this.cb$keptPose;
    }

    @Override
    public Direction cb$keptDirection() {
        return this.cb$keptDirection;
    }

    @Override
    public float cb$keptDegrees() {
        return this.cb$keptDegrees;
    }

    @Override
    public void cb$keep(Transformation pose, Direction direction, float degrees) {
        this.cb$keptPose = pose;
        this.cb$keptDirection = direction;
        this.cb$keptDegrees = degrees;
    }
}
