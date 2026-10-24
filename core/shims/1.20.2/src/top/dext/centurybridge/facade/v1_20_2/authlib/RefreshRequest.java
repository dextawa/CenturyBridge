package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.GameProfile;

/** authlib 4.0.43's com.mojang.authlib.yggdrasil.request.RefreshRequest (gone in 5.0.47): the /refresh body */
public class RefreshRequest {
    private String clientToken;
    private String accessToken;
    private GameProfile selectedProfile;
    private boolean requestUser = true;

    public RefreshRequest(String accessToken, String clientToken) {
        this(accessToken, clientToken, null);
    }

    public RefreshRequest(String accessToken, String clientToken, GameProfile profile) {
        this.clientToken = clientToken;
        this.accessToken = accessToken;
        this.selectedProfile = profile;
    }
}
