package top.dext.centurybridge.step.v26_1.rt;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.LpVec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11 FriendlyByteBuf Vec3 codecs and ServerLevel members 26.1 removed or moved onto the server.
 * <ul>
 * <li>writeVec3 / readVec3: three raw doubles, 1.21.11's bodies; writeLpVec3 / readLpVec3: 26.1's LpVec3,
 *     which 1.21.11's methods called.</li>
 * <li>ServerLevel.getShade answered 1.0; setWeatherParameters, setDayTime and the random sequences act on the
 *     server's (26.1 keeps one weather, one overworld clock and one random-sequence set per server; 1.21.11's
 *     levels all used the overworld's); getDayCount is the overworld day time over 24000, as 1.21.11 divided.</li>
 * </ul>
 */
public final class CbWallServerLevel {

    private CbWallServerLevel() {
    }

    // ---- FriendlyByteBuf ----

    /** 1.21.11 FriendlyByteBuf.writeVec3(ByteBuf, Vec3) */
    public static void writeVec3(ByteBuf buf, Vec3 v) {
        buf.writeDouble(v.x());
        buf.writeDouble(v.y());
        buf.writeDouble(v.z());
    }

    /** 1.21.11 FriendlyByteBuf.readVec3(ByteBuf) */
    public static Vec3 readVec3(ByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    /** 1.21.11 FriendlyByteBuf.writeVec3(Vec3) */
    public static void writeVec3(FriendlyByteBuf buf, Vec3 v) {
        writeVec3((ByteBuf) buf, v);
    }

    /** 1.21.11 FriendlyByteBuf.readVec3() */
    public static Vec3 readVec3(FriendlyByteBuf buf) {
        return readVec3((ByteBuf) buf);
    }

    /** 1.21.11 FriendlyByteBuf.writeLpVec3(Vec3) */
    public static void writeLpVec3(FriendlyByteBuf buf, Vec3 v) {
        LpVec3.write(buf, v);
    }

    /** 1.21.11 FriendlyByteBuf.readLpVec3() */
    public static Vec3 readLpVec3(FriendlyByteBuf buf) {
        return LpVec3.read(buf);
    }

    // ---- ServerLevel ----

    /** 1.21.11 ServerLevel.getShade(Direction, boolean): 1.0 */
    public static float getShade(ServerLevel level, Direction direction, boolean shade) {
        return 1.0F;
    }

    /** 1.21.11 ServerLevel.setWeatherParameters(clearTime, weatherTime, raining, thundering) */
    public static void setWeatherParameters(ServerLevel level, int clearTime, int weatherTime, boolean raining, boolean thundering) {
        level.getServer().setWeatherParameters(clearTime, weatherTime, raining, thundering);
    }

    /** 1.21.11 ServerLevel.setDayTime(long): the overworld clock */
    public static void setDayTime(ServerLevel level, long time) {
        CbWallLevelData.setOverworldTime(level.getServer(), time);
    }

    /** 1.21.11 ServerLevel.getDayCount() */
    public static long getDayCount(ServerLevel level) {
        return level.getOverworldClockTime() / 24000L;
    }

    /** 1.21.11 ServerLevel.getRandomSequence(Identifier) */
    public static RandomSource getRandomSequence(ServerLevel level, Identifier id) {
        return level.getServer().getRandomSequence(id);
    }

    /** 1.21.11 ServerLevel.getRandomSequences() */
    public static RandomSequences getRandomSequences(ServerLevel level) {
        return level.getServer().getRandomSequences();
    }
}
