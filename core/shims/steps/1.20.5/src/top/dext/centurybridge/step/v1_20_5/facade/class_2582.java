package top.dext.centurybridge.step.v1_20_5.facade;

import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/**
 * 1.20.4 BannerPattern (net/minecraft/class_2582) in mod code. 1.20.5 kept the name for a final record
 * (asset id, translation key) in a datapack registry; 1.20.4's was a class with a hash name, registered
 * in BuiltInRegistries.BANNER_PATTERN, that mods built and extended. Mod code sees this class only:
 * BuiltInRegistries.BANNER_PATTERN is CbBlockBanner's registry of these, and every 1.20.4 API handing a
 * pattern out answers the one the 1.20.5 record was made from (by key). The game only ever holds
 * records: a pattern registered here becomes, in 1.20.5's registry, the record 1.20.4's key gave it
 * (CbBlockBanner).
 */
public class class_2582 {

    /** hashname: the pattern's code in 1.20.4's banner NBT */
    public final String field_11824;

    public class_2582(String hashname) {
        this.field_11824 = hashname;
    }

    /** location(key, isBanner): 1.20.4's texture of the pattern on a banner or a shield */
    public static net.minecraft.class_2960 method_22536(net.minecraft.class_5321<class_2582> key, boolean isBanner) {
        return key.method_29177().method_45138(isBanner ? "entity/banner/" : "entity/shield/");
    }

    /** getHashname() */
    public String method_10945() {
        return this.field_11824;
    }

    /** byHash(hash): the registered pattern of that code, null when none */
    public static net.minecraft.class_6880<class_2582> method_10946(String hash) {
        return CbBlockBanner.byHash(hash);
    }
}
