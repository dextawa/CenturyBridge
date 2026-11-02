package top.dext.centurybridge.step.v1_21_11.rt;

import net.minecraft.class_1297;
import net.minecraft.class_3218;

/** 1.21.10 VillagerTrades.ItemListing.getOffer(entity, random) calls on 1.21.11, which takes the ServerLevel first */
public final class CbTrades {
    private CbTrades() {
    }

    /** the level 1.21.10's listings read (entity.level()): a ServerLevel for every trader that makes offers, else null */
    public static class_3218 level(class_1297 entity) {
        return entity.method_73183() instanceof class_3218 level ? level : null;
    }
}
