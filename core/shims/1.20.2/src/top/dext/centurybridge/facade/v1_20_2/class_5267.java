package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_5267 (AdvancementEntityPredicateSerializer),
 * deleted in 1.20.2. Legacy code passes it to conditions.toJson(serializer), where the
 * bridged toJson ignores it, and asks it to write a condition array: that goes through
 * the same loot codecs the rest of the legacy loot JSON uses.
 */
public class class_5267 {

    /** INSTANCE */
    public static final class_5267 field_24420 = new class_5267();

    public class_5267() {
    }

    /** conditionsToJson(conditions) */
    public com.google.gson.JsonElement method_27849(net.minecraft.class_5341[] conditions) {
        return top.dext.centurybridge.rt.v1_20_2.CbLootJson.toJsonTree(conditions);
    }
}
