package cb.s215;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_32;
import net.minecraft.class_3218;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_3949;
import net.minecraft.class_5268;
import net.minecraft.class_5304;
import net.minecraft.class_5321;
import net.minecraft.class_5363;
import net.minecraft.class_6880;
import net.minecraft.class_8565;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.4 ServerLevel subclass overriding the sound / event members whose excepted Player 1.21.5 widened to Entity.
 * Driver allocates it without its constructor (only the overrides run) and calls 1.21.5's Entity shapes the way
 * 1.21.5 vanilla does: each override records what it was handed.
 */
public class SoundLevel extends class_3218 {
    public final List<String> seen = new ArrayList<>();

    public SoundLevel(MinecraftServer server, Executor ex, class_32.class_5143 storage, class_5268 data, class_5321<class_1937> key,
                      class_5363 stem, class_3949 progress, boolean debug, long seed, List<class_5304> spawners, boolean tick,
                      class_8565 random) {
        super(server, ex, storage, data, key, stem, progress, debug, seed, spawners, tick, random);
    }

    private static String who(class_1657 p) {
        return p == null ? "null" : "player";
    }

    @Override
    public void method_8465(class_1657 except, double x, double y, double z, class_6880<class_3414> sound, class_3419 source,
                            float volume, float pitch, long seed) {
        seen.add("seeded:" + who(except) + ":" + (int) x + ":" + seed);
    }

    @Override
    public void method_43128(class_1657 except, double x, double y, double z, class_3414 sound, class_3419 source, float volume,
                             float pitch) {
        seen.add("sound:" + who(except) + ":" + x);
    }

    @Override
    public void method_8444(class_1657 except, int type, class_2338 pos, int data) {
        seen.add("event:" + who(except) + ":" + type + ":" + data);
    }

    @Override
    public boolean method_8505(class_1657 player, class_2338 pos) {
        seen.add("interact:" + who(player));
        return true;
    }

    /** 1.21.4's Entity overload, folded into playSound(Entity, BlockPos, ..) in 1.21.5; its super call ends in method_43128 */
    @Override
    public void method_45445(class_1297 entity, class_2338 pos, class_3414 sound, class_3419 source, float volume, float pitch) {
        seen.add("entity-overload");
        super.method_45445(entity, pos, sound, source, volume, pitch);
    }
}
