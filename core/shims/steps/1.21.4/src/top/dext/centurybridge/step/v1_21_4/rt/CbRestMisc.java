package top.dext.centurybridge.step.v1_21_4.rt;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import net.minecraft.class_2338;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_3218;
import net.minecraft.class_5778;
import net.minecraft.class_7225;
import net.minecraft.class_7654;
import net.minecraft.class_10376;

/**
 * Values 1.21.3 constructors and calls implied that 1.21.4 asks for explicitly (superCtorReshapes /
 * callReshapes), each the value 1.21.3's own code used:
 * <ul>
 * <li>SimpleJsonResourceReloadListener(registries, codec, directory) / (codec, directory) and scanDirectory(..,
 *     directory, ..): 1.21.3 read the directory through FileToIdConverter.json(directory) and decoded with the
 *     registries' serialization context over JsonOps; 1.21.4 takes the converter and context themselves.</li>
 * <li>CherryLeavesBlock(props): 1.21.3's cherry leaves dropped CHERRY_LEAVES particles one tick in ten; 1.21.4's
 *     constructor takes that chance and particle (1.21.4's own cherry leaves pass 10 and CHERRY_LEAVES).</li>
 * <li>TargetColorParticleOption(target, color): 1.21.3's client gave each trail particle a lifetime of
 *     10 + random(40) ticks; 1.21.4's option carries the duration, drawn from the same range.</li>
 * <li>AcquirePoi.create(..): 1.21.4 adds a position validator; its own overload without one passes
 *     (level, pos) -> true, which 1.21.3's behaviour is.</li>
 * <li>SpawnUtil.trySpawnMob(..): 1.21.4 adds a collision check flag; 1.21.3 did no such check (false).</li>
 * <li>MultifaceGrowthConfiguration's block: 1.21.4 types it MultifaceSpreadeableBlock, which every 1.21.3
 *     MultifaceBlock was (a mod's subclass is reparented by superRenames).</li>
 * </ul>
 */
public final class CbRestMisc {

    private CbRestMisc() {
    }

    public static DynamicOps<JsonElement> jsonContext(class_7225.class_7874 registries) {
        return registries.method_57093(JsonOps.INSTANCE);
    }

    public static class_7654 jsonDirectory(String directory) {
        return class_7654.method_45114(directory);
    }

    public static int cherryParticleChance() {
        return 10;
    }

    public static class_2394 cherryParticle() {
        return class_2398.field_43379;
    }

    public static int trailDuration() {
        return 10 + ThreadLocalRandom.current().nextInt(40);
    }

    public static BiPredicate<class_3218, class_2338> anyPoiPosition() {
        return (level, pos) -> true;
    }

    public static boolean noCollisionCheck() {
        return false;
    }

    public static class_10376 spreadable(class_5778 block) {
        return (class_10376) block;
    }

    /** MultifaceGrowthConfiguration.placeBlock read as 1.21.3's MultifaceBlock-typed field */
    public static class_5778 field_37709(net.minecraft.class_5780 config) {
        return config.field_37709;
    }
}
