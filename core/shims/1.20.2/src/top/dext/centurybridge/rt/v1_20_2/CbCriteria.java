package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 criterion contracts on top of 1.20.2's. Reached from the methods the
 * converter synthesizes into a legacy trigger (adapterHooks).
 */
public final class CbCriteria {

    /**
     * conditionsFromJson(json, Optional player, deserializer) of a legacy trigger: an
     * absent player predicate was LootContextPredicate.EMPTY in 1.20.1 -- the match-all
     * Statics.field_24388 stands for it. 1.20.2's AbstractCriterionConditions is
     * itself an AbstractCriterion.Conditions, so the old result is the new one.
     */
    public static net.minecraft.class_4558.class_8788 conditionsFromJson(CbLegacyAbstractCriterion trigger,
            com.google.gson.JsonObject json, java.util.Optional<net.minecraft.class_5258> player,
            net.minecraft.class_5257 deserializer) {
        return trigger.method_27854(json, player.orElse(Statics.field_24388), deserializer);
    }

    private CbCriteria() {
    }
}
