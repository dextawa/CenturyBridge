package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1959;
import net.minecraft.class_238;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2586;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2784;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2874;
import net.minecraft.class_2902;
import net.minecraft.class_3568;
import net.minecraft.class_3610;
import net.minecraft.class_4538;
import net.minecraft.class_4543;
import net.minecraft.class_5455;
import net.minecraft.class_6539;
import net.minecraft.class_6880;
import net.minecraft.class_7699;
import net.minecraft.class_7923;

/**
 * A plain 1.20.2 BlockGetter where 1.20.3 wants a LevelReader (Block.getCloneItemStack, see
 * {@link CbWorldBlocks#levelReader}). It answers the BlockGetter surface -- block states, fluids,
 * block entities, height -- from the getter, and registry access from the built-in registries, which
 * is all the 1.20.3 getCloneItemStack implementations read (the stems look their seed item up by key
 * in the item registry). Every other world query has no answer in a BlockGetter and throws, naming
 * itself, instead of making one up.
 */
public final class CbWorldGetterReader implements class_4538 {

    private final class_1922 getter;

    CbWorldGetterReader(class_1922 getter) {
        this.getter = getter;
    }

    private UnsupportedOperationException none(String what) {
        return new UnsupportedOperationException("a 1.20.2 BlockGetter (" + getter.getClass().getName()
            + ") stands in for the LevelReader 1.20.3 needs here and cannot answer " + what);
    }

    // BlockGetter / LevelHeightAccessor: the getter's own answers
    @Override
    public class_2586 method_8321(class_2338 pos) {
        return getter.method_8321(pos);
    }

    @Override
    public class_2680 method_8320(class_2338 pos) {
        return getter.method_8320(pos);
    }

    @Override
    public class_3610 method_8316(class_2338 pos) {
        return getter.method_8316(pos);
    }

    @Override
    public int method_31605() {
        return getter.method_31605();
    }

    @Override
    public int method_31607() {
        return getter.method_31607();
    }

    // registry access: the built-in registries (blocks, items, ...)
    @Override
    public class_5455 method_30349() {
        return class_5455.method_40302(class_7923.field_41167);
    }

    // world state a BlockGetter does not have
    @Override
    public class_2791 method_8402(int x, int z, class_2806 status, boolean load) {
        throw none("getChunk");
    }

    @Override
    public boolean method_8393(int x, int z) {
        throw none("hasChunk");
    }

    @Override
    public int method_8624(class_2902.class_2903 type, int x, int z) {
        throw none("getHeight(Heightmap.Types, x, z)");
    }

    @Override
    public int method_8594() {
        throw none("getSkyDarken");
    }

    @Override
    public class_4543 method_22385() {
        throw none("getBiomeManager");
    }

    @Override
    public class_6880<class_1959> method_22387(int x, int y, int z) {
        throw none("getUncachedNoiseBiome");
    }

    @Override
    public boolean method_8608() {
        throw none("isClientSide");
    }

    @Override
    public int method_8615() {
        throw none("getSeaLevel");
    }

    @Override
    public class_2874 method_8597() {
        throw none("dimensionType");
    }

    @Override
    public class_7699 method_45162() {
        throw none("enabledFeatures");
    }

    @Override
    public float method_24852(class_2350 direction, boolean shade) {
        throw none("getShade");
    }

    @Override
    public class_3568 method_22336() {
        throw none("getLightEngine");
    }

    @Override
    public class_2784 method_8621() {
        throw none("getWorldBorder");
    }

    @Override
    public List<class_265> method_20743(class_1297 entity, class_238 box) {
        throw none("getEntityCollisions");
    }

    @Override
    public int method_23752(class_2338 pos, class_6539 resolver) {
        throw none("getBlockTint");
    }
}
