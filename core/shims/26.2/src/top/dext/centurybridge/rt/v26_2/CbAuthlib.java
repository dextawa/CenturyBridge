package top.dext.centurybridge.rt.v26_2;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;

/**
 * authlib 7 (1.21.9) made GameProfile a record: getName()/getId()/getProperties() became the
 * accessors name()/id()/properties(), the same values. The converter routes a 1.20.1 mod's
 * calls here.
 */
public final class CbAuthlib {

    public static String getName(GameProfile profile) {
        return profile.name();
    }

    public static UUID getId(GameProfile profile) {
        return profile.id();
    }

    public static PropertyMap getProperties(GameProfile profile) {
        return profile.properties();
    }

    private CbAuthlib() {
    }
}
