package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.authlib.GameProfile;

/** authlib 6.0.58's ProfileLookupCallback success shape, which a converted mod's implementation still declares */
public interface CbLibProfileLookupOld {
    void onProfileLookupSucceeded(GameProfile profile);
}
