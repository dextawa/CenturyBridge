package top.dext.centurybridge.compat.fapi.loot.v1;

import com.google.gson.Gson;
import java.io.Reader;

/**
 * Fabric API loot tables v1's LootJsonParser: loot JSON read with the loot-table Gson. 1.20.1 built it
 * from LootGsons.getTableGsonBuilder(); 1.20.2 has no loot Gson, and this layer's LootGsons facade
 * (facade/v1_20_2/class_5270) rebuilds 1.20.1's on the 1.20.2 loot codecs.
 */
public final class LootJsonParser {

    private static final Gson GSON = top.dext.centurybridge.facade.v1_20_2.class_5270.method_27862().create();

    private LootJsonParser() {
    }

    public static <T> T read(Reader json, Class<T> c) {
        return net.minecraft.class_3518.method_15276(GSON, json, c);
    }

    public static <T> T read(String json, Class<T> c) {
        return net.minecraft.class_3518.method_15284(GSON, json, c);
    }
}
