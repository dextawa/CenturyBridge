package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

/**
 * The 1.20.4 codecs of the world's dispatched types this area owns -- biome sources and chunk
 * generators -- on 1.20.5, which made them map codecs (see CbCodecs for why the conversion is
 * exact). A mod reading a vanilla CODEC field gets the codec 1.20.4 held there; a mod's codec()
 * override is answered to 1.20.5 as the map codec standing for it (override hook unwrap), and its
 * own super.codec() gets super's map codec as the codec 1.20.4 answered (super bridge).
 */
public final class CbBlockCodecs {

    /** super's codec() of a mod ChunkGenerator subclass, reached from its super.codec() */
    public interface ChunkGeneratorSuper {
        MapCodec<?> cb$super$method_28506();
    }

    /** super's codec() of a mod BiomeSource subclass */
    public interface BiomeSourceSuper {
        MapCodec<?> cb$super$method_28442();
    }

    /** CheckerboardColumnBiomeSource.CODEC */
    public static Codec<?> field_24715() {
        return CbCodecs.fromMapCodec(net.minecraft.class_1973.field_24715);
    }

    /** FixedBiomeSource.CODEC */
    public static Codec<?> field_24717() {
        return CbCodecs.fromMapCodec(net.minecraft.class_1992.field_24717);
    }

    /** MultiNoiseBiomeSource.CODEC */
    public static Codec<?> field_24719() {
        return CbCodecs.fromMapCodec(net.minecraft.class_4766.field_24719);
    }

    /** TheEndBiomeSource.CODEC */
    public static Codec<?> field_24730() {
        return CbCodecs.fromMapCodec(net.minecraft.class_2169.field_24730);
    }

    /** ChunkGenerator's protected codec() (CbBlockChunkGeneratorInvoker) */
    public interface ChunkGeneratorCodec {
        MapCodec<? extends net.minecraft.class_2794> cb$chunkGeneratorCodec();
    }

    /** BiomeSource's protected codec() (CbBlockBiomeSourceInvoker) */
    public interface BiomeSourceCodec {
        MapCodec<? extends net.minecraft.class_1966> cb$biomeSourceCodec();
    }

    /** this.codec() in a chunk generator class that inherits it: 1.20.5's map codec as 1.20.4's codec, one object per codec (CbCodecs) */
    public static Codec<?> method_28506(net.minecraft.class_2794 self) {
        return CbCodecs.fromMapCodec(((ChunkGeneratorCodec) self).cb$chunkGeneratorCodec());
    }

    /** this.codec() in a biome source class that inherits it, likewise */
    public static Codec<?> method_28442(net.minecraft.class_1966 self) {
        return CbCodecs.fromMapCodec(((BiomeSourceCodec) self).cb$biomeSourceCodec());
    }

    /** a mod ChunkGenerator's super.codec() */
    public static Codec<?> superChunkGeneratorCodec(ChunkGeneratorSuper self) {
        return CbCodecs.fromMapCodec(self.cb$super$method_28506());
    }

    /** a mod BiomeSource's super.codec() */
    public static Codec<?> superBiomeSourceCodec(BiomeSourceSuper self) {
        return CbCodecs.fromMapCodec(self.cb$super$method_28442());
    }

    private CbBlockCodecs() {
    }
}
