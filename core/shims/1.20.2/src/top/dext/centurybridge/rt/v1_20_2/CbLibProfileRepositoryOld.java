package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.authlib.ProfileLookupCallback;
import top.dext.centurybridge.facade.v1_20_2.AuthlibAgent;

/** authlib 4.0.43's GameProfileRepository shape (with an Agent), which a converted mod's repository still declares */
@FunctionalInterface
public interface CbLibProfileRepositoryOld {
    void findProfilesByNames(String[] names, AuthlibAgent agent, ProfileLookupCallback callback);
}
