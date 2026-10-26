package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.joml.Vector3f;

/**
 * 1.21.11 block entity render state fields 26.1 keeps as another field of the same state (javap of both renderers'
 * extractRenderState and submit):
 * <ul>
 * <li>BedRenderState.isHead: 1.21.11's extract set it to part == HEAD of the bed block; 26.1 keeps the part itself.
 *     A view both ways.</li>
 * <li>ChestRenderState.angle: 1.21.11's extract set it to the chest block's FACING.toYRot() and its submit turned the
 *     model by it; 26.1 keeps the facing and turns the model by it. Read: the facing's angle. Written: the horizontal
 *     facing nearest the angle (1.21.11 turned by any angle a mod wrote; 26.1 turns only by a facing).</li>
 * <li>BannerRenderState.angle / standing and SkullBlockRenderState.direction / rotationDegrees: 1.21.11's extract
 *     computed them from the block state and its submit posed the model by them (banner: translate (0.5, 0, 0.5),
 *     rotate Y by angle, scale (2/3, -2/3, -2/3); skull: translate by direction or (0.5, 0, 0.5), scale (-1, -1, 1),
 *     the model's head turned by rotationDegrees); 26.1's extract keeps the pose as a Transformation (the same pose,
 *     WallAndGroundTransformations of the same block state properties) plus the banner's attachmentType. Read: what
 *     1.21.11's extract computed from the state's block state, unless a 1.21.11 write's pose is still the state's
 *     transformation (then the written values). Written: the transformation is rebuilt from the 1.21.11 values by
 *     1.21.11's submit arithmetic (mixin/CbWallBlockEntityPoseMixin keeps it with the values).</li>
 * <li>The entity states' block fields (Enderman carriedBlock, Tnt blockState, CopperGolem blockOnAntenna,
 *     SnowGolem hasPumpkin, BlockDisplay blockRenderState): views of the BlockModelRenderState 26.1 resolves from
 *     the same block (rt/CbWallBlockModelSource).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallRenderStateViews {

    private CbWallRenderStateViews() {
    }

    /** 1.21.11 BedRenderState.isHead (get) */
    public static boolean bedIsHead(BedRenderState state) {
        return state.part == BedPart.HEAD;
    }

    /** 1.21.11 BedRenderState.isHead (put) */
    public static void setBedIsHead(BedRenderState state, boolean head) {
        state.part = head ? BedPart.HEAD : BedPart.FOOT;
    }

    /** 1.21.11 ChestRenderState.angle (get) */
    public static float chestAngle(ChestRenderState state) {
        return state.facing.toYRot();
    }

    /** 1.21.11 ChestRenderState.angle (put) */
    public static void setChestAngle(ChestRenderState state, float angle) {
        state.facing = Direction.fromYRot(angle);
    }

    /** mixin/CbWallBlockEntityPoseMixin: the pose a 1.21.11 write built and the values it stands for */
    @Environment(EnvType.CLIENT)
    public interface KeptPose {
        Transformation cb$keptPose();

        Direction cb$keptDirection();

        float cb$keptDegrees();

        void cb$keep(Transformation pose, Direction direction, float degrees);
    }

    private static KeptPose written(Object state, Transformation current) {
        KeptPose kept = (KeptPose) state;
        return kept.cb$keptPose() != null && kept.cb$keptPose() == current ? kept : null;
    }

    /** 1.21.11 BannerRenderState.angle (get) */
    public static float bannerAngle(BannerRenderState state) {
        KeptPose kept = written(state, state.transformation);
        if (kept != null) {
            return kept.cb$keptDegrees();
        }
        BlockState block = state.blockState;
        if (block == null) {
            return 0.0F;
        }
        if (block.getBlock() instanceof BannerBlock) {
            return -RotationSegment.convertToDegrees(block.getValue(BannerBlock.ROTATION));
        }
        return block.hasProperty(WallBannerBlock.FACING) ? -block.getValue(WallBannerBlock.FACING).toYRot() : 0.0F;
    }

    /** 1.21.11 BannerRenderState.angle (put): 1.21.11 BannerRenderer.submitBanner's pose for that angle */
    public static void setBannerAngle(BannerRenderState state, float angle) {
        Transformation pose = new Transformation(new Vector3f(0.5F, 0.0F, 0.5F), Axis.YP.rotationDegrees(angle),
            new Vector3f(0.6666667F, -0.6666667F, -0.6666667F), null);
        state.transformation = pose;
        ((KeptPose) state).cb$keep(pose, null, angle);
    }

    /** 1.21.11 BannerRenderState.standing (get): 26.1 keeps it as the attachment type */
    public static boolean bannerStanding(BannerRenderState state) {
        return state.attachmentType == BannerBlock.AttachmentType.GROUND;
    }

    /** 1.21.11 BannerRenderState.standing (put): picks the standing or the wall model, as in 1.21.11 */
    public static void setBannerStanding(BannerRenderState state, boolean standing) {
        state.attachmentType = standing ? BannerBlock.AttachmentType.GROUND : BannerBlock.AttachmentType.WALL;
    }

    /** 1.21.11 SkullBlockRenderState.direction (get): the wall skull's facing, null for a ground skull */
    public static Direction skullDirection(SkullBlockRenderState state) {
        KeptPose kept = written(state, state.transformation);
        if (kept != null) {
            return kept.cb$keptDirection();
        }
        BlockState block = state.blockState;
        return block != null && block.getBlock() instanceof WallSkullBlock ? block.getValue(WallSkullBlock.FACING) : null;
    }

    /** 1.21.11 SkullBlockRenderState.rotationDegrees (get) */
    public static float skullRotationDegrees(SkullBlockRenderState state) {
        KeptPose kept = written(state, state.transformation);
        if (kept != null) {
            return kept.cb$keptDegrees();
        }
        BlockState block = state.blockState;
        if (block == null) {
            return 0.0F;
        }
        if (block.getBlock() instanceof WallSkullBlock) {
            return RotationSegment.convertToDegrees(RotationSegment.convertToSegment(block.getValue(WallSkullBlock.FACING).getOpposite()));
        }
        return block.hasProperty(SkullBlock.ROTATION) ? RotationSegment.convertToDegrees(block.getValue(SkullBlock.ROTATION)) : 0.0F;
    }

    /** 1.21.11 SkullBlockRenderState.direction (put) */
    public static void setSkullDirection(SkullBlockRenderState state, Direction direction) {
        poseSkull(state, direction, skullRotationDegrees(state));
    }

    /** 1.21.11 SkullBlockRenderState.rotationDegrees (put) */
    public static void setSkullRotationDegrees(SkullBlockRenderState state, float degrees) {
        poseSkull(state, skullDirection(state), degrees);
    }

    /**
     * 1.21.11 SkullBlockRenderer.submitSkull's pose: translate (0.5 - stepX/4, 0.25, 0.5 - stepZ/4) for a wall skull,
     * (0.5, 0, 0.5) without a direction, scale (-1, -1, 1), then the head turned by the degrees about Y; under that
     * scale the head's turn is a turn by minus the degrees before it (26.1's TRANSFORMATIONS have the same form).
     */
    private static void poseSkull(SkullBlockRenderState state, Direction direction, float degrees) {
        Vector3f translation = direction == null ? new Vector3f(0.5F, 0.0F, 0.5F)
            : new Vector3f(0.5F - direction.getStepX() * 0.25F, 0.25F, 0.5F - direction.getStepZ() * 0.25F);
        Transformation pose = new Transformation(translation, Axis.YP.rotationDegrees(-degrees), new Vector3f(-1.0F, -1.0F, 1.0F), null);
        state.transformation = pose;
        ((KeptPose) state).cb$keep(pose, direction, degrees);
    }

    /** 1.21.11 EndermanRenderState.carriedBlock (get) */
    public static BlockState endermanCarriedBlock(EndermanRenderState state) {
        return CbWallBlockModelSource.source(state.carriedBlock);
    }

    /** 1.21.11 EndermanRenderState.carriedBlock (put) */
    public static void setEndermanCarriedBlock(EndermanRenderState state, BlockState block) {
        CbWallBlockModelSource.show(state.carriedBlock, block, EndermanRenderer.BLOCK_DISPLAY_CONTEXT);
    }

    /** 1.21.11 TntRenderState.blockState (get) */
    public static BlockState tntBlockState(TntRenderState state) {
        return CbWallBlockModelSource.source(state.blockState);
    }

    /** 1.21.11 TntRenderState.blockState (put) */
    public static void setTntBlockState(TntRenderState state, BlockState block) {
        CbWallBlockModelSource.show(state.blockState, block, TntRenderer.BLOCK_DISPLAY_CONTEXT);
    }

    /** 1.21.11 CopperGolemRenderState.blockOnAntenna (get) */
    public static Optional<BlockState> copperGolemBlockOnAntenna(CopperGolemRenderState state) {
        return Optional.ofNullable(CbWallBlockModelSource.source(state.blockOnAntenna));
    }

    /** 1.21.11 CopperGolemRenderState.blockOnAntenna (put) */
    public static void setCopperGolemBlockOnAntenna(CopperGolemRenderState state, Optional<BlockState> block) {
        CbWallBlockModelSource.show(state.blockOnAntenna, block.orElse(null), CopperGolemRenderer.BLOCK_DISPLAY_CONTEXT);
    }

    /** 1.21.11 SnowGolemRenderState.hasPumpkin (get): 26.1 resolves the carved pumpkin into headBlock when it has one */
    public static boolean snowGolemHasPumpkin(SnowGolemRenderState state) {
        return CbWallBlockModelSource.source(state.headBlock) != null;
    }

    /** 1.21.11 SnowGolemRenderState.hasPumpkin (put): 1.21.11's head layer drew a carved pumpkin when it was set */
    public static void setSnowGolemHasPumpkin(SnowGolemRenderState state, boolean pumpkin) {
        CbWallBlockModelSource.show(state.headBlock, pumpkin ? Blocks.CARVED_PUMPKIN.defaultBlockState() : null,
            SnowGolemRenderer.BLOCK_DISPLAY_CONTEXT);
    }

    /** 1.21.11 BlockDisplayEntityRenderState.blockRenderState (get) */
    public static Display.BlockDisplay.BlockRenderState blockDisplayBlockRenderState(BlockDisplayEntityRenderState state) {
        BlockState block = CbWallBlockModelSource.source(state.blockModel);
        return block != null ? new Display.BlockDisplay.BlockRenderState(block) : null;
    }

    /** 1.21.11 BlockDisplayEntityRenderState.blockRenderState (put) */
    public static void setBlockDisplayBlockRenderState(BlockDisplayEntityRenderState state, Display.BlockDisplay.BlockRenderState block) {
        CbWallBlockModelSource.show(state.blockModel, block != null ? block.blockState() : null, DisplayRenderer.BLOCK_DISPLAY_CONTEXT);
    }

    /** mixin/CbWallMovingBlockLevelMixin: the level a 1.21.11 write named */
    @Environment(EnvType.CLIENT)
    public interface KeptLevel {
        BlockAndLightGetter cb$keptLevel();

        void cb$keepLevel(BlockAndLightGetter level);
    }

    /**
     * 1.21.11 MovingBlockRenderState.level (get). 1.21.11's state asked its level only for getShade and
     * getLightEngine (javap: its own getShade / getLightEngine delegated there; the block, biome and position were
     * fields); 26.1's state keeps those two answers (cardinalLighting, lightEngine), which its renderers fill from the
     * client level. The converted field type is 26.1's BlockAndLightGetter (the wall's name for
     * 1.21.11's BlockAndTintGetter). Answers: the level a 1.21.11 write named while the state still holds that level's
     * answers; the client level when the state holds its light engine; otherwise the state itself, which answers both
     * as the state holds them (1.21.11's initial value, the empty getter, answered the defaults 26.1's state starts with).
     */
    public static BlockAndLightGetter movingBlockLevel(MovingBlockRenderState state) {
        BlockAndLightGetter kept = ((KeptLevel) state).cb$keptLevel();
        if (kept != null && kept.getLightEngine() == state.lightEngine
            && (!(kept instanceof BlockAndTintGetter tint) || tint.cardinalLighting() == state.cardinalLighting)) {
            return kept;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getLightEngine() == state.lightEngine) {
            return level;
        }
        return state;
    }

    /**
     * 1.21.11 MovingBlockRenderState.level (put): the state takes the level's light engine and, from a level that
     * shades (26.1's BlockAndTintGetter, which ClientLevel is), its cardinal lighting (1.21.11's getShade).
     */
    public static void setMovingBlockLevel(MovingBlockRenderState state, BlockAndLightGetter level) {
        if (level instanceof BlockAndTintGetter tint) {
            state.cardinalLighting = tint.cardinalLighting();
        }
        state.lightEngine = level.getLightEngine();
        ((KeptLevel) state).cb$keepLevel(level);
    }
}
