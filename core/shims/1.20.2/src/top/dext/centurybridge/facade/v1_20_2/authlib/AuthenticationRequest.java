package top.dext.centurybridge.facade.v1_20_2.authlib;

import top.dext.centurybridge.facade.v1_20_2.AuthlibAgent;

/** authlib 4.0.43's com.mojang.authlib.yggdrasil.request.AuthenticationRequest (gone in 5.0.47): the /authenticate body */
public class AuthenticationRequest {
    private AuthlibAgent agent;
    private String username;
    private String password;
    private String clientToken;
    private boolean requestUser = true;

    public AuthenticationRequest(AuthlibAgent agent, String username, String password, String clientToken) {
        this.agent = agent;
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
    }
}
