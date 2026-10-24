package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.AuthenticationService;
import top.dext.centurybridge.facade.v1_20_2.AuthlibAgent;

/**
 * authlib 4.0.43's com.mojang.authlib.BaseAuthenticationService (gone in 5.0.47): an empty base of the service interface.
 * 4.0.43's interface also declared createUserAuthentication(Agent), which 5.0.47's dropped; it is declared here, so a
 * subclass implements it as it did and a call through this type reaches the subclass's
 */
public abstract class BaseAuthenticationService implements AuthenticationService {
    public BaseAuthenticationService() {
    }

    public abstract UserAuthentication createUserAuthentication(AuthlibAgent agent);
}
