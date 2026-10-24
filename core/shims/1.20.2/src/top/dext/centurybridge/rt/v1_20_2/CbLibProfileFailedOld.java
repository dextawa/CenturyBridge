package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.authlib.GameProfile;

/** authlib 4.0.43's ProfileLookupCallback failure shape, which a converted mod's callback still declares */
public interface CbLibProfileFailedOld {
    void onProfileLookupFailed(GameProfile profile, Exception exception);
}
