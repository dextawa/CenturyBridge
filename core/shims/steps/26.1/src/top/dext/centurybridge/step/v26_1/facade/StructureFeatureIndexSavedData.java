package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import top.dext.centurybridge.step.v26_1.rt.CbWallSavedData;

/**
 * 1.21.11's StructureFeatureIndexSavedData (net/minecraft/class_3440): the pre-1.13 structure index files
 * ({@code <legacy name>_index.dat}: every chunk a structure of that kind started in, and the ones not yet moved
 * into their chunk) LegacyStructureDataHandler read. 26.1 dropped it (its LegacyStructureFileFix moves that data
 * into the chunks when the world is upgraded). 1.21.11's members and bodies, on 26.1's SavedData; type(id) names
 * the file by 1.21.11's string (rt/CbWallSavedData).
 */
public class StructureFeatureIndexSavedData extends SavedData {

    public static final Codec<LongSet> field_56472 = Codec.LONG_STREAM.xmap(LongOpenHashSet::toSet, LongSet::longStream);

    /** 1.21.11 StructureFeatureIndexSavedData.CODEC: {All: [..], Remaining: [..]} */
    public static final Codec<StructureFeatureIndexSavedData> field_56471 = RecordCodecBuilder.create(i -> i.group(
        field_56472.fieldOf("All").forGetter(d -> d.field_15301),
        field_56472.fieldOf("Remaining").forGetter(d -> d.field_15302)
    ).apply(i, StructureFeatureIndexSavedData::new));

    public final LongSet field_15301;
    public final LongSet field_15302;

    /** 1.21.11 type(id): the saved data of that name, data-fixed as structure feature indices */
    public static SavedDataType<StructureFeatureIndexSavedData> method_67412(String id) {
        return CbWallSavedData.newType(id, StructureFeatureIndexSavedData::new, field_56471, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES);
    }

    public StructureFeatureIndexSavedData(LongSet field_15301, LongSet field_15302) {
        this.field_15301 = field_15301;
        this.field_15302 = field_15302;
    }

    public StructureFeatureIndexSavedData() {
        this(new LongOpenHashSet(), new LongOpenHashSet());
    }

    /** 1.21.11 addIndex(chunk) */
    public void method_14896(long chunk) {
        this.field_15301.add(chunk);
        this.field_15302.add(chunk);
        this.setDirty();
    }

    /** 1.21.11 hasStartIndex(chunk) */
    public boolean method_14897(long chunk) {
        return this.field_15301.contains(chunk);
    }

    /** 1.21.11 hasUnhandledIndex(chunk) */
    public boolean method_14894(long chunk) {
        return this.field_15302.contains(chunk);
    }

    /** 1.21.11 removeIndex(chunk) */
    public void method_14895(long chunk) {
        if (this.field_15302.remove(chunk)) {
            this.setDirty();
        }
    }

    /** 1.21.11 getAll() */
    public LongSet method_14898() {
        return this.field_15301;
    }
}
