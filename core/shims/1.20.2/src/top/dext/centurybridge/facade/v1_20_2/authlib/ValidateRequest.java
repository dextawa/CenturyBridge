package top.dext.centurybridge.facade.v1_20_2.authlib;

/** authlib 4.0.43's com.mojang.authlib.yggdrasil.request.ValidateRequest (gone in 5.0.47): the /validate body */
public class ValidateRequest {
    private String clientToken;
    private String accessToken;

    public ValidateRequest(String accessToken, String clientToken) {
        this.clientToken = clientToken;
        this.accessToken = accessToken;
    }
}
