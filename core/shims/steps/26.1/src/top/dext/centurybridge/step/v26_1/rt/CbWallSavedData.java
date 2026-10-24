package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HexFormat;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * 1.21.11's SavedDataType named its data by a plain string: DimensionDataStorage kept it in the file
 * {@code <id>.dat} of its data folder (getDataFile: dataFolder.resolve(id + ".dat")), any string allowed. 26.1
 * names it by an Identifier and keeps it in {@code <namespace>/<path>.dat}. A type a 1.21.11 mod builds from a
 * string gets a 26.1 Identifier that carries the string losslessly (namespace centurybridge, path
 * {@code legacy_saved_data/<hex of its UTF-8>}: two types with the same string are equal, as the 1.21.11 records
 * were), and 26.1's storage puts that type's data in the file 1.21.11 used (mixin/CbWallSavedDataStorageMixin),
 * read and written alike. id() of such a type gives the string back; of a 26.1 type, its Identifier as a string.
 */
public final class CbWallSavedData {

    private static final String NAMESPACE = "centurybridge";
    private static final String PREFIX = "legacy_saved_data/";

    private CbWallSavedData() {
    }

    /** 1.21.11 new SavedDataType(String id, Supplier constructor, Codec codec, DataFixTypes dataFixType) */
    public static <T extends SavedData> SavedDataType<T> newType(String id, Supplier<T> constructor, Codec<T> codec, DataFixTypes dataFixType) {
        return new SavedDataType<>(key(id), constructor, codec, dataFixType);
    }

    /** 1.21.11 SavedDataType.id(): the string the type was built from */
    public static String id(SavedDataType<?> type) {
        String legacy = legacyName(type.id());
        return legacy != null ? legacy : type.id().toString();
    }

    /** the 26.1 Identifier standing for a 1.21.11 string id */
    public static Identifier key(String id) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, PREFIX + HexFormat.of().formatHex(id.getBytes(StandardCharsets.UTF_8)));
    }

    /** the 1.21.11 string an Identifier from key(..) stands for, or null for any other Identifier */
    public static String legacyName(Identifier id) {
        if (!NAMESPACE.equals(id.getNamespace()) || !id.getPath().startsWith(PREFIX)) {
            return null;
        }
        try {
            return new String(HexFormat.of().parseHex(id.getPath().substring(PREFIX.length())), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException notHex) {
            // a centurybridge identifier under this prefix that key(..) did not make
            return null;
        }
    }

    /** the file 1.21.11's DimensionDataStorage kept a string-named type in: {@code <dataFolder>/<id>.dat} */
    public static Path legacyFile(Path dataFolder, String id) {
        return dataFolder.resolve(id + ".dat");
    }
}
