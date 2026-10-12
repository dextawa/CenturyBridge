package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_5336 (JsonSerializableType / SerializerType), deleted in
 * 1.20.2: a registry element that carries the JsonSerializer of its values. The 1.20.1 class is
 * ported as it was. A mod's own registry of these (built with GsonAdapterFactory, the class_5330
 * facade) keeps working; the vanilla loot types it used to be the parent of are 1.20.2 records
 * now -- CbLootSerializers.serializerOfType answers getJsonSerializer() for those.
 */
public class class_5336<T> {

    /** the serializer (private in 1.20.1, under this name: a mod's access widener may reach it) */
    public final class_5335<? extends T> field_25204;

    public class_5336(class_5335<? extends T> serializer) {
        this.field_25204 = serializer;
    }

    /** getJsonSerializer() */
    public class_5335<? extends T> method_29312() {
        return this.field_25204;
    }
}
