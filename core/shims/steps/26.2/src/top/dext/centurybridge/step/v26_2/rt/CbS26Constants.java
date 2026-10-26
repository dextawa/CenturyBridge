package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.scores.TeamColor;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 26.1.2 public static fields 26.2 dropped or retyped, read through fieldGetters (s26c-constants.json), each answering
 * the value 26.1.2's static initializer stored (javap of both clinits):
 * <ul>
 *   <li>LevelChunkSection.SECTION_WIDTH / SECTION_HEIGHT / SECTION_SIZE: 16, 16, 4096 (26.1.2 constants);</li>
 *   <li>Waypoint.MAX_RANGE: 60000000 (26.1.2 constant);</li>
 *   <li>ChunkPos.MAX_COORDINATE_VALUE: blockToSectionCoord(BlockPos.MAX_HORIZONTAL_COORDINATE) minus 26.1.2's
 *       SAFETY_MARGIN_CHUNKS, (32 + the FULL step's accumulated dependency count + 1) * 2, computed as 26.1.2's
 *       clinit did from the same 26.2 members;</li>
 *   <li>Timelines.NIGHT_FOG_COLOR_MULTIPLIER: ARGB.colorFromFloat(1, 0.06, 0.06, 0.09);</li>
 *   <li>Goat.LONG_JUMPING_DIMENSIONS: EntityDimensions.scalable(0.9, 1.3).scale(0.7), one object kept;</li>
 *   <li>Mth.X_AXIS / Y_AXIS / Z_AXIS: 26.2 keeps the same shared Vector3f objects (its clinit stores new Vector3f) and
 *       types the fields Vector3fc; the getter hands out that object, as 26.1.2's field did;</li>
 *   <li>ObjectiveCriteria.TEAM_KILL / KILLED_BY_TEAM: 26.1.2's ObjectiveCriteria[16] in its colour order (BLACK ..
 *       WHITE), holding the "teamkill."/"killedByTeam." criteria 26.2 registers and keeps in a TeamColor map: the
 *       same criteria objects, one array per field kept.</li>
 * </ul>
 */
public final class CbS26Constants {
    private CbS26Constants() {}

    /** 26.1.2's literal colour order of TEAM_KILL / KILLED_BY_TEAM (ChatFormatting BLACK .. WHITE) */
    private static final String[] COLOURS = {
        "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", "GOLD", "GRAY",
        "DARK_GRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
    };

    public static int LevelChunkSection_SECTION_WIDTH() {
        return 16;
    }

    public static int LevelChunkSection_SECTION_HEIGHT() {
        return 16;
    }

    public static int LevelChunkSection_SECTION_SIZE() {
        return 4096;
    }

    public static int Waypoint_MAX_RANGE() {
        return 60000000;
    }

    private static final class ChunkPosHolder {
        static final int SAFETY_MARGIN_CHUNKS =
            (32 + ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().size() + 1) * 2;
        static final int MAX_COORDINATE_VALUE = SectionPos.blockToSectionCoord(BlockPos.MAX_HORIZONTAL_COORDINATE) - SAFETY_MARGIN_CHUNKS;
    }

    public static int ChunkPos_MAX_COORDINATE_VALUE() {
        return ChunkPosHolder.MAX_COORDINATE_VALUE;
    }

    private static final class TimelinesHolder {
        static final int NIGHT_FOG_COLOR_MULTIPLIER = ARGB.colorFromFloat(1.0F, 0.06F, 0.06F, 0.09F);
    }

    public static int Timelines_NIGHT_FOG_COLOR_MULTIPLIER() {
        return TimelinesHolder.NIGHT_FOG_COLOR_MULTIPLIER;
    }

    private static final class GoatHolder {
        static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.scalable(0.9F, 1.3F).scale(0.7F);
    }

    public static EntityDimensions Goat_LONG_JUMPING_DIMENSIONS() {
        return GoatHolder.LONG_JUMPING_DIMENSIONS;
    }

    public static Vector3f Mth_X_AXIS() {
        return (Vector3f) Mth.X_AXIS;
    }

    public static Vector3f Mth_Y_AXIS() {
        return (Vector3f) Mth.Y_AXIS;
    }

    public static Vector3f Mth_Z_AXIS() {
        return (Vector3f) Mth.Z_AXIS;
    }

    /** 26.1.2 Mth.rotationAroundAxis(Vector3f, Quaternionf, Quaternionf): 26.2 takes the axis as Vector3fc, same body */
    public static Quaternionf rotationAroundAxis(Vector3f axis, Quaternionf rotation, Quaternionf result) {
        return Mth.rotationAroundAxis(axis, rotation, result);
    }

    private static ObjectiveCriteria[] byColour(java.util.Map<TeamColor, ObjectiveCriteria> map) {
        ObjectiveCriteria[] criteria = new ObjectiveCriteria[COLOURS.length];
        for (int i = 0; i < COLOURS.length; i++) {
            criteria[i] = map.get(TeamColor.valueOf(COLOURS[i]));
        }
        return criteria;
    }

    private static final class CriteriaHolder {
        static final ObjectiveCriteria[] TEAM_KILL = byColour(ObjectiveCriteria.TEAM_KILL);
        static final ObjectiveCriteria[] KILLED_BY_TEAM = byColour(ObjectiveCriteria.KILLED_BY_TEAM);
    }

    public static ObjectiveCriteria[] ObjectiveCriteria_TEAM_KILL() {
        return CriteriaHolder.TEAM_KILL;
    }

    public static ObjectiveCriteria[] ObjectiveCriteria_KILLED_BY_TEAM() {
        return CriteriaHolder.KILLED_BY_TEAM;
    }
}
