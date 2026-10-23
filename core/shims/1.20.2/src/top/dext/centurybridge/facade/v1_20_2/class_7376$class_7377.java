package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_7376$class_7377 (EntitySubPredicate.Type): the reader of one
 * kind of entity sub-predicate from its JSON object. 1.20.2 replaced it with a record holding the kind's
 * MapCodec (class_7376$class_8745); every 1.20.1 reference is renamed here. 1.20.2's own kinds are
 * wrapped one-to-one (CbAdvSubTypes.of), and a 1.20.1 mod's own kind gets a MapCodec of its own built on
 * this reader and the predicate's serializeCustomData (CbAdvSubTypes.typeOf).
 */
@FunctionalInterface
public interface class_7376$class_7377 {

    /** deserialize(json) */
    net.minecraft.class_7376 deserialize(com.google.gson.JsonObject json);
}
