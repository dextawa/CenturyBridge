package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.GsonBuilder;

/**
 * Facade for 1.20.1 net/minecraft/class_5270 (LootGsons / Deserializers), deleted in 1.20.2: the
 * Gson builders the loot data was read with. Each builder registers what 1.20.1's did, in the same
 * order, through rt/v1_20_2/CbLootGson (adapters that read and write through the 1.20.2 codecs).
 */
public class class_5270 {

    public class_5270() {
    }

    /** createConditionSerializer() */
    public static GsonBuilder method_27860() {
        return top.dext.centurybridge.rt.v1_20_2.CbLootGson.conditionBuilder();
    }

    /** createFunctionSerializer() */
    public static GsonBuilder method_27861() {
        return top.dext.centurybridge.rt.v1_20_2.CbLootGson.functionBuilder();
    }

    /** createLootTableSerializer() */
    public static GsonBuilder method_27862() {
        return top.dext.centurybridge.rt.v1_20_2.CbLootGson.tableBuilder();
    }
}
