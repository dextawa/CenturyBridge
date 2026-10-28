package cb.s215;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_1959;
import net.minecraft.class_1966;
import net.minecraft.class_2338;
import net.minecraft.class_2791;
import net.minecraft.class_2794;
import net.minecraft.class_2902;
import net.minecraft.class_3233;
import net.minecraft.class_4543;
import net.minecraft.class_4966;
import net.minecraft.class_5138;
import net.minecraft.class_5483;
import net.minecraft.class_5539;
import net.minecraft.class_6012;
import net.minecraft.class_6748;
import net.minecraft.class_6880;
import net.minecraft.class_7138;

/**
 * A 1.21.4 ChunkGenerator whose getMobsAt override reads vanilla's list (super) as 1.21.4 entries and answers a
 * WeightedRandomList built from them plus its own blaze of weight 77. Never registered or used for terrain: the
 * Driver calls getMobsAt as a mod would, and cb-s215n calls it through 1.21.5's ChunkGenerator.getMobsAt, reading the
 * answer as 1.21.5's WeightedList<SpawnerData> (the override round trip).
 */
public final class Gen extends class_2794 {
    public static volatile int vanillaSeen;

    public Gen(class_1966 biomes) {
        super(biomes);
    }

    @Override
    public class_6012<class_5483.class_1964> method_12113(class_6880<class_1959> biome, class_5138 structures, class_1311 category, class_2338 pos) {
        List<class_5483.class_1964> out = new ArrayList<>();
        int seen = 0;
        for (class_5483.class_1964 d : super.method_12113(biome, structures, category, pos).method_34994()) {
            // a 1.21.4 entry: its own weight, read through WeightedEntry
            if (d.method_34979().method_34976() > 0) {
                seen++;
            }
            out.add(d);
        }
        vanillaSeen = seen;
        out.add(new class_5483.class_1964(class_1299.field_6099, 77, 1, 1));
        return class_6012.method_34988(out);
    }

    @Override
    protected MapCodec<? extends class_2794> method_28506() {
        throw new UnsupportedOperationException("cb-s215 test generator has no codec");
    }

    @Override
    public void method_12108(class_3233 region, long seed, class_7138 random, class_4543 biomes, class_5138 structures, class_2791 chunk) {
    }

    @Override
    public void method_12110(class_3233 region, class_5138 structures, class_7138 random, class_2791 chunk) {
    }

    @Override
    public void method_12107(class_3233 region) {
    }

    @Override
    public int method_12104() {
        return 256;
    }

    @Override
    public CompletableFuture<class_2791> method_12088(class_6748 blender, class_7138 random, class_5138 structures, class_2791 chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int method_16398() {
        return 63;
    }

    @Override
    public int method_33730() {
        return -64;
    }

    @Override
    public int method_16397(int x, int z, class_2902.class_2903 type, class_5539 heights, class_7138 random) {
        return 64;
    }

    @Override
    public class_4966 method_26261(int x, int z, class_5539 heights, class_7138 random) {
        return new class_4966(heights.method_31607(), new net.minecraft.class_2680[0]);
    }

    @Override
    public void method_40450(List<String> lines, class_7138 random, class_2338 pos) {
    }
}
