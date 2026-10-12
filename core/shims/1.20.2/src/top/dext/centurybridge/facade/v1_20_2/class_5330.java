package top.dext.centurybridge.facade.v1_20_2;

import java.util.function.Function;

/**
 * Facade for 1.20.1 net/minecraft/class_5330 (GsonAdapterFactory / JsonSerializing), deleted in
 * 1.20.2: the builder of a Gson adapter that dispatches on a registry of JsonSerializableTypes
 * (the class_5336 facade). Ported as it was; see class_5330$class_5333 for the one change.
 */
public class class_5330 {

    public class_5330() {
    }

    /** builder(registry, rootFieldName, idFieldName, typeGetter) */
    public static <E, T extends class_5336<E>> class_5330$class_5331<E, T> method_29306(net.minecraft.class_2378<T> registry,
            String rootFieldName, String idFieldName, Function<E, T> typeGetter) {
        return new class_5330$class_5331<>(registry, rootFieldName, idFieldName, typeGetter);
    }
}
