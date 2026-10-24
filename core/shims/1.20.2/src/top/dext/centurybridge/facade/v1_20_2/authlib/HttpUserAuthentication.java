package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.HttpAuthenticationService;

/** authlib 4.0.43's com.mojang.authlib.HttpUserAuthentication (gone in 5.0.47) */
public abstract class HttpUserAuthentication extends BaseUserAuthentication {
    protected HttpUserAuthentication(HttpAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override
    public HttpAuthenticationService getAuthenticationService() {
        return (HttpAuthenticationService) super.getAuthenticationService();
    }
}
