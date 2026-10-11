package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 AbstractCriterion.conditionsFromJson(json, player, deserializer)
 * shape: the player predicate became an Optional in 1.20.2.
 */
public interface CbLegacyAbstractCriterion {

    /** conditionsFromJson(json, player, deserializer) */
    net.minecraft.class_195 method_27854(com.google.gson.JsonObject json, net.minecraft.class_5258 player,
            net.minecraft.class_5257 deserializer);
}
